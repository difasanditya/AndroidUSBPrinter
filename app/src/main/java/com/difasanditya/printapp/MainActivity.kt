package com.difasanditya.printapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val printer = USBAdapter()
        val msg = "This is a message to print"

        val buttonPrint = findViewById<Button>(R.id.Print)
        buttonPrint.setOnClickListener {
            printer.print(msg)
        }

        /*
        Printer Connection
         */

        findViewById<Button>(R.id.connect).setOnClickListener {
            printer.connect(this)
        }

        findViewById<Button>(R.id.disconnect).setOnClickListener {
            printer.disconnect()
        }

        /*
        Standard Device Requests
         */

        findViewById<Button>(R.id.clearFeature).setOnClickListener {
            printer.clearFeature()
        }

        findViewById<Button>(R.id.getConfiguration).setOnClickListener {
            printer.getConfiguration()
        }

        findViewById<Button>(R.id.getDescriptor).setOnClickListener {
            printer.getDescriptor()
        }

        findViewById<Button>(R.id.getInterface).setOnClickListener {
            printer.getInterface()
        }

        findViewById<Button>(R.id.getStatus).setOnClickListener {
            printer.getStatus()
        }

        findViewById<Button>(R.id.setAddress).setOnClickListener {
            printer.setAddress()
        }

        findViewById<Button>(R.id.setConfiguration).setOnClickListener {
            printer.setConfiguration()
        }

        findViewById<Button>(R.id.setDescriptor).setOnClickListener {
            printer.setDescriptor()
        }

        findViewById<Button>(R.id.setInterface).setOnClickListener {
            printer.setInterface()
        }

        findViewById<Button>(R.id.setFeature).setOnClickListener {
            printer.setFeature()
        }

        /*
        Class Specific Requests
         */

        val textDeviceID = findViewById<TextView>(R.id.textDeviceID)

        findViewById<Button>(R.id.getDeviceID).setOnClickListener {
            printer.getDeviceID(textDeviceID)
        }

        val textPortStatus = findViewById<TextView>(R.id.textPortStatus)
        findViewById<Button>(R.id.getPortStatus).setOnClickListener {
            printer.getPortStatus(textPortStatus)
        }

        findViewById<Button>(R.id.softReset).setOnClickListener {
            printer.softReset()
        }
    }
}
