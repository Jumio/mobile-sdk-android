package com.jumio.sample.kotlin.netverify.customui

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.util.SparseArray
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment
import com.jumio.commons.utils.ScreenUtil
import com.jumio.nv.data.country.Country
import com.jumio.sample.R
import java.util.*

/**
 * A placeholder fragment containing a simple view.
 */
class NetverifyCustomDocSelectionFragment : Fragment(), OnItemClickListener {
	private var listView: ListView? = null
	private var loadingIndicator: ProgressBar? = null
	private var tvVerification: TextView? = null
	private var tvHelp: TextView? = null
	private var callback: OnDocumentSelectionInteractionListener? = null
	private var countries: ArrayList<Country>? = null
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

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_COUNTRY_LIST)) {
			updateListView(savedInstanceState.getSerializable(INSTANCE_COUNTRY_LIST) as ArrayList<Country>)
		}
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putSerializable(INSTANCE_COUNTRY_LIST, countries)
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
		if(countries == null) {
			return;
		}
		this.countries = countries;
		val countryAdapter = CountryAdapter(countries)
		listView!!.adapter = countryAdapter
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
		val countryViewHolder = view.tag as CountryAdapter.CountryViewHolder
		callback!!.onCountrySelected(countryViewHolder.country?.isoCode)
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

	/**
	 * Custom adapter for the country list
	 */
	class CountryAdapter(private val countries: List<Country>) : BaseAdapter(), SectionIndexer, ListAdapter {
		private var sections: SparseArray<String> = SparseArray()

		class CountryViewHolder {
			var country: Country? = null
			var name: TextView? = null
		}

		override fun getCount(): Int {
			return countries.size + sections.size()
		}

		override fun getItem(position: Int): Any {
			return countries[position - getSectionForPosition(position) - 1]
		}

		override fun getItemId(position: Int): Long {
			return position.toLong()
		}

		override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
			val layout:View
			var holder: CountryViewHolder? = null
			val viewType = getItemViewType(position)
			if (convertView == null) {
				layout = LayoutInflater.from(parent.context).inflate(R.layout.netverify_custom_document_selection_countrylist_row, parent, false)
				holder = CountryViewHolder()
				holder.name = layout.findViewById<View>(R.id.countryNameTextView) as TextView
				layout.tag = holder
			} else {
				layout = convertView
				holder = layout.tag as CountryViewHolder
			}
			if (viewType == TYPE_HEADER) {
				//holder.name.setPadding(0, dp10 / 2, dp10, dp10 / 2);
				holder.name!!.layoutParams.height = ScreenUtil.dipToPx(parent.context, 28f).toInt()
				holder.name!!.typeface = Typeface.DEFAULT_BOLD
				holder.name!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
				holder.name!!.text = sections.valueAt(getSectionForPosition(position))
				holder.country = null
				layout.contentDescription = sections.valueAt(getSectionForPosition(position))
			} else {
				val country = getItem(position) as Country
				if (!TextUtils.isEmpty(country.name)) {
					layout.contentDescription = country.name
				} else {
					layout.contentDescription = country.isoCode
				}

				//holder.name.setPadding(0, dp10, dp10, dp10);
				holder.name!!.layoutParams.height = ScreenUtil.dipToPx(parent.context, 56f).toInt()
				holder.name!!.typeface = Typeface.DEFAULT
				holder.name!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
				holder.name!!.text = country.name
				holder.country = country
			}
			return layout
		}

		override fun getSections(): Array<String> {
			val sectionArray = Array<String>(sections.size()) { index -> sections.valueAt(index)}
			return sectionArray
		}

		override fun getPositionForSection(sectionIndex: Int): Int {
			var sectionIndex = sectionIndex
			if (sectionIndex < 0) sectionIndex = 0
			if (sectionIndex > sections.size() - 1) sectionIndex = sections.size() - 1
			return sections.keyAt(sectionIndex)
		}

		override fun getSectionForPosition(position: Int): Int {
			for (i in 0 until sections.size()) {
				val start = sections.keyAt(i)
				val end = if (i + 1 < sections.size()) sections.keyAt(i + 1) else count
				if (position >= start && position < end) return i
			}
			return -1
		}

		override fun getViewTypeCount(): Int {
			return 2
		}

		override fun getItemViewType(position: Int): Int {
			return if (sections.indexOfKey(position) < 0) TYPE_ITEM else TYPE_HEADER
		}

		override fun areAllItemsEnabled(): Boolean {
			return false
		}

		override fun isEnabled(position: Int): Boolean {
			return getItemViewType(position) != TYPE_HEADER
		}

		private fun initFastScroll() {
			val size = countries.size
			sections.clear()
			for (i in 0 until size) {
				var element = countries[i].name.substring(0, 1)
				if (element.compareTo("Ä", ignoreCase = true) == 0) element = "A" else if (element.compareTo("Å", ignoreCase = true) == 0) element = "A" else if (element.compareTo("Ü", ignoreCase = true) == 0) element = "U" else if (element.compareTo("Ö", ignoreCase = true) == 0) element = "O"
				if (positionOfSection(element) < 0) {
					sections.append(i + sections.size(), element)
				}
			}
			sections.size()
		}

		private fun positionOfSection(section: String): Int {
			var position = -1
			for (i in 0 until sections.size()) {
				val value = sections[sections.keyAt(i)]
				if (value == section) position = i
			}
			return position
		}

		companion object {
			const val TYPE_HEADER = 0
			const val TYPE_ITEM = 1
		}

		init {
			initFastScroll()
			notifyDataSetChanged()
		}
	}

	companion object {
		private const val INSTANCE_COUNTRY_LIST = "country_list";
	}
}