// Copyright 2022 Jumio Corporation, all rights reserved.
package com.jumio.sample.kotlin.customui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.jumio.commons.log.Log
import com.jumio.commons.utils.ScreenUtil
import com.jumio.core.views.CameraScanView
import com.jumio.defaultui.JumioActivity
import com.jumio.sample.R
import com.jumio.sample.databinding.ActivityCustomuiBinding
import com.jumio.sample.kotlin.customui.adapter.CustomConsentAdapter
import com.jumio.sample.kotlin.customui.adapter.CustomCountryAdapter
import com.jumio.sample.kotlin.customui.adapter.CustomDocumentAdapter
import com.jumio.sample.kotlin.customui.adapter.JumioArrayAdapter
import com.jumio.sdk.JumioSDK
import com.jumio.sdk.consent.JumioConsentItem
import com.jumio.sdk.controller.JumioController
import com.jumio.sdk.credentials.JumioCredential
import com.jumio.sdk.credentials.JumioCredentialInfo
import com.jumio.sdk.credentials.JumioDataCredential
import com.jumio.sdk.credentials.JumioDocumentCredential
import com.jumio.sdk.credentials.JumioFaceCredential
import com.jumio.sdk.credentials.JumioIDCredential
import com.jumio.sdk.enums.JumioAcquireMode
import com.jumio.sdk.enums.JumioConsentType
import com.jumio.sdk.enums.JumioCredentialPart
import com.jumio.sdk.enums.JumioDataCenter
import com.jumio.sdk.enums.JumioFallbackReason
import com.jumio.sdk.enums.JumioScanMode
import com.jumio.sdk.enums.JumioScanStep
import com.jumio.sdk.enums.JumioScanUpdate
import com.jumio.sdk.error.JumioError
import com.jumio.sdk.exceptions.SDKNotConfiguredException
import com.jumio.sdk.handler.JumioConfirmationHandler
import com.jumio.sdk.handler.JumioRejectHandler
import com.jumio.sdk.interfaces.JumioControllerInterface
import com.jumio.sdk.interfaces.JumioScanPartInterface
import com.jumio.sdk.result.JumioResult
import com.jumio.sdk.retry.JumioRetryReason
import com.jumio.sdk.scanpart.JumioScanPart
import com.jumio.sdk.util.JumioDeepLinkHandler
import com.jumio.sdk.views.JumioActivityAttacher
import com.jumio.sdk.views.JumioConfirmationView
import com.jumio.sdk.views.JumioFileAttacher
import com.jumio.sdk.views.JumioRejectView
import java.text.DecimalFormat

private const val TAG = "UI-Less"

/**
 * Sample implementation that handles the whole Jumio SDK Workflow for the Custom UI approach
 */
class CustomUiActivity :
	AppCompatActivity(),
	JumioControllerInterface,
	JumioScanPartInterface,
	AdapterView.OnItemSelectedListener {

	private lateinit var sdk: JumioSDK
	private lateinit var binding: ActivityCustomuiBinding
	private lateinit var jumioController: JumioController

	private var consentItems: List<JumioConsentItem>? = null
	private var credential: JumioCredential? = null
	private var scanPart: JumioScanPart? = null

	private var customCountryAdapter: CustomCountryAdapter? = null
	private var customDocumentAdapter: CustomDocumentAdapter? = null
	private var customConsentAdapter: CustomConsentAdapter? = null

	private var confirmationHandler: JumioConfirmationHandler = JumioConfirmationHandler()
	private var rejectHandler: JumioRejectHandler = JumioRejectHandler()

	private var successDrawable: Drawable? = null
	private var errorDrawable: Drawable? = null

	private var error: JumioError? = null
	private var retryReason: JumioRetryReason? = null

	private val scanView: CameraScanView
		get() = binding.scanView

	private fun validatePermissions(): Boolean {
		if (JumioSDK.hasAllRequiredPermissions(this)) {
			return true
		}

		val mp = JumioSDK.getMissingPermissions(this)
		ActivityCompat.requestPermissions(this, mp, PERMISSION_REQUEST_CODE)

		return false
	}

	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)

		if (requestCode != PERMISSION_REQUEST_CODE ||
			grantResults.isEmpty() ||
			grantResults[0] != PackageManager.PERMISSION_GRANTED
		) {
			finish()
		}
	}

	@Suppress("DEPRECATION")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setFinishOnTouchOutside(false)

		binding = ActivityCustomuiBinding.inflate(layoutInflater)
		setContentView(binding.root)

		sdk = JumioSDK(this).apply {
			token = intent.getStringExtra(EXTRA_TOKEN) as String
			dataCenter = intent.getSerializableExtra(EXTRA_DATACENTER) as JumioDataCenter
			intent.getIntExtra(EXTRA_CUSTOMTHEME, 0).let {
				if (it != 0) {
					customThemeId = it
				}
			}
		}

		successDrawable = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.jumio_success))
		errorDrawable = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.jumio_error))

		if (savedInstanceState == null) {
			jumioController = sdk.start(applicationContext, this)
			showView(binding.loadingIndicator, binding.controllerControls, hideLoading = false)
		} else {
			restoreJumioSdk(savedInstanceState)
		}

		initControllerUi()
		initDocumentSelectionUi()
		initCredentialUi()
		initScanPartUi()

		binding.extraction.setOnCheckedChangeListener { _, isChecked ->
			scanView.extraction = isChecked
		}

		binding.switchCamera.setOnClickListener {
			scanView.switchCamera()
		}

		binding.takePicture.setOnClickListener {
			scanView.takePicture()
		}

		binding.toggleFlash.setOnClickListener {
			scanView.flash = !scanView.flash
		}

		binding.startFallback.setOnClickListener {
			scanPart?.fallback()
		}

		binding.retryScan.setOnClickListener {
			hideViewsAfter(binding.inlineScanLayout)
			confirmationHandler.retake()
		}

		binding.confirmScan.setOnClickListener {
			hideViewsAfter(binding.inlineScanLayout)
			confirmationHandler.confirm()
		}

		binding.rejectScan.setOnClickListener {
			hideViewsAfter(binding.inlineScanLayout)
			rejectHandler.retake()
		}

		binding.partRetryButton.setOnClickListener { view ->
			hideView(view)

			retryReason?.let {
				scanPart?.retry(it)
			}
		}

		binding.errorRetryButton.setOnClickListener { _ ->
			hideView(binding.errorRetryButton, showLoading = true)

			error?.let {
				jumioController.retry(it)
			}
		}

		binding.userConsentedButton.setOnClickListener {
			consentItems?.filter { it.type == JumioConsentType.PASSIVE }?.forEach {
				setUserConsent(it, true)
			}
			if (userConsentedAll()) {
				hideViewsAfter(binding.credentialLayout)
				log("User consented to all consent items")
			} else {
				log("User consent is missing")
			}
		}
		validatePermissions()
	}

	@SuppressLint("MissingSuperCall")
	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)

		intent.data?.let { deepLink ->
			val activeScanPart = scanPart ?: return
			JumioDeepLinkHandler.consumeForScanPart(deepLink, activeScanPart)
		}
	}

	private fun setUserConsent(consentItem: JumioConsentItem, decision: Boolean) {
		jumioController.userConsented(consentItem, decision)
	}

	private fun userConsentedAll(): Boolean {
		val unconsentedItems = jumioController.getUnconsentedItems()
		return unconsentedItems.isEmpty()
	}

	private fun initConsentUi() {
		val consentItems: List<JumioConsentItem> = consentItems ?: return

		val recyclerView = findViewById<RecyclerView>(R.id.userConsentList)
		customConsentAdapter = CustomConsentAdapter(consentItems, this::setUserConsent)
		recyclerView.adapter = customConsentAdapter
		recyclerView.layoutManager = LinearLayoutManager(this)
		showView(binding.userConsentLayout, binding.userConsentList)
	}

	private fun initScanPartUi() {
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
	}

	private fun initCredentialUi() {
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
	}

	private fun initDocumentSelectionUi() {
		binding.btnSetCountryAndDocumentType.setOnClickListener {
			if (credential is JumioIDCredential) {
				val country = customCountryAdapter?.getItem(
					binding.customCountrySpinner.selectedItemPosition
				) ?: throw IllegalStateException("Country not available!")

				val jumioDocument = customDocumentAdapter?.getDocument(
					binding.customDocumentSpinner.selectedItemPosition
				) ?: throw IllegalStateException("Document not available!")

				(credential as JumioIDCredential).let {
					it.setConfiguration(country, jumioDocument)
					setupCredentialParts(it.credentialParts)
				}
			}
		}

		binding.btnSetAcquireMode.setOnClickListener {
			if (credential is JumioDocumentCredential) {
				try {
					val acquireMode = when (binding.acquireModeGroup.checkedRadioButtonId) {
						R.id.acquireModeCamera -> JumioAcquireMode.CAMERA
						R.id.acquireModeFile -> JumioAcquireMode.FILE
						else -> throw Exception("AcquireMode not supported")
					}

					(credential as JumioDocumentCredential).setConfiguration(acquireMode)

					setupCredentialParts(credential!!.credentialParts)
				} catch (e: Exception) {
					showError(e.message)
				}
			}
		}
	}

	private fun initControllerUi() {
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
	}

	private fun restoreJumioSdk(savedInstanceState: Bundle) {
		sdk.restore(
			applicationContext,
			savedInstanceState,
			this,
			this
		) { controller, credentials, activeCredential, activeScanPart ->
			jumioController = controller
			credential = activeCredential
			scanPart = activeScanPart

			hideView(binding.loadingIndicator)
			onInitialized(credentials, null)
			credential?.let {
				val country = savedInstanceState.getString("selectedCountry")
				val document = savedInstanceState.getString("selectedDocument")
				setupCredential(country, document)

				restoreIconState(binding.credentialLayout, savedInstanceState, "credentials")

				scanPart?.let {
					binding.digitalIdentityView.restoreState(savedInstanceState)
					setupScanPart()
					restoreIconState(binding.scanSideLayout, savedInstanceState, "scansides")
				}
			}
		}
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)

		outState.putString("selectedCountry", binding.customCountrySpinner.selectedItem as? String)
		outState.putString("selectedDocument", binding.customDocumentSpinner.selectedItem as? String)

		// save views:
		saveIconState(binding.credentialLayout, outState, "credentials")
		saveIconState(binding.scanSideLayout, outState, "scansides")
		binding.digitalIdentityView.saveState(outState)

		if (this::jumioController.isInitialized && !jumioController.isComplete) {
			jumioController.persist(outState)
		}
	}

	/**
	 * In case the activity is destroyed and the workflow has not been finished already (indicated by [JumioController.isComplete]),
	 * make sure to call [JumioController.stop]
	 */
	override fun onDestroy() {
		if (this::jumioController.isInitialized && !jumioController.isComplete) {
			jumioController.stop()
		}

		try {
			// Cancel (and destroy) any active scan part
			scanPart?.cancel()
		} catch (e: SDKNotConfiguredException) {
			Log.w(TAG, e.message)
		}

		super.onDestroy()
	}

	/**
	 * Handle back button presses accordingly - finish the SDK when it is complete, otherwise just cancel it
	 */
	@Suppress("DEPRECATION")
	@Deprecated("Deprecated in Java")
	override fun onBackPressed() {
		if (this::jumioController.isInitialized) {
			if (jumioController.isComplete) {
				jumioController.finish()
			} else {
				jumioController.cancel()
			}
		} else {
			super.onBackPressed()
		}
	}

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

	private fun restoreIconState(group: ViewGroup, inState: Bundle, prefix: String) {
		for (i in 0..group.childCount) {
			val child: View? = group.getChildAt(i)
			val selected = inState.getString(prefix)
			(child as Button?)?.let {
				if (it.text == selected) {
					it.tag = true
				}
				it.setCompoundDrawables(
					if (inState.getBoolean("$prefix ${it.text}")) {
						successDrawable
					} else {
						errorDrawable
					},
					null,
					null,
					null
				)
			}
		}
	}

	// /////////////////////////////////////////////////////////////
	// //////////////// JumioControllerInterface
	// /////////////////////////////////////////////////////////////
	/**
	 * Called as soon as the sdk is preloaded and credentials can be started.
	 * If the list of consentItems is not null, make sure to display user consent information,
	 * give the user the opportunity to consent if necessary, and then
	 * call [JumioController.userConsented] with a boolean representing the user decision.
	 *
	 * @param credentials
	 * @param consentItems
	 */
	@SuppressLint("SetTextI18n")
	override fun onInitialized(credentials: List<JumioCredentialInfo>, consentItems: List<JumioConsentItem>?) {
		this.consentItems = consentItems
		consentItems?.let {
			initConsentUi()
			log("User consent required")
		}

		binding.credentialLayout.removeAllViews()
		credentials.forEach { credentialInfo ->
			val button = Button(this)
			button.text = credentialInfo.category.name + " " + credentialInfo.id.subSequence(0, 5)
			button.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null)
			button.setOnClickListener { view ->
				try {
					credential = jumioController.start(credentialInfo)
					view.tag = true
				} catch (e: IllegalArgumentException) {
					showError(e.message)
					return@setOnClickListener
				}
				setupCredential()
			}
			binding.credentialLayout.addView(button)
		}
		showView(binding.credentialLayout)
	}

	override fun onError(error: JumioError) {
		if (error.isRetryable) {
			showView(binding.errorRetryButton)
		} else {
			hideView(binding.errorRetryButton)
		}

		hideView(binding.loadingIndicator, binding.inlineDiLayout)

		log(
			String.format(
				"onError: %s, %s, %s",
				error.code,
				error.message,
				"retry-able: ${if (error.isRetryable) "true" else "false"}"
			),
			Color.RED
		)

		this.error = error
	}

	/**
	 * The workflow is now finished and the result can be consumed
	 * @param result
	 */
	override fun onFinished(result: JumioResult) {
		log("onFinished")
		val data = Intent()
		data.putExtra(JumioActivity.EXTRA_RESULT, result)
		setResult(Activity.RESULT_OK, data)
		finish()
	}

	// /////////////////////////////////////////////////////////////
	// //////////////// JumioScanPartInterface
	// /////////////////////////////////////////////////////////////
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
				binding.toggleFlash.isEnabled = scanView.hasFlash
				binding.switchCamera.isEnabled = scanView.hasMultipleCameras
				binding.takePicture.isEnabled = scanView.isShutterEnabled
			}
			JumioScanUpdate.FALLBACK -> {
				log(
					"Fallback initiated due to: ${(data as? JumioFallbackReason)?.toString()}. Current scanMode: ${scanPart?.scanMode}"
				) // ktlint-disable max-line-length
				binding.startFallback.isEnabled = scanPart?.hasFallback == true
				binding.takePicture.isEnabled = scanView.isShutterEnabled
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
			JumioScanUpdate.CENTER_ID -> {
				log("Center your ID")
			}
			JumioScanUpdate.CENTER_FACE -> {
				log("Center your face")
			}
			JumioScanUpdate.LEVEL_EYES_AND_DEVICE -> {
				log("Hold your device at eye level")
			}
			JumioScanUpdate.HOLD_STILL -> {
				log("Hold still...")
			}
			JumioScanUpdate.HOLD_STRAIGHT -> {
				log("Hold straight")
			}
			JumioScanUpdate.MOVE_CLOSER -> {
				log("Move closer")
			}
			JumioScanUpdate.TOO_CLOSE -> {
				log("Too close")
			}
			JumioScanUpdate.MOVE_FACE_CLOSER -> {
				log("Move face closer")
			}
			JumioScanUpdate.FACE_TOO_CLOSE -> {
				log("Face too close")
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
		var logText = "onScanStep: ${jumioScanStep.name}"

		when (jumioScanStep) {
			JumioScanStep.PREPARE -> {
				showView(binding.loadingIndicator)
			}
			JumioScanStep.STARTED -> {
				if (data is JumioCredentialPart) {
					logText += ": $data\nExtraction Method: ${scanPart?.scanMode}"
				}
				hideView(binding.loadingIndicator)
			}
			JumioScanStep.SCAN_VIEW -> {
				val activeScanPart = scanPart ?: return

				binding.toggleFlash.isEnabled = false
				binding.switchCamera.isEnabled = false
				binding.takePicture.isEnabled = false

				showView(binding.inlineScanLayout)

				scanView.invalidate()
				scanView.requestLayout()
				scanView.attach(activeScanPart)
				lifecycle.addObserver(scanView)

				binding.startFallback.isEnabled = scanPart?.hasFallback == true
			}
			JumioScanStep.IMAGE_TAKEN -> {
				// Nothing to do in the custom ui implementation
			}
			JumioScanStep.NEXT_PART -> {
				if (data is JumioCredentialPart) {
					logText += ": $data\nExtraction Method: ${scanPart?.scanMode}"
				}
				binding.startFallback.isEnabled = scanPart?.hasFallback == true
				binding.takePicture.isEnabled = scanView.isShutterEnabled
			}
			JumioScanStep.PROCESSING -> {
				hideView(binding.inlineScanLayout, showLoading = true)
			}
			JumioScanStep.CONFIRMATION_VIEW -> {
				hideView(binding.inlineScanLayout)
				showView(binding.inlineConfirmLayout)
				scanPart?.let { jumioScanPart ->
					binding.confirmationViewList.removeAllViews()
					confirmationHandler.attach(jumioScanPart)
					confirmationHandler.parts.forEach { jumioCredentialPart ->
						val confirmationView = JumioConfirmationView(this@CustomUiActivity)
						confirmationHandler.renderPart(jumioCredentialPart, confirmationView)
						binding.confirmationViewList.addView(confirmationView)
					}
				}
			}
			JumioScanStep.REJECT_VIEW -> {
				val rejectMap = data as? Map<JumioCredentialPart, String>
				rejectMap?.forEach {
					logText += ": ${it.key.name} -> ${it.value}"
				}
				/**
				 * To display granular feedback, check against the values provided in [com.jumio.sdk.reject.JumioRejectReason]
				 */
// 				when(data) {
// 					JumioRejectReason.BLURRY -> ...
// 					JumioRejectReason.DIGITAL_COPY -> ...
// 					...
// 				}
				showView(binding.inlineRejectLayout)
				scanPart?.let { jumioScanPart ->
					binding.rejectViewList.removeAllViews()
					rejectHandler.attach(jumioScanPart)
					rejectHandler.parts.forEach { jumioCredentialPart ->
						val rejectView = JumioRejectView(this@CustomUiActivity)
						rejectHandler.renderPart(jumioCredentialPart, rejectView)
						binding.rejectViewList.addView(rejectView)
					}
				}
			}
			JumioScanStep.RETRY -> {
				if (data is JumioRetryReason) {
					logText += ", Reason: ${data.code}"
					retryReason = data
				}
				showView(binding.partRetryButton)
			}
			JumioScanStep.CAN_FINISH -> {
				lifecycle.removeObserver(scanView)
				hideView(
					binding.inlineConfirmLayout,
					binding.partRetryButton,
					binding.loadingIndicator,
					binding.inlineDiLayout
				)
			}
			JumioScanStep.ATTACH_ACTIVITY -> {
				scanPart?.let { JumioActivityAttacher(this).attach(it) }
			}
			JumioScanStep.ATTACH_FILE -> {
				val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
					addCategory(Intent.CATEGORY_OPENABLE)
					type = "*/*"
					putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf"))
				}
				try {
					launcher.launch(intent)
				} catch (e: Exception) {
					e.printStackTrace()
					log("Could not start file picker", Color.RED)
				}
			}
			JumioScanStep.ADDON_SCAN_PART -> {
				showView(binding.addonControls)
			}
			JumioScanStep.DIGITAL_IDENTITY_VIEW -> {
				val activeScanPart = scanPart ?: return

				showView(binding.inlineDiLayout)
				binding.digitalIdentityView.attach(activeScanPart)
			}
			JumioScanStep.THIRD_PARTY_VERIFICATION -> {
				hideView(binding.inlineDiLayout, showLoading = true)
			}
		}

		log(logText)
	}

	private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		if (result.resultCode == RESULT_OK) {
			val fileAttacher = JumioFileAttacher()
			try {
				scanPart?.let { fileAttacher.attach(it) }
				result.data?.let {
					val returnUri = it.data ?: throw Exception("Could not get Uri")
					val fileDescriptor = contentResolver.openFileDescriptor(returnUri, "r") ?: throw Exception(
						"Could not open file descriptor"
					)

					fileAttacher.setFileDescriptor(fileDescriptor)
				}
			} catch (e: Exception) {
				showError(e.message)
			}
		}
	}

	// /////////////////////////////////////////////////////////////
	// //////////////// Helper Functions
	// /////////////////////////////////////////////////////////////
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
				(credential as? JumioIDCredential)?.let { credential ->
					val selectedCountry = binding.customCountrySpinner.selectedItem as String
					val documents = credential.getPhysicalDocumentsForCountry(selectedCountry) +
						credential.getDigitalDocumentsForCountry(selectedCountry)

					customDocumentAdapter = CustomDocumentAdapter(this@CustomUiActivity, documents)
					binding.customDocumentSpinner.adapter = customDocumentAdapter
				}
			}
			binding.customDocumentSpinner -> {
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
	 */
	private fun setupCredential(country: String? = null, document: String? = null) {
		showView(binding.credentialControls)
		when (credential) {
			is JumioIDCredential -> {
				(credential as JumioIDCredential).also { idCredential ->
					showView(binding.countryDocumentLayout) // setup country/doctype/variant spinner
					binding.customCountrySpinner.onItemSelectedListener = null
					binding.customDocumentSpinner.onItemSelectedListener = null

					customCountryAdapter = CustomCountryAdapter(this, idCredential.supportedCountries)
					binding.customCountrySpinner.adapter = customCountryAdapter
					binding.customCountrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
						override fun onItemSelected(parent: AdapterView<*>?, view: View?, bposition: Int, id: Long) {
							binding.customCountrySpinner.onItemSelectedListener = this@CustomUiActivity
						}

						override fun onNothingSelected(parent: AdapterView<*>?) {
							// Left intentionally blank, nothing to do.
						}
					}

					log("Suggested Country: ${idCredential.suggestedCountry}")
					setSpinner(binding.customCountrySpinner, country ?: idCredential.suggestedCountry)

					val selectedCountry = binding.customCountrySpinner.selectedItem as String
					val documents = idCredential.getPhysicalDocumentsForCountry(selectedCountry) +
						idCredential.getDigitalDocumentsForCountry(selectedCountry)

					customDocumentAdapter = CustomDocumentAdapter(this@CustomUiActivity, documents)
					binding.customDocumentSpinner.adapter = customDocumentAdapter
					binding.customDocumentSpinner.onItemSelectedListener =
						object : AdapterView.OnItemSelectedListener {
							override fun onItemSelected(
								parent: AdapterView<*>?,
								view: View?,
								bposition: Int,
								id: Long
							) {
								binding.customDocumentSpinner.onItemSelectedListener = this@CustomUiActivity
							}

							override fun onNothingSelected(parent: AdapterView<*>?) {
								// Left intentionally blank, nothing to do.
							}
						}

					setSpinner(binding.customDocumentSpinner, document)

					if (idCredential.isConfigured) {
						setupCredentialParts(idCredential.credentialParts)
					}
				}
			}
			is JumioDocumentCredential -> {
				(credential as JumioDocumentCredential).let {
					binding.acquireModeCamera.visibility = if (
						it.availableAcquireModes.contains(JumioAcquireMode.CAMERA)
					) {
						View.VISIBLE
					} else {
						View.GONE
					}
					binding.acquireModeFile.visibility = if (it.availableAcquireModes.contains(JumioAcquireMode.FILE)) {
						View.VISIBLE
					} else {
						View.GONE
					}

					showView(binding.acquireModeLayout)

					if (it.isConfigured) {
						setupCredentialParts(it.credentialParts)
					}
				}
			}
			is JumioFaceCredential, is JumioDataCredential -> {
				hideViewsAfter(binding.credentialControls)

				credential?.let {
					setupCredentialParts(it.credentialParts)
				}
			}
		}
	}

	/**
	 * Setup the scan part in the gui
	 */
	private fun setupScanPart() {
		showView(binding.scanPartControls)

		val activeScanPart = scanPart ?: return

		if (activeScanPart.scanMode == JumioScanMode.WEB) {
			hideView(binding.inlineScanLayout)
			showView(binding.digitalIdentityView)
		} else if (activeScanPart.scanMode != JumioScanMode.FACE_IPROOV &&
			activeScanPart.scanMode != JumioScanMode.DEVICE_RISK
		) {
			hideView(binding.digitalIdentityView)
			initScanView()
		}
	}

	private val decimalFormat: DecimalFormat = DecimalFormat("0.00")

	/**
	 * Initialize the scan view with the min size and set the ratio seekbar to sane values
	 */
	private fun initScanView() {
		showView(binding.scanView)

		val screenRatio = binding.rootScrollView.width.toFloat() / binding.rootScrollView.height
		val isPortrait = binding.rootScrollView.height > binding.rootScrollView.width || isTabletDevice()

		val params = FrameLayout.LayoutParams(
			if (isPortrait) FrameLayout.LayoutParams.MATCH_PARENT else FrameLayout.LayoutParams.WRAP_CONTENT,
			if (isPortrait) FrameLayout.LayoutParams.WRAP_CONTENT else ScreenUtil.dpToPx(this, 300)
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
		binding.scanView.ratio = calculateScanViewRatio(isPortrait)
		binding.ratioTextView.text = "Ratio: " + decimalFormat.format(binding.scanView.ratio)
		binding.ratioTextView.setBackgroundColor(ContextCompat.getColor(this, R.color.spinnerBackground))

		binding.ratioSeekBar.setOnSeekBarChangeListener(
			object : SeekBar.OnSeekBarChangeListener {
				override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
					// Left intentionally blank, nothing to do.
				}

				override fun onStartTrackingTouch(seekBar: SeekBar) {
					// Left intentionally blank, nothing to do.
				}

				override fun onStopTrackingTouch(seekBar: SeekBar) {
					binding.scanView.ratio = calculateScanViewRatio(isPortrait)

					binding.ratioTextView.text = "Ratio: " + decimalFormat.format(binding.scanView.ratio)

					binding.scanView.invalidate()
					binding.scanView.requestLayout()
				}
			}
		)

		binding.ratioSeekBar.progress = oldProgress
	}

	/**
	 * Checks if the current device is a tablet device, based on the smallest screen width
	 *
	 * @return true in case it is a tablet
	 */
	private fun isTabletDevice(): Boolean = resources.configuration.smallestScreenWidthDp >= 600

	/**
	 * Calculates the scan view ratio based on the minRatio and the current progress
	 *
	 * @param isPortrait if the screen is in portrait orientation or not
	 * @return scan view ratio
	 */
	private fun calculateScanViewRatio(isPortrait: Boolean): Float {
		return if (isPortrait) {
			binding.scanView.minRatio - binding.ratioSeekBar.progress.toFloat() / 100
		} else {
			binding.scanView.minRatio + binding.ratioSeekBar.progress.toFloat() / 100
		}
	}

	/**
	 * Setup the credential parts in the gui
	 *
	 * @param credentialParts
	 */
	private fun setupCredentialParts(credentialParts: List<JumioCredentialPart>) {
		binding.scanSideLayout.removeAllViews()

		credentialParts.forEach { part ->
			val button = Button(this).apply {
				text = part.name
				setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null)

				setOnClickListener { view ->
					try {
						scanPart = credential?.initScanPart(part, this@CustomUiActivity)
						view.tag = true
						setupScanPart()
					} catch (e: Exception) {
						showError(e.message)
					}
				}
			}

			binding.scanSideLayout.addView(button)
		}

		showView(binding.scanSideLayout)
	}

	private fun showView(vararg views: View, hideLoading: Boolean = true) {
		if (hideLoading) binding.loadingIndicator.visibility = View.GONE
		for (view in views) view.visibility = View.VISIBLE
	}

	private fun hideView(vararg views: View, showLoading: Boolean = false) {
		for (view in views) view.visibility = View.GONE
		if (showLoading) binding.loadingIndicator.visibility = View.VISIBLE
	}

	private fun hideViewsAfter(lastVisible: View) {
		val views = arrayOf(
			binding.controllerControls,
			binding.credentialLayout,
			binding.credentialControls,
			binding.countryDocumentLayout,
			binding.acquireModeLayout,
			binding.scanSideLayout,
			binding.topMarginSeekBarLayout,
			binding.userConsentLayout,
			binding.scanPartControls,
			binding.seekBarLayout,
			binding.inlineScanLayout,
			binding.inlineDiLayout,
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
	 * Display the error [message] as snackbar
	 *
	 * @param message
	 */
	private fun showError(message: String?) {
		Snackbar.make(binding.root, message ?: "Unknown error", Snackbar.LENGTH_LONG).show()
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
		Log.d(TAG, message)
		try {
			val logline = TextView(this).apply {
				text = message
				setTextColor(color)
			}

			binding.inlineCallbackLog.addView(logline, 0)

			if (binding.inlineCallbackLog.childCount > 40) {
				binding.inlineCallbackLog.removeViewAt(binding.inlineCallbackLog.childCount - 1)
			}
		} catch (e: Exception) {
			Log.e(TAG, String.format("Could not write to callback log: %s", e.message))
			Log.e(TAG, message)
		}
	}

	private fun catchAndShow(function: () -> Any) {
		try {
			function()
		} catch (e: Exception) {
			e.printStackTrace()
			showError(e.message)
		}
	}

	private fun updateIcon(group: ViewGroup, success: Boolean) {
		for (i in 0..group.childCount) {
			val child: View? = group.getChildAt(i)
			if (child?.tag == true) {
				(child as Button).setCompoundDrawablesWithIntrinsicBounds(
					if (success) successDrawable else errorDrawable,
					null,
					null,
					null
				)
				child.tag = false
				break
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
			} else if (adapter is JumioArrayAdapter<*>) {
				adapter.getValue(i)
			} else {
				adapter.getItem(i) as String
			}

			if (format == value) {
				spinner?.setSelection(i, false)
				break
			}
		}
	}

	companion object {
		const val PERMISSION_REQUEST_CODE = 100
		const val EXTRA_TOKEN = "token"
		const val EXTRA_DATACENTER = "datacenter"
		const val EXTRA_CUSTOMTHEME = "customtheme"

		@JvmStatic
		fun start(
			activity: Activity,
			activityResultLauncher: ActivityResultLauncher<Intent>,
			token: String,
			dataCenter: JumioDataCenter,
			customTheme: Int = 0
		) {
			require(token.isNotEmpty()) { "Token needs to be set" }

			val intent = Intent(activity, CustomUiActivity::class.java).apply {
				putExtra(EXTRA_TOKEN, token)
				putExtra(EXTRA_DATACENTER, dataCenter)
				putExtra(EXTRA_CUSTOMTHEME, customTheme)
			}

			activityResultLauncher.launch(intent)
		}
	}
}
