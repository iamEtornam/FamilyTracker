package co.etornam.familytracker.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.etornam.familytracker.R;
import co.etornam.familytracker.model.Health;
import co.etornam.familytracker.model.Profile;
import de.hdodenhof.circleimageview.CircleImageView;

import static co.etornam.familytracker.util.Constants.HEALTH_DB;
import static co.etornam.familytracker.util.Constants.USER_DB;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileDisplayFragment extends Fragment {

	@BindView(R.id.txtBloodGroup)
	TextView txtBloodGroup;
	@BindView(R.id.imgUserProfile)
	CircleImageView imgUserProfile;
	@BindView(R.id.txtUserName)
	TextView txtUserName;
	@BindView(R.id.txtUserGender)
	TextView txtUserGender;
	@BindView(R.id.txtUserDob)
	TextView txtUserDob;
	@BindView(R.id.txtUserHomeAddress)
	TextView txtUserHomeAddress;
	@BindView(R.id.txtUserWorkAddress)
	TextView txtUserWorkAddress;
	@BindView(R.id.txtUserMobileNumber)
	TextView txtUserMobileNumber;
	@BindView(R.id.divider)
	View divider;
	@BindView(R.id.fragDisplayMain)
	ScrollView fragDisplayMain;
	@BindView(R.id.txtBloodPressure)
	TextView txtBloodPressure;
	@BindView(R.id.txtAllergies)
	TextView txtAllergies;
	@BindView(R.id.txtMedication)
	TextView txtMedication;
	@BindView(R.id.txtDonor)
	TextView txtDonor;
	@BindView(R.id.txtDiabetic)
	TextView txtDiabetic;
	@BindView(R.id.displayLayout)
	LinearLayout displayLayout;
	private String TAG = ProfileDisplayFragment.class.getSimpleName();
	private DatabaseReference mDatabase;
	private FirebaseAuth mAuth;

	public ProfileDisplayFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile_display, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mAuth = FirebaseAuth.getInstance();
		mDatabase = FirebaseDatabase.getInstance().getReference();
		getUserDetails();
	}

	private void getUserDetails() {
		mDatabase.child(USER_DB).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				Profile profile = dataSnapshot.getValue(Profile.class);
				assert profile != null;
				String fullName = profile.getFirstName() + " " + profile.getOtherName();
				txtUserName.setText(fullName);
				txtUserDob.setText(profile.getDateOfBirth());
				txtUserHomeAddress.setText(profile.getHomeAddress());
				txtUserWorkAddress.setText(profile.getWorkAddress());
				txtUserGender.setText(profile.getGender());
				txtUserMobileNumber.setText(profile.getMobileNumber());
				Picasso.get()
						.load(profile.getProfileImgUrl())
						.placeholder(R.drawable.ic_person)
						.error(R.drawable.ic_image_placeholder)
						.into(imgUserProfile);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Snackbar.make(Objects.requireNonNull(getView()).findViewById(R.id.fragDisplayMain), "Couldn't Retrieve your Details ", Snackbar.LENGTH_SHORT)
						.setActionTextColor(getResources().getColor(R.color.colorRed))
						.setAction("Try Again!", v -> getUserDetails())
						.show();
			}
		});

		mDatabase.child(HEALTH_DB).child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				Health health = dataSnapshot.getValue(Health.class);
				assert health != null;
				txtDiabetic.setText(health.getDiabetic());
				txtDonor.setText(health.getDonor());

			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.d(TAG, "onCancelled: " + databaseError.getMessage());
				Snackbar.make(Objects.requireNonNull(getView()).findViewById(R.id.fragDisplayMain), "Couldn't Retrieve your Details ", Snackbar.LENGTH_SHORT)
						.setActionTextColor(getResources().getColor(R.color.colorRed))
						.setAction("Try Again!", v -> getUserDetails())
						.show();
			}
		});
	}
}
