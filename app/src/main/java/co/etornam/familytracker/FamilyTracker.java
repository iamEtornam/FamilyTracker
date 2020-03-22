package co.etornam.familytracker;

import com.google.firebase.database.FirebaseDatabase;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

public class FamilyTracker extends MultiDexApplication {

	@Override
	public void onCreate() {
		super.onCreate();
		FirebaseDatabase.getInstance().setPersistenceEnabled(true);
	}


}
