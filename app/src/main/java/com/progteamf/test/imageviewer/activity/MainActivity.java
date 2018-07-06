package com.progteamf.test.imageviewer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.progteamf.test.imageviewer.R;

import java.io.InputStream;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
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

        Intent intentFromAppA = getIntent();
        if (!isIntentFromA(intentFromAppA)) {
            showClosingDialog();
        } else {
            url = intentFromAppA.getStringExtra(LINK_TAG);
            //==============Download and set Image to the ImageView=================================
            img = findViewById(R.id.imageView);
            new GetImageFromURL(img).execute(url);
        }


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

    private boolean isIntentFromA(Intent intent) {

        Set<String> ss = intent.getCategories();
        for (String temp : ss) {
            if (temp.equals(APP_A_URL_TAG)) return true;
        }
        return false;

    }
}
