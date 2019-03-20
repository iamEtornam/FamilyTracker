package co.etornam.familytracker.dialogFragment;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.etornam.familytracker.R;
import co.etornam.familytracker.model.Contact;
import de.hdodenhof.circleimageview.CircleImageView;
import gun0912.tedbottompicker.TedBottomPicker;

import static android.app.Activity.RESULT_OK;
import static androidx.browser.customtabs.CustomTabsIntent.KEY_ID;
import static co.etornam.familytracker.util.Constants.CONTACT_DB;
import static co.etornam.familytracker.util.NetworkUtil.isNetworkAvailable;
import static co.etornam.familytracker.util.RandomStringGenerator.getAlphaNumbericString;

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
	@BindView(R.id.fragDialogMain)
	LinearLayout fragDialogMain;
	@BindView(R.id.progressBar)
	ProgressBar progressBar;
	private String TAG = ContactDialogFragment.class.getSimpleName();
	private String name = null;
	private String number = null;
	private Uri resultUri = null;
	private String photo = null;
	private Task<Uri> urlTask;
	private Bitmap imageFile;
	private StorageReference mStorage, mImageRef;
	private DatabaseReference mDatabase;
	private FirebaseAuth mAuth;
	private String key;

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
		Bundle bundle = getArguments();

		mAuth = FirebaseAuth.getInstance();
		mStorage = FirebaseStorage.getInstance().getReference();
		mDatabase = FirebaseDatabase.getInstance().getReference();

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),
				R.array.relation_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerRelation.setAdapter(adapter);

		assert bundle != null;
		if (!bundle.isEmpty()) {
			key = bundle.getString(KEY_ID);
			mDatabase.child(CONTACT_DB).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					if (dataSnapshot.exists()) {
						Contact contact = dataSnapshot.getValue(Contact.class);
						assert contact != null;
						edtContactName.setText(contact.getName());
						edtContactNumber.setText(contact.getNumber());
						Picasso.get()
								.load(contact.getImageUrl())
								.placeholder(getResources().getDrawable(R.drawable.ic_image_placeholder))
								.error(getResources().getDrawable(R.drawable.img_error))
								.into(imgContactProfile);

						ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.blood_array, android.R.layout.simple_spinner_item);
						adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinnerRelation.setAdapter(adapter);
						if (contact.getRelation() != null) {
							int spinnerPosition = adapter.getPosition(contact.getRelation());
							spinnerRelation.setSelection(spinnerPosition);
						}
					}
				}

				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {
					Log.d(TAG, "onCancelled: " + databaseError.getMessage());
				}
			});

		}
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
				if (!isNetworkAvailable(Objects.requireNonNull(getContext()))) {
					Toast.makeText(getContext(), "No internet Connection.", Toast.LENGTH_SHORT).show();
				} else {
					validateData();
				}
				break;
		}
	}


	private void validateData() {
		if (!edtContactName.getText().toString().isEmpty() && !edtContactNumber.getText().toString().isEmpty() && resultUri != null && spinnerRelation != null) {
			saveContactDetails();
		} else if (edtContactName.getText().toString().isEmpty()) {
			edtContactName.setError(getResources().getString(R.string.error_msg));
		} else if (edtContactNumber.getText().toString().isEmpty()) {
			edtContactNumber.setError(getResources().getString(R.string.error_msg));
		} else if (resultUri == null) {
			txtSelectPhoto.setError(getResources().getString(R.string.error_msg_photo));
		}
	}

	private void saveContactDetails() {
		progressBar.setVisibility(View.VISIBLE);
		String name = edtContactName.getText().toString();
		String contact = edtContactNumber.getText().toString();
		String relation = spinnerRelation.getSelectedItem().toString();

		int size = Objects.requireNonNull(mAuth.getCurrentUser()).getUid().length();

		mImageRef = mStorage.child("contact_images").child(getAlphaNumbericString(size) + ".jpg");
		UploadTask uploadTask = mImageRef.putFile(resultUri);
		urlTask = uploadTask.continueWithTask(task -> {
			if (!task.isSuccessful()) {
				throw Objects.requireNonNull(task.getException());
			}
			return mImageRef.getDownloadUrl();
		}).addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				final Uri downloadUrl = task.getResult();
				assert downloadUrl != null;

				writeContactDetails(downloadUrl.toString(), name, contact, relation);
			} else {
				Snackbar.make(Objects.requireNonNull(getView()).findViewById(R.id.fragDialogMain), "Couldn't upload Profile Photo. ", Snackbar.LENGTH_SHORT).show();
			}
			progressBar.setVisibility(View.GONE);
		});
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
		Log.d(TAG, "onItemSelected: " + parent.getItemAtPosition(position).toString());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	private void writeContactDetails(String imageUrl, String name, String number, String relation) {
		Contact contact = new Contact(imageUrl, name, number, relation, ServerValue.TIMESTAMP);
		mDatabase.child(CONTACT_DB).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).push().setValue(contact).addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				Snackbar.make(Objects.requireNonNull(getView()).findViewById(R.id.fragDialogMain), "Contact Saved!!! ", Snackbar.LENGTH_SHORT).show();
				this.dismiss();
			} else {
				Snackbar.make(Objects.requireNonNull(getView()).findViewById(R.id.fragDialogMain), "Couldn't Save Contact ", Snackbar.LENGTH_SHORT).show();
			}
		});
	}
}
