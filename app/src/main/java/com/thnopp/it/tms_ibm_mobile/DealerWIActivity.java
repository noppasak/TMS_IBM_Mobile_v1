package com.thnopp.it.tms_ibm_mobile;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;

public class DealerWIActivity extends Activity {
    private WebView webView;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi);


        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String de = prefs.getString("dealerwi","");

        webView = (WebView) findViewById(R.id.webView1);
        webView.setInitialScale(1);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
      /*  webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);*/



        webView.loadUrl("https://docs.google.com/gview?embedded=true&url="+de);




    }

}
