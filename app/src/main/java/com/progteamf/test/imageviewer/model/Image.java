package com.progteamf.test.imageviewer.model;

import java.util.GregorianCalendar;
import java.util.Objects;

public class Image {

    private int id;
    private String link;
    private Status status;
    private GregorianCalendar time;

    public Image() {
    }

    public Image(int id, String link, Status status, GregorianCalendar time) {
        this.id = id;
        this.link = link;
        this.status = status;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public GregorianCalendar getTime() {
        return time;
    }

    public void setTime(GregorianCalendar time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", link='" + link + '\'' +
                ", status=" + status +
                ", time='" + time + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return id == image.id &&
                Objects.equals(link, image.link) &&
                status == image.status &&
                Objects.equals(time, image.time);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, link, status, time);
    }
}
