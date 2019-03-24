package co.etornam.familytracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
	@BindView(R.id.btnEdit)
	Button btnEdit;
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
				if (dataSnapshot.exists()) {
					Health health = dataSnapshot.getValue(Health.class);
					assert health != null;
					String donor = getString(R.string.donor) + health.getDonor();
					String diabetic = getString(R.string.diabetic_desc) + health.getDiabetic();
					String bloodGroup = getString(R.string.blood_group_desc) + health.getBloodgroup();
					String bloodPressure = getString(R.string.blood_pressure_desc) + health.getBloodpressure();
					String allergy = getString(R.string.allergy_desc) + health.getAllergy();
					String allergyInfo = getString(R.string.allergy_info_desc) + health.getAllergyinfo();
					String medication = getString(R.string.medication_desc) + health.getMedication();
					String medicationInfo = getString(R.string.medication_info_desc) + health.getMedinfo();
					String insuranceName = getString(R.string.indurance_desc) + health.getCompanyname();
					String insuranceNumber = getString(R.string.insurance_number_desc) + health.getInsurancenumber();
					String doctorName = getString(R.string.doctor_desc) + health.getDoctorname();
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

					if (health.getAllergy().equalsIgnoreCase(getString(R.string.yes))) {
						txtAllergyInfo.setVisibility(View.VISIBLE);
						txtAllergyInfo.setText(allergyInfo);
					} else {
						txtAllergyInfo.setVisibility(View.GONE);
					}

					if (health.getMedication().equalsIgnoreCase(getString(R.string.yes))) {
						txtMedicationInfo.setVisibility(View.VISIBLE);
						txtMedicationInfo.setText(medicationInfo);
					} else {
						txtMedicationInfo.setVisibility(View.GONE);
					}
				} else {
					new AlertDialog.Builder(DisplayHealthInfoActivity.this)
							.setMessage(getString(R.string.health_dia_msg))
							.setCancelable(false)
							.setPositiveButton(getString(R.string.ok), (dialog, id) -> {
								Intent intent = new Intent(DisplayHealthInfoActivity.this, HealthActivity.class);
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
				Snackbar.make(findViewById(R.id.layoutMain), getString(R.string.could_not_get_health_info), Snackbar.LENGTH_SHORT)
						.setActionTextColor(getResources().getColor(R.color.colorRed))
						.setAction(getString(R.string.try_again), v -> getUserHealthDetails())
						.show();
			}
		});
	}

	@OnClick(R.id.btnEdit)
	public void onViewClicked() {
		Intent intent = new Intent(this, HealthActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
}
