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
import android.graphics.drawable.Drawable
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
import androidx.core.content.res.ResourcesCompat
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
private val PP_CHIP_PULSE_SCALE_KEYFRAMES = floatArrayOf(
	1.0f,
	1.8f,
	1.0f
)

internal open class NfcPassportHelpAnimation(protected val context: Context) : NfcHelpAnimationInterface {

	protected var isActive = false
	protected var isConfigured = false

	private var passportCoverDrawable: Drawable? = null
	protected var ivPassportCover: AppCompatImageView? = null
	protected var ivPhone: AppCompatImageView? = null
	private var ivCheckmark: AppCompatImageView? = null
	protected var ivPPChipIndicator: AppCompatImageView? = null
	private var ivNfcScanIndicator: AppCompatImageView? = null
	protected var ivPhoneChipIndicator: DeviceNfcChipIndicator? = null
	protected var animationContainer: RelativeLayout? = null
	protected var phoneContainer: FrameLayout? = null
	protected var ppCoverContainer: FrameLayout? = null
	private var globalAnimatorSet: AnimatorSet = AnimatorSet()
	protected var bezierInterpolator: TimeInterpolator? = null
	private var startTime = 0L
	protected var nfcChipLocation: NfcChipLocation? = null
	protected val containerMargin = 50.dpToPx(context)
	protected val springAnimationOvershoot = 2.dpToPx(context)
	private val phoneWidth = 79.dpToPx(context)
	private val phoneHeight = 167.dpToPx(context)
	private var isPauseRequested = false

	override fun destroy() {
		stop()
		ivPassportCover = null
		ivPhone = null
		ivCheckmark = null
		ivPPChipIndicator = null
		ivNfcScanIndicator = null
		ivPhoneChipIndicator?.destroy()
		ivPhoneChipIndicator = null
		phoneContainer = null
		ppCoverContainer = null
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

		bezierInterpolator = PathInterpolator(0.25f, 0f, 0.25f, 1f)

		animationContainer = rootView.findViewById<View>(R.id.animation_container) as RelativeLayout
		phoneContainer = rootView.findViewById<View>(R.id.iv_phone_container) as FrameLayout
		ppCoverContainer = rootView.findViewById<View>(R.id.iv_pp_cover_container) as FrameLayout

		ivPassportCover = rootView.findViewById(R.id.iv_pp_cover)
		ivPhone = rootView.findViewById(R.id.iv_phone)
		ivCheckmark = rootView.findViewById(R.id.iv_checkmark)
		ivPPChipIndicator = rootView.findViewById(R.id.iv_pp_chip_indicator)
		ivPhoneChipIndicator = rootView.findViewById(R.id.iv_phone_chip_indicator)
		ivNfcScanIndicator = rootView.findViewById(R.id.iv_scan_indicator)

		applyCustomizations(rootView.context.resources)
		reset()
		isConfigured = true
	}

	override fun pause() {
		if (globalAnimatorSet.isRunning) {
			isPauseRequested = true
		}
	}

	override fun resume() {
		isPauseRequested = false
		if (globalAnimatorSet.isPaused) {
			globalAnimatorSet.cancel()
			start()
		}
	}

	protected open fun reset() {
		ivPassportCover?.reset()
		ivPhone?.reset()
		ivCheckmark?.reset()
		ivPPChipIndicator?.reset()
		ivPhoneChipIndicator?.reset()
		ivNfcScanIndicator?.reset()
		phoneContainer?.reset(1f)
		ppCoverContainer?.reset(1f)
	}

	protected fun resizeLayoutAndAnimate() {
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

	protected open fun applyCustomizations(resources: Resources) {
		val style = context.theme.resolveNfcStyle()
		val wrapper = ContextThemeWrapper(context, style)

		passportCoverDrawable = ResourcesCompat.getDrawable(resources, R.drawable.jumio_nfc_pp_cover, wrapper.theme)
		ivPhone?.setDrawable(resources, R.drawable.jumio_nfc_device, wrapper.theme)
		ivPPChipIndicator?.setDrawable(resources, R.drawable.jumio_nfc_id_chip_indicator, wrapper.theme)
		ivNfcScanIndicator?.setDrawable(resources, R.drawable.jumio_nfc_scan_indicator, wrapper.theme)
		ivCheckmark?.setDrawable(resources, R.drawable.jumio_nfc_check_white, wrapper.theme)
		ivPhoneChipIndicator?.setDrawableWithGlowEffect(resources, R.drawable.jumio_nfc_device_chip_indicator, wrapper)
	}

	protected open fun setContainersPosition() {
		safeLet(animationContainer, phoneContainer, ppCoverContainer) {
				parent,
				phoneContainer,
				ppCoverContainer,
			->
			phoneContainer.translationX = phoneContainer.width.div(2f) + containerMargin.div(2f)
			ppCoverContainer.translationX = -ppCoverContainer.width.div(2f) - containerMargin.div(2f)
			ppCoverContainer.translationY = parent.height.div(2f) - ppCoverContainer.height.div(2f) - ppCoverContainer.y
		}
	}

	@Synchronized
	protected fun startAnimation() {
		try {
			if (!isActive) return
			reset()
			animationContainer?.alpha = 1f
			setContainersPosition()

			globalAnimatorSet = AnimatorSet().apply {
				startTime = SystemClock.uptimeMillis()
				playSequentially(animators)
				addListener(
					object : AnimatorListenerAdapter() {
						override fun onAnimationCancel(animation: Animator) {
							super.onAnimationCancel(animation)
							isActive = false
							isPauseRequested = false
						}

						override fun onAnimationEnd(animation: Animator) {
							super.onAnimationEnd(animation)
							if (isActive) {
								startAnimation()
							}
						}
					}
				)
				start()
			}
		} catch (_: Exception) {
			isActive = false
		}
	}

	protected open val animators get() = listOf(
		fadeInPassportAndPhone(),
		glowPPAndPhoneChipIndicator(),
		alignPPAndPhoneToSettle(),
		pulseNfcScanIndicator(),
		fadeInCheckmark(),
		fadeOutAll(),
		wait(1000)
	)

	protected fun fadeInPassportAndPhone() = AnimatorSet().apply {
		val duration = 800L
		this.startDelay = 100
		this.duration = duration
		ivPassportCover?.setImageDrawable(passportCoverDrawable)
		val phone = ivPhone?.animFloat(ALPHA, bezierInterpolator, duration, 0f, 1f)
		val phoneChipIndicator = ivPhoneChipIndicator?.animFloat(ALPHA, bezierInterpolator, duration, 0f, 1f)
		val ppCover = ivPassportCover?.animFloat(ALPHA, bezierInterpolator, duration, 0f, 1f)
		val ppChipIndicator = ivPPChipIndicator?.animFloat(ALPHA, bezierInterpolator, duration, 0f, 1f)

		playTogether(
			phone,
			phoneChipIndicator,
			ppCover,
			ppChipIndicator
		)
	}

	protected fun glowPPAndPhoneChipIndicator() = AnimatorSet().apply {
		playTogether(ivPhoneChipIndicator?.glowDeviceChipIndicator(1, 100), pulsePPChipIndicator(1, 100))
	}

	protected open fun alignPPAndPhoneToSettle() = AnimatorSet().apply {
		val duration = 1800L
		this.startDelay = 100
		this.duration = duration

		safeLet(animationContainer, ppCoverContainer, phoneContainer, ivPhoneChipIndicator) {
				parent,
				ppCoverContainer,
				phoneContainer,
				ivPhoneChipIndicator,
			->
			val centerX = parent.width.div(2f)
			val centerY = parent.height.div(2f)

			val ppX = centerX - ppCoverContainer.width.div(2f) - ppCoverContainer.left
			val phoneX = centerX - phoneContainer.width.div(2f) - phoneContainer.left
			val phoneY = when (nfcChipLocation) {
				NfcChipLocation.TOP, NfcChipLocation.BOTTOM ->
					centerY - phoneContainer.y - ivPhoneChipIndicator.y -
						ivPhoneChipIndicator.height.div(2f)
				else -> centerY - phoneContainer.height.div(2f) - phoneContainer.y
			}

			val ppTranslationX = ppCoverContainer.animFloat(
				TRANSLATION_X,
				bezierInterpolator,
				duration,
				ppX + springAnimationOvershoot
			)
			val phoneTranslationX = phoneContainer.animFloat(
				TRANSLATION_X,
				bezierInterpolator,
				duration,
				phoneX - springAnimationOvershoot
			)
			val phoneTranslationY = phoneContainer.animFloat(TRANSLATION_Y, bezierInterpolator, duration, phoneY)

			val ppUpdateListener = ValueAnimator.AnimatorUpdateListener {
				if (it.animatedFraction == 1f) {
					ppCoverContainer.springAnimation(ppX)
				}
			}
			ppTranslationX.addUpdateListener(ppUpdateListener)

			val phoneUpdateListener = ValueAnimator.AnimatorUpdateListener {
				if (it.animatedFraction == 1f) {
					phoneContainer.springAnimation(phoneX)
				}
			}
			phoneTranslationX.addUpdateListener(phoneUpdateListener)

			playTogether(ppTranslationX, phoneTranslationX, phoneTranslationY)
		}
	}

	protected fun pulseNfcScanIndicator() = AnimatorSet().apply {
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
				pulsePPChipIndicator(2, 0)
			)
		}
	}

	protected fun pulsePPChipIndicator(repeat: Int, startDelay: Long) = AnimatorSet().apply {
		val duration = 1200L
		this.startDelay = startDelay
		this.duration = duration
		ivPPChipIndicator?.let {
			val alpha = it.animFloat(ALPHA, bezierInterpolator, duration, 1f, repeat = repeat)
			val scaleX = it.animFloat(SCALE_X, bezierInterpolator, duration, *PP_CHIP_PULSE_SCALE_KEYFRAMES, repeat = repeat)
			val scaleY = it.animFloat(SCALE_Y, bezierInterpolator, duration, *PP_CHIP_PULSE_SCALE_KEYFRAMES, repeat = repeat)
			playTogether(alpha, scaleX, scaleY)
		}
	}

	protected fun fadeInCheckmark() = AnimatorSet().apply {
		val duration = 300L
		this.startDelay = 800
		this.duration = duration
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

	protected fun wait(time: Long) = AnimatorSet().apply {
		play(animationContainer?.animFloat(ROTATION_X, AnticipateOvershootInterpolator(), time, 0f, 0f))
	}

	protected fun fadeOutAll() = AnimatorSet().apply {
		play(animationContainer?.animFloat(ALPHA, AccelerateDecelerateInterpolator(), 200, 1f, 0f))
	}
}
