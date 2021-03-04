package com.jumio.sample.kotlin.netverify.customui

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jumio.sample.R

class NetverifyCustomSuccessFragment

	/**
	 * Constructor
	 */
	: Fragment(), View.OnClickListener {
	private var fullName: String? = null
	private var callback: OnSuccessFragmentInteractionListener? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (arguments != null) {
			fullName = arguments!!.getString(ARG_NAME)
		}
		setHasOptionsMenu(true)
	}

	/**
	 * Creates view and initializes elements, displays full name on document if that name available
	 *
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return root view
	 */
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
		val root = inflater.inflate(R.layout.fragment_success, container, false)
		val floatingActionButtonFinish: FloatingActionButton = root.findViewById(R.id.fragment_success_fab_finish)
		floatingActionButtonFinish.setOnClickListener(this)
		if (!TextUtils.isEmpty(fullName)) {
			(root.findViewById<View>(R.id.fragment_success_tv_name) as TextView).text = fullName
		} else {
			root.findViewById<View>(R.id.fragment_success_tv_name).visibility = View.GONE
		}
		return root
	}

//	/**
//	 * Creates action bar at the top of the screen
//	 *
//	 * @param menu     refers to action bar at the top
//	 * @param inflater could inflate existing menu, not used here
//	 */
//	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) { // back arrow not necessary
//		(activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(false)
//		super.onCreateOptionsMenu(menu, inflater)
//	}

	/**
	 * Attaches fragment
	 */
	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = if (context is OnSuccessFragmentInteractionListener) {
			context
		} else {
			throw RuntimeException(context.toString()
					+ " must implement OnSuccessFragmentInteractionListener")
		}
	}

	/**
	 * Detaches fragment
	 */
	override fun onDetach() {
		super.onDetach()
		callback = null
	}

	/**
	 * Handles fab button clicks
	 */
	override fun onClick(v: View?) {
		if (v != null && v.id == R.id.fragment_success_fab_finish && callback != null) {
			callback?.onFinish()
		}
	}

	/**
	 * Handles action bar items (back button)
	 *
	 * @param item refers to item in menu that was clicked
	 * @return boolean
	 */
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			activity?.finish()
			return true
		}
		return super.onOptionsItemSelected(item)
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 *
	 *
	 * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
	 */
	interface OnSuccessFragmentInteractionListener {
		fun onFinish()
	}

	companion object {
		private const val TAG = "NvCustomSuccessFragment"
		// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
		private const val ARG_NAME = "ARG_NAME"

		/**
		 * Constructor with parameters
		 *
		 * @param fullName specifies name on scanned document
		 * @return fragment
		 */
		fun newInstance(fullName: String?): NetverifyCustomSuccessFragment {
			val fragment = NetverifyCustomSuccessFragment()
			val args = Bundle()
			args.putString(ARG_NAME, fullName)
			fragment.arguments = args
			return fragment
		}
	}
}