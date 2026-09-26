package com.aiquotex.scanner

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder

class ScreenCaptureService : Service() {

    private var mediaProjection: MediaProjection? = null
    private var imageReader: ImageReader? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        createNotification()

        val resultCode = intent?.getIntExtra("resultCode", -1) ?: -1
        val data = intent?.getParcelableExtra<Intent>("data")

        if (resultCode != -1 && data != null) {
            val manager = getSystemService(MEDIA_PROJECTION_SERVICE)
                    as MediaProjectionManager

            mediaProjection = manager.getMediaProjection(resultCode, data)

            startScanner()
        }

        return START_STICKY
    }

    private fun startScanner() {

        // V16 Base Structure
        // এখানে পরের ধাপে VirtualDisplay তৈরি হবে।

        val bitmap = Bitmap.createBitmap(
            1080,
            1920,
            Bitmap.Config.ARGB_8888
        )

        val result = ScreenAnalyzer.analyze(bitmap)

        println(
            "Signal=${result.signal}  Confidence=${result.confidence}"
        )
    }

    private fun createNotification() {

        val channelId = "scanner_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                channelId,
                "AIQuotex Scanner",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager =
                getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("AIQuotexScanner")
            .setContentText("Screen Scanner Running")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        mediaProjection?.stop()
        imageReader?.close()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}