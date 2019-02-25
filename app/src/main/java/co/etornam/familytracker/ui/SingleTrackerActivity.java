package co.etornam.familytracker.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joaquimley.faboptions.FabOptions;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.etornam.familytracker.R;
import co.etornam.familytracker.model.Contact;
import co.etornam.familytracker.model.Tracker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static co.etornam.familytracker.util.Constants.CONTACT_DB;
import static co.etornam.familytracker.util.Constants.TRACKING_DB;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class SingleTrackerActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, View.OnClickListener {

	@BindView(R.id.mapViewSingle)
	MapView mapViewSingle;
	@BindView(R.id.fabOptions)
	FabOptions fabOptions;
	private MapboxMap mapboxMap;
	private PermissionsManager permissionsManager;
	private static final String TAG = SingleTrackerActivity.class.getSimpleName();
	private LocationComponent locationComponent;
	private DirectionsRoute currentRoute;
	private NavigationMapRoute navigationMapRoute;
	private DatabaseReference mDatabase;
	private String postionId;
	private Contact contact;
	private FirebaseAuth mAuth;
	private Double destinationLat;
	private Double destinationLng;
	private Point destinationPoint;
	private Point originPoint;
	private Location originLocation;
	private LocationEngine locationEngine;
	private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
	private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
	private SingleTrackerLocationCallback callback = new SingleTrackerLocationCallback(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Mapbox.getInstance(this, getResources().getString(R.string.mapbox_api));
		setContentView(R.layout.activity_single_tracker);
		ButterKnife.bind(this);
		mapViewSingle.onCreate(savedInstanceState);
		Intent trackerIntent = getIntent();
		if (trackerIntent != null) {
			postionId = trackerIntent.getStringExtra("position_id");
		}
		mapViewSingle.getMapAsync(this);
		mAuth = FirebaseAuth.getInstance();
		mDatabase = FirebaseDatabase.getInstance().getReference();
		fabOptions.setOnClickListener(this);
		fabOptions.setVisibility(View.GONE);

		mDatabase.child(CONTACT_DB).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child(postionId).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				contact = dataSnapshot.getValue(Contact.class);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.d(TAG, "onCancelled: " + databaseError.getMessage());
			}
		});
	}

	@SuppressLint("MissingPermission")
	private void initLocationEngine() {
		locationEngine = LocationEngineProvider.getBestLocationEngine(this);
		LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
				.setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
				.setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();
		locationEngine.requestLocationUpdates(request, callback, getMainLooper());
		locationEngine.getLastLocation(callback);
	}

	@Override
	public void onStop() {
		super.onStop();
		mapViewSingle.onStop();
	}

	@Override
	public void onStart() {
		super.onStart();
		mapViewSingle.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		mapViewSingle.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mapViewSingle.onPause();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapViewSingle.onLowMemory();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		mapViewSingle.onSaveInstanceState(outState);
	}

	@Override
	public void onExplanationNeeded(List<String> permissionsToExplain) {
		Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onPermissionResult(boolean granted) {
		if (granted) {
			enableLocationComponent(Objects.requireNonNull(mapboxMap.getStyle()));
		} else {
			Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	public void onMapReady(@NonNull MapboxMap mapboxMap) {
		this.mapboxMap = mapboxMap;
		mapboxMap.setStyle(getString(R.string.navigation_guidance_day), style -> {
			enableLocationComponent(style);
			addDestinationIconSymbolLayer(style);
		});
	}

	private void addDestinationIconSymbolLayer(Style loadedMapStyle) {
		initLocationEngine();
		loadedMapStyle.addImage("destination-icon-id",
				BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
		GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
		loadedMapStyle.addSource(geoJsonSource);
		SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
		destinationSymbolLayer.withProperties(
				iconImage("destination-icon-id"),
				iconAllowOverlap(true),
				iconIgnorePlacement(true)
		);
		loadedMapStyle.addLayer(destinationSymbolLayer);
	}

	private void getRoute(Point origin, Point destination) {
		assert Mapbox.getAccessToken() != null;
		NavigationRoute.builder(this)
				.accessToken(Mapbox.getAccessToken())
				.origin(origin)
				.destination(destination)
				.alternatives(true)
				.language(Locale.getDefault())
				.user(DirectionsCriteria.PROFILE_DEFAULT_USER)
				.profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
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
						if (navigationMapRoute != null) {
							navigationMapRoute.removeRoute();
							mapboxMap.clear();
						} else {
							navigationMapRoute = new NavigationMapRoute(null, mapViewSingle, mapboxMap, R.style.NavigationMapRoute);
						}
						if (fabOptions.getVisibility() == View.GONE) {
							fabOptions.setVisibility(View.VISIBLE);
						}
						navigationMapRoute.addRoute(currentRoute);
					}

					@Override
					public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable throwable) {
						Log.e(TAG, "Error: " + throwable.getMessage());
					}
				});
	}

	@SuppressLint("MissingPermission")
	private void enableLocationComponent(@NonNull Style loadedMapStyle) {

		if (PermissionsManager.areLocationPermissionsGranted(this)) {
			//	initLocationEngine();
			locationComponent = mapboxMap.getLocationComponent();
			locationComponent.activateLocationComponent(this, loadedMapStyle);
			locationComponent.setLocationComponentEnabled(true);
			locationComponent.setCameraMode(CameraMode.TRACKING);
			locationComponent.setRenderMode(RenderMode.COMPASS);
		} else {
			permissionsManager = new PermissionsManager(this);
			permissionsManager.requestLocationPermissions(this);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.faboptions_call:
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				if (contact != null) {
					callIntent.setData(Uri.parse("tel:" + contact.getNumber()));
					if (ActivityCompat.checkSelfPermission(this,
							Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
						ActivityCompat.requestPermissions(this,
								new String[]{Manifest.permission.CALL_PHONE},
								1023);
					} else {
						Toast.makeText(SingleTrackerActivity.this, "calling " + contact.getName(), Toast.LENGTH_SHORT).show();
						startActivity(callIntent);
					}
				}
				break;

			case R.id.faboptions_navigate:
				Intent navigationIntent = new Intent(SingleTrackerActivity.this, CustomNavigationActivity.class);
				navigationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				navigationIntent.putExtra("destinationLatitude", destinationLat);
				navigationIntent.putExtra("destinationLongitude", destinationLng);
				navigationIntent.putExtra("originLongitude", originLocation.getLongitude());
				navigationIntent.putExtra("originLatitude", originLocation.getLatitude());
				startActivity(navigationIntent);
				break;
		}
	}


	private class SingleTrackerLocationCallback implements LocationEngineCallback<LocationEngineResult> {
		private final WeakReference<SingleTrackerActivity> trackerActivityWeakReference;

		SingleTrackerLocationCallback(SingleTrackerActivity singleTrackerActivity) {
			this.trackerActivityWeakReference = new WeakReference<>(singleTrackerActivity);
		}

		@Override
		public void onSuccess(LocationEngineResult result) {
			SingleTrackerActivity trackerActivity = trackerActivityWeakReference.get();

			if (trackerActivity != null) {
				originLocation = result.getLastLocation();

				if (originLocation == null) {
					return;
				}
				mDatabase.child(TRACKING_DB).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child(postionId).addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						Tracker tracker = dataSnapshot.getValue(Tracker.class);
							assert tracker != null;
							destinationLat = Double.parseDouble(tracker.getLatitude());
							destinationLng = Double.parseDouble(tracker.getLongitude());
							LatLng destinationLatLng = new LatLng(destinationLat, destinationLng);
							originPoint = Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());
							destinationPoint = Point.fromLngLat(destinationLatLng.getLongitude(), destinationLatLng.getLatitude());
							mapboxMap.addMarker(new MarkerOptions().position(destinationLatLng));
							getRoute(originPoint, destinationPoint);

					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {
						Log.d(TAG, "onCancelled: " + databaseError.getMessage());
					}
				});
				Log.d(TAG, "onSuccess: " + result.getLocations());

				if (trackerActivity.mapboxMap != null && result.getLastLocation() != null) {
					trackerActivity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());

				}

			}
		}

		@Override
		public void onFailure(@NonNull Exception exception) {
			Log.d("LocationChangeActivity", exception.getLocalizedMessage());
			SingleTrackerActivity trackerActivity = trackerActivityWeakReference.get();
			if (trackerActivity != null) {
				Toast.makeText(getApplicationContext(), exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}
}
