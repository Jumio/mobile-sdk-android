// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jumio.defaultui.JumioActivity
import com.jumio.sample.R
import com.jumio.sample.compose.theme.JumioTheme
import com.jumio.sample.compose.views.atoms.AppBar
import com.jumio.sample.compose.views.molecules.NavigationDrawerMenu
import com.jumio.sample.compose.views.pages.HomePage
import com.jumio.sample.compose.views.pages.ResultPage
import com.jumio.sdk.JumioSDK
import com.jumio.sdk.preload.JumioPreloadCallback
import com.jumio.sdk.preload.JumioPreloader
import com.jumio.sdk.result.JumioResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"
private const val PERMISSION_REQUEST_CODE: Int = 303
private const val EXTRA_TOKEN = "token"
private const val EXTRA_DATACENTER = "datacenter"
private const val EXTRA_CUSTOMTHEME = "customtheme"

class MainActivity : ComponentActivity(), JumioPreloadCallback {

	private var showBottomSheet = MutableStateFlow(false)
	private var jumioResult: JumioResult? = null
	private val sdkForResultLauncher: ActivityResultLauncher<Intent> =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
			jumioResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				result.data?.getSerializableExtra(JumioActivity.EXTRA_RESULT, JumioResult::class.java)
			} else {
				@Suppress("DEPRECATION")
				result.data?.getSerializableExtra(JumioActivity.EXTRA_RESULT) as JumioResult?
			}
			showBottomSheet.value = true
			Log.d(TAG, "AccountId: ${jumioResult?.accountId}")
			Log.d(TAG, "WorkflowExecutionId: ${jumioResult?.workflowExecutionId}")
		}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			SetUpUI()
		}
		initModelPreloading()
	}

	@OptIn(ExperimentalMaterial3Api::class)
	@Composable
	private fun SetUpUI() {
		JumioTheme {
			val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
			val scope = rememberCoroutineScope()
			val snackBarHostState = remember { SnackbarHostState() }
			val sheetState = rememberModalBottomSheetState(
				skipPartiallyExpanded = false
			)
			val showBottomSheet = this.showBottomSheet.collectAsStateWithLifecycle()
			ModalNavigationDrawer(
				drawerState = drawerState,
				drawerContent = {
					NavigationDrawerMenu(
						openLink = {
							openLink(it)
						},
						cleanModels = {
							with(JumioPreloader) {
								init(this@MainActivity)
								clean()
								dispose()
							}
						}
					)
				}
			) {
				Scaffold(
					modifier = Modifier.fillMaxSize(),
					snackbarHost = {
						SnackbarHost(hostState = snackBarHostState)
					},
					topBar = {
						AppBar(
							title = stringResource(id = R.string.app_bar_title),
							onNavigationIconClick = {
								scope.launch {
									drawerState.apply {
										if (isClosed) open() else close()
									}
								}
							}
						)
					},
					content = { innerPadding ->
						HomePage(
							modifier = Modifier.padding(innerPadding),
							onCustomUiClick = { token, dataCenter ->
								onCustomUIClick(scope, snackBarHostState, token, dataCenter)
							},
							onDefaultUiClick = { token, dataCenter ->
								onDefaultUIClick(scope, snackBarHostState, token, dataCenter)
							}
						)
						if (showBottomSheet.value) {
							jumioResult?.let { result ->
								ModalBottomSheet(
									onDismissRequest = {
										this.showBottomSheet.value = false
									},
									sheetState = sheetState,
									modifier = Modifier.fillMaxSize()
								) {
									ResultPage(jumioResult = result) {
										scope.launch { sheetState.hide() }.invokeOnCompletion {
											if (!sheetState.isVisible) {
												this@MainActivity.showBottomSheet.value = false
											}
										}
									}
								}
							} ?: run {
								this.showBottomSheet.value = false
							}
						}
					}
				)
			}
		}
	}

	private fun onCustomUIClick(
		scope: CoroutineScope,
		snackBarHostState: SnackbarHostState,
		token: String,
		dataCenter: String,
	) {
		if (!isTokenSet(scope, snackBarHostState, token)) return
		if (!isSupportedPlatform(scope, snackBarHostState)) return
		if (!checkPermissions()) return
		val intent = Intent(this, CustomUIActivity::class.java).apply {
			putExtra(EXTRA_TOKEN, token)
			putExtra(EXTRA_DATACENTER, dataCenter)
			// The following parameter can be used to apply a custom theme:
			putExtra(EXTRA_CUSTOMTHEME, R.style.AppThemeCustomJumio)
		}
		sdkForResultLauncher.launch(intent)
	}

	private fun onDefaultUIClick(
		scope: CoroutineScope,
		snackBarHostState: SnackbarHostState,
		token: String,
		dataCenter: String,
	) {
		if (!isTokenSet(scope, snackBarHostState, token)) return
		if (!isSupportedPlatform(scope, snackBarHostState)) return
		if (!checkPermissions()) return
		val intent = Intent(this, JumioActivity::class.java)
		intent.putExtra(JumioActivity.EXTRA_TOKEN, token)
		intent.putExtra(JumioActivity.EXTRA_DATACENTER, dataCenter)
		// The following intent extra can be used to customize the Theme of Default UI
		// intent.putExtra(JumioActivity.EXTRA_CUSTOM_THEME, R.style.AppThemeCustomJumio)
		sdkForResultLauncher.launch(intent)
	}

	/**
	 * [JumioPreloadCallback.preloadFinished] will be called by the [JumioPreloader] once
	 * preloading finished. You may want to start the jumio sdk at this point.
	 */

	override fun preloadFinished() {
		Log.d(TAG, "Preloading finished")

		// Once preloading is finished successfully we can dispose the preloader (optional)
		JumioPreloader.dispose()
	}

	/**
	 * This functions shows how to setup the [JumioPreloader] and initiate preloading.
	 *
	 * Preloading in this case means that the SDK checks which models are required, and downloads them in advance.
	 */
	private fun initModelPreloading() = with(JumioPreloader) {
		init(this@MainActivity)
		setCallback(this@MainActivity)
		preloadIfNeeded()
	}

	/**
	 * Check and request missing permissions for the SDK.
	 *
	 * @param requestCode the request code for the SDK
	 */
	private fun checkPermissions(requestCode: Int = PERMISSION_REQUEST_CODE) =
		if (!JumioSDK.hasAllRequiredPermissions(this)) {
			// Acquire missing permissions.
			val mp = JumioSDK.getMissingPermissions(this)
			ActivityCompat.requestPermissions(
				this,
				mp,
				requestCode
			) // The result is received in onRequestPermissionsResult.
			false
		} else {
			true
		}

	private fun openLink(url: String) {
		val intent = Intent(Intent.ACTION_VIEW).apply {
			data = Uri.parse(url)
		}
		startActivity(intent)
	}

	private fun isSupportedPlatform(scope: CoroutineScope, snackBarHostState: SnackbarHostState) =
		if (!JumioSDK.isSupportedPlatform(this)) {
			scope.launch {
				snackBarHostState.showSnackbar(
					message = "This device is not supported!",
					duration = SnackbarDuration.Long
				)
			}
			false
		} else {
			true
		}

	private fun isTokenSet(scope: CoroutineScope, snackBarHostState: SnackbarHostState, token: String) =
		if (token.isBlank()) {
			scope.launch {
				snackBarHostState.showSnackbar(
					message = "Token needs to be set",
					duration = SnackbarDuration.Long
				)
			}
			false
		} else {
			true
		}
}
