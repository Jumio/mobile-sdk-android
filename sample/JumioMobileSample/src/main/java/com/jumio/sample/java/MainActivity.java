package com.jumio.sample.java;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.jumio.MobileSDK;
import com.jumio.core.enums.JumioDataCenter;
import com.jumio.nv.NetverifySDK;
import com.jumio.sample.R;
import com.jumio.sample.java.bam.BamCustomFragment;
import com.jumio.sample.java.bam.BamFragment;
import com.jumio.sample.java.documentverification.DocumentVerificationFragment;
import com.jumio.sample.java.netverify.NetverifyFragment;
import com.jumio.sample.java.netverify.customui.NetverifyCustomActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Copyright 2019 Jumio Corporation All rights reserved.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	public static final String KEY_API_TOKEN = "KEY_API_TOKEN";
	public static final String KEY_API_SECRET = "KEY_API_SECRET";
	public static final String KEY_DATACENTER = "KEY_DATACENTER";

	/*
	 * PUT YOUR NETVERIFY API TOKEN AND SECRET HERE
	 * Do not store your credentials hardcoded within the app. Make sure to store them server-side and load your credentials during runtime.
	 */
	private static String NETVERIFY_API_TOKEN = "";
	private static String NETVERIFY_API_SECRET = "";
	private static JumioDataCenter NETVERIFY_DATACENTER = JumioDataCenter.US;

	/*
	 * PUT YOUR BAM API TOKEN AND SECRET HERE
	 * Do not store your credentials hardcoded within the app. Make sure to store them server-side and load your credentials during runtime.
	 */
	private static String BAM_API_TOKEN = "";
	private static String BAM_API_SECRET = "";
	private static JumioDataCenter BAM_DATACENTER = JumioDataCenter.US;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Required for vector drawable compat handling https://stackoverflow.com/a/37864531/1297835
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

		setContentView(R.layout.activity_main);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(drawerToggle);
		drawerToggle.syncState();

		NavigationView navigationView = findViewById(R.id.nav_view);
		if (navigationView != null) {
			Menu menu = navigationView.getMenu();
			menu.findItem(R.id.nav_sdk).setTitle(MobileSDK.getSDKVersion());
			onNavigationItemSelected(menu.findItem(R.id.nav_netverify));
			navigationView.setNavigationItemSelectedListener(this);
			navigationView.setItemIconTintList(null);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == NetverifySDK.REQUEST_CODE_NFC_DETECTED) {
			Fragment fragment = getSupportFragmentManager().getFragments().get(0);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * Check and request missing permissions for the SDK.
	 *
	 * @param requestCode the request code for the SDK
	 */
	public boolean checkPermissions(int requestCode) {
		if (!MobileSDK.hasAllRequiredPermissions(this)) {
			//Acquire missing permissions.
			String[] mp = MobileSDK.getMissingPermissions(this);

			ActivityCompat.requestPermissions(this, mp, requestCode);
			//The result is received in onRequestPermissionsResult.
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

		Bundle bundle = new Bundle();

		switch (menuItem.getItemId()) {
			case R.id.nav_netverify:
				NetverifyFragment nvFragment = new NetverifyFragment();
				bundle.putString(KEY_API_TOKEN, NETVERIFY_API_TOKEN);
				bundle.putString(KEY_API_SECRET, NETVERIFY_API_SECRET);
				bundle.putSerializable(KEY_DATACENTER, NETVERIFY_DATACENTER);
				nvFragment.setArguments(bundle);
				switchFragment(nvFragment);
				break;
			case R.id.nav_netverify_custom:
				Intent nvCustomActivity = new Intent(getApplicationContext(), NetverifyCustomActivity.class);
				bundle.putString(KEY_API_TOKEN, NETVERIFY_API_TOKEN);
				bundle.putString(KEY_API_SECRET, NETVERIFY_API_SECRET);
				bundle.putSerializable(KEY_DATACENTER, NETVERIFY_DATACENTER);
				nvCustomActivity.putExtras(bundle);
				startActivity(nvCustomActivity);
				break;
			case R.id.nav_documentverification:
				DocumentVerificationFragment dvFragment = new DocumentVerificationFragment();
				bundle.putString(KEY_API_TOKEN, NETVERIFY_API_TOKEN);
				bundle.putString(KEY_API_SECRET, NETVERIFY_API_SECRET);
				bundle.putSerializable(KEY_DATACENTER, NETVERIFY_DATACENTER);
				dvFragment.setArguments(bundle);
				switchFragment(dvFragment);
				break;
			case R.id.nav_bam:
				BamFragment bamFragment = new BamFragment();
				bundle.putString(KEY_API_TOKEN, BAM_API_TOKEN);
				bundle.putString(KEY_API_SECRET, BAM_API_SECRET);
				bundle.putSerializable(KEY_DATACENTER, BAM_DATACENTER);
				bamFragment.setArguments(bundle);
				switchFragment(bamFragment);
				break;
			case R.id.nav_bam_custom:
				BamCustomFragment bamCustomFragment = new BamCustomFragment();
				bundle.putString(KEY_API_TOKEN, BAM_API_TOKEN);
				bundle.putString(KEY_API_SECRET, BAM_API_SECRET);
				bundle.putSerializable(KEY_DATACENTER, BAM_DATACENTER);
				bamCustomFragment.setArguments(bundle);
				switchFragment(bamCustomFragment);
				break;
			case R.id.nav_terms_conditions:
				openLink("https://www.jumio.com/legal-information/privacy-policy");
				break;
			case R.id.nav_licenses:
				openLink("https://github.com/Jumio/mobile-sdk-android/tree/master/licenses");
				break;
		}

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	private void openLink(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}

	private void switchFragment(Fragment fragment) {
		FragmentManager supportFragmentManager = getSupportFragmentManager();

		FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, fragment);
		fragmentTransaction.commitAllowingStateLoss();
	}
}
