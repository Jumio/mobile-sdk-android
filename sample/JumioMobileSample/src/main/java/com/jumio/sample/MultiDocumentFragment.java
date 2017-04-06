package com.jumio.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.jumio.core.enums.JumioDataCenter;
import com.jumio.core.exceptions.PlatformNotSupportedException;
import com.jumio.md.MultiDocumentSDK;

/**
 * Copyright 2017 Jumio Corporation All rights reserved.
 */
public class MultiDocumentFragment extends Fragment implements View.OnClickListener {
	private final static String TAG = "JumioSDK_MultiDocument";
	private static final int PERMISSION_REQUEST_CODE_MULTI_DOCUMENT = 301;

	private String apiToken = null;
	private String apiSecret = null;

	MultiDocumentSDK multiDocumentSDK;

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
		//Since the MultiDocumentSDK is a singleton internally, a new instance is not
		//created here.
		initializeMultiDocumentSDK();
		((MainActivity) getActivity()).checkPermissionsAndStart(multiDocumentSDK, PERMISSION_REQUEST_CODE_MULTI_DOCUMENT);
	}

	private void initializeMultiDocumentSDK() {
		try {
			// You can get the current SDK version using the method below.
			// MultiDocumentSDK.getSDKVersion();

			// Call the method isSupportedPlatform to check if the device is supported.
			// MultiDocumentSDK.isSupportedPlatform();

			// Applications implementing the SDK shall not run on rooted devices. Use either the below
			// method or a self-devised check to prevent usage of SDK scanning functionality on rooted
			// devices.
			if (MultiDocumentSDK.isRooted(getActivity()))
				Log.w(TAG, "Device is rooted");

			// To create an instance of the SDK, perform the following call as soon as your activity is initialized.
			// Make sure that your merchant API token and API secret are correct and specify an instance
			// of your activity. If your merchant account is created in the EU data center, use
			// JumioDataCenter.EU instead.
			multiDocumentSDK = MultiDocumentSDK.create(getActivity(), apiToken, apiSecret, JumioDataCenter.US);

			// One of the configured DocumentTypeCodes: BC, BS, CAAP, CB, CCS, CRC, HCC, IC, LAG, LOAP,
			// MEDC, MOAP, PB, SEL, SENC, SS, STUC, TAC, TR, UB, SSC, USSS, VC, VT, WWCC, CUSTOM
			multiDocumentSDK.setType("BC");

			// ISO 3166-1 alpha-3 country code
			multiDocumentSDK.setCountry("USA");

			// The merchant scan reference allows you to identify the scan (max. 100 characters).
			// Note: Must not contain sensitive data like PII (Personally Identifiable Information) or account login.
			multiDocumentSDK.setMerchantScanReference("YOURSCANREFERENCE");

			// You can also set a customer identifier (max. 100 characters).
			// Note: The customer ID should not contain sensitive data like PII (Personally Identifiable Information) or account login.
			multiDocumentSDK.setCustomerId("CUSTOMERID");

			// One of the Custom Document Type Codes as configurable by Merchant in Merchant UI.
			// multiDocumentSDK.setCustomDocumentCode("YOURCUSTOMDOCUMENTCODE");

			// Overrides the label for the document name (on Help Screen below document icon)
			// multiDocumentSDK.setDocumentName("DOCUMENTNAME");

			// Use the following property to identify the scan in your reports (max. 255 characters).
			// multiDocumentSDK.setMerchantReportingCriteria("YOURREPORTINGCRITERIA");

			// Callback URL for the confirmation after the verification is completed. This setting overrides your Jumio merchant settings.
			// multiDocumentSDK.setCallbackUrl("YOURCALLBACKURL");

			// Use the following method to set the default camera position.
			// multiDocumentSDK.setCameraPosition(JumioCameraPosition.FRONT);

			// Use the following method to disable showing help before scanning.
			// multiDocumentSDK.setShowHelpBeforeScan(false);

			// Additional information for this scan should not contain sensitive data like PII (Personally Identifiable Information) or account login
			// multiDocumentSDK.setAdditionalInformation("YOURADDITIONALINFORMATION");

			// Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
			//multiDocumentSDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID);

		} catch (PlatformNotSupportedException e) {
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(), "This platform is not supported", Toast.LENGTH_LONG).show();
		}
	}
}

