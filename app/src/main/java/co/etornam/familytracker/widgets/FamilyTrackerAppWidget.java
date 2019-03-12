package co.etornam.familytracker.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Objects;

import co.etornam.familytracker.R;
import co.etornam.familytracker.model.Contact;
import io.paperdb.Paper;

/**
 * Implementation of App Widget functionality.
 */
public class FamilyTrackerAppWidget extends AppWidgetProvider {
	static String CLICK_ACTION = "CLICKED";

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
	                            int appWidgetId) {

		Intent intent = new Intent(context, FamilyTrackerAppWidget.class);
		intent.setAction(CLICK_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		Paper.init(context);

		//read data
		String id = Paper.book().read("id");
		Contact data = Paper.book().read("data");

		// Construct the RemoteViews object
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.family_tracker_app_widget);
//		views.setTextViewText(R.id.imgWidgetPic, Picasso.get().load(data.getImageUrl()).into(R.id.imgWidgetPic));
		views.setTextViewText(R.id.txtWidgetName, data.getName());
		views.setOnClickPendingIntent(R.id.layoutWidgetMain, pendingIntent);
//		views.setEmptyView(R.id.imgWidgetPic, R.id.txtNoData);
//		views.setEmptyView(R.id.txtWidgetName, R.id.txtNoData);

		// Instruct the widget manager to update the widget
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (Objects.equals(intent.getAction(), CLICK_ACTION))
			Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// There may be multiple widgets active, so update all of them
		for (int appWidgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
	}

	@Override
	public void onEnabled(Context context) {
		// Enter relevant functionality for when the first widget is created
	}

	@Override
	public void onDisabled(Context context) {
		// Enter relevant functionality for when the last widget is disabled
	}
}

