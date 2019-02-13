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

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

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
			txtContactName.setText("Name: " + doctorName);
			txtContactNumber.setText("Number: " + doctorNumber);
		} else {
			if (layoutContact.getVisibility() == View.VISIBLE) {
				layoutContact.setVisibility(View.GONE);
			}
			doctorName = "";
			doctorNumber = "";
		}

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.blood_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerBlood.setAdapter(adapter);
	}

	private void initRadioButtons() {
		diabeticRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			switch (checkedId) {
				case R.id.diabeticNo:
					diabetic = "No";
					break;
				case R.id.diabeticYes:
					diabetic = "Yes";
					break;
			}
		});

		medicationRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			switch (checkedId) {
				case R.id.medicationNo:
					medication = "No";
					if (edtMedication.getVisibility() == View.VISIBLE) {
						edtMedication.setVisibility(View.GONE);
					}
					break;
				case R.id.medicationYes:
					medication = "Yes";
					if (edtMedication.getVisibility() == View.GONE) {
						edtMedication.setVisibility(View.VISIBLE);
					}
					break;
			}
		});

		bleederRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			switch (checkedId) {
				case R.id.bleederNo:
					bleeder = "No";
					break;
				case R.id.bleederYes:
					bleeder = "Yes";
					break;
			}
		});

		allergicRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			switch (checkedId) {
				case R.id.allergyNo:
					allergy = "No";
					if (edtAllergy.getVisibility() == View.VISIBLE) {
						edtAllergy.setVisibility(View.GONE);
					}
					break;
				case R.id.allergyYes:
					allergy = "Yes";
					if (edtAllergy.getVisibility() == View.GONE) {
						edtAllergy.setVisibility(View.VISIBLE);
					}
					break;
			}
		});

		pressureRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			switch (checkedId) {
				case R.id.pressureLow:
					pressure = "Low";
					break;
				case R.id.pressureNormal:
					pressure = "Normal";
					break;
				case R.id.pressureHigh:
					pressure = "High";
					break;
			}
		});

		donorRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			switch (checkedId) {
				case R.id.organDonorNo:
					donor = "No";
					break;
				case R.id.organDonorYes:
					donor = "Yes";
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
			Log.d(TAG, "validateDetails: " + medicationEdit);
		} else {
			Snackbar.make(findViewById(R.id.healthLayoutMain), "Please, Fill Empty Fields", Snackbar.LENGTH_LONG).show();
		}
	}

	private void writeUserHealthDetails(String bloodGroup, String diabetic, String medication, String medinfo, String allergy, String allergyinfo, String bloodpressure, String bleeder, String donor, String doctorname, String doctornumber, String companyname, String insurancenumber) {
		progressBar.setVisibility(View.VISIBLE);
		btnSaveHealth.setVisibility(View.GONE);
		Health health = new Health(bloodGroup, diabetic, medication, medinfo, allergy, allergyinfo, bloodpressure, bleeder, donor, doctorname, doctornumber, companyname, insurancenumber);
		mDatabase.child(HEALTH_DB).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).setValue(health).addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				Snackbar.make(findViewById(R.id.healthLayoutMain), "Details Saved!!! ", Snackbar.LENGTH_SHORT).show();
				btnSaveHealth.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
			} else {
				Snackbar.make(findViewById(R.id.healthLayoutMain), "Couldn't Save Details ", Snackbar.LENGTH_SHORT).show();
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
				validateDetails();
				break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}
