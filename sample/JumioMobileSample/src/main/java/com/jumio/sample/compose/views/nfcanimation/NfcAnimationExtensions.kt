/*
 * Copyright 2025 Jumio Corporation, all rights reserved.
 */
package com.jumio.sample.compose.views.nfcanimation

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.content.res.Resources
import android.util.Property
import android.util.TypedValue
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.jumio.defaultui.R

internal fun View.reset(alpha: Float = 0f) {
	this.alpha = alpha
	translationX = 0f
	translationY = 0f
	scaleX = 1f
	scaleY = 1f
	rotationX = 0f
	rotationY = 0f
}

internal fun View.springAnimation(position: Float) {
	SpringAnimation(this, SpringAnimation.TRANSLATION_X, position).apply {
		spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
		spring.stiffness = SpringForce.STIFFNESS_VERY_LOW
		start()
	}
}

internal fun Resources.Theme.resolveNfcStyle(): Int {
	val typedValue = TypedValue()
	return if (resolveAttribute(R.attr.jumio_nfc_customization, typedValue, true)) {
		typedValue.data
	} else {
		R.style.Jumio_Nfc_Customization
	}
}

internal fun View.animFloat(
	property: Property<View, Float>,
	interpolator: TimeInterpolator?,
	duration: Long,
	vararg values: Float,
	startDelay: Long = 0,
	repeat: Int = 0,
): ObjectAnimator = ObjectAnimator.ofFloat(this, property, *values).apply {
	this.duration = duration
	this.startDelay = startDelay
	this.interpolator = interpolator
	this.repeatCount = repeat
}

internal fun AppCompatImageView.setDrawable(resources: Resources, res: Int, theme: Resources.Theme) =
	setImageDrawable(ResourcesCompat.getDrawable(resources, res, theme))
