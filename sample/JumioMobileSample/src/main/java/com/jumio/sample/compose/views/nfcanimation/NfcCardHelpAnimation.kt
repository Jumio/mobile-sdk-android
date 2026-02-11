/*
 * (c) 2026 Jumio All rights reserved. US Patent App.
 */
package com.jumio.sample.compose.views.nfcanimation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.os.SystemClock
import android.view.Gravity
import android.view.View
import android.view.View.ALPHA
import android.view.View.ROTATION_X
import android.view.View.SCALE_X
import android.view.View.SCALE_Y
import android.view.View.TRANSLATION_X
import android.view.View.TRANSLATION_Y
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatImageView
import com.jumio.defaultui.R
import com.jumio.sample.compose.extension.dpToPx
import com.jumio.sample.compose.extension.safeLet
import kotlin.math.abs
import kotlin.math.min

private val NFC_SCAN_INDICATOR_SCALE_KEYFRAMES = floatArrayOf(
	0.41f,
	0.54f,
	0.68f,
	0.75f,
	0.83f,
	0.92f,
	1.0f
)
private val ID_CHIP_PULSE_SCALE_KEYFRAMES = floatArrayOf(
	1.0f,
	1.8f,
	1.0f
)

internal class NfcCardHelpAnimation(private val context: Context) : NfcHelpAnimationInterface {

	private var isActive = false
	private var isConfigured = false

	private var ivIdCard: AppCompatImageView? = null
	private var ivPhone: AppCompatImageView? = null
	private var ivCheckmark: AppCompatImageView? = null
	private var ivIdCardChipIndicator: AppCompatImageView? = null
	private var ivNfcScanIndicator: AppCompatImageView? = null
	private var ivPhoneChipIndicator: DeviceNfcChipIndicator? = null
	private var animationContainer: RelativeLayout? = null
	private var phoneContainer: FrameLayout? = null
	private var idCardContainer: FrameLayout? = null
	private var globalAnimatorSet: AnimatorSet = AnimatorSet()
	private var bezierInterpolator: TimeInterpolator? = null
	private var startTime = 0L
	private var nfcChipLocation: NfcChipLocation? = null
	private val containerMargin = 50.dpToPx(context)
	private val springAnimationOvershoot = 2.dpToPx(context)
	private val phoneWidth = 79.dpToPx(context)
	private val phoneHeight = 167.dpToPx(context)
	private var isPauseRequested = false

	override fun destroy() {
		stop()
		ivIdCard = null
		ivPhone = null
		ivCheckmark = null
		ivIdCardChipIndicator = null
		ivNfcScanIndicator = null
		ivPhoneChipIndicator?.destroy()
		ivPhoneChipIndicator = null
		phoneContainer = null
		idCardContainer = null
		animationContainer = null
	}

	override fun isDestroyed(): Boolean = !isActive

	@Synchronized
	override fun start() {
		if (!isConfigured) return
		if (!isActive) {
			isActive = true
			animationContainer?.post {
				resizeLayoutAndAnimate()
			}
		}
	}

	@Synchronized
	private fun stop() {
		if (!isConfigured) return
		globalAnimatorSet.cancel()
	}

	@Synchronized
	override fun configure(rootView: View) {
		if (isActive) {
			stop()
		}

		bezierInterpolator = PathInterpolator(0.25f, 0f, 0.50f, 1f)

		animationContainer = rootView.findViewById<View>(R.id.animation_container) as RelativeLayout
		phoneContainer = rootView.findViewById<View>(R.id.iv_phone_container) as FrameLayout
		idCardContainer = rootView.findViewById<View>(R.id.iv_id_card_container) as FrameLayout

		ivIdCard = rootView.findViewById(R.id.iv_id_card)
		ivPhone = rootView.findViewById(R.id.iv_phone)
		ivCheckmark = rootView.findViewById(R.id.iv_checkmark)
		ivIdCardChipIndicator = rootView.findViewById(R.id.iv_id_chip_indicator)
		ivPhoneChipIndicator = rootView.findViewById(R.id.iv_device_chip_indicator)
		ivNfcScanIndicator = rootView.findViewById(R.id.iv_scan_indicator)

		applyCustomizations(rootView.context.resources)
		reset()
		isConfigured = true
	}

	@Synchronized
	override fun pause() {
		if (globalAnimatorSet.isRunning) {
			isPauseRequested = true
		}
	}

	@Synchronized
	override fun resume() {
		isPauseRequested = false
		if (globalAnimatorSet.isPaused) {
			globalAnimatorSet.cancel()
			start()
		}
	}

	private fun reset() {
		ivIdCard?.reset()
		ivPhone?.reset()
		ivCheckmark?.reset()
		ivIdCardChipIndicator?.reset()
		ivPhoneChipIndicator?.reset()
		ivNfcScanIndicator?.reset()
		phoneContainer?.reset(1f)
		idCardContainer?.reset(1f)
	}

	private fun resizeLayoutAndAnimate() {
		nfcChipLocation = NfcLocationHelper.getNFCLocation(context)
		val params = FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.WRAP_CONTENT,
			FrameLayout.LayoutParams.WRAP_CONTENT
		).apply {
			gravity = when (nfcChipLocation) {
				NfcChipLocation.TOP -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
				NfcChipLocation.BOTTOM -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
				else -> Gravity.CENTER
			}
		}
		ivPhoneChipIndicator?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
			override fun onGlobalLayout() {
				ivPhoneChipIndicator?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
				resizePhoneContainer()
			}
		})
		ivPhoneChipIndicator?.layoutParams = params
	}

	private fun resizePhoneContainer() {
		safeLet(animationContainer, phoneContainer, ivPhoneChipIndicator) { parent, phoneContainer, ivPhoneChipIndicator ->
			if (nfcChipLocation == NfcChipLocation.TOP || nfcChipLocation == NfcChipLocation.BOTTOM) {
				val ivPhoneChipCenterY = ivPhoneChipIndicator.y + ivPhoneChipIndicator.height.div(2f)
				val parentHeightHalf = parent.height.div(2f)
				val areaBelowChipCenter = phoneContainer.height - ivPhoneChipCenterY

				var scaleTop = 1.0f
				if (ivPhoneChipCenterY > parentHeightHalf) {
					scaleTop = parentHeightHalf / ivPhoneChipCenterY
				}

				var scaleBottom = 1.0f
				if (areaBelowChipCenter > parentHeightHalf) {
					scaleBottom = parentHeightHalf / areaBelowChipCenter
				}

				val scaleFactor = min(scaleTop, scaleBottom).coerceIn(0.1f, 1.0f)
				if (abs(scaleFactor - 1f) > 0) {
					phoneContainer.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
						override fun onGlobalLayout() {
							phoneContainer.viewTreeObserver?.removeOnGlobalLayoutListener(this)
							startAnimation()
						}
					})
					val params = phoneContainer.layoutParams
					params.width = (phoneWidth * scaleFactor).toInt()
					params.height = (phoneHeight * scaleFactor).toInt()
					phoneContainer.layoutParams = params
					return
				}
			}
			startAnimation()
		}
	}

	private fun applyCustomizations(resources: Resources) {
		val style = context.theme.resolveNfcStyle()
		val wrapper = ContextThemeWrapper(context, style)

		ivPhone?.setDrawable(resources, R.drawable.jumio_nfc_device, wrapper.theme)
		ivCheckmark?.setDrawable(resources, R.drawable.jumio_nfc_check_white, wrapper.theme)
		ivIdCard?.setDrawable(resources, R.drawable.jumio_nfc_id_card, wrapper.theme)
		ivIdCardChipIndicator?.setDrawable(resources, R.drawable.jumio_nfc_id_chip_indicator, wrapper.theme)
		ivNfcScanIndicator?.setDrawable(resources, R.drawable.jumio_nfc_scan_indicator, wrapper.theme)
		ivPhoneChipIndicator?.setDrawableWithGlowEffect(resources, R.drawable.jumio_nfc_device_chip_indicator, wrapper)
	}

	private fun setContainersPosition() {
		safeLet(animationContainer, idCardContainer, phoneContainer) { parent, idCardContainer, phoneContainer ->
			idCardContainer.translationX = -idCardContainer.width.div(2f) - containerMargin.div(2f)
			phoneContainer.translationX = phoneContainer.width.div(2f) + containerMargin.div(2f)
		}
	}

	@Synchronized
	private fun startAnimation() {
		try {
			if (!isActive) return
			reset()
			animationContainer?.alpha = 1f
			setContainersPosition()

			globalAnimatorSet = AnimatorSet().apply {
				startTime = SystemClock.uptimeMillis()
				playSequentially(
					fadeInCardAndPhone(),
					glowIdCardAndPhoneChipIndicator(),
					alignIdCardAndPhoneToSettle(),
					pulseNfcScanIndicator(),
					fadeInCheckmark(),
					fadeOutAll(),
					wait(1000)
				)
				addListener(object : AnimatorListenerAdapter() {
					override fun onAnimationCancel(animation: Animator) {
						isActive = false
						isPauseRequested = false
					}

					override fun onAnimationEnd(animation: Animator) {
						if (isActive) {
							startAnimation()
						}
					}
				})
				start()
			}
		} catch (_: Exception) {
			isActive = false
		}
	}

	private fun fadeInCardAndPhone() = AnimatorSet().apply {
		val duration = 800L
		this.startDelay = 100
		this.duration = duration
		playTogether(
			ivPhone?.animFloat(ALPHA, bezierInterpolator, duration, 0f, 1f),
			ivPhoneChipIndicator?.animFloat(ALPHA, bezierInterpolator, duration, 0f, 1f),
			ivIdCard?.animFloat(ALPHA, bezierInterpolator, duration, 0f, 1f),
			ivIdCardChipIndicator?.animFloat(ALPHA, bezierInterpolator, duration, 0f, 1f)
		)
	}

	private fun glowIdCardAndPhoneChipIndicator() = AnimatorSet().apply {
		playTogether(
			ivPhoneChipIndicator?.glowDeviceChipIndicator(1, 100),
			pulseIdCardChipIndicator(1, 100)
		)
	}

	private fun alignIdCardAndPhoneToSettle() = AnimatorSet().apply {
		val duration = 1800L
		this.startDelay = 100
		this.duration = duration

		safeLet(animationContainer, idCardContainer, phoneContainer, ivPhoneChipIndicator) {
				parent,
				idContainer,
				phoneContainer,
				ivPhoneChipIndicator,
			->
			val centerX = parent.width.div(2f)
			val centerY = parent.height.div(2f)

			val idX = centerX - idContainer.width.div(2f) - idContainer.left
			val phoneX = centerX - phoneContainer.width.div(2f) - phoneContainer.left
			val phoneY = when (nfcChipLocation) {
				NfcChipLocation.TOP, NfcChipLocation.BOTTOM ->
					centerY - phoneContainer.y - ivPhoneChipIndicator.y -
						ivPhoneChipIndicator.height.div(2f)
				else -> centerY - phoneContainer.height.div(2f) - phoneContainer.y
			}

			val idTranslationX = idContainer.animFloat(
				TRANSLATION_X,
				bezierInterpolator,
				duration,
				idX + springAnimationOvershoot
			)
			val phoneTranslationX = phoneContainer.animFloat(
				TRANSLATION_X,
				bezierInterpolator,
				duration,
				phoneX - springAnimationOvershoot
			)
			val phoneTranslationY = phoneContainer.animFloat(
				TRANSLATION_Y,
				bezierInterpolator,
				duration,
				phoneY
			)

			val idCardUpdateListener = ValueAnimator.AnimatorUpdateListener {
				if (it.animatedFraction == 1f) {
					idContainer.springAnimation(idX)
				}
			}
			idTranslationX.addUpdateListener(idCardUpdateListener)

			val phoneUpdateListener = ValueAnimator.AnimatorUpdateListener {
				if (it.animatedFraction == 1f) {
					phoneContainer.springAnimation(phoneX)
				}
			}
			phoneTranslationX.addUpdateListener(phoneUpdateListener)

			playTogether(idTranslationX, phoneTranslationX, phoneTranslationY)
		}
	}

	private fun pulseNfcScanIndicator() = AnimatorSet().apply {
		val duration = 1200L
		startDelay = 900
		this.duration = duration

		safeLet(ivNfcScanIndicator, ivPhoneChipIndicator) { ivNfcScanIndicator, ivDeviceNfcChipIndicator ->
			val scaleX = ivNfcScanIndicator.animFloat(
				SCALE_X,
				bezierInterpolator,
				duration,
				*NFC_SCAN_INDICATOR_SCALE_KEYFRAMES,
				repeat = 2
			)

			val scaleY = ivNfcScanIndicator.animFloat(
				SCALE_Y,
				bezierInterpolator,
				duration,
				*NFC_SCAN_INDICATOR_SCALE_KEYFRAMES,
				repeat = 2
			)

			val alpha = ivNfcScanIndicator.animFloat(
				ALPHA,
				bezierInterpolator,
				duration,
				0f,
				1f,
				0f,
				repeat = 2
			)

			playTogether(
				scaleX,
				scaleY,
				alpha,
				ivDeviceNfcChipIndicator.glowDeviceChipIndicator(2, 0),
				pulseIdCardChipIndicator(2, 0)
			)
		}
	}

	private fun pulseIdCardChipIndicator(repeat: Int, startDelay: Long) = AnimatorSet().apply {
		val duration = 1200L
		this.startDelay = startDelay
		this.duration = duration
		ivIdCardChipIndicator?.let {
			val appear = it.animFloat(ALPHA, bezierInterpolator, duration, 1f, repeat = repeat)
			val scaleX = it.animFloat(SCALE_X, bezierInterpolator, duration, *ID_CHIP_PULSE_SCALE_KEYFRAMES, repeat = repeat)
			val scaleY = it.animFloat(SCALE_Y, bezierInterpolator, duration, *ID_CHIP_PULSE_SCALE_KEYFRAMES, repeat = repeat)
			playTogether(appear, scaleX, scaleY)
		}
	}

	private fun fadeInCheckmark() = AnimatorSet().apply {
		val duration = 300L
		this.startDelay = 800
		safeLet(ivCheckmark, ivPhoneChipIndicator) { ivCheckmark, ivPhoneChipIndicator ->
			val phoneChipAlpha = ivPhoneChipIndicator.animFloat(ALPHA, bezierInterpolator, duration, 1f, 0f)
			val alpha = ivCheckmark.animFloat(ALPHA, bezierInterpolator, duration, 0f, 1f)
			val scaleX = ivCheckmark.animFloat(SCALE_X, bezierInterpolator, duration, 0.8f, 1f)
			val scaleY = ivCheckmark.animFloat(SCALE_Y, bezierInterpolator, duration, 0.8f, 1f)
			val animatorSet = AnimatorSet().apply {
				playTogether(phoneChipAlpha, alpha, scaleX, scaleY)
			}
			playSequentially(animatorSet, wait(1500))
			addListener(object : AnimatorListenerAdapter() {
				override fun onAnimationEnd(animation: Animator) {
					super.onAnimationEnd(animation)
					if (isPauseRequested) {
						isPauseRequested = false
						globalAnimatorSet.pause()
					}
				}
			})
		}
	}

	private fun wait(time: Long) = AnimatorSet().apply {
		play(animationContainer?.animFloat(ROTATION_X, AnticipateOvershootInterpolator(), time, 0f, 0f))
	}

	private fun fadeOutAll() = AnimatorSet().apply {
		play(animationContainer?.animFloat(ALPHA, AccelerateDecelerateInterpolator(), 200, 1f, 0f))
	}
}
