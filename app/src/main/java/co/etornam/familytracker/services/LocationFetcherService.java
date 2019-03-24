package co.etornam.familytracker.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

import androidx.core.app.ActivityCompat;

import static co.etornam.familytracker.util.Constants.TRACKING_DB;

public class LocationFetcherService extends Service {
	private String TAG = LocationFetcherService.class.getSimpleName();
	private static final int LOCATION_INTERVAL = 500;
	private static final float LOCATION_DISTANCE = 1f;
	LocationListener[] mLocationListeners = new LocationListener[]{
			new LocationListener(LocationManager.PASSIVE_PROVIDER),
			new LocationListener(LocationManager.GPS_PROVIDER),
			new LocationListener(LocationManager.NETWORK_PROVIDER)
	};
	private LocationManager mLocationManager = null;
	private DatabaseReference mReference, mLocationDb;
	private FirebaseAuth mAuth;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "onStartCommand");
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	@Override
	public void onCreate() {
		Log.e(TAG, "onCreate");
		mReference = FirebaseDatabase.getInstance().getReference();
		mAuth = FirebaseAuth.getInstance();
		initializeLocationManager();

		try {
			mLocationManager.requestLocationUpdates(
					LocationManager.PASSIVE_PROVIDER,
					LOCATION_INTERVAL,
					LOCATION_DISTANCE,
					mLocationListeners[0]
			);
		} catch (java.lang.SecurityException ex) {
			Log.d(TAG, "fail to request location update, ignore", ex);
		} catch (IllegalArgumentException ex) {
			Log.d(TAG, "network provider does not exist, " + ex.getMessage());
		}
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");
		super.onDestroy();
		if (mLocationManager != null) {
			for (int i = 0; i < mLocationListeners.length; i++) {
				try {
					if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						return;
					}
					mLocationManager.removeUpdates(mLocationListeners[i]);
				} catch (Exception ex) {
					Log.i(TAG, "fail to remove location listener, ignore", ex);
				}
			}
		}
	}

	private void initializeLocationManager() {
		Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: " + LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
		if (mLocationManager == null) {
			mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
			initializeLocationManager();
		}
	}

	private class LocationListener implements android.location.LocationListener {
		Location mLastLocation;

		LocationListener(String provider) {
			Log.e(TAG, "LocationListener " + provider);
			mLastLocation = new Location(provider);
		}

		@Override
		public void onLocationChanged(Location location) {
			Log.e(TAG, "onLocationChanged: " + location);

			try {

				mLocationDb = mReference.child(TRACKING_DB).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
				HashMap<String, String> stringMap = new HashMap<>();
				stringMap.put("accuracy", String.valueOf(location.getAccuracy()));
				stringMap.put("altitude", String.valueOf(location.getAltitude()));
				stringMap.put("bearing", String.valueOf(location.getBearing()));
				stringMap.put("elapsedRealtimeNanos", String.valueOf(location.getElapsedRealtimeNanos()));
				stringMap.put("fromMockProvider", String.valueOf(location.isFromMockProvider()));
				stringMap.put("latitude", String.valueOf(location.getLatitude()));
				stringMap.put("longitude", String.valueOf(location.getLongitude()));
				stringMap.put("provider", location.getProvider());
				stringMap.put("speed", String.valueOf(location.getSpeed()));
				stringMap.put("time", String.valueOf(location.getTime()));
				mLocationDb.setValue(stringMap)
						.addOnSuccessListener(aVoid ->
								Log.d(TAG, "UserTracking onSuccess: Uploaded"))
						.addOnFailureListener(e ->
								Log.d(TAG, "UserTracking onFailure: failed to upload"));

				mLastLocation.set(location);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.e(TAG, "onProviderDisabled: " + provider);
		}

		@Override
		public void onProviderEnabled(String provider) {

			Log.e(TAG, "onProviderEnabled: " + provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.e(TAG, "onStatusChanged: " + provider);
		}
	}
}
