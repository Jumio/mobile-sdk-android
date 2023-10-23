package com.jumio.sample.kotlin.documentverification

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
import com.jumio.core.enums.JumioDataCenter
import com.jumio.core.exceptions.MissingPermissionException
import com.jumio.core.exceptions.PlatformNotSupportedException
import com.jumio.dv.DocumentVerificationSDK
import com.jumio.sample.R
import com.jumio.sample.kotlin.MainActivity

/**
 * Copyright 2019 Jumio Corporation All rights reserved.
 */
class DocumentVerificationFragment : Fragment(), View.OnClickListener {

	companion object {
		private const val TAG = "JumioSDK_DV"
		private const val PERMISSION_REQUEST_CODE_DOCUMENT_VERIFICATION = 301
	}

    private var apiToken: String? = null
    private var apiSecret: String? = null
	private var dataCenter: JumioDataCenter? = null
	private var switchOptionOne: SwitchMaterial? = null
	private var switchOptionTwo: SwitchMaterial? = null
	private var btnStart: MaterialButton? = null

    private lateinit var documentVerificationSDK: DocumentVerificationSDK

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

		switchOptionOne?.text = resources.getString(R.string.documentverification_enable_extraction)
		switchOptionOne?.isChecked = true
		switchOptionTwo?.visibility = View.GONE
		btnStart?.text = java.lang.String.format(resources.getString(R.string.button_start), resources.getString(R.string.section_documentverification))
		btnStart?.setOnClickListener(this)
	}

    override fun onClick(view: View) {
        //Since the DocumentVerificationSDK is a singleton internally, a new instance is not
        //created here.
        initializeDocumentVerificationSDK()

        if ((activity as MainActivity).checkPermissions(PERMISSION_REQUEST_CODE_DOCUMENT_VERIFICATION)) {
            try {
                startActivityForResult(documentVerificationSDK.intent, DocumentVerificationSDK.REQUEST_CODE)
            } catch (e: MissingPermissionException) {
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initializeDocumentVerificationSDK() {
        try {
            // You can get the current SDK version using the method below.
//			DocumentVerificationSDK.getSDKVersion();

            // Call the method isSupportedPlatform to check if the device is supported.
            if (!DocumentVerificationSDK.isSupportedPlatform(activity))
                Log.w(TAG, "Device not supported")

            // Applications implementing the SDK shall not run on rooted devices. Use either the below
            // method or a self-devised check to prevent usage of SDK scanning functionality on rooted
            // devices.
            if (DocumentVerificationSDK.isRooted(activity))
                Log.w(TAG, "Device is rooted")

            // To create an instance of the SDK, perform the following call as soon as your activity is initialized.
            // Make sure that your merchant API token and API secret are correct and specify an instance
            // of your activity. If your merchant account is created in the EU data center, use
            // JumioDataCenter.EU instead.
            documentVerificationSDK = DocumentVerificationSDK.create(activity, apiToken, apiSecret, dataCenter)

	        // One of the configured DocumentTypeCodes: BC, BS, CAAP, CB, CC, CCS, CRC, HCC, IC, LAG, LOAP,
	        // MEDC, MOAP, PB, SEL, SENC, SS, SSC, STUC, TAC, TR, UB, VC, VT, WWCC, CUSTOM
            documentVerificationSDK.setType("BC")

            // ISO 3166-1 alpha-3 country code
            documentVerificationSDK.setCountry("USA")

            // The customer internal reference allows you to identify the scan (max. 100 characters).
            // Note: Must not contain sensitive data like PII (Personally Identifiable Information) or account login.
            documentVerificationSDK.setCustomerInternalReference("YOURSCANREFERENCE")

            // You can also set a user reference (max. 100 characters).
            // Note: The user reference should not contain sensitive data like PII (Personally Identifiable Information) or account login.
            documentVerificationSDK.setUserReference("USERREFERENCE")

            // Set the following property to enable/disable data extraction for documents.
            documentVerificationSDK.setEnableExtraction(switchOptionOne?.isChecked == true)

            // One of the Custom Document Type Codes as configurable by Merchant in Merchant UI.
//			documentVerificationSDK.setCustomDocumentCode("YOURCUSTOMDOCUMENTCODE");

            // Overrides the label for the document name (on Help Screen below document icon)
//			documentVerificationSDK.setDocumentName("DOCUMENTNAME");

            // Use the following property to identify the scan in your reports (max. 255 characters).
//			documentVerificationSDK.setReportingCriteria("YOURREPORTINGCRITERIA");

            // Callback URL for the confirmation after the verification is completed. This setting overrides your Jumio merchant settings.
//			documentVerificationSDK.setCallbackUrl("YOURCALLBACKURL");

            // Use the following method to set the default camera position.
//			documentVerificationSDK.setCameraPosition(JumioCameraPosition.FRONT);

            // Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
//			documentVerificationSDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID);

        } catch (e: PlatformNotSupportedException) {
            Log.e(TAG, "Error in initializeNetverifySDK: ", e)
            Toast.makeText(activity?.applicationContext, "This platform is not supported", Toast.LENGTH_LONG).show()
        } catch (e1: NullPointerException) {
            Log.e(TAG, "Error in initializeNetverifySDK: ", e1)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DocumentVerificationSDK.REQUEST_CODE) {

	        val scanReference = data?.getStringArrayListExtra(DocumentVerificationSDK.EXTRA_SCAN_REFERENCE)

	        if (resultCode == Activity.RESULT_OK) {
		        //Handle the success case
            } else if (resultCode == Activity.RESULT_CANCELED) {
	            //Handle the error cases as described in our documentation: https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_faq.md#managing-errors
	            val errorMessage = data?.getStringExtra(DocumentVerificationSDK.EXTRA_ERROR_MESSAGE)
	            val errorCode = data?.getStringExtra(DocumentVerificationSDK.EXTRA_ERROR_CODE)
            }
        }
	    //At this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
	    //internal resources can be freed.
	    documentVerificationSDK.destroy()
    }
}