/*
 * Copyright 2025 Jumio Corporation, all rights reserved.
 */
package com.jumio.sample.compose.views.nfcanimation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.View.ALPHA
import android.view.View.ROTATION_Y
import android.view.View.TRANSLATION_X
import android.view.View.TRANSLATION_Y
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import com.jumio.defaultui.R
import com.jumio.sample.compose.extension.dpToPx
import com.jumio.sample.compose.extension.safeLet

internal class NfcUsPassportHelpAnimation(context: Context) : NfcPassportHelpAnimation(context) {

	private var passportCoverFlipped = false
	private var coverOnlyDrawable: Drawable? = null
	private var passportPageDrawable: Drawable? = null
	private var ivPassportOpened: AppCompatImageView? = null
	private var ppOpenContainer: FrameLayout? = null
	private var ppPageScale = 0f
	private val passportOpenWidth = 200.dpToPx(context)
	private val passportPageMargin = 4.dpToPx(context)

	override fun destroy() {
		super.destroy()
		ivPassportOpened = null
		ppOpenContainer = null
	}

	@Synchronized
	override fun start() {
		if (!isConfigured) return
		if (!isActive) {
			isActive = true
			animationContainer?.post {
				resizePassportContainer()
				resizeLayoutAndAnimate()
			}
		}
	}

	override fun configure(rootView: View) {
		ppOpenContainer = rootView.findViewById<View>(R.id.iv_pp_open_container) as FrameLayout
		ivPassportOpened = rootView.findViewById(R.id.iv_pp_opened)
		super.configure(rootView)
	}

	override fun reset() {
		super.reset()
		ivPassportOpened?.reset()
		ppOpenContainer?.reset(1f)
	}

	override fun applyCustomizations(resources: Resources) {
		super.applyCustomizations(resources)
		val style = context.theme.resolveNfcStyle()
		val wrapper = ContextThemeWrapper(context, style)
		passportPageDrawable = ResourcesCompat.getDrawable(resources, R.drawable.jumio_nfc_pp_page, wrapper.theme)
		coverOnlyDrawable = ResourcesCompat.getDrawable(resources, R.drawable.jumio_nfc_pp_cover_only, wrapper.theme)
		ivPassportOpened?.setDrawable(resources, R.drawable.jumio_nfc_pp_open, wrapper.theme)
	}

	private fun resizePassportContainer() {
		safeLet(animationContainer, ppCoverContainer, ppOpenContainer) { parent, ppCoverContainer, ppOpenContainer ->
			val parentWidthHalf = parent.width.div(2f)
			if (parentWidthHalf < ppOpenContainer.width) {
				val ppCoverParams = ppCoverContainer.layoutParams
				ppCoverParams.width = parentWidthHalf.div(2f).toInt()
				ppCoverContainer.layoutParams = ppCoverParams

				val ppOpenParams = ppOpenContainer.layoutParams
				ppOpenParams.width = parentWidthHalf.toInt()
				ppOpenContainer.layoutParams = ppOpenParams

				val widthScaleFactor = parentWidthHalf / passportOpenWidth
				ppPageScale = passportPageMargin * widthScaleFactor
			}
		}
	}

	override fun setContainersPosition() {
		safeLet(animationContainer, phoneContainer, ppCoverContainer, ppOpenContainer) {
				parent,
				phoneContainer,
				ppCoverContainer,
				ppOpenContainer,
			->
			phoneContainer.translationX = phoneContainer.width.div(2f) + containerMargin

			ppOpenContainer.translationX = parent.width.div(4f) - ppOpenContainer.width.div(2f) - ppOpenContainer.left
			ppOpenContainer.translationY = parent.height.div(2f) - ppOpenContainer.height.div(2f) - ppOpenContainer.y

			val ppOpenContainerX = ppOpenContainer.left + ppOpenContainer.translationX + ppOpenContainer.width.div(2f)
			ppCoverContainer.translationX = ppOpenContainerX - ppOpenContainer.width.div(4f) - ppCoverContainer.left
			ppCoverContainer.translationY = parent.height.div(2f) - ppCoverContainer.height.div(2f) - ppCoverContainer.y
		}
	}

	override val animators get() = listOf(
		fadeInPassportAndPhone(),
		unfoldPassport(),
		glowPPAndPhoneChipIndicator(),
		alignPPAndPhoneToSettle(),
		pulseNfcScanIndicator(),
		fadeInCheckmark(),
		fadeOutAll(),
		wait(1000)
	)

	private fun unfoldPassport() = AnimatorSet().apply {
		val animatorSet = AnimatorSet()
		val ppOpenContainer = ppOpenContainer ?: return@apply
		val scale = context.resources.displayMetrics.density
		val screenHeight = context.resources.displayMetrics.heightPixels * 2f

		val imageViewArray = arrayListOf<ImageView>()
		repeat((0..3).count()) {
			val ivTemp = ImageView(context).apply {
				val passportSheetParams = if (ppPageScale == 0f) {
					FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.WRAP_CONTENT,
						FrameLayout.LayoutParams.WRAP_CONTENT
					)
				} else {
					FrameLayout.LayoutParams(
						ppOpenContainer.width.div(2f).toInt() - ppPageScale.toInt(),
						(ppOpenContainer.height - ppPageScale.times(2f)).toInt()
					)
				}
				passportSheetParams.gravity = Gravity.CENTER or Gravity.START
				layoutParams = passportSheetParams
				setImageDrawable(passportPageDrawable)
				alpha = 0f
				translationX = ppOpenContainer.width.div(2f)
				cameraDistance = scale * screenHeight
			}
			imageViewArray.add(ivTemp)
		}

		imageViewArray.forEach {
			ppOpenContainer.addView(it)
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
					imageViewArray.forEach { ppOpenContainer.removeView(it) }
					super.onAnimationEnd(animation)
				}
			}
		)

		val transitionPPCover = ppCoverContainer?.let {
			val translationX = ppOpenContainer.x + ppOpenContainer.width.div(2f) - it.left
			it.animFloat(TRANSLATION_X, bezierInterpolator, 400, translationX)
		}

		val fadeInPPOpened = ivPassportOpened?.animFloat(ALPHA, bezierInterpolator, 1, 0.0f, 1.0f, startDelay = 200)

		val passportCoverAnimation = createFlipViewAnimation(ivPassportCover, animDuration = 400, isCover = true)

		val passportCoverUpdateListener = ValueAnimator.AnimatorUpdateListener {
			if (it.animatedFraction >= 0.45) {
				ivPPChipIndicator?.alpha = 0f
				imageViewArray[0].alpha = 1f
			}
		}
		passportCoverAnimation?.addUpdateListener(passportCoverUpdateListener)

		val lastPageAnimation = createFlipViewAnimation(imageViewArray[3], 1200, 600)
		val lastPageUpdateListener = ValueAnimator.AnimatorUpdateListener {
			if (it.animatedFraction >= 0.5) {
				ivPPChipIndicator?.alpha = 1f
			}
		}
		lastPageAnimation?.addUpdateListener(lastPageUpdateListener)

		animatorSet.startDelay = 1000
		animatorSet.playTogether(
			passportCoverAnimation,
			fadeInPPOpened,
			createFlipViewAnimation(imageViewArray[0], 800, 200),
			createFlipViewAnimation(imageViewArray[1], 800, 300),
			createFlipViewAnimation(imageViewArray[2], 1000, 400),
			lastPageAnimation
		)
		playSequentially(transitionPPCover, animatorSet)
	}

	private fun createFlipViewAnimation(
		viewToFlip: View?,
		animDuration: Long,
		animStartDelay: Long = 0,
		isCover: Boolean = false,
	): ObjectAnimator? {
		if (viewToFlip == null) return null

		viewToFlip.rotationY = 0f
		viewToFlip.pivotX = if (!isCover) {
			viewToFlip.left.toFloat()
		} else {
			0.0f
		}

		val flipViewAnimator = viewToFlip.animFloat(
			ROTATION_Y,
			bezierInterpolator,
			animDuration,
			0f,
			-180f,
			startDelay = animStartDelay
		)

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

	override fun alignPPAndPhoneToSettle() = AnimatorSet().apply {
		val duration = 1800L
		this.startDelay = 100
		this.duration = duration

		val parent = animationContainer ?: return@apply
		safeLet(ppCoverContainer, ppOpenContainer, phoneContainer, ivPhoneChipIndicator) {
				ppCoverContainer,
				ppOpenContainer,
				phoneContainer,
				ivPhoneChipIndicator,
			->

			val ppOpenX = parent.width.div(2f) - ppOpenContainer.width.div(2f) - ppOpenContainer.left
			val ppOpenY = parent.height.div(2f) - (ppOpenContainer.height.div(2f)) - ppOpenContainer.top

			val targetPPOpenX = ppOpenContainer.left + ppOpenX + ppOpenContainer.width.times(0.75f)
			val targetPPOpenY = ppOpenContainer.top + ppOpenY + ppOpenContainer.height.div(2f)

			val ppCoverX = targetPPOpenX - ppCoverContainer.width.div(2f) - ppCoverContainer.left
			val ppCoverY = targetPPOpenY - ppCoverContainer.height.div(2f) - ppCoverContainer.top

			val phoneX = targetPPOpenX - phoneContainer.width.div(2f) - phoneContainer.left
			val phoneY = when (nfcChipLocation) {
				NfcChipLocation.TOP, NfcChipLocation.BOTTOM ->
					targetPPOpenY - phoneContainer.y - ivPhoneChipIndicator.y -
						ivPhoneChipIndicator.height.div(2f)
				else -> targetPPOpenY - phoneContainer.height.div(2f) - phoneContainer.y
			}

			val ppOpenTranslationX = ppOpenContainer.animFloat(
				TRANSLATION_X,
				bezierInterpolator,
				duration,
				ppOpenX + springAnimationOvershoot
			)
			val ppOpenTranslationY = ppOpenContainer.animFloat(TRANSLATION_Y, bezierInterpolator, duration, ppOpenY)
			val ppCoverTranslationX = ppCoverContainer.animFloat(TRANSLATION_X, bezierInterpolator, duration, ppCoverX)
			val ppCoverTranslationY = ppCoverContainer.animFloat(TRANSLATION_Y, bezierInterpolator, duration, ppCoverY)
			val phoneTranslationX = phoneContainer.animFloat(
				TRANSLATION_X,
				bezierInterpolator,
				duration,
				phoneX - springAnimationOvershoot
			)
			val phoneTranslationY = phoneContainer.animFloat(TRANSLATION_Y, bezierInterpolator, duration, phoneY)

			val ppOpenUpdateListener = ValueAnimator.AnimatorUpdateListener {
				if (it.animatedFraction == 1f) {
					ppOpenContainer.springAnimation(ppOpenX)
				}
			}
			ppOpenTranslationX.addUpdateListener(ppOpenUpdateListener)

			val phoneUpdateListener = ValueAnimator.AnimatorUpdateListener {
				if (it.animatedFraction == 1f) {
					phoneContainer.springAnimation(phoneX)
				}
			}
			phoneTranslationX.addUpdateListener(phoneUpdateListener)

			playTogether(
				ppCoverTranslationX,
				ppCoverTranslationY,
				phoneTranslationX,
				phoneTranslationY,
				ppOpenTranslationX,
				ppOpenTranslationY
			)
		}
	}
}
