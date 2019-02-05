package co.etornam.familytracker.ui;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.etornam.familytracker.R;
import co.etornam.familytracker.fragments.ProfileDisplayFragment;

public class ProfileActivity extends AppCompatActivity {
	@BindView(R.id.fragContainer)
	FrameLayout fragContainer;

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
}
