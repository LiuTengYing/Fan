package com.example.fan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AmpReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            Log.e("AmpReceiver", "Context or Intent is null")
            return
        }

        Log.d("AmpReceiver", "Received broadcast: ${intent.action}")
        when (intent.action) {
            "com.example.fan.C_AMP" -> {
                val ampState = intent.getIntExtra("value", 0)
                Log.d("AmpReceiver", "C_AMP broadcast received with value: $ampState")

                if (context is MainActivity) {
                    context.isAmpOn = ampState == 1
                    context.updateIndicatorLight()
                    context.updateFanLight()

                    if (!context.isAmpOn) {
                        Log.d("AmpReceiver", "AMP is off, stopping fan animation and disabling controls")
                        context.stopFanAnimation()
                        context.updateSwitches(
                            context.findViewById(R.id.left_switch),
                            context.findViewById(R.id.right_switch),
                        )
                    } else {
                        Log.d("AmpReceiver", "AMP is on")
                    }
                }
            }
            "com.example.fan.U_AMP" -> {
                Log.d("AmpReceiver", "U_AMP broadcast received")
            }
            else -> {
                Log.d("AmpReceiver", "Unknown broadcast received")
            }
        }
    }
}
