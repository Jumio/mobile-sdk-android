package com.jumio.sample.kotlin.netverify.customui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jumio.nv.data.country.Country
import com.jumio.nv.gui.CountryAdapter
import com.jumio.nv.gui.CountryAdapter.CountryViewHolder
import com.jumio.nv.view.interactors.CountryListView
import com.jumio.sample.R
import java.util.*

/**
 * A placeholder fragment containing a simple view.
 */
class NetverifyCustomDocSelectionFragment : Fragment(), CountryListView, OnItemClickListener {
	private var listView: ListView? = null
	private var loadingIndicator: ProgressBar? = null
	private var tvVerification: TextView? = null
	private var tvHelp: TextView? = null
	private var callback: OnDocumentSelectionInteractionListener? = null
	/**
	 * Creates view and initializes list view with all possible countries
	 *
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return root view
	 */
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		val root = inflater.inflate(R.layout.fragment_netverify_custom_document_selection, container, false)
		listView = root.findViewById(R.id.fragment_custom_lv_countries)
		tvVerification = root.findViewById(R.id.fragment_custom_tv_verification)
		tvHelp = root.findViewById(R.id.fragment_custom_tv_help)
		loadingIndicator = root.findViewById(R.id.fragment_nv_custom_loading_indicator)
		listView?.onItemClickListener = this
		listView?.isFastScrollEnabled = true
		listView?.filterTouchesWhenObscured = true
		setHasOptionsMenu(true)
		hideView(true, tvVerification, tvHelp)
		return root
	}

	/**
	 * Throws error
	 * @param throwable containing error
	 */
	override fun onError(throwable: Throwable) {
		Log.e(TAG, "onError: ", throwable)
	}

	/**
	 * Attaches fragment
	 */
	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = if (context is OnDocumentSelectionInteractionListener) {
			context
		} else {
			throw RuntimeException(context.toString()
					+ " must implement OnSuccessFragmentInteractionListener")
		}
	}

	/**
	 * Sets country adapter list view adapter
	 */
	fun updateListView(countries: ArrayList<Country>) {
		val countryAdapter = CountryAdapter(countries)
		listView!!.adapter = countryAdapter
		countryAdapter.notifyDataSetChanged()
		showView(true, tvVerification!!, tvHelp!!, listView!!)
	}

	/**
	 * Make one or more views visible
	 *
	 * @param views specifies which view(s)
	 */
	private fun showView(hideLoading: Boolean, vararg views: View?) {
		if (hideLoading) loadingIndicator!!.visibility = View.GONE
		for (view in views) if (view != null) {
			view.visibility = View.VISIBLE
		}
	}

	/**
	 * Hide one or more views
	 *
	 * @param showLoading specifies if loading screen should be displayed while views are hidden
	 * @param views       specifies which view(s)
	 */
	private fun hideView(showLoading: Boolean, vararg views: View?) {
		for (view in views) if (view != null) {
			view.visibility = View.GONE
		}
		if (showLoading) loadingIndicator!!.visibility = View.VISIBLE
	}

	/**
	 * Handles click on listView item (in this case which country was clicked)
	 * Callback with country Iso code
	 */
	override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
		val countryViewHolder = view.tag as CountryViewHolder
		callback!!.onCountrySelected(countryViewHolder.country.isoCode)
	}

	/**
	 * Handles action bar items (back button)
	 *
	 * @param item refers to item in menu that was clicked
	 * @return boolean
	 */
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			activity!!.finish()
			return true
		}
		return super.onOptionsItemSelected(item)
	}

	/**
	 * Document selection interface for returning the Iso code of selected country
	 */
	interface OnDocumentSelectionInteractionListener {
		fun onCountrySelected(isoCode: String?)
	}

	companion object {
		private const val TAG = "NvCustomDocSelection"
	}
}