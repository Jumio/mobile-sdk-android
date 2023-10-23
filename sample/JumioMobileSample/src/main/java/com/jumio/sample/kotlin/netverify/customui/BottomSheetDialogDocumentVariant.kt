package com.jumio.sample.kotlin.netverify.customui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jumio.nv.data.document.NVDocumentType
import com.jumio.nv.data.document.NVDocumentVariant
import java.util.*

import com.jumio.sample.R

class BottomSheetDialogDocumentVariant : BottomSheetDialogFragment(), View.OnClickListener {
	private val optionLayouts: MutableList<LinearLayout> = ArrayList()
	private var documentType: NVDocumentType? = null
	private var callback: OnBottomSheetActionListener? = null

	interface OnBottomSheetActionListener {
		fun onDocumentTypeSelected(documentType: NVDocumentType?, documentVariant: NVDocumentVariant?, variantSelected: Boolean?)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val bundle = arguments
		if (bundle != null) {
			documentType = bundle.getSerializable(NetverifyCustomActivity.BUNDLE_DOCUMENT_TYPE) as NVDocumentType
		}

		//initialize callback
		callback = activity as OnBottomSheetActionListener?
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		val root: View = inflater.inflate(R.layout.bottom_sheet_dialog, container, false)
		optionLayouts.add(root.findViewById(R.id.bottom_sheet_documents_option1))
		optionLayouts.add(root.findViewById(R.id.bottom_sheet_documents_option2))
		for (l in optionLayouts) {
			l.setOnClickListener(this)
			l.visibility = View.VISIBLE
		}
		(root.findViewById<View>(R.id.bottom_sheet_documents_option1_text) as TextView).setText(R.string.netverify_document_variant_plastic)
		(root.findViewById<View>(R.id.bottom_sheet_documents_option2_text) as TextView).setText(R.string.netverify_document_variant_other)
		return root
	}

	override fun onClick(v: View?) {
		if (v != null) {
			when (v.id) {
				R.id.bottom_sheet_documents_option1 -> callback!!.onDocumentTypeSelected(documentType, NVDocumentVariant.PLASTIC, true)
				R.id.bottom_sheet_documents_option2 -> callback!!.onDocumentTypeSelected(documentType, NVDocumentVariant.PAPER, true)
			}
		}
		dismiss()
	}
}
