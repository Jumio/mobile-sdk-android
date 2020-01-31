package com.jumio.sample.java;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.jumio.MobileSDK;
import com.jumio.commons.utils.ScreenUtil;
import com.jumio.core.data.document.ScanSide;
import com.jumio.core.enums.JumioDataCenter;
import com.jumio.core.exceptions.MissingPermissionException;
import com.jumio.core.exceptions.PlatformNotSupportedException;
import com.jumio.nv.NetverifyDeallocationCallback;
import com.jumio.nv.NetverifyDocumentData;
import com.jumio.nv.NetverifyMrzData;
import com.jumio.nv.NetverifySDK;
import com.jumio.nv.custom.NetverifyCancelReason;
import com.jumio.nv.custom.NetverifyConfirmationType;
import com.jumio.nv.custom.NetverifyCountry;
import com.jumio.nv.custom.NetverifyCustomAnimationView;
import com.jumio.nv.custom.NetverifyCustomConfirmationView;
import com.jumio.nv.custom.NetverifyCustomSDKController;
import com.jumio.nv.custom.NetverifyCustomSDKInterface;
import com.jumio.nv.custom.NetverifyCustomScanInterface;
import com.jumio.nv.custom.NetverifyCustomScanPresenter;
import com.jumio.nv.custom.NetverifyCustomScanView;
import com.jumio.nv.custom.NetverifyScanMode;
import com.jumio.nv.nfc.custom.NetverifyCustomNfcAccess;
import com.jumio.nv.nfc.custom.NetverifyCustomNfcInterface;
import com.jumio.nv.nfc.custom.NetverifyCustomNfcPresenter;
import com.jumio.sdk.custom.SDKNotConfiguredException;
import com.jumio.nv.data.document.NVDocumentType;
import com.jumio.nv.data.document.NVDocumentVariant;
import com.jumio.sample.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

/**
 * Copyright 2019 Jumio Corporation All rights reserved.
 */
public class NetverifyCustomFragment extends Fragment implements View.OnClickListener, NetverifyDeallocationCallback {
    private final static String TAG = "NetverifyCustom";
    private static final int PERMISSION_REQUEST_CODE_NETVERIFY_CUSTOM = 303;

    private String apiToken = null;
    private String apiSecret = null;
    private JumioDataCenter dataCenter = null;

    private NetverifySDK netverifySDK = null;

    private ScrollView scrollView;
    private LinearLayout customScanContainer;
    private LinearLayout countryDocumentLayout;
    private LinearLayout partTypeLayout;
    private LinearLayout callbackLog;
    private LinearLayout netverifySettingsContainer;
    private LinearLayout customNfcAccessLayout;
    private LinearLayout userConsentLayout;
    private RelativeLayout customScanLayout;
    private RelativeLayout customConfirmLayout;
    private RelativeLayout customNfcLayout;
    private NetverifyCustomScanView customScanView;
    private NetverifyCustomConfirmationView customConfirmationView;
    private NetverifyCustomAnimationView customAnimationView;
    private ProgressBar loadingIndicator;
    private Spinner customCountrySpinner;
    private Spinner customDocumentSpinner;
    private Spinner customVariantSpinner;
    private Button startCustomScanButton;
    private Button stopCustomScanButton;
    private Button setCountryAndDocumentTypeButton;
    private Button frontSideButton;
    private Button backSideButton;
    private Button faceButton;
    private Button stopScan;
    private Switch extraction;
    private Button startFallback;
    private Button switchCamera;
    private Button takePicture;
    private Button toggleFlash;
    private Button retryScan;
    private Button confirmScan;
    private Button errorRetryButton;
    private Button partRetryButton;
    private Button finishButton;
    private Button nfcRetryButton;
    private Button nfcCancelButton;
    private Button userConsentedButton;
    private Switch switchVerification;
    private Switch switchIdentityVerification;
    private TextInputEditText idNumberEditText;
    private TextInputEditText dateOfBirthEditText;
    private TextInputEditText dateOfExpiryEditText;

    private NetverifyCustomSDKController customSDKController;
    private NetverifyCustomScanPresenter customScanViewPresenter;
    private NetverifyCustomNfcPresenter customNfcPresenter;
    private CustomCountryAdapter customCountryAdapter;
    private CustomDocumentAdapter customDocumentAdapter;
    private CustomVariantAdapter customVariantAdapter;
    private Drawable successDrawable;
    private Drawable errorDrawable;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_netverify_custom, container, false);

        Bundle args = getArguments();

        if(args != null) {
	        apiToken = args.getString(MainActivity.KEY_API_TOKEN);
	        apiSecret = args.getString(MainActivity.KEY_API_SECRET);
			dataCenter = (JumioDataCenter) args.getSerializable(MainActivity.KEY_DATACENTER);
        }

        scrollView = rootView.findViewById(R.id.scrollView);
        netverifySettingsContainer = rootView.findViewById(R.id.netverifySettingsContainer);
        customScanContainer = rootView.findViewById(R.id.netverifyCustomContainer);
        countryDocumentLayout = rootView.findViewById(R.id.countryDocumentLayout);
        partTypeLayout = rootView.findViewById(R.id.partTypeLayout);
        callbackLog = rootView.findViewById(R.id.callbackLog);
        customNfcAccessLayout = rootView.findViewById(R.id.customNfcAccessLayout);
        customScanLayout = rootView.findViewById(R.id.customScanLayout);
        customConfirmLayout = rootView.findViewById(R.id.customConfirmLayout);
        customNfcLayout = rootView.findViewById(R.id.customNfcLayout);
        userConsentLayout = rootView.findViewById(R.id.userConsentLayout);
        customScanView = rootView.findViewById(R.id.netverifyCustomScanView);
        customConfirmationView = rootView.findViewById(R.id.netverifyCustomConfirmationView);
		customAnimationView = rootView.findViewById(R.id.netverifyCustomAnimationView);
        loadingIndicator = rootView.findViewById(R.id.loadingIndicator);
        customCountrySpinner = rootView.findViewById(R.id.customCountrySpinner);
        customDocumentSpinner = rootView.findViewById(R.id.customDocumentSpinner);
        customVariantSpinner = rootView.findViewById(R.id.customVariantSpinner);
        startCustomScanButton = rootView.findViewById(R.id.startNetverifyCustomButton);
        stopCustomScanButton = rootView.findViewById(R.id.stopNetverifyCustomButton);
        setCountryAndDocumentTypeButton = rootView.findViewById(R.id.setCountryAndDocumentType);
        frontSideButton = rootView.findViewById(R.id.frontSideButton);
        backSideButton = rootView.findViewById(R.id.backSideButton);
        faceButton = rootView.findViewById(R.id.faceButton);
        stopScan = rootView.findViewById(R.id.stopScan);
        extraction = rootView.findViewById(R.id.extraction);
        startFallback = rootView.findViewById(R.id.startFallback);
        switchCamera = rootView.findViewById(R.id.switchCamera);
        takePicture = rootView.findViewById(R.id.takePicture);
        toggleFlash = rootView.findViewById(R.id.toggleFlash);
        retryScan = rootView.findViewById(R.id.retryScan);
        confirmScan = rootView.findViewById(R.id.confirmScan);
        errorRetryButton = rootView.findViewById(R.id.errorRetryButton);
		partRetryButton = rootView.findViewById(R.id.partRetryButton);
        finishButton = rootView.findViewById(R.id.finishButton);
        nfcRetryButton = rootView.findViewById(R.id.nfcRetryButton);
		nfcCancelButton = rootView.findViewById(R.id.nfcCancelButton);
		userConsentedButton = rootView.findViewById(R.id.userConsentedButton);
        switchVerification = rootView.findViewById(R.id.switchVerification);
        switchIdentityVerification = rootView.findViewById(R.id.switchIdentitiyVerification);
        idNumberEditText = rootView.findViewById(R.id.idNumberEditText);
        dateOfBirthEditText = rootView.findViewById(R.id.dateOfBirthEditText);
        dateOfExpiryEditText = rootView.findViewById(R.id.dateOfExpiryEditText);

        startCustomScanButton.setOnClickListener(this);
        stopCustomScanButton.setOnClickListener(this);
        setCountryAndDocumentTypeButton.setOnClickListener(this);
        frontSideButton.setOnClickListener(this);
        backSideButton.setOnClickListener(this);
        faceButton.setOnClickListener(this);
        stopScan.setOnClickListener(this);
        extraction.setOnClickListener(this);
        startFallback.setOnClickListener(this);
        switchCamera.setOnClickListener(this);
        takePicture.setOnClickListener(this);
        toggleFlash.setOnClickListener(this);
        retryScan.setOnClickListener(this);
        confirmScan.setOnClickListener(this);
        errorRetryButton.setOnClickListener(this);
		partRetryButton.setOnClickListener(this);
        finishButton.setOnClickListener(this);
        nfcRetryButton.setOnClickListener(this);
        nfcCancelButton.setOnClickListener(this);
        userConsentedButton.setOnClickListener(this);


        startCustomScanButton.setText(String.format(getResources().getString(R.string.button_start), getResources().getString(R.string.section_netverify_custom)));

        successDrawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.jumio_success));
        errorDrawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.jumio_error));

		hideView(false, countryDocumentLayout, partTypeLayout, finishButton, errorRetryButton, partRetryButton, customAnimationView);

		initScanView();

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
            if (customScanViewPresenter != null)
                customScanViewPresenter.pause();
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
            if (customScanViewPresenter != null)
                customScanViewPresenter.resume();
        } catch (SDKNotConfiguredException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        try {
        	if (stopScan != null) {
				stopScan.performClick();
			}
			if (stopCustomScanButton != null) {
				stopCustomScanButton.performClick();
			}
            if (customScanViewPresenter != null) {
				customScanViewPresenter.destroy();
			}
            if (customSDKController != null) {
				customSDKController.destroy();
			}
        } catch (SDKNotConfiguredException e) {
            e.printStackTrace();
        }
		super.onDestroy();
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(isSDKControllerValid()) {
    		customSDKController.consumeIntent(requestCode, resultCode, data);
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
        customScanView.setRatio(customScanView.getMinRatio());
    }

    @Override
    public void onClick(final View v) {
        v.setEnabled(false);
        boolean keepDisabled = false;
        if (v == startCustomScanButton) {
            if (!MobileSDK.hasAllRequiredPermissions(getActivity())) {
                ActivityCompat.requestPermissions(getActivity(), MobileSDK.getMissingPermissions(getActivity()), PERMISSION_REQUEST_CODE_NETVERIFY_CUSTOM);
            } else {
				netverifySettingsContainer.setVisibility(View.GONE);
				customScanContainer.setVisibility(View.VISIBLE);
				hideView(true, countryDocumentLayout, partTypeLayout, finishButton, loadingIndicator, errorRetryButton, partRetryButton);
				callbackLog.removeAllViews();

				try {
					initializeNetverifySDK();

					if (netverifySDK != null) {
						customSDKController = netverifySDK.start(new NetverifyCustomSDKImpl());
						customSDKController.resume();
					}
					keepDisabled = true;
				} catch (IllegalArgumentException | SDKNotConfiguredException | MissingPermissionException e) {
					Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
				}
			}
        } else if (v == stopCustomScanButton && isSDKControllerValid()) {
            hideView(false, stopCustomScanButton, countryDocumentLayout, partTypeLayout, customScanLayout, customConfirmLayout, finishButton, loadingIndicator);
            callbackLog.removeAllViews();
            try {
                customSDKController.pause();
                customSDKController.destroy();
            } catch (SDKNotConfiguredException e) {
                addToCallbackLog(e.getMessage());
            }
            if (netverifySDK != null) {
                netverifySDK.destroy();
				netverifySDK.checkDeallocation(NetverifyCustomFragment.this);
                netverifySDK = null;
            }
            customSDKController = null;
            customScanContainer.setVisibility(View.GONE);
            netverifySettingsContainer.setVisibility(View.VISIBLE);
        } else if (v == setCountryAndDocumentTypeButton && isSDKControllerValid()) {
            NetverifyCountry country = customCountryAdapter.getCountryObject(customCountrySpinner.getSelectedItemPosition());
            NVDocumentType documentType = customDocumentAdapter.getDocumentType(customDocumentSpinner.getSelectedItemPosition());
            NVDocumentVariant documentVariant = customVariantAdapter.getDocumentVariant(customVariantSpinner.getSelectedItemPosition());
            frontSideButton.setVisibility(View.GONE);
            frontSideButton.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null);
            frontSideButton.setEnabled(false);
            backSideButton.setVisibility(View.GONE);
            backSideButton.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null);
            backSideButton.setEnabled(false);
            faceButton.setVisibility(View.GONE);
            faceButton.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null);
            faceButton.setEnabled(false);

            List<ScanSide> sides = null;
            try {
                sides = customSDKController.setDocumentConfiguration(country, documentType, documentVariant);

                for (ScanSide side : sides) {
                    switch (side) {
                        case FRONT:
                            frontSideButton.setVisibility(View.VISIBLE);
                            break;
                        case BACK:
                            backSideButton.setVisibility(View.VISIBLE);
                            break;
                        case FACE:
                            faceButton.setVisibility(View.VISIBLE);
                            break;
                    }
                }
                showView(true, partTypeLayout);
            } catch (SDKNotConfiguredException e) {
                e.printStackTrace();
            }
		} else if (v == userConsentedButton && isSDKControllerValid()) {
			customSDKController.setUserConsented();
			hideView(false, userConsentLayout);
        } else if ((v == frontSideButton || v == backSideButton || v == faceButton) && isSDKControllerValid()) {
			customScanView.setMode(v == faceButton?NetverifyCustomScanView.MODE_FACE:NetverifyCustomScanView.MODE_ID);
			initScanView();

			showView(true, customScanLayout, customScanView);
			scrollView.post(new Runnable() {
				@Override
				public void run() {
					scrollView.scrollTo(0, customScanLayout.getTop());
					scrollView.postDelayed(new ScanPartRunnable(v), 250);
				}
			});
			keepDisabled = true;
        } else if (v == stopScan && isScanViewControllerValid()) {
            customScanViewPresenter.stopScan();
            hideView(false, customScanLayout);
			frontSideButton.setEnabled(true);
			backSideButton.setEnabled(true);
			faceButton.setEnabled(true);
            customScanViewPresenter.destroy();
            customScanViewPresenter = null;

            customAnimationView.destroy();
			hideView(false, partRetryButton, customAnimationView);
        } else if (v == extraction && isScanViewControllerValid()) {
            if (extraction.isChecked())
                customScanViewPresenter.resumeExtraction();
            else
                customScanViewPresenter.pauseExtraction();
        } else if (v == startFallback && isScanViewControllerValid()) {
            if (customScanViewPresenter.isFallbackAvailable()) {
                customScanViewPresenter.startFallback();
				//startFallback could result in an onNetverifyScanForPartFinished if the part is not mandatory
				//therefore check if the customScanViewPresenter is null!
				if(isScanViewControllerValid())
                	addToCallbackLog("start fallback: " + customScanViewPresenter.getScanMode());
                keepDisabled = true;
            }
        } else if (v == switchCamera && isScanViewControllerValid()) {
            if (customScanViewPresenter.hasMultipleCameras())
                customScanViewPresenter.switchCamera();
        } else if (v == takePicture && isScanViewControllerValid()) {
            if (customScanViewPresenter.showShutterButton())
                customScanViewPresenter.takePicture();
        } else if (v == toggleFlash && isScanViewControllerValid()) {
            if (customScanViewPresenter.hasFlash())
                customScanViewPresenter.toggleFlash();
        } else if (v == retryScan && isScanViewControllerValid()) {
            hideView(false, customConfirmLayout);
            showView(false, customScanLayout);

            customScanViewPresenter.retryScan();
        } else if (v == confirmScan && isScanViewControllerValid()) {
            hideView(true, customConfirmLayout);
            customScanViewPresenter.confirmScan();

            if (!extraction.isChecked())
                return;
		} else if (v == partRetryButton && isScanViewControllerValid()) {
			customAnimationView.destroy();
			hideView(false, partRetryButton, customAnimationView);

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
        } else if (v == finishButton && isSDKControllerValid()) {
            try {
                showView(false, loadingIndicator);
                customSDKController.finish();
                keepDisabled = true;
            } catch (SDKNotConfiguredException e) {
                addToCallbackLog(e.getMessage());
            }
        } else if (v == nfcRetryButton && isNfcPresenterValid()) {
			try {
				if (customNfcAccessLayout.getVisibility() == View.VISIBLE) {
					NetverifyCustomNfcAccess nfcAccessData = new NetverifyCustomNfcAccess();
					nfcAccessData.dateOfBirth = (Date) dateOfBirthEditText.getTag();
					nfcAccessData.dateOfExpiry = (Date) dateOfExpiryEditText.getTag();
					nfcAccessData.idNumber = idNumberEditText.getText().toString();
					customNfcPresenter.updateAccessData(nfcAccessData);
					hideView(true, customNfcAccessLayout);
				}

				customNfcPresenter.retry();
				hideView(true, customNfcLayout);
			} catch(NullPointerException e) {
				addToCallbackLog(e.getMessage());
			}
		} else if (v == nfcCancelButton && isNfcPresenterValid()) {
			customNfcPresenter.cancel();

			hideView(false, customNfcAccessLayout);
			hideView(false, customNfcLayout);

		}

        if (!keepDisabled)
            v.setEnabled(true);
    }



    private void initializeNetverifySDK() {
        try {
            // You can get the current SDK version using the method below.
//			NetverifySDK.getSDKVersion();

            // Call the method isSupportedPlatform to check if the device is supported.
            if (!NetverifySDK.isSupportedPlatform(getActivity()))
                android.util.Log.w(TAG, "Device not supported");

            // Applications implementing the SDK shall not run on rooted devices. Use either the below
            // method or a self-devised check to prevent usage of SDK scanning functionality on rooted
            // devices.
            if (NetverifySDK.isRooted(getActivity()))
                android.util.Log.w(TAG, "Device is rooted");

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
//			}

            // Enable ID verification to receive a verification status and verified data positions (see Callback chapter).
            // Note: Not possible for accounts configured as Fastfill only.
            netverifySDK.setEnableVerification(switchVerification.isChecked());

            // You can specify issuing country (ISO 3166-1 alpha-3 country code) and/or ID types and/or document variant to skip
            // their selection during the scanning process.
            // Use the following method to convert ISO 3166-1 alpha-2 into alpha-3 country code.
//			String alpha3 = IsoCountryConverter.convertToAlpha3("AT");
//			netverifySDK.setPreselectedCountry("AUT");
//			netverifySDK.ArrayList<NVDocumentType> documentTypes = new ArrayList<>();
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

            // Callback URL (max. 255 characters) for the confirmation after the verification is completed. This setting overrides your Jumio merchant settings.
//			netverifySDK.setCallbackUrl("YOURCALLBACKURL");

            // You can disable Identity Verification during the ID verification for a specific transaction.
            netverifySDK.setEnableIdentityVerification(switchIdentityVerification.isChecked());

            // Use the following method to disable eMRTD scanning.
//			netverifySDK.setEnableEMRTD(false);

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
            android.util.Log.e(TAG, "Error in initializeNetverifySDK: ", e);
            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            netverifySDK = null;
        }
    }

    private boolean isSDKControllerValid() {
        return customSDKController != null;
    }

    private boolean isScanViewControllerValid() {
        return customScanViewPresenter != null;
    }

	private boolean isNfcPresenterValid() {
    	return customNfcPresenter != null;
	}

	@Override
	public void onNetverifyDeallocated() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(startCustomScanButton != null) {
					startCustomScanButton.setEnabled(true);
				}
			}
		});
	}

	private class ScanPartRunnable implements Runnable {
		private View view;

		public ScanPartRunnable(View view) {
			this.view = view;
		}

		@Override
		public void run() {
			try {
				ScanSide scanSide = ScanSide.FRONT;
				if (view == backSideButton) {
					scanSide = ScanSide.BACK;
				} else if (view == faceButton) {
					scanSide = ScanSide.FACE;

					int[] location = new int[2];
					stopScan.getLocationOnScreen(location);

					Rect rectangle = new Rect();
					getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);

					customScanView.setCloseButtonWidth(stopScan.getWidth());
					customScanView.setCloseButtonHeight(stopScan.getHeight());
					customScanView.setCloseButtonTop(location[1] - rectangle.top);
					customScanView.setCloseButtonLeft(location[0] - rectangle.left);
					customScanView.setCloseButtonResId(R.drawable.jumio_close_button);
				}

				customScanViewPresenter = customSDKController.startScanForPart(scanSide, customScanView, customConfirmationView, new NetverifyCustomScanImpl());

				frontSideButton.setEnabled(false);
				backSideButton.setEnabled(false);
				faceButton.setEnabled(false);

				switchCamera.setEnabled(false);
				takePicture.setEnabled(false);
				toggleFlash.setEnabled(false);
				startFallback.setEnabled(false);
				extraction.setChecked(true);
				addToCallbackLog("start scanmode: " + customScanViewPresenter.getScanMode());
				addToCallbackLog("help text: " + customScanViewPresenter.getHelpText());
				startFallback.setEnabled(customScanViewPresenter.isFallbackAvailable());

			} catch (SDKNotConfiguredException e) {
				hideView(false, customScanLayout, customScanView);

				addToCallbackLog(e.getMessage());
				frontSideButton.setEnabled(true);
				backSideButton.setEnabled(true);
				faceButton.setEnabled(true);
			}
		}
	}

	private class RetryPartRunnable implements Runnable {

		@Override
		public void run() {
			try {
				if (customScanViewPresenter.getScanMode() == NetverifyScanMode.FACE) {

					int[] location = new int[2];
					stopScan.getLocationOnScreen(location);

					Rect rectangle = new Rect();
					getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);

					customScanView.setCloseButtonWidth(stopScan.getWidth());
					customScanView.setCloseButtonHeight(stopScan.getHeight());
					customScanView.setCloseButtonTop(location[1] - rectangle.top);
					customScanView.setCloseButtonLeft(location[0] - rectangle.left);
					customScanView.setCloseButtonResId(R.drawable.jumio_close_button);
				}

				customScanViewPresenter.retryScan();

			} catch (Exception e) {
				addToCallbackLog(e.getMessage());
			}
		}
	}

    private class NetverifyCustomSDKImpl implements NetverifyCustomSDKInterface {

        //Custom SDK Interface
        @Override
        public void onNetverifyCountriesReceived(HashMap<String, NetverifyCountry> countryList, String userCountryCode) {
            addToCallbackLog("onNetverifyCountriesReceived - user Country is " + userCountryCode);
			if(stopCustomScanButton == null || countryDocumentLayout == null)
				return;
            showView(true, stopCustomScanButton, countryDocumentLayout);
            final Context context = getActivity();
            if(context == null)
                return;
            customCountryAdapter = new CustomCountryAdapter(context, countryList);
            customCountrySpinner.setAdapter(customCountryAdapter);
            customCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    NetverifyCountry country = customCountryAdapter.getCountryObject(position);
                    customDocumentAdapter = new CustomDocumentAdapter(context, country.getDocumentTypes());
                    customDocumentSpinner.setAdapter(customDocumentAdapter);
                    customDocumentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            hideView(false, partTypeLayout, customScanLayout, customConfirmLayout, finishButton, loadingIndicator);
                            NetverifyCountry country = customCountryAdapter.getCountryObject(customCountrySpinner.getSelectedItemPosition());
                            NVDocumentType documentType = customDocumentAdapter.getDocumentType(position);
                            customVariantAdapter = new CustomVariantAdapter(context, country.getDocumentVariants(documentType));
                            customVariantSpinner.setAdapter(customVariantAdapter);
                            customVariantSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    hideView(false, partTypeLayout, customScanLayout, customConfirmLayout, finishButton, loadingIndicator);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

		@Override
		public void onNetverifyUserConsentRequried(String privacyPolicy) {
			showView(true, userConsentLayout);
			TextView userConsentUrl = userConsentLayout.findViewById(R.id.userConsentUrl);
			userConsentUrl.setText(privacyPolicy);
			Linkify.addLinks(userConsentUrl, Linkify.WEB_URLS);
			userConsentUrl.setMovementMethod(LinkMovementMethod.getInstance());
		}

        @Override
        public void onNetverifyResourcesLoaded() {
            addToCallbackLog("onNetverifyResourcesLoaded");
            frontSideButton.setEnabled(true);
            backSideButton.setEnabled(true);
            faceButton.setEnabled(true);
        }

        @Override
        public void onNetverifyFinished(NetverifyDocumentData documentData, String scanReference) {
            addToCallbackLog("onNetverifyFinished");
            hideView(false, countryDocumentLayout, partTypeLayout, finishButton, loadingIndicator, errorRetryButton);

            appendKeyValue("Scan reference", scanReference);

            if (documentData != null) {
                //Dont change the key strings - they are needed for the qa automation
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                appendKeyValue("Selected country", documentData.getSelectedCountry());
                appendKeyValue("Selected document type", documentData.getSelectedDocumentType() == null ? "" : documentData.getSelectedDocumentType().name());
                appendKeyValue("ID number", documentData.getIdNumber());
                appendKeyValue("Personal number", documentData.getPersonalNumber());
                appendKeyValue("OptData1", documentData.getOptionalData1());
                appendKeyValue("OptData2", documentData.getOptionalData2());
                appendKeyValue("Issue date", documentData.getIssuingDate() == null ? null : dateFormat.format(documentData.getIssuingDate()));
                appendKeyValue("Expiry date", documentData.getExpiryDate() == null ? null : dateFormat.format(documentData.getExpiryDate()));
                appendKeyValue("Issuing country", documentData.getIssuingCountry());
                appendKeyValue("Last name", documentData.getLastName());
                appendKeyValue("First name", documentData.getFirstName());
                appendKeyValue("Date of birth", documentData.getDob() == null ? null : dateFormat.format(documentData.getDob()));
                appendKeyValue("Gender", documentData.getGender() == null ? null : documentData.getGender().toString());
                appendKeyValue("Originating country", documentData.getOriginatingCountry());
                appendKeyValue("Address line", documentData.getAddressLine());
                appendKeyValue("City", documentData.getCity());
                appendKeyValue("Subdivision", documentData.getSubdivision());
                appendKeyValue("Post code", documentData.getPostCode());

                NetverifyMrzData mrz = documentData.getMrzData();
                if (mrz != null){
                    appendKeyValue("MRZ Validation: ", "");
                    appendKeyValue("   MRZ Type: ", mrz.getFormat().toString());
                    appendKeyValue("   Line1", mrz.getMrzLine1());
                    appendKeyValue("   Line2", mrz.getMrzLine2());
                    if (mrz.getMrzLine3() != null) {
                        appendKeyValue("   Line3", mrz.getMrzLine3());
                    }
                    appendKeyValue("   idNumberValid()", "" + mrz.idNumberValid());
                    appendKeyValue("   dobValid()", "" + mrz.dobValid());
                    appendKeyValue("   personalNumberValid()", "" + mrz.personalNumberValid());
                    appendKeyValue("   expiryDateValid()", "" + mrz.expiryDateValid());
                    appendKeyValue("   compositeValid()", "" + mrz.compositeValid());

                }
            }
        }

        @Override
        public void onNetverifyError(String errorCode, String errorMessage, boolean retryPossible, String scanReference) {
            hideView(true, finishButton);
            showView(true, errorRetryButton);
            addToCallbackLog(String.format("onNetverifyError: %s, %s, %d, %s", errorCode, errorMessage, retryPossible ? 0 : 1, scanReference != null ? scanReference : "null"));
        }
	}

    private class NetverifyCustomScanImpl implements NetverifyCustomScanInterface {

        @Override
        public void onNetverifyScanForPartFinished(ScanSide scanSide, boolean allPartsScanned) {
            customScanViewPresenter.destroy();
            customScanViewPresenter = null;

			frontSideButton.setEnabled(true);
			backSideButton.setEnabled(true);
			faceButton.setEnabled(true);

            addToCallbackLog("onNetverifyScanForPartFinished");
            if (customScanLayout.getVisibility() == View.VISIBLE)
                hideView(false, customScanLayout);
            if (customConfirmLayout.getVisibility() == View.VISIBLE)
                hideView(false, customConfirmLayout);
            hideView(false, loadingIndicator);
            switch (scanSide) {
                case FRONT:
                    frontSideButton.setCompoundDrawablesWithIntrinsicBounds(successDrawable, null, null, null);
                    break;
                case BACK:
                    backSideButton.setCompoundDrawablesWithIntrinsicBounds(successDrawable, null, null, null);
                    break;
                case FACE:
                    faceButton.setCompoundDrawablesWithIntrinsicBounds(successDrawable, null, null, null);
                    break;
            }
            if (allPartsScanned) {
                finishButton.setEnabled(true);
                showView(true, finishButton);
            }
        }

        //Custom ScanView Interface
        @Override
        public void onNetverifyCameraAvailable() {
            addToCallbackLog("onNetverifyCameraAvailable");
            switchCamera.setEnabled(customScanViewPresenter.hasMultipleCameras());
            takePicture.setEnabled(customScanViewPresenter.showShutterButton());
            toggleFlash.setEnabled(customScanViewPresenter.hasFlash());
            stopScan.setEnabled(true);
            extraction.setEnabled(true);
        }

        @Override
        public void onNetverifyExtractionStarted() {
            addToCallbackLog("onNetverifyExtractionStarted");
        }

        @Override
        public void onNetverifyPresentConfirmationView(NetverifyConfirmationType confirmationType) {
			addToCallbackLog(String.format("onNetverifyPresentConfirmationView %s", confirmationType.toString()));
            hideView(true, customScanLayout);
            showView(true, customConfirmLayout);
        }

        @Override
        public void onNetverifyNoUSAddressFound() {
            addToCallbackLog("onNetverifyNoUsAddressFound");
        }

        @Override
        public void onNetverifyFaceInLandscape() {
            addToCallbackLog("onNetverifyFaceInLandscape");

            customScanViewPresenter.getHelpAnimation(customAnimationView);
			showView(false, partRetryButton, customAnimationView);
        }

        @Override
        public void onNetverifyShowLegalAdvice(String legalAdvice) {
            addToCallbackLog("onNetverifyShowLegalAdvice");
            addToCallbackLog(legalAdvice);
        }

		@Override
		public void onNetverifyDisplayBlurHint() {
			addToCallbackLog("onNetverifyDisplayBlurHint");
		}

		@Override
		public void onNetverifyScanForPartCanceled(ScanSide scanSide, NetverifyCancelReason cancelReason) {
			addToCallbackLog(String.format("onNetverifyScanForPartCanceled scanSide: %s reason: %s helptext: %s", scanSide.toString(), cancelReason.toString(), customScanViewPresenter.getHelpText()));

			if(scanSide == ScanSide.FACE) {
				customScanViewPresenter.getHelpAnimation(customAnimationView);
				showView(false, customAnimationView);
			}

			showView(false, partRetryButton);
		}

		@Override
		public NetverifyCustomNfcInterface getNetverifyCustomNfcInterface() {
			addToCallbackLog("getNetverifyCustomNfcInterface");

			return new NetverifyCustomNfcImpl();
		}

		@Override
		public void onNetverifyStartNfcExtraction(NetverifyCustomNfcPresenter netverifyCustomNfcPresenter) {
			addToCallbackLog("Waiting for eMrtd Document..");

			hideView(false, nfcRetryButton);
			showView(false, customNfcLayout);

			customNfcPresenter = netverifyCustomNfcPresenter;
		}
	}

	private class NetverifyCustomNfcImpl implements NetverifyCustomNfcInterface {

		@Override
		public void onNetverifyNfcStarted() {
			addToCallbackLog("onNetverifyNfcStarted");

			hideView(true, customNfcLayout);
		}

		@Override
		public void onNetverifyNfcUpdate(int progress) {
			addToCallbackLog(String.format("onNetverifyNfcUpdate %d", progress));
		}

		@Override
		public void onNetverifyNfcFinished() {
			addToCallbackLog("onNetverifyNfcFinished");
		}

		@Override
		public void onNetverifyNfcSystemSettings() {
			addToCallbackLog("Please enable NFC in your system settings");

			showView(true, customNfcLayout, nfcRetryButton);
		}

		@Override
		public void onNetverifyNfcError(String errorMessage, boolean retryable, boolean accessUpdate, @Nullable NetverifyCustomNfcAccess nfcAccessData) {
			addToCallbackLog("onNetverifyNfcError "+errorMessage);

			showView(true, customNfcLayout, nfcRetryButton);

			if(accessUpdate) {
				java.text.DateFormat dateFormat = DateFormat.getDateFormat(getActivity());
				dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

				dateOfBirthEditText.setText(dateFormat.format(nfcAccessData.dateOfBirth));
				dateOfBirthEditText.setTag(nfcAccessData.dateOfBirth);
				dateOfBirthEditText.setOnClickListener(new DatePickerListener(new DatePickerDialog.OnDateSetListener(){
					@Override
					public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
						Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
						calendar.set(Calendar.YEAR, year);
						calendar.set(Calendar.MONTH, month);
						calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

						dateOfBirthEditText.setText(dateFormat.format(calendar.getTime()));
						dateOfBirthEditText.setTag(calendar.getTime());
					}
				}, nfcAccessData.dateOfBirth));

				dateOfExpiryEditText.setText(dateFormat.format(nfcAccessData.dateOfExpiry));
				dateOfExpiryEditText.setTag(nfcAccessData.dateOfExpiry);
				dateOfExpiryEditText.setOnClickListener(new DatePickerListener(new DatePickerDialog.OnDateSetListener(){
					@Override
					public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
						Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
						calendar.set(Calendar.YEAR, year);
						calendar.set(Calendar.MONTH, month);
						calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

						dateOfExpiryEditText.setText(dateFormat.format(calendar.getTime()));
						dateOfExpiryEditText.setTag(calendar.getTime());
					}
				}, nfcAccessData.dateOfExpiry));

				List<InputFilter> filters = new ArrayList<>(Arrays.asList(idNumberEditText.getFilters()));
				filters.add(0, new InputFilter.AllCaps());
				filters.add(1, new AlphanumInputfilter());
				idNumberEditText.setFilters(filters.toArray(new InputFilter[filters.size()]));
				idNumberEditText.setText(nfcAccessData.idNumber);
				showView(true, customNfcAccessLayout);
			}
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

    private class CustomCountryAdapter extends ArrayAdapter<String> {

        private HashMap<String, NetverifyCountry> countryList;

        public CustomCountryAdapter(Context context, HashMap<String, NetverifyCountry> countryList) {
            super(context, android.R.layout.simple_spinner_item);

            this.countryList = countryList;
            ArrayList<String> sortedCountryList = new ArrayList<String>(countryList.keySet());
            Collections.sort(sortedCountryList);
            addAll(sortedCountryList);
        }

        public NetverifyCountry getCountryObject(int position) {
            return countryList.get(getItem(position));
        }
    }

    private class CustomDocumentAdapter extends ArrayAdapter<String> {

        private NVDocumentType[] documentTypes;

        public CustomDocumentAdapter(Context context, Set<NVDocumentType> documentTypeSet) {
            super(context, android.R.layout.simple_spinner_item);

            documentTypes = documentTypeSet.toArray(new NVDocumentType[documentTypeSet.size()]);
            for (NVDocumentType documentType : documentTypes) {
                add(documentType.name());
            }
        }

        public NVDocumentType getDocumentType(int position) {
            return documentTypes[position];
        }
    }

    private class CustomVariantAdapter extends ArrayAdapter<String> {

        private NVDocumentVariant[] documentVariants;

        public CustomVariantAdapter(Context context, Set<NVDocumentVariant> documentVariantSet) {
            super(context, android.R.layout.simple_spinner_item);

            this.documentVariants = documentVariantSet.toArray(new NVDocumentVariant[documentVariantSet.size()]);
            for (NVDocumentVariant documentVariant : documentVariants) {
                add(documentVariant.name());
            }
        }

        public NVDocumentVariant getDocumentVariant(int position) {
            return documentVariants[position];
        }
    }

	private class AlphanumInputfilter implements InputFilter {
		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			// Only keep characters that are alphanumeric
			StringBuilder builder = new StringBuilder();
			for (int i = start; i < end; i++) {
				char c = source.charAt(i);
				if (Character.isLetterOrDigit(c)) {
					builder.append(c);
				}
			}

			// If all characters are valid, return null, otherwise only return the filtered characters
			boolean allCharactersValid = (builder.length() == end - start);
			return allCharactersValid ? null : builder.toString();
		}
	}

	private class DatePickerListener implements View.OnClickListener {
		private final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
		private DatePickerDialog.OnDateSetListener mListener;


		public DatePickerListener(DatePickerDialog.OnDateSetListener listener, @Nullable Date startDate) {

			mListener = listener;
			if (startDate != null)
				cal.setTime(startDate);
		}

		@Override
		public void onClick(View v) {
			if(getActivity() != null) {
				new DatePickerDialog(getActivity(), R.style.Theme_AppCompat_Light_Dialog, mListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
						.show();
			}
		}
	}
}
