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
            lastSyncMessage.text = "Double Crap"
        }
        else{
            readMessages()
        }
    }

    private fun readMessages() {

        var cr = contentResolver.query(
            Uri.parse("content://sms/inbox"),
            null,
            null,
            null,
            null
        )

        if(cr.moveToFirst()){
            do {
                if(cr!= null && cr.moveToFirst()){
                    do {
                        lastSyncMessage.text = lastSyncMessage.text.toString() + " " +cr.getString(cr.getColumnIndex("body"))
                    }while(cr.moveToNext())
                }
            }while(cr.moveToNext())
        }
    }
}