package co.etornam.familytracker.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import co.etornam.familytracker.R;

public class SettingsFragment extends PreferenceFragmentCompat {
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		addPreferencesFromResource(R.xml.pref_settings);
	}

	@Override
	public boolean onPreferenceTreeClick(Preference preference) {
		String key = preference.getKey();
		if (key.equals(getString(R.string.pref_report_key))) {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL, new String[]{"sunumacbright@gmail.com"});
			i.putExtra(Intent.EXTRA_SUBJECT, "Report from Family Tracker App");
			i.putExtra(Intent.EXTRA_TEXT, "Enter details here...");
			try {
				startActivity(Intent.createChooser(i, "Send mail..."));
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			}
		} else if (key.equals(getString(R.string.pref_share_key))) {
			Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			String shareBody = "Hi, check out this awesome App. you will love it!";
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Awesome!!!");
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
			startActivity(Intent.createChooser(sharingIntent, "Share via..."));
		} else if (key.equals(getString(R.string.pref_privacy_key))) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/1KgEHGi2T9JbvtAX-Yoe15j5Ac1CR5Acr4mcfexM61ak/"));
			startActivity(browserIntent);
		} else if (key.equals(getString(R.string.pref_terms_key))) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/1KgEHGi2T9JbvtAX-Yoe15j5Ac1CR5Acr4mcfexM61ak/"));
			startActivity(browserIntent);
		}
		return super.onPreferenceTreeClick(preference);
	}
}
