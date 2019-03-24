package co.etornam.familytracker.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import co.etornam.familytracker.R;

import static co.etornam.familytracker.util.Constants.EMAIL_ADDRESS;
import static co.etornam.familytracker.util.Constants.SITE_URL;

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
			i.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL_ADDRESS});
			i.putExtra(Intent.EXTRA_SUBJECT, R.string.email_subject);
			i.putExtra(Intent.EXTRA_TEXT, R.string.email_details);
			try {
				startActivity(Intent.createChooser(i, getString(R.string.send_mail)));
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(getContext(), getString(R.string.no_client_installed), Toast.LENGTH_SHORT).show();
			}
		} else if (key.equals(getString(R.string.pref_share_key))) {
			Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			String shareBody = getString(R.string.share_msg);
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.share_subject);
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
			startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
		} else if (key.equals(getString(R.string.pref_privacy_key))) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(SITE_URL));
			startActivity(browserIntent);
		} else if (key.equals(getString(R.string.pref_terms_key))) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(SITE_URL));
			startActivity(browserIntent);
		}
		return super.onPreferenceTreeClick(preference);
	}
}
