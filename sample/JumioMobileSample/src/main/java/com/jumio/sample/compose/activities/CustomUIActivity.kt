// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jumio.defaultui.JumioActivity
import com.jumio.sample.compose.theme.JumioTheme
import com.jumio.sample.compose.viewModel.CustomUIViewModel
import com.jumio.sample.compose.viewModel.ViewModelFactory
import com.jumio.sample.compose.views.navigation.AppNavigation
import com.jumio.sample.compose.views.pages.AcquireModeSelectionPage
import com.jumio.sample.compose.views.pages.ConfirmationPage
import com.jumio.sample.compose.views.pages.ConsentPage
import com.jumio.sample.compose.views.pages.CountryAndDocumentSelectionPage
import com.jumio.sample.compose.views.pages.DigitalIdentityPage
import com.jumio.sample.compose.views.pages.ErrorPage
import com.jumio.sample.compose.views.pages.LoaderPage
import com.jumio.sample.compose.views.pages.NfcScanPage
import com.jumio.sample.compose.views.pages.RejectionPage
import com.jumio.sample.compose.views.pages.ScanPage
import com.jumio.sample.compose.views.pages.UploadFileHelpPage
import com.jumio.sdk.JumioSDK
import com.jumio.sdk.enums.JumioDataCenter
import com.jumio.sdk.enums.JumioScanStep
import com.jumio.sdk.util.JumioDeepLinkHandler
import com.jumio.sdk.views.JumioActivityAttacher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val EXTRA_TOKEN = "token"
private const val EXTRA_DATACENTER = "datacenter"
private const val TAG = "CustomUIActivity"
private const val PERMISSION_REQUEST_CODE = 100
private const val EXTRA_CUSTOMTHEME = "customtheme"

class CustomUIActivity : ComponentActivity() {

	private val viewModel: CustomUIViewModel by viewModels {
		val token = intent.getStringExtra(EXTRA_TOKEN) as String
		val dataCenter = intent.getStringExtra(EXTRA_DATACENTER)?.let { JumioDataCenter.valueOf(it) } as JumioDataCenter
		val customTheme = intent.getIntExtra(EXTRA_CUSTOMTHEME, 0)
		ViewModelFactory(
			owner = this,
			application = this.application,
			token = token,
			dataCenter = dataCenter,
			customTheme = customTheme
		)
	}

	private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		if (result.resultCode == RESULT_OK) {
			try {
				result.data?.let {
					val returnUri = it.data ?: throw Exception("Could not get Uri")
					contentResolver.openFileDescriptor(returnUri, "r")?.use { fileDescriptor ->
						viewModel.fileAttacher.setFileDescriptor(fileDescriptor)
					} ?: throw Exception("Could not open file descriptor")
				}
			} catch (exception: Exception) {
				showError(exception.message)
			}
		}
	}

	private fun validatePermissions(): Boolean {
		if (JumioSDK.hasAllRequiredPermissions(this)) {
			return true
		}

		val mp = JumioSDK.getMissingPermissions(this)
		ActivityCompat.requestPermissions(this, mp, PERMISSION_REQUEST_CODE)

		return false
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)

		if (requestCode != PERMISSION_REQUEST_CODE ||
			grantResults.isEmpty() ||
			grantResults[0] != PackageManager.PERMISSION_GRANTED
		) {
			finish()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setObserver()
		enableEdgeToEdge()
		setContent {
			JumioTheme {
				Scaffold(
					content = {
						SetContent(modifier = Modifier.consumeWindowInsets(it).padding(it))
					}
				)
			}
		}
		validatePermissions()
	}

	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)

		intent.data?.let { deepLink ->
			val activeScanPart = viewModel.currentScanPart ?: return
			JumioDeepLinkHandler.consumeForScanPart(deepLink, activeScanPart)
		}
	}

	@Composable
	fun SetContent(modifier: Modifier) {
		val navController = rememberNavController()
		LaunchedEffect(viewModel.navigationState) {
			viewModel.navigationState.collectLatest { navigationState ->
				navigationState?.let { navigationPage ->
					viewModel.navigationState.value = null
					if (viewModel.isNewCredentialStarted) {
						viewModel.isNewCredentialStarted = false
						navController.navigate(navigationPage, NavOptions.Builder().setPopUpTo(AppNavigation.Consent, false).build())
						return@collectLatest
					}
					navController.previousBackStackEntry?.destination?.let { destination ->
						if (
							(destination.hasRoute<AppNavigation.Scan>() && navigationPage == AppNavigation.Scan) ||
							(destination.hasRoute<AppNavigation.UploadFileHelp>() && navigationPage == AppNavigation.UploadFileHelp) ||
							(destination.hasRoute<AppNavigation.NfcScan>() && navigationPage == AppNavigation.NfcScan)
						) {
							navController.popBackStack()
							return@collectLatest
						}
					}
					navController.currentBackStackEntry?.destination?.let { destination ->
						if (destination.hasRoute(AppNavigation.Loader::class) ||
							destination.hasRoute(AppNavigation.Error::class) ||
							destination.hasRoute(AppNavigation.Confirmation::class) ||
							destination.hasRoute(AppNavigation.Rejection::class)
						) {
							navController.navigate(navigationPage, NavOptions.Builder().setPopUpTo(destination.id, true).build())
						} else {
							navController.navigate(navigationPage, NavOptions.Builder().build())
						}
					} ?: run {
						navController.navigate(navigationPage, NavOptions.Builder().build())
					}
				}
			}
		}

		NavHost(navController = navController, startDestination = AppNavigation.Consent) {
			composable<AppNavigation.Consent> {
				ConsentPage(viewModel = viewModel, modifier = modifier, onClose = ::closeAndFinish)
			}
			composable<AppNavigation.SelectCountryAndDocument> {
				CountryAndDocumentSelectionPage(viewModel = viewModel, modifier = modifier, onClose = ::closeAndFinish)
			}
			composable<AppNavigation.Scan> {
				ScanPage(viewModel = viewModel, modifier = modifier)
			}
			composable<AppNavigation.AcquireMode> {
				AcquireModeSelectionPage(
					viewModel = viewModel,
					modifier = modifier,
					onClose = ::closeAndFinish
				)
			}
			composable<AppNavigation.DigitalIdentity> {
				DigitalIdentityPage(viewModel = viewModel, modifier = modifier)
			}
			composable<AppNavigation.Confirmation> {
				ConfirmationPage(viewModel = viewModel, modifier = modifier, onClose = ::closeAndFinish)
			}
			composable<AppNavigation.Rejection> {
				RejectionPage(viewModel = viewModel, modifier = modifier, onClose = ::closeAndFinish)
			}
			composable<AppNavigation.Loader> {
				val args = it.toRoute<AppNavigation.Loader>()
				LoaderPage(title = args.title, modifier = modifier)
			}
			composable<AppNavigation.Error> {
				val args = it.toRoute<AppNavigation.Error>()
				ErrorPage(
					message = args.message,
					isRetryable = args.isRetryable,
					onRetryClick = { viewModel.onRetry() },
					onClose = ::closeAndFinish,
					modifier = modifier
				)
			}
			composable<AppNavigation.UploadFileHelp> {
				UploadFileHelpPage(
					requirements = viewModel.fileAttacher.requirements,
					onSelectFile = {
						openDocument()
					},
					onBack = {
						navController.popBackStack()
					},
					modifier = modifier
				)
			}
			composable<AppNavigation.NfcScan> {
				NfcScanPage(
					viewModel = viewModel,
					onClose = ::closeAndFinish,
					onBackPress = {
						viewModel.cancelCurrentScanPart()
						navController.popBackStack(AppNavigation.Scan, true)
					},
					modifier = modifier
				)
			}
		}
	}

	private fun openDocument() {
		val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
			addCategory(Intent.CATEGORY_OPENABLE)
			type = "*/*"
			putExtra(Intent.EXTRA_MIME_TYPES, viewModel.fileAttacher.requirements.mimeTypes.toTypedArray())
		}
		try {
			launcher.launch(intent)
		} catch (exception: Exception) {
			showError(exception.message)
		}
	}

	private fun showError(message: String?) {
		Toast.makeText(this, message ?: "Unknown Error", Toast.LENGTH_LONG).show()
	}

	private fun setObserver() {
		lifecycleScope.launch {
			viewModel.workflowResult.collectLatest { jumioResult ->
				jumioResult?.let {
					val data = Intent()
					data.putExtra(JumioActivity.EXTRA_RESULT, it)
					setResult(RESULT_OK, data)
					finish()
				}
			}
		}
		lifecycleScope.launch {
			viewModel.scanStepEvent.collectLatest { jumioScanStep ->
				jumioScanStep?.let { scanStep ->
					if (scanStep == JumioScanStep.ATTACH_ACTIVITY) {
						viewModel.currentScanPart?.let { JumioActivityAttacher(this@CustomUIActivity).attach(it) }
					}
				}
			}
		}
	}

	private fun closeAndFinish() {
		viewModel.finishController()
		finish()
	}

	override fun onDestroy() {
		viewModel.finishController()
		super.onDestroy()
	}
}
