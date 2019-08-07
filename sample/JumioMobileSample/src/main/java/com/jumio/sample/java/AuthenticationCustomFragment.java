package com.jumio.sample.java;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.jumio.MobileSDK;
import com.jumio.auth.AuthenticationCallback;
import com.jumio.auth.AuthenticationDeallocationCallback;
import com.jumio.auth.AuthenticationResult;
import com.jumio.auth.AuthenticationSDK;
import com.jumio.auth.custom.AuthenticationCancelReason;
import com.jumio.auth.custom.AuthenticationCustomAnimationView;
import com.jumio.auth.custom.AuthenticationCustomSDKController;
import com.jumio.auth.custom.AuthenticationCustomSDKInterface;
import com.jumio.auth.custom.AuthenticationCustomScanInterface;
import com.jumio.auth.custom.AuthenticationCustomScanView;
import com.jumio.commons.utils.ScreenUtil;
import com.jumio.core.enums.JumioDataCenter;
import com.jumio.core.exceptions.MissingPermissionException;
import com.jumio.core.exceptions.PlatformNotSupportedException;
import com.jumio.sample.R;
import com.jumio.sdk.custom.SDKNotConfiguredException;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

/**
 * Copyright 2019 Jumio Corporation All rights reserved.
 */
public class AuthenticationCustomFragment extends Fragment implements View.OnClickListener, AuthenticationDeallocationCallback {
    private final static String TAG = "AuthenticationCustom";
    private static final int PERMISSION_REQUEST_CODE_AUTHENTICATION_CUSTOM = 304;

    private String apiToken = null;
    private String apiSecret = null;
	private JumioDataCenter dataCenter = null;

    private AuthenticationSDK authenticationSDK = null;

    private ScrollView scrollView;
	private LinearLayout authenticationSettingsContainer;
	private LinearLayout customScanContainer;
	private LinearLayout partTypeLayout;
	private LinearLayout callbackLog;
	private FrameLayout customScanLayout;
	private AuthenticationCustomScanView customScanView;
	private AuthenticationCustomAnimationView customAnimationView;
	private ProgressBar loadingIndicator;
	private EditText enrollmentTransactionReference;
	private Button startCustomScanButton;
	private Button cancelCustomScanButton;
	private Button faceButton;
	private Button errorRetryButton;
	private Button partRetryButton;

	private AuthenticationCustomSDKController customSDKController;
	private Drawable successDrawable;
	private Drawable errorDrawable;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_authentication_custom, container, false);

        Bundle args = getArguments();

        if(args != null) {
        	apiToken = args.getString(MainActivity.KEY_API_TOKEN);
            apiSecret = args.getString(MainActivity.KEY_API_SECRET);
			dataCenter = (JumioDataCenter) args.getSerializable(MainActivity.KEY_DATACENTER);
        }

        scrollView = rootView.findViewById(R.id.scrollView);
		authenticationSettingsContainer = rootView.findViewById(R.id.authenticationSettingsContainer);
        customScanContainer = rootView.findViewById(R.id.authenticationCustomContainer);
        partTypeLayout = rootView.findViewById(R.id.partTypeLayout);
		customScanLayout = rootView.findViewById(R.id.customScanLayout);
		callbackLog = rootView.findViewById(R.id.callbackLog);
        customScanView = rootView.findViewById(R.id.authenticationCustomScanView);
		customAnimationView = rootView.findViewById(R.id.authenticationCustomAnimationView);
		enrollmentTransactionReference = rootView.findViewById(R.id.etEnrollmentTransactionReference);
		startCustomScanButton = rootView.findViewById(R.id.startAuthenticationCustomButton);
		cancelCustomScanButton = rootView.findViewById(R.id.stopAuthenticationCustomButton);
        loadingIndicator = rootView.findViewById(R.id.loadingIndicator);
        faceButton = rootView.findViewById(R.id.faceButton);
        errorRetryButton = rootView.findViewById(R.id.errorRetryButton);
		partRetryButton = rootView.findViewById(R.id.partRetryButton);

		startCustomScanButton.setOnClickListener(this);
		cancelCustomScanButton.setOnClickListener(this);
        faceButton.setOnClickListener(this);
        errorRetryButton.setOnClickListener(this);
		partRetryButton.setOnClickListener(this);

		startCustomScanButton.setText(String.format(getResources().getString(R.string.button_start), getResources().getString(R.string.section_authentication)));

        successDrawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.success));
        errorDrawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.error));

        initScanView();

		hideView(false, errorRetryButton, partRetryButton, customAnimationView);

        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        initScanView();
    }

    @Override
    public void onPause() {
        try {
            if (customSDKController != null)
                customSDKController.pause();
        } catch (SDKNotConfiguredException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
        	if(cancelCustomScanButton != null) {
				cancelCustomScanButton.performClick();
			}
            if (customSDKController != null) {
				customSDKController.destroy();
			}
        } catch (SDKNotConfiguredException e) {
            e.printStackTrace();
        }
    }

    private boolean isPortrait() {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.y > size.x;
	}

    private void initScanView() {
    	boolean isPortrait = isPortrait();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(isPortrait ? FrameLayout.LayoutParams.MATCH_PARENT : FrameLayout.LayoutParams.WRAP_CONTENT, isPortrait ? FrameLayout.LayoutParams.WRAP_CONTENT : ScreenUtil.dpToPx(getActivity(), 300));
        customScanView.setLayoutParams(params);
    }

    @Override
    public void onClick(View v) {
        v.setEnabled(false);
		boolean keepDisabled = false;
        if (v == startCustomScanButton) {
            if (!MobileSDK.hasAllRequiredPermissions(getActivity())) {
                ActivityCompat.requestPermissions(getActivity(), MobileSDK.getMissingPermissions(getActivity()), PERMISSION_REQUEST_CODE_AUTHENTICATION_CUSTOM);
            } else {
				authenticationSettingsContainer.setVisibility(View.GONE);
				customScanContainer.setVisibility(View.VISIBLE);

				InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				if(inputMethodManager != null && enrollmentTransactionReference != null) {
					inputMethodManager.hideSoftInputFromWindow(enrollmentTransactionReference.getWindowToken(), 0);
				}

				hideView(true, errorRetryButton, partRetryButton, customAnimationView);
				callbackLog.removeAllViews();

				try {
					initializeAuthenticationSDK();
					keepDisabled = true;
				} catch (IllegalArgumentException e) {
					Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
				}
			}
        } else if (v == cancelCustomScanButton && isSDKControllerValid()) {
            hideView(false, cancelCustomScanButton, partTypeLayout, customScanLayout, loadingIndicator);
            callbackLog.removeAllViews();
            try {
                customSDKController.pause();
                customSDKController.destroy();
            } catch (SDKNotConfiguredException e) {
                addToCallbackLog(e.getMessage());
            }
            if (authenticationSDK != null) {
				authenticationSDK.destroy();
				authenticationSDK.checkDeallocation(AuthenticationCustomFragment.this);
				authenticationSDK = null;
            }
            customSDKController = null;
            customScanContainer.setVisibility(View.GONE);
            authenticationSettingsContainer.setVisibility(View.VISIBLE);
        } else if (v == faceButton && isSDKControllerValid()) {
			showView(true, customScanLayout);

			scrollView.post(new Runnable() {
				@Override
				public void run() {
					scrollView.scrollTo(0, customScanLayout.getTop());
					scrollView.postDelayed(new ScanPartRunnable(), 250);
				}
			});
		} else if (v == partRetryButton && isSDKControllerValid()) {
			customAnimationView.destroy();
			hideView(false, partRetryButton, customAnimationView);
			showView(true, customScanLayout);

			scrollView.post(new Runnable() {
				@Override
				public void run() {
					scrollView.scrollTo(0, customScanLayout.getTop());
					scrollView.postDelayed(new RetryPartRunnable(), 250);
				}
			});
		} else if (v == errorRetryButton && isSDKControllerValid()) {
			hideView(true, errorRetryButton);
			try {
				customSDKController.retry();
			} catch (SDKNotConfiguredException e) {
				addToCallbackLog(e.getMessage());
			}
		}

		if (!keepDisabled)
			v.setEnabled(true);
    }

	private void initializeAuthenticationSDK() {
		try {
			// You can get the current SDK version using the method below.
//			AuthenticationSDK.getSDKVersion();

			// Call the method isSupportedPlatform to check if the device is supported.
			if (!AuthenticationSDK.isSupportedPlatform(getActivity()))
				android.util.Log.w(TAG, "Device not supported");

			// Applications implementing the SDK shall not run on rooted devices. Use either the below
			// method or a self-devised check to prevent usage of SDK scanning functionality on rooted
			// devices.
			if (AuthenticationSDK.isRooted(getActivity()))
				android.util.Log.w(TAG, "Device is rooted");

			// To create an instance of the SDK, perform the following call as soon as your activity is initialized.
			// Make sure that your merchant API token and API secret are correct and specify an instance
			// of your activity. If your merchant account is created in the EU data center, use
			// JumioDataCenter.EU instead.
			authenticationSDK = AuthenticationSDK.create(getActivity(), apiToken, apiSecret, dataCenter);

			// Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
//			authenticationSDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID);

			// You can also set a customer identifier (max. 100 characters).
			// Note: The customer ID should not contain sensitive data like PII (Personally Identifiable Information) or account login.
//			authenticationSDK.setUserReference("USERREFERENCE");

			// Callback URL (max. 255 characters) for the confirmation after authentication is completed. This setting overrides your Jumio merchant settings.
//			authenticationSDK.setCallbackUrl("YOURCALLBACKURL");

			// The scan reference of an eligible Netverify scan has to be used as the enrollmentTransactionReference
			authenticationSDK.setEnrollmentTransactionReference(enrollmentTransactionReference.getText().toString());

			// Instead an Authentication transaction can also be created via the facemap server to server API and set here
			// authenticationSDK.setAuthenticationTransactionReference("YOURAUTHENTICATIONTRANSACTIONREFERENCE");

			// Use the following method to initialize the SDK
			authenticationSDK.initiate(new AuthenticationCallback() {
				@Override
				public void onAuthenticationInitiateSuccess() {
					try {
						showView(true, cancelCustomScanButton, partTypeLayout, faceButton);
						faceButton.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null);

						if (authenticationSDK != null) {
							customSDKController = authenticationSDK.start(new AuthenticationCustomSDKImpl());
							customSDKController.resume();
						}
					} catch (NullPointerException | MissingPermissionException | SDKNotConfiguredException e) {
						android.util.Log.e(TAG, "Error in initializeAuthenticationSDK: ", e);
						Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
						authenticationSDK = null;

						authenticationSettingsContainer.setVisibility(View.VISIBLE);
						customScanContainer.setVisibility(View.GONE);
						hideView(false, loadingIndicator);
						startCustomScanButton.setEnabled(true);
					}
				}

				@Override
				public void onAuthenticationInitiateError(String errorCode, String errorMessage, boolean retryPossible) {
					Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();

					authenticationSettingsContainer.setVisibility(View.VISIBLE);
					customScanContainer.setVisibility(View.GONE);
					hideView(false, loadingIndicator);
					startCustomScanButton.setEnabled(true);
				}
			});

		} catch (PlatformNotSupportedException | NullPointerException | MissingPermissionException |IllegalArgumentException e) {
			android.util.Log.e(TAG, "Error in initializeAuthenticationSDK: ", e);
			Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			authenticationSDK = null;

			authenticationSettingsContainer.setVisibility(View.VISIBLE);
			customScanContainer.setVisibility(View.GONE);
			hideView(false, loadingIndicator);
			startCustomScanButton.setEnabled(true);
		}
	}


	private boolean isSDKControllerValid() {
        return customSDKController != null;
    }

	@Override
	public void onAuthenticationDeallocated() {
		if(getActivity() != null) {
	        getActivity().runOnUiThread(() -> {
		        if(startCustomScanButton != null) {
			        startCustomScanButton.setEnabled(true);
		        }
	        });
		}
	}

	private class ScanPartRunnable implements Runnable {
		@Override
		public void run() {
			try {
				int[] location = new int[2];
				customScanView.getLocationOnScreen(location);

				Rect rectangle = new Rect();
				getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);

				customScanView.setCloseButtonWidth(ScreenUtil.dpToPx(getActivity(), 24));
				customScanView.setCloseButtonHeight(ScreenUtil.dpToPx(getActivity(), 24));
				customScanView.setCloseButtonTop(location[1] - rectangle.top);
				customScanView.setCloseButtonLeft(location[0] - rectangle.left);
				customScanView.setCloseButtonResId(R.drawable.jumio_close_button);

				customSDKController.startScan(customScanView, new AuthenticationCustomScanImpl());
				addToCallbackLog("help text: " + customSDKController.getHelpText());
			} catch (SDKNotConfiguredException e) {
				addToCallbackLog(e.getMessage());
			}
		}
	}

	private class RetryPartRunnable implements Runnable {
		@Override
		public void run() {
			try {
				int[] location = new int[2];
				customScanView.getLocationOnScreen(location);

				Rect rectangle = new Rect();
				getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);

				customScanView.setCloseButtonWidth(ScreenUtil.dpToPx(getActivity(), 24));
				customScanView.setCloseButtonHeight(ScreenUtil.dpToPx(getActivity(), 24));
				customScanView.setCloseButtonTop(location[1] - rectangle.top);
				customScanView.setCloseButtonLeft(location[0] - rectangle.left);
				customScanView.setCloseButtonResId(R.drawable.jumio_close_button);

				customSDKController.retryScan();
			} catch (Exception e) {
				addToCallbackLog(e.getMessage());
			}
		}
	}

    private class AuthenticationCustomSDKImpl implements AuthenticationCustomSDKInterface {

		@Override
		public void onAuthenticationFinished(AuthenticationResult authenticationResult, String scanReference) {
			addToCallbackLog("onAuthenticationFinished");
			hideView(false, partTypeLayout, loadingIndicator, errorRetryButton);

			appendKeyValue("Scan reference", scanReference);

			if (authenticationResult != null) {
				appendKeyValue("Result", authenticationResult.toString());
			}
		}

		@Override
		public void onAuthenticationError(String errorCode, String errorMessage, boolean retryPossible, String scanReference) {
			showView(true, errorRetryButton);
			addToCallbackLog(String.format("onAuthenticationError: %s, %s, %d, %s", errorCode, errorMessage, retryPossible ? 0 : 1, scanReference != null ? scanReference : "null"));

			if(errorCode.startsWith("M")) {
				appendKeyValue("Scan reference", scanReference);
				hideView(false, partTypeLayout, loadingIndicator, errorRetryButton, partRetryButton);
			}
		}
	}

    private class AuthenticationCustomScanImpl implements AuthenticationCustomScanInterface {

		@Override
		public void onAuthenticationScanProcessing() {
			addToCallbackLog("onAuthenticationScanProcessing");
			if (customScanLayout.getVisibility() == View.VISIBLE)
				hideView(false, customScanLayout);
			hideView(false, loadingIndicator);
			faceButton.setCompoundDrawablesWithIntrinsicBounds(successDrawable, null, null, null);
		}

		@Override
		public void onAuthenticationScanCanceled(AuthenticationCancelReason cancelReason) {
			addToCallbackLog(String.format("onAuthenticationScanCanceled reason: %s, helptext: %s", cancelReason.toString(), customSDKController.getHelpText()));

			customSDKController.getHelpAnimation(customAnimationView);
			showView(false, partRetryButton, customAnimationView);

			faceButton.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null);
		}

		@Override
		public void onAuthenticationScanForPartFinished() {
			addToCallbackLog("onAuthenticationScanForPartFinished");
		}

		@Override
		public void onAuthenticationFaceInLandscape() {
			addToCallbackLog("onAuthenticationFaceInLandscape");

			customSDKController.getHelpAnimation(customAnimationView);
			showView(false, partRetryButton, customAnimationView);

			faceButton.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null);
		}
	}

    private void showView(boolean hideLoading, View... views) {
        if (hideLoading)
            loadingIndicator.setVisibility(View.GONE);
        for (View view : views)
            view.setVisibility(View.VISIBLE);
    }

    private void hideView(boolean showLoading, View... views) {
        for (View view : views)
            view.setVisibility(View.GONE);
        if (showLoading)
            loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void addToCallbackLog(String message) {
        Log.d("UI-Less", message);
        try {
            Context context = getActivity();
            if (context == null)
                return;
            TextView logline = new TextView(context);
            logline.setText(message);
            callbackLog.addView(logline, 0);
            if (callbackLog.getChildCount() > 40)
                callbackLog.removeViewAt(callbackLog.getChildCount() - 1);
        } catch (Exception e) {
            Log.e("UI-Less", String.format("Could not write to callback log: %s", e.getMessage()));
            Log.e("UI-Less", message);
        }
    }

    private void appendKeyValue(String key, CharSequence value) {
        addToCallbackLog(String.format("%s: %s", key, value));
    }
}
