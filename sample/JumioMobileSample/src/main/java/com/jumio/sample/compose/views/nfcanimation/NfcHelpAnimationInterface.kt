/*
 * Copyright 2022 Jumio Corporation, all rights reserved.
 */
package com.jumio.sample.compose.views.nfcanimation

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import javax.security.auth.Destroyable

interface NfcHelpAnimationInterface : Destroyable {
	fun configure(rootView: View, isUsa: Boolean)
	fun start()
	fun resume()
	fun pause()
}

internal fun AppCompatImageView.reset() {
	alpha = 0f
	translationX = 0f
	translationY = 0f
	scaleX = 1f
	scaleY = 1f
	rotationX = 0f
	rotationY = 0f
}
