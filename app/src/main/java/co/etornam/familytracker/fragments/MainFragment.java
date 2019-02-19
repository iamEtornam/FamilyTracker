package co.etornam.familytracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import co.etornam.familytracker.R;
import co.etornam.familytracker.model.Contact;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainFragment extends Fragment {
	private FirebaseRecyclerAdapter<Contact, ContactViewHolder> recyclerAdapter;
	private FirebaseAuth mAuth;
	@BindView(R.id.rvMainContact)
	RecyclerView rvMainContact;

	public MainFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		Toast.makeText(getContext(), "Horaaay!", Toast.LENGTH_SHORT).show();
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mAuth = FirebaseAuth.getInstance();
		Query query = FirebaseDatabase.getInstance()
				.getReference()
				.child("contacts")
				.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
				.limitToLast(10);

		FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
				.setQuery(query, Contact.class)
				.build();

		recyclerAdapter = new FirebaseRecyclerAdapter<Contact, ContactViewHolder>(options) {
			@Override
			protected void onBindViewHolder(@NonNull ContactViewHolder contactViewHolder, int i, @NonNull Contact contact) {
				Toast.makeText(getContext(), "contact name: " + contactViewHolder.ContactNameTextView, Toast.LENGTH_SHORT).show();
			}

			@NonNull
			@Override
			public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
				View singleView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_main_item, parent, false);
				return new ContactViewHolder(singleView);
			}
		};

		rvMainContact.setAdapter(recyclerAdapter);
	}

	@Override
	public void onStart() {
		super.onStart();
		recyclerAdapter.startListening();
	}

	@Override
	public void onStop() {
		super.onStop();
		recyclerAdapter.stopListening();
	}

	public static class ContactViewHolder extends RecyclerView.ViewHolder {

		TextView ContactNameTextView;
		CircleImageView ContactImage;
		TextView ContactRelationTextView;
		ImageButton ContactTrackBtn;
		ConstraintLayout ContactMainLayout;

		ContactViewHolder(View v) {
			super(v);
			ContactNameTextView = v.findViewById(R.id.txtName);
			ContactRelationTextView = v.findViewById(R.id.txtRelation);
			ContactImage = v.findViewById(R.id.imgProfilePic);
			ContactTrackBtn = v.findViewById(R.id.btnTrack);
			ContactMainLayout = v.findViewById(R.id.contactMainLayout);
		}
	}
}
