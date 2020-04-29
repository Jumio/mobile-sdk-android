package com.jumio.sample.java.netverify.customui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jumio.nv.data.document.NVDocumentType;
import com.jumio.nv.data.document.NVDocumentVariant;
import com.jumio.sample.R;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetDialogDocuments extends BottomSheetDialogFragment implements View.OnClickListener {

	private List<LinearLayout> optionLayouts = new ArrayList<>();
	private List<ImageView> imageViews = new ArrayList<>();
	private List<TextView> textViews = new ArrayList<>();
	private List<NVDocumentType> documentTypes = new ArrayList<>();
	private OnBottomSheetActionListener callback;

	public interface OnBottomSheetActionListener {
		void onDocumentTypeSelected(NVDocumentType type, NVDocumentVariant variant, boolean variantSelected);
	}

	public BottomSheetDialogDocuments() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			documentTypes = new ArrayList<NVDocumentType>() ;
			ArrayList<String> stringList = bundle.getStringArrayList(NetverifyCustomActivity.BUNDLE_DOCUMENT_TYPE_LIST);
			if(stringList != null) {
				for(String s : stringList) {
					documentTypes.add(NVDocumentType.fromString(s));
				}
			}

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
		optionLayouts.add(root.findViewById(R.id.bottom_sheet_documents_option3));
		optionLayouts.add(root.findViewById(R.id.bottom_sheet_documents_option4));

		for (LinearLayout l : optionLayouts) {
			l.setOnClickListener(this);
		}

		imageViews.add(root.findViewById(R.id.bottom_sheet_documents_option1_image));
		imageViews.add(root.findViewById(R.id.bottom_sheet_documents_option2_image));
		imageViews.add(root.findViewById(R.id.bottom_sheet_documents_option3_image));
		imageViews.add(root.findViewById(R.id.bottom_sheet_documents_option4_image));

		textViews.add(root.findViewById(R.id.bottom_sheet_documents_option1_text));
		textViews.add(root.findViewById(R.id.bottom_sheet_documents_option2_text));
		textViews.add(root.findViewById(R.id.bottom_sheet_documents_option3_text));
		textViews.add(root.findViewById(R.id.bottom_sheet_documents_option4_text));

		int i = 0;
		for (NVDocumentType type : documentTypes) {
			if (i < documentTypes.size()) {
				optionLayouts.get(i).setVisibility(View.VISIBLE);
				imageViews.get(i).setImageResource(getIconForDocumentType(type));
				textViews.get(i).setText(type.getLocalizedName(getActivity()));
				i++;
			}
		}

		return root;
	}

	private int getIconForDocumentType(NVDocumentType documentType) {
		switch (documentType) {
			case DRIVER_LICENSE:
				return R.drawable.ic_selection_driver_license;
			case VISA:
				return R.drawable.ic_selection_visa;
			case PASSPORT:
				return R.drawable.ic_selection_passport;
			case IDENTITY_CARD:
				return R.drawable.ic_selection_id_card;
		}
		return 0;
	}

	@Override
	public void onClick(View v) {
		if (v == null) {
			return;
		}
		callback.onDocumentTypeSelected(documentTypes.get(optionLayouts.indexOf(v)), NVDocumentVariant.PLASTIC, false);
		dismiss();
	}
}
