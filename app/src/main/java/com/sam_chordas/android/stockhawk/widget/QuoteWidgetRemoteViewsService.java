package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

/**
 * Created by Yash Tanna on 03/17/2016.
 */
public class QuoteWidgetRemoteViewsService extends RemoteViewsService {
  private static final String[] STOCK_COLUMNS = {
      QuoteColumns._ID,
      QuoteColumns.SYMBOL,
      QuoteColumns.BIDPRICE,
      QuoteColumns.PERCENT_CHANGE,
      QuoteColumns.CHANGE,
      QuoteColumns.ISUP
  };

  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return new RemoteViewsFactory() {
      private Cursor data = null;

      @Override
      public void onCreate() {

      }

      @Override
      public void onDataSetChanged() {
        if (data != null) {
          data.close();
        }
        // This method is called by the app hosting the widget (e.g., the launcher)
        // However, our ContentProvider is not exported so it doesn't have access to the
        // data. Therefore we need to clear (and finally restore) the calling identity so
        // that calls use our process and permission
        final long identityToken = Binder.clearCallingIdentity();
        data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI, STOCK_COLUMNS,
            QuoteColumns.ISCURRENT + "=?", new String[]{"1"}, null);
        Binder.restoreCallingIdentity(identityToken);
      }

      @Override
      public void onDestroy() {
        if (data != null) {
          data.close();
          data = null;
        }
      }

      @Override
      public int getCount() {
        return data == null ? 0 : data.getCount();
      }

      @Override
      public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
            data == null || !data.moveToPosition(position)) {
          return null;
        }
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_collection_item);
        views.setTextViewText(R.id.stock_symbol, data.getString(data.getColumnIndex("symbol")));
        if (data.getInt(data.getColumnIndex("is_up")) == 1) {
          views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
          views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }

        if (Utils.showPercent) {
          views.setTextViewText(R.id.change, data.getString(data.getColumnIndex("percent_change")));
        } else {
          views.setTextViewText(R.id.change, data.getString(data.getColumnIndex("change")));
        }

        final Intent fillInIntent = new Intent();
        fillInIntent.putExtra("symbol", data.getString(data.getColumnIndex("symbol")));
        views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

        return views;
      }

      @Override
      public RemoteViews getLoadingView() {
        return null;
      }

      @Override
      public int getViewTypeCount() {
        return 1;
      }

      @Override
      public long getItemId(int position) {
        if (data != null && data.moveToPosition(position)) {
          final int QUOTES_ID_COL = 0;
          return data.getLong(QUOTES_ID_COL);
        }
        return position;
      }

      @Override
      public boolean hasStableIds() {
        return true;
      }
    };
  }
}
