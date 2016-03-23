package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockDetailService;
import com.squareup.picasso.Picasso;

public class MyStockDetailActivity extends AppCompatActivity {

  private void setButtonClick(int id, String img, ImageView imgView) {
    Button button = (Button) findViewById(id);
    final String imgurl = img;
    final ImageView imageView = imgView;
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Picasso.with(getApplicationContext()).load(imgurl).into(imageView);
      }
    });
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my_stock_detail);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    Configuration config = getResources().getConfiguration();
    Intent intent = getIntent();
    String symbol = intent.getStringExtra(MyStocksActivity.EXTRA_MESSAGE);
    //Log.v("Symbol", intent.getStringExtra(MyStocksActivity.EXTRA_MESSAGE));
    String stockquery = "select * from yahoo.finance.quotes where symbol in ('" + symbol + "')";
    Uri uri = Uri.parse("http://query.yahooapis.com/v1/public/yql?").buildUpon()
        .appendQueryParameter("q", stockquery)
        .appendQueryParameter("env", "store://datatables.org/alltableswithkeys")
        .appendQueryParameter("format", "json")
        .build();
    TextView stock_heading = (TextView) findViewById(R.id.stock_heading);
    stock_heading.setText(symbol);
    StockDetailService stockDetailService = new StockDetailService(uri.toString(), this);
    stockDetailService.execute();
    String imgurl = "http://ichart.finance.yahoo.com/b?s=" + symbol;
    ImageView imageView = (ImageView) findViewById(R.id.stock_image);
    Picasso.with(this).load(imgurl).into(imageView);
    setButtonClick(R.id.button1d, imgurl, imageView);
    setButtonClick(R.id.button5d, "http://ichart.finance.yahoo.com/w?s=" + symbol, imageView);
    setButtonClick(R.id.button3m, "http://ichart.finance.yahoo.com/c/3m/" + symbol, imageView);
    setButtonClick(R.id.button6m, "http://ichart.finance.yahoo.com/c/6m/" + symbol, imageView);
    setButtonClick(R.id.button1y, "http://ichart.finance.yahoo.com/c/1y/" + symbol, imageView);
    setButtonClick(R.id.button2y, "http://ichart.finance.yahoo.com/c/2y/" + symbol, imageView);
    setButtonClick(R.id.button, "http://ichart.finance.yahoo.com/c/my/" + symbol, imageView);
  }
}