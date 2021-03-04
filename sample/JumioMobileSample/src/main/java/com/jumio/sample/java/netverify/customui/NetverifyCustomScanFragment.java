package com.jumio.sample.java.netverify.customui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jumio.commons.utils.ScreenUtil;
import com.jumio.core.data.document.ScanSide;
import com.jumio.nv.custom.NetverifyCancelReason;
import com.jumio.nv.custom.NetverifyConfirmationType;
import com.jumio.nv.custom.NetverifyCustomAnimationView;
import com.jumio.nv.custom.NetverifyCustomConfirmationView;
import com.jumio.nv.custom.NetverifyCustomScanInterface;
import com.jumio.nv.custom.NetverifyCustomScanPresenter;
import com.jumio.nv.custom.NetverifyCustomScanView;
import com.jumio.nv.custom.NetverifyScanMode;
import com.jumio.nv.nfc.custom.NetverifyCustomNfcInterface;
import com.jumio.nv.nfc.custom.NetverifyCustomNfcPresenter;
import com.jumio.sample.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import static com.jumio.core.data.document.ScanSide.FACE;

public class NetverifyCustomScanFragment extends Fragment implements View.OnClickListener {

	private final static String TAG = "NvCustomScanFragment";
	private static final String ARG_SCAN_SIDE = "ARG_SCAN_SIDE";
	private static final String ARG_SCAN_DOCUMENT = "ARG_SCAN_DOCUMENT";
	private static final String ARG_SCAN_PROGRESS = "ARG_SCAN_PROGRESS";

	private String scanSide, documentType, progressText;
	private ProgressBar loadingIndicator;
	private NetverifyCustomScanView customScanView;
	private NetverifyCustomScanPresenter customScanViewPresenter;
	private NetverifyCustomNfcPresenter customNfcViewPresenter;
	private NetverifyCustomConfirmationView customConfirmationView;
	private NetverifyCustomAnimationView customAnimationView;
	private TextView tvHelp, tvDocumentType, tvSteps;
	private Button btnConfirm, btnRetake, btnFallback, btnRetryFace, btnCapture, btnDismissHelp, btnSkipNfc;
	private boolean isOnConfirmation;
	private int modeType;

	private OnScanFragmentInteractionListener callback;
	private NetverifyCustomScanImpl customScanImpl;

	private MenuItem flash, switchCamera;
	private Toast blurToast;

	/**
	 * Constructor with parameters
	 *
	 * @param scanSide     specifies which side of document is scanned (front, back or face in case of face scan)
	 * @param document     specifies what kind of document is scanned (passport, DL, etc.)
	 * @param progressText text tracking progress
	 * @return fragment
	 */
	static NetverifyCustomScanFragment newInstance(String scanSide, String document, String progressText) {
		NetverifyCustomScanFragment fragment = new NetverifyCustomScanFragment();
		Bundle args = new Bundle();
		args.putString(ARG_SCAN_SIDE, scanSide);
		args.putString(ARG_SCAN_DOCUMENT, document);
		args.putString(ARG_SCAN_PROGRESS, progressText);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			scanSide = getArguments().getString(ARG_SCAN_SIDE);
			documentType = getArguments().getString(ARG_SCAN_DOCUMENT);
			progressText = getArguments().getString(ARG_SCAN_PROGRESS);
		}
	}

	/**
	 * Creates view and initializes elements depending on what kind of scan is happening, document or face
	 *
	 * @param inflater layoutInflater
	 * @param container ViewGroup
	 * @param savedInstanceState Bundle
	 * @return root view
	 */
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View root;
		// Inflate layout for this fragment in case of FACE scan
		// Needs animation view to display help animations
		if (ScanSide.valueOf(scanSide) == FACE) {
			root = inflater.inflate(R.layout.fragment_netverify_custom_scan_face, container, false);
			btnRetryFace = root.findViewById(R.id.fragment_custom_scan_face_btn_retry);
			btnRetryFace.setOnClickListener(this);
			btnRetryFace.setVisibility(View.INVISIBLE);

			modeType = NetverifyCustomScanView.MODE_FACE;
			customScanView = root.findViewById(R.id.fragment_nv_custom_scan_view);

			Rect rectangle = new Rect();
			if (getActivity() != null) {
				getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
			}
			customScanView.setMode(modeType);

			tvHelp = root.findViewById(R.id.fragment_nv_custom_scan_face_helptext);
		} else {
			// Inflate layout for this fragment in case of DOCUMENT scan
			// Needs retake, confirm and fallback buttons
			root = inflater.inflate(R.layout.fragment_netverify_custom_scan, container, false);

			btnRetake = root.findViewById(R.id.fragment_custom_scan_btn_retake);
			btnConfirm = root.findViewById(R.id.fragment_custom_scan_btn_confirm);
			btnFallback = root.findViewById(R.id.fragment_custom_scan_btn_fallback);
			btnFallback.setOnClickListener(this);
			btnRetake.setOnClickListener(this);
			btnConfirm.setOnClickListener(this);
			tvHelp = root.findViewById(R.id.fragment_custom_scan_tv_help);
			tvDocumentType = root.findViewById(R.id.fragment_custom_scan_tv_document_type);
			tvDocumentType.setText(HtmlCompat.fromHtml(getContext().getString(R.string.netverify_helpview_small_title_capture, documentType, ""), HtmlCompat.FROM_HTML_MODE_LEGACY));

			tvSteps = root.findViewById(R.id.fragment_custom_scan_tv_steps);
			tvSteps.setText(progressText);
			modeType = NetverifyCustomScanView.MODE_ID;
			customScanView = root.findViewById(R.id.fragment_nv_custom_scan_view);
			customScanView.setMode(modeType);
			btnSkipNfc = root.findViewById(R.id.fragment_custom_scan_btn_skip_nfc);
			btnSkipNfc.setOnClickListener(this);
		}

		btnCapture = root.findViewById(R.id.fragment_custom_scan_btn_capture);
		btnCapture.setOnClickListener(this);

		btnDismissHelp = root.findViewById(R.id.fragment_custom_scan_btn_dismiss_help);
		btnDismissHelp.setOnClickListener(this);

		loadingIndicator = root.findViewById(R.id.fragment_nv_custom_loading_indicator);
		customConfirmationView = root.findViewById(R.id.fragment_nv_custom_confirmation_view);
		customAnimationView = root.findViewById(R.id.fragment_nv_custom_animation_view);

		customScanImpl = new NetverifyCustomScanImpl();
		customScanViewPresenter = callback.onInitScanningWithSide(ScanSide.valueOf(scanSide), customScanView, customConfirmationView, customScanImpl);
		setHasOptionsMenu(true);
		return root;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (customScanViewPresenter != null && savedInstanceState == null) {
			setHelpText(customScanViewPresenter.getHelpText());

			// show upfront help animation if available, otherwise skip and start scanning directly
			if (customScanViewPresenter.hasHelpAnimation()) {
				showDocumentHelpAnimation();
			} else {
				//Check for displaying shutter button for face manual capturing here as no upfront help is displayed
				customScanViewPresenter.startScan();
			}

			//Check for displaying shutter button for face manual capturing here as no upfront help is displayed
			showShutterButton(customScanViewPresenter.showShutterButton());
		}
	}

	/**
	 * Set up and show document help animation at the beginning of document scanning process
	 * Continues to actual customScanView on button click
	 */
	private void showDocumentHelpAnimation() {
		hideView(false, tvDocumentType, tvSteps, customScanView);
		showView(btnDismissHelp, customAnimationView);
		if (customScanViewPresenter.hasHelpAnimation()) {
			customScanViewPresenter.getHelpAnimation(customAnimationView);
		}
	}

	/**
	 * Starts face scanning over again, if face scan went wrong for whatever reason
	 * (e.g. it was manually cancelled by user, the lighting was too dark, etc.)
	 */
	private void retryFaceScanning() {
		customAnimationView.destroy();
		hideView(false, customAnimationView, btnRetryFace);
		if(btnCapture != null && customScanViewPresenter.showShutterButton()) {
			showView(btnCapture);
		}
		onRetryScan();
	}

	/**
	 * Shows loading screen with spinner as loading indicator
	 */
	private void showSubmissionLoading() {
		hideView(true, customConfirmationView, customScanView, btnCapture);
	}

	/**
	 * Shows confirmation if document scan was successful, lets user either accept scanned image
	 * or retake it in case the result is not satisfactory
	 */
	private void showConfirmation(boolean faceOnBack) {
		hideView(false, customScanView, btnFallback);
		if(btnCapture != null) {
			hideView(false, btnCapture);
		}
		showView(customConfirmationView, btnConfirm, btnRetake);
		if (tvHelp != null) {
			if(faceOnBack) {
				tvHelp.setText(getString(R.string.custom_ui_scan_face_on_back));
			} else {
				tvHelp.setText(getString(R.string.custom_ui_scan_confirmation));
			}
		}
		if (tvDocumentType != null) {
			tvDocumentType.setText(R.string.netverify_scanview_title_check);
		}
	}

	/**
	 * Shows loading screen with spinner as loading indicator, informs user that document is processing
	 */
	private void showLoading() {
		hideView(true, customScanView, btnFallback, btnConfirm, btnRetake, btnCapture);
		if (tvHelp != null) {
			if(modeType == NetverifyCustomScanView.MODE_ID) {
				tvHelp.setText(getString(R.string.netverify_scanview_snackbar_progress));
			}
			else if(modeType == NetverifyCustomScanView.MODE_FACE) {
				tvHelp.setText(Html.fromHtml(getString(R.string.netverify_scanview_analyzing_biometrics)));
			}
			if(tvHelp.getVisibility() == View.GONE) {
				tvHelp.setVisibility(View.VISIBLE);
			}
		}
		if (tvDocumentType != null) {
			tvDocumentType.setText(R.string.netverify_scanview_title_check);
		}
	}



	/**
	 * Handles cancelled scan
	 * If document scan was cancelled, user is asked to try again, if face scan was cancelled
	 * user is shown appropriate help animation
	 */
	private void displayRetryHelp(ScanSide scanSide) {
		if (customScanViewPresenter != null) {
			customScanViewPresenter.stopScan();
			if (customScanViewPresenter.hasHelpAnimation()) {
				customScanViewPresenter.getHelpAnimation(customAnimationView);
				showView(customAnimationView);
			}
			setHelpText(customScanViewPresenter.getHelpText());
		}
		if ((getActivity()) != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
			Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		}
		if (scanSide == ScanSide.FACE) {
			showView(btnRetryFace, tvHelp);
		} else {
			showView(btnRetake, tvHelp);
		}
	}

	/**
	 * Make one or more views visible
	 *
	 * @param views specifies which view(s)
	 */
	private void showView(View... views) {
		for (View view : views) {
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * Hide one or more views
	 *
	 * @param showLoading specifies if loading screen should be displayed while views are hidden
	 * @param views       specifies which view(s)
	 */
	private void hideView(boolean showLoading, View... views) {
		for (View view : views) {
			if (view != null) {
				view.setVisibility(View.GONE);
			}
		}
		loadingIndicator.setVisibility(showLoading ? View.VISIBLE : View.GONE);
	}


	/**
	 * Attaches fragment
	 */
	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		if (context instanceof OnScanFragmentInteractionListener) {
			callback = (OnScanFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
				+ " must implement OnSuccessFragmentInteractionListener");
		}
	}

	/**
	 * Detaches fragment
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		callback = null;
	}

	/**
	 * Creates action bar at the top of the screen, menu contains items
	 * like the back button, camera flash and camera switch button
	 *
	 * @param menu refers to action bar at the top
	 * @param inflater inflates existing menu
	 */
	@Override
	public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
		if(getActivity() != null) {
			inflater.inflate(R.menu.menu_netverify_custom, menu);
		}
		if (customScanViewPresenter != null) {
			customScanImpl.onNetverifyCameraAvailable();
		}

		flash = menu.findItem(R.id.action_toggle_flash);
		switchCamera = menu.findItem(R.id.action_switch_camera);

		if (flash != null) {
			flash.setVisible(isFlashAvailable());
		}
		if (switchCamera != null) {
			switchCamera.setVisible(isSwitchCameraAvailable());
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	/**
	 * Handles action bar items (back, flash and camera switch button)
	 *
	 * @param item refers to item in menu that was clicked
	 * @return boolean
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			if(getActivity() != null) {
				getActivity().finish();
			}
			return true;
		}

		if (customScanViewPresenter != null) {
			if (item.getItemId() == R.id.action_toggle_flash) {
				customScanViewPresenter.toggleFlash();
			} else if (item.getItemId() == R.id.action_switch_camera) {
				customScanViewPresenter.switchCamera();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Handles all possible button clicks
	 */
	@Override
	public void onClick(View v) {
		if (v != null) {
			switch (v.getId()) {
				case R.id.fragment_custom_scan_btn_confirm:
					showSubmissionLoading();
					onConfirmScan();
					Log.i(TAG, "Scan btn confirm clicked");
					break;
				case R.id.fragment_custom_scan_btn_retake:
					onRetryScan();
					Log.i(TAG, "Scan btn retake clicked");
					break;
				case R.id.fragment_custom_scan_btn_fallback:
					onStartFallback();
					Log.i(TAG, "Scan btn fallback clicked");
					break;
				case R.id.fragment_custom_scan_btn_capture:
					customScanViewPresenter.takePicture();
					Log.i(TAG, "Capture btn clicked");
					break;
				case R.id.fragment_custom_scan_face_btn_retry:
					retryFaceScanning();
					Log.i(TAG, "Scan btn face retry clicked");
					break;
				case R.id.fragment_custom_scan_btn_dismiss_help:
					hideView(false, customAnimationView, btnDismissHelp);
					initScanView(customScanView);
					customScanViewPresenter.startScan();
					showView(tvDocumentType, tvSteps, customScanView);
					if(customScanViewPresenter.isFallbackAvailable()) {
						setFallbackVisibility(true);
					}
					showShutterButton(customScanViewPresenter.showShutterButton());
					Log.i(TAG, "Dismiss help btn clicked");
					break;
				case R.id.fragment_custom_scan_btn_skip_nfc:
					hideView(false, customAnimationView, btnSkipNfc);
					customNfcViewPresenter.cancel();
					customNfcViewPresenter = null;
					customAnimationView.destroy();
					Log.i(TAG, "Skip NFC btn clicked");
					break;
				default:
					break;
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (customScanViewPresenter != null) {
			customScanViewPresenter.resume();
		}
	}

	@Override
	public void onPause() {
		if (customScanViewPresenter != null) {
			customScanViewPresenter.pause();
		}
		super.onPause();
	}

	/**
	 * Sets visibility of capture button
	 * (only in case of manual capture, e.g. paper documents, or if face scan isn't done automatically,
	 * but in capture mode)
	 *
	 * @param visible boolean
	 */
	private void showShutterButton(boolean visible) {
		if (btnCapture != null) {
			btnCapture.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
		if(tvHelp != null) {
			tvHelp.setVisibility(visible ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Callback when scan is confirmed as finished
	 */
	private void onConfirmScan() {
		isOnConfirmation = false;
		if(getActivity() != null) {
			getActivity().invalidateOptionsMenu();
		}
		customScanViewPresenter.confirmScan();
	}

	/**
	 * Called if scan is repeated
	 */
	private void onRetryScan() {
		setFallbackVisibility(customScanViewPresenter.isFallbackAvailable());
		isOnConfirmation = false;
		if (flash != null) {
			flash.setVisible(isFlashAvailable());
		}
		if (switchCamera != null) {
			switchCamera.setVisible(isSwitchCameraAvailable());
		}
		if(getActivity() != null) {
			getActivity().invalidateOptionsMenu();
		}
		if (btnRetryFace != null) {
			hideView(false, customAnimationView, btnRetryFace, tvHelp);
		}
		hideView(false, customConfirmationView, btnRetake, btnConfirm, tvHelp);

		if(customScanViewPresenter.showShutterButton()) {
			showView(btnCapture);
			hideView(false, btnFallback, tvHelp);
			setHelpText("   ");
		} else {
			hideView(false, btnCapture);
		}
		showView(customScanView);
		if (tvDocumentType != null) {
			tvDocumentType.setText(HtmlCompat.fromHtml(getContext().getString(R.string.netverify_helpview_small_title_capture, documentType, ""), HtmlCompat.FROM_HTML_MODE_LEGACY));
		}

		setHelpText(customScanViewPresenter.getHelpText());
		customScanViewPresenter.retryScan();
	}

	/**
	 * Called if fallback is being used (either because it is the only option available or user chose to use it)
	 */
	private void onStartFallback() {
		if (customScanViewPresenter != null) {
			customScanViewPresenter.startFallback();
			setFallbackVisibility(false);
		}
	}

	//#####################################################
	// HELPER METHODS
	//#####################################################

	/**
	 * Creates dialog to handle disbled NFC settings
	 */
	private void buildNfcSettingsDialog() {
		try {
			if(getActivity() != null) {
				new MaterialAlertDialogBuilder(getActivity())
					.setTitle(com.jumio.nv.R.string.netverify_nfc_enable_dialog_title)
					.setMessage(com.jumio.nv.R.string.netverify_nfc_enable_dialog_text)
					.setPositiveButton(android.R.string.yes, (dialog, which) -> {
						dialog.dismiss();
						getActivity().startActivity(new Intent("android.settings.NFC_SETTINGS"));
					})
					.setNegativeButton(android.R.string.no, (dialog, which) -> {
						dialog.dismiss();
						customNfcViewPresenter.cancel();
					})
					.show();
			}
		} catch(Exception e) {
			Log.e(TAG, "dialog builder: ", e);
		}
	}

	private void buildNfcErrorDialog(String errorMessage, Boolean retryable) {
		try {
			if (getActivity() != null) {
				MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getActivity());
				dialogBuilder.setTitle(R.string.netverify_nfc_general_error_dialog_title);
				dialogBuilder.setMessage(errorMessage);

				if (retryable) {
					dialogBuilder.setPositiveButton(R.string.jumio_button_retry, (dialog, which) -> {
						dialog.dismiss();
						customNfcViewPresenter.retry();
					});
					dialogBuilder.setNegativeButton(R.string.jumio_button_cancel, (dialog, which) -> {
						dialog.dismiss();
						customNfcViewPresenter.cancel();
					});
				} else {
					dialogBuilder.setPositiveButton(R.string.jumio_button_cancel, (dialog, which) -> {
						dialog.dismiss();
						customNfcViewPresenter.cancel();
					});
				}
				dialogBuilder.show();
			}
		} catch (Exception e) {
			Log.e(TAG, "dialog builder: ", e);
		}
	}

	/**
	 * Sets text displayed at bottom of the screen to help user with the process
	 *
	 * @param helpText String
	 */
	private void setHelpText(String helpText) {
		if (!TextUtils.isEmpty(helpText)) {
			if (tvHelp != null) {
				tvHelp.setText(helpText);
			}
		}
	}

	/**
	 * Sets visibility of fallback button
	 *
	 * @param visible boolean
	 */
	private void setFallbackVisibility(boolean visible) {
		if (btnFallback != null) {
			btnFallback.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
	}

	/**
	 * Checks if device is in portrait mode
	 *
	 * @return boolean
	 */
	private boolean isPortrait() {
		Point size = new Point();
		if(getActivity() != null) {
			Display display = getActivity().getWindowManager().getDefaultDisplay();
			display.getSize(size);
		}
		return size.y > size.x;
	}

	/**
	 * Checks if device has flash available
	 */
	private boolean isFlashAvailable() {
		return customScanViewPresenter != null && !isOnConfirmation && customScanViewPresenter.hasFlash();
	}

	/**
	 * Checks if device can switch between front and back camera
	 */
	private boolean isSwitchCameraAvailable() {
		return customScanViewPresenter != null && !isOnConfirmation && customScanViewPresenter.hasMultipleCameras();
	}

	//#####################################################
	// SCAN METHODS
	//#####################################################

	/**
	 * Initializes a scan view that is used for both document and face scans
	 *
	 * @param customScanView NetverifyCustomScanView
	 */
	private void initScanView(NetverifyCustomScanView customScanView) {
		//Changes layout parameters if device is in portrait mode
		boolean isPortrait = isPortrait();
		ConstraintLayout.LayoutParams params = null;
		if (getActivity() != null) {
			params = new ConstraintLayout.LayoutParams(isPortrait ? ConstraintLayout.LayoutParams.MATCH_PARENT : ConstraintLayout.LayoutParams.WRAP_CONTENT,
				isPortrait ? ConstraintLayout.LayoutParams.WRAP_CONTENT : ScreenUtil.dpToPx(getActivity().getApplicationContext(), 300));
		}
		customScanView.setLayoutParams(params);
		if (customScanViewPresenter != null) {
			if (isPortrait) {
				customScanView.setRatio(isFaceScan() ? 0.71f : 0.9f);
			} else {
				customScanView.setRatio(isFaceScan() ? 1.66f : 1.0f);
			}
		}
	}

	private boolean isFaceScan() {
		return customScanViewPresenter.getScanMode() == NetverifyScanMode.FACE_MANUAL ||
			customScanViewPresenter.getScanMode() == NetverifyScanMode.FACE_IPROOV ||
			customScanViewPresenter.getScanMode() == NetverifyScanMode.FACE_ZOOM;
	}

	/**
	 * Handles NFC detection.
	 */
	private class NetverifyCustomNfcImpl implements NetverifyCustomNfcInterface {

		@Override
		public void onNetverifyNfcStarted() {
			Log.i(TAG, "onNetverifyNfcStarted");
			tvDocumentType.setText(R.string.netverify_nfc_header_extracting);
		}

		@Override
		public void onNetverifyNfcUpdate(int progress) {
			Log.i(TAG, String.format("onNetverifyNfcUpdate %d", progress));
		}

		@Override
		public void onNetverifyNfcFinished() {
			hideView(false, customAnimationView, btnSkipNfc);
			tvDocumentType.setText(R.string.netverify_nfc_header_finish);
			showView(tvSteps);
			Log.i(TAG, "onNetverifyNfcFinished");
		}

		@Override
		public void onNetverifyNfcSystemSettings() {
			buildNfcSettingsDialog();
			Log.i(TAG, "NFC not enabled");
		}

		@Override
		public void onNetverifyNfcError(String errorMessage, boolean retryable) {
			tvDocumentType.setText(R.string.netverify_nfc_header_start);
			buildNfcErrorDialog(errorMessage, retryable);
			Log.e(TAG, String.format("$errorMessage, retry possible: $retryable"));
		}
	}

	/**
	 * Handles actual scanning
	 */
	private class NetverifyCustomScanImpl implements NetverifyCustomScanInterface {

		/**
		 * Custom ScanView interface
		 * Handles finished scan, checks if all necessary sides have been scanned or not,
		 * starts another scan fragment if they're not all finished yet
		 *
		 * @param scanSide        the scanned side
		 * @param allPartsScanned true if all parts have been scanned
		 */
		@Override
		public void onNetverifyScanForPartFinished(ScanSide scanSide, boolean allPartsScanned) {
			customScanViewPresenter.destroy();
			customScanViewPresenter = null;

			// not all necessary parts scanned yet
			if (!allPartsScanned) {
				//index refers to list containing all possible sides
				callback.onScanForPartFinished();
			} else {
				//show loading on scan view during submission
				showSubmissionLoading();
				callback.onScanFinished();
			}
			Log.i(TAG, "onNetverifyScanForPartFinished");
		}

		/**
		 * Custom ScanView interface
		 */
		@Override
		public void onNetverifyCameraAvailable() {
			Log.i(TAG, "onNetverifyCameraAvailable");
			if (flash != null) {
				flash.setVisible(isFlashAvailable());
			}
		}

		/**
		 * Custom ScanView interface
		 * Shows loading screen, disables flash and camera switch button
		 */
		@Override
		public void onNetverifyExtractionStarted() {
			Log.i(TAG, "onNetverifyExtractionStarted");
			if (flash != null) {
				flash.setVisible(false);
			}
			if (switchCamera != null) {
				switchCamera.setVisible(false);
			}
			showLoading();
		}

		/**
		 * Custom ScanView interface
		 * Show loading indicator and display loading text, hide buttons
		 */
		@Override
		public void onNetverifyPrepareScanning() {
			Log.i(TAG, "onNetverifyPrepareScanning");
			showLoading();
			hideView(true, customScanView, btnFallback, btnConfirm, btnRetake, btnCapture);
			if(tvHelp != null) {
				tvHelp.setVisibility(View.VISIBLE);
				tvHelp.setText(getString(R.string.jumio_accessibility_loading));
			}
		}

		/**
		 * Custom ScanView interface
		 * Shows confirmation if scan has been finished
		 *
		 * @param confirmationType the type of confirmation that should be displayed
		 */
		@Override
		public void onNetverifyPresentConfirmationView(NetverifyConfirmationType confirmationType) {
			isOnConfirmation = true;
			Log.i(TAG, "onNetverifyPresentConfirmationView");
			if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
				Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayShowHomeEnabled(true);
			}
			showConfirmation(confirmationType == NetverifyConfirmationType.CHECK_DOCUMENT_SIDE);
		}

		/**
		 * No US Address has been found in the barcode. The scan preview will switch to frontside scanning if available.
		 * Check for the changed scan mode and help text. Will only be called on a Fastfill scan.
		 */
		@Override
		public void onNetverifyNoUSAddressFound() {
			Log.i(TAG, "onNetverifyNoUSAddressFound");
		}

		/**
		 * Face scanning is not possible in landscape orientation. Please notify the user accordingly
		 */
		@Override
		public void onNetverifyFaceInLandscape() {
			Log.i(TAG, "onNetverifyFaceInLandscape");
		}

		/**
		 * During the scanning of some ID cards, a legal advice need to be shown.
		 */
		@Override
		public void onNetverifyShowLegalAdvice(String legalAdvice) {
			Toast toast = Toast.makeText(getActivity().getApplicationContext(), legalAdvice, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			Log.i(TAG, "onNetverifyShowLegalAdvice: " + legalAdvice);
		}

		/**
		 * Notify the user that the image is blurry and therefore can't be taken.
		 */
		@Override
		public void onNetverifyDisplayBlurHint() {
			if(blurToast != null) {
				try {
					if (blurToast.getView().isShown()) {
						return;
					} else {
						blurToast.cancel();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			blurToast = Toast.makeText(getActivity().getApplicationContext(), R.string.jumio_scanview_refocus, Toast.LENGTH_SHORT);
			blurToast.setGravity(Gravity.CENTER, 0, 0);
			blurToast.show();
			Log.i(TAG, "onNetverifyDisplayBlurHint");
		}

		/**
		 * Handles scan cancellation, whether user cancelled it manually (back button) or scan went wrong
		 * because of a certain reason (bad lighting, etc.), starts up appropriate help animation in that case
		 *
		 * @param scanSide              the scanned side
		 * @param netverifyCancelReason reason why scan was cancelled
		 */
		@Override
		public void onNetverifyScanForPartCanceled(ScanSide scanSide, NetverifyCancelReason netverifyCancelReason) {
			Log.i(TAG, "onNetverifyScanForPartCanceled");
			if(isDetached()) {
				return;
			}
			hideView(false);
			if (tvHelp != null) {
				setHelpText(getString(R.string.netverify_scanview_snackbar_check_process_error));
			}

			switch (netverifyCancelReason) {
				case ERROR_GENERIC:
					displayRetryHelp(scanSide);
					break;
				case USER_BACK:
				case USER_CANCEL:
					if (scanSide == ScanSide.FACE) {
						displayRetryHelp(scanSide);
					} else {
						if (callback != null) {
							callback.onScanCancelled();
						}
					}
					break;
				case NOT_AVAILABLE:
					if (callback != null) {
						callback.onScanCancelled();
					}
					break;
				default:
					break;
			}
		}

		/**
		 * This function will be called when the NFC scan is getting prepared. If no NFC scan should be done, Null can be returned here.
		 *
		 * @return instance of {@link NetverifyCustomNfcInterface} or null
		 */
		@Override
		public NetverifyCustomNfcInterface getNetverifyCustomNfcInterface() {
			Log.i(TAG, "getNetverifyCustomNfcInterface");
			return new NetverifyCustomNfcImpl();
		}

		/**
		 * NFC scanning can be started now and can be controlled with the {@link NetverifyCustomNfcPresenter}
		 *
		 * @param netverifyCustomNfcPresenter NetverifyCustomNfcPresenter
		 */
		@Override
		public void onNetverifyStartNfcExtraction(NetverifyCustomNfcPresenter netverifyCustomNfcPresenter) {
			Log.i(TAG, "onNetverifyStartNfcExtraction");

			customNfcViewPresenter = netverifyCustomNfcPresenter;
			customNfcViewPresenter.getHelpAnimation(customAnimationView);

			tvDocumentType.setText(R.string.netverify_nfc_header_start);
			hideView(false, tvSteps);
			setHelpText(customNfcViewPresenter.getHelpText());

			showView(customAnimationView, btnSkipNfc);
			hideView(false, customScanView, btnConfirm, btnRetake);
		}
	}

	/**
	 * Interface for fragment interaction with activity.
	 */
	public interface OnScanFragmentInteractionListener {

		NetverifyCustomScanPresenter onInitScanningWithSide(ScanSide side, NetverifyCustomScanView scanView,
		                                                    NetverifyCustomConfirmationView confirmationView, NetverifyCustomScanInterface customScanInterface);

		void onScanForPartFinished();

		void onScanCancelled();

		void onScanFinished();
	}
}
