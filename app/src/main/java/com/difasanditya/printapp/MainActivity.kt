package com.difasanditya.printapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val printer = USBAdapter()
        val msg = "This is a message to print"

        val buttonConnect = findViewById<Button>(R.id.connect)
        buttonConnect.setOnClickListener {
            printer.createConn(this)
        }

        val buttonPrint = findViewById<Button>(R.id.print)
        buttonPrint.setOnClickListener {
            printer.printMessage(msg)
        }

        val buttonCancel = findViewById<Button>(R.id.cancel)
        buttonCancel.setOnClickListener {
            printer.cancelPrint()
        }
    }
}
