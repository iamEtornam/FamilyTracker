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
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.etornam.familytracker.R;
import co.etornam.familytracker.fragments.MainFragment;
import co.etornam.familytracker.fragments.ProfileDisplayFragment;
import co.etornam.familytracker.fragments.TrackerFragment;
import co.etornam.familytracker.services.LocationFetcherService;

public class MainActivity extends AppCompatActivity {
	@BindView(R.id.mainContainer)
	FrameLayout mainContainer;
	@BindView(R.id.mainNavView)
	BottomNavigationView mainNavView;
	private boolean isTrackingActivated;
	private boolean isPinActivated;
	private LocationFetcherService fetcherService;
	private FirebaseAuth mAuth;


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
		setupPreferences();
		Intent fetchIntent = new Intent(this.getApplication(), LocationFetcherService.class);
		this.getApplication().startService(fetchIntent);
		this.getApplication().bindService(fetchIntent, serviceConnection, Context.BIND_AUTO_CREATE);
		mAuth = FirebaseAuth.getInstance();
		displaySelectedScreen(R.id.action_home);

//		fetcherService.startTracking();

		mainNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
				int itemId = menuItem.getItemId();
				displaySelectedScreen(itemId);
				return true;
			}
		});

	}

	void displaySelectedScreen(int screenId) {
		Fragment selectedFragment = null;
		switch (screenId) {
			case R.id.action_home:
				selectedFragment = new MainFragment();
				break;
			case R.id.action_profile:
				selectedFragment = new ProfileDisplayFragment();
				break;
			case R.id.action_track:
				selectedFragment = new TrackerFragment();
				break;
			case R.id.action_setting:
				Intent settingsIntent = new Intent(getApplication(), SettingsActivity.class);
				settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(settingsIntent);
				break;
		}
		assert selectedFragment != null;
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.mainContainer, selectedFragment);
		transaction.commit();
		transaction.addToBackStack(null);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
