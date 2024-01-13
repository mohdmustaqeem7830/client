package com.Hamza.niosgeniusbookshub;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ViewPdf extends AppCompatActivity {

    WebView viewer;
    PDFView pdfView;
    TextView tester;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.bluecolor));

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        viewer = findViewById(R.id.viewer);
        viewer.getSettings().setJavaScriptEnabled(true);
        String url = getIntent().getStringExtra("url");

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading data...");
        pdfView = findViewById(R.id.pdf_view);

        // Check if the URL is a remote URL or a local file path
            if (isRemoteUrl(url)) {
                new RetrievePDFfromUrl().execute(url);
            } else {
                new RetrievePDFfromFile().execute(url);

        }
    }

    private boolean isRemoteUrl(String url) {
        // Simple check if the URL starts with "http" or "https"
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

    class RetrievePDFfromUrl extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            // We are using InputStream
            // for getting our PDF.
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                // Below is the step where we are
                // creating our connection.
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    // Response is success.
                    // We are getting InputStream from URL
                    // and storing it in our variable.
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                // This is the method
                // to handle errors.
                e.printStackTrace();
            }
            return inputStream;
        }





        @Override
        protected void onPostExecute(InputStream inputStream) {
            // After the execution of our AsyncTask,
            // we are loading our PDF in our PDFView.
            if (inputStream != null) {
                pdfView.fromStream(inputStream).load();
            } else {
                // Handle the case where inputStream is null
                // For example, show a message or take appropriate action
                // In this example, we'll print a log.
                Log.e("ViewPdf", "Failed to retrieve PDF InputStream");
            }
        }
    }

    class RetrievePDFfromFile extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            try {
                // Open the local file as an InputStream
                InputStream inputStream = new FileInputStream(strings[0]);
                return inputStream;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            if (inputStream != null) {
                pdfView.fromStream(inputStream).load();
            } else {
                Log.e("ViewPdf", "Failed to retrieve PDF InputStream from file");
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
