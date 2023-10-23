// Copyright 2021 Jumio Corporation, all rights reserved.
package com.jumio.sample.kotlin

import android.content.Intent
import android.net.Uri
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
import com.jumio.sample.R
import com.jumio.sample.databinding.ActivityMainBinding
import com.jumio.sample.kotlin.customui.CustomUiActivity
import com.jumio.sdk.JumioSDK
import com.jumio.sdk.enums.JumioDataCenter
import com.jumio.sdk.result.JumioFaceResult
import com.jumio.sdk.result.JumioIDResult
import com.jumio.sdk.result.JumioResult

private const val PERMISSION_REQUEST_CODE: Int = 303
private val TAG = MainActivity::class.java.simpleName

/**
 * Sample activity that handles the whole jumio sdk workflow for the custom ui approach
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

	private lateinit var binding: ActivityMainBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState) // Required for vector drawable compat handling https://stackoverflow.com/a/37864531/1297835
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

		binding = ActivityMainBinding.inflate(layoutInflater)
		val view = binding.root
		setContentView(view)
		setSupportActionBar(binding.toolbar)

		binding.btnStartDefault.setOnClickListener(this)
		binding.btnStartCustom.setOnClickListener(this)

		val drawerToggle = ActionBarDrawerToggle(
			this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
		)
		binding.drawerLayout.addDrawerListener(drawerToggle)
		drawerToggle.syncState()

		val menu = binding.navView.menu
		menu.findItem(R.id.nav_sdk).title = JumioSDK.version
		onNavigationItemSelected(menu.findItem(R.id.nav_products))

		binding.navView.setNavigationItemSelectedListener(this)
		binding.navView.itemIconTintList = null
	}

	override fun onBackPressed() {
		if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
			binding.drawerLayout.closeDrawer(GravityCompat.START)
		} else {
			super.onBackPressed()
		}
	}

	/**
	 * Check and request missing permissions for the SDK.
	 *
	 * @param requestCode the request code for the SDK
	 */
	private fun checkPermissions(requestCode: Int = PERMISSION_REQUEST_CODE) =
		if (!JumioSDK.hasAllRequiredPermissions(this)) { //Acquire missing permissions.
			val mp = JumioSDK.getMissingPermissions(this)
			ActivityCompat.requestPermissions(this, mp, requestCode) //The result is received in onRequestPermissionsResult.
			false
		} else {
			true
		}

	override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
		when (menuItem.itemId) {
			R.id.nav_terms_conditions -> openLink("https://www.jumio.com/legal-information/privacy-policy")
			R.id.nav_licenses -> openLink("https://github.com/Jumio/mobile-sdk-android/tree/master/licenses")
		}
		binding.drawerLayout.closeDrawer(GravityCompat.START)
		return true
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

	override fun onClick(v: View?) {
		if (!isTokenSet()) return
		if (!isSupportedPlatform()) return
		if (!checkPermissions()) return

		v?.let {
			val token = binding.tokenEditText.text.toString()

			when (it.id) {
				R.id.btn_startCustom -> {
					val dataCenter = JumioDataCenter.valueOf(binding.datacenterSpinner.selectedItem as String)
					CustomUiActivity.start(this, sdkForResultLauncher, token, dataCenter)
				}
				R.id.btn_startDefault -> {
					val intent = Intent(this, JumioActivity::class.java)
					val dataCenter: String = binding.datacenterSpinner.selectedItem.toString()
					intent.putExtra(JumioActivity.EXTRA_TOKEN, token)
					intent.putExtra(JumioActivity.EXTRA_DATACENTER, dataCenter)
					//The following intent extra can be used to customize the Theme of Default UI
					//intent.putExtra(JumioActivity.EXTRA_CUSTOM_THEME, R.style.AppThemeCustomJumio)
					sdkForResultLauncher.launch(intent)
				}
			}
		}
	}

	private val sdkForResultLauncher: ActivityResultLauncher<Intent> =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
			val jumioResult: JumioResult? = result.data?.getSerializableExtra(JumioActivity.EXTRA_RESULT) as JumioResult?
			Log.d(TAG, "AccountId: ${jumioResult?.accountId}")
			Log.d(TAG, "WorkflowExecutionId: ${jumioResult?.workflowExecutionId}")

			if (jumioResult?.isSuccess == true) {
				jumioResult.credentialInfos?.forEach {
					when (jumioResult.getResult(it)) {
						is JumioIDResult -> { //check your id result here
						}
						is JumioFaceResult -> { //check your face result here
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
