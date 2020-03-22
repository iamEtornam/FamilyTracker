package co.etornam.familytracker.services;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import co.etornam.familytracker.R;
import co.etornam.familytracker.widgets.TrackerAppWidget;

import static co.etornam.familytracker.util.PrefUtil.getSharedPProfileHomeForWidget;
import static co.etornam.familytracker.util.PrefUtil.getSharedPProfileNameForWidget;
import static co.etornam.familytracker.util.PrefUtil.getSharedPProfileNumberForWidget;
import static co.etornam.familytracker.util.PrefUtil.getSharedPProfileWorkForWidget;

public class WidgetService extends Service {
	private static final String LOG = "TAG";

	public WidgetService() {
	}

	@Override
	public void onStart(Intent intent, int startId) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
				.getApplicationContext());

		int[] allWidgetIds = intent
				.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);


		for (int widgetId : allWidgetIds) {

			String profileName = getSharedPProfileNameForWidget(getApplicationContext());
			String profileNumber = getSharedPProfileNumberForWidget(getApplicationContext());
			String profileHome = getSharedPProfileHomeForWidget(getApplicationContext());
			String profileWork = getSharedPProfileWorkForWidget(getApplicationContext());

			RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.tracker_app_widget);
			Log.w("WidgetExample", profileHome);


			remoteViews.setTextViewText(R.id.txtName, profileName);
			remoteViews.setTextViewText(R.id.txtNumber, profileNumber);
			remoteViews.setTextViewText(R.id.txtHome, profileHome);
			remoteViews.setTextViewText(R.id.txtWork, profileWork);

			// Register an onClickListener
			Intent clickIntent = new Intent(this.getApplicationContext(), TrackerAppWidget.class);

			clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
					allWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.updateBtn, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
		stopSelf();

		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
