package com.jumio.sample.kotlin

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.text.format.DateFormat
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.jumio.MobileSDK
import com.jumio.commons.utils.ScreenUtil
import com.jumio.core.data.document.ScanSide
import com.jumio.core.enums.JumioDataCenter
import com.jumio.core.exceptions.MissingPermissionException
import com.jumio.core.exceptions.PlatformNotSupportedException
import com.jumio.nv.NetverifyDeallocationCallback
import com.jumio.nv.NetverifyDocumentData
import com.jumio.nv.NetverifySDK
import com.jumio.nv.custom.*
import com.jumio.nv.data.document.NVDocumentType
import com.jumio.nv.data.document.NVDocumentVariant
import com.jumio.nv.nfc.custom.NetverifyCustomNfcAccess
import com.jumio.nv.nfc.custom.NetverifyCustomNfcInterface
import com.jumio.nv.nfc.custom.NetverifyCustomNfcPresenter
import com.jumio.sample.R
import com.jumio.sdk.custom.SDKNotConfiguredException
import kotlinx.android.synthetic.main.fragment_netverify_custom.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright 2019 Jumio Corporation All rights reserved.
 */
class NetverifyCustomFragment : Fragment(), View.OnClickListener, NetverifyDeallocationCallback {

	companion object {
		private const val TAG = "NetverifyCustom"
		private const val PERMISSION_REQUEST_CODE_NETVERIFY_CUSTOM = 303
	}

    private var apiToken: String? = null
    private var apiSecret: String? = null
    private var dataCenter: JumioDataCenter? = null

    private lateinit var netverifySDK: NetverifySDK

    private var customSDKController: NetverifyCustomSDKController? = null
    private var customScanViewPresenter: NetverifyCustomScanPresenter? = null
	private var customNfcPresenter: NetverifyCustomNfcPresenter? = null
    private var customCountryAdapter: CustomCountryAdapter? = null
    private var customDocumentAdapter: CustomDocumentAdapter? = null
    private var customVariantAdapter: CustomVariantAdapter? = null
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

    private val isScanViewControllerValid: Boolean
        get() = customScanViewPresenter != null

	private val isNfcPresenterValid: Boolean
		get() = customNfcPresenter != null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_netverify_custom, container, false)

        apiToken = arguments?.getString(MainActivity.KEY_API_TOKEN)
        apiSecret = arguments?.getString(MainActivity.KEY_API_SECRET)
		dataCenter = arguments?.getSerializable(MainActivity.KEY_DATACENTER) as JumioDataCenter

        successDrawable = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.jumio_success))
        errorDrawable = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.jumio_error))

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

		initScanView()

		startNetverifyCustomButton?.text = java.lang.String.format(resources.getString(R.string.button_start), resources.getString(R.string.section_netverify_custom))
        startNetverifyCustomButton?.setOnClickListener(this)
		stopNetverifyCustomButton?.setOnClickListener(this)
        setCountryAndDocumentType?.setOnClickListener(this)
        frontSideButton?.setOnClickListener(this)
        backSideButton?.setOnClickListener(this)
        faceButton?.setOnClickListener(this)
        stopScan?.setOnClickListener(this)
        extraction?.setOnClickListener(this)
        startFallback?.setOnClickListener(this)
        switchCamera?.setOnClickListener(this)
        takePicture?.setOnClickListener(this)
        toggleFlash?.setOnClickListener(this)
        retryScan?.setOnClickListener(this)
        confirmScan?.setOnClickListener(this)
        errorRetryButton?.setOnClickListener(this)
        partRetryButton?.setOnClickListener(this)
        finishButton?.setOnClickListener(this)
		nfcCancelButton?.setOnClickListener(this)
		nfcRetryButton?.setOnClickListener(this)
		userConsentedButton.setOnClickListener(this)

        hideView(false, countryDocumentLayout, partTypeLayout, finishButton, errorRetryButton, partRetryButton, netverifyCustomAnimationView)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        initScanView()
    }

    override fun onPause() {
        try {
            customScanViewPresenter?.pause()
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
            customScanViewPresenter?.resume()
        } catch (e: SDKNotConfiguredException) {
            e.printStackTrace()
        }

    }

	override fun onDestroyView() {
		try {
			stopScan?.performClick()
			stopNetverifyCustomButton?.performClick()
			customScanViewPresenter?.destroy()
			customSDKController?.destroy()
		} catch (e: SDKNotConfiguredException) {
			e.printStackTrace()
		}

		super.onDestroyView()
	}


	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		customSDKController?.consumeIntent(requestCode, resultCode, data)
	}

	override fun onNetverifyDeallocated() {
		activity?.runOnUiThread {
			startNetverifyCustomButton?.isEnabled = true
		}
	}

    private fun initScanView() {
        val isPortrait = isPortrait
        val params = FrameLayout.LayoutParams(if (isPortrait) FrameLayout.LayoutParams.MATCH_PARENT else FrameLayout.LayoutParams.WRAP_CONTENT, if (isPortrait) FrameLayout.LayoutParams.WRAP_CONTENT else ScreenUtil.dpToPx(activity, 300))
		netverifyCustomScanView?.layoutParams = params
		netverifyCustomScanView?.ratio = netverifyCustomScanView.minRatio
    }

    override fun onClick(v: View) {
        v.isEnabled = false
        var keepDisabled = false
        if (v === startNetverifyCustomButton) {
            if (!MobileSDK.hasAllRequiredPermissions(activity)) {
                ActivityCompat.requestPermissions(activity!!, MobileSDK.getMissingPermissions(activity), PERMISSION_REQUEST_CODE_NETVERIFY_CUSTOM)
            } else {
                netverifySettingsContainer?.visibility = View.GONE
                netverifyCustomContainer?.visibility = View.VISIBLE
				hideView(true, countryDocumentLayout, partTypeLayout, finishButton, loadingIndicator, errorRetryButton, partRetryButton)
                callbackLog?.removeAllViews()

                try {
                    initializeNetverifySDK()

                    if (::netverifySDK.isInitialized) {
                        customSDKController = netverifySDK.start(NetverifyCustomSDKImpl())
                        customSDKController?.resume()
                    }
					keepDisabled = true
                } catch (e: IllegalArgumentException) {
                    Snackbar.make(view!!, e.message ?: "", Snackbar.LENGTH_LONG).show()
                } catch (e1: SDKNotConfiguredException) {
                    Snackbar.make(view!!, e1.message ?: "", Snackbar.LENGTH_LONG).show()
                } catch (e2: MissingPermissionException) {
                    Snackbar.make(view!!, e2.message ?: "", Snackbar.LENGTH_LONG).show()
                }

            }
        } else if (v === stopNetverifyCustomButton && isSDKControllerValid) {
            hideView(false, stopNetverifyCustomButton, countryDocumentLayout, partTypeLayout, customScanLayout, customConfirmLayout, finishButton, loadingIndicator)
            callbackLog?.removeAllViews()
            try {
                customSDKController?.pause()
                customSDKController?.destroy()
            } catch (e: SDKNotConfiguredException) {
                addToCallbackLog(e.message)
            }

            netverifySDK.destroy()
			netverifySDK.checkDeallocation(this@NetverifyCustomFragment)

            customSDKController = null
            netverifyCustomContainer?.visibility = View.GONE
            netverifySettingsContainer?.visibility = View.VISIBLE
        } else if (v === setCountryAndDocumentType && isSDKControllerValid) {
            val country = customCountryAdapter?.getCountryObject(customCountrySpinner.selectedItemPosition)
            val documentType = customDocumentAdapter?.getDocumentType(customDocumentSpinner.selectedItemPosition)
            val documentVariant = customVariantAdapter?.getDocumentVariant(customVariantSpinner.selectedItemPosition)
            frontSideButton?.visibility = View.GONE
            frontSideButton?.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null)
            frontSideButton?.isEnabled = false
            backSideButton?.visibility = View.GONE
            backSideButton?.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null)
            backSideButton?.isEnabled = false
            faceButton?.visibility = View.GONE
            faceButton?.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null)
            faceButton?.isEnabled = false

            var sides: List<ScanSide>? = null
            try {
                sides = customSDKController?.setDocumentConfiguration(country, documentType, documentVariant)

                for (side in sides!!) {
                    when (side) {
                        ScanSide.FRONT -> frontSideButton?.visibility = View.VISIBLE
                        ScanSide.BACK -> backSideButton?.visibility = View.VISIBLE
                        ScanSide.FACE -> faceButton?.visibility = View.VISIBLE
                    }
                }
                showView(true, partTypeLayout)
            } catch (e: Exception) {
                e.printStackTrace()
            }
		} else if (v === userConsentedButton && isSDKControllerValid) {
			customSDKController?.setUserConsented()
			hideView(false, userConsentLayout)
        } else if ((v === frontSideButton || v === backSideButton || v === faceButton) && isSDKControllerValid) {
			netverifyCustomScanView?.mode = if (v === faceButton) NetverifyCustomScanView.MODE_FACE else NetverifyCustomScanView.MODE_ID
            initScanView()

            showView(true, customScanLayout, netverifyCustomScanView)

            scrollView?.post {
                scrollView?.scrollTo(0, customScanLayout?.top ?: 0)
                scrollView?.postDelayed(ScanPartRunnable(v), 250)
            }
            keepDisabled = true
        } else if (v === stopScan && isScanViewControllerValid) {
            customScanViewPresenter?.stopScan()
            hideView(false, customScanLayout)
            frontSideButton?.isEnabled = true
            backSideButton?.isEnabled = true
            faceButton?.isEnabled = true
            customScanViewPresenter?.destroy()
            customScanViewPresenter = null

			netverifyCustomAnimationView?.destroy()
            hideView(false, partRetryButton, netverifyCustomAnimationView)
        } else if (v === extraction && isScanViewControllerValid) {
            if (extraction?.isChecked == true)
                customScanViewPresenter?.resumeExtraction()
            else
                customScanViewPresenter?.pauseExtraction()
        } else if (v === startFallback && isScanViewControllerValid) {
            if (customScanViewPresenter?.isFallbackAvailable == true) {
                customScanViewPresenter?.startFallback()
                //startFallback could result in an onNetverifyScanForPartFinished if the part is not mandatory
                //therefore check if the customScanViewPresenter is null!
                if (isScanViewControllerValid)
                    addToCallbackLog("start fallback: " + customScanViewPresenter?.scanMode)
                keepDisabled = true
            }
        } else if (v === switchCamera && isScanViewControllerValid) {
            if (customScanViewPresenter?.hasMultipleCameras() == true)
                customScanViewPresenter?.switchCamera()
        } else if (v === takePicture && isScanViewControllerValid) {
            if (customScanViewPresenter?.showShutterButton() == true)
                customScanViewPresenter?.takePicture()
        } else if (v === toggleFlash && isScanViewControllerValid) {
            if (customScanViewPresenter?.hasFlash() == true)
                customScanViewPresenter?.toggleFlash()
        } else if (v === retryScan && isScanViewControllerValid) {
            hideView(false, customConfirmLayout)
            showView(false, customScanLayout)

            customScanViewPresenter?.retryScan()
        } else if (v === confirmScan && isScanViewControllerValid) {
            hideView(true, customConfirmLayout)
            customScanViewPresenter?.confirmScan()

            if (extraction?.isChecked == false)
                return
        } else if (v === partRetryButton && isScanViewControllerValid) {
			netverifyCustomAnimationView?.destroy()
			hideView(false, partRetryButton, netverifyCustomAnimationView)

            scrollView?.post {
                scrollView?.scrollTo(0, customScanLayout?.top ?: 0)
                scrollView?.postDelayed(RetryPartRunnable(), 250)
            }
        } else if (v === errorRetryButton && isSDKControllerValid) {
            hideView(true, errorRetryButton)
            try {
                customSDKController?.retry()
            } catch (e: SDKNotConfiguredException) {
                addToCallbackLog(e.message)
            }
        } else if (v === finishButton && isSDKControllerValid) {
            try {
                showView(false, loadingIndicator)
                customSDKController?.finish()
                keepDisabled = true
            } catch (e: SDKNotConfiguredException) {
                addToCallbackLog(e.message)
            }
        } else if (v === nfcRetryButton && isNfcPresenterValid) {
			try {
				if(customNfcAccessLayout?.visibility == View.VISIBLE) {
					val nfcAccessData = NetverifyCustomNfcAccess()
					nfcAccessData.dateOfBirth = dateOfBirthEditText?.tag as Date?
					nfcAccessData.dateOfExpiry = dateOfExpiryEditText?.tag as Date?
					nfcAccessData.idNumber = idNumberEditText?.text.toString()
					customNfcPresenter?.updateAccessData(nfcAccessData)
					hideView(true, customNfcAccessLayout)
				}

				customNfcPresenter?.retry()
				hideView(true, customNfcLayout)
			} catch (e: NullPointerException) {
				addToCallbackLog(e.message)
			}
		} else if (v === nfcCancelButton && isNfcPresenterValid) {
			customNfcPresenter?.cancel()

			hideView(false, customNfcAccessLayout)
			hideView(false, customNfcLayout)

		}

        if (!keepDisabled)
            v.isEnabled = true
    }


    private fun initializeNetverifySDK() {
        try {
            // You can get the current SDK version using the method below.
//			NetverifySDK.getSDKVersion();

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
//				netverifySDK = NetverifySDK.create(getActivity(), "YOUROFFLINETOKEN", "YOURPREFERREDCOUNTRY");
//			} catch (SDKExpiredException e) {
//				e.printStackTrace();
//				Toast.makeText(getActivity().getApplicationContext(), "The offline SDK is expired", Toast.LENGTH_LONG).show();
//			}

            // Enable ID verification to receive a verification status and verified data positions (see Callback chapter).
            // Note: Not possible for accounts configured as Fastfill only.
            netverifySDK.setEnableVerification(switchVerification?.isChecked == true)

            // You can specify issuing country (ISO 3166-1 alpha-3 country code) and/or ID types and/or document variant to skip
            // their selection during the scanning process.
            // Use the following method to convert ISO 3166-1 alpha-2 into alpha-3 country code.
//			val alpha3: String = IsoCountryConverter.convertToAlpha3("AT")
//			netverifySDK.setPreselectedCountry("AUT")
//			val documentTypes = ArrayList<NVDocumentType>()
//			documentTypes.add(NVDocumentType.PASSPORT)
//			netverifySDK.setPreselectedDocumentTypes(documentTypes)
//			netverifySDK.setPreselectedDocumentVariant(NVDocumentVariant.PLASTIC)

            // The customer internal reference allows you to identify the scan (max. 100 characters).
            // Note: Must not contain sensitive data like PII (Personally Identifiable Information) or account login.
//			netverifySDK.setCustomerInternalReference("YOURSCANREFERENCE");

            // Use the following property to identify the scan in your reports (max. 100 characters).
//			netverifySDK.setReportingCriteria("YOURREPORTINGCRITERIA");

            // You can also set a user reference (max. 100 characters).
            // Note: The user reference should not contain sensitive data like PII (Personally Identifiable Information) or account login.
//			netverifySDK.setUserReference("USERREFERENCE");

            // Callback URL (max. 255 characters) for the confirmation after the verification is completed. This setting overrides your Jumio merchant settings.
//			netverifySDK.setCallbackUrl("YOURCALLBACKURL")

            // You can disable Identity Verification during the ID verification for a specific transaction.
            netverifySDK.setEnableIdentityVerification(switchIdentitiyVerification?.isChecked == true)

            // Use the following method to disable eMRTD scanning.
//			netverifySDK.setEnableEMRTD(false)

            // Use the following method to set the default camera position.
//			netverifySDK.setCameraPosition(JumioCameraPosition.FRONT)

            // Use the following method to only support IDs where data can be extracted on mobile only.
//			netverifySDK.setDataExtractionOnMobileOnly(true)

            // Use the following method to explicitly send debug-info to Jumio. (default: false)
            // Only set this property to true if you are asked by our Jumio support personnel.
//			netverifySDK.sendDebugInfoToJumio(true)

            // Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
//			netverifySDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID);

			// Set watchlist screening on transaction level. Enable to override the default search, or disable watchlist screening for this transaction.
//			netverifySDK.setWatchlistScreening(NVWatchlistScreening.ENABLED);

			// Search profile for watchlist screening.
//			netverifySDK.setWatchlistSearchProfile("YOURPROFILENAME");

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

    private inner class ScanPartRunnable(private val view: View) : Runnable {

        override fun run() {
            try {
                var scanSide = ScanSide.FRONT
				if (view === backSideButton) {
					scanSide = ScanSide.BACK
				} else if (view === faceButton) {
					scanSide = ScanSide.FACE

					val location = IntArray(2)
					stopScan?.getLocationOnScreen(location)

					val rectangle = Rect()
					activity?.window?.decorView?.getWindowVisibleDisplayFrame(rectangle)

					netverifyCustomScanView?.closeButtonWidth = stopScan?.width ?:0
					netverifyCustomScanView?.closeButtonHeight = stopScan?.height ?:0
					netverifyCustomScanView?.closeButtonTop = location[1] - rectangle.top
					netverifyCustomScanView?.closeButtonLeft = location[0] - rectangle.left
					netverifyCustomScanView?.closeButtonResId = R.drawable.jumio_close_button
				}

                customScanViewPresenter = customSDKController?.startScanForPart(scanSide, netverifyCustomScanView, netverifyCustomConfirmationView, NetverifyCustomScanImpl())

                frontSideButton?.isEnabled = false
                backSideButton?.isEnabled = false
                faceButton?.isEnabled = false

                switchCamera?.isEnabled = false
                takePicture?.isEnabled = false
                toggleFlash?.isEnabled = false
                startFallback?.isEnabled = false
                extraction?.isChecked = true
                addToCallbackLog("start scanmode: " + customScanViewPresenter?.scanMode)
                addToCallbackLog("help text: " + customScanViewPresenter?.helpText)
                startFallback?.isEnabled = customScanViewPresenter?.isFallbackAvailable == true

            } catch (e: SDKNotConfiguredException) {
				hideView(false, customScanLayout, netverifyCustomScanView)

                addToCallbackLog(e.message)
                frontSideButton?.isEnabled = true
                backSideButton?.isEnabled = true
                faceButton?.isEnabled = true
            }

        }
    }

	private inner class RetryPartRunnable() : Runnable {

		override fun run() {
			try {
				if (customScanViewPresenter?.scanMode == NetverifyScanMode.FACE) {
					val location = IntArray(2)
					stopScan?.getLocationOnScreen(location)

					val rectangle = Rect()
					activity?.window?.decorView?.getWindowVisibleDisplayFrame(rectangle)

					netverifyCustomScanView?.closeButtonWidth = stopScan?.width ?:0
					netverifyCustomScanView?.closeButtonHeight = stopScan?.height ?:0
					netverifyCustomScanView?.closeButtonTop = location[1] - rectangle.top
					netverifyCustomScanView?.closeButtonLeft = location[0] - rectangle.left
					netverifyCustomScanView?.closeButtonResId = R.drawable.jumio_close_button
				}

				customScanViewPresenter?.retryScan()

			} catch (e: Exception) {
				addToCallbackLog(e.message)
			}
		}
	}

    private inner class NetverifyCustomSDKImpl : NetverifyCustomSDKInterface {
		//Custom SDK Interface
        override fun onNetverifyCountriesReceived(countryList: HashMap<String, NetverifyCountry>, userCountryCode: String) {
            addToCallbackLog("onNetverifyCountriesReceived - user Country is $userCountryCode")
            if(stopNetverifyCustomButton == null || countryDocumentLayout == null)
                return
            showView(true, stopNetverifyCustomButton, countryDocumentLayout)
            val context = activity ?: return
            customCountryAdapter = CustomCountryAdapter(context, countryList)
            customCountrySpinner?.adapter = customCountryAdapter
            customCountrySpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val country: NetverifyCountry? = customCountryAdapter?.getCountryObject(position)
                    customDocumentAdapter = CustomDocumentAdapter(context, country!!.documentTypes)
                    customDocumentSpinner?.adapter = customDocumentAdapter
                    customDocumentSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            hideView(false, partTypeLayout, customScanLayout, customConfirmLayout, finishButton, loadingIndicator)
                            val selectedCountry = customCountryAdapter?.getCountryObject(customCountrySpinner.selectedItemPosition)
                            val documentType = customDocumentAdapter?.getDocumentType(position)
                            customVariantAdapter = CustomVariantAdapter(context, selectedCountry!!.getDocumentVariants(documentType))
                            customVariantSpinner?.adapter = customVariantAdapter
                            customVariantSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                    hideView(false, partTypeLayout, customScanLayout, customConfirmLayout, finishButton, loadingIndicator)
                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {

                                }
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {

                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }

		override fun onNetverifyUserConsentRequried(privacyPolicy: String?) {
			showView(true, userConsentLayout)
			userConsentUrl?.text = privacyPolicy
			Linkify.addLinks(userConsentUrl, Linkify.WEB_URLS);
			userConsentUrl?.movementMethod = LinkMovementMethod.getInstance()
		}

        override fun onNetverifyResourcesLoaded() {
            addToCallbackLog("onNetverifyResourcesLoaded")
            frontSideButton?.isEnabled = true
            backSideButton?.isEnabled = true
            faceButton?.isEnabled = true
        }

        override fun onNetverifyFinished(documentData: NetverifyDocumentData?, scanReference: String) {
            addToCallbackLog("onNetverifyFinished")
            hideView(false, countryDocumentLayout, partTypeLayout, finishButton, loadingIndicator, errorRetryButton)

            appendKeyValue("Scan reference", scanReference)

            if (documentData != null) {
                //Dont change the key strings - they are needed for the qa automation
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
				dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                appendKeyValue("Selected country", documentData.selectedCountry)
                appendKeyValue("Selected document type", if (documentData.selectedDocumentType == null) "" else documentData.selectedDocumentType.name)
                appendKeyValue("ID number", documentData.idNumber)
                appendKeyValue("Personal number", documentData.personalNumber)
                appendKeyValue("OptData1", documentData.optionalData1)
                appendKeyValue("OptData2", documentData.optionalData2)
                appendKeyValue("Issue date", if (documentData.issuingDate == null) null else dateFormat.format(documentData.issuingDate))
                appendKeyValue("Expiry date", if (documentData.expiryDate == null) null else dateFormat.format(documentData.expiryDate))
                appendKeyValue("Issuing country", documentData.issuingCountry)
                appendKeyValue("Last name", documentData.lastName)
                appendKeyValue("First name", documentData.firstName)
                appendKeyValue("Date of birth", if (documentData.dob == null) null else dateFormat.format(documentData.dob))
                appendKeyValue("Gender", if (documentData.gender == null) null else documentData.gender.toString())
                appendKeyValue("Originating country", documentData.originatingCountry)
                appendKeyValue("Address line", documentData.addressLine)
                appendKeyValue("City", documentData.city)
                appendKeyValue("Subdivision", documentData.subdivision)
                appendKeyValue("Post code", documentData.postCode)

                val mrz = documentData.mrzData
                if (mrz != null) {
                    appendKeyValue("MRZ Validation: ", "")
                    appendKeyValue("   MRZ Type: ", mrz.format.toString())
                    appendKeyValue("   Line1", mrz.mrzLine1)
                    appendKeyValue("   Line2", mrz.mrzLine2)
                    if (mrz.mrzLine3 != null) {
                        appendKeyValue("   Line3", mrz.mrzLine3)
                    }
                    appendKeyValue("   idNumberValid()", "" + mrz.idNumberValid())
                    appendKeyValue("   dobValid()", "" + mrz.dobValid())
                    appendKeyValue("   personalNumberValid()", "" + mrz.personalNumberValid())
                    appendKeyValue("   expiryDateValid()", "" + mrz.expiryDateValid())
                    appendKeyValue("   compositeValid()", "" + mrz.compositeValid())

                }
            }
        }

        override fun onNetverifyError(errorCode: String, errorMessage: String, retryPossible: Boolean, scanReference: String?) {
            hideView(true, finishButton)
            showView(true, errorRetryButton)
            addToCallbackLog(String.format("onNetverifyError: %s, %s, %d, %s", errorCode, errorMessage, if (retryPossible) 0 else 1, scanReference
                    ?: "null"))
        }
	}

    private inner class NetverifyCustomScanImpl : NetverifyCustomScanInterface {

		override fun onNetverifyScanForPartFinished(scanSide: ScanSide, allPartsScanned: Boolean) {
            customScanViewPresenter?.destroy()
            customScanViewPresenter = null
            frontSideButton?.isEnabled = true
            backSideButton?.isEnabled = true
            faceButton?.isEnabled = true
            addToCallbackLog("onNetverifyScanForPartFinished")
            if (customScanLayout?.visibility == View.VISIBLE)
                hideView(false, customScanLayout)
            if (customConfirmLayout?.visibility == View.VISIBLE)
                hideView(false, customConfirmLayout)
            hideView(false, loadingIndicator)
            when (scanSide) {
                ScanSide.FRONT -> frontSideButton?.setCompoundDrawablesWithIntrinsicBounds(successDrawable, null, null, null)
                ScanSide.BACK -> backSideButton?.setCompoundDrawablesWithIntrinsicBounds(successDrawable, null, null, null)
                ScanSide.FACE -> faceButton?.setCompoundDrawablesWithIntrinsicBounds(successDrawable, null, null, null)
            }
            if (allPartsScanned) {
                finishButton?.isEnabled = true
                showView(true, finishButton)
            }
        }

        //Custom ScanView Interface
        override fun onNetverifyCameraAvailable() {
            addToCallbackLog("onNetverifyCameraAvailable")
            switchCamera?.isEnabled = customScanViewPresenter?.hasMultipleCameras() == true
            takePicture?.isEnabled = customScanViewPresenter?.showShutterButton() == true
            toggleFlash?.isEnabled = customScanViewPresenter?.hasFlash() == true
            stopScan?.isEnabled = true
            extraction?.isEnabled = true
        }

        override fun onNetverifyExtractionStarted() {
            addToCallbackLog("onNetverifyExtractionStarted")
        }

        override fun onNetverifyPresentConfirmationView(confirmationType: NetverifyConfirmationType) {
			addToCallbackLog(String.format("onNetverifyPresentConfirmationView %s", confirmationType.toString()))
            hideView(true, customScanLayout)
            showView(true, customConfirmLayout)
        }

        override fun onNetverifyNoUSAddressFound() {
            addToCallbackLog("onNetverifyNoUsAddressFound")
        }

        override fun onNetverifyFaceInLandscape() {
            addToCallbackLog("onNetverifyFaceInLandscape")

			customScanViewPresenter?.getHelpAnimation(netverifyCustomAnimationView)
            showView(false, partRetryButton, netverifyCustomAnimationView)
        }

        override fun onNetverifyShowLegalAdvice(legalAdvice: String) {
            addToCallbackLog("onNetverifyShowLegalAdvice")
            addToCallbackLog(legalAdvice)
        }

        override fun onNetverifyDisplayBlurHint() {
            addToCallbackLog("onNetverifyDisplayBlurHint")
        }
        
        override fun onNetverifyScanForPartCanceled(scanSide: ScanSide?, cancelReason: NetverifyCancelReason?) {
			addToCallbackLog(String.format("onNetverifyScanForPartCanceled scanSide: %s reason: %s helptext: %s", scanSide.toString(), cancelReason.toString(), customScanViewPresenter?.helpText))

			if(scanSide == ScanSide.FACE) {
				customScanViewPresenter?.getHelpAnimation(netverifyCustomAnimationView)
				showView(false, netverifyCustomAnimationView)
			}
			showView(false, partRetryButton)
        }

		override fun getNetverifyCustomNfcInterface(): NetverifyCustomNfcInterface {
			addToCallbackLog("getNetverifyCustomNfcInterface")

			return NetverifyCustomNfcImpl()
		}

		override fun onNetverifyStartNfcExtraction(netverifyCustomNfcPresenter: NetverifyCustomNfcPresenter?) {
			addToCallbackLog("Waiting for eMrtd Document..")

			hideView(false, nfcRetryButton)
			showView(false, customNfcLayout)

			customNfcPresenter = netverifyCustomNfcPresenter
		}
    }

	private inner class NetverifyCustomNfcImpl : NetverifyCustomNfcInterface {

		override fun onNetverifyNfcStarted() {
			addToCallbackLog("onNetverifyNfcStarted")

			hideView(true, customNfcLayout)
		}

		override fun onNetverifyNfcUpdate(progress: Int) {
			addToCallbackLog(String.format("onNetverifyNfcUpdate %d", progress))
		}

		override fun onNetverifyNfcFinished() {
			addToCallbackLog("onNetverifyNfcFinished")
		}

		override fun onNetverifyNfcSystemSettings() {
			addToCallbackLog("Please enable NFC in your system settings")

			showView(true, customNfcLayout, nfcRetryButton)
		}

		override fun onNetverifyNfcError(errorMessage: String?, retryable: Boolean, accessUpdate: Boolean, nfcAccessData: NetverifyCustomNfcAccess?) {
			addToCallbackLog("onNetverifyNfcError "+errorMessage)

			showView(true, customNfcLayout, nfcRetryButton)

			if(accessUpdate) {
				val dateFormat = DateFormat.getDateFormat(activity)
				dateFormat.timeZone = TimeZone.getTimeZone("UTC")

				dateOfBirthEditText?.setText(dateFormat.format(nfcAccessData?.dateOfBirth))
				dateOfBirthEditText?.tag = nfcAccessData?.dateOfBirth
				dateOfBirthEditText?.setOnClickListener(DatePickerListener(DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
					val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH)
					calendar.set(Calendar.YEAR, year)
					calendar.set(Calendar.MONTH, monthOfYear)
					calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

					dateOfBirthEditText?.setText(dateFormat.format(calendar.time))
					dateOfBirthEditText?.tag = calendar.time
				}, nfcAccessData?.dateOfBirth))

				dateOfExpiryEditText?.setText(dateFormat.format(nfcAccessData?.dateOfExpiry))
				dateOfExpiryEditText?.tag = nfcAccessData?.dateOfExpiry
				dateOfExpiryEditText?.setOnClickListener(DatePickerListener(DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
					val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH)
					calendar.set(Calendar.YEAR, year)
					calendar.set(Calendar.MONTH, monthOfYear)
					calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

					dateOfExpiryEditText?.setText(dateFormat.format(calendar.time))
					dateOfExpiryEditText?.tag = calendar.time
				}, nfcAccessData?.dateOfExpiry))

				val filters = ArrayList(Arrays.asList<InputFilter>(*idNumberEditText.getFilters()))
				filters.add(0, InputFilter.AllCaps())
				filters.add(1, AlphanumInputfilter())
				idNumberEditText.setFilters(filters.toTypedArray())
				idNumberEditText.setText(nfcAccessData?.idNumber)
				showView(true, customNfcAccessLayout)
			}
		}
	}

    private fun showView(hideLoading: Boolean, vararg views: View?) {
        if (hideLoading)
            loadingIndicator?.visibility = View.GONE
        for (view in views)
            view?.visibility = View.VISIBLE
    }

    private fun hideView(showLoading: Boolean, vararg views: View?) {
        for (view in views) {
			view?.visibility = View.GONE
		}
        if (showLoading) {
			loadingIndicator?.visibility = View.VISIBLE
		}
    }

    private fun addToCallbackLog(message: String?) {
        if (message != null) {
            Log.d("UI-Less", message)
            try {
                val context = activity ?: return
                val logline = TextView(context)
                logline.text = message
                callbackLog?.addView(logline, 0)
                if (callbackLog?.childCount ?:0 > 40)
                    callbackLog?.removeViewAt(callbackLog?.childCount ?:0 - 1)
            } catch (e: Exception) {
                Log.e("UI-Less", String.format("Could not write to callback log: %s", e.message))
                Log.e("UI-Less", message)
            }
        }
    }

    private fun appendKeyValue(key: String, value: CharSequence?) {
        addToCallbackLog(String.format("%s: %s", key, value))
    }

    private inner class CustomCountryAdapter(context: Context, val countryList: HashMap<String, NetverifyCountry>) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item) {

        init {
            val sortedCountryList = ArrayList(countryList.keys)
            Collections.sort(sortedCountryList)
            addAll(sortedCountryList)
        }

        fun getCountryObject(position: Int): NetverifyCountry? {
            return countryList[getItem(position)]
        }
    }

    private inner class CustomDocumentAdapter(context: Context, documentTypeSet: Set<NVDocumentType>) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item) {

        private val documentTypes: Array<NVDocumentType>

        init {

            documentTypes = documentTypeSet.toTypedArray()
            for (documentType in documentTypes) {
                add(documentType.name)
            }
        }

        fun getDocumentType(position: Int): NVDocumentType {
            return documentTypes[position]
        }
    }

    private inner class CustomVariantAdapter(context: Context, documentVariantSet: Set<NVDocumentVariant>) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item) {

        private val documentVariants: Array<NVDocumentVariant> = documentVariantSet.toTypedArray()

        init {

            for (documentVariant in documentVariants) {
                add(documentVariant.name)
            }
        }

        fun getDocumentVariant(position: Int): NVDocumentVariant {
            return documentVariants[position]
        }
    }

	private inner class AlphanumInputfilter : InputFilter {
		override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
			// Only keep characters that are alphanumeric
			val builder = StringBuilder()
			for (i in start until end) {
				val c = source[i]
				if (Character.isLetterOrDigit(c)) {
					builder.append(c)
				}
			}

			// If all characters are valid, return null, otherwise only return the filtered characters
			val allCharactersValid = builder.length == end - start
			return if (allCharactersValid) null else builder.toString()
		}
	}

	private inner class DatePickerListener(private val mListener: DatePickerDialog.OnDateSetListener, startDate: Date?) : View.OnClickListener {
		private val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH)

		init {
			if (startDate != null) {
				calendar.time = startDate
			}
		}

		override fun onClick(v: View) {
			DatePickerDialog(activity!!.applicationContext, R.style.Theme_AppCompat_Light_Dialog, mListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
					.show()
		}
	}
}