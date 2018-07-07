package com.progteamf.test.imageviewer.db.model;

import java.util.Objects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.RealmField;
import io.realm.annotations.Required;

@RealmClass
public class ImageRealmObject extends RealmObject{

    @PrimaryKey
    @RealmField(name = "id")
    private String id;

    @Required
    private String link;

    @Required
    private String time;

    private int statusId;

    private String statusMessage;

    public ImageRealmObject() {
    }

    public ImageRealmObject(String id, String link, String time, int statusId, String statusMessage) {

        this.id = id;
        this.link = link;
        this.time = time;
        this.statusId = statusId;
        this.statusMessage = statusMessage;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageRealmObject that = (ImageRealmObject) o;
        return Objects.equals(id, that.id) &&
                statusId == that.statusId &&
                Objects.equals(link, that.link) &&
                Objects.equals(time, that.time) &&
                Objects.equals(statusMessage, that.statusMessage);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, link, time, statusId, statusMessage);
    }
}
