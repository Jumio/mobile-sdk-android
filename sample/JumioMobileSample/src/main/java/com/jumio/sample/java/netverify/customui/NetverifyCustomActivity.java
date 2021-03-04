package com.jumio.sample.java.netverify.customui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.jumio.core.data.document.ScanSide;
import com.jumio.core.enums.JumioDataCenter;
import com.jumio.core.exceptions.MissingPermissionException;
import com.jumio.core.exceptions.PlatformNotSupportedException;
import com.jumio.nv.NetverifyDocumentData;
import com.jumio.nv.NetverifySDK;
import com.jumio.nv.custom.NetverifyCountry;
import com.jumio.nv.custom.NetverifyCustomConfirmationView;
import com.jumio.nv.custom.NetverifyCustomSDKController;
import com.jumio.nv.custom.NetverifyCustomSDKInterface;
import com.jumio.nv.custom.NetverifyCustomScanInterface;
import com.jumio.nv.custom.NetverifyCustomScanPresenter;
import com.jumio.nv.custom.NetverifyCustomScanView;
import com.jumio.nv.data.country.Country;
import com.jumio.nv.data.document.NVDocumentType;
import com.jumio.nv.data.document.NVDocumentVariant;
import com.jumio.sample.R;
import com.jumio.sample.java.MainActivity;
import com.jumio.sdk.custom.SDKNotConfiguredException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class NetverifyCustomActivity extends AppCompatActivity implements BottomSheetDialogDocuments.OnBottomSheetActionListener,
	BottomSheetDialogDocumentVariant.OnBottomSheetActionListener,
	NetverifyCustomDocSelectionFragment.OnDocumentSelectionInteractionListener,
	NetverifyCustomScanFragment.OnScanFragmentInteractionListener,
	NetverifyCustomSuccessFragment.OnSuccessFragmentInteractionListener {

	private final static String TAG = "NetverifyCustomActivity";
	public static final String BUNDLE_DOCUMENT_TYPE_LIST = "BUNDLE_DOCUMENT_TYPE_LIST";
	public static final String BUNDLE_DOCUMENT_TYPE = "BUNDLE_DOCUMENT_TYPE";
	private static final String INSTANCE_COUNTRY_MAP = "country_map";
	private static final String INSTANCE_SCANSIDES = "scansides";
	private static final String INSTANCE_SELECTED_COUNTRY = "selected_country";
	private static final String INSTANCE_SELECTED_DOCUMENTTYPE = "selected_documenttype";
	private static final String INSTANCE_SELECTED_SCANSIDE = "selected_scanside";
	private static final int PERMISSION_REQUEST_CODE_NETVERIFY_CUSTOM = 303;

	private String apiToken = null;
	private String apiSecret = null;
	private JumioDataCenter datacenter = null;

	private static NetverifySDK netverifySDK = null;
	private static NetverifyCustomSDKController customSDKController = null;
	private static NetverifyCustomScanPresenter customScanViewPresenter = null;

	private NetverifyCountry selectedCountry = null;
	private Map<String, NetverifyCountry> countryMap = null;

	private NVDocumentType selectedDocumentType;
	private int selectedScanSide;
	private List<ScanSide> sides;

	//#####################################################
	// LIFECYCLE METHODS
	//#####################################################

	@SuppressLint("RestrictedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_netverify_custom);
		Toolbar toolbar = findViewById(R.id.toolbar);
		AppBarLayout appBarLayout = findViewById(R.id.netverify_custom_appBarLayout);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			appBarLayout.setElevation(0f);
		}
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle("");
		}

		//Set up credentials
		Bundle args = getIntent().getExtras();

		if (args != null) {
			apiToken = args.getString(MainActivity.KEY_API_TOKEN);
			apiSecret = args.getString(MainActivity.KEY_API_SECRET);
			datacenter = (JumioDataCenter) args.getSerializable(MainActivity.KEY_DATACENTER);
		}

		// action bar at top of the screen
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			Drawable icon = AppCompatDrawableManager.get().getDrawable(getApplicationContext(), R.drawable.ic_arrow_back_white);
			getSupportActionBar().setHomeAsUpIndicator(icon);
			getSupportActionBar().setShowHideAnimationEnabled(false);
		}

		if(savedInstanceState != null && netverifySDK != null && customSDKController != null) {
			//Activity has been recreated
			netverifySDK.recreate(this);
			customSDKController.recreate(this, new NetverifyCustomSDKImpl());

			countryMap = (Map<String, NetverifyCountry>) savedInstanceState.getSerializable(INSTANCE_COUNTRY_MAP);
			sides = (List<ScanSide>) savedInstanceState.getSerializable(INSTANCE_SCANSIDES);
			selectedCountry = (NetverifyCountry) savedInstanceState.getSerializable(INSTANCE_SELECTED_COUNTRY);
			selectedDocumentType = (NVDocumentType) savedInstanceState.getSerializable(INSTANCE_SELECTED_DOCUMENTTYPE);
			selectedScanSide = savedInstanceState.getInt(INSTANCE_SELECTED_SCANSIDE);
		} else {
			cleanupSDK();
			initializeNetverifyCustom();
		}
	}

	@Override
	public void onPause() {
		try {
			if (customSDKController != null)
				customSDKController.pause();
		} catch (SDKNotConfiguredException e) {
			Log.e(TAG, "onPause: ", e);
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			if (customSDKController != null)
				customSDKController.resume();
		} catch (SDKNotConfiguredException e) {
			Log.e(TAG, "onResume: ", e);
		}
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putSerializable(INSTANCE_SELECTED_COUNTRY, selectedCountry);
		outState.putSerializable(INSTANCE_COUNTRY_MAP, (Serializable) countryMap);
		outState.putSerializable(INSTANCE_SELECTED_DOCUMENTTYPE, selectedDocumentType);
		outState.putSerializable(INSTANCE_SCANSIDES, (Serializable) sides);
		outState.putInt(INSTANCE_SELECTED_SCANSIDE, selectedScanSide);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(customSDKController != null) {
			customSDKController.consumeIntent(requestCode, resultCode, data);
		}
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
	public void onDocumentTypeSelected(NVDocumentType documentType, NVDocumentVariant documentVariant, boolean variantSelected) {
		try {
			selectedDocumentType = documentType;
			if (selectedCountry.getDocumentVariants(documentType).size() > 1 && !variantSelected) {

				// display dialog to choose between plastic and other format
				BottomSheetDialogDocumentVariant bottomSheetFragment = new BottomSheetDialogDocumentVariant();
				Bundle bundle = new Bundle();
				bundle.putSerializable(BUNDLE_DOCUMENT_TYPE, selectedDocumentType);
				bottomSheetFragment.setArguments(bundle);
				bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
			} else {
				// sides refers to how many sides of the document need to be scanned (front, back or both), depending on what kind of document it is and what country issued it
				customSDKController.setDocumentConfiguration(selectedCountry, documentType, documentVariant);
			}
		} catch (SDKNotConfiguredException e) {
			Log.e(TAG, "onDocumentTypeSelected: ", e);
		}
	}

	/**
	 * Scan has been initialized.
	 *
	 * @param side             refers to which side of the document is being scanned (front or back)
	 * @param scanView         does the actual document and face scanning
	 * @param confirmationView returns the scanned image for confirmation
	 */
	public NetverifyCustomScanPresenter onInitScanningWithSide(ScanSide side, NetverifyCustomScanView scanView, NetverifyCustomConfirmationView confirmationView,
	                                                           NetverifyCustomScanInterface customScanImpl) {

		try {
			if(customScanViewPresenter == null) {
				customScanViewPresenter = customSDKController.initScanForPart(side, scanView, confirmationView, customScanImpl);
			} else {
				customScanViewPresenter.recreate(scanView, confirmationView, customScanImpl);
			}
			if (customScanViewPresenter == null) {
				Log.e(TAG, "onStartScanningWithSide ");
				throw new SDKNotConfiguredException("Could not create customScanViewPresenter");
			}
			invalidateOptionsMenu();
			return customScanViewPresenter;
		} catch (SDKNotConfiguredException e) {
			Log.e(TAG, "onStartScanningWithSide: " + e);
			finish();
			return null;
		}
	}

	/**
	 * Current scan has been finished, method checks if this was last scan or if another one is necessary.
	 * If scanning process is not finished yet, new ScanFragment is started.
	 */
	@Override
	public void onScanForPartFinished() {
		customScanViewPresenter = null;
		selectedScanSide++;
		if (selectedScanSide < sides.size()) {

			String progressString = getApplicationContext().getString(R.string.netverify_helpview_progress_text, selectedScanSide + 1, sides.size());
			NetverifyCustomScanFragment newScanFragment = NetverifyCustomScanFragment.newInstance(sides.get(selectedScanSide).toString(),
				selectedDocumentType.getLocalizedName(this), progressString);
			startFragment(newScanFragment, NetverifyCustomScanFragment.class.getSimpleName(), true);
		}
	}

	/**
	 * Active scan has been cancelled, either manually by user or due to error.
	 */
	@Override
	public void onScanCancelled() {
		onFinish();
	}

	/**
	 * Scan has been finished.
	 */
	@Override
	public void onScanFinished() {
		try {
			customScanViewPresenter = null;
			customSDKController.finish();
		} catch (SDKNotConfiguredException e) {
			Log.e(TAG, "onScanFinished: ", e);
		}
	}

	/**
	 * Country has been chosen out of country list
	 *
	 * @param countryKey to find country in Hashmap
	 */
	@Override
	public void onCountrySelected(String countryKey) {
		if (countryMap != null) {
			selectedCountry = countryMap.get(countryKey);
			Set<NVDocumentType> documentTypes = selectedCountry != null ? selectedCountry.getDocumentTypes() : null;
			if (documentTypes != null) {
				showBottomSheetDialogFragment(documentTypes);
			}
		}
	}

	/**
	 * Finish current activity.
	 */
	@Override
	public void onFinish() {
		cleanupSDK();
		finish();
	}

	//#####################################################
	// CUSTOM UI FUNCTIONALITY
	//#####################################################

	/**
	 * Initializes customized NetverifySDK
	 */
	private void initializeNetverifyCustom() {

		//Check if there is permission to use a customized version of NetverifySDK
		if (!NetverifySDK.hasAllRequiredPermissions(this)) {
			ActivityCompat.requestPermissions(this, NetverifySDK.getMissingPermissions(this), PERMISSION_REQUEST_CODE_NETVERIFY_CUSTOM);
		} else {
			//Show Document Selection Fragment
			try {
				//Display first fragment
				NetverifyCustomDocSelectionFragment docSelectionFragment = new NetverifyCustomDocSelectionFragment();
				startFragment(docSelectionFragment, NetverifyCustomDocSelectionFragment.class.getSimpleName(), false);

				//Initialize usual NetverifySDK
				initializeNetverifySDK();

				if (netverifySDK != null) {
					customSDKController = netverifySDK.start(new NetverifyCustomSDKImpl());
				}

			} catch (IllegalArgumentException | MissingPermissionException e) {
				showToastMessage(e.getMessage());
				Log.e(TAG, "initializeNetverifyCustom: ", e);
			}
		}
	}

	/**
	 * Initializes standard NetverifySDK
	 * Certain parameters can be adjusted individually (disabling paper documents entirely,
	 * allowing only documents from specific countries, etc.)
	 * To set a custom theme, use setCustomTheme(R.style.YOUR-CUSTOM-THEME-ID)
	 */
	private void initializeNetverifySDK() {
		try {
			// You can get the current SDK version using the method below.
//			NetverifySDK.getSDKVersion();

			// Call the method isSupportedPlatform to check if the device is supported.
			if (!NetverifySDK.isSupportedPlatform(this))
				Log.w(TAG, "Device not supported");

			// Applications implementing the SDK shall not run on rooted devices. Use either the below
			// method or a self-devised check to prevent usage of SDK scanning functionality on rooted
			// devices.
			if (NetverifySDK.isRooted(this))
				Log.w(TAG, "Device is rooted");

			// To create an instance of the SDK, perform the following call as soon as your activity is initialized.
			// Make sure that your merchant API token and API secret are correct and specify an instance
			// of your activity. If your merchant account is created in the EU data center, use
			// JumioDataCenter.EU instead.
			netverifySDK = NetverifySDK.create(this, apiToken, apiSecret, datacenter);

			// Use the following method to create an instance of the SDK, using offline fastfill scanning.
//			try {
//				netverifySDK = NetverifySDK.create(getActivity(), "YOUROFFLINETOKEN", "YOURPREFERREDCOUNTRY");
//			} catch (SDKExpiredException e) {
//				Toast.makeText(getActivity().getApplicationContext(), "The offline SDK is expired", Toast.LENGTH_LONG).show();
// 				Log.e(TAG, "initializeNetverifySDK SDK expired: ", e);
//			}

			// Enable ID verification to receive a verification status and verified data positions (see Callback chapter).
			// Note: Not possible for accounts configured as Fastfill only.
			netverifySDK.setEnableVerification(true);

			// You can specify issuing country (ISO 3166-1 alpha-3 country code) and/or ID types and/or document variant to skip
			// their selection during the scanning process.
			// Use the following method to convert ISO 3166-1 alpha-2 into alpha-3 country code.
//			String alpha3 = IsoCountryConverter.convertToAlpha3("AT");
//			netverifySDK.setPreselectedCountry("AUT");
//			netverifySDK.ArrayList<NVDocumentType> documentTypes = new ArrayList<>();
//			documentTypes.add(NVDocumentType.PASSPORT);
//			netverifySDK.setPreselectedDocumentTypes(documentTypes);
//
			netverifySDK.setPreselectedDocumentVariant(NVDocumentVariant.PLASTIC);

			// The customer internal reference allows you to identify the scan (max. 100 characters).
			// Note: Must not contain sensitive data like PII (Personally Identifiable Information) or account login.
//			netverifySDK.setCustomerInternalReference("YOURSCANREFERENCE");

			// Use the following property to identify the scan in your reports (max. 100 characters).
//			netverifySDK.setReportingCriteria("YOURREPORTINGCRITERIA");

			// You can also set a user reference (max. 100 characters).
			// Note: The user reference should not contain sensitive data like PII (Personally Identifiable Information) or account login.
//			netverifySDK.setUserReference("USERREFERENCE");

			// Callback URL (max. 255 characters) for the confirmation after the verification is completed. This setting overrides your Jumio merchant settings.
//			netverifySDK.setCallbackUrl("YOURCALLBACKURL");

			// You can disable Identity Verification during the ID verification for a specific transaction.
			netverifySDK.setEnableIdentityVerification(true);

			// Use the following method to set the default camera position.
//			netverifySDK.setCameraPosition(JumioCameraPosition.FRONT);

			// Use the following method to only support IDs where data can be extracted on mobile only.
//			netverifySDK.setDataExtractionOnMobileOnly(true);

			// Use the following method to explicitly send debug-info to Jumio. (default: false)
			// Only set this property to true if you are asked by our Jumio support personnel.
//			netverifySDK.sendDebugInfoToJumio(true);

			// Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
//			netverifySDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID);

			// Set watchlist screening on transaction level. Enable to override the default search, or disable watchlist screening for this transaction.
//			netverifySDK.setWatchlistScreening(NVWatchlistScreening.ENABLED);

			// Search profile for watchlist screening.
//			netverifySDK.setWatchlistSearchProfile("YOURPROFILENAME");

			// Use the following method to initialize the SDK before displaying it
//			netverifySDK.initiate(new NetverifyInitiateCallback() {
//				@Override
//				public void onNetverifyInitiateSuccess() {
//				}
//				@Override
//				public void onNetverifyInitiateError(String errorCode, String errorMessage, boolean retryPossible) {
//				}
//			});

		} catch (PlatformNotSupportedException e) {
			Log.e(TAG, "Error in initializeNetverifySDK: ", e);
			showToastMessage(e.getMessage());
			netverifySDK = null;
		}
	}

	/**
	 * Checks if all necessary permissions for the SDK are granted
	 * custom NetverifySDK will only be initialized if all permissions are granted
	 *
	 * @param requestCode  int
	 * @param permissions  String[]
	 * @param grantResults int[]
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

		boolean allGranted = true;
		for (int grantResult : grantResults) {
			if (grantResult != PackageManager.PERMISSION_GRANTED) {
				allGranted = false;
				break;
			}
		}

		if (allGranted) {
			if (requestCode == PERMISSION_REQUEST_CODE_NETVERIFY_CUSTOM) {
				initializeNetverifyCustom();
				try {
					customSDKController.resume();
				} catch (SDKNotConfiguredException e) {
					showToastMessage(e.getMessage());
					Log.e(TAG, "onRequestPermissionsResult: ", e);
				}
			}
		} else {
			Toast.makeText(getApplicationContext(), "You need to grant all required permissions to start the Jumio SDK", Toast.LENGTH_LONG).show();
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	/**
	 * Destroys SDKController and NetverifySDK if they exist
	 */
	private void cleanupSDK() {
		if(customScanViewPresenter != null) {
			try {
				customScanViewPresenter.destroy();
			} catch (Exception e) {
				Log.w(TAG, e.getMessage());
			}
			customScanViewPresenter = null;
		}
		if(customSDKController != null) {
			try {
				customSDKController.destroy();
			} catch (Exception e) {
				Log.w(TAG, e.getMessage());
			}
			customSDKController = null;
		}
		if (netverifySDK != null) {
			netverifySDK.destroy();
			netverifySDK = null;
		}
	}

	/**
	 * Back button of device was clicked.
	 */
	@Override
	public void onBackPressed() {
		cleanupSDK();
		super.onBackPressed();
	}

	//#####################################################
	// HELPER METHODS
	//#####################################################

	private void showToastMessage(String s) {
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
	}

	/**
	 * Start new fragment, closes and removes old one
	 *
	 * @param newFragment          Fragment
	 * @param fragmentName         String
	 * @param closeCurrentFragment boolean
	 */
	public void startFragment(Fragment newFragment, String fragmentName, boolean closeCurrentFragment) {
		FragmentManager fragmentManager = getSupportFragmentManager();

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.animator.nv_fade_in, R.animator.nv_fade_out);
		fragmentTransaction.replace(R.id.fragment_holder, newFragment, fragmentName);
		try {
			fragmentTransaction.commitAllowingStateLoss();
		} catch (IllegalStateException e) {
			Log.e(TAG, "startFragment: ", e);
		}
	}

	/**
	 * Shows which document types are available if a specific country is selected
	 * (e.g. just passport, passport and driver's license, etc.)
	 *
	 * @param documentData Set<NVDocumentType>
	 */
	public void showBottomSheetDialogFragment(Set<NVDocumentType> documentData) {
		BottomSheetDialogDocuments bottomSheetFragment = new BottomSheetDialogDocuments();
		ArrayList<NVDocumentType> sortedList = new ArrayList<>(documentData);
		Collections.sort(sortedList);
		Bundle bundle = new Bundle();
		ArrayList<String> nvDocumentTypeList = new ArrayList<>();
		for(NVDocumentType t : sortedList) {
			nvDocumentTypeList.add(t.toString());
		}
		bundle.putStringArrayList(BUNDLE_DOCUMENT_TYPE_LIST, nvDocumentTypeList);
		bottomSheetFragment.setArguments(bundle);
		bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
	}

	//#####################################################
	// CUSTOM SDK CLASSES
	//#####################################################

	private class NetverifyCustomSDKImpl implements NetverifyCustomSDKInterface {

		/**
		 * Displays country list with all possible countries for user to choose from
		 *
		 * @param countryList     Hashmap with all possible countries. The key is the ISO 3155-1 alpha 3 code, the value is an instance of {@link NetverifyCountry}
		 * @param userCountryCode - ISO 3166-1 alpha 3 user country
		 */
		@Override
		public void onNetverifyCountriesReceived(Map<String, NetverifyCountry> countryList, String userCountryCode) {
			countryMap = countryList;
			Log.i(TAG, "onNetverifyCountriesReceived - user Country is " + userCountryCode);

			//display fragment with countries loaded
			ArrayList<String> iso3CountryList = new ArrayList<>(countryList.keySet());
			ArrayList<Country> convertedCountryList = new ArrayList<>();
			for (String s : iso3CountryList) {
				convertedCountryList.add(new Country(s));
			}
			Collections.sort(convertedCountryList);
			NetverifyCustomDocSelectionFragment frag = (NetverifyCustomDocSelectionFragment) getSupportFragmentManager().getFragments().get(0);
			frag.updateListView(convertedCountryList);
		}

		/**
		 * Called as soon as all resources are loaded and scanning can be started
		 */
		@Override
		public void onNetverifyResourcesLoaded(List<ScanSide> scanSides) {
			Log.i(TAG, "onNetverifyResourcesLoaded");

			sides = scanSides;
			selectedScanSide = 0;
			//Start scanning for side
			String progressString = getApplicationContext().getString(R.string.netverify_helpview_progress_text, selectedScanSide + 1, sides.size());
			NetverifyCustomScanFragment scanFragment = NetverifyCustomScanFragment.newInstance(sides.get(selectedScanSide).toString(), selectedDocumentType!=null?selectedDocumentType.getLocalizedName(NetverifyCustomActivity.this):"", progressString);

			startFragment(scanFragment, NetverifyCustomScanFragment.class.getSimpleName(), true);
		}

		/**
		 * Sets parameters for and starts confirmation fragment if all scans have been finished
		 * and verification process has been successful
		 *
		 * @param data  workflow result data, like {@link com.jumio.nv.NetverifySDK#EXTRA_SCAN_REFERENCE},  {@link com.jumio.nv.NetverifySDK#EXTRA_ACCOUNT_ID},  {@link com.jumio.nv.NetverifySDK#EXTRA_SCAN_DATA}
		 */
		@Override
		public void onNetverifyFinished(Bundle data) {
			Log.i(TAG, "onNetverifyFinished");
			Log.i(TAG, "scanReference: " + data.getString(NetverifySDK.EXTRA_SCAN_REFERENCE));
			Log.i(TAG, "accountId: " + data.getString(NetverifySDK.EXTRA_ACCOUNT_ID));
			NetverifyDocumentData documentData = (NetverifyDocumentData) data.getSerializable(NetverifySDK.EXTRA_SCAN_DATA);
			if (documentData != null) {
				String firstName = !TextUtils.isEmpty(documentData.getFirstName()) ? documentData.getFirstName() : "";
				String lastname = !TextUtils.isEmpty(documentData.getLastName()) ? documentData.getLastName() : "";

				NetverifyCustomSuccessFragment successFragment = NetverifyCustomSuccessFragment.newInstance(firstName + " " + lastname);
				startFragment(successFragment, NetverifyCustomSuccessFragment.class.getSimpleName(), true);
			} else {
				onFinish();
			}
		}

		/**
		 * An error occurred during the process
		 *
		 * @param errorCode     the error code
		 * @param errorMessage  the localized error message
		 * @param retryPossible true when {@link NetverifyCustomSDKController#retry()} can be called
		 * @param scanReference Scan reference
		 * @param accountId     Account ID, if available
		 */
		@Override
		public void onNetverifyError(String errorCode, String errorMessage, boolean retryPossible,  @Nullable  String scanReference, @Nullable String accountId) {
			Log.i(TAG, String.format(Locale.getDefault(), "onNetverifyError: %s, %s, %d, %s, %s", errorCode, errorMessage, retryPossible ? 0 : 1, scanReference != null ? scanReference : "null", accountId != null ? accountId : "null"));
			NetverifyCustomActivity.this.runOnUiThread(new NetverifyErrorPresenter(errorCode, errorMessage, retryPossible));
		}

		/**
		 * This callback is invoked when the end-user’s consent to Jumio’s privacy policy is legally required.
		 *
		 * @param s String
		 */
		@Override
		public void onNetverifyUserConsentRequried(String s) {
			Log.i(TAG, "onNetverifyUserConsentRequired: " + s);
		}
	}

	/**
	 * Creates and displays error dialogue, detailing error code, message and if retry is possible or not
	 */
	private class NetverifyErrorPresenter implements Runnable {
		private String errorCode;
		private String errorMessage;
		private boolean retryPossible;

		NetverifyErrorPresenter(String errorCode, String errorMessage, boolean retryPossible) {
			this.errorCode = errorCode;
			this.errorMessage = errorMessage;
			this.retryPossible = retryPossible;
		}

		@Override
		public void run() {
			if (!errorCode.startsWith("G")) {
				try {
					NetverifyCustomActivity.this.runOnUiThread(() -> {

					});
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NetverifyCustomActivity.this);
					alertDialogBuilder.setMessage(errorMessage);
					alertDialogBuilder.setCancelable(false);
					if (retryPossible) {
						alertDialogBuilder.setPositiveButton(R.string.jumio_button_retry, (dialogInterface, i) -> {
							if (customSDKController != null) {
								try {
									customSDKController.retry();
								} catch (SDKNotConfiguredException e) {
									Log.e(TAG, "dialog interface: ", e);
								}
							}
						});
					}
					alertDialogBuilder.setNegativeButton(R.string.jumio_button_cancel, (dialogInterface, i) -> finish());
					alertDialogBuilder.show();
				} catch (Exception e) { //do not handle
					Log.e(TAG, "dialog builder: ", e);
				}
			}
		}
	}
}
