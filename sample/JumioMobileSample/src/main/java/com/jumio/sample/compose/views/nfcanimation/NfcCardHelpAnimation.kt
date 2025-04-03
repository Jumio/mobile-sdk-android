/*
 * Copyright 2025 Jumio Corporation, all rights reserved.
 */
package com.jumio.sample.compose.views.nfcanimation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.PathInterpolator
import android.widget.RelativeLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import com.jumio.commons.utils.dpToPx
import com.jumio.defaultui.R
import java.util.Collections

class NfcCardHelpAnimation(private val context: Context) : NfcHelpAnimationInterface {

	private var isActive = false
	private var isConfigured = false

	private var ivIdCard: AppCompatImageView? = null
	private var ivPhone: AppCompatImageView? = null
	private var ivCheckmark: AppCompatImageView? = null
	private var animationContainer: RelativeLayout? = null
	private var globalAnimatorSet: AnimatorSet = AnimatorSet()
	private var bezierInterpolator: TimeInterpolator? = null
	private val phoneHeightTotal = 167.dpToPx(context)
	private var startTime = 0L
	private var ivIdCardDrawable: Drawable? = null
	private var ivIdCardCenter = 0f

	override fun destroy() {
		stop()
		ivIdCard = null
		ivPhone = null
		ivCheckmark = null
		animationContainer = null
	}

	override fun isDestroyed(): Boolean = !isActive

	@Synchronized
	override fun start() {
		if (!isConfigured) return
		if (!isActive) {
			isActive = true
			startAnimation()
		}
	}

	@Synchronized
	private fun stop() {
		if (!isConfigured) return
		globalAnimatorSet.cancel()
	}

	@Synchronized
	override fun configure(rootView: View, isUsa: Boolean) {
		if (isActive) {
			stop()
		}

		bezierInterpolator = PathInterpolator(0.25f, 0f, 0.50f, 1f)

		animationContainer = rootView.findViewById<View>(R.id.animation_container) as RelativeLayout
		animationContainer?.alpha = 1f
		ivIdCard = rootView.findViewById(R.id.iv_id_card)
		ivPhone = rootView.findViewById(R.id.iv_phone)
		ivCheckmark = rootView.findViewById(R.id.iv_checkmark)

		applyCustomizations(rootView.context.resources)

		ivIdCardCenter = ((ivPhone as View).width - (ivIdCard as View).width) / 2f - 2f

		ivIdCard?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
			override fun onGlobalLayout() {
				ivIdCard?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
				ivIdCard?.let {
					it.translationX = -(it.width.div(2).toFloat())
				}
			}
		})

		ivIdCard?.reset()
		ivPhone?.reset()
		ivCheckmark?.reset()
		isConfigured = true
	}

	@Synchronized
	override fun pause() {
		if (globalAnimatorSet.isRunning) {
			globalAnimatorSet.pause()
			ivCheckmark?.alpha = 0f
			ivPhone?.alpha = 1f
			ivIdCard?.animate()?.alpha(1f)?.translationX(ivIdCardCenter)?.translationY(phoneHeightTotal * 0.30f)
		}
	}

	@Synchronized
	override fun resume() {
		if (globalAnimatorSet.isPaused) {
			globalAnimatorSet.cancel()
			ivIdCard?.reset()
			ivPhone?.reset()
			ivCheckmark?.reset()
			start()
		}
	}

	private fun applyCustomizations(resources: Resources) {
		val theme: Resources.Theme = context.theme
		val typedValue = TypedValue()
		var resourceId: Int = R.style.Jumio_Nfc_Customization
		if (theme.resolveAttribute(R.attr.jumio_nfc_customization, typedValue, true)) {
			resourceId = typedValue.data
		}
		val wrapper = ContextThemeWrapper(context, resourceId)

		val device = ResourcesCompat.getDrawable(resources, R.drawable.jumio_nfc_device, wrapper.theme)
		ivPhone?.setImageDrawable(device)

		val checkWhite = ResourcesCompat.getDrawable(resources, R.drawable.jumio_nfc_check_white, wrapper.theme)
		ivCheckmark?.setImageDrawable(checkWhite)
		ivIdCardDrawable = ResourcesCompat.getDrawable(resources, R.drawable.jumio_nfc_id_card, wrapper.theme)
		ivIdCard?.setImageDrawable(ivIdCardDrawable)
	}

	@Synchronized
	private fun startAnimation() {
		try {
			if (!isActive) return
			globalAnimatorSet = AnimatorSet()
			startTime = SystemClock.uptimeMillis()
			globalAnimatorSet.playSequentially(
				appearPhone(),
				fadeInIdCard(),
				slideCardBehindPhone(),
				displayCheckmark(),
				wait(1000),
				fadeOutAll()
			)

			globalAnimatorSet.addListener(
				object : AnimatorListenerAdapter() {
					override fun onAnimationCancel(animation: Animator) {
						super.onAnimationCancel(animation)
						isActive = false
					}

					override fun onAnimationEnd(animation: Animator) {
						super.onAnimationEnd(animation)
						if (isActive) {
							ivIdCard?.reset()
							ivPhone?.reset()
							ivCheckmark?.reset()

							startAnimation()
						}
					}
				}
			)
			globalAnimatorSet.start()
		} catch (ex: Exception) {
			isActive = false
		}
	}

	private fun fadeInIdCard(): AnimatorSet {
		val animatorSet = AnimatorSet()
		ivIdCard?.let {
			it.translationX = -(it.width.div(2).toFloat())
			it.translationY = phoneHeightTotal * 0.10f
			it.scaleX = 1.1f
			it.scaleY = 1.1f
		}

		animatorSet.startDelay = 800
		animatorSet.duration = 300

		if (ivIdCard != null) {
			val appear = ObjectAnimator.ofFloat(ivIdCard, "alpha", 0f, 1f).apply {
				duration = 300
				interpolator = bezierInterpolator
			}
			val scaleX = ObjectAnimator.ofFloat(ivIdCard, "scaleX", 1.1f, 1f).apply {
				duration = 300
				interpolator = bezierInterpolator
			}
			val scaleY = ObjectAnimator.ofFloat(ivIdCard, "scaleY", 1.1f, 1f).apply {
				duration = 300
				interpolator = bezierInterpolator
			}
			animatorSet.playTogether(appear, scaleX, scaleY)
		}

		return animatorSet
	}

	@SuppressLint("ObjectAnimatorBinding")
	private fun appearPhone(): AnimatorSet {
		val animatorSet = AnimatorSet()
		animatorSet.addListener(
			object : AnimatorListenerAdapter() {
				override fun onAnimationStart(animation: Animator) {
					ivPhone?.scaleX = 1.1f
					ivPhone?.scaleY = 1.1f
					super.onAnimationStart(animation)
				}
			}
		)

		animatorSet.startDelay = 300
		animatorSet.duration = 300

		if (ivPhone != null) {
			val appearPhone = ObjectAnimator.ofFloat(ivPhone, "alpha", 0f, 1f).apply {
				duration = 300
				interpolator = bezierInterpolator
			}
			val scaleXPhone = ObjectAnimator.ofFloat(ivPhone, "scaleX", 1.1f, 1f).apply {
				duration = 300
				interpolator = bezierInterpolator
			}
			val scaleYPhone = ObjectAnimator.ofFloat(ivPhone, "scaleY", 1.1f, 1f).apply {
				duration = 300
				interpolator = bezierInterpolator
			}
			animatorSet.playTogether(appearPhone, scaleXPhone, scaleYPhone)
		}

		return animatorSet
	}

	@SuppressLint("ObjectAnimatorBinding")
	private fun slideCardBehindPhone(): AnimatorSet {
		val animatorSet = AnimatorSet()

		if (ivIdCard != null) {
			val moveCardRight = ObjectAnimator.ofFloat(
				ivIdCard,
				"translationX",
				ivIdCardCenter
			).apply {
				startDelay = 800
				duration = 300
				interpolator = bezierInterpolator
			}

			val moveCardDown1 = ObjectAnimator.ofFloat(
				ivIdCard,
				"translationY",
				phoneHeightTotal * 0.30f
			).apply {
				startDelay = 800
				duration = 300
				interpolator = bezierInterpolator
			}

			val moveCardDown2 = ObjectAnimator.ofFloat(
				ivIdCard,
				"translationY",
				phoneHeightTotal * 0.50f
			).apply {
				startDelay = 800
				duration = 300
				interpolator = bezierInterpolator
			}

			val moveCardUp = ObjectAnimator.ofFloat(
				ivIdCard,
				"translationY",
				phoneHeightTotal * 0.30f
			).apply {
				startDelay = 800
				duration = 300
				interpolator = bezierInterpolator
			}

			animatorSet.playSequentially(moveCardRight, moveCardDown1, moveCardDown2, moveCardUp)
		}
		return animatorSet
	}

	private fun displayCheckmark(): AnimatorSet {
		val animatorSet = AnimatorSet()
		animatorSet.addListener(
			object : AnimatorListenerAdapter() {
				override fun onAnimationStart(animation: Animator) {
					val centerYPhone = ivPhone?.let { it.translationY + it.height.div(2f) }
					if (centerYPhone != null && ivCheckmark != null) {
						ivCheckmark?.translationY = centerYPhone.minus(ivCheckmark?.height?.div(2f)!!)
					}
					super.onAnimationStart(animation)
				}
			}
		)
		val appearCheckmark = ivCheckmark?.let {
			ObjectAnimator.ofFloat(it, "alpha", 0f, 1f).apply {
				startDelay = 800
				duration = 300
				interpolator = AnticipateOvershootInterpolator()
			}
		}

		animatorSet.play(appearCheckmark)
		return animatorSet
	}

	private fun wait(time: Long): AnimatorSet {
		val animatorSet = AnimatorSet()
		val placeholder = ivCheckmark?.let {
			ObjectAnimator.ofFloat(it, "rotationX", 0f, 0f).apply {
				duration = time
				interpolator = AnticipateOvershootInterpolator()
			}
		}
		animatorSet.play(placeholder)
		return animatorSet
	}

	private fun fadeOutAll(): AnimatorSet {
		val animatorSet = AnimatorSet()

		val viewList: List<View?> = listOf<View?>(ivPhone, ivCheckmark, ivIdCard)
		val animatorList = mutableListOf<ObjectAnimator>()
		viewList.forEach {
			animatorList.add(
				ObjectAnimator.ofFloat(it, "alpha", 1f, 0f).apply {
					duration = 200
				}
			)
		}
		val immutable: Collection<Animator> = Collections.unmodifiableList(animatorList)
		animatorSet.playTogether(immutable)
		return animatorSet
	}
}
