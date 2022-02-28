// Copyright 2021 Jumio Corporation, all rights reserved.
package com.jumio.sample.kotlin.customui

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.jumio.defaultui.JumioActivity
import com.jumio.sample.R
import com.jumio.sample.databinding.ActivityCustomuiBinding
import com.jumio.sdk.JumioSDK
import com.jumio.sdk.controller.JumioController
import com.jumio.sdk.credentials.*
import com.jumio.sdk.enums.*
import com.jumio.sdk.error.JumioError
import com.jumio.sdk.interfaces.JumioControllerInterface
import com.jumio.sdk.interfaces.JumioScanPartInterface
import com.jumio.sdk.result.JumioResult
import com.jumio.sdk.retry.JumioRetryReason
import com.jumio.sdk.scanpart.JumioScanPart
import com.jumio.sdk.views.JumioActivityAttacher
import java.text.DecimalFormat

/**
 * Sample activity that handles the whole jumio sdk workflow for the custom ui approach
 */
class CustomUiActivity : AppCompatActivity(), JumioControllerInterface, JumioScanPartInterface, AdapterView.OnItemSelectedListener {
	companion object {
		const val EXTRA_TOKEN = "token"
		const val EXTRA_DATACENTER = "datacenter"
		const val SELECTED_COUNTRY_STATE_KEY = "selectedCountryStateKey"
		const val SELECTED_DOCUMENT_STATE_KEY = "selectedDocumentStateKey"
		const val SELECTED_VARIANT_STATE_KEY = "selectedVariantStateKey"
		const val CREDENTIALS_PREFIX = "credentials"
		const val SCAN_SIDES_PREFIX = "scansides"

		@JvmStatic
		fun start(activity: Activity, activityResultLauncher: ActivityResultLauncher<Intent>, token: String, dataCenter: JumioDataCenter) {
			require(token.isNotEmpty()) { "Token needs to be set" }

			val intent = Intent(activity, CustomUiActivity::class.java).apply {
				putExtra(EXTRA_TOKEN, token)
				putExtra(EXTRA_DATACENTER, dataCenter)
			}

			activityResultLauncher.launch(intent)
		}
	}

	private lateinit var binding: ActivityCustomuiBinding
	private lateinit var sdk: JumioSDK
	private lateinit var jumioController: JumioController
	private var credential: JumioCredential? = null
	private var scanPart: JumioScanPart? = null

	private var customCountryAdapter: CustomCountryAdapter? = null
	private var customDocumentAdapter: CustomDocumentAdapter? = null
	private var customVariantAdapter: CustomVariantAdapter? = null

	private var successDrawable: Drawable? = null
	private var errorDrawable: Drawable? = null

	private var error: JumioError? = null
	private var retryReason: JumioRetryReason? = null

	/**
	 * Create or restore the JumioSDK instance and set all the click listeners
	 *
	 * @param savedInstanceState
	 */
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityCustomuiBinding.inflate(layoutInflater)
		setContentView(binding.root)

		sdk = JumioSDK(this)
		sdk.token = intent.getStringExtra(EXTRA_TOKEN) as String
		sdk.dataCenter = intent.getSerializableExtra(EXTRA_DATACENTER) as JumioDataCenter

		successDrawable = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.jumio_success))
		errorDrawable = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.jumio_error))

		if (savedInstanceState != null) {
			sdk.restore(this, savedInstanceState, this, this) { controller, credentials, activeCredential, activeScanPart ->
				jumioController = controller
				credential = activeCredential
				scanPart = activeScanPart

				hideView(binding.loadingIndicator)
				onInitialized(credentials, null)
				credential?.let {

					val country = savedInstanceState.getString(SELECTED_COUNTRY_STATE_KEY)
					val document = savedInstanceState.getString(SELECTED_DOCUMENT_STATE_KEY)
					val variant = savedInstanceState.getString(SELECTED_VARIANT_STATE_KEY)
					setupCredential(country, document, variant)
					restoreIconState(binding.credentialLayout, savedInstanceState, CREDENTIALS_PREFIX)
					scanPart?.let {
						setupScanPart()
						restoreIconState(binding.scanSideLayout, savedInstanceState, SCAN_SIDES_PREFIX)
					}
				}
			}
		} else {
			jumioController = sdk.start(this, this)
			showView(binding.loadingIndicator, binding.controllerControls, hideLoading = false)
		}

		binding.controllerCancel.setOnClickListener {
			jumioController.cancel()
			hideViewsAfter(binding.controllerControls)
			showView(binding.loadingIndicator)
		}
		binding.controllerFinish.setOnClickListener {
			catchAndShow {
				jumioController.finish()
				hideViewsAfter(binding.controllerControls)
				showView(binding.loadingIndicator)
			}
		}
		binding.btnSetCountryAndDocumentType.setOnClickListener {
			if (credential is JumioIDCredential) {
				val country = customCountryAdapter?.getItem(binding.customCountrySpinner.selectedItemPosition)
				val documentList = customCountryAdapter?.getDocumentList(binding.customCountrySpinner.selectedItemPosition)
				val documentType = customDocumentAdapter?.getDocumentType(binding.customDocumentSpinner.selectedItemPosition)
				val documentVariant = customVariantAdapter?.getDocumentVariant(binding.customVariantSpinner.selectedItemPosition)
				val jumioDocument = documentList?.find { it.type == documentType && it.variant == documentVariant }
				if (country != null && jumioDocument != null) {
					(credential as JumioIDCredential).let {
						it.setConfiguration(country, jumioDocument)
						setupScanSides(it.scanSides)
					}
				}
			}
		}

		binding.credentialFinish.setOnClickListener {
			catchAndShow {
				credential?.finish()
				updateIcon(binding.credentialLayout, credential?.isComplete == true)
				credential = null
				hideView(binding.credentialControls)
				hideViewsAfter(binding.credentialControls)
			}
		}
		binding.credentialCancel.setOnClickListener {
			credential?.cancel()
			updateIcon(binding.credentialLayout, credential?.isComplete == true)
			credential = null
			hideView(binding.credentialControls)
			hideViewsAfter(binding.credentialControls)
		}

		binding.scanPartStart.setOnClickListener {
			scanPart?.start()
		}
		binding.scanPartFinish.setOnClickListener {
			catchAndShow {
				scanPart?.finish()
				updateIcon(binding.scanSideLayout, true)
				scanPart = null
				hideView(binding.scanPartControls)
				hideViewsAfter(binding.scanPartControls)
			}
		}
		binding.scanPartCancel.setOnClickListener {
			catchAndShow {
				scanPart?.cancel()
				updateIcon(binding.scanSideLayout, false)
				scanPart = null
				hideView(binding.scanPartControls)
				hideViewsAfter(binding.scanPartControls)
			}
		}
		binding.addonInit.setOnClickListener {
			catchAndShow {
				scanPart = credential?.getAddonPart()
				hideView(binding.addonControls)
				showView(binding.scanPartControls)
			}
		}
		binding.addonCancel.setOnClickListener {
			catchAndShow {
				scanPart = credential?.getAddonPart()
				scanPart?.cancel()
				scanPart = null
				hideView(binding.addonControls)
			}
		}
		binding.extraction.setOnCheckedChangeListener { _, isChecked ->
			binding.scanView.extraction = isChecked
		}
		binding.switchCamera.setOnClickListener {
			binding.scanView.switchCamera()
		}
		binding.takePicture.setOnClickListener {
			binding.scanView.takePicture()
		}
		binding.toggleFlash.setOnClickListener {
			binding.scanView.flash = !binding.scanView.flash
		}
		binding.startFallback.setOnClickListener {
			scanPart?.fallback()
		}
		binding.retryScan.setOnClickListener {
			hideViewsAfter(binding.inlineScanLayout)
			binding.confirmationView.retake()
		}
		binding.confirmScan.setOnClickListener {
			hideViewsAfter(binding.inlineScanLayout)
			binding.confirmationView.confirm()
		}
		binding.rejectScan.setOnClickListener {
			hideViewsAfter(binding.inlineScanLayout)
			binding.rejectView.retake()
		}
		binding.partRetryButton.setOnClickListener {
			hideView(it)
			retryReason?.let {
				scanPart?.retry(it)
			}
		}
		binding.errorRetryButton.setOnClickListener {
			hideView(binding.errorRetryButton, showLoading = true)
			error?.let {
				jumioController.retry(it)
			}
		}
		binding.userConsentedButton.setOnClickListener {
			hideViewsAfter(binding.credentialLayout)
			jumioController.userConsented()
		}
	}

	/**
	 * Save the actual state and persist the SDK in case of activity persistence
	 *
	 * @param outState
	 */
	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)

		outState.putString(SELECTED_COUNTRY_STATE_KEY, binding.customCountrySpinner.selectedItem as String?)
		outState.putString(SELECTED_DOCUMENT_STATE_KEY, binding.customDocumentSpinner.selectedItem as String?)
		outState.putString(SELECTED_VARIANT_STATE_KEY, binding.customVariantSpinner.selectedItem as String?)

		//save views:
		saveIconState(binding.credentialLayout, outState, CREDENTIALS_PREFIX)
		saveIconState(binding.scanSideLayout, outState, SCAN_SIDES_PREFIX)

		if (this::jumioController.isInitialized && !jumioController.isComplete) {
			jumioController.persist(outState)
		}
	}

	/**
	 * In case the activity is destroyed and the workflow has not been finished already (indicated by [JumioController.isComplete]),
	 * make sure to call [jumioController.stop]
	 *
	 */
	override fun onDestroy() {
		super.onDestroy()

		if (this::jumioController.isInitialized && !jumioController.isComplete) {
			jumioController.stop()
		}
	}


	///////////////////////////////////////////////////////////////
	//                  JumioControllerInterface                 //
	///////////////////////////////////////////////////////////////
	/**
	 * Called as soon as the sdk is preloaded and credentials can be started.
	 * If the policyUrl is not null make sure to display user consent information and then
	 * call [JumioController.userConsented] if the user consents
	 *
	 * @param credentials
	 * @param policyUrl
	 */
	override fun onInitialized(credentials: ArrayList<JumioCredentialInfo>, policyUrl: String?) {
		policyUrl?.let {
			log("User consent required")
			binding.userConsentUrl.text = it
			showView(binding.userConsentLayout)
		}

		binding.credentialLayout.removeAllViews()
		credentials.forEach { credentialInfo ->
			val button = Button(this)
			button.text = "${credentialInfo.category.name} ${credentialInfo.id.subSequence(0, 5)}"
			button.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null)
			button.setOnClickListener { view ->
				try {
					credential = jumioController.start(credentialInfo)
					view.tag = true
				} catch (e: IllegalArgumentException) {
					e.message?.let {
						showError(it)
					}
					return@setOnClickListener
				}
				setupCredential()
			}
			binding.credentialLayout.addView(button)
		}
		showView(binding.credentialLayout)
	}

	/**
	 * Invoked in case an error occured - in case it is not retryable the [JumioController.cancel] needs to be called,
	 * otherwise it can be retried by calling [JumioController.retry]
	 *
	 * @param error
	 */
	override fun onError(error: JumioError) {
		if (error.isRetryable) {
			showView(binding.errorRetryButton)
		} else {
			hideView(binding.errorRetryButton)
		}
		hideView(binding.loadingIndicator)
		log(String.format("onError: %s, %s, %s", error.code, error.message, if (error.isRetryable) "true" else "false"), Color.RED)
		this.error = error
	}

	/**
	 * The workflow is now finished and the result can be consumed
	 *
	 * @param result
	 */
	override fun onFinished(result: JumioResult) {
		log("onFinished")
		val data = Intent()
		data.putExtra(JumioActivity.EXTRA_RESULT, result)
		setResult(Activity.RESULT_OK, data)
		finish()
	}

	///////////////////////////////////////////////////////////////
	//                 JumioScanPartInterface                    //
	///////////////////////////////////////////////////////////////
	/**
	 * [JumioScanUpdate] are optional information that can be consumed
	 *
	 * @param jumioScanUpdate
	 * @param data optional
	 */
	override fun onUpdate(jumioScanUpdate: JumioScanUpdate, data: Any?) {
		when (jumioScanUpdate) {
			JumioScanUpdate.LEGAL_HINT -> {
				log("LEGAL HINT")
				(data as String?)?.let {
					log(it)
				}
			}
			JumioScanUpdate.CAMERA_AVAILABLE -> {
				log("CAMERA_AVAILABLE")
				binding.toggleFlash.isEnabled = binding.scanView.hasFlash
				binding.switchCamera.isEnabled = binding.scanView.hasMultipleCameras
				binding.takePicture.isEnabled = binding.scanView.isShutterEnabled
			}
			JumioScanUpdate.FALLBACK -> {
				scanPart?.let {
					log("Start Fallback: ${it.scanMode}")
					binding.startFallback.isEnabled = it.hasFallback == true
					binding.takePicture.isEnabled = binding.scanView.isShutterEnabled
				}
			}
			JumioScanUpdate.NFC_EXTRACTION_STARTED -> {
				log("NFC Extraction started")
			}
			JumioScanUpdate.NFC_EXTRACTION_PROGRESS -> {
				log("NFC Extraction progress $data")
			}
			JumioScanUpdate.NFC_EXTRACTION_FINISHED -> {
				log("NFC Extraction finished")
			}
		}
	}

	/**
	 * [JumioScanStep] are required steps that must be handled
	 *
	 * @param jumioScanStep
	 * @param data optional
	 */
	override fun onScanStep(jumioScanStep: JumioScanStep, data: Any?) {
		log("onScanStep: ${jumioScanStep.name}")
		when (jumioScanStep) {
			JumioScanStep.PREPARE -> {
				showView(binding.loadingIndicator)
			}
			JumioScanStep.STARTED -> {
				hideView(binding.loadingIndicator)
			}
			JumioScanStep.SCAN_VIEW -> {
				showView(binding.inlineScanLayout)
				binding.scanView.invalidate()
				binding.scanView.requestLayout()
				scanPart?.let {
					binding.scanView.attach(it)
					lifecycle.addObserver(binding.scanView)
					binding.startFallback.isEnabled = it.hasFallback == true
				}
			}
			JumioScanStep.IMAGE_TAKEN -> {
				hideView(binding.inlineScanLayout, showLoading = true)
			}
			JumioScanStep.PROCESSING -> {
				showView(binding.loadingIndicator)
			}
			JumioScanStep.CONFIRMATION_VIEW -> {
				showView(binding.inlineConfirmLayout)
				scanPart?.let {
					binding.confirmationView.attach(it)
				}
			}
			JumioScanStep.REJECT_VIEW -> {
				if (data is String) {
					log("retry code: $data")
				}
				/**
				 * To display granular feedback, check against the values provided in [com.jumio.sdk.reject.JumioRejectReason]
				 */
//				when(data) {
//					JumioRejectReason.BLURRY -> TODO()
//					...
//				}
				showView(binding.inlineRejectLayout)
				scanPart?.let {
					binding.rejectView.attach(it)
				}
			}
			JumioScanStep.RETRY -> {
				if (data is JumioRetryReason) {
					log("retry reason: ${data.code}")
					log("retry message: ${data.message}")
					retryReason = data
				}
				showView(binding.partRetryButton)
			}
			JumioScanStep.CAN_FINISH -> {
				lifecycle.removeObserver(binding.scanView)
				hideView(binding.inlineConfirmLayout, binding.partRetryButton, binding.loadingIndicator)
			}
			JumioScanStep.ATTACH_ACTIVITY -> {
				scanPart?.let {
					JumioActivityAttacher(this).attach(it)
				}
			}
			JumioScanStep.ADDON_SCAN_PART -> {
				showView(binding.addonControls)
			}
		}
	}


	///////////////////////////////////////////////////////////////
	//                    Helper Functions                       //
	///////////////////////////////////////////////////////////////

	/**
	 * OnItemSelectedListener interface implementation for handling country, document and variant spinner changes
	 *
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
		when (parent) {
			binding.customCountrySpinner -> {
				customCountryAdapter?.getDocumentList(position)?.also {
					customDocumentAdapter = CustomDocumentAdapter(this@CustomUiActivity, it)
					binding.customDocumentSpinner.adapter = customDocumentAdapter
				}
			}
			binding.customDocumentSpinner -> {
				hideViewsAfter(binding.countryDocumentLayout)
				customDocumentAdapter?.getDocumentType(position)?.also { documentType ->
					customDocumentAdapter?.getDocumentVariants(documentType)?.also { variantList ->
						customVariantAdapter = CustomVariantAdapter(this@CustomUiActivity, variantList)
						binding.customVariantSpinner.adapter = customVariantAdapter
					}
				}
			}
			binding.customVariantSpinner -> {
				hideViewsAfter(binding.countryDocumentLayout)
			}
		}
	}

	override fun onNothingSelected(parent: AdapterView<*>) {
		// Left intentionally blank, nothing to do.
	}

	/**
	 * Setup credential section and preselect country, document and variant for ID credentials.
	 *
	 * @param country
	 * @param document
	 * @param variant
	 */
	private fun setupCredential(country: String? = null, document: String? = null, variant: String? = null) {
		showView(binding.credentialControls)
		when (credential) {
			is JumioIDCredential -> {
				(credential as JumioIDCredential).let {
					showView(binding.countryDocumentLayout)
					//setup country/doctype/variant spinner
					binding.customCountrySpinner.onItemSelectedListener = null
					binding.customDocumentSpinner.onItemSelectedListener = null
					binding.customVariantSpinner.onItemSelectedListener = null

					customCountryAdapter = CustomCountryAdapter(this, it.countries)
					binding.customCountrySpinner.adapter = customCountryAdapter
					binding.customCountrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
						override fun onItemSelected(parent: AdapterView<*>?, view: View?, bposition: Int, id: Long) {
							binding.customCountrySpinner.onItemSelectedListener = this@CustomUiActivity
						}

						override fun onNothingSelected(parent: AdapterView<*>?) {
							// Left intentionally blank, nothing to do.
						}

					}
					log("Suggested Country: ${it.suggestedCountry}")
					setSpinner(binding.customCountrySpinner, country ?: it.suggestedCountry)

					customCountryAdapter?.getDocumentList(binding.customCountrySpinner.selectedItemPosition)?.also {
						customDocumentAdapter = CustomDocumentAdapter(this@CustomUiActivity, it)
						binding.customDocumentSpinner.adapter = customDocumentAdapter
					}
					binding.customDocumentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
						override fun onItemSelected(parent: AdapterView<*>?, view: View?, bposition: Int, id: Long) {
							binding.customDocumentSpinner.onItemSelectedListener = this@CustomUiActivity
						}

						override fun onNothingSelected(parent: AdapterView<*>?) {
							// Left intentionally blank, nothing to do.
						}

					}
					setSpinner(binding.customDocumentSpinner, document)

					customDocumentAdapter?.getDocumentType(binding.customDocumentSpinner.selectedItemPosition)?.also { documentType ->
						customDocumentAdapter?.getDocumentVariants(documentType)?.also { variantList ->
							customVariantAdapter = CustomVariantAdapter(this@CustomUiActivity, variantList)
							binding.customVariantSpinner.adapter = customVariantAdapter
						}
					}
					binding.customVariantSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
						override fun onItemSelected(parent: AdapterView<*>?, view: View?, bposition: Int, id: Long) {
							binding.customVariantSpinner.onItemSelectedListener = this@CustomUiActivity
						}

						override fun onNothingSelected(parent: AdapterView<*>?) {
							// Left intentionally blank, nothing to do.
						}

					}
					setSpinner(binding.customVariantSpinner, variant)

					if (it.isConfigured) {
						setupScanSides(it.scanSides)
					}
				}
			}
			is JumioFaceCredential -> {
				hideView(binding.countryDocumentLayout)
				credential?.let {
					setupScanSides(it.scanSides)
				}
			}
		}
	}

	/**
	 * Setup the scan part in the gui
	 */
	private fun setupScanPart() {
		log("Extraction Method: ${scanPart?.scanMode}")
		showView(binding.scanPartControls)
		if (scanPart?.scanMode != JumioScanMode.FACE_IPROOV) {
			initScanView()
			showView(binding.seekBarLayout)
		}
	}

	/**
	 * Setup the scan sides in the gui
	 *
	 * @param scanSides
	 */
	private fun setupScanSides(scanSides: ArrayList<JumioScanSide>) {
		binding.scanSideLayout.removeAllViews()
		scanSides.forEach { scanSide ->
			val button = Button(this)
			button.text = scanSide.name
			button.setOnClickListener { view ->
				try {
					scanPart = credential?.initScanPart(scanSide, this)
					view.tag = true
					setupScanPart()
				} catch (e: Exception) {
					e.message?.let {
						showError(it)
					}
				}
			}
			button.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null)
			binding.scanSideLayout.addView(button)
		}
		showView(binding.scanSideLayout)
	}

	/**
	 * Initialize the scan view with the min size and set the ratio seekbar to sane values
	 *
	 */
	private fun initScanView() {
		val size = getScreenSize()
		val screenRatio = size.x.toFloat() / size.y
		val isPortrait = size.y > size.x

		val params = FrameLayout.LayoutParams(
			if (isPortrait) FrameLayout.LayoutParams.MATCH_PARENT else FrameLayout.LayoutParams.WRAP_CONTENT,
			if (isPortrait) FrameLayout.LayoutParams.WRAP_CONTENT else TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 300f, resources.displayMetrics
			).toInt()
		)
		binding.scanView.layoutParams = params

		binding.topMarginSeekBar.max = 100
		if (isPortrait) {
			binding.ratioSeekBar.max = ((binding.scanView.minRatio - screenRatio) * 100).toInt()
		} else {
			binding.ratioSeekBar.max = ((screenRatio - binding.scanView.minRatio) * 100).toInt()
		}

		val oldProgress = binding.ratioSeekBar.progress

		binding.ratioSeekBar.progress = 0
		binding.scanView.ratio = binding.scanView.minRatio
		binding.ratioTextView.text = ratioText(binding.scanView.ratio)
		binding.ratioTextView.setBackgroundColor(ContextCompat.getColor(this, R.color.spinnerBackground))

		binding.ratioSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
			override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
				// Left intentionally blank, nothing to do.
			}

			override fun onStartTrackingTouch(seekBar: SeekBar) {
				// Left intentionally blank, nothing to do.
			}

			override fun onStopTrackingTouch(seekBar: SeekBar) {
				if (isPortrait) binding.scanView.ratio = binding.scanView.minRatio - binding.ratioSeekBar.progress.toFloat() / 100
				else binding.scanView.ratio = binding.scanView.minRatio + binding.ratioSeekBar.progress.toFloat() / 100

				binding.ratioTextView.text = ratioText(binding.scanView.ratio)
				binding.scanView.invalidate()
				binding.scanView.requestLayout()
			}
		})

		binding.ratioSeekBar.progress = oldProgress
	}

	/**
	 * Formats ratio for displaying
	 *
	 * @param   ratio   Ratio from scanView
	 * @return          Ratio formatted in pattern "0.00"
	 */
	private fun ratioText(ratio: Float) = "Ratio: " + DecimalFormat("0.00").format(ratio)

	/**
	 * Get the current screen size
	 *
	 * @return
	 */
	@Suppress("DEPRECATION")
	private fun getScreenSize(): Point {
		val size = Point()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			val windowMetrics = windowManager.currentWindowMetrics
			val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
			size.x = windowMetrics.bounds.width() - insets.left - insets.right
			size.y = windowMetrics.bounds.height() - insets.top - insets.bottom
		} else {
			val display = windowManager.defaultDisplay
			display.getSize(size)
		}
		return size
	}

	/**
	 * Show different view sections
	 *
	 * @param views
	 * @param hideLoading
	 */
	private fun showView(vararg views: View, hideLoading: Boolean = true) {
		if (hideLoading) binding.loadingIndicator.visibility = View.GONE
		for (view in views) view.visibility = View.VISIBLE
	}

	/**
	 * Hide different view sections
	 *
	 * @param views
	 * @param showLoading
	 */
	private fun hideView(vararg views: View, showLoading: Boolean = false) {
		for (view in views) view.visibility = View.GONE
		if (showLoading) binding.loadingIndicator.visibility = View.VISIBLE
	}

	/**
	 * Hide all view sections after the specified one
	 *
	 * @param lastVisible
	 */
	private fun hideViewsAfter(lastVisible: View) {
		val views = arrayOf(
			binding.controllerControls,
			binding.credentialLayout,
			binding.credentialControls,
			binding.countryDocumentLayout,
			binding.scanSideLayout,
			binding.topMarginSeekBarLayout,
			binding.userConsentLayout,
			binding.scanPartControls,
			binding.seekBarLayout,
			binding.inlineScanLayout,
			binding.inlineConfirmLayout,
			binding.inlineRejectLayout,
			binding.animationView,
			binding.errorRetryButton,
			binding.partRetryButton,
			binding.loadingIndicator
		)
		val index = views.indexOf(lastVisible) + 1
		for (i in index..views.lastIndex) views[i].visibility = View.GONE
	}

	/**
	 * Save the icon state in different view groups (scan sides, credentials)
	 *
	 * @param group
	 * @param outState
	 * @param prefix
	 */
	private fun saveIconState(group: ViewGroup, outState: Bundle, prefix: String) {
		for (i in 0..group.childCount) {
			val child: View? = group.getChildAt(i)
			(child as Button?)?.let {
				outState.putBoolean("$prefix ${it.text}", it.compoundDrawables[0] == successDrawable)
				if (it.tag == true) {
					outState.putString(prefix, it.text.toString())
				}
			}
		}
	}


	/**
	 * Restore the icon state in different view groups (scan sides, credentials)
	 *
	 * @param group
	 * @param outState
	 * @param prefix
	 */
	private fun restoreIconState(group: ViewGroup, inState: Bundle, prefix: String) {
		for (i in 0..group.childCount) {
			val child: View? = group.getChildAt(i)
			val selected = inState.getString(prefix)
			(child as Button?)?.let {
				if (it.text == selected) {
					it.tag = true
				}
				it.setCompoundDrawables(if (inState.getBoolean("$prefix ${it.text}")) successDrawable else errorDrawable, null, null, null)
			}
		}
	}

	/**
	 * Update the icon of an active child in a group
	 *
	 * @param group
	 * @param success
	 */
	private fun updateIcon(group: ViewGroup, success: Boolean) {
		for (i in 0..group.childCount) {
			val child: View? = group.getChildAt(i)
			if (child?.tag == true) {
				(child as Button).setCompoundDrawablesWithIntrinsicBounds(if (success) successDrawable else errorDrawable, null, null, null)
				child.tag = false
				break
			}
		}
	}

	/**
	 * Display the message in a snackbar
	 *
	 * @param message
	 */
	private fun showError(message: String) {
		Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
	}

	/**
	 * Add an entry to the logging view
	 *
	 * @param message
	 * @param color
	 */
	private fun log(message: String?, color: Int = ContextCompat.getColor(this, R.color.logText)) {
		if (message == null) {
			return
		}
		Log.d("CustomUI", message)
		try {
			val logline = TextView(this)
			logline.text = message
			logline.setTextColor(color)
			binding.inlineCallbackLog.addView(logline, 0)
			if (binding.inlineCallbackLog.childCount > 40) binding.inlineCallbackLog.removeViewAt(binding.inlineCallbackLog.childCount - 1)
		} catch (e: Exception) {
			Log.e("CustomUI", String.format("Could not write to callback log: %s", e.message))
			Log.e("CustomUI", message)
		}
	}

	/**
	 * Helper to catch and show an exception
	 *
	 * @param function
	 */
	private fun catchAndShow(function: () -> Any) {
		try {
			function()
		} catch (e: Exception) {
			e.printStackTrace()
			e.message?.let {
				showError(it)
			}
		}
	}

	/**
	 * Helper function to select a value in a spinner
	 *
	 * @param spinner
	 * @param value
	 * @param compare
	 */
	private fun setSpinner(spinner: Spinner?, value: String?, compare: ((Any?) -> String)? = null) {
		val adapter = spinner?.adapter as ArrayAdapter<*>?
		spinner?.setBackgroundColor(ContextCompat.getColor(this, R.color.spinnerBackground))
		if (value == null) {
			if (adapter != null && !adapter.isEmpty) spinner?.setSelection(0)
			return
		}
		if (adapter == null) {
			return
		}
		for (i in 0 until adapter.count) {
			val format: String = if (compare != null) {
				compare(adapter.getItem(i))
			} else {
				adapter.getItem(i) as String
			}
			if (format == value) {
				spinner?.setSelection(i, false)
				break
			}
		}
	}
}
