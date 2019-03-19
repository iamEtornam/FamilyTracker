package co.etornam.familytracker.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.etornam.familytracker.Adapter.CustomAdapter;
import co.etornam.familytracker.R;
import co.etornam.familytracker.dialogFragment.ContactDialogFragment;
import co.etornam.familytracker.model.Contact;

import static co.etornam.familytracker.util.Constants.CONTACT_DB;

public class ContactActivity extends AppCompatActivity {

	private static final String TAG = ContactActivity.class.getSimpleName();
	@BindView(R.id.gridContact)
	GridView gridContact;
	@BindView(R.id.fabAddContact)
	FloatingActionButton fabAddContact;
	private DatabaseReference mDatabase;
	private FirebaseAuth mAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		ButterKnife.bind(this);
		mAuth = FirebaseAuth.getInstance();
		mDatabase = FirebaseDatabase.getInstance().getReference();


		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		List<Contact> contactList = getGridData();

		gridContact.setAdapter(new CustomAdapter(contactList, this));
	}

	@Override
	protected void onStart() {
		super.onStart();
		getGridData();
	}

	private List<Contact> getGridData() {
		List<Contact> contacts = new ArrayList<>();
		mDatabase.child(CONTACT_DB).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				contacts.clear();
				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					Contact contact = snapshot.getValue(Contact.class);
					assert contact != null;
					String name = contact.getName();
					String photo = contact.getImageUrl();
					String number = contact.getNumber();
					String relation = contact.getRelation();
					Object timestamp = contact.getTimestamp();
					Contact contact1 = new Contact(photo, name, number, relation, timestamp);
					contacts.add(contact1);
					Log.d(TAG, "onDataChange: " + contact.getName());

				}

			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.d(TAG, "onCancelled: " + databaseError.getMessage());
			}
		});
		Log.d(TAG, "getGridData: " + contacts);
		return contacts;
	}

	@OnClick(R.id.fabAddContact)
	public void onViewClicked() {
		ContactDialogFragment contactDialogFragment = ContactDialogFragment.newInstance();
		contactDialogFragment.show(getSupportFragmentManager(), "contact_dialog_fragment");
	}
}
