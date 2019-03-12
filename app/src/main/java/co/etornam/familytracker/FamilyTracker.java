package co.etornam.familytracker;

import com.google.firebase.database.FirebaseDatabase;

import androidx.multidex.MultiDexApplication;
import io.paperdb.Paper;

public class FamilyTracker extends MultiDexApplication {

	@Override
	public void onCreate() {
		super.onCreate();
		FirebaseDatabase.getInstance().setPersistenceEnabled(true);
		Paper.init(this);
	}


}
