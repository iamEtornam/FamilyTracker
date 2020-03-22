package co.etornam.familytracker.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.Objects;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.etornam.familytracker.R;
import co.etornam.familytracker.fragments.MainFragment;
import co.etornam.familytracker.fragments.ProfileDisplayFragment;
import co.etornam.familytracker.fragments.SettingsFragment;
import co.etornam.familytracker.fragments.TrackerFragment;
import co.etornam.familytracker.security.PasscodeActivity;
import co.etornam.familytracker.services.LocationFetcherService;

import static co.etornam.familytracker.util.Constants.AUTH_STATUS;
import static co.etornam.familytracker.util.Constants.ID_KEY;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
	private static final String TAG = MainActivity.class.getSimpleName();
	@BindView(R.id.mainContainer)
	FrameLayout mainContainer;
	@BindView(R.id.mainNavView)
	BottomNavigationView mainNavView;
	@BindView(R.id.toolbar)
	Toolbar toolbar;
	private boolean isTrackingActivated;
	private boolean isPinActivated;
	private String key;
	private FirebaseAuth mAuth;
	private SharedPreferences sharedPreferences;

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		toolbar.setTitle(getResources().getString(R.string.home));
		toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
		setSupportActionBar(toolbar);
		mAuth = FirebaseAuth.getInstance();
		displaySelectedScreen(R.id.action_home);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		checkPinStatus();

		mainNavView.setOnNavigationItemSelectedListener(menuItem -> {
			int itemId = menuItem.getItemId();
			displaySelectedScreen(itemId);
			return true;
		});
		FirebaseDynamicLinks.getInstance()
				.getDynamicLink(getIntent())
				.addOnSuccessListener(this, pendingDynamicLinkData -> {
					// Get deep link from result (may be null if no link is found)
					Uri deepLink = null;
					if (pendingDynamicLinkData != null) {
						deepLink = pendingDynamicLinkData.getLink();
						handleDeepLink(deepLink);
						Log.d(TAG, "onSuccess: DeepLink: " + deepLink);
					}

				})
				.addOnFailureListener(this, e ->
						Log.w(TAG, "getDynamicLink:onFailure", e));
	}

	private void handleDeepLink(Uri deepLink) {
		if (Objects.equals(deepLink.getPath(), "/tracker")) {
			key = deepLink.getQueryParameter("id");
			new Handler().postDelayed(() -> {
				Intent intent = new Intent(MainActivity.this, SingleTrackerActivity.class);
				intent.putExtra("id", deepLink.getQueryParameter("id"));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}, 3000);
		}
	}

	private void checkPinStatus() {
		SharedPreferences preferences = getSharedPreferences(AUTH_STATUS, MODE_PRIVATE);
		boolean isAuthSet = preferences.getBoolean(getString(R.string.isAuthSet_key), false);
		isPinActivated = sharedPreferences.getBoolean(getString(R.string.pref_protect_key), false);
		if (isPinActivated && isAuthSet) {
			Intent pinIntent = new Intent(this, PasscodeActivity.class);
			pinIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(pinIntent);
		}
	}

	void displaySelectedScreen(int screenId) {
		Fragment selectedFragment = null;
		Bundle bundle = new Bundle();
		bundle.putString(ID_KEY, key);
		switch (screenId) {
			case R.id.action_home:
				selectedFragment = new MainFragment();
				selectedFragment.setArguments(bundle);
				break;
			case R.id.action_profile:
				selectedFragment = new ProfileDisplayFragment();
				break;
			case R.id.action_track:
				selectedFragment = new TrackerFragment();
				break;
			case R.id.action_setting:
				selectedFragment = new SettingsFragment();
				break;
		}
		assert selectedFragment != null;
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.mainContainer, selectedFragment);
		transaction.addToBackStack(null);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.commit();

	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setMessage(getString(R.string.want_to_exit))
				.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
					Intent homeIntent = new Intent(Intent.ACTION_MAIN);
					homeIntent.addCategory(Intent.CATEGORY_HOME);
					homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(homeIntent);
				})
				.setNegativeButton(getResources().getString(R.string.no), (dialog, which) ->
						displaySelectedScreen(R.id.action_home)
				)
				.show();

		super.onBackPressed();
	}


	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		SharedPreferences preferences = getSharedPreferences(AUTH_STATUS, MODE_PRIVATE);
		boolean isAuthSet = preferences.getBoolean(getString(R.string.isAuthSet_key), false);
		isPinActivated = sharedPreferences.getBoolean(getString(R.string.pref_protect_key), false);
		if (key.equals(getString(R.string.pref_protect_key))) {
			if (isPinActivated && !isAuthSet) {
				Intent pinIntent = new Intent(this, PasscodeSetActivity.class);
				pinIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(pinIntent);
			} else if (!isPinActivated) {
				preferences.edit().putBoolean(getString(R.string.isAuthSet_key), false).apply();
				SharedPreferences lockPref = getSharedPreferences(getResources().getString(R.string.lock), MODE_PRIVATE);
				lockPref.edit().clear().apply();
			}

		} else if (key.equals(getString(R.string.pref_track_key))) {
			isTrackingActivated = sharedPreferences.getBoolean(getString(R.string.pref_track_key), false);
			if (isTrackingActivated) {
				startService(new Intent(this, LocationFetcherService.class));
				Toast.makeText(this, "start tracking...", Toast.LENGTH_SHORT).show();
			} else {
				stopService(new Intent(this, LocationFetcherService.class));
				Toast.makeText(this, "stop tracking...", Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
	}


}
