package com.progteamf.test.imageviewer.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.progteamf.test.imageviewer.R;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private ImageView img;
    private Bitmap bitmap;
    private String url = "https://www.file-extensions.org/imgs/app-icon/128/11151/android-studio-icon.png";
//    private static final String pathName = "/BIGDIG/Test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //==============Download and set Image to the ImageView=================================
        img = findViewById(R.id.imageView);
        new GetImageFromURL(img).execute(url);
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
}
