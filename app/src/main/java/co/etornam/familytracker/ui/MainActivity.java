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
import androidx.appcompat.app.AppCompatActivity;
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
		setupPreferences();
		Intent fetchIntent = new Intent(this.getApplication(), LocationFetcherService.class);
		this.getApplication().startService(fetchIntent);
		this.getApplication().bindService(fetchIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//		fetcherService.startTracking();

		mainNavView.setOnNavigationItemSelectedListener(menuItem -> {
			switch (menuItem.getItemId()) {
				case R.id.action_home:
					MainFragment mainFragment = new MainFragment();
					transaction.replace(R.id.mainContainer, mainFragment);
					transaction.commit();
					break;
				case R.id.action_profile:

					ProfileDisplayFragment profileDisplayFragment = new ProfileDisplayFragment();
					transaction.replace(R.id.mainContainer, profileDisplayFragment);
					transaction.commit();
					break;
				case R.id.action_track:
					break;
				case R.id.action_setting:
					Intent intentSettings = new Intent(getApplication(), SettingsActivity.class);
					intentSettings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intentSettings);
					break;
			}
			return false;
		});
	}
}
