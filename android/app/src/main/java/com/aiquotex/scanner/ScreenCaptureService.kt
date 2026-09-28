package com.aiquotex.scanner

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper

class ScreenCaptureService : Service() {

    private val handler = Handler(Looper.getMainLooper())

    private val scannerLoop = object : Runnable {
        override fun run() {
            val result = ScreenAnalyzer.analyze()

            // পরের ধাপে OverlayService এই ডাটা ব্যবহার করবে
            val intent = Intent("AI_SIGNAL_UPDATE")
            intent.putExtra("signal", result.signal)
            intent.putExtra("confidence", result.confidence)
            intent.putExtra("trend", result.trend)
            intent.putExtra("rsi", result.rsi)
            intent.putExtra("timer", result.timer)
            sendBroadcast(intent)

            handler.postDelayed(this, 1000)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotification()
        handler.post(scannerLoop)
        return START_STICKY
    }

    private fun createNotification() {
        val channelId = "scanner_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "AIQuotexScanner",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("AIQuotexScanner")
            .setContentText("Live Scanner Running")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        handler.removeCallbacks(scannerLoop)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
