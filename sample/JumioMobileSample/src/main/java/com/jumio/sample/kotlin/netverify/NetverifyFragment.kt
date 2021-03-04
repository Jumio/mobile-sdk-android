package com.jumio.sample.kotlin.netverify

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
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
import com.jumio.nv.NetverifyDeallocationCallback
import com.jumio.nv.NetverifyDocumentData
import com.jumio.nv.NetverifySDK
import com.jumio.sample.R
import com.jumio.sample.kotlin.MainActivity


/**
 * Copyright 2019 Jumio Corporation All rights reserved.
 */
class NetverifyFragment : Fragment(), View.OnClickListener, NetverifyDeallocationCallback {

	companion object {
        private const val TAG = "JumioSDK_Netverify"
        private const val PERMISSION_REQUEST_CODE_NETVERIFY = 303
    }

	private var apiToken: String? = null
	private var apiSecret: String? = null
	private var dataCenter: JumioDataCenter? = null
	private var switchOptionOne: SwitchMaterial? = null
	private var switchOptionTwo: SwitchMaterial? = null
	private var btnStart: MaterialButton? = null

	private lateinit var netverifySDK: NetverifySDK

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

		switchOptionOne?.text = resources.getString(R.string.netverify_verification_enabled)
		switchOptionTwo?.text = resources.getString(R.string.netverify_identity_verification_enabled)
		switchOptionTwo?.isChecked = true
		btnStart?.text = java.lang.String.format(resources.getString(R.string.button_start), resources.getString(R.string.section_netverify))
		btnStart?.setOnClickListener(this)
	}

    override fun onClick(view: View) {
        //Since the NetverifySDK is a singleton internally, a new instance is not
        //created here.
        initializeNetverifySDK()

        if ((activity as MainActivity).checkPermissions(PERMISSION_REQUEST_CODE_NETVERIFY)) {
                try {
                if (::netverifySDK.isInitialized) {
					view.isEnabled = false
                    startActivityForResult(netverifySDK.intent, NetverifySDK.REQUEST_CODE)
                }
            } catch (e: MissingPermissionException) {
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
				view.isEnabled = true
            }

        }
    }

    private fun initializeNetverifySDK() {
        try {
            // You can get the current SDK version using the method below.
//            NetverifySDK.getSDKVersion()

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
            netverifySDK = NetverifySDK.create(activity, apiToken, apiSecret, dataCenter)

            // Use the following method to create an instance of the SDK, using offline fastfill scanning.
//			try {
//				netverifySDK = NetverifySDK.create(activity, "YOUROFFLINETOKEN", "YOURPREFERREDCOUNTRY")
//			} catch (e: SDKExpiredException) {
//				e.printStackTrace()
//				Toast.makeText(activity?.applicationContext, "The offline SDK is expired", Toast.LENGTH_LONG).show()
//				return
//			}

            // Enable ID verification to receive a verification status and verified data positions (see Callback chapter).
            // Note: Not possible for accounts configured as Fastfill only.
            netverifySDK.setEnableVerification(switchOptionOne?.isChecked == true)

            // You can specify issuing country (ISO 3166-1 alpha-3 country code) and/or ID types and/or document variant to skip
            // their selection during the scanning process.
            // Use the following method to convert ISO 3166-1 alpha-2 into alpha-3 country code.
//			val alpha3 = IsoCountryConverter.convertToAlpha3("AT")
//			netverifySDK.setPreselectedCountry("AUT")
//			val documentTypes = ArrayList<NVDocumentType>()
//			documentTypes.add(NVDocumentType.PASSPORT)
//			netverifySDK.setPreselectedDocumentTypes(documentTypes)
//			netverifySDK.setPreselectedDocumentVariant(NVDocumentVariant.PLASTIC)

            // The customer internal reference allows you to identify the scan (max. 100 characters).
            // Note: Must not contain sensitive data like PII (Personally Identifiable Information) or account login.
			netverifySDK.setCustomerInternalReference("YOURSCANREFERENCE")

            // Use the following property to identify the scan in your reports (max. 100 characters).
//			netverifySDK.setReportingCriteria("YOURREPORTINGCRITERIA")

            // You can also set a user reference (max. 100 characters).
            // Note: The user reference should not contain sensitive data like PII (Personally Identifiable Information) or account login.
//			netverifySDK.setUserReference("USERREFERENCE")

            // Callback URL for the confirmation after the verification is completed. This setting overrides your Jumio merchant settings.
//			netverifySDK.setCallbackUrl("YOURCALLBACKURL")

            // You can disable Identity Verification during the ID verification for a specific transaction.
            netverifySDK.setEnableIdentityVerification(switchOptionTwo?.isChecked == true)

            // Use the following method to set the default camera position.
//			netverifySDK.setCameraPosition(JumioCameraPosition.FRONT)

            // Use the following method to only support IDs where data can be extracted on mobile only.
//			netverifySDK.setDataExtractionOnMobileOnly(true)

            // Use the following method to explicitly send debug-info to Jumio. (default: false)
            // Only set this property to true if you are asked by our Jumio support personnel.
//			netverifySDK.sendDebugInfoToJumio(true)

            // Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
//			netverifySDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID)

			// Set watchlist screening on transaction level. Enable to override the default search, or disable watchlist screening for this transaction.
//			netverifySDK.setWatchlistScreening(NVWatchlistScreening.ENABLED)

			// Search profile for watchlist screening.
//			netverifySDK.setWatchlistSearchProfile("YOURPROFILENAME")

            // Use the following method to initialize the SDK before displaying it
//			netverifySDK.initiate(object : NetverifyInitiateCallback {
//				override fun onNetverifyInitiateSuccess() {}
//				override fun onNetverifyInitiateError(errorCode: String, errorMessage: String, retryPossible: Boolean) {}
//			})

        } catch (e: PlatformNotSupportedException) {
            Log.e(TAG, "Error in initializeNetverifySDK: ", e)
            Toast.makeText(activity?.applicationContext, e.message, Toast.LENGTH_LONG).show()
        } catch (e1: NullPointerException) {
            Log.e(TAG, "Error in initializeNetverifySDK: ", e1)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NetverifySDK.REQUEST_CODE) {

	        val scanReference = data?.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE)

	        if (resultCode == Activity.RESULT_OK) {
		        //Handle the success case and retrieve scan data
		        val documentData = data?.getParcelableExtra<Parcelable>(NetverifySDK.EXTRA_SCAN_DATA) as? NetverifyDocumentData
                val mrzData = documentData?.mrzData
            } else if (resultCode == Activity.RESULT_CANCELED) {
	            //Handle the error cases as highlighted in our documentation: https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_faq.md#managing-errors
                val errorMessage = data?.getStringExtra(NetverifySDK.EXTRA_ERROR_MESSAGE)
                val errorCode = data?.getStringExtra(NetverifySDK.EXTRA_ERROR_CODE)
            }

            //At this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
            //internal resources can be freed.
            netverifySDK.destroy()
			netverifySDK.checkDeallocation(this)
        }
    }

	override fun onNetverifyDeallocated() {
		activity?.runOnUiThread {
			btnStart?.isEnabled = true
		}
	}
}