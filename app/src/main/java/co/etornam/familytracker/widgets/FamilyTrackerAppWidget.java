package co.etornam.familytracker.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;
import co.etornam.familytracker.R;
import co.etornam.familytracker.model.Health;
import co.etornam.familytracker.ui.MainActivity;

import static co.etornam.familytracker.util.Constants.HEALTH_DB;


/**
 * Implementation of App Widget functionality.
 */
public class FamilyTrackerAppWidget extends AppWidgetProvider {
	static String CLICK_ACTION = "CLICKED";
	static RemoteViews views;
	static Health health;

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
	                            int appWidgetId) {


		//Construct the RemoteViews object
		views = new RemoteViews(context.getPackageName(), R.layout.family_tracker_app_widget);
		Intent mainIntent = new Intent(context, MainActivity.class);
		PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);


		views.setOnClickPendingIntent(R.id.layoutWidgetMain, mainPendingIntent);
		Intent intent = new Intent(context, FamilyTrackerAppWidget.class);
		intent.setAction(CLICK_ACTION);
		context.sendBroadcast(intent);
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (CLICK_ACTION.equals(intent.getAction()) && getInformation() != null) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.family_tracker_app_widget);
			views.setTextViewText(R.id.txtWidgetName, getInformation().getDoctorname());
			ComponentName appWidget = new ComponentName(context, FamilyTrackerAppWidget.class);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			appWidgetManager.updateAppWidget(appWidget, views);
		}
	}

	private Health getInformation() {

		DatabaseReference databaseReference =
				FirebaseDatabase.getInstance().getReference().child(HEALTH_DB);
		databaseReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				health = dataSnapshot.getValue(Health.class);
				if (health != null) {
					views.setTextViewText(R.id.txtWidgetName, health.getDoctorname());
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
		return health;
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// There may be multiple widgets active, so update all of them
		for (int appWidgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
	}

	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
	}
}

