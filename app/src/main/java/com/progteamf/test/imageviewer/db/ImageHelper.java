package com.progteamf.test.imageviewer.db;

import com.progteamf.test.imageviewer.db.model.ImageRealmObject;
import com.progteamf.test.imageviewer.model.Image;
import com.progteamf.test.imageviewer.model.Status;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class ImageHelper {

    public static void setFromImage(ImageRealmObject iro, Image image){
        iro.setLink(image.getLink());
        iro.setTime(new SimpleDateFormat("dd/MM/yyyy kk:mm").format(image.getTime().getTime()));
        iro.setStatusId(image.getStatus().getId());
    }

    public static Image convertFromImageRealmObject(ImageRealmObject iro){
        Status status = null;
        switch (iro.getStatusId()){
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
        if (status!=null) status.setMessage(iro.getStatusMessage());

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm");
        Date d = null;
        try {
            d = dateFormat.parse(iro.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(d.getTime());

        return new Image(iro.getId(),
                iro.getLink(),
                status,
                gc);
    }
}
