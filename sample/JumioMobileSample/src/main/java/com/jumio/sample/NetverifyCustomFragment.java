package com.jumio.sample;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jumio.MobileSDK;
import com.jumio.commons.log.Log;
import com.jumio.commons.utils.ScreenUtil;
import com.jumio.core.data.document.ScanSide;
import com.jumio.core.enums.JumioDataCenter;
import com.jumio.core.exceptions.MissingPermissionException;
import com.jumio.core.exceptions.PlatformNotSupportedException;
import com.jumio.nv.NetverifyDocumentData;
import com.jumio.nv.NetverifyMrzData;
import com.jumio.nv.NetverifySDK;
import com.jumio.nv.custom.NetverifyCountry;
import com.jumio.nv.custom.NetverifyCustomConfirmationView;
import com.jumio.nv.custom.NetverifyCustomSDKController;
import com.jumio.nv.custom.NetverifyCustomSDKInterface;
import com.jumio.nv.custom.NetverifyCustomScanInterface;
import com.jumio.nv.custom.NetverifyCustomScanPresenter;
import com.jumio.nv.custom.NetverifyCustomScanView;
import com.jumio.nv.custom.NetverifyScanMode;
import com.jumio.nv.custom.SDKNotConfiguredException;
import com.jumio.nv.data.document.NVDocumentType;
import com.jumio.nv.data.document.NVDocumentVariant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Copyright 2018 Jumio Corporation All rights reserved.
 */
public class NetverifyCustomFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = "NetverifyCustom";
    private static final int PERMISSION_REQUEST_CODE_NETVERIFY_CUSTOM = 303;
    public static final int GOOGLE_VISION_REQUEST_CODE = 1000;

    private String apiToken = null;
    private String apiSecret = null;

    private NetverifySDK netverifySDK = null;

    private LinearLayout customScanContainer;
    private LinearLayout countryDocumentLayout;
    private LinearLayout partTypeLayout;
    private LinearLayout callbackLog;
    private LinearLayout netverifySettingsContainer;
    private RelativeLayout customScanLayout;
    private RelativeLayout customConfirmLayout;
    private NetverifyCustomScanView customScanView;
    private NetverifyCustomConfirmationView customConfirmationView;
    private ProgressBar loadingIndicator;
    private Spinner customCountrySpinner;
    private Spinner customDocumentSpinner;
    private Spinner customVariantSpinner;
    private Button startCustomScanButton;
    private Button cancelCustomScanButton;
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
    private Button finishButton;
    private Switch switchVerification;
    private Switch switchFaceMatch;

    private NetverifyCustomSDKController customSDKController;
    private NetverifyCustomScanPresenter customScanViewPresenter;
    private CustomCountryAdapter customCountryAdapter;
    private CustomDocumentAdapter customDocumentAdapter;
    private CustomVariantAdapter customVariantAdapter;
    private Drawable successDrawable;
    private Drawable errorDrawable;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_netverify_custom, container, false);

        Bundle args = getArguments();

        apiToken = args.getString(MainActivity.KEY_API_TOKEN);
        apiSecret = args.getString(MainActivity.KEY_API_SECRET);

        netverifySettingsContainer = (LinearLayout)rootView.findViewById(R.id.netverifySettingsContainer);
        customScanContainer = (LinearLayout)rootView.findViewById(R.id.netverifyCustomContainer);
        countryDocumentLayout = (LinearLayout)rootView.findViewById(R.id.countryDocumentLayout);
        partTypeLayout = (LinearLayout)rootView.findViewById(R.id.partTypeLayout);
        callbackLog = (LinearLayout)rootView.findViewById(R.id.callbackLog);
        customScanLayout = (RelativeLayout)rootView.findViewById(R.id.customScanLayout);
        customConfirmLayout = (RelativeLayout)rootView.findViewById(R.id.customConfirmLayout);
        customScanView = (NetverifyCustomScanView)rootView.findViewById(R.id.netverifyCustomScanView);
        customConfirmationView = (NetverifyCustomConfirmationView)rootView.findViewById(R.id.netverifyCustomConfirmationView);
        loadingIndicator = (ProgressBar)rootView.findViewById(R.id.loadingIndicator);
        customCountrySpinner = (Spinner)rootView.findViewById(R.id.customCountrySpinner);
        customDocumentSpinner = (Spinner)rootView.findViewById(R.id.customDocumentSpinner);
        customVariantSpinner = (Spinner)rootView.findViewById(R.id.customVariantSpinner);
        startCustomScanButton = (Button)rootView.findViewById(R.id.startNetverifyCustomButton);
        cancelCustomScanButton = (Button)rootView.findViewById(R.id.stopNetverifyCustomButton);
        setCountryAndDocumentTypeButton = (Button)rootView.findViewById(R.id.setCountryAndDocumentType);
        frontSideButton = (Button)rootView.findViewById(R.id.frontSideButton);
        backSideButton = (Button)rootView.findViewById(R.id.backSideButton);
        faceButton = (Button)rootView.findViewById(R.id.faceButton);
        stopScan = (Button)rootView.findViewById(R.id.stopScan);
        extraction = (Switch)rootView.findViewById(R.id.extraction);
        startFallback = (Button)rootView.findViewById(R.id.startFallback);
        switchCamera = (Button)rootView.findViewById(R.id.switchCamera);
        takePicture = (Button)rootView.findViewById(R.id.takePicture);
        toggleFlash = (Button)rootView.findViewById(R.id.toggleFlash);
        retryScan = (Button)rootView.findViewById(R.id.retryScan);
        confirmScan = (Button)rootView.findViewById(R.id.confirmScan);
        errorRetryButton = (Button)rootView.findViewById(R.id.errorRetryButton);
        finishButton = (Button)rootView.findViewById(R.id.finishButton);
        switchVerification = (Switch)rootView.findViewById(R.id.switchVerification);
        switchFaceMatch = (Switch)rootView.findViewById(R.id.switchFaceMatch);

        startCustomScanButton.setOnClickListener(this);
        cancelCustomScanButton.setOnClickListener(this);
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
        finishButton.setOnClickListener(this);


        startCustomScanButton.setText(args.getString(MainActivity.KEY_BUTTON_TEXT));

        successDrawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.success));
        errorDrawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.error));

        initScanView();

        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        initScanView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            stopCustomScanIfActiv();
        }
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
        super.onDestroy();
        try {
            if (customScanViewPresenter != null)
                customScanViewPresenter.destroy();
            if (customSDKController != null)
                customSDKController.destroy();
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
        if(customScanViewPresenter != null) {
			if (isPortrait)
				customScanView.setRatio(customScanViewPresenter.getScanMode() == NetverifyScanMode.FACE ? 0.8f : 1.33f);
			else
				customScanView.setRatio(customScanViewPresenter.getScanMode() == NetverifyScanMode.FACE ? 1.66f : 1.33f);
		}
    }

    @Override
    public void onClick(View v) {
        v.setEnabled(false);
        boolean keepDisabled = false;
        if (v == startCustomScanButton) {
            if (!MobileSDK.hasAllRequiredPermissions(getActivity())) {
                ActivityCompat.requestPermissions(getActivity(), MobileSDK.getMissingPermissions(getActivity()), PERMISSION_REQUEST_CODE_NETVERIFY_CUSTOM);
            } else {
				netverifySettingsContainer.setVisibility(View.GONE);
				customScanContainer.setVisibility(View.VISIBLE);
				showView(false, loadingIndicator);
				callbackLog.removeAllViews();

				try {
					initializeNetverifySDK();
					
					if (netverifySDK != null) {
						customSDKController = netverifySDK.start(new NetverifyCustomSDKImpl());
						customSDKController.resume();
					}

				} catch (IllegalArgumentException | SDKNotConfiguredException | MissingPermissionException e) {
					Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
				}
			}
        } else if (v == cancelCustomScanButton && isSDKControllerValid()) {
            hideView(false, cancelCustomScanButton, countryDocumentLayout, partTypeLayout, customScanLayout, customConfirmLayout, finishButton, loadingIndicator);
            callbackLog.removeAllViews();
            try {
                customSDKController.pause();
                customSDKController.destroy();
            } catch (SDKNotConfiguredException e) {
                addToCallbackLog(e.getMessage());
            }
            if (netverifySDK != null) {
                netverifySDK.destroy();
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
        } else if ((v == frontSideButton || v == backSideButton || v == faceButton) && isSDKControllerValid()) {
            try {
                ScanSide scanSide = ScanSide.FRONT;
                if (v == backSideButton)
                    scanSide = ScanSide.BACK;
                else if (v == faceButton)
                    scanSide = ScanSide.FACE;

                if(isPortrait())
                	customScanView.setRatio(scanSide == ScanSide.FACE?0.8f:1.33f);
                else
                	customScanView.setRatio(scanSide == ScanSide.FACE?1.66f:1.33f);

                customScanViewPresenter = customSDKController.startScanForPart(scanSide, customScanView, customConfirmationView, new NetverifyCustomScanImpl());

                switchCamera.setEnabled(false);
                takePicture.setEnabled(false);
                toggleFlash.setEnabled(false);
                startFallback.setEnabled(false);
                extraction.setChecked(true);
                showView(true, customScanLayout);
                addToCallbackLog("start scanmode: " + customScanViewPresenter.getScanMode());
                addToCallbackLog("help text: " + customScanViewPresenter.getHelpText());
                startFallback.setEnabled(customScanViewPresenter.isFallbackAvailable());

            } catch (SDKNotConfiguredException e) {
                addToCallbackLog(e.getMessage());
            }
        } else if (v == stopScan && isScanViewControllerValid()) {
            customScanViewPresenter.stopScan();
            hideView(false, customScanLayout);
            customScanViewPresenter.destroy();
            customScanViewPresenter = null;
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
        }

        if (!keepDisabled)
            v.setEnabled(true);
    }



    private void initializeNetverifySDK() {
        try {
            // You can get the current SDK version using the method below.
            // NetverifySDK.getSDKVersion();

            // Call the method isSupportedPlatform to check if the device is supported.
            if (!NetverifySDK.isSupportedPlatform(getActivity()))
                android.util.Log.w(TAG, "Device not supported");

            // Check if the Google Vision API is available and operational. This is required by the face match step.
            // If the Google Vision API is not available or operational, a fallback image picker will be used for face capturing.
            //
            // OPERATIONAL API is uptodate and can be used
            // NOT_OPERATIONAL API is not available
            // DIALOG_PENDING API is available but an user resolvable error occured. The errordialog is displayed
            NetverifySDK.GoogleVisionStatus googleVisionStatus = NetverifySDK.isMobileVisionOperational(getActivity(), GOOGLE_VISION_REQUEST_CODE);
            if(googleVisionStatus != NetverifySDK.GoogleVisionStatus.OPERATIONAL)
                throw new PlatformNotSupportedException("Google Vision not operational at the moment!");

            // Applications implementing the SDK shall not run on rooted devices. Use either the below
            // method or a self-devised check to prevent usage of SDK scanning functionality on rooted
            // devices.
            if (NetverifySDK.isRooted(getActivity()))
                android.util.Log.w(TAG, "Device is rooted");

            // To create an instance of the SDK, perform the following call as soon as your activity is initialized.
            // Make sure that your merchant API token and API secret are correct and specify an instance
            // of your activity. If your merchant account is created in the EU data center, use
            // JumioDataCenter.EU instead.
            netverifySDK = NetverifySDK.create(getActivity(), apiToken, apiSecret, JumioDataCenter.US);

            // Use the following method to create an instance of the SDK, using offline fastfill scanning.
            // try {
            //     netverifySDK = NetverifySDK.create(getActivity(), "YOUROFFLINETOKEN", "YOURPREFERREDCOUNTRY");
            // } catch (SDKExpiredException e) {
            //    e.printStackTrace();
            //    Toast.makeText(getActivity().getApplicationContext(), "The offline SDK is expired", Toast.LENGTH_LONG).show();
            // }

            // Enable ID verification to receive a verification status and verified data positions (see Callback chapter).
            // Note: Not possible for accounts configured as Fastfill only.
            netverifySDK.setRequireVerification(switchVerification.isChecked());

            // You can specify issuing country (ISO 3166-1 alpha-3 country code) and/or ID types and/or document variant to skip
            // their selection during the scanning process.
            // Use the following method to convert ISO 3166-1 alpha-2 into alpha-3 country code.
            // String alpha3 = IsoCountryConverter.convertToAlpha3("AT");
            // netverifySDK.setPreselectedCountry("AUT");
            // ArrayList<NVDocumentType> documentTypes = new ArrayList<>();
            // documentTypes.add(NVDocumentType.PASSPORT);
            // netverifySDK.setPreselectedDocumentTypes(documentTypes);
            // netverifySDK.setPreselectedDocumentVariant(NVDocumentVariant.PLASTIC);

            // The merchant scan reference allows you to identify the scan (max. 100 characters).
            // Note: Must not contain sensitive data like PII (Personally Identifiable Information) or account login.
            // netverifySDK.setMerchantScanReference("YOURSCANREFERENCE");

            // Use the following property to identify the scan in your reports (max. 100 characters).
            // netverifySDK.setMerchantReportingCriteria("YOURREPORTINGCRITERIA");

            // You can also set a customer identifier (max. 100 characters).
            // Note: The customer ID should not contain sensitive data like PII (Personally Identifiable Information) or account login.
            // netverifySDK.setCustomerId("CUSTOMERID");

            // Callback URL for the confirmation after the verification is completed. This setting overrides your Jumio merchant settings.
            // netverifySDK.setCallbackUrl("YOURCALLBACKURL");

            // You can enable face match during the ID verification for a specific transaction.
            netverifySDK.setRequireFaceMatch(switchFaceMatch.isChecked());

            // Use the following method to disable eMRTD scanning.
            // netverifySDK.setEnableEMRTD(false);

            // Use the following method to set the default camera position.
            // netverifySDK.setCameraPosition(JumioCameraPosition.FRONT);

            // Use the following method to only support IDs where data can be extracted on mobile only.
            // netverifySDK.setDataExtractionOnMobileOnly(true);

            // Use the following method to explicitly send debug-info to Jumio. (default: false)
            // Only set this property to true if you are asked by our Jumio support personnel.
            // netverifySDK.sendDebugInfoToJumio(true);

            // Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
            // netverifySDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID);

            // Use the following method to initialize the SDK before displaying it
//			   netverifySDK.initiate(new NetverifyInitiateCallback() {
//			     @Override
//			     public void onNetverifyInitiateSuccess() {
//			     }
//			     @Override
//			     public void onNetverifyInitiateError(String errorCode, String errorMessage, boolean retryPossible) {
//			     }
//			 });

        } catch (PlatformNotSupportedException e) {
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

    private void stopCustomScanIfActiv() {
        if (customSDKController != null) {
            stopScan.performClick();
            cancelCustomScanButton.performClick();
        }
    }

    private class NetverifyCustomSDKImpl implements NetverifyCustomSDKInterface {

        //Custom SDK Interface
        @Override
        public void onNetverifyCountriesReceived(HashMap<String, NetverifyCountry> countryList, String userCountryCode) {
            addToCallbackLog("onNetverifyCountriesReceived - user Country is " + userCountryCode);
            showView(true, cancelCustomScanButton, countryDocumentLayout);
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
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
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
                appendKeyValue("Middle name", documentData.getMiddleName());
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
        public void onNetverifyPresentConfirmationView() {
            addToCallbackLog("onNetverifyPresentConfirmationView");
            hideView(true, customScanLayout);
            showView(true, customConfirmLayout);
        }

        @Override
        public void onNetverifyNoUSAddressFound() {
            addToCallbackLog("onNetverifyNoUsAddressFound");
        }

        @Override
        public void onNetverifyFaceOnBackside() {
            addToCallbackLog("onNetverifyFaceOnBackside");
        }

        @Override
        public void onNetverifyFaceInLandscape() {
            addToCallbackLog("onNetverifyFaceInLandscape");
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
}
