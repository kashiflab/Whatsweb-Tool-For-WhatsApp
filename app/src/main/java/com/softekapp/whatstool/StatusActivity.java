package com.softekapp.whatstool;


import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.softekapp.whatstool.Fragments.bwa.BWAFragment;
import com.softekapp.whatstool.Fragments.wa.WAFragment;

import java.io.File;

public class StatusActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        try {
            if (!isMyServiceRunning(Class.forName("com.softekapp.statussaver.service.NotificationService"))) {
                try {
                    startService(new Intent(this, Class.forName("com.softekapp.statussaver.service.NotificationService")));
                } catch (Throwable e) {
                    throw new NoClassDefFoundError(e.getMessage());
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        stash();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null) {
            navigationView.getMenu().getItem(0).setChecked(true);
            Fragment fragment = new WAFragment();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.framelayout, fragment).commit();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_whatsapp) {
                Fragment fragment = new WAFragment();
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.framelayout, fragment).commit();
        }
            // Handle the camera action
         else if (id == R.id.nav_business) {
            if(checkInstallation("com.whatsapp.w4b")) {
                Fragment fragment = new BWAFragment();
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.framelayout, fragment).commit();
            }
            else{
                Toast.makeText(this, "Business Whatsapp Not Installed", Toast.LENGTH_SHORT).show();
            }
        }
        else {
//            AppRate.with(this).showRateDialog(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void stash() {
        File file = new File(new StringBuffer().append(new StringBuffer().append(Environment.getExternalStorageDirectory()).append(File.separator).toString()).append("WhatsApp/Media/.Statuses").toString());
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        file = new File(new StringBuffer().append(new StringBuffer().append(Environment.getExternalStorageDirectory()).append(File.separator).toString()).append("WhatsApp Business/Media/.Statuses").toString());
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        file = new File(new StringBuffer().append(new StringBuffer().append(Environment.getExternalStorageDirectory()).append(File.separator).toString()).append("GBWhatsApp/Media/.Statuses").toString());
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        new File(new StringBuffer().append(new StringBuffer().append(Environment.getExternalStorageDirectory()).append(File.separator).toString()).append("StorySaver/").toString()).mkdirs();
    }
    private boolean checkInstallation(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private boolean isMyServiceRunning(Class<?> cls) {
        for (ActivityManager.RunningServiceInfo runningServiceInfo : ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}

