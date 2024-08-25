package com.holyqoranyasseraldossari.autoclickerforhamesterkombat.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.Settings
import android.view.animation.LinearInterpolator
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.holyqoranyasseraldossari.autoclickerforhamesterkombat.R
import com.holyqoranyasseraldossari.autoclickerforhamesterkombat.service.FloatingService

class MainActivity : AppCompatActivity() {
    private lateinit var startServiceButton: Button
    private lateinit var stopServiceButton: Button
    private lateinit var handlerThread: HandlerThread
    private lateinit var backgroundHandler: Handler
    private var status = 0

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the HandlerThread and Handler
        handlerThread = HandlerThread("ClickThread")
        handlerThread.start()
        backgroundHandler = Handler(handlerThread.looper)

        startServiceButton = findViewById(R.id.startServiceButton)
//        stopServiceButton = findViewById(R.id.stopServiceButton)


        animateButton()
        startServiceButton.setOnClickListener {
            if (status == 0) {
                status = 1
                startServiceButton.text = "STOP"
                if (!Settings.canDrawOverlays(this)) {
                    // Request the permission to draw over other apps
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    startActivity(intent)
                } else {
                    // Start the FloatingService if permission is already granted
                    startService(Intent(this, FloatingService::class.java))
                }
            }else{
                startServiceButton.text = "START"
                // Stop the FloatingService
                stopService(Intent(this, FloatingService::class.java))
                status = 0
            }
        }




    }


    private fun animateButton() {
        startServiceButton.animate()
            .translationYBy(-50f) // Move up by 50 pixels
            .setDuration(1000)
            .setInterpolator(LinearInterpolator())
            .withEndAction {
                startServiceButton.animate()
                    .translationYBy(50f) // Move down by 50 pixels
                    .setDuration(1000)
                    .setInterpolator(LinearInterpolator())
                    .withEndAction {
                        animateButton() // Recursively call the function to loop the animation
                    }
                    .start()
            }
            .start()
    }

}
