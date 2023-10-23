package com.jumio.sample.java.netverify.customui;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.jumio.commons.utils.ScreenUtil;
import com.jumio.nv.data.country.Country;
import com.jumio.sample.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class NetverifyCustomDocSelectionFragment extends Fragment implements AdapterView.OnItemClickListener {

	private static final String INSTANCE_COUNTRY_LIST = "country_list";

	private ListView listView;
	private ProgressBar loadingIndicator;
	private TextView tvVerification, tvHelp;
	private OnDocumentSelectionInteractionListener callback;
	private ArrayList<Country> countries;

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
		hideView(tvVerification, tvHelp);

		return root;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if(savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_COUNTRY_LIST)) {
			updateListView((ArrayList<Country>) savedInstanceState.getSerializable(INSTANCE_COUNTRY_LIST));
		}
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putSerializable(INSTANCE_COUNTRY_LIST, countries);
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
		if(countries == null) {
			return;
		}
		this.countries = countries;
		listView.setAdapter(new CountryAdapter(countries));
		showView(tvVerification, tvHelp, listView);
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

	/**
	 * Custom adapter for the country list
	 */
	public static class CountryAdapter extends BaseAdapter implements SectionIndexer, ListAdapter {

		public static final int TYPE_HEADER = 0;
		public static final int TYPE_ITEM = 1;

		private SparseArray<String> sections;
		private final List<Country> countries;

		public static class CountryViewHolder {
			public Country country;
			public TextView name;
		}

		public CountryAdapter(List<Country> countries) {

			this.countries = countries;
			initFastScroll();
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return countries.size() + sections.size();
		}

		@Override
		public Object getItem(int position) {
			return countries.get(position - getSectionForPosition(position) - 1);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CountryAdapter.CountryViewHolder holder = null;

			int viewType = getItemViewType(position);

			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.netverify_custom_document_selection_countrylist_row, parent, false);
				holder = new CountryViewHolder();
				holder.name = (TextView) convertView.findViewById(R.id.countryNameTextView);
				convertView.setTag(holder);
			} else {
				holder = (CountryAdapter.CountryViewHolder) convertView.getTag();
			}

			if (viewType == TYPE_HEADER) {
				//holder.name.setPadding(0, dp10 / 2, dp10, dp10 / 2);
				holder.name.getLayoutParams().height = (int) ScreenUtil.dipToPx(parent.getContext(), 28);
				holder.name.setTypeface(Typeface.DEFAULT_BOLD);
				holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				holder.name.setText(sections.valueAt(getSectionForPosition(position)));
				holder.country = null;
				convertView.setContentDescription(sections.valueAt(getSectionForPosition(position)));
			} else {
				Country country = (Country) getItem(position);
				if (!TextUtils.isEmpty(country.getName())) {
					convertView.setContentDescription(country.getName());
				} else {
					convertView.setContentDescription(country.getIsoCode());
				}

				//holder.name.setPadding(0, dp10, dp10, dp10);
				holder.name.getLayoutParams().height = (int) ScreenUtil.dipToPx(parent.getContext(), 56);
				holder.name.setTypeface(Typeface.DEFAULT);
				holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				holder.name.setText(country.getName());
				holder.country = country;
			}

			return convertView;
		}

		@Override
		public Object[] getSections() {
			String[] sectionArray = new String[sections.size()];
			for (int i = 0; i < sections.size(); i++) {
				sectionArray[i] = sections.valueAt(i);
			}
			return sectionArray;
		}

		@Override
		public int getPositionForSection(int sectionIndex) {
			if (sectionIndex < 0)
				sectionIndex = 0;

			if (sectionIndex > sections.size() - 1)
				sectionIndex = sections.size() - 1;

			return sections.keyAt(sectionIndex);
		}

		@Override
		public int getSectionForPosition(int position) {
			for (int i = 0; i < sections.size(); i++) {
				int start = sections.keyAt(i);
				int end = (i + 1 < sections.size() ? sections.keyAt(i + 1) : getCount());
				if (position >= start && position < end)
					return i;
			}
			return -1;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			return sections.indexOfKey(position) < 0 ? TYPE_ITEM : TYPE_HEADER;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return getItemViewType(position) != TYPE_HEADER;
		}

		private void initFastScroll() {
			int size = countries.size();
			sections = new SparseArray<String>();
			for (int i = 0; i < size; i++) {
				String element = countries.get(i).getName().substring(0, 1);
				if (element.compareToIgnoreCase("Ä") == 0)
					element = "A";
				else if (element.compareToIgnoreCase("Å") == 0)
					element = "A";
				else if (element.compareToIgnoreCase("Ü") == 0)
					element = "U";
				else if (element.compareToIgnoreCase("Ö") == 0)
					element = "O";
				if (positionOfSection(element) < 0) {
					sections.append(i + sections.size(), element);
				}
			}
			sections.size();
		}

		private int positionOfSection(String section) {
			int position = -1;
			for (int i = 0; i < sections.size(); i++) {
				String value = sections.get(sections.keyAt(i));
				if (value.equals(section))
					position = i;
			}
			return position;
		}
	}
}
