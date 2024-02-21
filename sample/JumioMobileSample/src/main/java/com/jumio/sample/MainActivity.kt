// Copyright 2023 Jumio Corporation, all rights reserved.
package com.jumio.sample

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.jumio.defaultui.JumioActivity
import com.jumio.sample.customui.CustomUiActivity
import com.jumio.sample.databinding.ActivityMainBinding
import com.jumio.sdk.JumioSDK
import com.jumio.sdk.enums.JumioDataCenter
import com.jumio.sdk.preload.JumioPreloadCallback
import com.jumio.sdk.preload.JumioPreloader
import com.jumio.sdk.result.JumioFaceResult
import com.jumio.sdk.result.JumioIDResult
import com.jumio.sdk.result.JumioResult

private const val PERMISSION_REQUEST_CODE: Int = 303
private const val TAG = "MainActivity"

/**
 * Sample activity that handles the whole jumio sdk workflow for the custom ui approach
 */
class MainActivity :
	AppCompatActivity(),
	NavigationView.OnNavigationItemSelectedListener,
	View.OnClickListener,
	JumioPreloadCallback {

	private lateinit var binding: ActivityMainBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		// Required for vector drawable compat handling https://stackoverflow.com/a/37864531/1297835
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

		binding = ActivityMainBinding.inflate(layoutInflater)
		val view = binding.root
		setContentView(view)
		setSupportActionBar(binding.toolbar)

		binding.btnStartDefault.setOnClickListener(this)
		binding.btnStartCustom.setOnClickListener(this)

		val drawerToggle = ActionBarDrawerToggle(
			this,
			binding.drawerLayout,
			binding.toolbar,
			R.string.navigation_drawer_open,
			R.string.navigation_drawer_close
		)
		binding.drawerLayout.addDrawerListener(drawerToggle)
		drawerToggle.syncState()

		val menu = binding.navView.menu
		menu.findItem(R.id.nav_sdk).title = JumioSDK.version

		binding.navView.setNavigationItemSelectedListener(this)
		binding.navView.itemIconTintList = null

		initModelPreloading()
	}

	@Deprecated("Deprecated in Java")
	override fun onBackPressed() {
		if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
			binding.drawerLayout.closeDrawer(GravityCompat.START)
		} else {
			onBackPressedDispatcher.onBackPressed()
		}
	}

	override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
		when (menuItem.itemId) {
			R.id.nav_terms_of_use, R.id.nav_privacy_policy -> openLink(
				"https://www.jumio.com/legal-information/privacy-policy/jumio-showcase-app-privacy-terms/"
			)
			R.id.nav_licenses -> openLink("https://github.com/Jumio/mobile-sdk-android/tree/master/licenses")
			R.id.nav_sdk -> openLink("https://github.com/Jumio/mobile-sdk-android/releases/latest")
			R.id.nav_advanced_clean_models -> with(JumioPreloader) {
				init(this@MainActivity)
				clean()
				dispose()
			}
		}

		binding.drawerLayout.closeDrawer(GravityCompat.START)
		return true
	}

	override fun onClick(v: View?) {
		if (!isTokenSet()) return
		if (!isSupportedPlatform()) return
		if (!checkPermissions()) return

		v?.let {
			val token = binding.tokenEditText.text.toString()

			when (it.id) {
				R.id.btn_startCustom -> {
					val dataCenter = JumioDataCenter.valueOf(binding.datacenterSpinner.selectedItem as String)
					CustomUiActivity.start(
						this,
						sdkForResultLauncher,
						token,
						dataCenter
						// The following parameter can be used to apply a custom theme:
						// customTheme = R.style.AppThemeCustomJumio
					)
				}
				R.id.btn_startDefault -> {
					val intent = Intent(this, JumioActivity::class.java)
					val dataCenter: String = binding.datacenterSpinner.selectedItem.toString()
					intent.putExtra(JumioActivity.EXTRA_TOKEN, token)
					intent.putExtra(JumioActivity.EXTRA_DATACENTER, dataCenter)
					// The following intent extra can be used to customize the Theme of Default UI
					// intent.putExtra(JumioActivity.EXTRA_CUSTOM_THEME, R.style.AppThemeCustomJumio)
					sdkForResultLauncher.launch(intent)
				}
			}
		}
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
	private fun checkPermissions(
		requestCode: Int = PERMISSION_REQUEST_CODE,
	) = if (!JumioSDK.hasAllRequiredPermissions(this)) {
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

	private fun isSupportedPlatform() = if (!JumioSDK.isSupportedPlatform(this)) {
		Snackbar.make(binding.root, "This device is not supported!", Snackbar.LENGTH_LONG).show()
		false
	} else {
		true
	}

	private fun isTokenSet() = if (binding.tokenEditText.text.isNullOrBlank()) {
		Snackbar.make(binding.root, "Token needs to be set", Snackbar.LENGTH_LONG).show()
		false
	} else {
		true
	}

	private val sdkForResultLauncher: ActivityResultLauncher<Intent> =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

			val jumioResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				result.data?.getSerializableExtra(JumioActivity.EXTRA_RESULT, JumioResult::class.java)
			} else {
				@Suppress("DEPRECATION")
				result.data?.getSerializableExtra(JumioActivity.EXTRA_RESULT) as JumioResult?
			}

			Log.d(TAG, "AccountId: ${jumioResult?.accountId}")
			Log.d(TAG, "WorkflowExecutionId: ${jumioResult?.workflowExecutionId}")

			if (jumioResult?.isSuccess == true) {
				jumioResult.credentialInfos?.forEach {
					when (jumioResult.getResult(it)) {
						is JumioIDResult -> {
							// check your id result here
						}
						is JumioFaceResult -> {
							// check your face result here
						}
					}
				}
			} else {
				jumioResult?.error?.let {
					Log.d(TAG, it.message)
				}
			}
		}
}
