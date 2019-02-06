package co.etornam.familytracker.dialogFragment;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.etornam.familytracker.R;
import de.hdodenhof.circleimageview.CircleImageView;
import gun0912.tedbottompicker.TedBottomPicker;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactDialogFragment extends BottomSheetDialogFragment implements AdapterView.OnItemSelectedListener {


	private final int PICK_CONTACT = 11999;
	@BindView(R.id.spinnerRelation)
	Spinner spinnerRelation;
	@BindView(R.id.imgContactProfile)
	CircleImageView imgContactProfile;
	@BindView(R.id.txtSelectPhoto)
	TextView txtSelectPhoto;
	@BindView(R.id.edtContactName)
	TextInputEditText edtContactName;
	@BindView(R.id.edtContactNumber)
	TextInputEditText edtContactNumber;
	@BindView(R.id.imgContactSelector)
	ImageButton imgContactSelector;
	@BindView(R.id.btnSaveContact)
	Button btnSaveContact;
	private String TAG = ContactDialogFragment.class.getSimpleName();
	private String name = null;
	private String number = null;
	private Uri resultUri = null;
	private String photo = null;

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

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),
				R.array.relation_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerRelation.setAdapter(adapter);
	}

	@OnClick({R.id.imgContactProfile, R.id.txtSelectPhoto, R.id.imgContactSelector, R.id.btnSaveContact})
	void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.imgContactProfile:
				pickImage();
				break;
			case R.id.txtSelectPhoto:
				pickImage();
				break;
			case R.id.imgContactSelector:
				selectContact();
				break;
			case R.id.btnSaveContact:
				validateData();
				break;
		}
	}


	private void validateData() {
		String contactName = edtContactName.getText().toString();
		String contactNumber = edtContactNumber.getText().toString();

		if (!contactName.isEmpty() && !contactNumber.isEmpty() && resultUri != null) {

		} else if (contactName.isEmpty()) {
			edtContactName.setError(getResources().getString(R.string.error_msg));
		} else if (contactNumber.isEmpty()) {
			edtContactNumber.setError(getResources().getString(R.string.error_msg));
		} else if (resultUri == null) {
			txtSelectPhoto.setError(getResources().getString(R.string.error_msg_photo));
		}
	}

	private void pickImage() {
		TedBottomPicker picker = new TedBottomPicker.Builder(Objects.requireNonNull(getContext()))
				.setOnImageSelectedListener(uri -> {
					resultUri = uri;
					Picasso.get()
							.load(resultUri)
							.placeholder(R.drawable.ic_person)
							.error(R.drawable.img_error)
							.into(imgContactProfile);
				}).create();
		assert getFragmentManager() != null;
		picker.show(getFragmentManager());
	}

	private void selectContact() {
		Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
		startActivityForResult(i, PICK_CONTACT);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
			Uri contactUri = data.getData();

			assert contactUri != null;
			try (Cursor cursor = Objects.requireNonNull(getContext()).getContentResolver().query(contactUri, null, null, null, null)) {
				assert cursor != null;
				cursor.moveToFirst();
				int numberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
				int nameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
				int picColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
				name = cursor.getString(nameColumn);
				number = cursor.getString(numberColumn);
				photo = cursor.getString(picColumn);
				edtContactName.setText(name);
				edtContactNumber.setText(number);
				Picasso.get()
						.load(photo)
						.placeholder(R.drawable.ic_person)
						.error(R.drawable.img_error)
						.into(imgContactProfile);
			} catch (Exception e) {
				Log.d(TAG, "onActivityResult: " + e.getMessage());
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "onItemSelected: " + parent.getItemAtPosition(position));
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}
