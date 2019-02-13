package co.etornam.familytracker.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import co.etornam.familytracker.R;

public class MainActivity extends AppCompatActivity {
	boolean isTrackingActivated;
	boolean isPinActivated;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupPreferences();
	}

	private void setupPreferences() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		isTrackingActivated = sharedPreferences.getBoolean("track_key", false);
		isPinActivated = sharedPreferences.getBoolean("protect_key", false);
	}
}
