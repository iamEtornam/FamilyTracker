package co.etornam.familytracker.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import co.etornam.familytracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileDisplayFragment extends Fragment {


	public ProfileDisplayFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_profile_display, container, false);
	}

}
