package com.jumio.sample.java;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.jumio.auth.AuthenticationCallback;
import com.jumio.auth.AuthenticationDeallocationCallback;
import com.jumio.auth.AuthenticationResult;
import com.jumio.auth.AuthenticationSDK;
import com.jumio.core.enums.JumioDataCenter;
import com.jumio.core.exceptions.MissingPermissionException;
import com.jumio.core.exceptions.PlatformNotSupportedException;
import com.jumio.sample.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class AuthenticationFragment extends Fragment implements View.OnClickListener, AuthenticationDeallocationCallback {
	private final static String TAG = "JumioSDK_Authentication";
	private static final int PERMISSION_REQUEST_CODE_AUTHENTICATION = 304;

	private String apiToken = null;
	private String apiSecret = null;
	private JumioDataCenter dataCenter = null;

	private AuthenticationSDK authenticationSDK;

	private EditText etScanRef = null;
	private Button startSDK;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		rootView.findViewById(R.id.tvOptions).setVisibility(View.GONE);
		rootView.findViewById(R.id.switchOptionOne).setVisibility(View.GONE);
		rootView.findViewById(R.id.switchOptionTwo).setVisibility(View.GONE);

		Bundle args = getArguments();

		if(args != null) {
			apiToken = args.getString(MainActivity.KEY_API_TOKEN);
			apiSecret = args.getString(MainActivity.KEY_API_SECRET);
			dataCenter = (JumioDataCenter) args.getSerializable(MainActivity.KEY_DATACENTER);
		}

		TextInputLayout textInputLayoutScanRef = rootView.findViewById(R.id.tilOptional);
		textInputLayoutScanRef.setVisibility(View.VISIBLE);
		etScanRef = rootView.findViewById(R.id.etOptional);

		startSDK = rootView.findViewById(R.id.btnStart);
		startSDK.setText(String.format(getResources().getString(R.string.button_start), getResources().getString(R.string.section_authentication)));
		startSDK.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onClick(View view) {
		//Since the Authentication is a singleton internally, a new instance is not
		//created here.
		startSDK.setEnabled(false);
		initializeAuthenticationSDK();
	}

	private void initializeAuthenticationSDK() {
		try {
			// You can get the current SDK version using the method below.
//			AuthenticationSDK.getSDKVersion();

			// Call the method isSupportedPlatform to check if the device is supported.
			if (!AuthenticationSDK.isSupportedPlatform(getActivity()))
				Log.w(TAG, "Device not supported");

			// Applications implementing the SDK shall not run on rooted devices. Use either the below
			// method or a self-devised check to prevent usage of SDK scanning functionality on rooted
			// devices.
			if (AuthenticationSDK.isRooted(getActivity()))
				Log.w(TAG, "Device is rooted");

			// To create an instance of the SDK, perform the following call as soon as your activity is initialized.
			// Make sure that your merchant API token and API secret are correct and specify an instance
			// of your activity. If your merchant account is created in the EU data center, use
			// JumioDataCenter.EU instead.
			authenticationSDK = AuthenticationSDK.create(getActivity(), apiToken, apiSecret, dataCenter);

			// Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
//			authenticationSDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID);

			// You can also set a user reference (max. 100 characters).
			// Note: The user reference should not contain sensitive data like PII (Personally Identifiable Information) or account login.
//			authenticationSDK.setUserReference("USERREFERENCE");

			// Callback URL (max. 255 characters) for the confirmation after authentication is completed. This setting overrides your Jumio merchant settings.
//			authenticationSDK.setCallbackUrl("YOURCALLBACKURL");

			// The scan reference of an eligible Netverify scan has to be used as the enrollmentTransactionReference
			authenticationSDK.setEnrollmentTransactionReference(etScanRef.getText().toString());

			// Instead an Authentication transaction can also be created via the facemap server to server API and set here
			// authenticationSDK.setAuthenticationTransactionReference("YOURAUTHENTICATIONTRANSACTIONREFERENCE");

			// Use the following method to initialize the SDK
			if (((MainActivity) getActivity()).checkPermissions(PERMISSION_REQUEST_CODE_AUTHENTICATION)) {
				authenticationSDK.initiate(new AuthenticationCallback() {
					@Override
					public void onAuthenticationInitiateSuccess() {
						try {
							startActivityForResult(authenticationSDK.getIntent(), AuthenticationSDK.REQUEST_CODE);
						} catch (MissingPermissionException e) {
							Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void onAuthenticationInitiateError(String errorCode, String errorMessage, boolean retryPossible) {
						Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
						startSDK.setEnabled(true);
					}
				});
			} else {
				startSDK.setEnabled(true);
			}

		} catch (PlatformNotSupportedException | NullPointerException | MissingPermissionException | IllegalArgumentException e) {
			Log.e(TAG, "Error in initializeAuthenticationSDK: ", e);
			if(getActivity() != null) {
				Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			}
			authenticationSDK = null;

			startSDK.setEnabled(true);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == AuthenticationSDK.REQUEST_CODE) {
			if (data == null)
				return;
			if (resultCode == Activity.RESULT_OK) {
				String transactionReference = data.getStringExtra(AuthenticationSDK.EXTRA_TRANSACTION_REFERENCE);
				AuthenticationResult authenticationResult = (AuthenticationResult) data.getSerializableExtra(AuthenticationSDK.EXTRA_SCAN_DATA);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				String errorMessage = data.getStringExtra(AuthenticationSDK.EXTRA_ERROR_MESSAGE);
				String errorCode = data.getStringExtra(AuthenticationSDK.EXTRA_ERROR_CODE);
			}

			//At this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
			//internal resources can be freed.
			if (authenticationSDK != null) {
				authenticationSDK.destroy();
				authenticationSDK.checkDeallocation(this);
				authenticationSDK = null;
			}
		}
	}

	@Override
	public void onAuthenticationDeallocated() {
		if(getActivity() != null) {
			getActivity().runOnUiThread(() -> {
				if(startSDK != null) {
					startSDK.setEnabled(true);
				}
			});
		}
	}
}