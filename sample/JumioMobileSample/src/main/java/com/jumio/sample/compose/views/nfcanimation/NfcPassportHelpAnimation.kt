package com.jumio.sample.compose.views.nfcanimation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.util.TypedValue
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.PathInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import com.jumio.commons.utils.dpToPx
import com.jumio.defaultui.R
import java.util.Collections
import kotlin.math.abs

class NfcPassportHelpAnimation(private val context: Context) : NfcHelpAnimationInterface {

	private var isActive = false
	private var isConfigured = false
	private var passportCoverFlipped = false

	private var coverOnlyDrawable: Drawable? = null
	private var passportCoverDrawable: Drawable? = null
	private var passportPageDrawable: Drawable? = null
	private var ivPassportOpened: AppCompatImageView? = null
	private var ivPassportCover: AppCompatImageView? = null
	private var ivPhone: AppCompatImageView? = null
	private var ivCheckmark: AppCompatImageView? = null
	private var animationContainer: RelativeLayout? = null
	private var globalAnimatorSet: AnimatorSet = AnimatorSet()
	private var bezierInterpolator: TimeInterpolator? = null
	private val passportHeightTotal = 144.dpToPx(context)
	private var passportMargin = 0f
	private var passportAnimWidthHalf = 0f
	private var isPassportUsa = false
	private var startTime = 0L

	override fun destroy() {
		stop()
		ivPassportOpened = null
		ivPassportCover = null
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

		isPassportUsa = isUsa

		// Width of the passport cover (EU) and the opened passport (US) for correct centering

		if (isPassportUsa) {
			passportAnimWidthHalf = 100.dpToPx(context).toFloat()
			passportMargin = 4.dpToPx(context).toFloat()
		} else {
			passportAnimWidthHalf = 48.dpToPx(context).toFloat()
			passportMargin = 8.dpToPx(context).toFloat()
		}

		bezierInterpolator = PathInterpolator(0.25f, 0f, 0.25f, 1f)

		animationContainer = rootView.findViewById<View>(R.id.animation_container) as RelativeLayout
		animationContainer?.alpha = 1f
		ivPassportCover = rootView.findViewById(R.id.iv_pp_cover)
		ivPassportOpened = rootView.findViewById(R.id.iv_pp_opened)

		ivPhone = rootView.findViewById(R.id.iv_phone)
		ivCheckmark = rootView.findViewById(R.id.iv_checkmark)

		applyCustomizations(rootView.context.resources)

		ivPassportCover?.reset()
		ivPassportOpened?.reset()
		ivPhone?.reset()
		ivCheckmark?.reset()
		isConfigured = true
	}

	override fun pause() {
		if (globalAnimatorSet.isRunning) {
			globalAnimatorSet.pause()
			ivCheckmark?.alpha = 0f
			if (isPassportUsa) {
				ivPassportCover?.alpha = 0f
				ivPassportOpened?.alpha = 1f
			} else {
				ivPassportCover?.alpha = 1f
				ivPassportOpened?.alpha = 0f
			}
			ivPhone?.let {
				it.animate()
					.alpha(1f)
					.translationX(
						passportAnimWidthHalf + (
							(abs(passportAnimWidthHalf - it.width)).div(2f)
							) - (passportMargin.div(2f))
					)
					.translationY(passportHeightTotal * 0.65f)
			}
		}
	}

	override fun resume() {
		if (globalAnimatorSet.isPaused) {
			globalAnimatorSet.cancel()
			ivPassportCover?.reset()
			ivPassportOpened?.reset()
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
		passportCoverDrawable = ResourcesCompat.getDrawable(resources, R.drawable.jumio_nfc_pp_cover, wrapper.theme)
		passportPageDrawable = ResourcesCompat.getDrawable(resources, R.drawable.jumio_nfc_pp_page, wrapper.theme)
		coverOnlyDrawable = ResourcesCompat.getDrawable(resources, R.drawable.jumio_nfc_pp_cover_only, wrapper.theme)

		val passportOpened = ResourcesCompat.getDrawable(resources, R.drawable.jumio_nfc_pp_open, wrapper.theme)
		ivPassportOpened?.setImageDrawable(passportOpened)

		val device = ResourcesCompat.getDrawable(resources, R.drawable.jumio_nfc_device, wrapper.theme)
		ivPhone?.setImageDrawable(device)

		val checkWhite = ResourcesCompat.getDrawable(resources, R.drawable.jumio_nfc_check_white, wrapper.theme)
		ivCheckmark?.setImageDrawable(checkWhite)
	}

	@Synchronized
	private fun startAnimation() {
		try {
			if (!isActive) return
			globalAnimatorSet = AnimatorSet()
			startTime = SystemClock.uptimeMillis()
			if (isPassportUsa) {
				globalAnimatorSet.playSequentially(
					fadeInPassportCover(),
					unfoldPassport(),
					appearPhone(),
					slidePhoneOverPassport(),
					displayCheckmark(),
					wait(1000),
					fadeOutAll()
				)
			} else {
				globalAnimatorSet.playSequentially(
					fadeInPassportCover(),
					wait(500),
					appearPhone(),
					slidePhoneOverPassport(),
					displayCheckmark(),
					wait(1000),
					fadeOutAll()
				)
			}
			globalAnimatorSet.addListener(
				object : AnimatorListenerAdapter() {
					override fun onAnimationCancel(animation: Animator) {
						super.onAnimationCancel(animation)
						isActive = false
					}

					override fun onAnimationEnd(animation: Animator) {
						super.onAnimationEnd(animation)
						if (isActive) {
							ivPassportCover?.reset()
							ivPassportOpened?.reset()
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

	private fun createFlipViewAnimation(
		viewToFlip: View?,
		animDuration: Long,
		animStartDelay: Long = 0,
		isCover: Boolean = false,
	): ObjectAnimator? {
		if (viewToFlip == null) return null

		viewToFlip.rotationY = 0f
		if (!isCover) {
			viewToFlip.pivotX = viewToFlip.left.toFloat()
		} else {
			viewToFlip.pivotX = 0.0f
		}

		val flipViewAnimator = ObjectAnimator.ofFloat(viewToFlip, "rotationY", 0f, -180f).apply {
			duration = animDuration
			startDelay = animStartDelay
			interpolator = bezierInterpolator
		}

		val updateListener = ValueAnimator.AnimatorUpdateListener {
			if (it.animatedFraction >= 0.01) {
				viewToFlip.alpha = 1f
			}

			if (it.animatedFraction >= 0.5 && !passportCoverFlipped) {
				ivPassportCover?.setImageDrawable(coverOnlyDrawable)
				passportCoverFlipped = true
			}
		}
		flipViewAnimator.addUpdateListener(updateListener)

		return flipViewAnimator
	}

	private fun fadeInPassportCover(): AnimatorSet {
		val animatorSet = AnimatorSet()
		ivPassportCover?.setImageDrawable(passportCoverDrawable)
		ivPassportCover?.translationX = passportAnimWidthHalf

		val fadeInPPCover = ivPassportCover?.let {
			ObjectAnimator.ofFloat(it, "alpha", 0.0f, 1.0f).apply {
				startDelay = 200
				duration = 200
				interpolator = AnticipateInterpolator()
			}
		}

		animatorSet.play(fadeInPPCover)
		return animatorSet
	}

	private fun unfoldPassport(): AnimatorSet {
		val animatorSet = AnimatorSet()
		val scale = context.resources.displayMetrics.density
		val screenHeight = context.resources.displayMetrics.heightPixels * 2

		val imageViewArray = arrayListOf<ImageView>()
		repeat((0..3).count()) {
			val ivTemp = ImageView(context)
			val passportSheetParams =
				RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT
				)
			ivTemp.layoutParams = passportSheetParams
			ivTemp.setImageDrawable(passportPageDrawable)
			ivTemp.alpha = 0f
			ivTemp.translationX = passportAnimWidthHalf
			ivTemp.translationY = passportMargin
			ivTemp.cameraDistance = scale * screenHeight
			imageViewArray.add(ivTemp)
		}

		imageViewArray.forEach {
			animationContainer?.addView(it)
		}

		animatorSet.addListener(
			object : AnimatorListenerAdapter() {
				override fun onAnimationStart(animation: Animator) {
					ivPassportOpened?.alpha = 0.0f
					ivPassportOpened?.cameraDistance = screenHeight * scale
					ivPassportCover?.cameraDistance = screenHeight * scale

					passportCoverFlipped = false
					super.onAnimationStart(animation)
				}

				override fun onAnimationEnd(animation: Animator) {
					ivPassportCover?.alpha = 0f
					imageViewArray.forEach { animationContainer?.removeView(it) }
					super.onAnimationEnd(animation)
				}
			}
		)

		val fadeInPPOpened = ivPassportOpened?.let {
			ObjectAnimator.ofFloat(it, "alpha", 0.0f, 1.0f).apply {
				startDelay = 199
				duration = 1
			}
		}

		val passportCoverAnimation = createFlipViewAnimation(ivPassportCover, animDuration = 400, isCover = true)

		val passportCoverUpdateListener = ValueAnimator.AnimatorUpdateListener {
			if (it.animatedFraction >= 0.45) {
				imageViewArray[0].alpha = 1f
			}
		}
		passportCoverAnimation?.addUpdateListener(passportCoverUpdateListener)

		animatorSet.startDelay = 1000
		animatorSet.playTogether(
			passportCoverAnimation,
			fadeInPPOpened,
			createFlipViewAnimation(imageViewArray[0], 800, 200),
			createFlipViewAnimation(imageViewArray[1], 800, 300),
			createFlipViewAnimation(imageViewArray[2], 1000, 400),
			createFlipViewAnimation(imageViewArray[3], 1200, 600)
		)
		return animatorSet
	}

	@SuppressLint("ObjectAnimatorBinding")
	private fun appearPhone(): AnimatorSet {
		val animatorSet = AnimatorSet()
		animatorSet.addListener(
			object : AnimatorListenerAdapter() {
				override fun onAnimationStart(animation: Animator) {
					ivPhone?.let {
						it.translationX = (
							passportAnimWidthHalf + (
								(abs(passportAnimWidthHalf - it.width)).div(2f)
								) - (passportMargin.div(2f))
							)
					}
					ivPhone?.translationY = passportHeightTotal * 0.15f

					ivPhone?.scaleX = 1.1f
					ivPhone?.scaleY = 1.1f

					super.onAnimationStart(animation)
				}
			}
		)

		animatorSet.startDelay = 200
		animatorSet.duration = 600

		if (ivPhone != null) {
			val appearPhone = ObjectAnimator.ofFloat(ivPhone, "alpha", 0f, 1f).apply {
				duration = 600
				interpolator = bezierInterpolator
			}
			val scaleXPhone = ObjectAnimator.ofFloat(ivPhone, "scaleX", 1.1f, 1f).apply {
				duration = 600
				interpolator = bezierInterpolator
			}
			val scaleYPhone = ObjectAnimator.ofFloat(ivPhone, "scaleY", 1.1f, 1f).apply {
				duration = 600
				interpolator = bezierInterpolator
			}
			animatorSet.playTogether(appearPhone, scaleXPhone, scaleYPhone)
		}

		return animatorSet
	}

	@SuppressLint("ObjectAnimatorBinding")
	private fun slidePhoneOverPassport(): AnimatorSet {
		val animatorSet = AnimatorSet()

		if (ivPhone != null) {
			val movePhoneDown1 = ObjectAnimator.ofFloat(
				ivPhone,
				"translationY",
				passportHeightTotal * 0.40f
			).apply {
				startDelay = 600
				duration = 600
				interpolator = bezierInterpolator
			}

			val movePhoneDown2 = ObjectAnimator.ofFloat(
				ivPhone,
				"translationY",
				passportHeightTotal * 0.65f
			).apply {
				startDelay = 600
				duration = 600
				interpolator = bezierInterpolator
			}

			animatorSet.playSequentially(movePhoneDown1, movePhoneDown2)
		}
		return animatorSet
	}

	private fun displayCheckmark(): AnimatorSet {
		val animatorSet = AnimatorSet()
		animatorSet.addListener(
			object : AnimatorListenerAdapter() {
				override fun onAnimationStart(animation: Animator) {
					val centerXPhone = ivPhone?.let { it.translationX + it.width.div(2f) }
					val centerYPhone = ivPhone?.let { it.translationY + it.height.div(2f) }
					if (centerXPhone != null && centerYPhone != null && ivCheckmark != null) {
						ivCheckmark?.translationX = centerXPhone.minus(ivCheckmark?.width?.div(2f)!!)
						ivCheckmark?.translationY = centerYPhone.minus(ivCheckmark?.height?.div(2f)!!)
					}
					super.onAnimationStart(animation)
				}
			}
		)
		val appearCheckmark = ivCheckmark?.let {
			ObjectAnimator.ofFloat(it, "alpha", 0f, 1f).apply {
				duration = 400
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

		ivPassportCover?.alpha = 0f
		val viewList: List<View?> = if (isPassportUsa) {
			listOf<View?>(ivPassportOpened, ivPhone, ivCheckmark)
		} else {
			listOf<View?>(ivPhone, ivCheckmark)
		}
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
