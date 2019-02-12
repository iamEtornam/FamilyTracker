package co.etornam.familytracker.security;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.tuyenmonkey.mkloader.MKLoader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.etornam.familytracker.R;
import co.etornam.familytracker.ui.HealthActivity;
import co.etornam.familytracker.ui.MainActivity;

public class SignUpActivity extends AppCompatActivity {
	private static final String TAG = SignUpActivity.class.getSimpleName();
	private static final int RC_SIGN_IN = 9001;
	@BindView(R.id.btnSignup)
	Button btnSignup;
	@BindView(R.id.txtTerms)
	TextView txtTerms;
	@BindView(R.id.signupLayout)
	RelativeLayout signupLayout;
	@BindView(R.id.txtGettingStarted)
	TextView txtGettingStarted;
	@BindView(R.id.progressIndicator)
	MKLoader progressIndicator;
	private FirebaseUser user;
	private FirebaseAuth mAuth;
	private GoogleSignInClient mGoogleSignInClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		ButterKnife.bind(this);

		mAuth = FirebaseAuth.getInstance();
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getResources().getString(R.string.default_web_client_id))
				.requestEmail()
				.build();

		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
	}

	@OnClick({R.id.btnSignup, R.id.txtTerms})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.btnSignup:
				signupUser();
				break;
			case R.id.txtTerms:
				readTermCondition();
				break;
		}
	}

	private void readTermCondition() {
		String url = "https://google.com";
		CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
		builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
		builder.setStartAnimations(this, R.anim.slide_right, R.anim.slide_left);
		builder.setExitAnimations(this, R.anim.slide_left, R.anim.slide_right);
		CustomTabsIntent customTabsIntent = builder.build();
		customTabsIntent.launchUrl(this, Uri.parse(url));
	}

	private void signupUser() {
		progressIndicator.setVisibility(View.VISIBLE);
		Intent signInIntent = mGoogleSignInClient.getSignInIntent();
		startActivityForResult(signInIntent, RC_SIGN_IN);
		btnSignup.setVisibility(View.GONE);
	}

	@Override
	public void onStart() {
		super.onStart();
		user = mAuth.getCurrentUser();
		if (user != null) {
			startActivity(new Intent(this, HealthActivity.class));
		}
	}

	private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

		AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, task -> {
					if (task.isSuccessful()) {
						Log.d(TAG, "signInWithCredential:success");

						startActivity(new Intent(this, MainActivity.class));

					} else {
						Snackbar.make(findViewById(R.id.signupLayout), "Error: Something went wrong.", Snackbar.LENGTH_LONG)
								.setAction("Try Again!", v ->
										signupUser()
								)
								.setActionTextColor(getResources().getColor(R.color.colorRed))
								.show();

					}

					progressIndicator.setVisibility(View.GONE);
					btnSignup.setVisibility(View.VISIBLE);

				});
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			try {
				GoogleSignInAccount account = task.getResult(ApiException.class);
				assert account != null;
				firebaseAuthWithGoogle(account);
				Snackbar.make(findViewById(R.id.signupLayout), "Welcome " + account.getDisplayName(), Snackbar.LENGTH_SHORT).show();
			} catch (ApiException e) {
				Snackbar.make(findViewById(R.id.signupLayout), "Error: Something went wrong.", Snackbar.LENGTH_SHORT).show();
				Log.w(TAG, "Google sign in failed", e);

			}
		}
	}
}
