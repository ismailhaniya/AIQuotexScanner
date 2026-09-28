package com.aiquotex.scanner

import android.graphics.Bitmap
import android.graphics.Color

data class ScanResult(
    val chartDetected: Boolean,
    val confidence: Int,
    val trend: String
)

object ScreenAnalyzer {

    fun analyzeFrame(bitmap: Bitmap): ScanResult {

        val width = bitmap.width
        val height = bitmap.height

        var greenPixels = 0
        var redPixels = 0

        // স্ক্রিনের মাঝের অংশ স্ক্যান
        for (x in width / 4 until (width * 3) / 4 step 8) {
            for (y in height / 4 until (height * 3) / 4 step 8) {

                val pixel = bitmap.getPixel(x, y)

                val r = Color.red(pixel)
                val g = Color.green(pixel)

                if (g > r + 30) greenPixels++
                if (r > g + 30) redPixels++
            }
        }

        val total = greenPixels + redPixels

        if (total < 20) {
            return ScanResult(
                chartDetected = false,
                confidence = 0,
                trend = "NO_CHART"
            )
        }

        val trend = if (greenPixels > redPixels) "BULLISH" else "BEARISH"
        val confidence =
            (maxOf(greenPixels, redPixels) * 100 / total).coerceAtMost(95)

        return ScanResult(
            chartDetected = true,
            confidence = confidence,
            trend = trend
        )
    }
}
