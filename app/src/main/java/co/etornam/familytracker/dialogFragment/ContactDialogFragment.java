package co.etornam.familytracker.dialogFragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.etornam.familytracker.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactDialogFragment extends BottomSheetDialogFragment {


	@BindView(R.id.imgContactProfile)
	CircleImageView imgContactProfile;
	@BindView(R.id.txtSelectPhoto)
	TextView txtSelectPhoto;
	@BindView(R.id.edtContactName)
	TextInputEditText edtContactName;
	@BindView(R.id.edtContactNumber)
	TextInputEditText edtContactNumber;
	@BindView(R.id.btnSaveContact)
	Button btnSaveContact;

	public static ContactDialogFragment newInstance() {
		return new ContactDialogFragment();
	}


	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_contact_dialog, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@OnClick({R.id.imgContactProfile, R.id.txtSelectPhoto, R.id.btnSaveContact})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.imgContactProfile:
				break;
			case R.id.txtSelectPhoto:
				break;
			case R.id.btnSaveContact:
				break;
		}
	}
}
