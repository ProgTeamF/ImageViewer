package com.progteamf.test.imageviewer.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;
import com.progteamf.test.imageviewer.R;
import com.progteamf.test.imageviewer.controller.DownloadTaskController;
import com.progteamf.test.imageviewer.db.ImageDAO;
import com.progteamf.test.imageviewer.model.Image;
import com.progteamf.test.imageviewer.model.Status;
import com.progteamf.test.imageviewer.service.DeleteLinkService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {

    private final String HISTORY_TAG = "history_tab";

    private Image image;

    private final String ID_TAG = "id_image";
    private final String STATUS_ID_TAG = "status_id_image";
    private final String STATUS_MESSAGE_TAG = "status_message_image";
    private final String DATE_TAG = "data_image";

    private static boolean REALM_ISNT_INIT = true;
    private static final String MAIN_TAG = "MainActivity_log";

    private static final String LINK_TAG = "link_tag";
    private static final String APP_A_URL_TAG = "app_a_url_tag";

    private boolean IS_FROM_APP_A = false;
    private boolean IS_FROM_HISTORY_TAB = false;


    private boolean IMAGE_IS_DOWLOADED = false;
    /**
     * Attributes for displaying image
     */
    private ImageView img;
    private Bitmap bitmap;
    private String url;
    private TextView statusText;
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
        isIntentFromA(intentFromAppA);


        if (IS_FROM_HISTORY_TAB) {
            IS_FROM_HISTORY_TAB = false;
            image = new Image();
            image.setId(intentFromAppA.getStringExtra(ID_TAG));
            image.setLink(intentFromAppA.getStringExtra(LINK_TAG));
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm");
            Date d = null;
            try {
                d = dateFormat.parse(intentFromAppA.getStringExtra(DATE_TAG));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(d.getTime());
            image.setTime(gc);
            Status status = null;
            switch (intentFromAppA.getIntExtra(STATUS_ID_TAG, 3)) {
                case 1:
                    status = Status.DOWNLOADED;
                    break;
                case 2:
                    status = Status.ERROR;
                    break;
                case 3:
                    status = Status.UNKNOWN;
                    break;
            }
            status.setMessage(intentFromAppA.getStringExtra(STATUS_MESSAGE_TAG));
            image.setStatus(status);

            System.out.println(image.toString());


            if (isConnectingToInternet()) {
                //==============Download and set Image to the ImageView=================================
                img = findViewById(R.id.imageView);
                statusText = findViewById(R.id.statusView);
                statusText.setText(image.getStatus().getMessage());
                new GetImageFromURL(img, statusText).execute(image.getLink());


                if (image.getStatus().equals(Status.DOWNLOADED)) {
                    //Starts the download of image to device's External Storage at AsyncTask
                    new DownloadTaskController(MainActivity.this, image.getLink());
                    System.out.println("Downloaded");
                    System.out.println("Downloaded");
                    System.out.println("Downloaded");


                    SharedPreferences mPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(image);
                    prefsEditor.putString("image", json);
                    prefsEditor.commit();

                    Intent deleteService = new Intent(this, DeleteLinkService.class);
                    startService(deleteService);
                }

            } else {
                Toast.makeText(getApplicationContext(), "There isn't internet connection", Toast.LENGTH_LONG).show();
            }

        } else if (IS_FROM_APP_A) {
            url = intentFromAppA.getStringExtra(LINK_TAG);

            //adds image to db
            image= new Image();
            image.setLink(url);
            image.setStatus(Status.UNKNOWN);
            image.setTime(new GregorianCalendar());
            new ImageDAO().create(image);


            if (isConnectingToInternet()) {
                //==============Download and set Image to the ImageView=================================
                statusText = findViewById(R.id.statusView);
                img = findViewById(R.id.imageView);
                new GetImageFromURL(img, statusText).execute(url);
            } else {
                Toast.makeText(getApplicationContext(), "There isn't internet connection", Toast.LENGTH_LONG).show();
            }
        } else {
            showClosingDialog();
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
        TextView statusView;

        public GetImageFromURL(ImageView imgV, TextView statusView) {
            this.imgV = imgV;
            this.statusView = statusView;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            String urldisplay = url[0];
            bitmap = null;
            InputStream srt;
            try {
                URL link = new URL(urldisplay);
                HttpURLConnection urlConn = (HttpURLConnection) link.openConnection();
                urlConn.connect();


                srt = link.openStream();
                bitmap = BitmapFactory.decodeStream(srt);
                srt.close();
                IMAGE_IS_DOWLOADED = true;
                if (!IS_FROM_APP_A | IS_FROM_HISTORY_TAB) {
                    image.setStatus(com.progteamf.test.imageviewer.model.Status.DOWNLOADED);
                    new ImageDAO().update(image);
                }
            } catch (IOException e) {
                if (!IS_FROM_APP_A | IS_FROM_HISTORY_TAB) {
                    com.progteamf.test.imageviewer.model.Status s = com.progteamf.test.imageviewer.model.Status.ERROR;
                    s.setMessage(e.getMessage());
                    image.setStatus(s);
                    new ImageDAO().update(image);
                }
            }


            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            switch (image.getStatus()){
                case DOWNLOADED:
                    statusView.setText("--- Downloaded ---");
                    break;
                case ERROR:
                    statusView.setText("--- Error ---\n"+image.getStatus().getMessage());
                case UNKNOWN:
                    statusView.setText("--- Unknown ---");
                    break;
            }
            imgV.setImageBitmap(bitmap);

        }
    }

    public void showClosingDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View root = inflater.inflate(R.layout.layout_closing_dialog, null);
        final TextView sec = root.findViewById(R.id.textView);
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
                StringBuilder message = new StringBuilder(getResources().getText(R.string.launcher_msg));
                Formatter f = new Formatter();
                sec.setText(f.format(message.toString(),seconds).toString());
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

    private void isIntentFromA(Intent intent) {

        Set<String> ss = intent.getCategories();
        for (String temp : ss) {
            if (temp.equals(APP_A_URL_TAG)) IS_FROM_APP_A = true;
            if (temp.equals(HISTORY_TAG)) IS_FROM_HISTORY_TAB = true;
        }

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
