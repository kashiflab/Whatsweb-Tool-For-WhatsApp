package com.softekapp.whatstool.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.softekapp.whatstool.Adapters.SliderAdapter;
import com.softekapp.whatstool.Models.SlidingBannerModel;
import com.softekapp.whatstool.R;
import com.softekapp.whatstool.StatusActivity;
import com.softekapp.whatstool.Utils.Utils;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FirstActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 1001;
    public static boolean isSlideAdShown = false;
    private ProgressDialog pDialog;
    private CardView webWhatsappCD;
    private CardView filesCD;
    private SliderView sliderView;
    private TextView noInternetTV;
    private ImageView imageView;
    private static boolean isShown = false;
    private CardView statusSaverCD;
//    private CardView webwhatsappCD2;

    private AdView adView;

    private SliderAdapter adapter;
    private List<SlidingBannerModel> slidingBannerModels = new ArrayList<>();

    private DatabaseReference reference;

    private static String TAG = "TAG";

    public static InterstitialAd interstitialAd;

    private boolean filesbool=false, web1bool=false, isStatus =false;

//    private Button adminBtn;

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pDialog.isShowing() & isShown){
            hidepDialog();
        }
        if(!interstitialAd.isAdLoaded()){
            interstitialAd.loadAd();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        RequestPermissions();

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);

        // Instantiate an InterstitialAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        interstitialAd = new InterstitialAd(this, getString(R.string.INTERSTITIAL_ID));

        // Set listeners for the Interstitial Ad
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                if(web1bool){
                    startActivity(new Intent(FirstActivity.this,WebWhatsAppActivity.class));
                    web1bool=false;
                } else  if(isSlideAdShown){
                    isSlideAdShown = false;
                    startActivity(new Intent(FirstActivity.this,WebViewActivity.class).putExtra("url",adapter.getURLID()));
                }
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
//                interstitialAd.loadAd();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        });


        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();

        adView = new AdView(this, getString(R.string.BANNER_ID), AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();

        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.i("Error",adError.getErrorMessage());
                ad.loadAd();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.i("Loaded","Loaded");
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.i("Clicked","Clicked");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.i("Logging","Logging");
            }
        });

        initpDialog();

        reference = FirebaseDatabase.getInstance().getReference().child("Banner");

        statusSaverCD = findViewById(R.id.statussaver);
        sliderView = findViewById(R.id.imageSlider);
        noInternetTV = findViewById(R.id.noInternet);
        webWhatsappCD = findViewById(R.id.webwhatsappCD);
//        webwhatsappCD2 = findViewById(R.id.webwhatsappCD2);
        filesCD = findViewById(R.id.filesCD);
        imageView = findViewById(R.id.noInternetIV);
//        adminBtn = findViewById(R.id.admin);

        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();


        getBannersData();

        statusSaverCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstActivity.this, StatusActivity.class));
            }
        });

//        adminBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(FirstActivity.this,AdminActivity.class));
//            }
//        });

        if(!Utils.isNetworkAvailable(FirstActivity.this)){
            sliderView.setVisibility(View.GONE);
            noInternetTV.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            Log.i("internet","Not Available");
        }else{
            sliderView.setVisibility(View.VISIBLE);
            noInternetTV.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
        }

        webWhatsappCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(interstitialAd.isAdLoaded()){
                    // Show the ad
                    interstitialAd.show();
                    showpDialog();
                    web1bool=true;
                }else{
                    web1bool = false;
                    startActivity(new Intent(FirstActivity.this, WebWhatsAppActivity.class));
                }

            }
        });

        filesCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstActivity.this,MainActivity.class));
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(interstitialAd.isAdLoaded() & !isShown){
                    interstitialAd.show();
                    isShown = true;
                }
                hidepDialog();
            }
        },5000);

        showAdWithDelay();
    }

    private void showAdWithDelay() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                Log.i("hello", "world");
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (interstitialAd.isAdLoaded()) {
                            interstitialAd.show();
                        } else {
                            Log.d("TAG", " Interstitial not loaded");
                        }
                        interstitialAd.loadAd();
                    }
                });
            }
        }, 180, 180, TimeUnit.SECONDS); //show ad after every 4 minutes
    }

    private void getBannersData() {
        showpDialog();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(slidingBannerModels.size()>0){
                    slidingBannerModels.clear();
                }
                for(DataSnapshot ds: snapshot.getChildren()){
                    slidingBannerModels.add(new SlidingBannerModel(ds.getKey().toString(),ds.child("url").getValue().toString(),
                            ds.child("title").getValue().toString(),ds.child("imageURL").getValue().toString()));
                }
                adapter = new SliderAdapter(FirstActivity.this,slidingBannerModels);
                sliderView.setSliderAdapter(adapter);

                if(isShown){
                    hidepDialog();
                }
//                if(slidingBannerModels.size()==0){
//                    noData.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FirstActivity.this, "Error"+ error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });
    }

    @SuppressLint("WrongConstant")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.privacy) {
            Uri uri = Uri.parse("https://whatstool-0.flycricket.io/privacy.html");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return (true);
        }else if(item.getItemId() == R.id.rateus){
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.sftekapp.whatstool&hl=en");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return (true);
        }else if(item.getItemId() == R.id.shareus){
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Hey, check out this amazing WhatsTool app from Google Play Store." +
                    "\nIt has 250+ games, WhatsApp Cleaner, Web WhatsApp and WhatsApp Status Saver."+
                    "Here is the link: " +
                    "https://play.google.com/store/apps/details?id=com.softekapp.whatstool&hl=en.";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "WhatsTool for WhatsApp");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        return (super.onOptionsItemSelected(item));
    }

    protected void initpDialog() {
        pDialog = new ProgressDialog(FirstActivity.this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }

    private void RequestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                "android.permission.READ_EXTERNAL_STORAGE")
                != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"},
                        REQUEST_STORAGE_PERMISSION);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted Successfully", Toast.LENGTH_SHORT).show();

                } else {
                    RequestPermissions();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}