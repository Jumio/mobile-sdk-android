package com.jumio.sample.java.netverify.customui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jumio.nv.data.document.NVDocumentType;
import com.jumio.nv.data.document.NVDocumentVariant;
import com.jumio.sample.R;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetDialogDocumentVariant extends BottomSheetDialogFragment implements View.OnClickListener {

	private List<LinearLayout> optionLayouts = new ArrayList<>();
	private NVDocumentType documentType;
	private OnBottomSheetActionListener callback;

	public interface OnBottomSheetActionListener {
		void onDocumentTypeSelected(NVDocumentType type, NVDocumentVariant variant, boolean variantSelected);
	}

	public BottomSheetDialogDocumentVariant() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			documentType = (NVDocumentType) bundle.getSerializable(NetverifyCustomActivity.BUNDLE_DOCUMENT_TYPE);
		}

		//initialize callback
		callback = (OnBottomSheetActionListener) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.bottom_sheet_dialog, container, false);

		optionLayouts.add(root.findViewById(R.id.bottom_sheet_documents_option1));
		optionLayouts.add(root.findViewById(R.id.bottom_sheet_documents_option2));

		for (LinearLayout l : optionLayouts) {
			l.setOnClickListener(this);
			l.setVisibility(View.VISIBLE);
		}

		((TextView) root.findViewById(R.id.bottom_sheet_documents_option1_text)).setText(R.string.netverify_document_variant_plastic);
		((TextView) root.findViewById(R.id.bottom_sheet_documents_option2_text)).setText(R.string.netverify_document_variant_other);

		return root;
	}

	@Override
	public void onClick(View v) {
		if(v!= null) {
			switch (v.getId()) {
				case R.id.bottom_sheet_documents_option1:
					callback.onDocumentTypeSelected(documentType, NVDocumentVariant.PLASTIC, true);
					break;
				case R.id.bottom_sheet_documents_option2:
					callback.onDocumentTypeSelected(documentType, NVDocumentVariant.PAPER, true);
					break;
				default:
					break;
			}
		}

		dismiss();
	}
}
