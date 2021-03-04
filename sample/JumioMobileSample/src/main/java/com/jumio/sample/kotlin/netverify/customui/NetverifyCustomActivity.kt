package com.jumio.sample.kotlin.netverify.customui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatDrawableManager
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.jumio.core.data.document.ScanSide
import com.jumio.core.enums.JumioDataCenter
import com.jumio.core.exceptions.MissingPermissionException
import com.jumio.core.exceptions.PlatformNotSupportedException
import com.jumio.nv.NetverifyDocumentData
import com.jumio.nv.NetverifySDK
import com.jumio.nv.custom.*
import com.jumio.nv.data.country.Country
import com.jumio.nv.data.document.NVDocumentType
import com.jumio.nv.data.document.NVDocumentVariant
import com.jumio.sample.R
import com.jumio.sample.kotlin.MainActivity
import com.jumio.sdk.custom.SDKNotConfiguredException
import java.util.*
import kotlin.collections.ArrayList

class NetverifyCustomActivity : AppCompatActivity(), BottomSheetDialogDocuments.OnBottomSheetActionListener,
		BottomSheetDialogDocumentVariant.OnBottomSheetActionListener,
		NetverifyCustomDocSelectionFragment.OnDocumentSelectionInteractionListener,
		NetverifyCustomScanFragment.OnScanFragmentInteractionListener,
		NetverifyCustomSuccessFragment.OnSuccessFragmentInteractionListener {
	private var apiToken: String? = null
	private var apiSecret: String? = null
	private var datacenter: JumioDataCenter? = null
	private var selectedCountry: NetverifyCountry? = null
	private var countryMap: Map<String, NetverifyCountry>? = null
	private var selectedDocumentType: NVDocumentType? = null
	private var selectedScanSide = 0
	private var sides: List<ScanSide>? = null

	//#####################################################
	// LIFECYCLE METHODS
	//#####################################################
	@SuppressLint("RestrictedApi")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_netverify_custom)
		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		val appBarLayout = findViewById<AppBarLayout>(R.id.netverify_custom_appBarLayout)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			appBarLayout.elevation = 0f
		}
		setSupportActionBar(toolbar)
		supportActionBar?.title = ""
		//Set up credentials
		val args = intent.extras
		if (args != null) {
			apiToken = args.getString(MainActivity.KEY_API_TOKEN)
			apiSecret = args.getString(MainActivity.KEY_API_SECRET)
			datacenter = args.getSerializable(MainActivity.KEY_DATACENTER) as JumioDataCenter?
		}
		// action bar at top of the screen
		if (supportActionBar != null) {
			supportActionBar?.setDisplayHomeAsUpEnabled(true)
			val icon = AppCompatDrawableManager.get().getDrawable(applicationContext, R.drawable.ic_arrow_back_white)
			supportActionBar?.setHomeAsUpIndicator(icon)
			supportActionBar?.setShowHideAnimationEnabled(false)
		}

		if (savedInstanceState != null && netverifySDK != null && customSDKController != null) {
			//Activity has been recreated
			netverifySDK?.recreate(this)
			customSDKController?.recreate(this, NetverifyCustomSDKImpl())

			countryMap = savedInstanceState.getSerializable(INSTANCE_COUNTRY_MAP) as Map<String, NetverifyCountry>?
			sides = savedInstanceState.getSerializable(INSTANCE_SCANSIDES) as List<ScanSide>?
			selectedCountry = savedInstanceState.getSerializable(INSTANCE_SELECTED_COUNTRY) as NetverifyCountry?
			selectedDocumentType = savedInstanceState.getSerializable(INSTANCE_SELECTED_DOCUMENTTYPE) as NVDocumentType?
			selectedScanSide = savedInstanceState.getInt(INSTANCE_SELECTED_SCANSIDE)
		} else {
			cleanupSDK()
			initializeNetverifyCustom()
		}
	}

	public override fun onPause() {
		try {
			customSDKController?.pause()
		} catch (e: SDKNotConfiguredException) {
			Log.e(TAG, "onPause: ", e)
		}
		super.onPause()
	}

	public override fun onResume() {
		try {
			customSDKController?.resume()
		} catch (e: SDKNotConfiguredException) {
			Log.e(TAG, "onResume: ", e)
		}
		super.onResume()
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)

		outState.putSerializable(INSTANCE_SELECTED_COUNTRY, selectedCountry)
		outState.putSerializable(INSTANCE_COUNTRY_MAP, countryMap as java.io.Serializable?)
		outState.putSerializable(INSTANCE_SELECTED_DOCUMENTTYPE, selectedDocumentType)
		outState.putSerializable(INSTANCE_SCANSIDES, sides as java.io.Serializable?)
		outState.putInt(INSTANCE_SELECTED_SCANSIDE, selectedScanSide)
	}

	public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		customSDKController?.consumeIntent(requestCode, resultCode, data)
	}

	//#####################################################
	// FRAGMENT CALLBACKS
	//#####################################################
	/**
	 * Specific kind of document has been selected.
	 *
	 * @param documentType    specifies what type of document (passport, driver's license, etc.)
	 * @param documentVariant specifies if the document is paper or plastic
	 */
	override fun onDocumentTypeSelected(documentType: NVDocumentType?, documentVariant: NVDocumentVariant?, variantSelected: Boolean?) {
		try {
			selectedDocumentType = documentType

			if (selectedCountry!!.getDocumentVariants(documentType).size > 1 && !variantSelected!!) {
				val bottomSheetFragment = BottomSheetDialogDocumentVariant()
				val bundle = Bundle()
				bundle.putSerializable(BUNDLE_DOCUMENT_TYPE, selectedDocumentType)
				bottomSheetFragment.arguments = bundle
				bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
			} else {
				// sides refers to how many sides of the document need to be scanned (front, back or both), depending on what kind of document it is and what country issued it
				customSDKController?.setDocumentConfiguration(selectedCountry, documentType, documentVariant)
			}
		} catch (e: SDKNotConfiguredException) {
			Log.e(TAG, "onDocumentTypeSelected: ", e)
		}
	}

	/**
	 * Scan has been initialized.
	 *
	 * @param side             refers to which side of the document is being scanned (front or back)
	 * @param scanView         does the actual document and face scanning
	 * @param confirmationView returns the scanned image for confirmation
	 */
	override fun onInitScanningWithSide(side: ScanSide?, scanView: NetverifyCustomScanView?, confirmationView: NetverifyCustomConfirmationView?,
	                                    customScanInterface: NetverifyCustomScanInterface?): NetverifyCustomScanPresenter? {
		return try {
			if(customScanViewPresenter == null) {
				customScanViewPresenter = customSDKController?.initScanForPart(side, scanView, confirmationView, customScanInterface)
			} else {
				customScanViewPresenter?.recreate(scanView, confirmationView, customScanInterface)
			}
			if (customScanViewPresenter == null) {
				Log.e(TAG, "onStartScanningWithSide ")
				throw SDKNotConfiguredException("Could not create customScanViewPresenter")
			}
			invalidateOptionsMenu()
			customScanViewPresenter
		} catch (e: SDKNotConfiguredException) {
			Log.e(TAG, "onStartScanningWithSide: $e")
			finish()
			null
		}
	}

	/**
	 * Current scan has been finished, method checks if this was last scan or if another one is necessary.
	 * If scanning process is not finished yet, new ScanFragment is started.
	 */
	override fun onScanForPartFinished() {
		customScanViewPresenter = null;
		selectedScanSide++
		if (selectedScanSide < sides!!.size) {
			val progressString = applicationContext.getString(R.string.netverify_helpview_progress_text, selectedScanSide + 1, sides!!.size)
			val newScanFragment = NetverifyCustomScanFragment.newInstance(sides!![selectedScanSide].toString(),
					selectedDocumentType!!.getLocalizedName(this), progressString)
			startFragment(newScanFragment, NetverifyCustomScanFragment::class.java.simpleName, true)
		}
	}

	/**
	 * Active scan has been cancelled, either manually by user or due to error.
	 */
	override fun onScanCancelled() {
		onFinish()
	}

	/**
	 * Scan has been finished.
	 */
	override fun onScanFinished() {
		try {
			customScanViewPresenter = null;
			customSDKController?.finish()
		} catch (e: SDKNotConfiguredException) {
			Log.e(TAG, "onScanFinished: ", e)
		}
	}

	/**
	 * Country has been chosen out of country list
	 *
	 * @param isoCode to find country in Hashmap
	 */
	override fun onCountrySelected(isoCode: String?) {
		if (countryMap != null) {
			selectedCountry = countryMap!![isoCode]
			showBottomSheetDialogFragment(selectedCountry!!.documentTypes)
		}
	}

	/**
	 * Finish current activity.
	 */
	override fun onFinish() {
		cleanupSDK()
		finish()
	}
	//#####################################################
	// CUSTOM UI FUNCTIONALITY
	//#####################################################
	/**
	 * Initializes customized NetverifySDK
	 */
	private fun initializeNetverifyCustom() { //Check if there is permission to use a customized version of NetverifySDK
		if (!NetverifySDK.hasAllRequiredPermissions(this)) {
			ActivityCompat.requestPermissions(this, NetverifySDK.getMissingPermissions(this), PERMISSION_REQUEST_CODE_NETVERIFY_CUSTOM)
		} else { //Show Document Selection Fragment
			try { //Display first fragment
				val docSelectionFragment = NetverifyCustomDocSelectionFragment()
				startFragment(docSelectionFragment, NetverifyCustomDocSelectionFragment::class.java.simpleName, false)
				//Initialize usual NetverifySDK
				initializeNetverifySDK()
				if (netverifySDK != null) {
					customSDKController = netverifySDK?.start(NetverifyCustomSDKImpl())
				}
			} catch (e: IllegalArgumentException) {
				showToastMessage(e.message)
				Log.e(TAG, "initializeNetverifyCustom: ", e)
			} catch (e: MissingPermissionException) {
				showToastMessage(e.message)
				Log.e(TAG, "initializeNetverifyCustom: ", e)
			}
		}
	}

	/**
	 * Initializes standard NetverifySDK
	 * Certain parameters can be adjusted individually (disabling paper documents entirely,
	 * allowing only documents from specific countries, etc.)
	 * To set a custom theme, use setCustomTheme(R.style.YOUR-CUSTOM-THEME-ID)
	 */
	private fun initializeNetverifySDK() {
		try { // You can get the current SDK version using the method below.
//			NetverifySDK.getSDKVersion()
// Call the method isSupportedPlatform to check if the device is supported.
			if (!NetverifySDK.isSupportedPlatform(this)) Log.w(TAG, "Device not supported")
			// Applications implementing the SDK shall not run on rooted devices. Use either the below
// method or a self-devised check to prevent usage of SDK scanning functionality on rooted
// devices.
			if (NetverifySDK.isRooted(this)) Log.w(TAG, "Device is rooted")
			// To create an instance of the SDK, perform the following call as soon as your activity is initialized.
// Make sure that your merchant API token and API secret are correct and specify an instance
// of your activity. If your merchant account is created in the EU data center, use
// JumioDataCenter.EU instead.
			netverifySDK = NetverifySDK.create(this, apiToken, apiSecret, datacenter)
			// Use the following method to create an instance of the SDK, using offline fastfill scanning.
//			try {
//				netverifySDK = NetverifySDK.create(getActivity(), "YOUROFFLINETOKEN", "YOURPREFERREDCOUNTRY")
//			} catch (SDKExpiredException e) {
//				Toast.makeText(getActivity().getApplicationContext(), "The offline SDK is expired", Toast.LENGTH_LONG).show()
// 				Log.e(TAG, "initializeNetverifySDK SDK expired: ", e)
//			}
// Enable ID verification to receive a verification status and verified data positions (see Callback chapter).
// Note: Not possible for accounts configured as Fastfill only.
			netverifySDK?.setEnableVerification(true)
			// You can specify issuing country (ISO 3166-1 alpha-3 country code) and/or ID types and/or document variant to skip
// their selection during the scanning process.
// Use the following method to convert ISO 3166-1 alpha-2 into alpha-3 country code.
//			String alpha3 = IsoCountryConverter.convertToAlpha3("AT")
//			netverifySDK?.setPreselectedCountry("AUT")
//			netverifySDK?.ArrayList<NVDocumentType> documentTypes = new ArrayList<>()
//			documentTypes.add(NVDocumentType.PASSPORT)
//			netverifySDK?.setPreselectedDocumentTypes(documentTypes)
//
			netverifySDK?.setPreselectedDocumentVariant(NVDocumentVariant.PLASTIC)
			// The customer internal reference allows you to identify the scan (max. 100 characters).
// Note: Must not contain sensitive data like PII (Personally Identifiable Information) or account login.
//			netverifySDK?.setCustomerInternalReference("YOURSCANREFERENCE")
// Use the following property to identify the scan in your reports (max. 100 characters).
//			netverifySDK?.setReportingCriteria("YOURREPORTINGCRITERIA")
// You can also set a user reference (max. 100 characters).
// Note: The user reference should not contain sensitive data like PII (Personally Identifiable Information) or account login.
//			netverifySDK?.setUserReference("USERREFERENCE")
// Callback URL (max. 255 characters) for the confirmation after the verification is completed. This setting overrides your Jumio merchant settings.
//			netverifySDK?.setCallbackUrl("YOURCALLBACKURL")
// You can disable Identity Verification during the ID verification for a specific transaction.
			netverifySDK?.setEnableIdentityVerification(true)
// Use the following method to set the default camera position.
//			netverifySDK?.setCameraPosition(JumioCameraPosition.FRONT)
// Use the following method to only support IDs where data can be extracted on mobile only.
//			netverifySDK?.setDataExtractionOnMobileOnly(true)
// Use the following method to explicitly send debug-info to Jumio. (default: false)
// Only set this property to true if you are asked by our Jumio support personnel.
//			netverifySDK?.sendDebugInfoToJumio(true)
// Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
//			netverifySDK?.setCustomTheme(R.style.YOURCUSTOMTHEMEID)
// Set watchlist screening on transaction level. Enable to override the default search, or disable watchlist screening for this transaction.
//			netverifySDK?.setWatchlistScreening(NVWatchlistScreening.ENABLED)
// Search profile for watchlist screening.
//			netverifySDK?.setWatchlistSearchProfile("YOURPROFILENAME")
// Use the following method to initialize the SDK before displaying it
//			netverifySDK?.initiate(new NetverifyInitiateCallback() {
//				@Override
//				public void onNetverifyInitiateSuccess() {
//				}
//				@Override
//				public void onNetverifyInitiateError(String errorCode, String errorMessage, boolean retryPossible) {
//				}
//			})
		} catch (e: PlatformNotSupportedException) {
			Log.e(TAG, "Error in initializeNetverifySDK: ", e)
			showToastMessage(e.message)
		}
	}

	/**
	 * Checks if all necessary permissions for the SDK are granted
	 * custom NetverifySDK will only be initialized if all permissions are granted
	 *
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		var allGranted = true
		for (grantResult in grantResults) {
			if (grantResult != PackageManager.PERMISSION_GRANTED) {
				allGranted = false
				break
			}
		}
		if (allGranted) {
			if (requestCode == PERMISSION_REQUEST_CODE_NETVERIFY_CUSTOM) {
				initializeNetverifyCustom()
				try {
					customSDKController?.resume()
				} catch (e: SDKNotConfiguredException) {
					showToastMessage(e.message)
					Log.e(TAG, "onRequestPermissionsResult: ", e)
				}
			}
		} else {
			Toast.makeText(applicationContext, "You need to grant all required permissions to start the Jumio SDK", Toast.LENGTH_LONG).show()
			super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		}
	}

	/**
	 * Destroys SDKController and NetverifySDK if they exist
	 */
	private fun cleanupSDK() {
		if (customScanViewPresenter != null) {
			try {
				customScanViewPresenter!!.destroy()
			} catch (e: java.lang.Exception) {
				Log.w(TAG, e.message)
			}
			customScanViewPresenter = null
		}
		if (customSDKController != null) {
			try {
				customSDKController!!.destroy()
			} catch (e: java.lang.Exception) {
				Log.w(TAG, e.message)
			}
			customSDKController = null
		}
		if (netverifySDK != null) {
			netverifySDK?.destroy()
			netverifySDK = null
		}
	}

	/**
	 * Back button of device was clicked.
	 */
	override fun onBackPressed() {
		cleanupSDK()
		super.onBackPressed()
	}

	//#####################################################
	// HELPER METHODS
	//#####################################################
	private fun showToastMessage(s: String?) {
		Toast.makeText(applicationContext, s, Toast.LENGTH_LONG).show()
	}

	/**
	 * Start new fragment, closes and removes old one
	 *
	 * @param newFragment
	 * @param fragmentName
	 * @param closeCurrentFragment
	 */
	fun startFragment(newFragment: Fragment, fragmentName: String?, closeCurrentFragment: Boolean) {
		val fragmentManager = supportFragmentManager
		val fragmentTransaction = fragmentManager.beginTransaction()
		fragmentTransaction.setCustomAnimations(R.animator.nv_fade_in, R.animator.nv_fade_out)
		fragmentTransaction.replace(R.id.fragment_holder, newFragment, fragmentName)
		try {
			fragmentTransaction.commitAllowingStateLoss()
		} catch (e: IllegalStateException) {
			Log.e(TAG, "startFragment: ", e)
		}
	}

	/**
	 * Shows which document types are available if a specific country is selected
	 * (e.g. just passport, passport and driver's license, etc.)
	 *
	 * @param documentData
	 */
	private fun showBottomSheetDialogFragment(documentData: Set<NVDocumentType>?) {
		if(!documentData.isNullOrEmpty()) {
			val bottomSheetFragment = BottomSheetDialogDocuments()
			val sortedList = ArrayList(documentData)
			sortedList.sort()
			val bundle = Bundle()
			bundle.putStringArrayList(BUNDLE_DOCUMENT_TYPE_LIST, ArrayList(sortedList.map { it.toString() }.toList()))
			bottomSheetFragment.arguments = bundle
			bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
		}
	}

	//#####################################################
	// CUSTOM SDK CLASSES
	//#####################################################
	private inner class NetverifyCustomSDKImpl : NetverifyCustomSDKInterface {
		/**
		 * Displays country list with all possible countries for user to choose from
		 *
		 * @param countryList     Hashmap with all possible countries. The key is the ISO 3155-1 alpha 3 code, the value is an instance of [NetverifyCountry]
		 * @param userCountryCode - ISO 3166-1 alpha 3 user country
		 */
		override fun onNetverifyCountriesReceived(countryList: Map<String, NetverifyCountry>, userCountryCode: String) {
			countryMap = countryList
			Log.i(TAG, "onNetverifyCountriesReceived - user Country is $userCountryCode")

			//display fragment with countries loaded
			val iso3CountryList = ArrayList(countryList.keys)
			val convertedCountryList = ArrayList<Country>()
			for (s in iso3CountryList) {
				convertedCountryList.add(Country(s))
			}
			convertedCountryList.sort()
			val frag = supportFragmentManager.fragments[0] as NetverifyCustomDocSelectionFragment
			frag.updateListView(convertedCountryList)
		}

		/**
		 * Called as soon as all resources are loaded and scanning can be started
		 */
		override fun onNetverifyResourcesLoaded(scanSides: List<ScanSide>) {
			Log.i(TAG, "onNetverifyResourcesLoaded")

			sides = scanSides
			selectedScanSide = 0
			//Start scanning for side
			val progressString = applicationContext.getString(R.string.netverify_helpview_progress_text, selectedScanSide + 1, sides?.size)
			val scanFragment = NetverifyCustomScanFragment.newInstance(sides?.get(selectedScanSide).toString(), selectedDocumentType?.getLocalizedName(this@NetverifyCustomActivity), progressString)

			startFragment(scanFragment, NetverifyCustomScanFragment::class.java.simpleName, true)
		}

		/**
		 * Sets parameters for and starts confirmation fragment if all scans have been finished
		 * and verification process has been successful
		 *
		 * @param data  workflow result data, like {@link com.jumio.nv.NetverifySDK#EXTRA_SCAN_REFERENCE},  {@link com.jumio.nv.NetverifySDK#EXTRA_ACCOUNT_ID},  {@link com.jumio.nv.NetverifySDK#EXTRA_SCAN_DATA}
		 */
		override fun onNetverifyFinished(data: Bundle) {
			Log.i(TAG, "onNetverifyFinished")
			Log.i(TAG, "scanReference: " + data.getString(NetverifySDK.EXTRA_SCAN_REFERENCE))
			Log.i(TAG, "accountId: " + data.getString(NetverifySDK.EXTRA_ACCOUNT_ID))
			val documentData = data.getSerializable(NetverifySDK.EXTRA_SCAN_DATA) as NetverifyDocumentData?
			if (documentData != null) {
				val firstName = if (!TextUtils.isEmpty(documentData.firstName)) documentData.firstName else ""
				val lastname = if (!TextUtils.isEmpty(documentData.lastName)) documentData.lastName else ""
				val successFragment = NetverifyCustomSuccessFragment.newInstance("$firstName $lastname")
				startFragment(successFragment, NetverifyCustomSuccessFragment::class.java.simpleName, true)
			} else {
				onFinish()
			}
		}

		/**
		 * An error occurred during the process
		 *
		 * @param errorCode     the error code
		 * @param errorMessage  the localized error message
		 * @param retryPossible true when [NetverifyCustomSDKController.retry] can be called
		 * @param scanReference Scan reference
		 * @param accountId     Account ID, if available
		 */
		override fun onNetverifyError(errorCode: String, errorMessage: String, retryPossible: Boolean, scanReference: String?, accountId: String?) {
			Log.i(TAG, String.format(Locale.getDefault(), "onNetverifyError: %s, %s, %d, %s, %s", errorCode, errorMessage, if (retryPossible) 0 else 1, scanReference
					?: "null", accountId?: "null"))
			runOnUiThread(NetverifyErrorPresenter(errorCode, errorMessage, retryPossible))
		}

		/**
		 * This callback is invoked when the end-user’s consent to Jumio’s privacy policy is legally required.
		 *
		 * @param s
		 */
		override fun onNetverifyUserConsentRequried(s: String) {
			Log.i(TAG, "onNetverifyUserConsentRequired: $s")
		}
	}

	/**
	 * Creates and displays error dialogue, detailing error code, message and if retry is possible or not
	 */
	private inner class NetverifyErrorPresenter internal constructor(private val errorCode: String, private val errorMessage: String, private val retryPossible: Boolean) : Runnable {
		override fun run() {
			if (!errorCode.startsWith("G")) {
				try {
					runOnUiThread { }
					val alertDialogBuilder = AlertDialog.Builder(this@NetverifyCustomActivity)
					alertDialogBuilder.setMessage(errorMessage)
					alertDialogBuilder.setCancelable(false)
					if (retryPossible) {
						alertDialogBuilder.setPositiveButton(R.string.jumio_button_retry) { _, _ ->
							if (customSDKController != null) {
								try {
									customSDKController?.retry()
								} catch (e: SDKNotConfiguredException) {
									Log.e(TAG, "dialog interface: ", e)
								}
							}
						}
					}
					alertDialogBuilder.setNegativeButton(R.string.jumio_button_cancel) { _, _ -> finish() }
					alertDialogBuilder.show()
				} catch (e: Exception) { //do not handle
					Log.e(TAG, "dialog builder: ", e)
				}
			}
		}
	}

	companion object {
		private const val TAG = "NetverifyCustomActivity"
		const val BUNDLE_DOCUMENT_TYPE_LIST = "BUNDLE_DOCUMENT_TYPE_LIST"
		const val BUNDLE_DOCUMENT_TYPE = "BUNDLE_DOCUMENT_TYPE"
		private const val INSTANCE_COUNTRY_MAP = "country_map";
		private const val INSTANCE_SCANSIDES = "scansides";
		private const val INSTANCE_SELECTED_COUNTRY = "selected_country";
		private const val INSTANCE_SELECTED_DOCUMENTTYPE = "selected_documenttype";
		private const val INSTANCE_SELECTED_SCANSIDE = "selected_scanside";
		private const val PERMISSION_REQUEST_CODE_NETVERIFY_CUSTOM = 303

		private var netverifySDK: NetverifySDK? = null
		private var customSDKController: NetverifyCustomSDKController? = null
		private var customScanViewPresenter: NetverifyCustomScanPresenter? = null;
	}
}