package com.progteamf.test.imageviewer.model;

/**
 * Created by N.Babiy on 7/5/2018.
 */

public enum Status {

    DOWNLOADED(1), ERROR(2), UNKNOWN(3);

    private int id;
    private String message;

    Status(int id) {
        this.id = id;

    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Status{" +
                "id=" + id +
                ", message='" + message + '\'' +
                '}';
    }
}
