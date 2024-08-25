package com.holyqoranyasseraldossari.autoclickerforhamesterkombat.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Path
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class AutoClickAccessibilityService : AccessibilityService() {
    private val handler = Handler()
    private var isTapping = false
    private val clickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val x = intent.getStringExtra("x_coordinate")?.toFloat() ?: -1f
            val y = intent.getStringExtra("y_coordinate")?.toFloat() ?: -1f
//            Toast.makeText(baseContext, "receiving x:$x , y:$y", Toast.LENGTH_SHORT).show()
            if (x != -1f && y != -1f) {
                performClick(x, y)
            } else {
                Log.d("ClickAction", "Invalid coordinates received.")
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()
        Toast.makeText(baseContext, "AutoClickAccessibilityService Created", Toast.LENGTH_SHORT).show()
        val filter = IntentFilter("com.holyqoranyasseraldossari.CLICK_ACTION")
        registerReceiver(clickReceiver, filter)
    }

    override fun onInterrupt() {
        // Handle service interruptions
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Handle accessibility events if needed
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Toast.makeText(baseContext, "AutoClickAccessibilityService connected", Toast.LENGTH_SHORT).show()
    }

    private fun performTap(x: Float, y: Float) {
        val path = Path()
        path.moveTo(x, y)

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 50))
        dispatchGesture(gestureBuilder.build(), null, null)
    }


    fun performClick(x: Float, y: Float) {
        isTapping = true
        handler.post(object : Runnable {
            override fun run() {
                if (isTapping) {
                    performTap(x, y)
                    handler.postDelayed(this, 100) // Delay between taps (adjust as needed)
                }
            }
        })
    }



//    @SuppressLint("ObsoleteSdkInt")
//    private fun performClick(x: Float, y: Float) {
//        // Ensure coordinates are within screen bounds
//        val screenWidth = resources.displayMetrics.widthPixels
//        val screenHeight = resources.displayMetrics.heightPixels
//
//        val clampedX = x.coerceIn(0f, screenWidth.toFloat())
//        val clampedY = y.coerceIn(0f, screenHeight.toFloat())
//
//        val path = Path()
//        path.moveTo(clampedX, clampedY)
//
//        val gestureBuilder = GestureDescription.Builder()
//        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 50))
//        dispatchGesture(gestureBuilder.build(), null, null)
//    }
//
//    fun startTapping(x: Float, y: Float) {
//        isTapping = true
//        handler.post(object : Runnable {
//            override fun run() {
//                if (isTapping) {
//                    performClick(x, y)
//                    handler.postDelayed(this, 50) // Adjust delay as needed
//                }
//            }
//        })
//    }

    fun stopTapping() {
        isTapping = false
        handler.removeCallbacksAndMessages(null)
    }
}
