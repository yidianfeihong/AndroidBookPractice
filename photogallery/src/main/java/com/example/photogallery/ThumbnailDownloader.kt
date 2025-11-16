package com.example.photogallery

import android.os.HandlerThread

private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD = 0

class ThumbnailDownloader<in T>: HandlerThread(TAG) {

    override fun start() {
        super.start()
    }

    override fun quit(): Boolean {
        return super.quit()
    }


    override fun run() {
        super.run()
    }


    override fun onLooperPrepared() {
        super.onLooperPrepared()
    }


    private fun doSth(){
        //StrictMode.enableDefaults()
    }
}