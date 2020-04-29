package com.jumio.sample.kotlin.netverify.customui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jumio.nv.data.document.NVDocumentType
import com.jumio.nv.data.document.NVDocumentVariant
import com.jumio.sample.R
import java.util.*

class BottomSheetDialogDocuments : BottomSheetDialogFragment(), View.OnClickListener {
	private val optionLayouts: MutableList<LinearLayout> = ArrayList()
	private val imageViews: MutableList<ImageView> = ArrayList()
	private val textViews: MutableList<TextView> = ArrayList()
	private var documentTypes: List<NVDocumentType>? = ArrayList()
	private var callback: OnBottomSheetActionListener? = null

	interface OnBottomSheetActionListener {
		fun onDocumentTypeSelected(documentType: NVDocumentType?, documentVariant: NVDocumentVariant?, variantSelected: Boolean?)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val bundle = arguments
		if (bundle != null) {
			documentTypes = bundle.getStringArrayList(NetverifyCustomActivity.BUNDLE_DOCUMENT_TYPE_LIST)?.map { NVDocumentType.fromString(it) }
		}
		//initialize callback
		callback = activity as OnBottomSheetActionListener?
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		val root = inflater.inflate(R.layout.bottom_sheet_dialog, container, false)
		optionLayouts.add(root.findViewById(R.id.bottom_sheet_documents_option1))
		optionLayouts.add(root.findViewById(R.id.bottom_sheet_documents_option2))
		optionLayouts.add(root.findViewById(R.id.bottom_sheet_documents_option3))
		optionLayouts.add(root.findViewById(R.id.bottom_sheet_documents_option4))
		for (l in optionLayouts) {
			l.visibility = View.GONE
			l.setOnClickListener(this)
		}
		imageViews.add(root.findViewById(R.id.bottom_sheet_documents_option1_image))
		imageViews.add(root.findViewById(R.id.bottom_sheet_documents_option2_image))
		imageViews.add(root.findViewById(R.id.bottom_sheet_documents_option3_image))
		imageViews.add(root.findViewById(R.id.bottom_sheet_documents_option4_image))
		textViews.add(root.findViewById(R.id.bottom_sheet_documents_option1_text))
		textViews.add(root.findViewById(R.id.bottom_sheet_documents_option2_text))
		textViews.add(root.findViewById(R.id.bottom_sheet_documents_option3_text))
		textViews.add(root.findViewById(R.id.bottom_sheet_documents_option4_text))
		var i = 0
		for (type in documentTypes!!) {
			if (i < documentTypes!!.size) {
				optionLayouts[i].visibility = View.VISIBLE
				imageViews[i].setImageResource(getIconForDocumentType(type))
				textViews[i].text = type.getLocalizedName(activity)
				i++
			}
		}
		return root
	}

	private fun getIconForDocumentType(documentType: NVDocumentType?): Int {
		return when (documentType) {
			NVDocumentType.DRIVER_LICENSE -> R.drawable.ic_selection_driver_license
			NVDocumentType.VISA -> R.drawable.ic_selection_visa
			NVDocumentType.PASSPORT -> R.drawable.ic_selection_passport
			NVDocumentType.IDENTITY_CARD -> R.drawable.ic_selection_id_card
			else -> return 0
		}
	}

	override fun onClick(v: View?) {
		if (v == null) {
			return
		}
		callback!!.onDocumentTypeSelected(documentTypes!![optionLayouts.indexOf(v)], NVDocumentVariant.PLASTIC, false)
		dismiss()
	}
}