package co.etornam.familytracker.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.etornam.familytracker.R;
import co.etornam.familytracker.fragments.MainFragment;
import co.etornam.familytracker.fragments.ProfileDisplayFragment;
import co.etornam.familytracker.services.LocationFetcherService;

public class MainActivity extends AppCompatActivity {
	private boolean isTrackingActivated;
	private boolean isPinActivated;
	private LocationFetcherService fetcherService;
	private FragmentTransaction transaction;
	@BindView(R.id.mainNavView)
	BottomNavigationView mainNavView;
	private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			String name = className.getClassName();
			if (name.endsWith("BackgroundService")) {
				fetcherService = ((LocationFetcherService.LocationServiceBinder) service).getService();
//				btnStartTracking.setEnabled(true);
//				txtStatus.setText("GPS Ready");
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			if (className.getClassName().equals("BackgroundService")) {
				fetcherService = null;
			}
		}
	};

	private void setupPreferences() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		isTrackingActivated = sharedPreferences.getBoolean("track_key", false);
		isPinActivated = sharedPreferences.getBoolean("protect_key", false);
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		transaction = getSupportFragmentManager().beginTransaction();
		displaySelectedScreen(R.id.action_home);
		setupPreferences();
		Intent fetchIntent = new Intent(this.getApplication(), LocationFetcherService.class);
		this.getApplication().startService(fetchIntent);
		this.getApplication().bindService(fetchIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//		fetcherService.startTracking();

		mainNavView.setOnNavigationItemSelectedListener(menuItem -> {
			displaySelectedScreen(menuItem.getItemId());
			return true;
		});
	}

	private void displaySelectedScreen(int itemId) {
		Fragment fragment = null;

		switch (itemId) {
			case R.id.action_home:
				fragment = new MainFragment();
				break;
			case R.id.action_profile:
				fragment = new ProfileDisplayFragment();
				break;
			case R.id.action_track:
				break;
			case R.id.action_setting:
				Intent intentSettings = new Intent(getApplication(), SettingsActivity.class);
				intentSettings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intentSettings);
				break;
		}

		if (fragment != null) {
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.mainContainer, fragment);
			transaction.addToBackStack(null);
		}
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setMessage("Are you sure you want to exit?")
				.setCancelable(false)
				.setPositiveButton("Yes", (dialog, id) -> MainActivity.this.finish())
				.setNegativeButton("No", (dialog, which) -> displaySelectedScreen(R.id.action_home))
				.show();
		super.onBackPressed();
	}
}
