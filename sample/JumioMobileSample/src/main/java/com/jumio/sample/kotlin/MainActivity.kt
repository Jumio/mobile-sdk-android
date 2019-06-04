package com.jumio.sample.kotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.jumio.MobileSDK
import com.jumio.core.enums.JumioDataCenter
import com.jumio.nv.NetverifySDK
import com.jumio.sample.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Copyright 2019 Jumio Corporation All rights reserved.
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val KEY_API_TOKEN = "KEY_API_TOKEN"
        const val KEY_API_SECRET = "KEY_API_SECRET"
		const val KEY_DATACENTER = "KEY_DATACENTER"

        /* PUT YOUR NETVERIFY API TOKEN AND SECRET HERE */
        const val NETVERIFY_API_TOKEN = ""
        const val NETVERIFY_API_SECRET = ""
		val NETVERIFY_DATACENTER = JumioDataCenter.US

        /* PUT YOUR BAM API TOKEN AND SECRET HERE */
        const val BAM_API_TOKEN = ""
        const val BAM_API_SECRET = ""
		val BAM_DATACENTER = JumioDataCenter.US
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val drawerToggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		drawer_layout?.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        val menu = nav_view?.menu
        menu?.findItem(R.id.nav_sdk)?.title = MobileSDK.getSDKVersion()
        menu?.findItem(R.id.nav_netverify)?.let { onNavigationItemSelected(it) }
		nav_view?.setNavigationItemSelectedListener(this)
		nav_view?.itemIconTintList = null
    }

	public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == NetverifySDK.REQUEST_CODE_NFC_DETECTED) {
			val fragment = supportFragmentManager.fragments.get(0)
			if(fragment is NetverifyCustomFragment) {
				fragment.onActivityResult(requestCode, resultCode, data)
			}
		}

		super.onActivityResult(requestCode, resultCode, data)
	}

    /**
     * Check and request missing permissions for the SDK.
     *
     * @param requestCode the request code for the SDK
     */
    fun checkPermissions(requestCode: Int): Boolean {
        if (!MobileSDK.hasAllRequiredPermissions(this)) {
            //Acquire missing permissions.
            val mp = MobileSDK.getMissingPermissions(this)

            ActivityCompat.requestPermissions(this, mp, requestCode)
            //The result is received in onRequestPermissionsResult.
            return false
        } else {
            return true
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
                val nvCustomFragment = NetverifyCustomFragment()
                bundle.putString(KEY_API_TOKEN, NETVERIFY_API_TOKEN)
                bundle.putString(KEY_API_SECRET, NETVERIFY_API_SECRET)
				bundle.putSerializable(KEY_DATACENTER, NETVERIFY_DATACENTER)
                nvCustomFragment.arguments = bundle
                switchFragment(nvCustomFragment)
            }
            R.id.nav_authentication -> {
                val authFragment = AuthenticationFragment()
                bundle.putString(KEY_API_TOKEN, NETVERIFY_API_TOKEN)
                bundle.putString(KEY_API_SECRET, NETVERIFY_API_SECRET)
				bundle.putSerializable(KEY_DATACENTER, NETVERIFY_DATACENTER)
                authFragment.arguments = bundle
                switchFragment(authFragment)
            }
            R.id.nav_authentication_custom -> {
                val authCustomFragment = AuthenticationCustomFragment()
                bundle.putString(KEY_API_TOKEN, NETVERIFY_API_TOKEN)
                bundle.putString(KEY_API_SECRET, NETVERIFY_API_SECRET)
				bundle.putSerializable(KEY_DATACENTER, NETVERIFY_DATACENTER)
                authCustomFragment.arguments = bundle
                switchFragment(authCustomFragment)
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

		drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun switchFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commitAllowingStateLoss()
    }
}