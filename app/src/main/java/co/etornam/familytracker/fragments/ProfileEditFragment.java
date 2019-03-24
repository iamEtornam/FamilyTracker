package co.etornam.familytracker.fragments;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.etornam.familytracker.R;
import co.etornam.familytracker.model.Profile;
import de.hdodenhof.circleimageview.CircleImageView;
import gun0912.tedbottompicker.TedBottomPicker;

import static co.etornam.familytracker.util.Constants.PROFILE_STORAGE;
import static co.etornam.familytracker.util.Constants.USER_DB;
import static co.etornam.familytracker.util.NetworkUtil.isNetworkAvailable;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileEditFragment extends Fragment {

	private String TAG = ProfileEditFragment.class.getSimpleName();
	@BindView(R.id.imgProfile)
	CircleImageView imgProfile;
	@BindView(R.id.txtImgSelect)
	TextView txtImgSelect;
	@BindView(R.id.edtFirstName)
	TextInputEditText edtFirstName;
	@BindView(R.id.edtOtherName)
	TextInputEditText edtOtherName;
	@BindView(R.id.rBtnMale)
	RadioButton rBtnMale;
	@BindView(R.id.rBtnFemale)
	RadioButton rBtnFemale;
	@BindView(R.id.rBtnOther)
	RadioButton rBtnOther;
	@BindView(R.id.edtDateOfBirth)
	TextInputEditText edtDateOfBirth;
	@BindView(R.id.edtHomeAddress)
	TextInputEditText edtHomeAddress;
	@BindView(R.id.edtWorkAddress)
	TextInputEditText edtWorkAddress;
	@BindView(R.id.edtMobileNumber)
	TextInputEditText edtMobileNumber;
	@BindView(R.id.progressIndicator)
	MKLoader progressIndicator;
	@BindView(R.id.btnUpdateDetail)
	MaterialButton btnUpdateDetail;
	@BindView(R.id.genderGroup)
	RadioGroup genderGroup;
	@BindView(R.id.fragMainLayout)
	ScrollView fragMainLayout;
	private Uri resultUri = null;
	private Task<Uri> urlTask;
	private FirebaseAuth mAuth;
	private Bitmap imageFile;
	private StorageReference mStorage, mImageRef;
	private DatabaseReference mDatabase;
	private String gender;
	private String firstName;
	private String otherName;
	private String dateOfBirth;
	private String homeAddress;
	private String workAddress;
	private String mobileNumber;


	public ProfileEditFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
		Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mAuth = FirebaseAuth.getInstance();
		mStorage = FirebaseStorage.getInstance().getReference();
		mDatabase = FirebaseDatabase.getInstance().getReference();

		genderGroup.setOnCheckedChangeListener((group, checkedId) -> {
			switch (checkedId) {
				case R.id.rBtnMale:
					gender = getString(R.string.male);
					break;
				case R.id.rBtnFemale:
					gender = getString(R.string.female);
					break;
				case R.id.rBtnOther:
					gender = getString(R.string.other);
					break;
			}
		});
	}

	@OnClick({R.id.imgProfile, R.id.txtImgSelect, R.id.btnUpdateDetail})
	void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.imgProfile:
				pickImage();
				break;
			case R.id.txtImgSelect:
				pickImage();
				break;
			case R.id.btnUpdateDetail:
				if (!isNetworkAvailable(Objects.requireNonNull(getContext()))) {
					Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
				} else {
					validateUserDetails();
				}

				Log.d(TAG, "onViewClicked: " + gender);
				break;
		}
	}

	private void pickImage() {
		TedBottomPicker picker = new TedBottomPicker.Builder(Objects.requireNonNull(getContext()))
				.setOnImageSelectedListener(uri -> {
					resultUri = uri;
					imgProfile.setImageURI(resultUri);
				}).create();
		assert getFragmentManager() != null;
		picker.show(getFragmentManager());
	}

	private void validateUserDetails() {
		firstName = Objects.requireNonNull(edtFirstName.getText()).toString();
		otherName = Objects.requireNonNull(edtOtherName.getText()).toString();
		dateOfBirth = Objects.requireNonNull(edtDateOfBirth.getText()).toString();
		homeAddress = Objects.requireNonNull(edtHomeAddress.getText()).toString();
		workAddress = Objects.requireNonNull(edtWorkAddress.getText()).toString();
		mobileNumber = Objects.requireNonNull(edtMobileNumber.getText()).toString();


		if (!firstName.isEmpty() && !otherName.isEmpty() && !dateOfBirth.isEmpty() && !homeAddress.isEmpty() && !workAddress.isEmpty() && !mobileNumber.isEmpty() && resultUri != null) {
			saveUserDetails();
		} else if (firstName.isEmpty()) {
			edtFirstName.setError(getResources().getString(R.string.error_msg));
		} else if (otherName.isEmpty()) {
			edtOtherName.setError(getResources().getString(R.string.error_msg));
		} else if (dateOfBirth.isEmpty()) {
			edtDateOfBirth.setError(getResources().getString(R.string.error_msg));
		} else if (homeAddress.isEmpty()) {
			edtHomeAddress.setError(getResources().getString(R.string.error_msg));
		} else if (workAddress.isEmpty()) {
			edtWorkAddress.setError(getResources().getString(R.string.error_msg));
		} else if (mobileNumber.isEmpty()) {
			edtMobileNumber.setError(getResources().getString(R.string.error_msg));
		} else if (resultUri == null) {
			txtImgSelect.setError(getString(R.string.select_photo));
		} else {
			Snackbar.make(getView().findViewById(R.id.fragMainLayout), getString(R.string.something_wrong), Snackbar.LENGTH_SHORT).show();
		}
	}

	private void saveUserDetails() {
		btnUpdateDetail.setVisibility(View.GONE);
		progressIndicator.setVisibility(View.VISIBLE);
		mImageRef = mStorage.child(PROFILE_STORAGE).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + ".jpg");
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

				writeUserDetails(firstName, otherName, dateOfBirth, homeAddress, workAddress, mobileNumber, downloadUrl.toString(), gender);
			} else {
				Snackbar.make(Objects.requireNonNull(getView()).findViewById(R.id.fragMainLayout), getString(R.string.could_not_upload), Snackbar.LENGTH_SHORT).show();
			}
			btnUpdateDetail.setVisibility(View.VISIBLE);
			progressIndicator.setVisibility(View.GONE);
		});
	}

	private void writeUserDetails(String firstName, String otherName, String dateOfBirth, String homeAddress, String workAddress, String mobileNumber, String profileImgUrl, String gender) {
		Profile profile = new Profile(firstName, otherName, dateOfBirth, homeAddress, workAddress, mobileNumber, profileImgUrl, gender, ServerValue.TIMESTAMP);
		mDatabase.child(USER_DB).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).setValue(profile).addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				Snackbar.make(Objects.requireNonNull(getView()).findViewById(R.id.fragMainLayout), getString(R.string.detail_saved), Snackbar.LENGTH_SHORT).show();
				ProfileDisplayFragment profileDisplayFragment = new ProfileDisplayFragment();
				assert getFragmentManager() != null;
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.fragContainer, profileDisplayFragment);
				transaction.commit();
			} else {
				Snackbar.make(Objects.requireNonNull(getView()).findViewById(R.id.fragMainLayout), getString(R.string.detail_not_saved), Snackbar.LENGTH_SHORT).show();
			}
		});
	}
}