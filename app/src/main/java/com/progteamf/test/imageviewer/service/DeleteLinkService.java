package com.progteamf.test.imageviewer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.progteamf.test.imageviewer.db.ImageDAO;
import com.progteamf.test.imageviewer.model.Image;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * Created by N.Babiy on 7/9/2018.
 */

public class DeleteLinkService extends Service {
    private final LocalBinder mBinder = new LocalBinder();
    protected Handler handler;
    protected Toast mToast;

    public class LocalBinder extends Binder {
        public DeleteLinkService getService() {
            return DeleteLinkService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {



// write your code to post content on server
                SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = mPrefs.getString("image", "");
                Image image = gson.fromJson(json, Image.class);
                initRealm();
                if (new ImageDAO().read(image.getId())!=null) new ImageDAO().delete(image.getId());
                Toast.makeText(getApplicationContext(), "Link \n'"+image.getLink() + "'\nwas deleted", Toast.LENGTH_LONG).show();
                onDestroy();


            }
        }, 15000);
        return android.app.Service.START_STICKY;
    }



    private void initRealm()  {
        Log.e("Service", "realm initialization.");
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .directory(new File(Environment.getExternalStorageDirectory() + "/BIGDIG/test/realm"))
                .name("progTeamF.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
