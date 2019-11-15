package com.carzex;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.delight.android.webview.AdvancedWebView;

public class MainActivity2 extends AppCompatActivity implements AdvancedWebView.Listener {

    AdvancedWebView advancedWebView;
    ProgressDialog progressDialog;
    String URL = "https://www.carzex.com";
    private boolean doubleBackToExitPressedOnce = false;
    String mailSubject = "", mailChooser = "", senderMail = "info@carzex.com";

    public static final int USER_MOBILE = 0;
    public static final int USER_DESKTOP = 1;

    @BindView(R.id.noInternetLayout)
    LinearLayout noInternetLayout;

    @BindView(R.id.retry)
    Button retry;

    Utility utility;

    String TAG = "MainActivity";

    @BindView(R.id.toolbar_back)
    ImageView toolbar_back;


    @BindView(R.id.toolbar_title)
    TextView toolbar_title;

    Set<String> linksSet;
    List<String> linksList;


    @BindView(R.id.toollayout)
    RelativeLayout toollayout;


    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        linksSet = new HashSet<>();
        linksList = new ArrayList<>();
        advancedWebView = findViewById(R.id.webview);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        utility = new Utility(this);

        linksSet.add(URL);
        loadData();
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });


    }


    void loadData() {
        if (utility.isNetworkConnected()) {
            progressDialog.show();
            advancedWebView.setVisibility(View.VISIBLE);
            advancedWebView.setListener(this, this);
            advancedWebView.loadUrl(URL);
            advancedWebView.setWebViewClient(new WebViewSettings());
            noInternetLayout.setVisibility(View.GONE);
        } else {
            advancedWebView.setVisibility(View.GONE);
            noInternetLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        progressDialog.show();
    }

    @Override
    public void onPageFinished(String url) {
        progressDialog.dismiss();
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        progressDialog.dismiss();
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        advancedWebView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    class WebViewSettings extends WebViewClient {
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
//            progressDialog.dismiss();
//            linksSet.add(url);
//        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "shouldOverrideUrlLoading : " + url);
            URL = url;
            loadData();
            if (url.startsWith("http:") || url.startsWith("https:")) {
                return false;
            }
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                return true;
            }
            if (url.startsWith("mailto:")) {
                final Activity activity = MainActivity2.this;
                if (activity != null) {
                    MailTo mt = MailTo.parse(url);
                    Intent i = newEmailIntent(activity, senderMail, mailSubject, mailSubject, "");
                    activity.startActivity(i);
                    view.reload();
                    return true;
                }
            }
            if (url.equals("alert://alert")) {
                Toast.makeText(MainActivity2.this, "alert", Toast.LENGTH_LONG).show();
            } else if (url.equals("choose://image")) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");

                startActivityForResult(intent, FILECHOOSER_RESULTCODE);
            }
            return true;
        }

        @Override
        public void onReceivedError(final WebView view, int errorCode, String description,
                                    final String failingUrl) {
            //control you layout, show something like a retry button, and
            //call view.loadUrl(failingUrl) to reload.
            if (errorCode == 106) {
                loadData();
            }
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

    }

    private Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
        //Toast.makeText(context, "select mail opti ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (advancedWebView.canGoBack()) {
                        advancedWebView.setWebViewClient(new MainActivity2.WebViewSettings());
                        advancedWebView.goBack();
                    } else {
                        if (doubleBackToExitPressedOnce) {
                            finish();
                        }
                        this.doubleBackToExitPressedOnce = true;
                        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        advancedWebView.onResume();
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        super.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        advancedWebView.onPause();
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        advancedWebView.onDestroy();
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        super.onDestroy();
    }
}