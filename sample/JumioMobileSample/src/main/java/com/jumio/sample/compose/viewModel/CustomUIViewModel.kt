// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.viewModel

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jumio.sample.R
import com.jumio.sample.compose.views.navigation.AppNavigation
import com.jumio.sdk.JumioSDK
import com.jumio.sdk.consent.JumioConsentItem
import com.jumio.sdk.controller.JumioController
import com.jumio.sdk.credentials.JumioCredential
import com.jumio.sdk.credentials.JumioCredentialInfo
import com.jumio.sdk.credentials.JumioDocumentCredential
import com.jumio.sdk.credentials.JumioFaceCredential
import com.jumio.sdk.credentials.JumioIDCredential
import com.jumio.sdk.data.JumioTiltState
import com.jumio.sdk.document.JumioDocument
import com.jumio.sdk.enums.JumioConsentType
import com.jumio.sdk.enums.JumioCredentialPart
import com.jumio.sdk.enums.JumioDataCenter
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
import com.jumio.sdk.views.JumioFileAttacher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "CustomUIViewModel"

class CustomUIViewModel(
	val savedStateHandle: SavedStateHandle,
	application: Application,
	token: String,
	dataCenter: JumioDataCenter,
	customTheme: Int,
) : AndroidViewModel(application), JumioControllerInterface, JumioScanPartInterface {

	val consentPageLoaderState = MutableStateFlow(false)
	var navigationState = MutableStateFlow<AppNavigation?>(null)
		private set

	val confirmationHandler: JumioConfirmationHandler = JumioConfirmationHandler()
	val rejectHandler: JumioRejectHandler = JumioRejectHandler()
	val fileAttacher = JumioFileAttacher()
	private var jumioError: JumioError? = null
	private var retryReason: JumioRetryReason? = null

	private var jumioController: JumioController? = null

	var credentialInfoList: List<JumioCredentialInfo> = emptyList()
		private set
	var currentCredential: JumioCredential? = null
		private set

	private var currentCredentialPart: JumioCredentialPart? = null
	var currentScanPart: JumioScanPart? = null
	var countryList: List<String> = listOf()
		private set
	var documentList = MutableStateFlow<List<JumioDocument>>(listOf())
		private set

	var scanStepEvent = MutableStateFlow<JumioScanStep?>(null)
	var scanUpdateEvent = MutableStateFlow<Pair<JumioScanUpdate, Any?>?>(null)

	private var currentCredentialInfo: JumioCredentialInfo?
		get() = savedStateHandle["currentCredentialInfo"]
		set(value) = savedStateHandle.set("currentCredentialInfo", value)

	var selectedCountry: String
		get() = savedStateHandle["selectedCountry"] ?: ""
		private set(value) = savedStateHandle.set("selectedCountry", value)
	var consentItems: List<JumioConsentItem>
		get() = savedStateHandle["consentItems"] ?: emptyList()
		private set(value) = savedStateHandle.set("consentItems", value)
	var scanAlignmentState = MutableStateFlow("")
		private set
	var flipDocument = MutableStateFlow("")
		private set
	val workflowResult = MutableStateFlow<JumioResult?>(null)
	var isNewCredentialStarted = false

	init {
		val jumioSDKHandle = savedStateHandle.get<Bundle>("jumioSDK")
		savedStateHandle.setSavedStateProvider("jumioSDK") {
			val data = Bundle()
			jumioController?.persist(data)
			data
		}
		val jumioSDK = JumioSDK(application.applicationContext).apply {
			this.token = token
			this.dataCenter = dataCenter
			if (customTheme != 0) {
				customThemeId = customTheme
			}
		}
		if (jumioSDKHandle == null) {
			consentPageLoaderState.value = true
			jumioController = jumioSDK.start(application, this)
		} else {
			jumioSDK.restore(
				getApplication(),
				jumioSDKHandle,
				this,
				this
			) { controller, credentials, activeCredential, activeScanPart ->
				jumioController = controller
				credentialInfoList = credentials
				currentCredential = activeCredential
				currentScanPart = activeScanPart
				onInitialized(credentials, controller.getUnconsentedItems())
				setUpCredential()
			}
		}
	}

	override fun onInitialized(credentials: List<JumioCredentialInfo>, consentItems: List<JumioConsentItem>?) {
		credentialInfoList = credentials
		this.consentItems = consentItems ?: listOf()
		consentItems?.forEach { consentItem ->
			if (consentItem.type == JumioConsentType.PASSIVE) {
				jumioController?.userConsented(consentItem, true)
			}
		}
		consentPageLoaderState.value = false
	}

	override fun onError(error: JumioError) {
		Log.e(TAG, "onError ${error.message}")
		this.jumioError = error
		val errorMessage = "onError ${error.code} ${error.message} retry-able: ${if (error.isRetryable) "true" else "false"}"
		navigationState.value = AppNavigation.Error(errorMessage, error.isRetryable)
	}

	/**
	 * The workflow is now finished and the [result] can be consumed
	 */
	override fun onFinished(result: JumioResult) {
		Log.d(TAG, "onFinished")
		workflowResult.value = result
	}

	override fun onUpdate(jumioScanUpdate: JumioScanUpdate, data: Any?) {
		Log.d(TAG, "onUpdate $jumioScanUpdate $data")
		when (jumioScanUpdate) {
			JumioScanUpdate.CENTER_ID,
			JumioScanUpdate.CENTER_FACE,
			JumioScanUpdate.LEVEL_EYES_AND_DEVICE,
			JumioScanUpdate.HOLD_STRAIGHT,
			JumioScanUpdate.MOVE_CLOSER,
			JumioScanUpdate.TOO_CLOSE,
			JumioScanUpdate.HOLD_STILL,
			JumioScanUpdate.MOVE_FACE_CLOSER,
			JumioScanUpdate.FACE_TOO_CLOSE,
			JumioScanUpdate.MOVE_FACE_INTO_FRAME,
			JumioScanUpdate.TILT_FACE_UP,
			JumioScanUpdate.TILT_FACE_DOWN,
			JumioScanUpdate.TILT_FACE_LEFT,
			JumioScanUpdate.TILT_FACE_RIGHT,
			-> {
				scanAlignmentState.value = jumioScanUpdate.name
			}
			JumioScanUpdate.TILT -> {
				scanAlignmentState.value = (data as? JumioTiltState)?.let {
					"${jumioScanUpdate.name} currentAngle = ${it.currentAngle} targetAngle = ${it.targetAngle}"
				} ?: run {
					jumioScanUpdate.name
				}
			}
			else -> {
				scanAlignmentState.value = ""
			}
		}
		scanUpdateEvent.value = Pair(jumioScanUpdate, data)
	}

	override fun onScanStep(jumioScanStep: JumioScanStep, data: Any?) {
		Log.d(TAG, "onScanStep $jumioScanStep $data")
		val context = getApplication<Application>().applicationContext
		scanStepEvent.value = jumioScanStep
		when (jumioScanStep) {
			JumioScanStep.PREPARE -> {
				navigationState.value =
					AppNavigation.Loader(title = context.getString(R.string.loading))
			}

			JumioScanStep.STARTED -> {
				// Hide loader
			}

			JumioScanStep.ATTACH_ACTIVITY -> {}

			JumioScanStep.ATTACH_FILE -> {
				currentScanPart?.let { fileAttacher.attach(it) }
				navigationState.value = AppNavigation.UploadFileHelp
			}

			JumioScanStep.SCAN_VIEW -> {
				setUpScanView()
			}

			JumioScanStep.IMAGE_TAKEN -> {
				// Nothing to do in the custom ui implementation
			}

			JumioScanStep.NEXT_PART -> {
				flipDocument.value = context.getString(R.string.flip_document) + " $data"
				viewModelScope.launch(Dispatchers.Main) {
					delay(3000)
					flipDocument.value = ""
				}
			}

			JumioScanStep.PROCESSING -> {
				navigationState.value =
					AppNavigation.Loader(title = context.getString(R.string.processing))
			}

			JumioScanStep.CONFIRMATION_VIEW -> {
				navigationState.value = AppNavigation.Confirmation
			}

			JumioScanStep.REJECT_VIEW -> {
				// To display granular feedback, check against the values provided in [com.jumio.sdk.reject.JumioRejectReason]
				//
				// when(data) {
				//   JumioRejectReason.BLURRY -> ...
				//   JumioRejectReason.DIGITAL_COPY -> ...
				//   ...
				// }
				currentScanPart?.let {
					rejectHandler.attach(it)
				}
				navigationState.value = AppNavigation.Rejection
			}

			JumioScanStep.RETRY -> {
				retryReason = data as? JumioRetryReason
				val errorMessage = retryReason?.let { "${it.code}\n${it.message}" } ?: "Unknown Error"
				navigationState.value = AppNavigation.Error(
					message = errorMessage,
					isRetryable = true
				)
			}

			JumioScanStep.CAN_FINISH -> {
				finishScanPartAndContinue()
			}

			JumioScanStep.ADDON_SCAN_PART -> {
				currentScanPart = currentCredential?.getAddonPart()
				setUpScanView()
			}

			JumioScanStep.DIGITAL_IDENTITY_VIEW -> {
				setUpScanView()
			}

			JumioScanStep.THIRD_PARTY_VERIFICATION -> {
				navigationState.value =
					AppNavigation.Loader(title = context.getString(R.string.verifying))
			}
		}
	}

	override fun onCleared() {
		super.onCleared()
		if (jumioController?.isComplete == false) {
			jumioController?.stop()
		}
	}

	fun onConsentItemToggle(consentItem: JumioConsentItem, isChecked: Boolean) {
		if (consentItem.type == JumioConsentType.ACTIVE) {
			jumioController?.userConsented(consentItem, isChecked)
		}
	}

	fun onUiEvent(uiEvent: CustomUIEvent) {
		Log.d(TAG, "onUiEvent $uiEvent")
		when (uiEvent) {
			is CustomUIEvent.StartClicked -> {
				startWithFirstCredential()
			}
			is CustomUIEvent.CountrySelected -> {
				selectedCountry = uiEvent.country
				if (currentCredential is JumioIDCredential) {
					val idCredential = currentCredential as JumioIDCredential
					documentList.value = idCredential.getPhysicalDocumentsForCountry(selectedCountry) +
						idCredential.getDigitalDocumentsForCountry(selectedCountry)
				}
			}
			is CustomUIEvent.DocumentSelected -> {
				(currentCredential as? JumioIDCredential)?.let {
					it.setConfiguration(selectedCountry, uiEvent.document)
					startScanPartWith(it.credentialParts.first())
				}
			}
			is CustomUIEvent.AcquireModeClicked -> {
				(currentCredential as? JumioDocumentCredential)?.let {
					it.setConfiguration(uiEvent.mode)
					startScanPartWith(it.credentialParts.first())
				}
			}
		}
	}

	private fun startWithFirstCredential() {
		if (jumioController?.getUnconsentedItems()?.isNotEmpty() == true) {
			Log.d(TAG, "User consent is missing")
			return
		}
		Log.d(TAG, "User consented to all consent items")
		try {
			// cancel the credential here in case it is the first one..
			try {
				currentCredential?.cancel()
			} catch (e: Exception) {
				// Current credential cannot be cancelled, ignore the error
				Log.e(TAG, e.message ?: "credential cancel failed")
			}
			currentCredentialInfo = credentialInfoList.firstOrNull()
			currentCredential = currentCredentialInfo?.let { jumioController?.start(it) }
			setUpCredential()
		} catch (e: IllegalArgumentException) {
			// Current credential could not be started
			Log.e(TAG, e.message ?: "credential start failed")
		} catch (e: SDKNotConfiguredException) {
			Log.e(TAG, e.message ?: "SDKNotConfiguredException")
		}
	}

	private fun setUpCredential() {
		currentCredential?.let {
			if (it.isConfigured) {
				isNewCredentialStarted = true
				startScanPartWith(it.credentialParts.first())
				return
			}
		}

		when (currentCredential) {
			is JumioIDCredential -> {
				Log.d(TAG, "setUpCredential JumioIDCredential")
				isNewCredentialStarted = true
				val idCredential = currentCredential as JumioIDCredential
				countryList = idCredential.supportedCountries.sorted()
				selectedCountry = idCredential.suggestedCountry ?: ""
				documentList.value = idCredential.getPhysicalDocumentsForCountry(selectedCountry) +
					idCredential.getDigitalDocumentsForCountry(selectedCountry)
				navigationState.value = AppNavigation.SelectCountryAndDocument
			}
			is JumioDocumentCredential -> {
				Log.d(TAG, "setUpCredential JumioDocumentCredential")
				isNewCredentialStarted = true
				navigationState.update { AppNavigation.AcquireMode }
			}
			is JumioFaceCredential -> {
				Log.d(TAG, "setUpCredential JumioFaceCredential")
				isNewCredentialStarted = true
				currentCredential?.let {
					startScanPartWith(it.credentialParts.first())
				}
			}
		}
	}

	private fun startScanPartWith(credentialPart: JumioCredentialPart) {
		// Init scan part and start scan
		try {
			try {
				currentScanPart?.cancel()
			} catch (e: Exception) {
				// Current credential cannot be cancelled, ignore the error
				Log.e(TAG, e.message ?: "scan part cancel failed")
			}
			currentCredentialPart = credentialPart
			currentScanPart = currentCredential?.initScanPart(credentialPart, this)
			currentScanPart?.start()
		} catch (exception: Exception) {
			// Invalid arguments on initializing the scan part
			Log.e(TAG, "${exception.message}")
		}
	}

	private fun finishScanPartAndContinue() {
		try {
			currentScanPart?.finish()
			currentScanPart = currentCredential?.getAddonPart()
			if (currentScanPart == null) {
				continueWithNextPart()
			} else {
				currentScanPart?.start()
			}
		} catch (exception: SDKNotConfiguredException) {
			Log.e(TAG, "${exception.message}")
		} catch (exception: IllegalArgumentException) {
			Log.e(TAG, "${exception.message}")
		}
	}

	private fun continueWithNextPart() {
		try {
			if (currentCredential?.isComplete == true) {
				currentCredential?.finish()
				continueWithNextCredential()
				return
			}
			val currentPart = currentCredentialPart ?: return
			val availableParts = currentCredential?.credentialParts ?: return

			val nextIndex = availableParts.indexOf(currentPart) + 1
			if (nextIndex != 0 && availableParts.size > nextIndex) {
				startScanPartWith(availableParts[nextIndex])
			}
		} catch (exception: SDKNotConfiguredException) {
			Log.e(TAG, "${exception.message}")
		} catch (exception: IllegalArgumentException) {
			Log.e(TAG, "${exception.message}")
		}
	}

	private fun continueWithNextCredential() {
		if (currentCredentialInfo?.id == credentialInfoList.last().id) {
			// Finish the controller and continue with upload view
			currentCredential = null
			navigationState.value =
				AppNavigation.Loader(title = getApplication<Application>().applicationContext.getString(R.string.uploading))
			jumioController?.finish()
		} else {
			val nextIndex = (credentialInfoList.indexOfFirst { it.id == currentCredentialInfo?.id }).plus(1)
			currentCredentialInfo = credentialInfoList[nextIndex]
			currentCredential = currentCredentialInfo?.let { jumioController?.start(it) }
			setUpCredential()
		}
	}

	private fun setUpScanView() = when (currentScanPart?.scanMode) {
		JumioScanMode.WEB -> {
			navigationState.value = AppNavigation.DigitalIdentity
		}
		JumioScanMode.FACE_IPROOV -> {
			/*Not needed*/
		}
		JumioScanMode.NFC -> {
			navigationState.value = AppNavigation.NfcScan
		}
		else -> {
			navigationState.value = AppNavigation.Scan
		}
	}

	fun onRetry() {
		if (scanStepEvent.value == JumioScanStep.RETRY) {
			retryReason?.let {
				currentScanPart?.retry(it)
				retryReason = null
			}
		} else {
			jumioError?.let {
				try {
					jumioController?.retry(it)
					navigationState.value = AppNavigation.Consent
				} catch (exception: IllegalArgumentException) {
					showError(exception.message)
				} catch (exception: SDKNotConfiguredException) {
					showError(exception.message)
				}
			}
		}
	}

	private fun showError(message: String?) {
		Toast.makeText(getApplication(), message ?: "Unknown Error", Toast.LENGTH_LONG).show()
	}

	fun finishController() {
		try {
			currentScanPart?.cancel()
			jumioController?.cancel()
		} catch (e: SDKNotConfiguredException) {
			Log.e(TAG, e.message ?: "SDKNotConfiguredException")
		} catch (e: IllegalArgumentException) {
			Log.e(TAG, e.message ?: "IllegalArgumentException")
		}
	}

	fun skipAddonPart() {
		cancelCurrentScanPart()
		continueWithNextPart()
	}

	fun cancelCurrentScanPart() {
		try {
			currentScanPart?.cancel()
			currentScanPart = null
		} catch (exception: SDKNotConfiguredException) {
			Log.e(TAG, exception.message ?: "SDKNotConfiguredException")
		}
	}
}
