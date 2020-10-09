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

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            TODO("not implemented") //Input request permission function here
        }
        else{

            //Now that permissions are set correctly go through and run following functions
            //Grab all relevant messages
            var relevantMessages: MutableList<String> = readMessages()
            //Put messages through algorithm to grab array of shifts
            //shiftsToImport: MutableList<String> = textToShifts(relevantMessages)
            //Now import shifts to calender
            importToCalender(relevantMessages)

        }

    }

    private fun readMessages(): MutableList<String> {

        var messagesList: MutableList<String> = ArrayList()

        //Create contentResolver to parse through all sms messages

        var cr = contentResolver.query(
            Uri.parse("content://sms/inbox"),
            null,
            null,
            null,
            null
        )


        //If exists any textMessages then go through and find ones from particular number then read body of those messages.
        if (cr.moveToFirst()) {
            do {
                if (cr != null && cr.moveToFirst()) {
                    do {
                        val message = cr.getString(cr.getColumnIndex("body"))

                        if (cr.getString(cr.getColumnIndex("address")) == "0417262921") {
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
                lastSyncMessage.text = lastSyncMessage.text.toString() + "\n " + item.toString()
            }
        }
            return messagesList
    }

    private fun importToCalender(relevantMessages: MutableList<String>){

    }

    //Close off all functions by here
}