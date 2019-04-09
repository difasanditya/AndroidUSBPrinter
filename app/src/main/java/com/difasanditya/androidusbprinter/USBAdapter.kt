package com.difasanditya.androidusbprinter

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.*

class USBAdapter {
    private val ACTION_USB_PERMISSION = "com.difasanditya.androidusbprinter.USB_PERMISSION"
    private var usbManager: UsbManager? = null
    private var printerInterface: UsbInterface? = null
    private var writeEndpoint: UsbEndpoint? = null
    private var readEndpoint: UsbEndpoint? = null
    private var printer: UsbDevice? = null
    private val forceClaim = true

    /*
    Printer Connection
     */

    fun connect(context: Context) {
        val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList: HashMap<String, UsbDevice> = manager.deviceList
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
                        if(!manager.hasPermission(device)) {
                            manager.requestPermission(device, PendingIntent.getBroadcast(context, 0, Intent(ACTION_USB_PERMISSION), 0))
                        }
                        printerInterface = usbInt
                        printer = device
                        usbManager = manager
                    }
                }
            }
        }
    }

    /*
    TODO: Implement the function
     */
    fun disconnect() {}

    /*
    Printing
     */

    /*
    Eject paper/document after end of all print
     */
    private fun createPrintableData(text: String): ByteArray {
        return text.toByteArray().plus(byteArrayOf(0xFF.toByte())).plus(byteArrayOf(0x0C))
    }

    /*
    Print text from ByteArray
     */
    fun print(arr: ByteArray) {
        if (printer == null) return
        usbManager!!.openDevice(printer)?.apply {
            claimInterface(printerInterface, forceClaim)
            bulkTransfer(writeEndpoint, arr, arr.size, 60000)
            releaseInterface(printerInterface)
        }
    }

    /*
    Print text from a String
     */
    fun print(text: String) {
        print(createPrintableData(text))
    }

    /*
    Standard Device Requests
     */

    /*
    TODO: Implement the function
     */
    fun clearFeature() {}

    /*
    TODO: Implement the function
     */
    fun getConfiguration() {}

    /*
    TODO: Implement the function
     */
    fun getDescriptor() {}

    /*
    TODO: Implement the function
     */
    fun getInterface() {}

    /*
    TODO: Implement the function
     */
    fun getStatus() {}

    /*
    TODO: Implement the function
     */
    fun setAddress() {}

    /*
    TODO: Implement the function
     */
    fun setConfiguration() {}

    /*
    TODO: Implement the function
     */
    fun setDescriptor() {}

    /*
    TODO: Implement the function
     */
    fun setInterface() {}

    /*
    TODO: Implement the function
     */
    fun setFeature() {}

    /*
    Class Specific Requests
     */

    /*
    GET_DEVICE_ID

     */
    fun getDeviceID(): String {
        if (printer == null) return ""
        val buffer = ByteArray(255)
        var len = 0
        usbManager!!.openDevice(printer)?.apply {
            claimInterface(printerInterface, forceClaim)
            len = controlTransfer(0xA1, 0, 0, 0, buffer, 0xFF, 0)
            releaseInterface(printerInterface)
        }
        return String(buffer, 2, len - 2)
    }

    /*
    GET_PORT_STATUS
    Possible return value:
    -1 - Printer is not connected
     0 - Error, Not Selected, Paper Not Empty
     8 - No Error, Not Selected, Paper Not Empty
    16 - Error, Selected, Paper Not Empty
    24 - No Error, Selected, Paper Not Empty
    32 - Error, Not Selected, Paper Empty
    40 - No Error, Not Selected, Paper Empty
    48 - Error, Selected, Paper Empty
    56 - No Error, Selected, Paper Empty
    References:
    https://www.usb.org/sites/default/files/usbprint11a021811.pdf (4.2.2 GET_PORT_STATUS, Page 7)
     */
    fun getPortStatus(): Int {
        if (printer == null) return -1
        val buffer = ByteArray(1)
        usbManager!!.openDevice(printer)?.apply {
            claimInterface(printerInterface, forceClaim)
            controlTransfer(0xA1, 1, 0, 0, buffer, 1, 0)
            releaseInterface(printerInterface)
        }
        return buffer[0].toInt()
    }

    /*
    SOFT_RESET
    References:
    https://www.usb.org/sites/default/files/usbprint11a021811.pdf (Class-Specific Requests, Page 6)
    http://sdphca.ucsd.edu/lab_equip_manuals/usb_20.pdf (Chapter 9: USB Device Framework, Page 239)
     */
    fun softReset() {
        if (printer == null) return
        usbManager!!.openDevice(printer)?.apply {
            claimInterface(printerInterface, forceClaim)
            controlTransfer(0x21, 2, 0, 0, null, 0, 0)
            releaseInterface(printerInterface)
        }
    }
}