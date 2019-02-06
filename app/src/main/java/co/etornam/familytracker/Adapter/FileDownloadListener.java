package co.etornam.familytracker.Adapter;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;

import androidx.annotation.NonNull;

public class FileDownloadListener implements OnFailureListener, OnSuccessListener<FileDownloadTask.TaskSnapshot> {
	private String TAG = FileDownloadListener.class.getSimpleName();

	public FileDownloadListener() {

	}

	@Override
	public void onFailure(@NonNull Exception e) {
		Log.d(TAG, "Failed: " + e.getMessage());
	}

	@Override
	public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
		Log.d(TAG, "Success TaskSnapShot: " + taskSnapshot.toString());


//        login Activity implements this listner
		//  listener.tokenSuccessful(getRider());
	}
}

