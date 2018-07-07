package com.progteamf.test.imageviewer.db;

import com.progteamf.test.imageviewer.db.core.CRUD;
import com.progteamf.test.imageviewer.db.model.ImageRealmObject;
import com.progteamf.test.imageviewer.model.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

public class ImageDAO implements CRUD<Image> {

    private Realm mRealm;

    private void openConnection() {
        mRealm = Realm.getDefaultInstance();
    }

    private void closeConnection() {
        mRealm.close();
    }

    @Override
    public void create(Image image) {
        openConnection();
        mRealm.beginTransaction();
        ImageRealmObject iro = mRealm.createObject(ImageRealmObject.class, UUID.randomUUID().toString());
        ImageHelper.setFromImage(iro, image);
        mRealm.commitTransaction();
        closeConnection();
    }

    @Override
    public Image read(String id) {
        openConnection();
        Image image = ImageHelper.convertFromImageRealmObject(
                mRealm.where(ImageRealmObject.class).equalTo("id", id).findFirst());
        closeConnection();
        return image;
    }

    @Override
    public List<Image> readAll() {
        openConnection();
        List<Image> images = new ArrayList<>();
        RealmResults<ImageRealmObject> realmResults = mRealm.where(ImageRealmObject.class).findAll();
        for (ImageRealmObject iro : realmResults) {
            images.add(ImageHelper.convertFromImageRealmObject(iro));
        }
        closeConnection();
        return images;
    }

    @Override
    public void update(Image image) {
        openConnection();
        ImageRealmObject iro = new ImageRealmObject();
        ImageHelper.setFromImage(iro, image);
        iro.setId(image.getId());
        mRealm.beginTransaction();
        mRealm.insertOrUpdate(iro);
        mRealm.commitTransaction();
        closeConnection();
    }

    @Override
    public void delete(String  id) {
        openConnection();
        mRealm.beginTransaction();
        mRealm.where(ImageRealmObject.class).equalTo("id", id).findFirst().deleteFromRealm();
        mRealm.commitTransaction();
        closeConnection();
    }


}
