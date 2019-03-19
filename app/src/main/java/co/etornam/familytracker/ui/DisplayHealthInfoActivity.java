package co.etornam.familytracker.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import static co.etornam.familytracker.util.NetworkUtil.isNetworkAvailable;

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
	@BindView(R.id.txtAllergyInfo)
	TextView txtAllergyInfo;
	@BindView(R.id.txtMedicationInfo)
	TextView txtMedicationInfo;
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
		if (!isNetworkAvailable(this)) {
			Toast.makeText(this, "No internet Connection.", Toast.LENGTH_SHORT).show();
		} else {
			getUserHealthDetails();
		}

	}

	private void getUserHealthDetails() {
		mDatabase.child(HEALTH_DB).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				Health health = dataSnapshot.getValue(Health.class);
				assert health != null;
				String donor = "DONOR: " + health.getDonor();
				String diabetic = "DIABETIC: " + health.getDiabetic();
				String bloodGroup = "BLOOD GROUP: " + health.getBloodgroup();
				String bloodPressure = "BLOOD PRESSURE: " + health.getBloodpressure();
				String allergy = "ALLERGY: " + health.getAllergy();
				String allergyInfo = "ALLERGY INFO. : " + health.getAllergyinfo();
				String medication = "MEDICATION: " + health.getMedication();
				String medicationInfo = "MEDICATION INFO. : " + health.getMedinfo();
				String insuranceName = "INSURANCE COMPANY: " + health.getCompanyname();
				String insuranceNumber = "INSURANCE NUMBER: " + health.getInsurancenumber();
				String doctorName = "DOCTOR: " + health.getDoctorname();
				String doctorNumber = "(" + health.getDoctornumber() + ")";

				txtDonor.setText(donor);
				txtDiabetic.setText(diabetic);
				txtBloodGroup.setText(bloodGroup);
				txtBloodPressure.setText(bloodPressure);
				txtAllergies.setText(allergy);
				txtMedication.setText(medication);
				txtInsuranceCompany.setText(insuranceName);
				txtInsuranceId.setText(insuranceNumber);
				txtDoctorName.setText(doctorName);
				txtDoctorNumber.setText(doctorNumber);

				if (allergy.equalsIgnoreCase("yes")) {
					txtAllergyInfo.setVisibility(View.VISIBLE);
					txtAllergyInfo.setText(allergyInfo);
				} else {
					txtAllergyInfo.setVisibility(View.GONE);
				}

				if (medication.equalsIgnoreCase("yes")) {
					txtMedicationInfo.setVisibility(View.VISIBLE);
					txtMedicationInfo.setText(medicationInfo);
				} else {
					txtMedicationInfo.setVisibility(View.GONE);
				}
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
