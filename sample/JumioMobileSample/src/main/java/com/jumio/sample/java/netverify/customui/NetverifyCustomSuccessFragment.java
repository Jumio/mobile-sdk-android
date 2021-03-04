package com.jumio.sample.java.netverify.customui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jumio.sample.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class NetverifyCustomSuccessFragment extends Fragment implements View.OnClickListener {

	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_NAME = "ARG_NAME";

	private String fullName;

	private OnSuccessFragmentInteractionListener callback;

	/**
	 * Constructor with parameters
	 *
	 * @param fullName specifies name on scanned document
	 * @return fragment
	 */
	static NetverifyCustomSuccessFragment newInstance(String fullName) {
		NetverifyCustomSuccessFragment fragment = new NetverifyCustomSuccessFragment();
		Bundle args = new Bundle();
		args.putString(ARG_NAME, fullName);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			fullName = getArguments().getString(ARG_NAME);
		}
		setHasOptionsMenu(true);
	}

	/**
	 * Creates view and initializes elements, displays full name on document if that name available
	 *
	 * @param inflater LayoutInflater
	 * @param container ViewGroup
	 * @param savedInstanceState Bundle
	 * @return root view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_success, container, false);
		FloatingActionButton floatingActionButtonFinish = root.findViewById(R.id.fragment_success_fab_finish);
		floatingActionButtonFinish.setOnClickListener(this);
		if (!TextUtils.isEmpty(fullName)) {
			((TextView) root.findViewById(R.id.fragment_success_tv_name)).setText(fullName);
		} else {
			(root.findViewById(R.id.fragment_success_tv_name)).setVisibility(View.GONE);
		}
		return root;
	}

//	/**
//	 * Creates action bar at the top of the screen
//	 *
//	 * @param menu     refers to action bar at the top
//	 * @param inflater could inflate existing menu, not used here
//	 */
//	@Override
//	public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
//		// back arrow not necessary
//		super.onCreateOptionsMenu(menu, Objects.requireNonNull(inflater));
//	}

	/**
	 * Attaches fragment
	 */
	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		if (context instanceof OnSuccessFragmentInteractionListener) {
			callback = (OnSuccessFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
				+ " must implement OnSuccessFragmentInteractionListener");
		}
	}

	/**
	 * Detaches fragment
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		callback = null;
	}

	/**
	 * Handles fab button clicks
	 */
	@Override
	public void onClick(View v) {
		if (v != null && v.getId() == R.id.fragment_success_fab_finish) {
			if (callback != null) {
				callback.onFinish();
			}
		}
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
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnSuccessFragmentInteractionListener {
		void onFinish();
	}
}
