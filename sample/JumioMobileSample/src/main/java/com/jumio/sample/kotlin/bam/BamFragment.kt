package com.jumio.sample.kotlin.bam

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.jumio.bam.BamCardInformation
import com.jumio.bam.BamSDK
import com.jumio.core.enums.JumioDataCenter
import com.jumio.core.exceptions.MissingPermissionException
import com.jumio.core.exceptions.PlatformNotSupportedException
import com.jumio.sample.R
import com.jumio.sample.kotlin.MainActivity

/**
 * Copyright 2019 Jumio Corporation All rights reserved.
 */
class BamFragment : Fragment(), View.OnClickListener {

    companion object {
        private val TAG = "JumioSDK_Bam"
        private val PERMISSION_REQUEST_CODE_BAM = 300
    }

    private var apiToken: String? = null
    private var apiSecret: String? = null
	private var dataCenter: JumioDataCenter? = null
    private var switchOptionOne: SwitchMaterial? = null
    private var switchOptionTwo: SwitchMaterial? = null
    private var btnStart: MaterialButton? = null

    internal lateinit var bamSDK: BamSDK

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_main, container, false)

        apiToken = arguments?.getString(MainActivity.KEY_API_TOKEN)
        apiSecret = arguments?.getString(MainActivity.KEY_API_SECRET)
		dataCenter = arguments?.getSerializable(MainActivity.KEY_DATACENTER) as JumioDataCenter
        switchOptionOne = rootView.findViewById(R.id.switchOptionOne)
        switchOptionTwo = rootView.findViewById(R.id.switchOptionTwo)
        btnStart = rootView.findViewById(R.id.btnStart)

        return rootView
    }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		switchOptionOne?.text = resources.getString(R.string.bam_expiry_required)
		switchOptionTwo?.text = resources.getString(R.string.bam_cvv_required)
		btnStart?.text = java.lang.String.format(resources.getString(R.string.button_start), resources.getString(R.string.section_bam))
		btnStart?.setOnClickListener(this)
	}

    override fun onClick(view: View) {
        //Since the BamSDK is a singleton internally, a new instance is not
        //created here.
        if(::bamSDK.isInitialized) {
            bamSDK.clearCustomFields()
        }
        initializeBamSDK()

        if ((activity as MainActivity).checkPermissions(PERMISSION_REQUEST_CODE_BAM)) {
            try {
                startActivityForResult(bamSDK.intent, BamSDK.REQUEST_CODE)
            } catch (e: MissingPermissionException) {
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }

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

            // Use the following method to create an instance of the SDK, using offline credit card scanning.
//			try {
//				bamSDK = BamSDK.create(getActivity(), "YOUROFFLINETOKEN");
//			} catch (e: SDKExpiredException) {
//				e.printStackTrace();
//				Toast.makeText(activity?.applicationContext, "The offline SDK is expired", Toast.LENGTH_LONG).show();
//			}

            // Overwrite your specified reporting criteria to identify each scan attempt in your reports (max. 100 characters).
//			bamSDK.setMerchantReportingCriteria("YOURREPORTINGCRITERIA");

            // To restrict supported card types, pass an ArrayList of CreditCardTypes to the setSupportedCreditCardTypes method.
//			val creditCardTypes = ArrayList<CreditCardType>();
//			creditCardTypes.add(CreditCardType.VISA);
//			creditCardTypes.add(CreditCardType.MASTER_CARD);
//			creditCardTypes.add(CreditCardType.AMERICAN_EXPRESS);
//			creditCardTypes.add(CreditCardType.DINERS_CLUB);
//			creditCardTypes.add(CreditCardType.DISCOVER);
//			creditCardTypes.add(CreditCardType.CHINA_UNIONPAY);
//			creditCardTypes.add(CreditCardType.JCB);
//			bamSDK.setSupportedCreditCardTypes(creditCardTypes);

            // Expiry recognition, card holder name and CVV entry are enabled by default and can be disabled.
            // You can enable the recognition of sort code and account number.
            bamSDK.setExpiryRequired(switchOptionOne?.isChecked == true)
//			bamSDK.setCardHolderNameRequired(false);
            bamSDK.setCvvRequired(switchOptionTwo?.isChecked == true)
//			bamSDK.setSortCodeAndAccountNumberRequired(true);

            // You can show the unmasked credit card number to the user during the workflow if setCardNumberMaskingEnabled is disabled.
//			bamSDK.setCardNumberMaskingEnabled(false);

            // The user can edit the recognized expiry date if setExpiryEditable is enabled.
//			bamSDK.setExpiryEditable(true);

            // The user can edit the recognized card holder name if setCardHolderNameEditable is enabled.
//			bamSDK.setCardHolderNameEditable(true);

            // You can set a short vibration and sound effect to notify the user that the card has been detected.
            bamSDK.setVibrationEffectEnabled(true)
            bamSDK.setSoundEffect(R.raw.shutter_sound)

            // Use the following method to set the default camera position.
//			bamSDK.setCameraPosition(JumioCameraPosition.FRONT);

            // Automatically enable flash when scan is started.
//			bamSDK.setEnableFlashOnScanStart(true);

            // You can add custom fields to the confirmation page (keyboard entry or predefined values).
//			bamSDK.addCustomField("zipCodeId", getString(R.string.zip_code), InputType.TYPE_CLASS_NUMBER, "[0-9]{5,}");
//			val states = resources.getStringArray(R.array.state_selection_values)
//			val statesList = ArrayList<String>(states.size)
//			for (state in states) statesList.add(state)
//			bamSDK.addCustomField("stateId", getString(R.string.state), statesList, false, getString(R.string.state_reset_value));

            // Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
//			bamSDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID);

        } catch (e: PlatformNotSupportedException) {
            Log.e(TAG, "Error in initializeNetverifySDK: ", e)
            Toast.makeText(activity?.applicationContext, "This platform is not supported", Toast.LENGTH_LONG).show()
        } catch (e1: NullPointerException) {
            Log.e(TAG, "Error in initializeNetverifySDK: ", e1)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BamSDK.REQUEST_CODE) {

            val scanAttempts = data?.getStringArrayListExtra(BamSDK.EXTRA_SCAN_ATTEMPTS)

            if (resultCode == Activity.RESULT_OK) {
                //Handle the success case and retrieve scan data
                val cardInformation = data?.getParcelableExtra<BamCardInformation>(BamSDK.EXTRA_CARD_INFORMATION)
                cardInformation?.clear()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Handle the error cases as described in our documentation: https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_faq.md#managing-errors
                val errorMessage = data?.getStringExtra(BamSDK.EXTRA_ERROR_MESSAGE)
                val errorCode = data?.getStringExtra(BamSDK.EXTRA_ERROR_CODE)
            }

            //At this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
            //internal resources can be freed.
            bamSDK.destroy()
        }
    }
}