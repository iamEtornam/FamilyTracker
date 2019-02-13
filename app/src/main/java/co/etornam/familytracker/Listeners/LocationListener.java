package co.etornam.familytracker.Listeners;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

public class LocationListener implements android.location.LocationListener {
	private final String TAG = LocationListener.class.getSimpleName();
	private Location lastLocation = null;
	private Location mLastLocation;

	public LocationListener(String provider) {
		this.mLastLocation = new Location(provider);
	}

	@Override
	public void onLocationChanged(Location location) {
		mLastLocation = location;
		Log.d(TAG, "onLocationChanged: " + location);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(TAG, "onStatusChanged: " + status);
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(TAG, "onProviderEnabled: " + provider);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(TAG, "onProviderDisabled: " + provider);
	}
}
