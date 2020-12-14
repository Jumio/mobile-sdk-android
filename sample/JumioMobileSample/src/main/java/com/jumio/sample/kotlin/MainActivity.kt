package com.jumio.sample.kotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.jumio.MobileSDK
import com.jumio.core.enums.JumioDataCenter
import com.jumio.nv.NetverifySDK
import com.jumio.sample.R
import com.jumio.sample.kotlin.bam.BamCustomFragment
import com.jumio.sample.kotlin.bam.BamFragment
import com.jumio.sample.kotlin.documentverification.DocumentVerificationFragment
import com.jumio.sample.kotlin.netverify.NetverifyFragment
import com.jumio.sample.kotlin.netverify.customui.NetverifyCustomActivity

/**
 * Copyright 2019 Jumio Corporation All rights reserved.
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		// Required for vector drawable compat handling https://stackoverflow.com/a/37864531/1297835
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

		setContentView(R.layout.activity_main)
		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		setSupportActionBar(toolbar)
		val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
		val drawerToggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		drawer.addDrawerListener(drawerToggle)
		drawerToggle.syncState()
		val navigationView = findViewById<NavigationView>(R.id.nav_view)
		if (navigationView != null) {
			val menu = navigationView.menu
			menu.findItem(R.id.nav_sdk).title = MobileSDK.getSDKVersion()
			onNavigationItemSelected(menu.findItem(R.id.nav_netverify))
			navigationView.setNavigationItemSelectedListener(this)
			navigationView.itemIconTintList = null
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == NetverifySDK.REQUEST_CODE_NFC_DETECTED) {
			val fragment = supportFragmentManager.fragments[0]
		}
		super.onActivityResult(requestCode, resultCode, data)
	}

	override fun onBackPressed() {
		val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START)
		} else {
			super.onBackPressed()
		}
	}

	/**
	 * Check and request missing permissions for the SDK.
	 *
	 * @param requestCode the request code for the SDK
	 */
	fun checkPermissions(requestCode: Int): Boolean {
		return if (!MobileSDK.hasAllRequiredPermissions(this)) { //Acquire missing permissions.
			val mp = MobileSDK.getMissingPermissions(this)
			ActivityCompat.requestPermissions(this, mp, requestCode)
			//The result is received in onRequestPermissionsResult.
			false
		} else {
			true
		}
	}

	override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
		val bundle = Bundle()
		when (menuItem.itemId) {
			R.id.nav_netverify -> {
				val nvFragment = NetverifyFragment()
				bundle.putString(KEY_API_TOKEN, NETVERIFY_API_TOKEN)
				bundle.putString(KEY_API_SECRET, NETVERIFY_API_SECRET)
				bundle.putSerializable(KEY_DATACENTER, NETVERIFY_DATACENTER)
				nvFragment.arguments = bundle
				switchFragment(nvFragment)
			}
			R.id.nav_netverify_custom -> {
				val nvCustomActivity = Intent(applicationContext, NetverifyCustomActivity::class.java)
				bundle.putString(KEY_API_TOKEN, NETVERIFY_API_TOKEN)
				bundle.putString(KEY_API_SECRET, NETVERIFY_API_SECRET)
				bundle.putSerializable(KEY_DATACENTER, NETVERIFY_DATACENTER)
				nvCustomActivity.putExtras(bundle)
				startActivity(nvCustomActivity)
			}
			R.id.nav_documentverification -> {
				val dvFragment = DocumentVerificationFragment()
				bundle.putString(KEY_API_TOKEN, NETVERIFY_API_TOKEN)
				bundle.putString(KEY_API_SECRET, NETVERIFY_API_SECRET)
				bundle.putSerializable(KEY_DATACENTER, NETVERIFY_DATACENTER)
				dvFragment.arguments = bundle
				switchFragment(dvFragment)
			}
			R.id.nav_bam -> {
				val bamFragment = BamFragment()
				bundle.putString(KEY_API_TOKEN, BAM_API_TOKEN)
				bundle.putString(KEY_API_SECRET, BAM_API_SECRET)
				bundle.putSerializable(KEY_DATACENTER, BAM_DATACENTER)
				bamFragment.arguments = bundle
				switchFragment(bamFragment)
			}
			R.id.nav_bam_custom -> {
				val bamCustomFragment = BamCustomFragment()
				bundle.putString(KEY_API_TOKEN, BAM_API_TOKEN)
				bundle.putString(KEY_API_SECRET, BAM_API_SECRET)
				bundle.putSerializable(KEY_DATACENTER, BAM_DATACENTER)
				bamCustomFragment.arguments = bundle
				switchFragment(bamCustomFragment)
			}
			R.id.nav_terms_conditions -> openLink("https://www.jumio.com/legal-information/privacy-policy")
			R.id.nav_licenses -> openLink("https://github.com/Jumio/mobile-sdk-android/tree/master/licenses")
		}
		val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
		drawer.closeDrawer(GravityCompat.START)
		return true
	}

	private fun openLink(url: String) {
		val intent = Intent(Intent.ACTION_VIEW)
		intent.data = Uri.parse(url)
		startActivity(intent)
	}

	private fun switchFragment(fragment: Fragment) {
		val supportFragmentManager = supportFragmentManager
		val fragmentTransaction = supportFragmentManager.beginTransaction()
		fragmentTransaction.replace(R.id.fragment_container, fragment)
		fragmentTransaction.commitAllowingStateLoss()
	}

	companion object {
		const val KEY_API_TOKEN = "KEY_API_TOKEN"
		const val KEY_API_SECRET = "KEY_API_SECRET"
		const val KEY_DATACENTER = "KEY_DATACENTER"
		/*
	 * PUT YOUR NETVERIFY API TOKEN AND SECRET HERE
	 * Do not store your credentials hardcoded within the app. Make sure to store them server-side and load your credentials during runtime.
	 */
		private const val NETVERIFY_API_TOKEN = ""
		private const val NETVERIFY_API_SECRET = ""
		private val NETVERIFY_DATACENTER = JumioDataCenter.US
		/*
	 * PUT YOUR BAM API TOKEN AND SECRET HERE
	 * Do not store your credentials hardcoded within the app. Make sure to store them server-side and load your credentials during runtime.
	 */
		private const val BAM_API_TOKEN = ""
		private const val BAM_API_SECRET = ""
		private val BAM_DATACENTER = JumioDataCenter.US
	}
}