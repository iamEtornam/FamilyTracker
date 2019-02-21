package co.etornam.familytracker.ui;

import android.os.Bundle;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.etornam.familytracker.R;

public class SingleTrackerActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

	@BindView(R.id.mapViewSingle)
	MapView mapViewSingle;
	private MapboxMap mapboxMap;
	private PermissionsManager permissionsManager;
	private LocationEngine locationEngine;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Mapbox.getInstance(this, getResources().getString(R.string.mapbox_api));
		setContentView(R.layout.activity_single_tracker);
		ButterKnife.bind(this);
		mapViewSingle.onCreate(savedInstanceState);
		mapViewSingle.getMapAsync(this);
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

	}

	@Override
	public void onPermissionResult(boolean granted) {

	}

	@Override
	public void onMapReady(@NonNull MapboxMap mapboxMap) {

	}
}
