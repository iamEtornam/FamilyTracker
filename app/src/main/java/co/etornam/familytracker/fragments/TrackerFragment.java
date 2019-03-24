package co.etornam.familytracker.fragments;


import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.etornam.familytracker.R;

import static android.os.Looper.getMainLooper;
import static co.etornam.familytracker.util.NetworkUtil.isNetworkAvailable;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackerFragment extends Fragment implements OnMapReadyCallback, PermissionsListener {

	@BindView(R.id.mapView)
	MapView mapView;
	private MapboxMap mapboxMap;
	private PermissionsManager permissionsManager;
	private LocationEngine locationEngine;
	private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
	private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
	private TrackerFragmentLocationCallback callback = new TrackerFragmentLocationCallback(this);
	private DatabaseReference mDatabase;
	private FirebaseAuth mAuth;
	private String TAG = TrackerFragment.class.getSimpleName();
	private Double destinationLat;
	private Double destinationLng;
	private Point destinationPoint;

	public TrackerFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Mapbox.getInstance(Objects.requireNonNull(getContext()), getResources().getString(R.string.mapbox_api));
		mDatabase = FirebaseDatabase.getInstance().getReference();
		mAuth = FirebaseAuth.getInstance();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mapView.onCreate(savedInstanceState);
		mapView.getMapAsync(this);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_tracker, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (locationEngine != null) {
			locationEngine.removeLocationUpdates(callback);
		}
		mapView.onDestroy();
	}

	@Override
	public void onStop() {
		super.onStop();
		mapView.onStop();
	}

	@Override
	public void onStart() {
		super.onStart();
		mapView.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}

	@Override
	public void onExplanationNeeded(List<String> permissionsToExplain) {
		Toast.makeText(getActivity(), R.string.user_location_permission_explanation, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPermissionResult(boolean granted) {
		if (granted) {
			if (mapboxMap.getStyle() != null) {
				enableLocationComponent(mapboxMap.getStyle());
			}
		} else {
			Toast.makeText(getActivity(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
			Objects.requireNonNull(getActivity()).finish();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	public void onMapReady(@NonNull MapboxMap mapboxMap) {
		this.mapboxMap = mapboxMap;
		mapboxMap.setStyle(Style.MAPBOX_STREETS, this::enableLocationComponent);
	}

	@SuppressLint("MissingPermission")
	private void enableLocationComponent(Style style) {
		if (PermissionsManager.areLocationPermissionsGranted(Objects.requireNonNull(getActivity()))) {
			initLocationEngine();
			LocationComponent locationComponent = mapboxMap.getLocationComponent();
			locationComponent.activateLocationComponent(getActivity(), style, false);
			locationComponent.setLocationComponentEnabled(true);
			locationComponent.setCameraMode(CameraMode.TRACKING);
			locationComponent.setRenderMode(RenderMode.COMPASS);
		} else {
			permissionsManager = new PermissionsManager(this);
			permissionsManager.requestLocationPermissions(getActivity());
		}
	}

	@SuppressLint("MissingPermission")
	private void initLocationEngine() {
		locationEngine = LocationEngineProvider.getBestLocationEngine(Objects.requireNonNull(getActivity()));
		LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
				.setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
				.setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();
		locationEngine.requestLocationUpdates(request, callback, getMainLooper());
		locationEngine.getLastLocation(callback);
	}

	private class TrackerFragmentLocationCallback implements LocationEngineCallback<LocationEngineResult> {
		private final WeakReference<TrackerFragment> fragmentWeakReference;

		TrackerFragmentLocationCallback(TrackerFragment fragment) {
			this.fragmentWeakReference = new WeakReference<>(fragment);
		}

		@Override
		public void onSuccess(LocationEngineResult result) {
			TrackerFragment fragment = fragmentWeakReference.get();

			if (fragment != null) {
				Location location = result.getLastLocation();

				if (location == null) {
					return;
				}
				if (!isNetworkAvailable(Objects.requireNonNull(getContext()))) {
					Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
				}

				if (fragment.mapboxMap != null && result.getLastLocation() != null) {
					fragment.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
				}

			}
		}

		@Override
		public void onFailure(@NonNull Exception exception) {
			Log.d("LocationChangeActivity", exception.getLocalizedMessage());
			TrackerFragment fragment = fragmentWeakReference.get();
			if (fragment != null) {
				Toast.makeText(getContext(), exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}
}
