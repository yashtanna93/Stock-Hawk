package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by Yash Tanna on 03/17/2016.
 */
public class QuoteWidgetProvider extends AppWidgetProvider {
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    for (int appWidgetId : appWidgetIds) {
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_collection);

      // Create an Intent to launch MainActivity
      Intent intent = new Intent(context, MyStocksActivity.class);
      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
      views.setOnClickPendingIntent(R.id.widget, pendingIntent);

      // Set up the collection
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        setRemoteAdapter(context, views);
      } else {
        setRemoteAdapterV11(context, views);
      }

      PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
          .addNextIntentWithParentStack(intent)
          .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

      views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);

      // Tell the AppWidgetManager to perform an update on the current app widget
      appWidgetManager.updateAppWidget(appWidgetId, views);
    }
  }

  /**
   * Sets the remote adapter used to fill in the list items
   *
   * @param views RemoteViews to set the RemoteAdapter
   */
  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
    views.setRemoteAdapter(R.id.widget_list,
        new Intent(context, QuoteWidgetRemoteViewsService.class));
  }

  /**
   * Sets the remote adapter used to fill in the list items
   *
   * @param views RemoteViews to set the RemoteAdapter
   */
  @SuppressWarnings("deprecation")
  private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
    views.setRemoteAdapter(0, R.id.widget_list,
        new Intent(context, QuoteWidgetRemoteViewsService.class));
  }
}
