/*
 * Copyright (c) 2025. Jumio Corporation All rights reserved.
 */
package com.jumio.sample.compose.views.nfcanimation

import android.animation.AnimatorSet
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.jumio.defaultui.R
import com.jumio.sample.compose.extension.safeLet

private const val DEFAULT_ICON_HEIGHT_DP = 24
private const val GLOW_BITMAP_TARGET_HEIGHT_DP = 34
private const val GLOW_BLUR_RADIUS_DP = 10f
private const val GLOW_Y_OFFSET_DP = 2f

internal class DeviceNfcChipIndicator @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

	private val Int.dp get() = (this * resources.displayMetrics.density).toInt()
	private val Float.dp get() = this * resources.displayMetrics.density

	private var glowImageView: AppCompatImageView? = AppCompatImageView(context).apply {
		alpha = 0f
		scaleType = ImageView.ScaleType.CENTER_INSIDE
	}

	private var iconImageView: AppCompatImageView? = AppCompatImageView(context).apply {
		scaleType = ImageView.ScaleType.CENTER_INSIDE
	}

	init {
		addView(glowImageView, centeredLayoutParams(WRAP_CONTENT))
		addView(iconImageView, centeredLayoutParams(DEFAULT_ICON_HEIGHT_DP.dp))
	}

	private fun centeredLayoutParams(height: Int) = LayoutParams(WRAP_CONTENT, height).apply { gravity = Gravity.CENTER }

	fun setDrawableWithGlowEffect(resources: Resources, res: Int, wrapper: ContextThemeWrapper) {
		safeLet(iconImageView, glowImageView) { iconImageView, glowImageView ->
			iconImageView.setDrawable(resources, res, wrapper.theme)
			val colorRes = TypedValue()
			if (wrapper.theme.resolveAttribute(R.attr.jumio_nfc_chip_glow, colorRes, true)) {
				val glowBitmap = createAlignedGlowBitmap(
					drawableToBitmap(iconImageView.drawable, (GLOW_BITMAP_TARGET_HEIGHT_DP.dp)),
					GLOW_BLUR_RADIUS_DP.dp,
					ContextCompat.getColor(wrapper, colorRes.resourceId),
					GLOW_Y_OFFSET_DP.dp
				)
				glowImageView.setImageBitmap(glowBitmap)
			}
		}
	}

	private fun drawableToBitmap(drawable: Drawable, targetHeightPx: Int): Bitmap {
		(drawable as? BitmapDrawable)?.bitmap?.let { return it }
		val aspectRatio =
			drawable.intrinsicWidth.coerceAtLeast(1).toFloat() / drawable.intrinsicHeight.coerceAtLeast(1).toFloat()
		val targetWidth = (targetHeightPx * aspectRatio).toInt().coerceAtLeast(1)

		return createBitmap(targetWidth, targetHeightPx).apply {
			Canvas(this).apply {
				drawable.setBounds(0, 0, width, height)
				drawable.draw(this)
			}
		}
	}

	private fun createAlignedGlowBitmap(src: Bitmap, blurRadius: Float, glowColor: Int, yOffset: Float): Bitmap {
		val margin = blurRadius.times(2).toInt()
		val output = createBitmap(src.width + margin, src.height + margin)

		val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
			color = glowColor
			maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
		}

		val top = margin.div(2f) + yOffset
		val alpha = src.extractAlpha()
		Canvas(output).drawBitmap(alpha, margin.div(2f), top, paint)
		alpha.recycle()

		return output
	}

	fun glowDeviceChipIndicator(repeat: Int, startDelay: Long) = AnimatorSet().apply {
		this.startDelay = startDelay
		duration = 1200
		play(
			glowImageView?.animFloat(
				ALPHA,
				PathInterpolator(0.25f, 0f, 0.50f, 1f),
				1200,
				0f,
				1f,
				0f,
				repeat = repeat
			)
		)
	}

	fun destroy() {
		glowImageView = null
		iconImageView = null
	}
}
