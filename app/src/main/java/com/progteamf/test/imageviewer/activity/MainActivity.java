package com.progteamf.test.imageviewer.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.progteamf.test.imageviewer.R;
import com.progteamf.test.imageviewer.controller.DownloadTaskController;
import com.progteamf.test.imageviewer.db.ImageDAO;
import com.progteamf.test.imageviewer.model.Image;
import com.progteamf.test.imageviewer.model.Status;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {

    private static boolean REALM_ISNT_INIT = true;
    private static final String MAIN_TAG = "MainActivity_log";

    private static final String LINK_TAG = "link_tag";
    private static final String APP_A_URL_TAG = "app_a_url_tag";
    /**
     * Attributes for displaying image
     */
    private ImageView img;
    private Bitmap bitmap;
    private String url;
//    private static final String pathName = "/BIGDIG/Test";

    /**
     * Attributes for auto-closing
     */
    private CountDownTimer countDownTimer;
    private long leftTimeInMilliseconds = 11000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("MAIN", "onCreate");

        while (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            getPermissionForExternalStorage();
        }
        if (REALM_ISNT_INIT) {
            try {
                initRealm();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
            } catch (IllegalArgumentException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
            }
        }

        Intent intentFromAppA = getIntent();
        if (!isIntentFromA(intentFromAppA)) {
            showClosingDialog();
        } else {
            url = intentFromAppA.getStringExtra(LINK_TAG);

            if (isConnectingToInternet()) {
                //==============Download and set Image to the ImageView=================================
                img = findViewById(R.id.imageView);
                new GetImageFromURL(img).execute(url);


                //Starts the download of image to device's External Storage at AsyncTask
                new DownloadTaskController(MainActivity.this, url);
            } else {
                Toast.makeText(getApplicationContext(), "There isn't internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initRealm() throws IOException {
        REALM_ISNT_INIT = false;
        Log.e(MAIN_TAG, "realm initialization.");
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .directory(new File(Environment.getExternalStorageDirectory() + "/BIGDIG/test/realm"))
                .name("progTeamF.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }

    //=================Downloading File From The Internet==================================
    //Class used to download the image by URL
    public class GetImageFromURL extends AsyncTask<String, Void, Bitmap> {
        ImageView imgV;

        public GetImageFromURL(ImageView imgV) {
            this.imgV = imgV;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            String urldisplay = url[0];
            bitmap = null;
            InputStream srt;
            try {
                srt = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(srt);
                srt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imgV.setImageBitmap(bitmap);
        }
    }

    public void showClosingDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View root = inflater.inflate(R.layout.layout_closing_dialog, null);
        final TextView sec = root.findViewById(R.id.seconds);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(root);
        adb.setPositiveButton("Close right now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                countDownTimer.onFinish();
            }
        });
        final AlertDialog alertDialog = adb.create();

        alertDialog.show();
        countDownTimer = new CountDownTimer(leftTimeInMilliseconds, 1000) {
            @Override
            public void onTick(long l) {
                leftTimeInMilliseconds = l;
                int seconds = (int) leftTimeInMilliseconds / 1000;
                sec.setText("" + seconds + " ");
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();
                alertDialog.dismiss();
                MainActivity.this.finishAffinity();
            }
        }.start();
    }

    //Check if internet is present or not
    private boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private boolean isIntentFromA(Intent intent) {

        Set<String> ss = intent.getCategories();
        for (String temp : ss) {
            if (temp.equals(APP_A_URL_TAG)) return true;
        }
        return false;

    }

    private void getPermissionForExternalStorage() {
        do {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {


                // You can show your dialog message here but instead I am
                // showing the grant permission dialog box
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        10);


            } else {

                //Requesting permission
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        10);

            }
        }
        while (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
    }

}
