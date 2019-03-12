package co.etornam.familytracker.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
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
import butterknife.OnClick;
import co.etornam.familytracker.R;
import co.etornam.familytracker.dialogFragment.ContactDialogFragment;
import co.etornam.familytracker.model.Contact;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import static co.etornam.familytracker.util.Constants.CONTACT_DB;
import static co.etornam.familytracker.util.Constants.TRACKING_DB;

public class MainFragment extends Fragment {
	@BindView(R.id.addContactFab)
	FloatingActionButton addContactFab;
	private FirebaseRecyclerAdapter<Contact, ContactViewHolder> adapter;
	private FirebaseAuth mAuth;
	@BindView(R.id.rvMainContact)
	RecyclerView rvMainContact;
	private DatabaseReference mDatabase, mContactDb, mTrackingDb;
	private String TAG = MainFragment.class.getSimpleName();

	public MainFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDatabase = FirebaseDatabase.getInstance().getReference();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);
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
				contactViewHolder.ContactTrackBtn.setOnClickListener(v -> initializeTracker(list_id));

				contactViewHolder.ContactMainLayout.setOnClickListener(v -> initializeTracker(list_id));
				contactViewHolder.ContactMainLayout.setOnLongClickListener(v -> {
					addToPaperDb(list_id);
					return false;
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

	private void addToPaperDb(String list_id) {
		mContactDb = mDatabase.child(CONTACT_DB);
		mTrackingDb = mDatabase.child(TRACKING_DB);
		mContactDb.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child(list_id).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				Contact contact = dataSnapshot.getValue(Contact.class);
				assert contact != null;
				Paper.book().write("id", list_id);
				Paper.book().write("data", contact);
				Toast.makeText(getContext(), "Added to Widget!", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.d(TAG, "onCancelled: " + databaseError.getMessage());
				Toast.makeText(getContext(), "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();
			}
		});

		mTrackingDb.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("authorized").child(list_id).setValue(true);

	}

	private void initializeTracker(String positionId) {
/*
		DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
				.setLink(Uri.parse("https://www.etornam.com/"))
				.setDomainUriPrefix("https://etornam.page.link&"+positionId)
				.setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
				.buildDynamicLink();

		Uri dynamicLinkUri = dynamicLink.getUri();
		Log.d(TAG, "initializeTracker: "+Uri.decode(dynamicLinkUri.toString()));
		Intent sendIntent = new Intent();
		String msg = "Hey, check this out: " + Uri.decode(dynamicLinkUri.toString()) ;
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
*/

		FirebaseDynamicLinks.getInstance().createDynamicLink()
				.setLink(Uri.parse("https://www.etornam.com/tracker?id=" + positionId))
				.setDomainUriPrefix("https://etornam.page.link")
				// Open links with this app on Android
				.setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
				.buildShortDynamicLink()
				.addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
					@Override
					public void onComplete(@NonNull Task<ShortDynamicLink> task) {
						if (task.isSuccessful()) {
							// Short link created
							Uri shortLink = Objects.requireNonNull(task.getResult()).getShortLink();
							Log.d(TAG, "onComplete: SHORT LINK " + shortLink);

							Intent sendIntent = new Intent();
							String msg = "Hey, check this out: " + shortLink;
							sendIntent.setAction(Intent.ACTION_SEND);
							sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
							sendIntent.setType("text/plain");
							startActivity(sendIntent);
						} else {
							Log.d(TAG, "onComplete: " + task.getException());
						}
					}
				});

//
//		FirebaseDynamicLinks.getInstance().createDynamicLink()
//				.setLongLink(Uri.parse("https://example.page.link/?link=https://www.example.com/&apn=com.example.android&ibn=com.example.ios"))
//				.buildShortDynamicLink()
//				.addOnCompleteListener(task -> {
//					if (task.isSuccessful()) {
//						// Short link created
//						Uri shortLink = Objects.requireNonNull(task.getResult()).getShortLink();
//						Uri flowchartLink = task.getResult().getPreviewLink();
//						Log.d(TAG, "onComplete: SHORT LINK: " + shortLink);
//					} else {
//						Log.d(TAG, "initializeTracker: " + task.getException());
//					}
//				});


/*

		FirebaseDynamicLinks.getInstance().createDynamicLink()
				.setLink(Uri.parse("https://www.example.com/"))
				.setDomainUriPrefix("https://example.page.link&"+positionId)
				.setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
				.buildShortDynamicLink()
				.addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
					@Override
					public void onComplete(@NonNull Task<ShortDynamicLink> task) {
						Log.d(TAG, "onComplete: "+task.getResult().toString());
						if (task.isSuccessful()){
							Uri dynamicLink = Objects.requireNonNull(task.getResult()).getShortLink();
							Intent sendIntent = new Intent();
							String msg = "Hey, check this out: " + dynamicLinkUri.toString();
							sendIntent.setAction(Intent.ACTION_SEND);
							sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
							sendIntent.setType("text/plain");
							startActivity(sendIntent);
						}else {
							Log.d(TAG, "onComplete: "+task.getException());
						}
					}
				});
*/


		/*Intent trackerIntent = new Intent(getActivity(), SingleTrackerActivity.class);
		trackerIntent.putExtra("position_id", positionId);
		trackerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(trackerIntent);*/
	}

	public void onboardingShare(ShortDynamicLink dl) {
		// [START ddl_onboarding_share]
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "Try this amazing app: " + dl.getShortLink());
		startActivity(Intent.createChooser(intent, "Share using"));
		// [END ddl_onboarding_share]
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

	@OnClick(R.id.addContactFab)
	public void onViewClicked() {
		ContactDialogFragment contactDialogFragment = ContactDialogFragment.newInstance();
		assert getFragmentManager() != null;
		contactDialogFragment.show(getFragmentManager(), "contact_dialog_fragment");
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
