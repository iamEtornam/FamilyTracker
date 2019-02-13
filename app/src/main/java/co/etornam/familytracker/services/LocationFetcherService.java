package co.etornam.familytracker.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class LocationFetcherService extends Service {
	private final LocationServiceBinder binder = new LocationServiceBinder();
	private final String TAG = LocationFetcherService.class.getSimpleName();
	private final int LOCATION_INTERVAL = 500;
	private final int LOCATION_DISTANCE = 10;
	private LocationListener mLocationListener;
	private LocationManager mLocationManager;
	private NotificationManager notificationManager;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate: ");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			startForeground(1900, getNotification());
		} else {

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mLocationManager != null) {
			try {
				mLocationManager.removeUpdates(mLocationListener);
			} catch (Exception ex) {
				Log.d(TAG, "onDestroy: " + ex);
			}
		}
	}

	private void initializeLocationManager() {
		if (mLocationManager == null) {
			mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	public void startTracking() {
		initializeLocationManager();
		mLocationListener = new co.etornam.familytracker.Listeners.LocationListener(LocationManager.GPS_PROVIDER);
		try {
			if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    Activity#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for Activity#requestPermissions for more details.
				return;
			}
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);
		} catch (java.lang.SecurityException ex) {
			// Log.i(TAG, "fail to request location update, ignore", ex);
		} catch (IllegalArgumentException ex) {
			// Log.d(TAG, "gps provider does not exist " + ex.getMessage());
		}
	}

	public void stopTracking() {
		this.onDestroy();
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private Notification getNotification() {

		NotificationChannel channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);

		NotificationManager notificationManager = getSystemService(NotificationManager.class);
		notificationManager.createNotificationChannel(channel);

		Notification.Builder builder = new Notification.Builder(getApplicationContext(), "channel_01").setAutoCancel(true);
		return builder.build();
	}

	public class LocationServiceBinder extends Binder {
		public LocationFetcherService getService() {
			return LocationFetcherService.this;
		}
	}
}
