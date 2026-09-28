package com.aiquotex.scanner

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: LinearLayout

    private lateinit var signalText: TextView
    private lateinit var infoText: TextView

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val signal = intent?.getStringExtra("signal") ?: "WAIT"
            val confidence = intent?.getIntExtra("confidence", 0) ?: 0
            val timer = intent?.getIntExtra("timer", 180) ?: 180

            signalText.text = signal
            infoText.text = "Confidence: ${confidence}% | ${timer}s"
        }
    }

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        overlayView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20,20,20,20)
            setBackgroundColor(0xCC111827.toInt())
        }

        signalText = TextView(this).apply {
            text = "WAIT"
            textSize = 28f
            setTextColor(0xFFFFD54F.toInt())
        }

        infoText = TextView(this).apply {
            text = "Confidence: 0% | 180s"
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt())
        }

        overlayView.addView(signalText)
        overlayView.addView(infoText)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 30
        params.y = 150

        windowManager.addView(overlayView, params)

        registerReceiver(receiver, IntentFilter("AI_SIGNAL_UPDATE"))
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        windowManager.removeView(overlayView)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
