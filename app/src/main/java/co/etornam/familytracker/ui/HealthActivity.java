package co.etornam.familytracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.etornam.familytracker.R;
import co.etornam.familytracker.dialogFragment.DoctorDialogFragment;
import co.etornam.familytracker.model.Health;

import static co.etornam.familytracker.util.Constants.DOCTOR_NAME;
import static co.etornam.familytracker.util.Constants.DOCTOR_NUMBER;
import static co.etornam.familytracker.util.Constants.HEALTH_DB;
import static co.etornam.familytracker.util.NetworkUtil.isNetworkAvailable;

public class HealthActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
	private static final String TAG = HealthActivity.class.getSimpleName();
	@BindView(R.id.txtDiabetic)
	TextView txtDiabetic;
	@BindView(R.id.diabeticYes)
	RadioButton diabeticYes;
	@BindView(R.id.diabeticNo)
	RadioButton diabeticNo;
	@BindView(R.id.diabeticRadioGroup)
	RadioGroup diabeticRadioGroup;
	@BindView(R.id.layoutDiabetic)
	LinearLayout layoutDiabetic;
	@BindView(R.id.txtMedication)
	TextView txtMedication;
	@BindView(R.id.medicationYes)
	RadioButton medicationYes;
	@BindView(R.id.medicationNo)
	RadioButton medicationNo;
	@BindView(R.id.medicationRadioGroup)
	RadioGroup medicationRadioGroup;
	@BindView(R.id.layoutMedication)
	LinearLayout layoutMedication;
	@BindView(R.id.edtMedication)
	TextInputEditText edtMedication;
	@BindView(R.id.txtAllergy)
	TextView txtAllergy;
	@BindView(R.id.allergyYes)
	RadioButton allergyYes;
	@BindView(R.id.allergyNo)
	RadioButton allergyNo;
	@BindView(R.id.allergicRadioGroup)
	RadioGroup allergicRadioGroup;
	@BindView(R.id.layoutAllergy)
	LinearLayout layoutAllergy;
	@BindView(R.id.edtAllergy)
	TextInputEditText edtAllergy;
	@BindView(R.id.txtBloodPressure)
	TextView txtBloodPressure;
	@BindView(R.id.pressureHigh)
	RadioButton pressureHigh;
	@BindView(R.id.pressureNormal)
	RadioButton pressureNormal;
	@BindView(R.id.pressureLow)
	RadioButton pressureLow;
	@BindView(R.id.pressureRadioGroup)
	RadioGroup pressureRadioGroup;
	@BindView(R.id.relativePressure)
	LinearLayout relativePressure;
	@BindView(R.id.txtBleeder)
	TextView txtBleeder;
	@BindView(R.id.bleederYes)
	RadioButton bleederYes;
	@BindView(R.id.bleederNo)
	RadioButton bleederNo;
	@BindView(R.id.bleederRadioGroup)
	RadioGroup bleederRadioGroup;
	@BindView(R.id.layoutBleeder)
	LinearLayout layoutBleeder;
	@BindView(R.id.txtOrganDonor)
	TextView txtOrganDonor;
	@BindView(R.id.organDonorYes)
	RadioButton organDonorYes;
	@BindView(R.id.organDonorNo)
	RadioButton organDonorNo;
	@BindView(R.id.donorRadioGroup)
	RadioGroup donorRadioGroup;
	@BindView(R.id.layoutOrganDonor)
	LinearLayout layoutOrganDonor;
	@BindView(R.id.txtEmergency)
	TextView txtEmergency;
	@BindView(R.id.txtContactName)
	TextView txtContactName;
	@BindView(R.id.txtContactNumber)
	TextView txtContactNumber;
	@BindView(R.id.layoutContact)
	LinearLayout layoutContact;
	@BindView(R.id.edtCompanyName)
	TextInputEditText edtCompanyName;
	@BindView(R.id.insuranceId)
	TextInputEditText insuranceId;
	@BindView(R.id.btnAddContact)
	Button btnAddContact;
	@BindView(R.id.healthLayoutMain)
	ScrollView healthLayoutMain;
	@BindView(R.id.progressBar)
	ProgressBar progressBar;
	@BindView(R.id.btnSaveHealth)
	Button btnSaveHealth;
	@BindView(R.id.txtBlood)
	TextView txtBlood;
	@BindView(R.id.spinnerBlood)
	Spinner spinnerBlood;
	@BindView(R.id.layoutBlood)
	LinearLayout layoutBlood;
	private DatabaseReference mDatabase;
	private FirebaseAuth mAuth;
	private String doctorName;
	private String doctorNumber;
	private String diabetic;
	private String medication;
	private String bleeder;
	private String allergy;
	private String pressure;
	private String donor;
	private String allergyEdit;
	private String medicationEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_health);
		ButterKnife.bind(this);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mAuth = FirebaseAuth.getInstance();
		mDatabase = FirebaseDatabase.getInstance().getReference();
		initRadioButtons();
		Intent intent = getIntent();
		if (intent != null) {
			layoutContact.setVisibility(View.VISIBLE);
			doctorName = intent.getStringExtra(DOCTOR_NAME);
			doctorNumber = intent.getStringExtra(DOCTOR_NUMBER);
			txtContactName.setText(doctorName);
			txtContactNumber.setText(doctorNumber);
		} else {
			if (layoutContact.getVisibility() == View.VISIBLE) {
				layoutContact.setVisibility(View.GONE);
			}
			doctorName = "";
			doctorNumber = "";
		}

		mDatabase.child(HEALTH_DB).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()) {
					getUserHealthDetails();
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.d(TAG, "onCancelled: " + databaseError.getMessage());
			}
		});

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.blood_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerBlood.setAdapter(adapter);
	}

	private void initRadioButtons() {
		diabeticRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			switch (checkedId) {
				case R.id.diabeticNo:
					diabetic = getResources().getString(R.string.no);
					break;
				case R.id.diabeticYes:
					diabetic = getResources().getString(R.string.yes);
					break;
			}
		});

		medicationRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			switch (checkedId) {
				case R.id.medicationNo:
					medication = "no";
					if (edtMedication.getVisibility() == View.VISIBLE) {
						edtMedication.setVisibility(View.GONE);
					}
					break;
				case R.id.medicationYes:
					medication = "YES";
					if (edtMedication.getVisibility() == View.GONE) {
						edtMedication.setVisibility(View.VISIBLE);
					}
					break;
			}
		});

		bleederRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			switch (checkedId) {
				case R.id.bleederNo:
					bleeder = "NO";
					break;
				case R.id.bleederYes:
					bleeder = "YES";
					break;
			}
		});

		allergicRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			switch (checkedId) {
				case R.id.allergyNo:
					allergy = "NO";
					if (edtAllergy.getVisibility() == View.VISIBLE) {
						edtAllergy.setVisibility(View.GONE);
					}
					break;
				case R.id.allergyYes:
					allergy = "YES";
					if (edtAllergy.getVisibility() == View.GONE) {
						edtAllergy.setVisibility(View.VISIBLE);
					}
					break;
			}
		});

		pressureRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			switch (checkedId) {
				case R.id.pressureLow:
					pressure = "LOW";
					break;
				case R.id.pressureNormal:
					pressure = "NORMAL";
					break;
				case R.id.pressureHigh:
					pressure = "HIGH";
					break;
			}
		});

		donorRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			switch (checkedId) {
				case R.id.organDonorNo:
					donor = "NO";
					break;
				case R.id.organDonorYes:
					donor = "YES";
					break;
			}
		});

	}

	private void validateDetails() {
		String insuranceCompany = edtCompanyName.getText().toString();
		String insuranceNumber = insuranceId.getText().toString();
		String bloodGroup = spinnerBlood.getSelectedItem().toString();
		if (medication.equalsIgnoreCase("Yes")) {
			medicationEdit = edtMedication.getText().toString();
		} else {
			medicationEdit = "";
		}

		if (allergy.equalsIgnoreCase("Yes")) {
			allergyEdit = edtAllergy.getText().toString();
		} else {
			allergyEdit = "";
		}


		if (!bloodGroup.isEmpty() && !diabetic.isEmpty() && !medication.isEmpty() && !medicationEdit.isEmpty()
				&& !allergy.isEmpty() && !allergyEdit.isEmpty() && !pressure.isEmpty()
				&& !bleeder.isEmpty() && !donor.isEmpty() && !insuranceCompany.isEmpty()
				&& !insuranceNumber.isEmpty() || !doctorName.isEmpty() || !doctorNumber.isEmpty()) {
			writeUserHealthDetails(bloodGroup, diabetic, medication, medicationEdit, allergy, allergyEdit, pressure, bleeder, donor, doctorName, doctorNumber, insuranceCompany, insuranceNumber);

		} else {
			Snackbar.make(findViewById(R.id.healthLayoutMain), getString(R.string.field_error), Snackbar.LENGTH_LONG).show();
		}
	}

	private void writeUserHealthDetails(String bloodGroup, String diabetic, String medication, String medinfo, String allergy, String allergyinfo, String bloodpressure, String bleeder, String donor, String doctorname, String doctornumber, String companyname, String insurancenumber) {
		progressBar.setVisibility(View.VISIBLE);
		btnSaveHealth.setVisibility(View.GONE);
		Health health = new Health(bloodGroup, diabetic, medication, medinfo, allergy, allergyinfo, bloodpressure, bleeder, donor, doctorname, doctornumber, companyname, insurancenumber);
		mDatabase.child(HEALTH_DB).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).setValue(health).addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				Snackbar.make(findViewById(R.id.healthLayoutMain), getString(R.string.details_saved), Snackbar.LENGTH_SHORT).show();
				btnSaveHealth.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				Intent intent = new Intent(getApplicationContext(), DisplayHealthInfoActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			} else {
				Snackbar.make(findViewById(R.id.healthLayoutMain), getString(R.string.couldnt_save), Snackbar.LENGTH_SHORT).show();
				btnSaveHealth.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
			}
		});
	}

	@OnClick({R.id.btnAddContact, R.id.btnSaveHealth})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.btnAddContact:
				DialogFragment doctorFragment = new DoctorDialogFragment();
				doctorFragment.show(getSupportFragmentManager(), "doctor");
				break;
			case R.id.btnSaveHealth:
				if (!isNetworkAvailable(this)) {
					Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
				} else {
					validateDetails();
				}

				break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	private void getUserHealthDetails() {
		mDatabase.child(HEALTH_DB).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()) {
					Health health = dataSnapshot.getValue(Health.class);
					assert health != null;
					edtCompanyName.setText(health.getCompanyname());
					insuranceId.setText(health.getInsurancenumber());
					txtContactName.setText(health.getDoctorname());
					txtContactNumber.setText(health.getDoctornumber());
					if (health.getDonor().equalsIgnoreCase("Yes")) {
						donorRadioGroup.check(R.id.organDonorYes);
					} else {
						donorRadioGroup.check(R.id.organDonorNo);
					}

					if (health.getBleeder().equalsIgnoreCase("Yes")) {
						bleederRadioGroup.check(R.id.bleederYes);
					} else {
						bleederRadioGroup.check(R.id.bleederNo);
					}

					if (health.getBloodpressure().equalsIgnoreCase("high")) {
						pressureRadioGroup.check(R.id.pressureHigh);
					} else if (health.getBloodpressure().equalsIgnoreCase("normal")) {
						pressureRadioGroup.check(R.id.pressureNormal);
					} else {
						pressureRadioGroup.check(R.id.pressureLow);
					}

					if (health.getDiabetic().equalsIgnoreCase("Yes")) {
						diabeticRadioGroup.check(R.id.diabeticYes);
					} else {
						diabeticRadioGroup.check(R.id.diabeticNo);
					}

					if (health.getAllergy().equalsIgnoreCase("Yes")) {
						allergicRadioGroup.check(R.id.allergyYes);
						edtAllergy.setText(health.getAllergyinfo());
					} else {
						allergicRadioGroup.check(R.id.allergyNo);
					}


					if (health.getMedication().equalsIgnoreCase("Yes")) {
						medicationRadioGroup.check(R.id.medicationYes);
						edtMedication.setText(health.getMedinfo());
					} else {
						medicationRadioGroup.check(R.id.medicationNo);
					}

					ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(HealthActivity.this, R.array.blood_array, android.R.layout.simple_spinner_item);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerBlood.setAdapter(adapter);
					if (health.getBloodgroup() != null) {
						int spinnerPosition = adapter.getPosition(health.getBloodgroup());
						spinnerBlood.setSelection(spinnerPosition);
					}
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
