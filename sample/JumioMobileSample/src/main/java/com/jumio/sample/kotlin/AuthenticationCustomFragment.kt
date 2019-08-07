package com.jumio.sample.kotlin

import android.content.Context
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.jumio.MobileSDK
import com.jumio.auth.AuthenticationCallback
import com.jumio.auth.AuthenticationDeallocationCallback
import com.jumio.auth.AuthenticationResult
import com.jumio.auth.AuthenticationSDK
import com.jumio.auth.custom.AuthenticationCancelReason
import com.jumio.auth.custom.AuthenticationCustomSDKController
import com.jumio.auth.custom.AuthenticationCustomSDKInterface
import com.jumio.auth.custom.AuthenticationCustomScanInterface
import com.jumio.commons.utils.ScreenUtil
import com.jumio.core.enums.JumioDataCenter
import com.jumio.core.exceptions.MissingPermissionException
import com.jumio.core.exceptions.PlatformNotSupportedException
import com.jumio.sample.R
import com.jumio.sdk.custom.SDKNotConfiguredException
import kotlinx.android.synthetic.main.fragment_authentication_custom.*

/**
 * Copyright 2019 Jumio Corporation All rights reserved.
 */
class AuthenticationCustomFragment : Fragment(), View.OnClickListener, AuthenticationDeallocationCallback {

	companion object {
		private const val TAG = "AuthenticationCustom"
		private const val PERMISSION_REQUEST_CODE_AUTHENTICATION_CUSTOM = 304
	}

    private var apiToken: String? = null
    private var apiSecret: String? = null
	private var dataCenter: JumioDataCenter? = null

    private lateinit var authenticationSDK: AuthenticationSDK

    private var customSDKController: AuthenticationCustomSDKController? = null
    private var successDrawable: Drawable? = null
    private var errorDrawable: Drawable? = null

    private val isPortrait: Boolean
        get() {
            val display = activity?.windowManager?.defaultDisplay
            val size = Point()
            display?.getSize(size)
            return size.y > size.x
        }


    private val isSDKControllerValid: Boolean
        get() = customSDKController != null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_authentication_custom, container, false)

		apiToken = arguments?.getString(MainActivity.KEY_API_TOKEN)
        apiSecret = arguments?.getString(MainActivity.KEY_API_SECRET)
		dataCenter = arguments?.getSerializable(MainActivity.KEY_DATACENTER) as JumioDataCenter

        successDrawable = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.success))
        errorDrawable = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.error))

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

		initScanView()

		startAuthenticationCustomButton?.text = java.lang.String.format(resources.getString(R.string.button_start), resources.getString(R.string.section_authentication_custom))
        startAuthenticationCustomButton?.setOnClickListener(this)
        stopAuthenticationCustomButton?.setOnClickListener(this)
        faceButton?.setOnClickListener(this)
        errorRetryButton?.setOnClickListener(this)
        partRetryButton?.setOnClickListener(this)

		hideView(false, errorRetryButton, partRetryButton, authenticationCustomAnimationView)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        initScanView()
    }

    override fun onPause() {
        try {
            customSDKController?.pause()
        } catch (e: SDKNotConfiguredException) {
            e.printStackTrace()
        }

        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        try {
            customSDKController?.resume()
        } catch (e: SDKNotConfiguredException) {
            e.printStackTrace()
        }

    }

    override fun onDestroyView() {
        try {
			stopAuthenticationCustomButton.performClick()
			customSDKController?.destroy()
        } catch (e: SDKNotConfiguredException) {
            e.printStackTrace()
        }

		super.onDestroyView()
    }

    private fun initScanView() {
        val isPortrait = isPortrait
        val params = FrameLayout.LayoutParams(if (isPortrait) FrameLayout.LayoutParams.MATCH_PARENT else FrameLayout.LayoutParams.WRAP_CONTENT, if (isPortrait) FrameLayout.LayoutParams.WRAP_CONTENT else ScreenUtil.dpToPx(activity, 300))
        authenticationCustomScanView?.layoutParams = params
    }

    override fun onClick(v: View) {
        v.isEnabled = false
		var keepDisabled = false

        if (v === startAuthenticationCustomButton) {
            if (!MobileSDK.hasAllRequiredPermissions(activity)) {
                ActivityCompat.requestPermissions(activity!!, MobileSDK.getMissingPermissions(activity), PERMISSION_REQUEST_CODE_AUTHENTICATION_CUSTOM)
            } else {
                authenticationSettingsContainer?.visibility = View.GONE
                authenticationCustomContainer?.visibility = View.VISIBLE

				val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
				inputMethodManager.hideSoftInputFromWindow(etEnrollmentTransactionReference?.windowToken, 0)

				hideView(true, errorRetryButton, partRetryButton, authenticationCustomAnimationView)
                callbackLog?.removeAllViews()

                initializeAuthenticationSDK()
				keepDisabled = true
            }
        } else if (v === stopAuthenticationCustomButton && isSDKControllerValid) {
            hideView(false, stopAuthenticationCustomButton, partTypeLayout, customScanLayout, loadingIndicator, authenticationCustomAnimationView)
            callbackLog?.removeAllViews()
            try {
                customSDKController?.pause()
                customSDKController?.destroy()
            } catch (e: SDKNotConfiguredException) {
                addToCallbackLog(e.message)
            }

            authenticationSDK.destroy()
			authenticationSDK?.checkDeallocation(this@AuthenticationCustomFragment)

            customSDKController = null
            authenticationCustomContainer?.visibility = View.GONE
            authenticationSettingsContainer?.visibility = View.VISIBLE
        } else if (v === faceButton && isSDKControllerValid) {
            showView(true, customScanLayout)

            scrollView?.post {
                scrollView?.scrollTo(0, customScanLayout?.top ?:0)
                scrollView?.postDelayed(ScanPartRunnable(), 250)
            }
        } else if (v === partRetryButton && isSDKControllerValid) {
			authenticationCustomAnimationView?.destroy()
			hideView(false, partRetryButton, authenticationCustomAnimationView)
			showView(true, customScanLayout)

            scrollView?.post {
                scrollView?.scrollTo(0, customScanLayout?.top ?:0)
                scrollView?.postDelayed(RetryPartRunnable(), 250)
            }
        } else if (v === errorRetryButton && isSDKControllerValid) {
            hideView(true, errorRetryButton)
            try {
                customSDKController?.retry()
            } catch (e: SDKNotConfiguredException) {
                addToCallbackLog(e.message)
            }

        }
		if(!keepDisabled)
        	v.isEnabled = true
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

            // You can also set a customer identifier (max. 100 characters).
            // Note: The customer ID should not contain sensitive data like PII (Personally Identifiable Information) or account login.
//			authenticationSDK.setUserReference("USERREFERENCE");

            // Callback URL (max. 255 characters) for the confirmation after authentication is completed. This setting overrides your Jumio merchant settings.
//			authenticationSDK.setCallbackUrl("YOURCALLBACKURL");

			// The scan reference of an eligible Netverify scan has to be used as the enrollmentTransactionReference
			authenticationSDK.setEnrollmentTransactionReference(etEnrollmentTransactionReference?.text.toString())

	        // Instead an Authentication transaction can also be created via the facemap server to server API and set here
			// authenticationSDK.setAuthenticationTransactionReference("YOURAUTHENTICATIONTRANSACTIONREFERENCE")

            // Use the following method to initialize the SDK
            authenticationSDK.initiate(object : AuthenticationCallback {
                override fun onAuthenticationInitiateSuccess() {
                    try {
                        showView(true, stopAuthenticationCustomButton, partTypeLayout, faceButton)
                        faceButton?.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null)

                        if (authenticationSDK != null) {
                            customSDKController = authenticationSDK.start(AuthenticationCustomSDKImpl())
                            customSDKController?.resume()
                        }
                    } catch (e: NullPointerException) {
                        Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
                        Toast.makeText(activity?.applicationContext, e.message, Toast.LENGTH_LONG).show()

                        authenticationSettingsContainer?.visibility = View.VISIBLE
                        authenticationCustomContainer?.visibility = View.GONE
                        hideView(false, loadingIndicator)
						startAuthenticationCustomButton.isEnabled = true
                    } catch (e: MissingPermissionException) {
                        Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
                        Toast.makeText(activity?.applicationContext, e.message, Toast.LENGTH_LONG).show()
                        authenticationSettingsContainer?.visibility = View.VISIBLE
                        authenticationCustomContainer?.visibility = View.GONE
                        hideView(false, loadingIndicator)
						startAuthenticationCustomButton.isEnabled = true
                    } catch (e: SDKNotConfiguredException) {
                        Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
                        Toast.makeText(activity?.applicationContext, e.message, Toast.LENGTH_LONG).show()
                        authenticationSettingsContainer?.visibility = View.VISIBLE
                        authenticationCustomContainer?.visibility = View.GONE
                        hideView(false, loadingIndicator)
						startAuthenticationCustomButton.isEnabled = true
                    }

                }

                override fun onAuthenticationInitiateError(errorCode: String, errorMessage: String, retryPossible: Boolean) {
                    Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()

                    authenticationSettingsContainer?.visibility = View.VISIBLE
                    authenticationCustomContainer?.visibility = View.GONE
                    hideView(false, loadingIndicator)
					startAuthenticationCustomButton.isEnabled = true
                }
            })

        } catch (e: PlatformNotSupportedException) {
            Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
            Toast.makeText(activity?.applicationContext, e.message, Toast.LENGTH_LONG).show()
            authenticationSettingsContainer?.visibility = View.VISIBLE
            authenticationCustomContainer?.visibility = View.GONE
            hideView(false, loadingIndicator)
			startAuthenticationCustomButton.isEnabled = true
        } catch (e: NullPointerException) {
            Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
            Toast.makeText(activity?.applicationContext, e.message, Toast.LENGTH_LONG).show()
            authenticationSettingsContainer?.visibility = View.VISIBLE
            authenticationCustomContainer?.visibility = View.GONE
            hideView(false, loadingIndicator)
			startAuthenticationCustomButton.isEnabled = true
        } catch (e: MissingPermissionException) {
            Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
            Toast.makeText(activity?.applicationContext, e.message, Toast.LENGTH_LONG).show()
            authenticationSettingsContainer?.visibility = View.VISIBLE
            authenticationCustomContainer?.visibility = View.GONE
            hideView(false, loadingIndicator)
			startAuthenticationCustomButton.isEnabled = true
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
            Toast.makeText(activity?.applicationContext, e.message, Toast.LENGTH_LONG).show()
            authenticationSettingsContainer?.visibility = View.VISIBLE
            authenticationCustomContainer?.visibility = View.GONE
            hideView(false, loadingIndicator)
			startAuthenticationCustomButton.isEnabled = true
        }

    }

	override fun onAuthenticationDeallocated() {
		activity?.runOnUiThread {
			startAuthenticationCustomButton?.isEnabled = true
		}
	}


    private inner class ScanPartRunnable : Runnable {
        override fun run() {
            try {
				val location = IntArray(2)
				authenticationCustomScanView?.getLocationOnScreen(location)

				val rectangle = Rect()
				activity?.window?.decorView?.getWindowVisibleDisplayFrame(rectangle)

				authenticationCustomScanView?.closeButtonWidth = ScreenUtil.dpToPx(activity, 24)
				authenticationCustomScanView?.closeButtonHeight = ScreenUtil.dpToPx(activity, 24)
				authenticationCustomScanView?.closeButtonTop = location[1] - rectangle.top
				authenticationCustomScanView?.closeButtonLeft = location[0] - rectangle.left
				authenticationCustomScanView?.closeButtonResId = R.drawable.jumio_close_button

                customSDKController?.startScan(authenticationCustomScanView, AuthenticationCustomScanImpl())
                addToCallbackLog("help text: " + customSDKController?.helpText)
            } catch (e: Exception) {
                addToCallbackLog(e.message)
            }
        }
    }

	private inner class RetryPartRunnable : Runnable {
		override fun run() {
			try {

				val location = IntArray(2)
				authenticationCustomScanView?.getLocationOnScreen(location)

				val rectangle = Rect()
				activity?.window?.decorView?.getWindowVisibleDisplayFrame(rectangle)

				authenticationCustomScanView?.closeButtonWidth = ScreenUtil.dpToPx(activity, 24)
				authenticationCustomScanView?.closeButtonHeight = ScreenUtil.dpToPx(activity, 24)
				authenticationCustomScanView?.closeButtonTop = location[1] - rectangle.top
				authenticationCustomScanView?.closeButtonLeft = location[0] - rectangle.left
				authenticationCustomScanView?.closeButtonResId = R.drawable.jumio_close_button

				customSDKController?.retryScan()
			} catch (e: Exception) {
				addToCallbackLog(e.message)
			}

		}
	}

    private inner class AuthenticationCustomSDKImpl : AuthenticationCustomSDKInterface {

		override fun onAuthenticationFinished(authenticationResult: AuthenticationResult?, transactionReference: String) {
            addToCallbackLog("onAuthenticationFinished")
            hideView(false, partTypeLayout, loadingIndicator, errorRetryButton)

            appendKeyValue("Transaction reference", transactionReference)

            if (authenticationResult != null) {
                appendKeyValue("Result", authenticationResult.toString())
            }
        }

        override fun onAuthenticationError(errorCode: String, errorMessage: String, retryPossible: Boolean, transactionReference: String?) {
            showView(true, errorRetryButton)
            addToCallbackLog(String.format("onAuthenticationError: %s, %s, %d, %s", errorCode, errorMessage, if (retryPossible) 0 else 1, transactionReference
                    ?: "null"))

            if (errorCode.startsWith("M")) {
                appendKeyValue("Transaction reference", transactionReference?: "null")
                hideView(false, partTypeLayout, loadingIndicator, errorRetryButton, partRetryButton)
            }
        }
    }

    private inner class AuthenticationCustomScanImpl : AuthenticationCustomScanInterface {

        override fun onAuthenticationScanProcessing() {
            addToCallbackLog("onAuthenticationScanProcessing")
            if (customScanLayout?.visibility == View.VISIBLE)
                hideView(false, customScanLayout)
            hideView(false, loadingIndicator)
            faceButton?.setCompoundDrawablesWithIntrinsicBounds(successDrawable, null, null, null)
        }

        override fun onAuthenticationScanCanceled(cancelReason: AuthenticationCancelReason) {
			addToCallbackLog(String.format("onAuthenticationScanCanceled reason: %s helptext: %s", cancelReason.toString(), customSDKController?.helpText))

			customSDKController?.getHelpAnimation(authenticationCustomAnimationView)
			showView(false, partRetryButton, authenticationCustomAnimationView)

            faceButton?.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null)
        }

		override fun onAuthenticationScanForPartFinished() {
			addToCallbackLog("onAuthenticationScanForPartFinished")
		}

        override fun onAuthenticationFaceInLandscape() {
            addToCallbackLog("onAuthenticationFaceInLandscape")

			customSDKController?.getHelpAnimation(authenticationCustomAnimationView)
			showView(false, partRetryButton, authenticationCustomAnimationView)

            faceButton?.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null)
        }
    }

    private fun showView(hideLoading: Boolean, vararg views: View?) {
        if (hideLoading)
            loadingIndicator?.visibility = View.GONE
        for (view in views)
            view?.visibility = View.VISIBLE
    }

    private fun hideView(showLoading: Boolean, vararg views: View?) {
        for (view in views)
            view?.visibility = View.GONE
        if (showLoading)
            loadingIndicator?.visibility = View.VISIBLE
    }

    private fun addToCallbackLog(message: String?) {
	    if(message != null) {
		    Log.d("UI-Less", message)
		    try {
			    val context = activity ?: return
			    val logline = TextView(context)
			    logline.text = message
			    callbackLog?.addView(logline, 0)
			    if (callbackLog?.childCount ?: 0 > 40)
				    callbackLog?.removeViewAt(callbackLog?.childCount ?: 0 - 1)
		    } catch (e: Exception) {
			    Log.e("UI-Less", String.format("Could not write to callback log: %s", e.message))
			    Log.e("UI-Less", message)
		    }
	    }
    }

    private fun appendKeyValue(key: String, value: CharSequence) {
        addToCallbackLog(String.format("%s: %s", key, value))
    }
}
