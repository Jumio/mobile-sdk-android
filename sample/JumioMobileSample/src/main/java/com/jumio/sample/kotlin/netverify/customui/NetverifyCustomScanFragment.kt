package com.jumio.sample.kotlin.netverify.customui

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jumio.commons.utils.ScreenUtil
import com.jumio.core.data.document.ScanSide
import com.jumio.nv.custom.*
import com.jumio.nv.nfc.custom.NetverifyCustomNfcInterface
import com.jumio.nv.nfc.custom.NetverifyCustomNfcPresenter
import com.jumio.sample.R

class NetverifyCustomScanFragment : Fragment(), View.OnClickListener {
	private var scanSide: String? = null
	private var documentType: String? = null
	private var progressText: String? = null
	private var loadingIndicator: ProgressBar? = null
	private var customScanView: NetverifyCustomScanView? = null
	private var customScanViewPresenter: NetverifyCustomScanPresenter? = null
	private var customNfcViewPresenter: NetverifyCustomNfcPresenter? = null
	private var customConfirmationView: NetverifyCustomConfirmationView? = null
	private var customAnimationView: NetverifyCustomAnimationView? = null
	private var tvDocumentType: TextView? = null
	private var tvSteps: TextView? = null
	private var tvHelp: TextView? = null
	private var btnConfirm: Button? = null
	private var btnRetake: Button? = null
	private var btnFallback: Button? = null
	private var btnRetryFace: Button? = null
	private var btnCapture: Button? = null
	private var btnDismissHelp: Button? = null
	private var btnSkipNfc: Button? = null
	private var isOnConfirmation = false
	private var flashAvailableForCamera = false
	private var modeType = 0
	private var callback: OnScanFragmentInteractionListener? = null
	private var customScanImpl: NetverifyCustomScanImpl? = null
	private var flash: MenuItem? = null
	private var switchCamera: MenuItem? = null
	private var blurToast: Toast? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (arguments != null) {
			scanSide = arguments!!.getString(ARG_SCAN_SIDE)
			documentType = arguments!!.getString(ARG_SCAN_DOCUMENT)
			progressText = arguments!!.getString(ARG_SCAN_PROGRESS)
		}
	}

	/**
	 * Creates view and initializes elements depending on what kind of scan is happening, document or face
	 *
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return root view
	 */
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		val root: View
		// Inflate layout for this fragment in case of FACE scan
		// Needs animation view to display help animations
		if (ScanSide.valueOf(scanSide!!) == ScanSide.FACE) {
			root = inflater.inflate(R.layout.fragment_netverify_custom_scan_face, container, false)
			btnRetryFace = root.findViewById(R.id.fragment_custom_scan_face_btn_retry)
			btnRetryFace?.setOnClickListener(this)
			btnRetryFace?.visibility = View.GONE
			modeType = NetverifyCustomScanView.MODE_FACE
			customScanView = root.findViewById(R.id.fragment_nv_custom_scan_view)

			customScanView?.mode = modeType

			tvHelp = root.findViewById(R.id.fragment_nv_custom_scan_face_helptext)
		} else {
			// Inflate layout for this fragment in case of DOCUMENT scan
			// Needs retake, confirm and fallback buttons
			root = inflater.inflate(R.layout.fragment_netverify_custom_scan, container, false)

			btnRetake = root.findViewById(R.id.fragment_custom_scan_btn_retake)
			btnConfirm = root.findViewById(R.id.fragment_custom_scan_btn_confirm)
			btnFallback = root.findViewById(R.id.fragment_custom_scan_btn_fallback)
			btnRetake?.setOnClickListener(this)
			btnConfirm?.setOnClickListener(this)
			btnFallback?.setOnClickListener(this)
			tvHelp = root.findViewById(R.id.fragment_custom_scan_tv_help)
			tvDocumentType = root.findViewById(R.id.fragment_custom_scan_tv_document_type)
			tvDocumentType?.text = context?.getString(R.string.netverify_helpview_small_title_capture, documentType, "")?.let { HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY) }
			tvSteps = root.findViewById(R.id.fragment_custom_scan_tv_steps)
			tvSteps?.text = progressText
			modeType = NetverifyCustomScanView.MODE_ID
			customScanView = root.findViewById(R.id.fragment_nv_custom_scan_view)
			customScanView?.mode = modeType
			btnSkipNfc = root.findViewById(R.id.fragment_custom_scan_btn_skip_nfc)
			btnSkipNfc?.setOnClickListener(this)
		}

		btnCapture = root.findViewById(R.id.fragment_custom_scan_btn_capture)
		btnCapture?.setOnClickListener(this)

		btnDismissHelp = root.findViewById(R.id.fragment_custom_scan_btn_dismiss_help)
		btnDismissHelp?.setOnClickListener(this)

		loadingIndicator = root.findViewById(R.id.fragment_nv_custom_loading_indicator)
		customConfirmationView = root.findViewById(R.id.fragment_nv_custom_confirmation_view)
		customAnimationView = root.findViewById(R.id.fragment_nv_custom_animation_view)

		customScanImpl = NetverifyCustomScanImpl()
		customScanViewPresenter = callback?.onInitScanningWithSide(ScanSide.valueOf(scanSide!!), customScanView, customConfirmationView, customScanImpl)

		setHasOptionsMenu(true)
		return root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (customScanViewPresenter != null && savedInstanceState == null) {
			setHelpText(customScanViewPresenter?.helpText)
			// show upfront help animation if available, otherwise skip and start scanning directly
			if (customScanViewPresenter?.hasHelpAnimation() == true) {
				showDocumentHelpAnimation()
			} else {
				customScanViewPresenter?.startScan()
			}

			//Check for displaying shutter button for face manual capturing here as no upfront help is displayed
			showShutterButton(customScanViewPresenter?.showShutterButton() == true)
		}
	}

	/**
	 * Set up and show document help animation at the beginning of document scanning process
	 * Continues to actual customScanView on button click
	 */
	private fun showDocumentHelpAnimation() {
		hideView(false, tvDocumentType, tvSteps, customScanView)
		btnDismissHelp?.setOnClickListener(this)
		showView(btnDismissHelp)
		tvDocumentType?.visibility = View.INVISIBLE
		tvSteps?.visibility = View.INVISIBLE
		if (customScanViewPresenter?.hasHelpAnimation() == true) {
			customScanViewPresenter?.getHelpAnimation(customAnimationView)
		}
	}

	/**
	 * Starts face scanning over again, if face scan went wrong for whatever reason
	 * (e.g. it was manually cancelled by user, the lighting was too dark, etc.)
	 */
	private fun retryFaceScanning() {
		customAnimationView!!.destroy()
		hideView(false, customAnimationView!!, btnRetryFace)
		if (btnCapture != null && customScanViewPresenter!!.showShutterButton()) {
			showView(btnCapture)
		}
		onRetryScan()
	}

	/**
	 * Shows loading screen with spinner as loading indicator
	 */
	fun showSubmissionLoading() {
		hideView(true, customConfirmationView, customScanView, btnCapture)
	}

	/**
	 * Shows confirmation if document scan was successful, lets user either accept scanned image
	 * or retake it in case the result is not satisfactory
	 */
	fun showConfirmation(faceOnBack: Boolean) {
		hideView(false, customScanView, btnFallback, btnCapture)
		showView(customConfirmationView!!, btnConfirm, btnRetake)
		if (tvHelp != null) {
			if (faceOnBack) {
				tvHelp?.text = getString(R.string.custom_ui_scan_face_on_back)
			} else {
				tvHelp?.text = getString(R.string.custom_ui_scan_confirmation)
			}
		}
		if (tvDocumentType != null) {
			tvDocumentType?.setText(R.string.netverify_scanview_title_check)
		}
	}

	/**
	 * Shows loading screen with spinner as loading indicator, informs user that document is processing
	 */
	fun showLoading() {
		hideView(true, customScanView!!, btnFallback, btnConfirm, btnRetake, btnCapture)
		if (tvHelp != null) {
			if (modeType == NetverifyCustomScanView.MODE_ID) {
				tvHelp?.text = getString(R.string.netverify_scanview_snackbar_progress)
			} else if (modeType == NetverifyCustomScanView.MODE_FACE) {
				tvHelp?.text = HtmlCompat.fromHtml(getString(R.string.netverify_scanview_analyzing_biometrics), HtmlCompat.FROM_HTML_MODE_LEGACY)
			}
			if(tvHelp?.visibility == View.GONE) {
				showView(tvHelp)
			}
		}
		if (tvDocumentType != null) {
			tvDocumentType?.setText(R.string.netverify_scanview_title_check)
		}
	}

	/**
	 * Handles cancelled scan
	 * If document scan was cancelled, user is asked to try again, if face scan was cancelled
	 * user is shown appropriate help animation
	 */
	fun displayRetryHelp(scanSide: ScanSide) {
		customScanViewPresenter?.stopScan()
		if(customScanViewPresenter?.hasHelpAnimation() == true) {
			customScanViewPresenter?.getHelpAnimation(customAnimationView)
			showView(customAnimationView)
		}
		setHelpText(customScanViewPresenter?.helpText)
		(activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

		if (scanSide == ScanSide.FACE) {
			showView(btnRetryFace, tvHelp)
		} else {
			showView(btnRetake, tvHelp)
		}
	}

	/**
	 * Make one or more views visible
	 *
	 * @param views specifies which view(s)
	 */
	private fun showView(vararg views: View?) {
		for (view in views) {
			if (view != null) {
				view.visibility = View.VISIBLE
			}
		}
	}

	/**
	 * Hide one or more views
	 *
	 * @param showLoading specifies if loading screen should be displayed while views are hidden
	 * @param views       specifies which view(s)
	 */
	private fun hideView(showLoading: Boolean, vararg views: View?) {
		for (view in views) {
			if (view != null) {
				view.visibility = View.GONE
			}
		}
		loadingIndicator?.visibility = if (showLoading) View.VISIBLE else View.GONE
	}

	/**
	 * Attaches fragment
	 */
	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = if (context is OnScanFragmentInteractionListener) {
			context
		} else {
			throw RuntimeException(context.toString()
					+ " must implement OnSuccessFragmentInteractionListener")
		}
	}

	/**
	 * Detaches fragment
	 */
	override fun onDetach() {
		super.onDetach()
		callback = null
	}

	/**
	 * Creates action bar at the top of the screen, menu contains items
	 * like the back button, camera flash and camera switch button
	 *
	 * @param menu refers to action bar at the top
	 * @param inflater inflates existing menu
	 */
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.menu_netverify_custom, menu)
		if (customScanViewPresenter != null) {
			customScanImpl?.onNetverifyCameraAvailable()
		}
		flash = menu.findItem(R.id.action_toggle_flash)
		switchCamera = menu.findItem(R.id.action_switch_camera)
		if (flash != null) {
			flash?.isVisible = isFlashAvailable
		}
		if (switchCamera != null) {
			switchCamera?.isVisible = isSwitchCameraAvailable
		}
		super.onCreateOptionsMenu(menu, inflater)
	}

	/**
	 * Handles action bar items (back, flash and camera switch button)
	 *
	 * @param item refers to item in menu that was clicked
	 * @return boolean
	 */
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			activity?.finish()
			return true
		}
		if (customScanViewPresenter != null) {
			if (item.itemId == R.id.action_toggle_flash) {
				customScanViewPresenter?.toggleFlash()
			} else if (item.itemId == R.id.action_switch_camera) {
				customScanViewPresenter?.switchCamera()
			}
		}
		return super.onOptionsItemSelected(item)
	}

	/**
	 * Handles all possible button clicks
	 */
	override fun onClick(v: View?) {
		if (v != null) {
			when (v.id) {
				R.id.fragment_custom_scan_btn_confirm -> {
					showSubmissionLoading()
					onConfirmScan()
					Log.i(TAG, "Scan btn confirm clicked")
				}
				R.id.fragment_custom_scan_btn_retake -> {
					onRetryScan()
					Log.i(TAG, "Scan btn retake clicked")
				}
				R.id.fragment_custom_scan_btn_fallback -> {
					onStartFallback()
					Log.i(TAG, "Scan btn fallback clicked")
				}
				R.id.fragment_custom_scan_btn_capture -> {
					customScanViewPresenter?.takePicture()
					Log.i(TAG, "Capture btn clicked")
				}
				R.id.fragment_custom_scan_face_btn_retry -> {
					retryFaceScanning()
					Log.i(TAG, "Scan btn face retry clicked")
				}
				R.id.fragment_custom_scan_btn_dismiss_help -> {
					hideView(false, customAnimationView, btnDismissHelp)
					customScanView?.let { initScanView(it) }
					customScanViewPresenter?.startScan()
					showView(tvDocumentType, tvSteps, customScanView)
					if (customScanViewPresenter?.isFallbackAvailable == true) {
						setFallbackVisibility(true)
					}
					customScanViewPresenter?.showShutterButton()?.let { showShutterButton(it) }
					Log.i(TAG, "Dismiss help btn clicked")
				}
				R.id.fragment_custom_scan_btn_skip_nfc -> {
					hideView(false, customAnimationView, btnSkipNfc)
					customNfcViewPresenter?.cancel()
					customNfcViewPresenter = null
					customAnimationView?.destroy()
					Log.i(TAG, "Skip NFC btn clicked")
				}
			}
		}
	}

	override fun onResume() {
		super.onResume()
		if (customScanViewPresenter != null) {
			customScanViewPresenter?.resume()
		}
	}

	override fun onPause() {
		if (customScanViewPresenter != null) {
			customScanViewPresenter?.pause()
		}
		super.onPause()
	}

	/**
	 * Callback when scan is confirmed as finished
	 */
	private fun onConfirmScan() {
		isOnConfirmation = false
		activity?.invalidateOptionsMenu()
		customScanViewPresenter?.confirmScan()
	}

	/**
	 * Called if scan is repeated
	 */
	private fun onRetryScan() {
		if (customScanViewPresenter?.isFallbackAvailable != true) {
			setFallbackVisibility(false)
		} else {
			setFallbackVisibility(true)
		}
		isOnConfirmation = false
		if (flash != null) {
			flash?.isVisible = isFlashAvailable
		}
		if (switchCamera != null) {
			switchCamera?.isVisible = isSwitchCameraAvailable
		}
		if (activity != null) {
			activity?.invalidateOptionsMenu()
		}
		if (btnRetryFace != null) {
			hideView(false, customAnimationView, btnRetryFace, tvHelp)
		}
		hideView(false, customConfirmationView, btnRetake, btnConfirm, tvHelp)

		if (customScanViewPresenter?.showShutterButton() == true) {
			showView(btnCapture)
			hideView(false, btnFallback, tvHelp)
			setHelpText("   ")
		} else {
			hideView(false, btnCapture)
		}
		showView(customScanView)
		if (tvDocumentType != null) {
			tvDocumentType?.text = context?.getString(R.string.netverify_helpview_small_title_capture, documentType, "")?.let { HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY) }
		}
		setHelpText(customScanViewPresenter?.helpText)
		customScanViewPresenter?.retryScan()
	}

	/**
	 * Called if fallback is being used (either because it is the only option available or user chose to use it)
	 */
	private fun onStartFallback() {
		if (customScanViewPresenter != null) {
			customScanViewPresenter?.startFallback()
			setFallbackVisibility(false)
		}
	}

	//#####################################################
	// HELPER METHODS
	//#####################################################

	/**
	 * Creates dialog to handle disbled NFC settings
	 */
	fun buildNfcSettingsDialog() {
			try {
				if (activity != null) {
					activity?.applicationContext?.let {
						MaterialAlertDialogBuilder(it)
								.setTitle(com.jumio.nv.R.string.netverify_nfc_enable_dialog_title)
								.setMessage(com.jumio.nv.R.string.netverify_nfc_enable_dialog_text)
								.setPositiveButton(android.R.string.yes) { dialog, _ ->
									dialog.dismiss()
									activity?.startActivity(Intent("android.settings.NFC_SETTINGS"))
								}
								.setNegativeButton(android.R.string.no) { dialog, _ ->
									dialog.dismiss()
									customNfcViewPresenter?.cancel()
								}
								.show()
					}
				}
			} catch (e: Exception) { //do not handle
				Log.e(TAG, "dialog builder: ", e)
			}
	}

	/**
	 * Creates dialog to handle NFC error
	 *
	 * @param errorMessage
	 * @param retryable
	 */
	fun buildNfcErrorDialog(errorMessage: String?, retryable: Boolean) {
		try {
			if (activity != null) {
				val dialogBuilder = activity?.let { MaterialAlertDialogBuilder(it) }
				dialogBuilder?.setTitle(R.string.netverify_nfc_general_error_dialog_title)
				dialogBuilder?.setMessage(errorMessage)

				if(retryable) { // retryable error
					dialogBuilder?.setPositiveButton(R.string.jumio_button_retry) { dialog, _ ->
						dialog.dismiss()
						customNfcViewPresenter?.retry()
					}
					dialogBuilder?.setNegativeButton(R.string.jumio_button_cancel) { dialog, _ ->
						dialog.dismiss()
						customNfcViewPresenter?.cancel()
					}
				} else { // not retryable error
					dialogBuilder?.setNeutralButton(R.string.jumio_button_cancel) { dialog, _ ->
						dialog.dismiss()
						customNfcViewPresenter?.cancel()
					}
				}
				dialogBuilder?.show()
			}
		} catch (e: Exception) { //do not handle
			Log.e(TAG, "dialog builder: ", e)
		}
	}

	/**
	 * Sets text displayed at bottom of the screen to help user with the process
	 *
	 * @param helpText
	 */
	fun setHelpText(helpText: String?) {
		if (!TextUtils.isEmpty(helpText) && tvHelp != null) {
			tvHelp?.text = helpText
		}
	}

	/**
	 * Sets visibility of capture button
	 * (button only in use and visible if face scan isn't done automatically,
	 * but in capture mode)
	 *f
	 * @param visible Boolean
	 */
	private fun showShutterButton(visible: Boolean) {
		if (btnCapture != null) {
			btnCapture?.visibility = if (visible) View.VISIBLE else View.GONE
		}
		if (tvHelp != null) {
			tvHelp?.visibility = if (visible) View.GONE else View.VISIBLE
		}
	}

	/**
	 * Sets visibility of fallback button
	 *
	 * @param visible Boolean
	 */
	private fun setFallbackVisibility(visible: Boolean) {
		if (btnFallback != null) {
			btnFallback?.visibility = if (visible) View.VISIBLE else View.GONE
		}
	}

	/**
	 * Checks if device is in portrait mode
	 *
	 * @return boolean
	 */
	private val isPortrait: Boolean
		get() {
			val display = activity!!.windowManager.defaultDisplay
			val size = Point()
			display.getSize(size)
			return size.y > size.x
		}

	/**
	 * Checks if device has flash available
	 */
	private val isFlashAvailable: Boolean
		get() = customScanViewPresenter != null && !isOnConfirmation && customScanViewPresenter!!.hasFlash()

	/**
	 * Checks if device can switch between front and back camera
	 */
	private val isSwitchCameraAvailable: Boolean
		get() = customScanViewPresenter != null && !isOnConfirmation && customScanViewPresenter!!.hasMultipleCameras()

	//#####################################################
	// SCAN METHODS
	//#####################################################
	/**
	 * Initializes a scan view that is used for both document and face scans
	 *
	 * @param customScanView
	 */
	private fun initScanView(customScanView: NetverifyCustomScanView) { //Changes layout parameters if device is in portrait mode
		val isPortrait = isPortrait
		val params = ConstraintLayout.LayoutParams(if (isPortrait) ConstraintLayout.LayoutParams.MATCH_PARENT else ConstraintLayout.LayoutParams.WRAP_CONTENT,
				if (isPortrait) ConstraintLayout.LayoutParams.WRAP_CONTENT else ScreenUtil.dpToPx(activity!!.applicationContext, 300))
		customScanView.layoutParams = params
		if (customScanViewPresenter != null) {
			if (isPortrait) {
				customScanView.ratio = if (isFaceScan()) 0.71f else 0.9f
			} else {
				customScanView.ratio = if (isFaceScan()) 1.66f else 1.0f
			}
		}
	}

	private fun isFaceScan(): Boolean {
		return customScanViewPresenter!!.scanMode == NetverifyScanMode.FACE_MANUAL ||
				customScanViewPresenter!!.scanMode == NetverifyScanMode.FACE_IPROOV ||
				customScanViewPresenter!!.scanMode == NetverifyScanMode.FACE_ZOOM
	}

	/**
	 * Handles actual scanning
	 */
	private inner class NetverifyCustomScanImpl : NetverifyCustomScanInterface {
		/**
		 * Custom ScanView interface
		 * Handles finished scan, checks if all necessary sides have been scanned or not,
		 * starts another scan fragment if they're not all finished yet
		 *
		 * @param scanSide        the scanned side
		 * @param allPartsScanned true if all parts have been scanned
		 */
		override fun onNetverifyScanForPartFinished(scanSide: ScanSide, allPartsScanned: Boolean) {
			customScanViewPresenter?.destroy()
			customScanViewPresenter = null
			// not all necessary parts scanned yet
			if (!allPartsScanned) { //index refers to list containing all possible sides
				callback?.onScanForPartFinished()
			} else { //show loading on scan view during submission
				showSubmissionLoading()
				callback?.onScanFinished()
			}
			Log.i(TAG, "onNetverifyScanForPartFinished")
		}

		/**
		 * Custom ScanView interface
		 */
		override fun onNetverifyCameraAvailable() {
			Log.i(TAG, "onNetverifyCameraAvailable")
			flashAvailableForCamera = customScanViewPresenter!!.hasFlash()
			customScanViewPresenter?.isFallbackAvailable

			flash?.isVisible = isFlashAvailable
		}

		/**
		 * Custom ScanView interface
		 * Shows loading screen, disables flash and camera switch button
		 */
		override fun onNetverifyExtractionStarted() {
			Log.i(TAG, "onNetverifyExtractionStarted")
			if (flash != null) {
				flash?.isVisible = false
			}
			if (switchCamera != null) {
				switchCamera?.isVisible = false
			}
			showLoading()
		}

		/**
		 * Custom ScanView interface
		 * Show loading indicator and display loading text, hide buttons
		 */
		override fun onNetverifyPrepareScanning() {
			Log.i(TAG, "onNetverifyPrepareScanning")
			showLoading()
			hideView(true, customScanView!!, btnFallback, btnConfirm, btnRetake, btnCapture)
			tvHelp?.visibility = View.VISIBLE
			tvHelp?.text = getString(R.string.jumio_accessibility_loading)
		}

		/**
		 * Custom ScanView interface
		 * Shows confirmation if scan has been finished
		 *
		 * @param confirmationType the type of confirmation that should be displayed
		 */
		override fun onNetverifyPresentConfirmationView(confirmationType: NetverifyConfirmationType) {
			isOnConfirmation = true
			Log.i(TAG, "onNetverifyPresentConfirmationView")
			if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
				(activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
			}
			showConfirmation(confirmationType == NetverifyConfirmationType.CHECK_DOCUMENT_SIDE)
		}

		/**
		 * No US Address has been found in the barcode. The scan preview will switch to frontside scanning if available.
		 * Check for the changed scan mode and help text. Will only be called on a Fastfill scan.
		 */
		override fun onNetverifyNoUSAddressFound() {
			Log.i(TAG, "onNetverifyNoUSAddressFound")
		}

		/**
		 * Face scanning is not possible in landscape orientation. Please notify the user accordingly
		 */
		override fun onNetverifyFaceInLandscape() {
			Log.i(TAG, "onNetverifyFaceInLandscape")
		}

		/**
		 * During the scanning of some ID cards, a legal advice need to be shown.
		 */
		override fun onNetverifyShowLegalAdvice(legalAdvice: String) {
			val toast = Toast.makeText(activity!!.applicationContext, legalAdvice, Toast.LENGTH_LONG)
			toast.setGravity(Gravity.CENTER, 0, 0)
			toast.show()
			Log.i(TAG, "onNetverifyShowLegalAdvice: $legalAdvice")
		}

		/**
		 * Notify the user that the image is blurry and therefore can't be taken.
		 */
		override fun onNetverifyDisplayBlurHint() {
			if (blurToast != null) {
				try {
					if (blurToast?.getView()?.isShown() == true) {
						return
					} else {
						blurToast?.cancel()
					}
				} catch (e: java.lang.Exception) {
					e.printStackTrace()
				}
			}
			blurToast = Toast.makeText(activity!!.applicationContext, R.string.jumio_scanview_refocus, Toast.LENGTH_SHORT)
			blurToast?.setGravity(Gravity.CENTER, 0, 0)
			blurToast?.show()
			Log.i(TAG, "onNetverifyDisplayBlurHint")
		}
		/**
		 * Handles scan cancellation, whether user cancelled it manually (back button) or scan went wrong
		 * because of a certain reason (bad lighting, etc.), starts up appropriate help animation in that case
		 *
		 * @param scanSide              the scanned side
		 * @param netverifyCancelReason reason why scan was cancelled
		 */
		override fun onNetverifyScanForPartCanceled(scanSide: ScanSide, netverifyCancelReason: NetverifyCancelReason) {
			Log.i(TAG, "onNetverifyScanForPartCanceled")
			if(isDetached) {
				return
			}
			hideView(false)
			setHelpText(getString(R.string.netverify_scanview_snackbar_check_process_error))

			when (netverifyCancelReason) {
				NetverifyCancelReason.ERROR_GENERIC -> {
					displayRetryHelp(scanSide)
				}
				NetverifyCancelReason.USER_CANCEL, NetverifyCancelReason.USER_BACK -> {
					if (scanSide == ScanSide.FACE) {
						displayRetryHelp(scanSide)
					} else {
						callback?.onScanCancelled()
					}
				}
				NetverifyCancelReason.NOT_AVAILABLE -> callback?.onScanCancelled()
			}
		}

		/**
		 * This function will be called when the NFC scan is getting prepared. If no NFC scan should be done, Null can be returned here.
		 *
		 * @return instance of [NetverifyCustomNfcInterface] or null
		 */
		override fun getNetverifyCustomNfcInterface(): NetverifyCustomNfcInterface? {
			Log.i(TAG, "getNetverifyCustomNfcInterface")
			return NetverifyCustomNfcImpl()
		}

		/**
		 * NFC scanning can be started now and can be controlled with the [NetverifyCustomNfcPresenter]
		 *
		 * @param netverifyCustomNfcPresenter NetverifyCustomNfcPresenter
		 */
		override fun onNetverifyStartNfcExtraction(netverifyCustomNfcPresenter: NetverifyCustomNfcPresenter) {
			Log.i(TAG, "onNetverifyStartNfcExtraction")

			customNfcViewPresenter = netverifyCustomNfcPresenter
			customNfcViewPresenter?.getHelpAnimation(customAnimationView)

			tvDocumentType?.text = getString(R.string.netverify_nfc_header_start)
			hideView(false, tvSteps)

			setHelpText(customNfcViewPresenter?.helpText)

			showView(customAnimationView, btnSkipNfc)
			hideView(false, customScanView, btnConfirm, btnRetake)
		}
	}

	/**
	 * Handles NFC detection.
	 */
	private inner class NetverifyCustomNfcImpl : NetverifyCustomNfcInterface {
		override fun onNetverifyNfcStarted() {
			tvDocumentType?.text = getString(R.string.netverify_nfc_header_extracting)
			Log.i(TAG, "onNetverifyNfcStarted")
		}

		override fun onNetverifyNfcUpdate(progress: Int) {
			Log.i(TAG, String.format("onNetverifyNfcUpdate %d", progress))
		}

		override fun onNetverifyNfcFinished() {
			tvDocumentType?.text = getString(R.string.netverify_nfc_header_finish)
			hideView(false, customAnimationView, btnSkipNfc)
			showView(tvSteps)
			Log.i(TAG, "onNetverifyNfcFinished")
		}

		override fun onNetverifyNfcSystemSettings() {
			buildNfcSettingsDialog()
			Log.i(TAG, "NFC not enabled")
		}

		override fun onNetverifyNfcError(errorMessage: String?, retryable: Boolean) {
			tvDocumentType?.text = getString(R.string.netverify_nfc_header_start)
			buildNfcErrorDialog(errorMessage, retryable)
			Log.e(TAG, String.format("$errorMessage, retry possible: $retryable"))
		}
	}

	/**
	 * Interface for fragment interaction with activity.
	 */
	interface OnScanFragmentInteractionListener {
		fun onInitScanningWithSide(side: ScanSide?, scanView: NetverifyCustomScanView?,
		                           confirmationView: NetverifyCustomConfirmationView?, customScanInterface: NetverifyCustomScanInterface?): NetverifyCustomScanPresenter?

		fun onScanForPartFinished()
		fun onScanCancelled()
		fun onScanFinished()
	}

	companion object {
		private const val TAG = "NvCustomScanFragment"
		private const val ARG_SCAN_SIDE = "ARG_SCAN_SIDE"
		private const val ARG_SCAN_DOCUMENT = "ARG_SCAN_DOCUMENT"
		private const val ARG_SCAN_PROGRESS = "ARG_SCAN_PROGRESS"
		/**
		 * Constructor with parameters
		 *
		 * @param scanSide     specifies which side of document is scanned (front, back or face in case of face scan)
		 * @param document     specifies what kind of document is scanned (passport, DL, etc.)
		 * @param progressText text tracking progress
		 * @return fragment
		 */
		fun newInstance(scanSide: String?, document: String?, progressText: String?): NetverifyCustomScanFragment {
			val fragment = NetverifyCustomScanFragment()
			val args = Bundle()
			args.putString(ARG_SCAN_SIDE, scanSide)
			args.putString(ARG_SCAN_DOCUMENT, document)
			args.putString(ARG_SCAN_PROGRESS, progressText)
			fragment.arguments = args
			return fragment
		}
	}
}