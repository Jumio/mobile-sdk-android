package com.jumio.sample.kotlin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jumio.auth.AuthenticationCallback
import com.jumio.auth.AuthenticationDeallocationCallback
import com.jumio.auth.AuthenticationResult
import com.jumio.auth.AuthenticationSDK
import com.jumio.core.enums.JumioDataCenter
import com.jumio.core.exceptions.MissingPermissionException
import com.jumio.core.exceptions.PlatformNotSupportedException
import com.jumio.sample.R
import kotlinx.android.synthetic.main.fragment_main.*


class AuthenticationFragment : Fragment(), View.OnClickListener, AuthenticationDeallocationCallback {

	companion object {
        private const val TAG = "JumioSDK_Authentication"
        private const val PERMISSION_REQUEST_CODE_AUTHENTICATION = 304
    }

	private var apiToken: String? = null
	private var apiSecret: String? = null
	private var dataCenter: JumioDataCenter? = null

	internal lateinit var authenticationSDK: AuthenticationSDK

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)

        apiToken = arguments?.getString(MainActivity.KEY_API_TOKEN)
        apiSecret = arguments?.getString(MainActivity.KEY_API_SECRET)
		dataCenter = arguments?.getSerializable(MainActivity.KEY_DATACENTER) as JumioDataCenter

        return rootView
    }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		tvOptions?.visibility = View.GONE
		switchOptionOne?.visibility = View.GONE
		switchOptionTwo?.visibility = View.GONE
		tilOptional?.visibility = View.VISIBLE
		btnStart?.text = java.lang.String.format(resources.getString(R.string.button_start), resources.getString(R.string.section_authentication))
		btnStart?.setOnClickListener(this)
	}

    override fun onClick(view: View) {
        //Since the Authentication is a singleton internally, a new instance is not
        //created here.
		btnStart?.isEnabled = false
        initializeAuthenticationSDK()
    }

    private fun initializeAuthenticationSDK() {
        try {
            // You can get the current SDK version using the method below.
//			AuthenticationSDK.getSDKVersion();

            // Call the method isSupportedPlatform to check if the device is supported.
            if (!AuthenticationSDK.isSupportedPlatform(activity))
                Log.w(TAG, "Device not supported")

            // Applications implementing the SDK shall not run on rooted devices. Use either the below
            // method or a self-devised check to prevent usage of SDK scanning functionality on rooted
            // devices.
            if (AuthenticationSDK.isRooted(activity))
                Log.w(TAG, "Device is rooted")

            // To create an instance of the SDK, perform the following call as soon as your activity is initialized.
            // Make sure that your merchant API token and API secret are correct and specify an instance
            // of your activity. If your merchant account is created in the EU data center, use
            // JumioDataCenter.EU instead.
            authenticationSDK = AuthenticationSDK.create(activity, apiToken, apiSecret, dataCenter)

            // Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
//			authenticationSDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID);

            // You can also set a user reference (max. 100 characters).
            // Note: The user reference should not contain sensitive data like PII (Personally Identifiable Information) or account login.
//			authenticationSDK.setUserReference("USERREFERENCE");

            // Callback URL (max. 255 characters) for the confirmation after authentication is completed. This setting overrides your Jumio merchant settings.
//			authenticationSDK.setCallbackUrl("YOURCALLBACKURL");

            // The scan reference of an eligible Netverify scan has to be used as the enrollmentTransactionReference
            authenticationSDK.setEnrollmentTransactionReference(etOptional?.text.toString())

	        // Instead an Authentication transaction can also be created via the facemap server to server API and set here
			// authenticationSDK.setAuthenticationTransactionReference("YOURAUTHENTICATIONTRANSACTIONREFERENCE")

			// Use the following method to initialize the SDK.
            if ((activity as MainActivity).checkPermissions(PERMISSION_REQUEST_CODE_AUTHENTICATION)) {
                authenticationSDK.initiate(object : AuthenticationCallback {
                    override fun onAuthenticationInitiateSuccess() {
                        try {
                            startActivityForResult(authenticationSDK.intent, AuthenticationSDK.REQUEST_CODE)
                        } catch (e: MissingPermissionException) {
                            Toast.makeText(activity?.applicationContext, e.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onAuthenticationInitiateError(errorCode: String, errorMessage: String, retryPossible: Boolean) {
                        Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
						btnStart?.isEnabled = true
                    }
                })
            } else {
				btnStart?.isEnabled = true
            }

        } catch (e: PlatformNotSupportedException) {
            Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
            Toast.makeText(activity?.applicationContext, e.message, Toast.LENGTH_LONG).show()
			btnStart?.isEnabled = true
        } catch (e: NullPointerException) {
            Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
            Toast.makeText(activity?.applicationContext, e.message, Toast.LENGTH_LONG).show()
			btnStart?.isEnabled = true
        } catch (e: MissingPermissionException) {
            Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
            Toast.makeText(activity?.applicationContext, e.message, Toast.LENGTH_LONG).show()
			btnStart?.isEnabled = true
        } catch (e : IllegalArgumentException) {
            Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
            Toast.makeText(activity?.applicationContext, e.message, Toast.LENGTH_LONG).show()
			btnStart?.isEnabled = true
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AuthenticationSDK.REQUEST_CODE) {

	        val transactionReference = data?.getStringExtra(AuthenticationSDK.EXTRA_TRANSACTION_REFERENCE)

	        if (resultCode == Activity.RESULT_OK) {
		        //Handle the success case and retrieve scan data
		        val authenticationResult = data?.getSerializableExtra(AuthenticationSDK.EXTRA_SCAN_DATA) as AuthenticationResult
            } else if (resultCode == Activity.RESULT_CANCELED) {
		        //Handle the error cases as described in our documentation: https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_faq.md#managing-errors
		        val errorMessage = data?.getStringExtra(AuthenticationSDK.EXTRA_ERROR_MESSAGE)
                val errorCode = data?.getStringExtra(AuthenticationSDK.EXTRA_ERROR_CODE)
            }

            //At this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
            //internal resources can be freed.
            authenticationSDK.destroy()
			authenticationSDK.checkDeallocation(this)
        }
    }

	override fun onAuthenticationDeallocated() {
		activity?.runOnUiThread {
			btnStart?.isEnabled = true
		}
	}
}