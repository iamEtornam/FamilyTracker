package co.etornam.familytracker.ui;

import android.Manifest;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.etornam.familytracker.R;
import co.etornam.familytracker.fragments.ProfileDisplayFragment;
import me.zhanghai.android.effortlesspermissions.AfterPermissionDenied;
import me.zhanghai.android.effortlesspermissions.EffortlessPermissions;
import me.zhanghai.android.effortlesspermissions.OpenAppDetailsDialogFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;

public class ProfileActivity extends AppCompatActivity {
	@BindView(R.id.fragContainer)
	FrameLayout fragContainer;
	private static final int REQUEST_CODE_SAVE_FILE_PERMISSION = 1;
	private static final String[] PERMISSIONS_SAVE_FILE = {
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.READ_CONTACTS
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		ButterKnife.bind(this);

		ProfileDisplayFragment profileEditFragment = new ProfileDisplayFragment();

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragContainer, profileEditFragment);
		transaction.commit();
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		EffortlessPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}


	// Call back to the same method so that we'll check and proceed.
	@AfterPermissionGranted(REQUEST_CODE_SAVE_FILE_PERMISSION)
	private void saveFile() {
		if (EffortlessPermissions.hasPermissions(this, PERMISSIONS_SAVE_FILE)) {
			// We've got the permission.
			saveFileWithPermission();
		} else {
			// Request the permissions.
			EffortlessPermissions.requestPermissions(this,
					R.string.save_file_permission_request_message,
					REQUEST_CODE_SAVE_FILE_PERMISSION, PERMISSIONS_SAVE_FILE);
		}
	}

	@AfterPermissionDenied(REQUEST_CODE_SAVE_FILE_PERMISSION)
	private void onSaveFilePermissionDenied() {
		if (EffortlessPermissions.somePermissionPermanentlyDenied(this, PERMISSIONS_SAVE_FILE)) {
			// Some permission is permanently denied so we cannot request them normally.
			OpenAppDetailsDialogFragment.show(
					R.string.save_file_permission_permanently_denied_message,
					R.string.open_settings, this);
		} else {
			// User denied at least some of the required permissions, report the error.
			Toast.makeText(this, R.string.save_file_permission_denied, Toast.LENGTH_SHORT).show();
		}
	}

	private void saveFileWithPermission() {
		// It's show time!
		Toast.makeText(this, R.string.save_file_show_time, Toast.LENGTH_SHORT).show();
	}
}
