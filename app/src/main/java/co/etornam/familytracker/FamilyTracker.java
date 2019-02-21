package co.etornam.familytracker;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class FamilyTracker extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		FirebaseDatabase.getInstance().setPersistenceEnabled(true);
	}


}
