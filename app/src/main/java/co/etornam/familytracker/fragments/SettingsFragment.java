package co.etornam.familytracker.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import co.etornam.familytracker.R;

public class SettingsFragment extends PreferenceFragmentCompat {
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		addPreferencesFromResource(R.xml.pref_settings);
	}
}
