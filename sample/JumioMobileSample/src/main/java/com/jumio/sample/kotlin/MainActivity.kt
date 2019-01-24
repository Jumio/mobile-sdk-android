package com.jumio.sample.kotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.jumio.MobileSDK
import com.jumio.sample.R

/**
 * Copyright 2018 Jumio Corporation All rights reserved.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        const val KEY_SWITCH_ONE_TEXT = "KEY_SWITCH_ONE_TEXT"
        const val KEY_SWITCH_TWO_TEXT = "KEY_SWITCH_TWO_TEXT"
        const val KEY_API_TOKEN = "KEY_API_TOKEN"
        const val KEY_API_SECRET = "KEY_API_SECRET"

        /* PUT YOUR NETVERIFY API TOKEN AND SECRET HERE */
        const val NETVERIFY_API_TOKEN = ""
        const val NETVERIFY_API_SECRET = ""

        /* PUT YOUR BAM API TOKEN AND SECRET HERE */
        const val BAM_API_TOKEN = ""
        const val BAM_API_SECRET = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        val mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        val mViewPager = findViewById<View>(R.id.container) as ViewPager
        mViewPager.offscreenPageLimit = 4
        mViewPager.adapter = mSectionsPagerAdapter

        val tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(mViewPager)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private inner class SectionsPagerAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            val bundle = Bundle()

            when (position) {
                0 -> {
                    val nvFragment = NetverifyFragment()
                    bundle.putString(KEY_SWITCH_ONE_TEXT, resources.getString(R.string.netverify_verification_required))
                    bundle.putString(KEY_SWITCH_TWO_TEXT, resources.getString(R.string.netverify_face_required))
                    bundle.putString(KEY_API_TOKEN, NETVERIFY_API_TOKEN)
                    bundle.putString(KEY_API_SECRET, NETVERIFY_API_SECRET)
                    nvFragment.arguments = bundle
                    return nvFragment
                }
                1 -> {
                    val nvCustomFragment = NetverifyCustomFragment()
                    bundle.putString(KEY_API_TOKEN, NETVERIFY_API_TOKEN)
                    bundle.putString(KEY_API_SECRET, NETVERIFY_API_SECRET)
                    nvCustomFragment.arguments = bundle
                    return nvCustomFragment
                }
                2 -> {
                    val documentVerificationFragment = DocumentVerificationFragment()
                    bundle.putString(KEY_SWITCH_ONE_TEXT, resources.getString(R.string.documentverification_enable_extraction))
                    bundle.putString(KEY_API_TOKEN, NETVERIFY_API_TOKEN)
                    bundle.putString(KEY_API_SECRET, NETVERIFY_API_SECRET)
                    documentVerificationFragment.arguments = bundle
                    return documentVerificationFragment
                }
                3 -> {
                    val bamFragment = BamFragment()
                    bundle.putString(KEY_SWITCH_ONE_TEXT, resources.getString(R.string.bam_expiry_required))
                    bundle.putString(KEY_SWITCH_TWO_TEXT, resources.getString(R.string.bam_cvv_required))
                    bundle.putString(KEY_API_TOKEN, BAM_API_TOKEN)
                    bundle.putString(KEY_API_SECRET, BAM_API_SECRET)
                    bamFragment.arguments = bundle
                    return bamFragment
                }
                4 -> {
                    val bamCustomFragment = BamCustomFragment()
                    bundle.putString(KEY_API_TOKEN, BAM_API_TOKEN)
                    bundle.putString(KEY_API_SECRET, BAM_API_SECRET)
                    bamCustomFragment.arguments = bundle
                    return bamCustomFragment
                }
            }
            return null
        }

        override fun getCount(): Int {
            return 5
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return resources.getString(R.string.section_netverify)
                1 -> return resources.getString(R.string.section_netverify_custom)
                2 -> return resources.getString(R.string.section_documentverification)
                3 -> return resources.getString(R.string.section_bamcheckout)
                4 -> return resources.getString(R.string.section_bam_custom)
            }
            return null
        }
    }
}