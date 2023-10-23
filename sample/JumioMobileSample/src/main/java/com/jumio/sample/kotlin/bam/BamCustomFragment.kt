package com.jumio.sample.kotlin.bam

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.jumio.bam.BamCardInformation
import com.jumio.bam.BamSDK
import com.jumio.bam.custom.BamCustomScanInterface
import com.jumio.bam.custom.BamCustomScanPresenter
import com.jumio.bam.custom.BamCustomScanView
import com.jumio.bam.enums.BamErrorCase
import com.jumio.commons.utils.ScreenUtil
import com.jumio.core.enums.JumioDataCenter
import com.jumio.core.exceptions.PlatformNotSupportedException
import com.jumio.sample.R
import com.jumio.sample.kotlin.MainActivity
import java.util.*

/**
 * Copyright 2019 Jumio Corporation All rights reserved.
 */
class BamCustomFragment : Fragment(), BamCustomScanInterface {

	companion object {
		private const val TAG = "JumioSDK_BamCustom"
		private const val PERMISSION_REQUEST_CODE_BAM_CUSTOM = 302
	}

    private var apiToken: String? = null
    private var apiSecret: String? = null
	private var dataCenter: JumioDataCenter? = null
    private var btnStart : MaterialButton? = null
    private var btnStopBamCustom: MaterialButton? = null
    private var switchCameraImageView: ImageView? = null
    private var toggleFlashImageView: ImageView? = null
    private var bamCustomContainer: RelativeLayout? = null
    private var bamCustomScanView: BamCustomScanView? = null

	private lateinit var bamSDK: BamSDK

    private var customScanPresenter: BamCustomScanPresenter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_bam_custom, container, false)

        apiToken = arguments?.getString(MainActivity.KEY_API_TOKEN)
        apiSecret = arguments?.getString(MainActivity.KEY_API_SECRET)
		dataCenter = arguments?.getSerializable(MainActivity.KEY_DATACENTER) as JumioDataCenter

        btnStart  = rootView.findViewById(R.id.btnStart)
        btnStopBamCustom = rootView.findViewById(R.id.btnStopBamCustom)
        switchCameraImageView = rootView.findViewById(R.id.switchCameraImageView)
        toggleFlashImageView = rootView.findViewById(R.id.toggleFlashImageView)
        bamCustomContainer = rootView.findViewById(R.id.bamCustomContainer)
        bamCustomScanView = rootView.findViewById(R.id.bamCustomScanView)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (btnStart as MaterialButton).text = java.lang.String.format(resources.getString(R.string.button_start), resources.getString(R.string.section_bam_custom))
        btnStart?.setOnClickListener {
            //Since the BamSDK is a singleton internally, a new instance is not
            //created here.
            if (!BamSDK.hasAllRequiredPermissions(activity)) {
                ActivityCompat.requestPermissions(activity!!, BamSDK.getMissingPermissions(activity), PERMISSION_REQUEST_CODE_BAM_CUSTOM)
            } else
                btnStartScan()
        }

        btnStopBamCustom?.setOnClickListener {
            //Do not just re-instantiate the SDK here because fast subsequent taps on the button
            //can cause two SDK instances to be created, which will result in undefined (and
            //most likely incorrect) behaviour. A suitable place for the re-instantiation of the SDK
            //would be onCreate().
            stopBamCustomScan()
        }

        switchCameraImageView?.setOnClickListener { v ->
            v.visibility = View.INVISIBLE
            if (customScanPresenter?.hasMultipleCameras() == true)
                customScanPresenter?.switchCamera()
        }
        toggleFlashImageView?.setOnClickListener { v ->
            v.isEnabled = false
            if (customScanPresenter?.hasFlash() == true) {
                customScanPresenter?.toggleFlash()
                v.isEnabled = true
            }
            toggleFlashImageView?.setImageResource(if (customScanPresenter?.isFlashOn == true) R.drawable.ic_flash_off else R.drawable.ic_flash_on)
        }
    }

    private fun stopBamCustomScan() {
		customScanPresenter?.stopScan()
		customScanPresenter?.clearSDK()
		customScanPresenter = null
		bamCustomContainer?.visibility = View.GONE
		btnStart?.visibility = View.VISIBLE
    }

    override fun onPause() {
        customScanPresenter?.onActivityPause()
        stopBamCustomScan()
        super.onPause()
    }


    override fun onDestroy() {
        super.onDestroy()
        customScanPresenter?.clearSDK()
		customScanPresenter = null

        if(this::bamSDK.isInitialized) {
            bamSDK.destroy()
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val isPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT
        val params = FrameLayout.LayoutParams(if (isPortrait) FrameLayout.LayoutParams.MATCH_PARENT else FrameLayout.LayoutParams.WRAP_CONTENT, if (isPortrait) FrameLayout.LayoutParams.WRAP_CONTENT else ScreenUtil.dpToPx(activity, 300))
        bamCustomScanView?.layoutParams = params
    }

    private fun btnStartScan() {
        try {
            btnStart?.visibility = View.GONE
            bamCustomContainer?.visibility = View.VISIBLE
            initializeBamSDK()
            customScanPresenter = bamSDK.start(this, bamCustomScanView)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
        }

    }


    private fun initializeBamSDK() {
        try {
            // You can get the current SDK version using the method below.
//			BamSDK.getSDKVersion();

            // Call the method isSupportedPlatform to check if the device is supported.
            if (!BamSDK.isSupportedPlatform(activity))
                Log.w(TAG, "Device not supported")

            // Applications implementing the SDK shall not run on rooted devices. Use either the below
            // method or a self-devised check to prevent usage of SDK scanning functionality on rooted
            // devices.
            if (BamSDK.isRooted(activity))
                Log.w(TAG, "Device is rooted")

            // To create an instance of the SDK, perform the following call as soon as your activity is initialized.
            // Make sure that your merchant API token and API secret are correct and specify an instance
            // of your activity. If your merchant account is created in the EU data center, use
            // JumioDataCenter.EU instead.
            bamSDK = BamSDK.create(activity, apiToken, apiSecret, dataCenter)

            // Use the following method to enable offline credit card scanning.
//			try {
//				bamSDK = BamSDK.create(activity, "YOUROFFLINETOKEN");
//			} catch (e: SDKExpiredException) {
//				e.printStackTrace();
//				Toast.makeText(activity?.applicationContext, "The offline SDK is expired", Toast.LENGTH_LONG).show();
//			}

            // Overwrite your specified reporting criteria to identify each scan attempt in your reports (max. 100 characters).
//			bamSDK.setMerchantReportingCriteria("YOURREPORTINGCRITERIA")

            // To restrict supported card types, pass an ArrayList of CreditCardTypes to the setSupportedCreditCardTypes method.
//			val creditCardTypes = ArrayList<CreditCardType>()
//			creditCardTypes.add(CreditCardType.VISA)
//			creditCardTypes.add(CreditCardType.MASTER_CARD)
//			creditCardTypes.add(CreditCardType.AMERICAN_EXPRESS)
//			creditCardTypes.add(CreditCardType.DINERS_CLUB)
//			creditCardTypes.add(CreditCardType.DISCOVER)
//			creditCardTypes.add(CreditCardType.CHINA_UNIONPAY)
//			creditCardTypes.add(CreditCardType.JCB)
//			bamSDK.setSupportedCreditCardTypes(creditCardTypes)

            // Expiry recognition, card holder name and CVV entry are enabled by default and can be disabled.
            // You can enable the recognition of sort code and account number.
//			bamSDK.setExpiryRequired(false)
//			bamSDK.setCardHolderNameRequired(false)
//			bamSDK.setCvvRequired(false)
//			bamSDK.setSortCodeAndAccountNumberRequired(true)

            // You can show the unmasked credit card number to the user during the workflow if setCardNumberMaskingEnabled is disabled.
//			bamSDK.setCardNumberMaskingEnabled(false)

            // The user can edit the recognized expiry date if setExpiryEditable is enabled.
//			bamSDK.setExpiryEditable(true)

            // The user can edit the recognized card holder name if setCardHolderNameEditable is enabled.
//			bamSDK.setCardHolderNameEditable(true)

            // You can set a short vibration and sound effect to notify the user that the card has been detected.
//			bamSDK.setVibrationEffectEnabled(true)
//			bamSDK.setSoundEffect(R.raw.shutter_sound)

            // Use the following method to set the default camera position.
//			bamSDK.setCameraPosition(JumioCameraPosition.FRONT)

            // Automatically enable flash when scan is started.
//			bamSDK.setEnableFlashOnScanStart(true)

            // You can add custom fields to the confirmation page (keyboard entry or predefined values).
//			bamSDK.addCustomField("zipCodeId", getString(R.string.zip_code), InputType.TYPE_CLASS_NUMBER, "[0-9]{5,}")
//			val states = resources.getStringArray(R.array.state_selection_values)
//			val statesList = ArrayList<String>(states.size)
//			for (state in states) statesList.add(state)
//			bamSDK.addCustomField("stateId", getString(R.string.state), statesList, false, getString(R.string.state_reset_value))

            // Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
//			bamSDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID);

        } catch (e: PlatformNotSupportedException) {
            Log.e(TAG, "Error in initializeNetverifySDK: ", e)
            Toast.makeText(activity?.applicationContext, "This platform is not supported", Toast.LENGTH_LONG).show()
        } catch (e1: NullPointerException) {
            Log.e(TAG, "Error in initializeNetverifySDK: ", e1)
        }

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!isVisibleToUser) {
            stopBamCustomScan()
        }
    }

    //Called as soon as the camera is available for the custom scan. It is safe to check for flash and additional cameras here.
    override fun onBamCameraAvailable() {
        Log.d("BamCustomScan", "camera available")
        switchCameraImageView?.visibility = if (customScanPresenter?.hasMultipleCameras() == true) View.VISIBLE else View.INVISIBLE
        switchCameraImageView?.setImageResource(if (customScanPresenter?.isCameraFrontFacing == true) R.drawable.ic_camera_rear else R.drawable.ic_camera_front)
        toggleFlashImageView?.visibility = if (customScanPresenter?.hasFlash() == true) View.VISIBLE else View.INVISIBLE
        toggleFlashImageView?.setImageResource(if (customScanPresenter?.isFlashOn == true) R.drawable.ic_flash_off else R.drawable.ic_flash_on)
    }

    override fun onBamError(errorCode: String, errorMessage: String, retryPossible: Boolean, scanAttempts: ArrayList<String>) {
        Log.d("BamCustomScan", "error occured")
        //Do not show error dialog when it is an error because of background execution not supported exception.
        if (errorCode.startsWith(BamErrorCase.CANCEL_TYPE_BACKGROUND.code()))
            return
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle("Scan error")
        alertDialogBuilder.setMessage(errorMessage)
        if (retryPossible) {
            alertDialogBuilder.setPositiveButton("retry") { _, _ ->
                try {
                    customScanPresenter?.retryScan()
                } catch (e: UnsupportedOperationException) {
                    e.printStackTrace()
                    Toast.makeText(activity?.applicationContext, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        alertDialogBuilder.setNegativeButton("cancel") { _, _ -> btnStopBamCustom?.performClick() }
        alertDialogBuilder.show()
    }

    //When extraction is started, the preview screen will be paused. A loading indicator can be displayed within this callback.
    override fun onBamExtractionStarted() {
        Log.d("BamCustomScan", "extraction started")
    }

    override fun onBamExtractionFinished(bamCardInformation: BamCardInformation, scanAttempts: ArrayList<String>) {
        Log.d("BamCustomScan", "extraction finished")
        bamCardInformation.clear()

		switchCameraImageView?.visibility = View.INVISIBLE
		toggleFlashImageView?.visibility = View.INVISIBLE

        //		//At this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
        //		//internal resources can be freed.
        onDestroy()
    }
}
