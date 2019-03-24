package co.etornam.familytracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.etornam.familytracker.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static co.etornam.familytracker.util.Constants.DESTINATIONLAT;
import static co.etornam.familytracker.util.Constants.DESTINATIONLNG;
import static co.etornam.familytracker.util.Constants.ORIGINLAT;
import static co.etornam.familytracker.util.Constants.ORIGINLNG;

public class CustomNavigationActivity extends AppCompatActivity implements OnNavigationReadyCallback {

	@BindView(R.id.navigationView)
	NavigationView navigationView;
	private boolean simulateRoute = true;
	private String TAG = CustomNavigationActivity.class.getSimpleName();
	private DirectionsRoute currentRoute;
	private Double navigateDestinationLat, navigateDestinationLng;
	private Double navigateOriginLat, navigateOriginLng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_AppCompat_NoActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_navigation);
		ButterKnife.bind(this);
		navigationView.onCreate(savedInstanceState);
		navigationView.initialize(this);
		Intent navigationIntent = getIntent();
		if (navigationIntent != null) {
			navigateDestinationLat = navigationIntent.getDoubleExtra(DESTINATIONLAT, 0.0);
			navigateDestinationLng = navigationIntent.getDoubleExtra(DESTINATIONLNG, 0.0);
			navigateOriginLat = navigationIntent.getDoubleExtra(ORIGINLAT, 0.0);
			navigateOriginLng = navigationIntent.getDoubleExtra(ORIGINLNG, 0.0);
		}
		Log.d(TAG, "onCreate: originLatlng: " + navigateOriginLat + ", " + navigateOriginLng);
		Log.d(TAG, "onCreate: destinationLatlng: " + navigateDestinationLat + ", " + navigateDestinationLng);
	}

	@Override
	public void onStart() {
		super.onStart();
		navigationView.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		navigationView.onResume();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		navigationView.onLowMemory();
	}

	@Override
	public void onBackPressed() {
// If the navigation view didn't need to do anything, call super
		if (!navigationView.onBackPressed()) {
			super.onBackPressed();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		navigationView.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		navigationView.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onPause() {
		super.onPause();
		navigationView.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		navigationView.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		navigationView.onDestroy();
	}

	private void getRoute(Point origin, Point destination) {
		assert Mapbox.getAccessToken() != null;
		NavigationRoute.builder(this)
				.accessToken(Mapbox.getAccessToken())
				.origin(origin)
				.destination(destination)
				.build()
				.getRoute(new Callback<DirectionsResponse>() {
					@Override
					public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull Response<DirectionsResponse> response) {
						Log.d(TAG, "Response code: " + response.code());
						if (response.body() == null) {
							Log.e(TAG, "No routes found, make sure you set the right user and access token.");
							return;
						} else if (response.body().routes().size() < 1) {
							Log.e(TAG, "No routes found");
							return;
						}
						currentRoute = response.body().routes().get(0);
						NavigationViewOptions options = NavigationViewOptions.builder()
								.directionsRoute(currentRoute)
								.shouldSimulateRoute(simulateRoute)
								.build();

						navigationView.startNavigation(options);
					}

					@Override
					public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable throwable) {
						Log.e(TAG, "Error: " + throwable.getMessage());
					}
				});
	}

	@Override
	public void onNavigationReady(boolean isRunning) {
		Point destination = Point.fromLngLat(navigateDestinationLng, navigateDestinationLat);
		Point origin = Point.fromLngLat(navigateOriginLng, navigateOriginLat);
		getRoute(origin, destination);
	}


}
