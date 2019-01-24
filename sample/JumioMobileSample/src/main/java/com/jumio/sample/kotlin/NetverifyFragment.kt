package com.jumio.sample.kotlin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jumio.core.enums.JumioDataCenter
import com.jumio.core.exceptions.MissingPermissionException
import com.jumio.core.exceptions.PlatformNotSupportedException
import com.jumio.nv.NetverifyDocumentData
import com.jumio.nv.NetverifySDK
import com.jumio.sample.R

/**
 * Copyright 2018 Jumio Corporation All rights reserved.
 */
class NetverifyFragment : Fragment(), View.OnClickListener {

    companion object {
        private val TAG = "JumioSDK_Netverify"
        private val PERMISSION_REQUEST_CODE_NETVERIFY = 303
        private var apiToken: String? = null
        private var apiSecret: String? = null
    }

    internal lateinit var switchVerification: Switch
    internal lateinit var switchFaceMatch: Switch

    internal lateinit var netverifySDK: NetverifySDK

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        switchVerification = rootView.findViewById<View>(R.id.switchOptionOne) as Switch
        switchFaceMatch = rootView.findViewById<View>(R.id.switchOptionTwo) as Switch
        switchFaceMatch.isChecked = true


        val args = arguments

        switchVerification.text = args!!.getString(MainActivity.KEY_SWITCH_ONE_TEXT)
        switchFaceMatch.text = args.getString(MainActivity.KEY_SWITCH_TWO_TEXT)

        apiToken = args.getString(MainActivity.KEY_API_TOKEN)
        apiSecret = args.getString(MainActivity.KEY_API_SECRET)

        val startSDK = rootView.findViewById<View>(R.id.btnStart) as Button
        startSDK.text = java.lang.String.format(resources.getString(R.string.button_start), resources.getString(R.string.section_netverify))
        startSDK.setOnClickListener(this)

        return rootView
    }

    override fun onClick(view: View) {
        //Since the NetverifySDK is a singleton internally, a new instance is not
        //created here.
        initializeNetverifySDK()

        if ((activity as MainActivity).checkPermissions(PERMISSION_REQUEST_CODE_NETVERIFY)) {
            try {
                if (::netverifySDK.isInitialized) {
                    startActivityForResult(netverifySDK.intent, NetverifySDK.REQUEST_CODE)
                }
            } catch (e: MissingPermissionException) {
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun initializeNetverifySDK() {
        try {
            // You can get the current SDK version using the method below.
            NetverifySDK.getSDKVersion();

            // Call the method isSupportedPlatform to check if the device is supported.
            if (!NetverifySDK.isSupportedPlatform(activity))
                Log.w(TAG, "Device not supported")

            // Applications implementing the SDK shall not run on rooted devices. Use either the below
            // method or a self-devised check to prevent usage of SDK scanning functionality on rooted
            // devices.
            if (NetverifySDK.isRooted(activity))
                Log.w(TAG, "Device is rooted")

            // To create an instance of the SDK, perform the following call as soon as your activity is initialized.
            // Make sure that your merchant API token and API secret are correct and specify an instance
            // of your activity. If your merchant account is created in the EU data center, use
            // JumioDataCenter.EU instead.
            netverifySDK = NetverifySDK.create(activity, apiToken, apiSecret, JumioDataCenter.US)

            // Use the following method to create an instance of the SDK, using offline fastfill scanning.
//            try {
//                netverifySDK = NetverifySDK.create(activity, "YOUROFFLINETOKEN", "YOURPREFERREDCOUNTRY");
//            } catch (e: SDKExpiredException) {
//                e.printStackTrace();
//                Toast.makeText(activity!!.applicationContext, "The offline SDK is expired", Toast.LENGTH_LONG).show();
//            }

            // Enable ID verification to receive a verification status and verified data positions (see Callback chapter).
            // Note: Not possible for accounts configured as Fastfill only.
            netverifySDK.setRequireVerification(switchVerification.isChecked)

            // You can specify issuing country (ISO 3166-1 alpha-3 country code) and/or ID types and/or document variant to skip
            // their selection during the scanning process.
            // Use the following method to convert ISO 3166-1 alpha-2 into alpha-3 country code.
//            val alpha3 = IsoCountryConverter.convertToAlpha3("AT");
//            netverifySDK.setPreselectedCountry("AUT");
//            val documentTypes = ArrayList<NVDocumentType>();
//            documentTypes.add(NVDocumentType.PASSPORT);
//            netverifySDK.setPreselectedDocumentTypes(documentTypes);
//            netverifySDK.setPreselectedDocumentVariant(NVDocumentVariant.PLASTIC);

            // The merchant scan reference allows you to identify the scan (max. 100 characters).
            // Note: Must not contain sensitive data like PII (Personally Identifiable Information) or account login.
//            netverifySDK.setMerchantScanReference("YOURSCANREFERENCE");

            // Use the following property to identify the scan in your reports (max. 100 characters).
//            netverifySDK.setMerchantReportingCriteria("YOURREPORTINGCRITERIA");

            // You can also set a customer identifier (max. 100 characters).
            // Note: The customer ID should not contain sensitive data like PII (Personally Identifiable Information) or account login.
//            netverifySDK.setCustomerId("CUSTOMERID");

            // Callback URL for the confirmation after the verification is completed. This setting overrides your Jumio merchant settings.
//            netverifySDK.setCallbackUrl("YOURCALLBACKURL");

            // You can disable face match during the ID verification for a specific transaction.
            netverifySDK.setRequireFaceMatch(switchFaceMatch.isChecked)

            // Use the following method to disable eMRTD scanning.
//            netverifySDK.setEnableEMRTD(false);

            // Use the following method to set the default camera position.
//            netverifySDK.setCameraPosition(JumioCameraPosition.FRONT);

            // Use the following method to only support IDs where data can be extracted on mobile only.
//            netverifySDK.setDataExtractionOnMobileOnly(true);

            // Use the following method to explicitly send debug-info to Jumio. (default: false)
            // Only set this property to true if you are asked by our Jumio support personnel.
//            netverifySDK.sendDebugInfoToJumio(true);

            // Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
//             netverifySDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID);

            // Use the following method to initialize the SDK before displaying it
//            netverifySDK.initiate(object : NetverifyInitiateCallback {
//                override fun onNetverifyInitiateSuccess() {}
//                override fun onNetverifyInitiateError(errorCode: String, errorMessage: String, retryPossible: Boolean) {}
//            })

        } catch (e: PlatformNotSupportedException) {
            Log.e(TAG, "Error in initializeNetverifySDK: ", e)
            Toast.makeText(activity!!.applicationContext, e.message, Toast.LENGTH_LONG).show()
        } catch (e1: NullPointerException) {
            Log.e(TAG, "Error in initializeNetverifySDK: ", e1)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NetverifySDK.REQUEST_CODE) {
            if (data == null)
                return
            if (resultCode == Activity.RESULT_OK) {
                val scanReference = data.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE)
                val documentData = data.getParcelableExtra<Parcelable>(NetverifySDK.EXTRA_SCAN_DATA) as? NetverifyDocumentData
                val mrzData = if (documentData != null) documentData.mrzData else null
            } else if (resultCode == Activity.RESULT_CANCELED) {
                val errorMessage = data.getStringExtra(NetverifySDK.EXTRA_ERROR_MESSAGE)
                val errorCode = data.getStringExtra(NetverifySDK.EXTRA_ERROR_CODE)
            }

            //At this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
            //internal resources can be freed.
            netverifySDK.destroy()
        }
    }
}