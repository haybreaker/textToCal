package com.haybreaker.texttocal

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.READ_SMS), 1111)
        }
        else{
            //Now that permissions are set correctly go through and run following functions

            //Grab all relevant messages
            var relevantMessages: MutableList<String> = readMessages()
            //Put messages through algorithm to grab array of shifts
            var shiftData: MutableList<String> = calculateShifts(relevantMessages)
            //Import all calculated data into calender
            importToCal(shiftData)

        }

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

            lastSyncMessage.text = ""

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

        var calenderData: MutableList<String> = ArrayList()

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
                        calenderData.add(shiftMonth)
                        calenderData.add(shiftDate)
                        calenderData.add(startTime)
                        calenderData.add(finishTime)
                    }
                }

            }
        }
        return calenderData
    }

    private fun importToCal(shiftData: MutableList<String>){

    }
    //Close off all functions by here
}