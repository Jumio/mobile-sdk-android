package com.jumio.sample.java.netverify.customui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jumio.nv.data.country.Country;
import com.jumio.nv.gui.CountryAdapter;
import com.jumio.nv.view.interactors.CountryListView;
import com.jumio.sample.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class NetverifyCustomDocSelectionFragment extends Fragment implements CountryListView, AdapterView.OnItemClickListener {

	private final static String TAG = "NvCustomDocSelection";

	private ListView listView;
	private ProgressBar loadingIndicator;
	private TextView tvVerification, tvIdentityCheck, tvHelp;
	private OnDocumentSelectionInteractionListener callback;

	/**
	 * Creates view and initializes list view with all possible countries
	 *
	 * @param inflater LayoutInflater
	 * @param container ViewGroup
	 * @param savedInstanceState Bundle
	 * @return root view
	 */
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_netverify_custom_document_selection, container, false);

		listView = root.findViewById(R.id.fragment_custom_lv_countries);
		tvVerification = root.findViewById(R.id.fragment_custom_tv_verification);
		tvHelp = root.findViewById(R.id.fragment_custom_tv_help);
		loadingIndicator = root.findViewById(R.id.fragment_nv_custom_loading_indicator);

		listView.setOnItemClickListener(this);
		listView.setFastScrollEnabled(true);
		listView.setFilterTouchesWhenObscured(true);

		setHasOptionsMenu(true);
		hideView(tvVerification, tvIdentityCheck, tvHelp);

		return root;
	}

	/**
	 * Creates action bar at the top of the screen
	 *
	 * @param menu     refers to action bar at the top
	 * @param inflater could inflate existing menu, not used here
	 */
	@Override
	public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	/**
	 * Throws error
	 * @param throwable containing error
	 */
	@Override
	public void onError(Throwable throwable) {
		Log.e(TAG, "onError: ", throwable);
	}

	/**
	 * Attaches fragment
	 */
	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		if (context instanceof OnDocumentSelectionInteractionListener) {
			callback = (OnDocumentSelectionInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
				+ " must implement OnSuccessFragmentInteractionListener");
		}
	}

	/**
	 * Sets country adapter list view adapter
	 */
	void updateListView(ArrayList<Country> countries) {
		CountryAdapter countryAdapter = new CountryAdapter(countries);
		listView.setAdapter(countryAdapter);
		countryAdapter.notifyDataSetChanged();
		showView(tvVerification, tvIdentityCheck, tvHelp, listView);
	}

	/**
	 * Make one or more views visible
	 *
	 * @param views specifies which view(s)
	 */
	private void showView(View... views) {
		loadingIndicator.setVisibility(View.GONE);
		for (View view : views)
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
	}

	/**
	 * Hide one or more views
	 *
	 * @param views       specifies which view(s)
	 */
	private void hideView(View... views) {
		for (View view : views)
			if (view != null) {
				view.setVisibility(View.GONE);
			}
		loadingIndicator.setVisibility(View.VISIBLE);
	}

	/**
	 * Handles click on listView item (in this case which country was clicked)
	 * Callback with country Iso code
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CountryAdapter.CountryViewHolder countryViewHolder = (CountryAdapter.CountryViewHolder) view.getTag();

		callback.onCountrySelected(countryViewHolder.country.getIsoCode());
	}

	/**
	 * Handles action bar items (back button)
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
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Document selection interface for returning the Iso code of selected country
	 */
	public interface OnDocumentSelectionInteractionListener {
		void onCountrySelected(String isoCode);
	}
}
