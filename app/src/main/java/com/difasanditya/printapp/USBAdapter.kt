package com.difasanditya.printapp

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.*

class USBAdapter {
    private val ACTION_USB_PERMISSION = "com.difasanditya.printapp.USB_PERMISSION"
    private var usbManager: UsbManager? = null
    private var printerInterface: UsbInterface? = null
    private var writeEndpoint: UsbEndpoint? = null
    private var readEndpoint: UsbEndpoint? = null
    private var printer: UsbDevice? = null
    private val forceClaim = true

    fun createConn(context: Context) {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList
        deviceList.values.forEach{ device ->
            if(device.deviceClass == UsbConstants.USB_CLASS_PER_INTERFACE) {
                deviceLoop@for(i in 0 until device.interfaceCount) {
                    val usbInt:UsbInterface = device.getInterface(i)
                    if(usbInt.name == "Printer Interface" || usbInt.interfaceProtocol == UsbConstants.USB_CLASS_COMM){
                        for(j in 0 until usbInt.endpointCount) {
                            if(usbInt.getEndpoint(j).direction == UsbConstants.USB_DIR_OUT) {
                                writeEndpoint = usbInt.getEndpoint(j)
                            }
                            else {
                                readEndpoint = usbInt.getEndpoint(j)
                            }
                        }
                        if(!usbManager.hasPermission(device)) {
                            usbManager.requestPermission(device, PendingIntent.getBroadcast(context, 0, Intent(ACTION_USB_PERMISSION), 0))
                        }
                        printerInterface = usbInt
                        printer = device
                        this.usbManager = usbManager
                    }
                }
            }
        }
    }

    fun printMessage(msg: String) {
        if (printer == null) return
        usbManager!!.openDevice(printer)?.apply {
            claimInterface(printerInterface, forceClaim)
            val b = bulkTransfer(writeEndpoint, msg.toByteArray(), msg.toByteArray().size, 60000)
            releaseInterface(printerInterface)
        }
    }

    /*
    References:
    https://www.usb.org/sites/default/files/usbprint11a021811.pdf (Class-Specific Requests, Page 6)
    http://sdphca.ucsd.edu/lab_equip_manuals/usb_20.pdf (Chapter 9: USB Device Framework, Page 239)
     */
    fun cancelPrint() {
        if (printer == null) return
        usbManager!!.openDevice(printer)?.apply {
            claimInterface(printerInterface, forceClaim)
            val c = controlTransfer(0x21, 2, 0, 0, null, 0, 0)
            releaseInterface(printerInterface)
        }
    }
}