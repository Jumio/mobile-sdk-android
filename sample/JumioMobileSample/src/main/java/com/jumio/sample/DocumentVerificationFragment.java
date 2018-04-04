package com.jumio.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.jumio.core.enums.JumioDataCenter;
import com.jumio.core.exceptions.*;
import com.jumio.dv.DocumentVerificationSDK;

/**
 * Copyright 2018 Jumio Corporation All rights reserved.
 */
public class DocumentVerificationFragment extends Fragment implements View.OnClickListener {
	private final static String TAG = "JumioSDK_DV";
	private static final int PERMISSION_REQUEST_CODE_DOCUMENT_VERIFICATION = 301;

	private String apiToken = null;
	private String apiSecret = null;

	DocumentVerificationSDK documentVerificationSDK;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		(rootView.findViewById(R.id.switchOptionOne)).setVisibility(View.GONE);
		(rootView.findViewById(R.id.switchOptionTwo)).setVisibility(View.GONE);
		(rootView.findViewById(R.id.tvOptions)).setVisibility(View.GONE);

		Bundle args = getArguments();

		apiToken = args.getString(MainActivity.KEY_API_TOKEN);
		apiSecret = args.getString(MainActivity.KEY_API_SECRET);

		Button startSDK = (Button) rootView.findViewById(R.id.btnStart);
		startSDK.setText(args.getString(MainActivity.KEY_BUTTON_TEXT));
		startSDK.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onClick(View view) {
		//Since the DocumentVerificationSDK is a singleton internally, a new instance is not
		//created here.
		initializeDocumentVerificationSDK();

		if (((MainActivity) getActivity()).checkPermissions(PERMISSION_REQUEST_CODE_DOCUMENT_VERIFICATION)) {
			try {
				if (documentVerificationSDK != null) {
					startActivityForResult(documentVerificationSDK.getIntent(), DocumentVerificationSDK.REQUEST_CODE);
				}
			} catch (MissingPermissionException e) {
				Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	}

	private void initializeDocumentVerificationSDK() {
		try {
			// You can get the current SDK version using the method below.
			// DocumentVerificationSDK.getSDKVersion();

			// Call the method isSupportedPlatform to check if the device is supported.
			if (!DocumentVerificationSDK.isSupportedPlatform(getActivity()))
				Log.w(TAG, "Device not supported");

			// Applications implementing the SDK shall not run on rooted devices. Use either the below
			// method or a self-devised check to prevent usage of SDK scanning functionality on rooted
			// devices.
			if (DocumentVerificationSDK.isRooted(getActivity()))
				Log.w(TAG, "Device is rooted");

			// To create an instance of the SDK, perform the following call as soon as your activity is initialized.
			// Make sure that your merchant API token and API secret are correct and specify an instance
			// of your activity. If your merchant account is created in the EU data center, use
			// JumioDataCenter.EU instead.
			documentVerificationSDK = DocumentVerificationSDK.create(getActivity(), apiToken, apiSecret, JumioDataCenter.US);

			// One of the configured DocumentTypeCodes: BC, BS, CAAP, CB, CCS, CRC, HCC, IC, LAG, LOAP,
			// MEDC, MOAP, PB, SEL, SENC, SS, STUC, TAC, TR, UB, SSC, USSS, VC, VT, WWCC, CUSTOM
			documentVerificationSDK.setType("BC");

			// ISO 3166-1 alpha-3 country code
			documentVerificationSDK.setCountry("USA");

			// The merchant scan reference allows you to identify the scan (max. 100 characters).
			// Note: Must not contain sensitive data like PII (Personally Identifiable Information) or account login.
			documentVerificationSDK.setMerchantScanReference("YOURSCANREFERENCE");

			// You can also set a customer identifier (max. 100 characters).
			// Note: The customer ID should not contain sensitive data like PII (Personally Identifiable Information) or account login.
			documentVerificationSDK.setCustomerId("CUSTOMERID");

			// One of the Custom Document Type Codes as configurable by Merchant in Merchant UI.
			// documentVerificationSDK.setCustomDocumentCode("YOURCUSTOMDOCUMENTCODE");

			// Overrides the label for the document name (on Help Screen below document icon)
			// documentVerificationSDK.setDocumentName("DOCUMENTNAME");

			// Use the following property to identify the scan in your reports (max. 255 characters).
			// documentVerificationSDK.setMerchantReportingCriteria("YOURREPORTINGCRITERIA");

			// Callback URL for the confirmation after the verification is completed. This setting overrides your Jumio merchant settings.
			// documentVerificationSDK.setCallbackUrl("YOURCALLBACKURL");

			// Use the following method to set the default camera position.
			// documentVerificationSDK.setCameraPosition(JumioCameraPosition.FRONT);

			// Additional information for this scan should not contain sensitive data like PII (Personally Identifiable Information) or account login
			// documentVerificationSDK.setAdditionalInformation("YOURADDITIONALINFORMATION");

			// Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
			//documentVerificationSDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID);

		} catch (PlatformNotSupportedException e) {
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(), "This platform is not supported", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == DocumentVerificationSDK.REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				//Handle the result for the DocumentVerification SDK
				if (data == null) {
					return;
				}
			}
			//At this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
			//internal resources can be freed.
			if (documentVerificationSDK != null) {
				documentVerificationSDK.destroy();
				documentVerificationSDK = null;
			}
		}
	}
}

