// Copyright 2023 Jumio Corporation, all rights reserved.
package com.jumio.sample.xml

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
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.jumio.commons.log.Log
import com.jumio.commons.utils.dpToPx
import com.jumio.defaultui.JumioActivity
import com.jumio.sample.R
import com.jumio.sample.databinding.ActivityCustomuiBinding
import com.jumio.sample.xml.adapter.CustomConsentAdapter
import com.jumio.sample.xml.adapter.CustomCountryAdapter
import com.jumio.sample.xml.adapter.CustomDocumentAdapter
import com.jumio.sdk.JumioSDK
import com.jumio.sdk.consent.JumioConsentItem
import com.jumio.sdk.controller.JumioController
import com.jumio.sdk.credentials.JumioCredential
import com.jumio.sdk.credentials.JumioCredentialInfo
import com.jumio.sdk.credentials.JumioDocumentCredential
import com.jumio.sdk.credentials.JumioFaceCredential
import com.jumio.sdk.credentials.JumioIDCredential
import com.jumio.sdk.data.JumioTiltState
import com.jumio.sdk.document.JumioDocumentInfo
import com.jumio.sdk.enums.JumioAcquireMode
import com.jumio.sdk.enums.JumioCameraFacing
import com.jumio.sdk.enums.JumioConsentType
import com.jumio.sdk.enums.JumioCredentialPart
import com.jumio.sdk.enums.JumioDataCenter
import com.jumio.sdk.enums.JumioFallbackReason
import com.jumio.sdk.enums.JumioFlashState
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
import com.jumio.sdk.views.JumioScanView
import java.text.DecimalFormat

private const val TAG = "CustomUiActivity"
private const val PERMISSION_REQUEST_CODE = 100
private const val EXTRA_TOKEN = "token"
private const val EXTRA_DATACENTER = "datacenter"
private const val EXTRA_CUSTOMTHEME = "customtheme"

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
	private lateinit var backPressCallback: OnBackPressedCallback

	private var consentItems: List<JumioConsentItem> = emptyList()
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

	private val scanView: JumioScanView
		get() = binding.scanView

	private var cameraFacing: JumioCameraFacing? = null

	private fun validatePermissions(): Boolean {
		if (JumioSDK.hasAllRequiredPermissions(this)) {
			return true
		}

		val mp = JumioSDK.getMissingPermissions(this)
		ActivityCompat.requestPermissions(this, mp, PERMISSION_REQUEST_CODE)

		return false
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)

		if (requestCode != PERMISSION_REQUEST_CODE ||
			grantResults.isEmpty() ||
			grantResults[0] != PackageManager.PERMISSION_GRANTED
		) {
			finish()
		}
	}

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
			showView(binding.loadingIndicator, binding.controllerControls, hideLoading = false)
			jumioController = sdk.start(applicationContext, this)
		} else {
			restoreJumioSdk(savedInstanceState)
		}

		initControllerUi()
		initDocumentSelectionUi()
		initCredentialUi()
		initScanPartUi()
		initBackPressDispatcher()

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

			catchAndShow {
				error?.let {
					jumioController.retry(it)
				}
			}
		}

		binding.userConsentedButton.setOnClickListener {
			consentItems.forEach {
				if (it.type == JumioConsentType.ACTIVE) {
					val consent = customConsentAdapter?.getConsentForItem(it)
					if (consent != null) {
						jumioController.userConsented(it, consent)
					}
				} else if (it.type == JumioConsentType.PASSIVE) {
					jumioController.userConsented(it, true)
				}
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

	private fun userConsentedAll(): Boolean {
		val unconsentedItems = jumioController.getUnconsentedItems()
		return unconsentedItems.isEmpty()
	}

	private fun initConsentUi() {
		val recyclerView = findViewById<RecyclerView>(R.id.userConsentList)
		customConsentAdapter = CustomConsentAdapter(consentItems)
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
			catchAndShow {
				credential?.cancel()
				updateIcon(binding.credentialLayout, credential?.isComplete == true)
				credential = null
				hideView(binding.credentialControls)
				hideViewsAfter(binding.credentialControls)
			}
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
			catchAndShow {
				jumioController.cancel()
				hideViewsAfter(binding.controllerControls)
				showView(binding.loadingIndicator)
			}
		}

		binding.controllerFinish.setOnClickListener {
			catchAndShow {
				jumioController.finish()
				hideViewsAfter(binding.controllerControls)
				showView(binding.loadingIndicator)
			}
		}
	}

	/**
	 * Handle back button accordingly - finish the SDK when it is complete, otherwise just cancel it
	 */
	private fun initBackPressDispatcher() {
		if (::backPressCallback.isInitialized) {
			return
		}
		backPressCallback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				if (::jumioController.isInitialized) {
					if (jumioController.isComplete) {
						jumioController.finish()
					} else {
						catchAndShow {
							jumioController.cancel()
						}
					}
				} else {
					onBackPressedDispatcher.onBackPressed()
				}
			}
		}
		onBackPressedDispatcher.addCallback(this, backPressCallback)
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
			onInitialized(credentials, jumioController.getUnconsentedItems())
			credential?.let {
				val country = savedInstanceState.getString("selectedCountry")
				val document = savedInstanceState.getString("selectedDocument")
				val cameraFacing = savedInstanceState.getString("cameraFacing")
				if (cameraFacing == JumioCameraFacing.FRONT.name) {
					this.cameraFacing = JumioCameraFacing.FRONT
				}
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
		if (scanView.hasMultipleCameras && scanView.cameraFacing == JumioCameraFacing.FRONT) {
			outState.putString("cameraFacing", scanView.cameraFacing.name)
		}

		// save views:
		saveIconState(binding.credentialLayout, outState, "credentials")
		saveIconState(binding.scanSideLayout, outState, "scansides")
		binding.digitalIdentityView.saveState(outState)

		if (this::jumioController.isInitialized) {
			jumioController.persist(outState)
		}
	}

	/**
	 * Functions [JumioController.persist] and [JumioController.stop] need to be called independently from
	 * [JumioController.isComplete] as long as the workflow has not yet been finished or canceled.
	 */
	override fun onDestroy() {
		if (this::jumioController.isInitialized) {
			jumioController.stop()
		}

		if (::backPressCallback.isInitialized) {
			backPressCallback.remove()
		}

		try {
			// Cancel (and destroy) any active scan part
			scanPart?.cancel()
		} catch (e: SDKNotConfiguredException) {
			Log.w(TAG, e)
		}

		super.onDestroy()
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

	private fun restoreCameraFacing() {
		if (cameraFacing == JumioCameraFacing.FRONT) {
			scanView.cameraFacing = cameraFacing as JumioCameraFacing
			cameraFacing = null
		}
	}

	// /////////////////////////////////////////////////////////////
	// //////////////// JumioControllerInterface
	// /////////////////////////////////////////////////////////////
	/**
	 * Called as soon as the sdk is preloaded and credentials can be started.
	 * If the list of consentItems is not null, make sure to display user consent information,
	 * give the user the opportunity to consent if necessary, and then
	 * call [JumioController.userConsented] if the user has consented.
	 *
	 * @param credentials
	 * @param consentItems
	 */
	@SuppressLint("SetTextI18n")
	override fun onInitialized(credentials: List<JumioCredentialInfo>, consentItems: List<JumioConsentItem>?) {
		this.consentItems = consentItems ?: emptyList()
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
	 * The workflow is now finished and the [result] can be consumed
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
		Log.v(TAG, "ScanUpdate $jumioScanUpdate received with data: $data")

		when (jumioScanUpdate) {
			JumioScanUpdate.CAMERA_AVAILABLE -> {
				log("CAMERA_AVAILABLE")
				binding.toggleFlash.isEnabled = scanView.hasFlash
				binding.switchCamera.isEnabled = scanView.hasMultipleCameras
				binding.takePicture.isEnabled = scanView.isShutterEnabled
			}
			JumioScanUpdate.FALLBACK -> {
				log(
					"Fallback initiated due to: ${(data as? JumioFallbackReason)?.toString()}." +
						"Current scanMode: ${scanPart?.scanMode}"
				)
				binding.startFallback.isEnabled = scanPart?.hasFallback == true
				binding.takePicture.isEnabled = scanView.isShutterEnabled
			}
			JumioScanUpdate.NFC_EXTRACTION_STARTED -> log("NFC Extraction started")
			JumioScanUpdate.NFC_EXTRACTION_PROGRESS -> log("NFC Extraction progress $data")
			JumioScanUpdate.NFC_EXTRACTION_FINISHED -> log("NFC Extraction finished")
			JumioScanUpdate.CENTER_ID -> log("Center your ID")
			JumioScanUpdate.HOLD_STILL -> log("Hold still...")
			JumioScanUpdate.HOLD_STRAIGHT -> log("Hold straight")
			JumioScanUpdate.MOVE_CLOSER -> log("Move closer")
			JumioScanUpdate.TOO_CLOSE -> log("Too close")
			JumioScanUpdate.MOVE_FACE_INTO_FRAME -> log("Put your face in the frame")
			JumioScanUpdate.CENTER_FACE -> log("Center your face")
			JumioScanUpdate.LEVEL_EYES_AND_DEVICE -> log("Hold your device at eye level")
			JumioScanUpdate.TILT_FACE_UP -> log("Tilt your face up")
			JumioScanUpdate.TILT_FACE_DOWN -> log("Tilt your face down")
			JumioScanUpdate.TILT_FACE_LEFT -> log("Tilt your face left")
			JumioScanUpdate.TILT_FACE_RIGHT -> log("Tilt your face right")
			JumioScanUpdate.MOVE_FACE_CLOSER -> log("Move face closer")
			JumioScanUpdate.FACE_TOO_CLOSE -> log("Face too close")
			JumioScanUpdate.NEXT_POSITION -> log("Move face to next position")
			JumioScanUpdate.FLASH -> log("Flash state changing to ${data as JumioFlashState}")
			/**
			 * Whenever [JumioScanUpdate.TILT] is received, this means users must tilt their document vertically to a
			 * certain angle. The current and target angles are defined in [JumioTiltState], received via the [data]
			 * parameter of this function. A negative angle indicates that the document must be tilted in the
			 * opposite direction.
			 */
			JumioScanUpdate.TILT -> log("Tilt your document, ${data as JumioTiltState}")
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
				restoreCameraFacing()
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

					// To display granular feedback, check against the values provided in [com.jumio.sdk.reject.JumioRejectReason]
					//
					// when(it.value) {
					//   JumioRejectReason.BLURRY -> ...
					//   JumioRejectReason.DIGITAL_COPY -> ...
					//   ...
					// }
				}
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
				if (data is JumioDocumentInfo) {
					logText += ": scanned document $data"
				}
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
					contentResolver.openFileDescriptor(returnUri, "r")?.use { fileDescriptor ->
						fileAttacher.setFileDescriptor(fileDescriptor)
					} ?: throw Exception("Could not open file descriptor")
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
							override fun onItemSelected(parent: AdapterView<*>?, view: View?, bposition: Int, id: Long) {
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
			is JumioFaceCredential -> {
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
		} else if (activeScanPart.scanMode != JumioScanMode.FACE_IPROOV) {
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
			if (isPortrait) FrameLayout.LayoutParams.WRAP_CONTENT else 300.dpToPx(this)
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
		binding.ratioTextView.text = "Ratio: ${decimalFormat.format(binding.scanView.ratio)}"
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

					binding.ratioTextView.text = "Ratio: ${decimalFormat.format(binding.scanView.ratio)}"

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

	private fun catchAndShow(function: () -> Unit) {
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
		@JvmStatic
		fun start(
			activity: Activity,
			activityResultLauncher: ActivityResultLauncher<Intent>,
			token: String,
			dataCenter: JumioDataCenter,
			customTheme: Int = 0,
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
