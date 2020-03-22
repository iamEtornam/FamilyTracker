package co.etornam.familytracker.dialogFragment;


import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.etornam.familytracker.R;
import co.etornam.familytracker.ui.HealthActivity;

import static android.app.Activity.RESULT_OK;
import static co.etornam.familytracker.util.Constants.DOCTOR_NAME;
import static co.etornam.familytracker.util.Constants.DOCTOR_NUMBER;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorDialogFragment extends DialogFragment {
	private final int PICK_CONTACT = 12345;
	@BindView(R.id.edtDoctorName)
	TextInputEditText edtDoctorName;
	@BindView(R.id.edtDoctorNumber)
	TextInputEditText edtDoctorNumber;
	@BindView(R.id.btnContactSelect)
	ImageButton btnContactSelect;
	private String name;
	private String number;
	private String TAG = DoctorDialogFragment.class.getSimpleName();

	public static DoctorDialogFragment newInstance() {
		return new DoctorDialogFragment();
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
		View view = inflater.inflate(R.layout.fragment_doctor_dialog, null);
		ButterKnife.bind(this, view);
		AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
		builder.setView(view)
				.setPositiveButton(R.string.save_doctor_details, (dialog, which) -> {
					String doctorName = edtDoctorName.getText().toString();
					String doctorNumber = edtDoctorNumber.getText().toString();
					Intent intent = new Intent(getActivity().getBaseContext(), HealthActivity.class);
					intent.putExtra(DOCTOR_NAME, doctorName);
					intent.putExtra(DOCTOR_NUMBER, doctorNumber);
					getActivity().startActivity(intent);
					getActivity().finish();

				});
		return builder.create();
	}

	@OnClick(R.id.btnContactSelect)
	public void onViewClicked() {
		Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
		startActivityForResult(i, PICK_CONTACT);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
			Uri contactUri = data.getData();

			assert contactUri != null;
			try (Cursor cursor = Objects.requireNonNull(getContext()).getContentResolver().query(contactUri, null, null, null, null)) {
				assert cursor != null;
				cursor.moveToFirst();
				int numberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
				int nameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
				int picColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
				name = cursor.getString(nameColumn);
				number = cursor.getString(numberColumn);
				edtDoctorName.setText(name);
				edtDoctorNumber.setText(number);

			} catch (Exception e) {
				Log.d(TAG, "onActivityResult: " + e.getMessage());
			}
		}
	}
}
