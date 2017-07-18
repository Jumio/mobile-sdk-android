package com.jumio.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.jumio.MobileSDK;
import com.jumio.bam.BamSDK;
import com.jumio.core.exceptions.MissingPermissionException;
import com.jumio.nv.NetverifySDK;

/**
 * Copyright 2017 Jumio Corporation All rights reserved.
 */
public class MainActivity extends AppCompatActivity {

	public static final String KEY_SWITCH_ONE_TEXT = "KEY_SWITCH_ONE_TEXT";
	public static final String KEY_SWITCH_TWO_TEXT = "KEY_SWITCH_TWO_TEXT";
	public static final String KEY_BUTTON_TEXT = "KEY_BUTTON_TEXT";
	public static final String KEY_API_TOKEN = "KEY_API_TOKEN";
	public static final String KEY_API_SECRET = "KEY_API_SECRET";

	/* PUT YOUR NETVERIFY API TOKEN AND SECRET HERE */
	private static String NETVERIFY_API_TOKEN = "";
	private static String NETVERIFY_API_SECRET = "";

	/* PUT YOUR BAM API TOKEN AND SECRET HERE */
	private static String BAM_API_TOKEN = "";
	private static String BAM_API_SECRET = "";

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(mViewPager);

	}

	/**
	 * Check and request missing permissions for the SDK.
	 *
	 * @param sdk         the SDK instance to check
	 * @param requestCode the request code for the SDK
	 */
	public void checkPermissionsAndStart(MobileSDK sdk, int requestCode) {
		if (!MobileSDK.hasAllRequiredPermissions(this)) {
			//Acquire missing permissions.
			String[] mp = MobileSDK.getMissingPermissions(this);

			ActivityCompat.requestPermissions(this, mp, requestCode);
			//The result is received in onRequestPermissionsResult.
		} else {
			startSdk(sdk);
		}
	}

	private void startSdk(MobileSDK sdk) {
		try {
			sdk.start();
		} catch (MissingPermissionException e) {
			Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		//Forward the results to the fragments
		if (requestCode == NetverifySDK.REQUEST_CODE) {
			Fragment netverifyFragment = mSectionsPagerAdapter.getItem(0);
			if (netverifyFragment != null) {
				netverifyFragment.onActivityResult(requestCode, resultCode, intent);
			}
//			Fragment documentVerificationFragment = mSectionsPagerAdapter.getItem(1);
//			if (documentVerificationFragment != null) {
//				documentVerificationFragment.onActivityResult(requestCode, resultCode, intent);
//			}
		} else if (requestCode == BamSDK.REQUEST_CODE) {
			Fragment bamFragment = mSectionsPagerAdapter.getItem(2);
			if (bamFragment != null) {
				bamFragment.onActivityResult(requestCode, resultCode, intent);
			}
			Fragment bamCustomFragment = mSectionsPagerAdapter.getItem(3);
			if (bamCustomFragment != null) {
				bamCustomFragment.onActivityResult(requestCode, resultCode, intent);
			}
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			Bundle bundle = new Bundle();

			switch (position) {
				case 0:
					NetverifyFragment nvFragment = new NetverifyFragment();
					bundle.putString(KEY_SWITCH_ONE_TEXT, getResources().getString(R.string.netverify_verification_required));
					bundle.putString(KEY_SWITCH_TWO_TEXT, getResources().getString(R.string.netverify_face_required));
					bundle.putString(KEY_BUTTON_TEXT, String.format(getResources().getString(R.string.button_start), getResources().getString(R.string.section_netverify)));
					bundle.putString(KEY_API_TOKEN, NETVERIFY_API_TOKEN);
					bundle.putString(KEY_API_SECRET, NETVERIFY_API_SECRET);
					nvFragment.setArguments(bundle);
					return nvFragment;
				case 1:
					DocumentVerificationFragment documentVerificationFragment = new DocumentVerificationFragment();
					bundle.putString(KEY_BUTTON_TEXT, String.format(getResources().getString(R.string.button_start), getResources().getString(R.string.section_documentverification)));
					bundle.putString(KEY_API_TOKEN, NETVERIFY_API_TOKEN);
					bundle.putString(KEY_API_SECRET, NETVERIFY_API_SECRET);
					documentVerificationFragment.setArguments(bundle);
					return documentVerificationFragment;
				case 2:
					BamFragment bamFragment = new BamFragment();
					bundle.putString(KEY_SWITCH_ONE_TEXT, getResources().getString(R.string.bam_expiry_required));
					bundle.putString(KEY_SWITCH_TWO_TEXT, getResources().getString(R.string.bam_cvv_required));
					bundle.putString(KEY_BUTTON_TEXT, String.format(getResources().getString(R.string.button_start), getResources().getString(R.string.section_bamcheckout)));
					bundle.putString(KEY_API_TOKEN, BAM_API_TOKEN);
					bundle.putString(KEY_API_SECRET, BAM_API_SECRET);
					bamFragment.setArguments(bundle);
					return bamFragment;
				case 3:
					BamCustomFragment bamCustomFragment = new BamCustomFragment();
					bundle.putString(KEY_BUTTON_TEXT, String.format(getResources().getString(R.string.button_start), getResources().getString(R.string.section_bamcheckout)));
					bundle.putString(KEY_API_TOKEN, BAM_API_TOKEN);
					bundle.putString(KEY_API_SECRET, BAM_API_SECRET);
					bamCustomFragment.setArguments(bundle);
					return bamCustomFragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return getResources().getString(R.string.section_netverify);
				case 1:
					return getResources().getString(R.string.section_documentverification);
				case 2:
					return getResources().getString(R.string.section_bamcheckout);
				case 3:
					return getResources().getString(R.string.section_bam_custom);
			}
			return null;
		}
	}
}
