package com.jumio.sample.kotlin

import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.jumio.MobileSDK
import com.jumio.auth.AuthenticationCallback
import com.jumio.auth.AuthenticationResult
import com.jumio.auth.AuthenticationSDK
import com.jumio.auth.custom.AuthenticationCustomSDKController
import com.jumio.auth.custom.AuthenticationCustomSDKInterface
import com.jumio.auth.custom.AuthenticationCustomScanInterface
import com.jumio.auth.custom.AuthenticationCustomScanView
import com.jumio.commons.log.Log
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
class AuthenticationCustomFragment : Fragment(), View.OnClickListener {

    private var apiToken: String? = null
    private var apiSecret: String? = null

    private lateinit var authenticationSDK: AuthenticationSDK

    private var customSDKController: AuthenticationCustomSDKController? = null
    private var successDrawable: Drawable? = null
    private var errorDrawable: Drawable? = null

    private lateinit var startCustomScanButton: Button
    private lateinit var cancelCustomScanButton: Button
    private lateinit var customScanView: AuthenticationCustomScanView
    private lateinit var customScanContainer: LinearLayout

    private val isPortrait: Boolean
        get() {
            val display = activity!!.windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.y > size.x
        }


    private val isSDKControllerValid: Boolean
        get() = customSDKController != null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_authentication_custom, container, false)

        val args = arguments

        apiToken = args!!.getString(MainActivity.KEY_API_TOKEN)
        apiSecret = args.getString(MainActivity.KEY_API_SECRET)

        successDrawable = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.success))
        errorDrawable = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.error))

        customScanView = rootView.findViewById(R.id.authenticationCustomScanView)
        customScanContainer = rootView.findViewById(R.id.authenticationCustomContainer)
        startCustomScanButton = rootView.findViewById(R.id.startAuthenticationCustomButton)
        cancelCustomScanButton = rootView.findViewById(R.id.stopAuthenticationCustomButton)
        startCustomScanButton.text = java.lang.String.format(resources.getString(R.string.button_start), resources.getString(R.string.section_authentication_custom))

        initScanView()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCustomScanButton.setOnClickListener(this)
        cancelCustomScanButton.setOnClickListener(this)
        faceButton.setOnClickListener(this)
        errorRetryButton.setOnClickListener(this)
        partRetryButton.setOnClickListener(this)

    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        initScanView()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!isVisibleToUser) {
            stopCustomScanIfActiv()
        }
    }

    override fun onPause() {
        try {
            if (customSDKController != null)
                customSDKController!!.pause()
        } catch (e: SDKNotConfiguredException) {
            e.printStackTrace()
        }

        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        try {
            if (customSDKController != null)
                customSDKController!!.resume()
        } catch (e: SDKNotConfiguredException) {
            e.printStackTrace()
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            if (customSDKController != null)
                customSDKController!!.destroy()
        } catch (e: SDKNotConfiguredException) {
            e.printStackTrace()
        }

    }

    private fun initScanView() {
        val isPortrait = isPortrait
        val params = FrameLayout.LayoutParams(if (isPortrait) FrameLayout.LayoutParams.MATCH_PARENT else FrameLayout.LayoutParams.WRAP_CONTENT, if (isPortrait) FrameLayout.LayoutParams.WRAP_CONTENT else ScreenUtil.dpToPx(activity!!, 300))
        customScanView.layoutParams = params
    }

    override fun onClick(v: View) {
        v.isEnabled = false

        if (v === startCustomScanButton) {
            if (!MobileSDK.hasAllRequiredPermissions(activity)) {
                ActivityCompat.requestPermissions(activity!!, MobileSDK.getMissingPermissions(activity), PERMISSION_REQUEST_CODE_AUTHENTICATION_CUSTOM)
            } else {
                authenticationSettingsContainer!!.visibility = View.GONE
                customScanContainer.visibility = View.VISIBLE
                showView(false, loadingIndicator)
                callbackLog!!.removeAllViews()

                initializeAuthenticationSDK()

            }
        } else if (v === cancelCustomScanButton && isSDKControllerValid) {
            hideView(false, cancelCustomScanButton, partTypeLayout, customScanLayout, loadingIndicator)
            callbackLog!!.removeAllViews()
            try {
                customSDKController!!.pause()
                customSDKController!!.destroy()
            } catch (e: SDKNotConfiguredException) {
                addToCallbackLog(e.message)
            }

            authenticationSDK.destroy()

            customSDKController = null
            customScanContainer.visibility = View.GONE
            authenticationSettingsContainer!!.visibility = View.VISIBLE
        } else if (v === faceButton && isSDKControllerValid) {
            showView(true, customScanLayout)

            scrollView!!.post {
                scrollView!!.scrollTo(0, customScanLayout!!.top)
                try {
                    customSDKController!!.startScan(customScanView, AuthenticationCustomScanImpl())
                    addToCallbackLog("help text: " + customSDKController!!.helpText)
                } catch (e: SDKNotConfiguredException) {
                    addToCallbackLog(e.message)
                }
            }
        } else if (v === partRetryButton && isSDKControllerValid) {
            hideView(false, partRetryButton!!)

            scrollView!!.post {
                scrollView!!.scrollTo(0, customScanLayout!!.top)
                customSDKController!!.retryScan()
            }
        } else if (v === errorRetryButton && isSDKControllerValid) {
            hideView(true, errorRetryButton!!)
            try {
                customSDKController!!.retry()
            } catch (e: SDKNotConfiguredException) {
                addToCallbackLog(e.message)
            }

        }

        v.isEnabled = true
    }

    private fun initializeAuthenticationSDK() {
        try {
            // You can get the current SDK version using the method below.
            // AuthenticationSDK.getSDKVersion();

            // Call the method isSupportedPlatform to check if the device is supported.
            if (!AuthenticationSDK.isSupportedPlatform(activity))
                android.util.Log.w(TAG, "Device not supported")

            // Applications implementing the SDK shall not run on rooted devices. Use either the below
            // method or a self-devised check to prevent usage of SDK scanning functionality on rooted
            // devices.
            if (AuthenticationSDK.isRooted(activity))
                android.util.Log.w(TAG, "Device is rooted")

            // To create an instance of the SDK, perform the following call as soon as your activity is initialized.
            // Make sure that your merchant API token and API secret are correct and specify an instance
            // of your activity. If your merchant account is created in the EU data center, use
            // JumioDataCenter.EU instead.
            authenticationSDK = AuthenticationSDK.create(activity, apiToken, apiSecret, JumioDataCenter.US)

            // Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
            // authenticationSDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID);

            // You can also set a customer identifier (max. 100 characters).
            // Note: The customer ID should not contain sensitive data like PII (Personally Identifiable Information) or account login.
            // authenticationSDK.setUserReference("USERREFERENCE");

            // Callback URL for the confirmation after the verification is completed. This setting overrides your Jumio merchant settings.
            // authenticationSDK.setCallbackUrl("YOURCALLBACKURL");

            // Use the following method to initialize the SDK
            authenticationSDK.initiate(etEnrollmentTransactionReference.text.toString(), object : AuthenticationCallback {
                override fun onAuthenticationInitiateSuccess() {
                    try {
                        showView(true, cancelCustomScanButton, partTypeLayout, faceButton)
                        faceButton.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null)

                        if (authenticationSDK != null) {
                            customSDKController = authenticationSDK.start(AuthenticationCustomSDKImpl())
                            customSDKController!!.resume()
                        }
                    } catch (e: NullPointerException) {
                        android.util.Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
                        Toast.makeText(activity!!.applicationContext, e.message, Toast.LENGTH_LONG).show()

                        authenticationSettingsContainer!!.visibility = View.VISIBLE
                        customScanContainer.visibility = View.GONE
                        hideView(false, loadingIndicator!!)
                    } catch (e: MissingPermissionException) {
                        android.util.Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
                        Toast.makeText(activity!!.applicationContext, e.message, Toast.LENGTH_LONG).show()
                        authenticationSettingsContainer!!.visibility = View.VISIBLE
                        customScanContainer.visibility = View.GONE
                        hideView(false, loadingIndicator!!)
                    } catch (e: SDKNotConfiguredException) {
                        android.util.Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
                        Toast.makeText(activity!!.applicationContext, e.message, Toast.LENGTH_LONG).show()
                        authenticationSettingsContainer!!.visibility = View.VISIBLE
                        customScanContainer.visibility = View.GONE
                        hideView(false, loadingIndicator!!)
                    }

                }

                override fun onAuthenticationInitiateError(errorCode: String, errorMessage: String, retryPossible: Boolean) {
                    Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()

                    authenticationSettingsContainer!!.visibility = View.VISIBLE
                    customScanContainer.visibility = View.GONE
                    hideView(false, loadingIndicator!!)
                }
            })

        } catch (e: PlatformNotSupportedException) {
            android.util.Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
            Toast.makeText(activity!!.applicationContext, e.message, Toast.LENGTH_LONG).show()
            authenticationSettingsContainer!!.visibility = View.VISIBLE
            customScanContainer.visibility = View.GONE
            hideView(false, loadingIndicator!!)
        } catch (e: NullPointerException) {
            android.util.Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
            Toast.makeText(activity!!.applicationContext, e.message, Toast.LENGTH_LONG).show()
            authenticationSettingsContainer!!.visibility = View.VISIBLE
            customScanContainer.visibility = View.GONE
            hideView(false, loadingIndicator!!)
        } catch (e: MissingPermissionException) {
            android.util.Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
            Toast.makeText(activity!!.applicationContext, e.message, Toast.LENGTH_LONG).show()
            authenticationSettingsContainer!!.visibility = View.VISIBLE
            customScanContainer.visibility = View.GONE
            hideView(false, loadingIndicator!!)
        } catch (e: IllegalArgumentException) {
            android.util.Log.e(TAG, "Error in initializeAuthenticationSDK: ", e)
            Toast.makeText(activity!!.applicationContext, e.message, Toast.LENGTH_LONG).show()
            authenticationSettingsContainer!!.visibility = View.VISIBLE
            customScanContainer.visibility = View.GONE
            hideView(false, loadingIndicator!!)
        }

    }

    private fun stopCustomScanIfActiv() {
        if (customSDKController != null) {
            cancelCustomScanButton.performClick()
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
            if (customScanLayout!!.visibility == View.VISIBLE)
                hideView(false, customScanLayout!!)
            hideView(false, loadingIndicator!!)
            faceButton!!.setCompoundDrawablesWithIntrinsicBounds(successDrawable, null, null, null)
        }

        override fun onAuthenticationScanCanceled() {
            addToCallbackLog("onAuthenticationScanCanceled")

            showView(false, partRetryButton)

            faceButton!!.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null)
        }

        override fun onAuthenticationFaceInLandscape() {
            addToCallbackLog("onAuthenticationFaceInLandscape")

            showView(false, partRetryButton)

            faceButton!!.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null)
        }
    }

    private fun showView(hideLoading: Boolean, vararg views: View) {
        if (hideLoading)
            loadingIndicator!!.visibility = View.GONE
        for (view in views)
            view.visibility = View.VISIBLE
    }

    private fun hideView(showLoading: Boolean, vararg views: View) {
        for (view in views)
            view.visibility = View.GONE
        if (showLoading)
            loadingIndicator!!.visibility = View.VISIBLE
    }

    private fun addToCallbackLog(message: String?) {
        Log.d("UI-Less", message)
        try {
            val context = activity ?: return
            val logline = TextView(context)
            logline.text = message
            callbackLog!!.addView(logline, 0)
            if (callbackLog!!.childCount > 40)
                callbackLog!!.removeViewAt(callbackLog!!.childCount - 1)
        } catch (e: Exception) {
            Log.e("UI-Less", String.format("Could not write to callback log: %s", e.message))
            Log.e("UI-Less", message)
        }

    }

    private fun appendKeyValue(key: String, value: CharSequence) {
        addToCallbackLog(String.format("%s: %s", key, value))
    }

    companion object {
        private val TAG = "AuthenticationCustom"
        private val PERMISSION_REQUEST_CODE_AUTHENTICATION_CUSTOM = 304
    }
}
