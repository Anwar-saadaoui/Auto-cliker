package com.holyqoranyasseraldossari.autoclickerforhamesterkombat.service

import android.annotation.SuppressLint
import android.app.Instrumentation
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.holyqoranyasseraldossari.autoclickerforhamesterkombat.R

class FloatingService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var pointerView: View
    private var handler: Handler? = null
    private var backgroundHandler: Handler? = null
    private lateinit var handlerThread: HandlerThread
    private var isClicking = false
    private var clickX: Float = 0f
    private var clickY: Float = 0f
    private val TAG = "FloatingService"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()

        // Initialize handlerThread
        handlerThread = HandlerThread("ClickThread").apply { start() }
        backgroundHandler = Handler(handlerThread.looper)

        // Initialize handlers after creating handlerThread
        handler = Handler(Looper.getMainLooper())

        // Inflate floating and pointer views
        floatingView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)
        pointerView = LayoutInflater.from(this).inflate(R.layout.pointer_layout, null)

        // Floating view layout parameters
        val paramsFloatingView = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 100
        }

        // Pointer view layout parameters
        val paramsPointerView = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 200

            // israa saadaoui

//            Toast.makeText(baseContext, "broda cast sent", Toast.LENGTH_LONG).show()
        }

        // Setup window manager and add views
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingView, paramsFloatingView)
        windowManager.addView(pointerView, paramsPointerView)

        // Button listeners
        val startButton = floatingView.findViewById<ImageView>(R.id.startButton)
        val settingsButton = floatingView.findViewById<ImageView>(R.id.settingsButton)
        val moveButton = floatingView.findViewById<ImageView>(R.id.moveButton)

        // Start or stop clicking
        startButton.setOnClickListener {
            if (isClicking) {
                stopAutoClick()
            } else {
                startAutoClick()
            }
        }

        // Move floating view
        moveButton.setOnTouchListener(object : View.OnTouchListener {
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0.0f
            private var initialTouchY: Float = 0.0f

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = paramsFloatingView.x
                        initialY = paramsFloatingView.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        paramsFloatingView.x = initialX + (event.rawX - initialTouchX).toInt()
                        paramsFloatingView.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(floatingView, paramsFloatingView)
                        return true
                    }
                }
                return false
            }
        })

        // Move pointer view
        pointerView.setOnTouchListener(object : View.OnTouchListener {
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0.0f
            private var initialTouchY: Float = 0.0f

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = paramsPointerView.x
                        initialY = paramsPointerView.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY

                        val intent = Intent("com.holyqoranyasseraldossari.CLICK_ACTION").apply {
                            putExtra("x_coordinate", clickX)
                            putExtra("y_coordinate", clickY)
                            setPackage("com.holyqoranyasseraldossari.autoclickerforhamesterkombat")
                        }
                        sendBroadcast(intent)
//                        Toast.makeText(baseContext, "broadcast sent", Toast.LENGTH_LONG).show()
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        paramsPointerView.x = initialX + (event.rawX - initialTouchX).toInt()
                        paramsPointerView.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(pointerView, paramsPointerView)

                        clickX = event.rawX
                        clickY = event.rawY

                        Log.d(TAG, "Pointer moved to: X=$clickX, Y=$clickY")

                        val intent = Intent("com.holyqoranyasseraldossari.CLICK_ACTION").apply {
                            putExtra("x_coordinate", clickX.toString())
                            putExtra("y_coordinate", clickY.toString())
                            setPackage("com.holyqoranyasseraldossari.autoclickerforhamesterkombat")
                        }
                        sendBroadcast(intent)
//                        Toast.makeText(baseContext, "broadcast sent", Toast.LENGTH_LONG).show()
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun stopAutoClick() {
        isClicking = false
        handler?.removeCallbacksAndMessages(null)
    }

    private fun startAutoClick() {
        isClicking = true
        handler?.post(object : Runnable {
            override fun run() {
                if (isClicking) {
                    Log.d(TAG, "Performing click at: X=$clickX, Y=$clickY")
                    performClick(clickX, clickY)
                    handler?.postDelayed(this, 50) // Adjust the delay as needed
                }
            }
        })
    }


    private fun performClick(x: Float, y: Float) {
        Toast.makeText(baseContext, "used", Toast.LENGTH_SHORT).show()

        backgroundHandler?.post {
            try {
                val instrumentation = Instrumentation()
                val downTime = SystemClock.uptimeMillis()
                val eventTime = SystemClock.uptimeMillis() + 100

                val motionEventDown = MotionEvent.obtain(
                    downTime,
                    eventTime,
                    MotionEvent.ACTION_DOWN,
                    x,
                    y,
                    0
                )
                val motionEventUp = MotionEvent.obtain(
                    downTime,
                    eventTime + 100,
                    MotionEvent.ACTION_UP,
                    x,
                    y,
                    0
                )

                instrumentation.sendPointerSync(motionEventDown)
                instrumentation.sendPointerSync(motionEventUp)

                motionEventDown.recycle()
                motionEventUp.recycle()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to perform click: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerThread.quitSafely()
        if (::floatingView.isInitialized) windowManager.removeView(floatingView)
        if (::pointerView.isInitialized) windowManager.removeView(pointerView)
        stopAutoClick()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}