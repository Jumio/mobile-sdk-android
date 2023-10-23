package com.jumio.sample.java.netverify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jumio.core.enums.JumioDataCenter;
import com.jumio.core.exceptions.MissingPermissionException;
import com.jumio.core.exceptions.PlatformNotSupportedException;
import com.jumio.nv.NetverifyDeallocationCallback;
import com.jumio.nv.NetverifyDocumentData;
import com.jumio.nv.NetverifyMrzData;
import com.jumio.nv.NetverifySDK;
import com.jumio.sample.R;
import com.jumio.sample.java.MainActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Copyright 2019 Jumio Corporation All rights reserved.
 */
public class NetverifyFragment extends Fragment implements View.OnClickListener, NetverifyDeallocationCallback {
	private final static String TAG = "JumioSDK_Netverify";
	private static final int PERMISSION_REQUEST_CODE_NETVERIFY = 303;

	private String apiToken = null;
	private String apiSecret = null;
	private JumioDataCenter dataCenter = null;

	private NetverifySDK netverifySDK;

	private SwitchMaterial switchVerification;
	private SwitchMaterial switchIdentitiyVerification;
	private Button btnStart;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		switchVerification = rootView.findViewById(R.id.switchOptionOne);
		switchIdentitiyVerification = rootView.findViewById(R.id.switchOptionTwo);
		switchIdentitiyVerification.setChecked(true);

		Bundle args = getArguments();

		switchVerification.setText(getResources().getString(R.string.netverify_verification_enabled));
		switchIdentitiyVerification.setText(getResources().getString(R.string.netverify_identity_verification_enabled));

		if(args != null) {
			apiToken = args.getString(MainActivity.KEY_API_TOKEN);
			apiSecret = args.getString(MainActivity.KEY_API_SECRET);
			dataCenter = (JumioDataCenter) args.getSerializable(MainActivity.KEY_DATACENTER);
		}

		btnStart = rootView.findViewById(R.id.btnStart);
		btnStart.setText(String.format(getResources().getString(R.string.button_start), getResources().getString(R.string.section_netverify)));
		btnStart.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onClick(View view) {
		//Since the NetverifySDK is a singleton internally, a new instance is not
		//created here.
		initializeNetverifySDK();

		if(getActivity() != null) {
			if (((MainActivity) getActivity()).checkPermissions(PERMISSION_REQUEST_CODE_NETVERIFY)) {
				try {
					if (netverifySDK != null) {
						view.setEnabled(false);
						startActivityForResult(netverifySDK.getIntent(), NetverifySDK.REQUEST_CODE);
					}
				} catch (MissingPermissionException e) {
					Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
					view.setEnabled(true);
				}
			}
		}
	}

	private void initializeNetverifySDK() {
		try {
			// You can get the current SDK version using the method below.
//			NetverifySDK.getSDKVersion();

			// Call the method isSupportedPlatform to check if the device is supported.
			if (!NetverifySDK.isSupportedPlatform(getActivity()))
				Log.w(TAG, "Device not supported");

			// Applications implementing the SDK shall not run on rooted devices. Use either the below
			// method or a self-devised check to prevent usage of SDK scanning functionality on rooted
			// devices.
			if (NetverifySDK.isRooted(getActivity()))
				Log.w(TAG, "Device is rooted");

			// To create an instance of the SDK, perform the following call as soon as your activity is initialized.
			// Make sure that your merchant API token and API secret are correct and specify an instance
			// of your activity. If your merchant account is created in the EU data center, use
			// JumioDataCenter.EU instead.
			netverifySDK = NetverifySDK.create(getActivity(), apiToken, apiSecret, dataCenter);

			// Use the following method to create an instance of the SDK, using offline fastfill scanning.
//			try {
//				netverifySDK = NetverifySDK.create(getActivity(), "YOUROFFLINETOKEN", "YOURPREFERREDCOUNTRY");
//			} catch (SDKExpiredException e) {
//				e.printStackTrace();
//				Toast.makeText(getActivity().getApplicationContext(), "The offline SDK is expired", Toast.LENGTH_LONG).show();
//				return;
//			}

			// Enable ID verification to receive a verification status and verified data positions (see Callback chapter).
			// Note: Not possible for accounts configured as Fastfill only.
			netverifySDK.setEnableVerification(switchVerification.isChecked());

			// You can specify issuing country (ISO 3166-1 alpha-3 country code) and/or ID types and/or document variant to skip
			// their selection during the scanning process.
			// Use the following method to convert ISO 3166-1 alpha-2 into alpha-3 country code.
//			String alpha3 = IsoCountryConverter.convertToAlpha3("AT");
//			netverifySDK.setPreselectedCountry("AUT");
//			ArrayList<NVDocumentType> documentTypes = new ArrayList<>();
//			documentTypes.add(NVDocumentType.PASSPORT);
//			netverifySDK.setPreselectedDocumentTypes(documentTypes);
//			netverifySDK.setPreselectedDocumentVariant(NVDocumentVariant.PLASTIC);

			// The customer internal reference allows you to identify the scan (max. 100 characters).
			// Note: Must not contain sensitive data like PII (Personally Identifiable Information) or account login.
//			netverifySDK.setCustomerInternalReference("YOURSCANREFERENCE");

			// Use the following property to identify the scan in your reports (max. 100 characters).
//			netverifySDK.setReportingCriteria("YOURREPORTINGCRITERIA");

			// You can also set a user reference (max. 100 characters).
			// Note: The user reference should not contain sensitive data like PII (Personally Identifiable Information) or account login.
//			netverifySDK.setUserReference("USERREFERENCE");

			// Callback URL for the confirmation after the verification is completed. This setting overrides your Jumio merchant settings.
//			netverifySDK.setCallbackUrl("YOURCALLBACKURL");

			// You can disable Identity Verification during the ID verification for a specific transaction.
			netverifySDK.setEnableIdentityVerification(switchIdentitiyVerification.isChecked());

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

		} catch (PlatformNotSupportedException | NullPointerException e) {
			Log.e(TAG, "Error in initializeNetverifySDK: ", e);
			if(getActivity() != null){
				Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			}
			netverifySDK = null;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == NetverifySDK.REQUEST_CODE) {
			if (data == null)
				return;

			String scanReference = data.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE);

			if (resultCode == Activity.RESULT_OK) {
				//Handle the success case and retrieve data
				NetverifyDocumentData documentData = data.getParcelableExtra(NetverifySDK.EXTRA_SCAN_DATA);
				NetverifyMrzData mrzData = documentData != null ? documentData.getMrzData() : null;
			} else if (resultCode == Activity.RESULT_CANCELED) {
				//Handle the error cases as described in our documentation: https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_faq.md#managing-errors
				String errorMessage = data.getStringExtra(NetverifySDK.EXTRA_ERROR_MESSAGE);
				String errorCode = data.getStringExtra(NetverifySDK.EXTRA_ERROR_CODE);
			}

			//At this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
			//internal resources can be freed.
			if (netverifySDK != null) {
				netverifySDK.destroy();
				netverifySDK.checkDeallocation(this);
				netverifySDK = null;
			}
		}
	}

	@Override
	public void onNetverifyDeallocated() {
		if (getActivity() != null) {
			getActivity().runOnUiThread(() -> {
				if(btnStart != null) {
					btnStart.setEnabled(true);
				}
			});
		}
	}
}
