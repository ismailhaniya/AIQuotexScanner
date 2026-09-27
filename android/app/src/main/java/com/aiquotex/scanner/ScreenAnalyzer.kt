package com.aiquotex.scanner

import kotlin.random.Random

data class ScanResult(
    val signal: String,
    val confidence: Int,
    val trend: String,
    val rsi: Int,
    val timer: Int
)

object ScreenAnalyzer {

    private var countdown = 180

    fun analyze(): ScanResult {

        val confidence = Random.nextInt(75, 96)
        val rsi = Random.nextInt(25, 76)

        val signal = when {
            rsi >= 60 -> "UP"
            rsi <= 40 -> "DOWN"
            else -> "WAIT"
        }

        val trend = when (signal) {
            "UP" -> "BULLISH"
            "DOWN" -> "BEARISH"
            else -> "SIDEWAYS"
        }

        if (countdown > 0) countdown--
        else countdown = 180

        return ScanResult(
            signal = signal,
            confidence = confidence,
            trend = trend,
            rsi = rsi,
            timer = countdown
        )
    }
}
