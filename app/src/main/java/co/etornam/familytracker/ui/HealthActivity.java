package co.etornam.familytracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.etornam.familytracker.R;
import co.etornam.familytracker.dialogFragment.DoctorDialogFragment;

import static co.etornam.familytracker.util.Constants.DOCTOR_NAME;
import static co.etornam.familytracker.util.Constants.DOCTOR_NUMBER;

public class HealthActivity extends AppCompatActivity {
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
	@BindView(R.id.btnSaveContact)
	Button btnSaveContact;
	@BindView(R.id.btnAddContact)
	Button btnAddContact;
	private DatabaseReference mDatabase;
	private FirebaseAuth mAuth;
	private String doctorName;
	private String doctorNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_health);
		ButterKnife.bind(this);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mAuth = FirebaseAuth.getInstance();
		mDatabase = FirebaseDatabase.getInstance().getReference();
		Intent intent = getIntent();
		if (intent != null) {
			doctorName = intent.getStringExtra(DOCTOR_NAME);
			doctorNumber = intent.getStringExtra(DOCTOR_NUMBER);
			Toast.makeText(this, "doctor name: " + doctorName, Toast.LENGTH_SHORT).show();
		} else {
			doctorName = "";
			doctorNumber = "";
		}
	}

	@OnClick({R.id.btnAddContact, R.id.btnSaveContact})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.btnAddContact:
				DialogFragment doctorFragment = new DoctorDialogFragment();
				doctorFragment.show(getSupportFragmentManager(), "doctor");
				break;
			case R.id.btnSaveContact:
				validateDetails();
				break;
		}
	}

	private void validateDetails() {
	}
}
