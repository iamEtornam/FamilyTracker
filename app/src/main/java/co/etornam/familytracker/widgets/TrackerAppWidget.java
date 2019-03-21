package co.etornam.familytracker.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import co.etornam.familytracker.R;
import co.etornam.familytracker.ui.MainActivity;
import io.paperdb.Paper;

/**
 * Implementation of App Widget functionality.
 */
public class TrackerAppWidget extends AppWidgetProvider {

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
	                            int appWidgetId) {

		CharSequence widgetName = Paper.book().read("name");
		CharSequence widgetNumber = Paper.book().read("number");
		CharSequence widgetHomeAddress = Paper.book().read("home_address");
		CharSequence widgetWorkAddress = Paper.book().read("work_address");

		Intent intent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		// Construct the RemoteViews object
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tracker_app_widget);
		views.setTextViewText(R.id.txtPersonName, widgetName);
		views.setTextViewText(R.id.txtPersonNumber, widgetNumber);
		views.setTextViewText(R.id.txtPersonHomeAddress, widgetHomeAddress);
		views.setTextViewText(R.id.txtPersonWorkAddress, widgetWorkAddress);
		views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);

		// Instruct the widget manager to update the widget
		appWidgetManager.updateAppWidget(appWidgetId, views);
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

