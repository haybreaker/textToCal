package com.haybreaker.texttocal

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import java.io.InputStream
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarSetup()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.READ_SMS), 1111)
        }
        else{
            //Permissions set so now we wait for import to be initiated by user
            importButton.setOnClickListener(){
                //Now that permissions are set correctly go through and run following functions
                //Grab all relevant messages
                var relevantMessages: MutableList<String> = readMessages()
                //Put messages through algorithm to grab array of shifts
                var shiftData: MutableList<String> = calculateShifts(relevantMessages)
                //Import all calculated data into calender
                importToCal(shiftData)
            }
        }

        //Outside of permissions and other functions setup debugging mode and processes.
        textToCalImage.setOnLongClickListener(){
            Toast.makeText(this,"Debugging functions coming shortly",Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener  true

        }
    }

    private fun calendarSetup() {
        //Initialise initial variables to connect with google API
        /*var applicationName = "textToCal"
        var jsonFactory = JacksonFactory.getDefaultInstance()
        var tokensDirectoryPath = "tokens"

        //Further variables delcared however if changes made need to delete tokens
        var SCOPES: List<String> = Collections.singletonList(CalendarScopes.CALENDAR_READONLY)
        var credentialsFilePath = "/credentials.json"

        //Finalize other parts, more description to come
        var Credential = getCredentials(var NetHttpTransport HTTP_TRANSPORT){
            // Load client secrets.
            InputStream in = CalendarQuickstart .class.getResourceAsStream(CREDENTIALS_FILE_PATH)
        }

        var clientSecrets: GoogleClientSecrets =
            GoogleClientSecrets.load(jsonFactory, InputStreamReader(in))*/
    }

    private fun readMessages(): MutableList<String> {

        var messagesList: MutableList<String> = ArrayList()

        //Create contentResolver to parse through all sms messages

        var cr = contentResolver.query(
            Uri.parse("content://sms/inbox"),null, null, null, null)


        //If exists any textMessages then go through and find ones from particular number then read body of those messages.
        if (cr.moveToFirst()) {
            do {
                if (cr != null && cr.moveToFirst()) {
                    do {
                        val message = cr.getString(cr.getColumnIndex("body"))

                        if (cr.getString(cr.getColumnIndex("address")) == "+61417262921") {
                            messagesList.add(message)
                        }

                        //LEGACY - Base understanding of how to read texts from cursor
                        //lastSyncMessage.text = lastSyncMessage.text.toString() + " " +cr.getString(cr.getColumnIndex("body"))

                    } while (cr.moveToNext())
                }
            } while (cr.moveToNext())

            //Time to write a comment :)
            //Clear the message and then display all messages for debugging

            //LEGACY CODE ===== No longer using lastSyncMessage as debugging code
            //lastSyncMessage.text = ""

            for (item in messagesList) {
                //Legacy testing code
                //lastSyncMessage.text = lastSyncMessage.text.toString() + "\n " + item.toString()
            }
        }
            return messagesList
    }

    private fun calculateShifts(relevantMessages: MutableList<String>): MutableList<String> {
        var months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        var shiftMonth = ""
        var shiftDate = ""
        var startTime = ""
        var finishTime = ""
        var breakLength = ""
        //Temp Variables for calculation and manipulations
        var fixedWord = ""

        //Create the muteable list for holding raw data

        var calendarData: MutableList<String> = ArrayList()

        for(texts in relevantMessages){
            val lines = texts.split('\n')
            for(line in lines){
                //Declare needed variables
                var words = line.split(' ')
                for(month in months){
                    //Confirm relevant line entry as has a month in it.
                    if(line.contains(month)) {
                        shiftMonth = month
                        for(word in words){
                            //Space for completing per words analysis
                            //Calculate
                            if(word.contains("st") || word.contains("nd") || word.contains("rd") || word.contains("th")){
                                shiftDate = word.substring(0, word.length -2)
                            }
                            if(word.contains(":")){
                                fixedWord = word.substring(0, word.length -1)
                                var times = fixedWord.split("-")
                                startTime = times[0]
                                finishTime = times[1]
                            }
                        }
                        //End of if statement so anything for text with date in it
                        calendarData.add(shiftMonth)
                        calendarData.add(shiftDate)
                        calendarData.add(startTime)
                        calendarData.add(finishTime)
                    }
                }

            }
        }
        return calendarData
    }

    private fun importToCal(shiftData: MutableList<String>){

        /*var cr: ContentResolver = this.contentResolver
        var cv: ContentValues = ContentValues()

        cv.put(CalendarContract.Events.TITLE,"Work - Telstra Knox")
        cv.put(CalendarContract.Events.DESCRIPTION, "Working at Telstra Knox Today")
        cv.put(CalendarContract.Events.EVENT_LOCATION, "Telstra Knox City")
        cv.put(CalendarContract.Events.DTSTART, Calendar.getInstance().timeInMillis)
        cv.put(CalendarContract.Events.DTEND, Calendar.getInstance().timeInMillis + 60*60*1000)
        cv.put(CalendarContract.Events.CALENDAR_ID, 1)
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().timeZone.id)

        var uri: Uri = cr.insert(CalendarContract.Events.CONTENT_URI, cv)

        Toast.makeText(this, "Event is added", Toast.LENGTH_SHORT).show()*/
    }

    //Close off all functions by here
}