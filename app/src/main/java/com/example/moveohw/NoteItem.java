package com.example.moveohw;

import android.widget.Button;

import java.util.Date;

public class NoteItem {

    String NoteText;
    Date date;
    String datetxt;
    String id;
    Double latitude;
    Double longitude;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setAltitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDatetxt() {
        return datetxt;
    }

    public void setDatetxt(String datetxt) {
        this.datetxt = datetxt;
    }

    String title;

    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
    }

    public NoteItem( String noteText, Date date, String title,Double latitude,Double longitude) {

        this.NoteText = noteText;
        this.date = date;
        this.title=title;
        this.latitude=latitude;
        this.longitude=longitude;
    }





    public String getNoteText() {
        return NoteText;
    }

    public void setNoteText(String noteText) {
        NoteText = noteText;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}