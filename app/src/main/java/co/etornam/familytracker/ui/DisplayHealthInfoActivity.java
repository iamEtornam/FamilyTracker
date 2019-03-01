package co.etornam.familytracker.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.etornam.familytracker.R;
import co.etornam.familytracker.model.Health;

import static co.etornam.familytracker.util.Constants.HEALTH_DB;

public class DisplayHealthInfoActivity extends AppCompatActivity {

	@BindView(R.id.txtBloodGroup)
	TextView txtBloodGroup;
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
	@BindView(R.id.txtInsuranceCompany)
	TextView txtInsuranceCompany;
	@BindView(R.id.txtInsuranceId)
	TextView txtInsuranceId;
	@BindView(R.id.txtDoctorName)
	TextView txtDoctorName;
	@BindView(R.id.txtDoctorNumber)
	TextView txtDoctorNumber;
	@BindView(R.id.layoutMain)
	LinearLayout layoutMain;
	private String TAG = DisplayHealthInfoActivity.class.getSimpleName();
	private DatabaseReference mDatabase;
	private FirebaseAuth mAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_health_info);
		ButterKnife.bind(this);
		mAuth = FirebaseAuth.getInstance();
		mDatabase = FirebaseDatabase.getInstance().getReference();
		getUserHealthDetails();
	}

	private void getUserHealthDetails() {
		mDatabase.child(HEALTH_DB).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				Health health = dataSnapshot.getValue(Health.class);
				assert health != null;
				txtDonor.setText(health.getDonor());
				txtDiabetic.setText(health.getDiabetic());
				txtBloodGroup.setText(health.getBloodgroup());
				txtBloodPressure.setText(health.getBloodpressure());
				txtAllergies.setText(health.getAllergy());
				txtMedication.setText(health.getMedication());
				txtInsuranceCompany.setText(health.getCompanyname());
				txtInsuranceId.setText(health.getInsurancenumber());
				txtDoctorName.setText(health.getDoctorname());
				txtDoctorNumber.setText(health.getDoctornumber());
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.d(TAG, "onCancelled: " + databaseError.getMessage());
				Snackbar.make(findViewById(R.id.layoutMain), "Couldn't Retrieve your Health Details ", Snackbar.LENGTH_SHORT)
						.setActionTextColor(getResources().getColor(R.color.colorRed))
						.setAction("Try Again!", v -> getUserHealthDetails())
						.show();
			}
		});
	}
}
