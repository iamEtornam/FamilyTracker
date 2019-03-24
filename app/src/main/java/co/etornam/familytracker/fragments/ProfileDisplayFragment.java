package co.etornam.familytracker.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.etornam.familytracker.R;
import co.etornam.familytracker.model.Profile;
import co.etornam.familytracker.security.SignUpActivity;
import co.etornam.familytracker.ui.DisplayHealthInfoActivity;
import co.etornam.familytracker.ui.ProfileActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import static co.etornam.familytracker.util.Constants.USER_DB;
import static co.etornam.familytracker.util.NetworkUtil.isNetworkAvailable;
import static co.etornam.familytracker.util.PrefUtil.setSharedPProfileForWidget;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileDisplayFragment extends Fragment {


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
	@BindView(R.id.fragDisplayMain)
	ScrollView fragDisplayMain;
	@BindView(R.id.displayLayout)
	LinearLayout displayLayout;
	@BindView(R.id.btnLogout)
	Button btnLogout;
	@BindView(R.id.btnHealth)
	ImageButton btnHealth;
	@BindView(R.id.btnEdit)
	ImageButton btnEdit;

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
				if (dataSnapshot.exists()) {
					Profile profile = dataSnapshot.getValue(Profile.class);
					assert profile != null;
					String fullName = getString(R.string.full_name) + " " + profile.getFirstName() + " " + profile.getOtherName();
					String dob = getString(R.string.dob) + " " + profile.getDateOfBirth();
					String homeAddress = getString(R.string.home_profile) + " " + profile.getHomeAddress();
					String workAddress = getString(R.string.work) + " " + profile.getWorkAddress();
					String gender = getString(R.string.gender) + " " + profile.getGender();
					String mobileNumber = getString(R.string.mobile) + " " + profile.getMobileNumber();
					txtUserName.setText(fullName);
					txtUserDob.setText(dob);
					txtUserHomeAddress.setText(homeAddress);
					txtUserWorkAddress.setText(workAddress);
					txtUserGender.setText(gender);
					txtUserMobileNumber.setText(mobileNumber);
					Picasso.get()
							.load(profile.getProfileImgUrl())
							.placeholder(R.drawable.ic_person)
							.error(R.drawable.ic_image_placeholder)
							.into(imgUserProfile);

					setSharedPProfileForWidget(Objects.requireNonNull(getContext()), fullName, mobileNumber, homeAddress, workAddress);

				} else {
					new AlertDialog.Builder(Objects.requireNonNull(getContext()))
							.setMessage(getString(R.string.profile_dia_msg))
							.setCancelable(false)
							.setPositiveButton(getString(R.string.ok), (dialog, id) -> {
								Intent intent = new Intent(getContext(), ProfileActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							})
							.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {

									}
							).show();
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.d(TAG, "onCancelled: " + databaseError.getMessage());
				Snackbar.make(Objects.requireNonNull(getView()).findViewById(R.id.fragDisplayMain), getString(R.string.could_not_retrieve), Snackbar.LENGTH_SHORT)
						.setActionTextColor(getResources().getColor(R.color.colorRed))
						.setAction(getString(R.string.try_again), v -> getUserDetails())
						.show();
			}
		});
	}

	@OnClick(R.id.btnLogout)
	public void onViewClicked() {
		if (mAuth.getCurrentUser() != null) {
			if (!isNetworkAvailable(Objects.requireNonNull(getContext()))) {
				Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
			} else {
				new AlertDialog.Builder(Objects.requireNonNull(getContext()))
						.setMessage(getString(R.string.want_to_logout))
						.setCancelable(false)
						.setPositiveButton(getString(R.string.Yes), (dialog, id) -> {
							SharedPreferences sharedPreferences = getContext().getSharedPreferences(getResources().getString(R.string.profile), Context.MODE_PRIVATE);
							sharedPreferences.edit().clear().apply();
							mAuth.signOut();
							Intent logoutIntent = new Intent(getContext(), SignUpActivity.class);
							logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(logoutIntent);
						})
						.setNegativeButton(getString(R.string.no), (dialog, which) -> {

								}
						).show();


			}

		}
	}

	@OnClick({R.id.btnHealth, R.id.btnEdit})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.btnHealth:
				Intent healthIntent = new Intent(getContext(), DisplayHealthInfoActivity.class);
				healthIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(healthIntent);
				break;
			case R.id.btnEdit:
				Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
				profileIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(profileIntent);
				break;
		}
	}
}
