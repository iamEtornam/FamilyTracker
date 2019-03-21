package co.etornam.familytracker.fragments;

import android.app.ProgressDialog;
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

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.etornam.familytracker.BuildConfig;
import co.etornam.familytracker.R;
import co.etornam.familytracker.dialogFragment.ContactDialogFragment;
import co.etornam.familytracker.model.Contact;
import co.etornam.familytracker.model.Profile;
import co.etornam.familytracker.ui.ProfileActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import static co.etornam.familytracker.util.Constants.CONTACT_DB;
import static co.etornam.familytracker.util.Constants.TRACKING_DB;
import static co.etornam.familytracker.util.Constants.USER_DB;
import static co.etornam.familytracker.util.NetworkUtil.isNetworkAvailable;

public class MainFragment extends Fragment {
	@BindView(R.id.addContactFab)
	FloatingActionButton addContactFab;
	private FirebaseRecyclerAdapter<Contact, ContactViewHolder> adapter;
	private FirebaseAuth mAuth;
	@BindView(R.id.rvMainContact)
	RecyclerView rvMainContact;
	private DatabaseReference mDatabase, mContactDb, mTrackingDb, mUserDb;
	private String TAG = MainFragment.class.getSimpleName();
	private String userFullName;
	private ProgressDialog progressDialog;

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
		progressDialog = new ProgressDialog(getContext());
		mAuth = FirebaseAuth.getInstance();
		mContactDb = mDatabase.child(CONTACT_DB);
		mTrackingDb = mDatabase.child(TRACKING_DB);
		mUserDb = mDatabase.child(USER_DB);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
		layoutManager.setOrientation(RecyclerView.VERTICAL);
		rvMainContact.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
		rvMainContact.setLayoutManager(layoutManager);

		mUserDb.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()) {
					Profile profile = dataSnapshot.getValue(Profile.class);
					assert profile != null;
					userFullName = profile.getFirstName() + " " + profile.getOtherName();
				} else {
					new AlertDialog.Builder(Objects.requireNonNull(getContext()))
							.setMessage("Set up your Personal profile")
							.setCancelable(false)
							.setPositiveButton("Ok", (dialog, id) -> {
								Intent intent = new Intent(getContext(), ProfileActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							})
							.setNegativeButton("Cancel", (dialog, which) -> {

									}
							).show();
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.d(TAG, "onCancelled: " + databaseError.getMessage());
			}
		});

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
				contactViewHolder.ContactDeleteBtn.setOnClickListener(v -> new AlertDialog.Builder(Objects.requireNonNull(getContext()))
						.setMessage("Are you sure you want to Delete this?")
						.setCancelable(false)
						.setPositiveButton("Yes", (dialog, id) -> {
							if (!isNetworkAvailable(Objects.requireNonNull(getContext()))) {
								Toast.makeText(getContext(), "No internet Connection.", Toast.LENGTH_SHORT).show();
							} else {
								adapter.getRef(i).removeValue();
								notifyItemRemoved(contactViewHolder.getAdapterPosition());
								notifyDataSetChanged();

								Toast.makeText(getContext(), "Deleted! ", Toast
										.LENGTH_SHORT)
										.show();
							}
						})
						.setNegativeButton("No", (dialog, which) -> {

								}
						).show()

				);

				contactViewHolder.ContactMainLayout.setOnClickListener(v ->
						new AlertDialog.Builder(Objects.requireNonNull(getContext()))
								.setMessage("Do you want to share Location with this contact?")
								.setCancelable(false)
								.setPositiveButton("Yes", (dialog, id) -> {
									if (!isNetworkAvailable(Objects.requireNonNull(getContext()))) {
										Toast.makeText(getContext(), "No internet Connection.", Toast.LENGTH_SHORT).show();
									} else {

										initializeTracker(list_id);
									}
								})
								.setNegativeButton("No", (dialog, which) -> {

										}
								).show()
				);
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
		progressDialog.setMessage("Loading...");
		progressDialog.show();
		FirebaseDynamicLinks.getInstance().createDynamicLink()
				.setLink(Uri.parse("https://www.etornam.com/tracker?id=" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid()))
				.setDomainUriPrefix("https://etornam.page.link")
				// Open links with this app on Android
				.setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
				.buildShortDynamicLink()
				.addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						// Short link created
						Uri shortLink = Objects.requireNonNull(task.getResult()).getShortLink();
						Log.d(TAG, "onComplete: SHORT LINK " + shortLink);


						//send link thru sms api
						sendSms(shortLink, positionId);


						Intent sendIntent = new Intent();
						String msg = "Hey, here is a link to my current location: " + shortLink + " you can track me using FamilyTracker App.";
						sendIntent.setAction(Intent.ACTION_SEND);
						sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
						sendIntent.setType("text/plain");
						startActivity(sendIntent);
					} else {
						Log.d(TAG, "onComplete: " + task.getException());
					}
					progressDialog.dismiss();
				});
	}

	private void sendSms(Uri shortLink, String positionId) {

//		https://app.helliomessaging.com/api/sms?username=regnex&password=xxxxxx&senderId=TEST&msisdn=233xxxxxxxxx&message=Hello_World

		mContactDb.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child(positionId).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				Contact contact = dataSnapshot.getValue(Contact.class);
				assert contact != null;
				String msisdn = contact.getNumber();
				String username = BuildConfig.SMS_USERNAME;
				String password = "&password=" + BuildConfig.SMS_PASSWORD;
				String message = "&message=" + "Hey, here is a link to my current location: " + shortLink + " you can track me using FamilyTracker App.";
				String senderId = "&senderId=" + userFullName;
				String SMS_API_URL = BuildConfig.SMS_API_URL;


				StringRequest postRequest = new StringRequest(Request.Method.POST, SMS_API_URL,
						response -> {
							try {
								Log.d(TAG, "onResponse: " + response);
							} catch (Exception e) {
								e.printStackTrace();
							}
						},
						error -> error.printStackTrace()
				) {
					@Override
					protected Map<String, String> getParams() {
						Map<String, String> params = new HashMap<>();
						// the POST parameters:
						params.put("username", username);
						params.put("password", password);
						params.put("senderId", senderId);
						params.put("msisdn", msisdn);
						params.put("message", message);

						return params;
					}
				};
				Volley.newRequestQueue(Objects.requireNonNull(getContext())).add(postRequest);

			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.d(TAG, "onCancelled: " + databaseError.getMessage());
			}
		});
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
		ImageButton ContactDeleteBtn;
		ConstraintLayout ContactMainLayout;

		ContactViewHolder(View v) {
			super(v);
			ContactNameTextView = v.findViewById(R.id.txtName);
			ContactRelationTextView = v.findViewById(R.id.txtRelation);
			ContactImage = v.findViewById(R.id.imgProfilePic);
			ContactDeleteBtn = v.findViewById(R.id.btnDelete);
			ContactMainLayout = v.findViewById(R.id.contactMainLayout);
		}
	}
}
