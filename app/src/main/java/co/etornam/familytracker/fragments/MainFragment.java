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
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.etornam.familytracker.R;
import co.etornam.familytracker.model.Contact;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainFragment extends Fragment {
	private FirebaseRecyclerAdapter<Contact, ContactViewHolder> adapter;
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
		ButterKnife.bind(this, view);
		mAuth = FirebaseAuth.getInstance();
		LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
		layoutManager.setOrientation(RecyclerView.VERTICAL);
		rvMainContact.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
		rvMainContact.setLayoutManager(layoutManager);

		Query query = FirebaseDatabase.getInstance()
				.getReference()
				.child("contacts")
				.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

		FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
				.setQuery(query, Contact.class)
				.build();

		adapter = new FirebaseRecyclerAdapter<Contact, ContactViewHolder>(options) {
			@Override
			protected void onBindViewHolder(@NonNull ContactViewHolder contactViewHolder, int i, @NonNull Contact contact) {
				final String list_id = getRef(i).getKey();
				contactViewHolder.ContactNameTextView.setText(contact.getName());
				contactViewHolder.ContactRelationTextView.setText(contact.getRelation());
				Picasso.get().load(contact.getImageUrl())
						.error(R.drawable.img_error)
						.placeholder(R.drawable.ic_image_placeholder)
						.into(contactViewHolder.ContactImage);
				contactViewHolder.ContactTrackBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						initializeTracker(list_id);
					}
				});

				contactViewHolder.ContactMainLayout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						initializeTracker(list_id);
					}
				});
			}

			@NonNull
			@Override
			public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
				View singleView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_main_item, parent, false);
				return new ContactViewHolder(singleView);
			}
		};

		rvMainContact.setAdapter(adapter);
		return view;
	}

	private void initializeTracker(String positionId) {
		Toast.makeText(getContext(), "tracking: " + positionId, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStart() {
		super.onStart();
		adapter.startListening();
	}

	@Override
	public void onStop() {
		super.onStop();
		adapter.stopListening();
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
