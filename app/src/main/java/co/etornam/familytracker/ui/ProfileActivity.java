package co.etornam.familytracker.ui;

import android.Manifest;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.etornam.familytracker.R;
import co.etornam.familytracker.fragments.ProfileEditFragment;

public class ProfileActivity extends AppCompatActivity {
	@BindView(R.id.fragContainer)
	FrameLayout fragContainer;
	@BindView(R.id.profileMainLayout)
	LinearLayout profileMainLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		ButterKnife.bind(this);

		ProfileEditFragment profileEditFragment = new ProfileEditFragment();
		assert getSupportFragmentManager() != null;
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragContainer, profileEditFragment);
		transaction.commit();

		Dexter.withActivity(this).withPermissions(
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION
		).withListener(new MultiplePermissionsListener() {
			@Override
			public void onPermissionsChecked(MultiplePermissionsReport report) {

			}

			@Override
			public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
				token.continuePermissionRequest();
			}
		}).check();
	}
}
