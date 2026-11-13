package com.runit.ocrscanner.ui.utils

import android.view.OrientationEventListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberPortraitRotation(): Float {
    val context = LocalContext.current
    var rotation by remember {
        mutableFloatStateOf(0f)
    }

    DisposableEffect(context) {
        val listener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(degrees: Int) {
                if (degrees == ORIENTATION_UNKNOWN) return

                when (degrees) {
                    in 0..45, in 315..359 -> rotation = 0f
                    in 135..225 -> rotation = 180f
                }
            }
        }

        if (listener.canDetectOrientation()) {
            listener.enable()
        }

        onDispose {
            listener.disable()
        }
        onDispose { }
    }

    return rotation
}