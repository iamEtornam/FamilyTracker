package co.etornam.familytracker.security;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.nanchen.pinview.PinView;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.etornam.familytracker.R;
import co.etornam.familytracker.ui.MainActivity;
import co.etornam.familytracker.ui.PasscodeSetActivity;

public class PasscodeActivity extends AppCompatActivity implements PinView.PinViewEventListener {

	private static final String TAG = PasscodeSetActivity.class.getSimpleName();
	@BindView(R.id.pinView)
	PinView pinView;
	@BindView(R.id.btnValidate)
	Button btnValidate;
	private String pin;
	private SharedPreferences lockPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_passcode);
		ButterKnife.bind(this);
		pinView.setPinViewEventListener(this);
		lockPref = getSharedPreferences(getResources().getString(R.string.lock), MODE_PRIVATE);

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@OnClick(R.id.btnValidate)
	public void onViewClicked() {
		String savedPin = lockPref.getString("code", "null");
		Log.d(TAG, "onViewClicked: " + savedPin);
		assert savedPin != null;
		if (Objects.equals(savedPin, pin)) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else {
			Toast.makeText(this, "Passcode is wrong. Try again.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDataEntered(PinView pinView, boolean b) {
		pin = pinView.getValue();
	}
}
