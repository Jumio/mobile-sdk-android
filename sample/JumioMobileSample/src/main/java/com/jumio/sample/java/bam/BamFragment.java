package com.jumio.sample.java.bam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jumio.bam.BamCardInformation;
import com.jumio.bam.BamSDK;
import com.jumio.core.enums.JumioDataCenter;
import com.jumio.core.exceptions.MissingPermissionException;
import com.jumio.core.exceptions.PlatformNotSupportedException;
import com.jumio.sample.R;
import com.jumio.sample.java.MainActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Copyright 2019 Jumio Corporation All rights reserved.
 */
public class BamFragment extends Fragment implements View.OnClickListener {
	private final static String TAG = "JumioSDK_Bam";
	private static final int PERMISSION_REQUEST_CODE_BAM = 300;

	private String apiToken = null;
	private String apiSecret = null;
	private JumioDataCenter dataCenter = null;

	private BamSDK bamSDK;


	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		SwitchMaterial switchExpiryDate = rootView.findViewById(R.id.switchOptionOne);
		SwitchMaterial switchCvv = rootView.findViewById(R.id.switchOptionTwo);

		Bundle args = getArguments();

		switchExpiryDate.setText(getResources().getString(R.string.bam_expiry_required));
		switchCvv.setText(getResources().getString(R.string.bam_cvv_required));

		if (args != null) {
			apiToken = args.getString(MainActivity.KEY_API_TOKEN);
			apiSecret = args.getString(MainActivity.KEY_API_SECRET);
			dataCenter = (JumioDataCenter) args.getSerializable(MainActivity.KEY_DATACENTER);
		}

		Button startSDK = rootView.findViewById(R.id.btnStart);
		startSDK.setText(String.format(getResources().getString(R.string.button_start), getResources().getString(R.string.section_bam)));
		startSDK.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onClick(View view) {
		//Since the BamSDK is a singleton internally, a new instance is not
		//created here.
		if (bamSDK != null)
			bamSDK.clearCustomFields();
		initializeBamSDK();

		if(getActivity() != null) {
			if (((MainActivity) getActivity()).checkPermissions(PERMISSION_REQUEST_CODE_BAM)) {
				try {
					if (bamSDK != null) {
						startActivityForResult(bamSDK.getIntent(), BamSDK.REQUEST_CODE);
					}
				} catch (MissingPermissionException e) {
					Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private void initializeBamSDK() {
		try {
			// You can get the current SDK version using the method below.
//			BamSDK.getSDKVersion();

			// Call the method isSupportedPlatform to check if the device is supported.
			if (!BamSDK.isSupportedPlatform(getActivity()))
				Log.w(TAG, "Device not supported");

			// Applications implementing the SDK shall not run on rooted devices. Use either the below
			// method or a self-devised check to prevent usage of SDK scanning functionality on rooted
			// devices.
			if (BamSDK.isRooted(getActivity()))
				Log.w(TAG, "Device is rooted");

			// To create an instance of the SDK, perform the following call as soon as your activity is initialized.
			// Make sure that your merchant API token and API secret are correct and specify an instance
			// of your activity. If your merchant account is created in the EU data center, use
			// JumioDataCenter.EU instead.
			bamSDK = BamSDK.create(getActivity(), apiToken, apiSecret, dataCenter);

			// Use the following method to enable offline credit card scanning.
//			try {
//				bamSDK = BamSDK.create(SampleActivity.this, "YOUROFFLINETOKEN");
//			} catch (SDKExpiredException e) {
//				e.printStackTrace();
//				Toast.makeText(getApplicationContext(), "The offline SDK is expired", Toast.LENGTH_LONG).show();
//			}

			// Overwrite your specified reporting criteria to identify each scan attempt in your reports (max. 100 characters).
//			bamSDK.setMerchantReportingCriteria("YOURREPORTINGCRITERIA");

			// To restrict supported card types, pass an ArrayList of CreditCardTypes to the setSupportedCreditCardTypes method.
//			ArrayList<CreditCardType> creditCardTypes = new ArrayList<CreditCardType>();
//			creditCardTypes.add(CreditCardType.VISA);
//			creditCardTypes.add(CreditCardType.MASTER_CARD);
//			creditCardTypes.add(CreditCardType.AMERICAN_EXPRESS);
//			creditCardTypes.add(CreditCardType.DINERS_CLUB);
//			creditCardTypes.add(CreditCardType.DISCOVER);
//			creditCardTypes.add(CreditCardType.CHINA_UNIONPAY);
//			creditCardTypes.add(CreditCardType.JCB);
//			bamSDK.setSupportedCreditCardTypes(creditCardTypes);

			// Expiry recognition, card holder name and CVV entry are enabled by default and can be disabled.
			// You can enable the recognition of sort code and account number.
//			bamSDK.setExpiryRequired(false);
//			bamSDK.setCardHolderNameRequired(false);
//			bamSDK.setCvvRequired(false);
//			bamSDK.setSortCodeAndAccountNumberRequired(true);

			// You can show the unmasked credit card number to the user during the workflow if setCardNumberMaskingEnabled is disabled.
//			bamSDK.setCardNumberMaskingEnabled(false);

			// The user can edit the recognized expiry date if setExpiryEditable is enabled.
//			bamSDK.setExpiryEditable(true);

			// The user can edit the recognized card holder name if setCardHolderNameEditable is enabled.
//			bamSDK.setCardHolderNameEditable(true);

			// You can set a short vibration and sound effect to notify the user that the card has been detected.
//			bamSDK.setVibrationEffectEnabled(true);
//			bamSDK.setSoundEffect(R.raw.shutter_sound);

			// Use the following method to set the default camera position.
//			bamSDK.setCameraPosition(JumioCameraPosition.FRONT);

			// Automatically enable flash when scan is started.
//			bamSDK.setEnableFlashOnScanStart(true);

			// You can add custom fields to the confirmation page (keyboard entry or predefined values).
//			bamSDK.addCustomField("zipCodeId", getString(R.string.zip_code), InputType.TYPE_CLASS_NUMBER, "[0-9]{5,}");
			// ArrayList<String> states = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.state_selection_values)));
//			bamSDK.addCustomField("stateId", getString(R.string.state), states, false, getString(R.string.state_reset_value));

			// Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
//			bamSDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID);

		} catch (PlatformNotSupportedException | NullPointerException e) {
			e.printStackTrace();
			if(getActivity() != null) {
				Toast.makeText(getActivity().getApplicationContext(), "This platform is not supported", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == BamSDK.REQUEST_CODE) {
			if (data == null){
				return;
			}
			ArrayList<String> scanAttempts = data.getStringArrayListExtra(BamSDK.EXTRA_SCAN_ATTEMPTS);

			if (resultCode == Activity.RESULT_OK) {
				//Handle the success case and retrieve scan data
				BamCardInformation cardInformation = data.getParcelableExtra(BamSDK.EXTRA_CARD_INFORMATION);
				cardInformation.clear();
			} else if (resultCode == Activity.RESULT_CANCELED) {
				//Handle the error cases as described in our documentation: https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_faq.md#managing-errors
				String errorMessage = data.getStringExtra(BamSDK.EXTRA_ERROR_MESSAGE);
				String errorCode = data.getStringExtra(BamSDK.EXTRA_ERROR_CODE);
			}

			//At this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
			//internal resources can be freed.
			if (bamSDK != null) {
				bamSDK.destroy();
				bamSDK = null;
			}
		}
	}
}
