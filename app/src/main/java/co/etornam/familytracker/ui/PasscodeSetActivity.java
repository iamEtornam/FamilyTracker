package co.etornam.familytracker.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.etornam.familytracker.R;

import static co.etornam.familytracker.util.Constants.AUTH_STATUS;
import static co.etornam.familytracker.util.Constants.ID_CODE;

public class PasscodeSetActivity extends AppCompatActivity {

	@BindView(R.id.edtPinOne)
	EditText edtPinOne;
	@BindView(R.id.edtPinTwo)
	EditText edtPinTwo;
	@BindView(R.id.btnSubmit)
	Button btnSubmit;
	@BindView(R.id.btnForgotPasscode)
	Button btnForgotPasscode;
	private SharedPreferences preferences;
	private SharedPreferences lockPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_passcode_set);
		ButterKnife.bind(this);
		lockPref = getSharedPreferences(getResources().getString(R.string.lock), MODE_PRIVATE);
		preferences = getSharedPreferences(AUTH_STATUS, MODE_PRIVATE);
		if (preferences.getBoolean(getString(R.string.isAuthSet_key), false)) {
			edtPinOne.setEnabled(false);
			edtPinTwo.setEnabled(false);
			btnSubmit.setEnabled(false);
			edtPinOne.setBackgroundColor(getResources().getColor(R.color.colorGrey));
			edtPinTwo.setBackgroundColor(getResources().getColor(R.color.colorGrey));
			btnSubmit.setBackgroundColor(getResources().getColor(R.color.colorGrey));
		}
	}

	@OnClick({R.id.btnSubmit, R.id.btnForgotPasscode})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.btnSubmit:
				String codeOne = edtPinOne.getText().toString();
				String codeTwo = edtPinTwo.getText().toString();

				if (!codeOne.isEmpty() && !codeTwo.isEmpty() && codeOne.equals(codeTwo)) {
					//save the pin
					lockPref.edit().putString(ID_CODE, codeOne).apply();
					preferences.edit().putBoolean(getString(R.string.isAuthSet_key), true).apply();
					Intent intent = new Intent(this, MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent);
				} else {
					edtPinOne.setError(getString(R.string.pin_error));
					edtPinTwo.setError(getString(R.string.pin_error));
				}
				break;
			case R.id.btnForgotPasscode:
				lockPref.edit().clear().apply();
				edtPinOne.setEnabled(true);
				edtPinTwo.setEnabled(true);
				btnSubmit.setEnabled(true);
				edtPinOne.setBackground(getResources().getDrawable(R.drawable.pin_text_outline));
				edtPinTwo.setBackground(getResources().getDrawable(R.drawable.pin_text_outline));
				btnSubmit.setBackgroundColor(getResources().getColor(R.color.colorYellow));
				break;
		}
	}
}
