package com.sam_chordas.android.stockhawk.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import com.sam_chordas.android.stockhawk.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Yash Tanna on 03/21/2016.
 */
public class StockDetailService extends AsyncTask<Void, Void, String> {
  String url;
  Activity activity;
  private String response;
  private OkHttpClient client = new OkHttpClient();

  public StockDetailService(String url, Activity activity) {
    this.url = url;
    this.activity = activity;
  }

  String fetchData(String url) throws IOException {
    Request request = new Request.Builder()
        .url(url)
        .build();

    Response response = client.newCall(request).execute();
    return response.body().string();
  }

  public String getJsonResponse() {
    return response;
  }

  private void setTextView(int id, String text) {
    TextView textView = (TextView) activity.findViewById(id);
    textView.setText(text);
  }

  @Override
  protected String doInBackground(Void... params) {
    try {
      response = fetchData(url);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return response;
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onPostExecute(String result) {
    JSONObject stockJson = null;
    try {
      if(result!=null) {
        stockJson = new JSONObject(result);
        JSONObject quote = stockJson.getJSONObject("query").getJSONObject("results")
            .getJSONObject("quote");
        String currency = " " + quote.getString("Currency");
        setTextView(R.id.lastprice, quote.getString("PreviousClose") + currency);
        setTextView(R.id.openprice, quote.getString("Open") + currency);
        setTextView(R.id.highprice, quote.getString("DaysHigh") + currency);
        setTextView(R.id.lowprice, quote.getString("DaysLow") + currency);
        setTextView(R.id.marketcap, quote.getString("MarketCapitalization") + currency);
        setTextView(R.id.volume, quote.getString("Volume"));
        activity.setTitle(quote.getString("Name"));
        //Log.v("array-", stocksArray);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
