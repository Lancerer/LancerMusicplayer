package com.example.lancer.lancermusicplayer.bean;

import android.widget.ImageView;

/**
 * Created by Lancer on 2018/4/2.
 */

public class Info {
    private String title;
    private String artist;
    private String duration;
    private String Url;
    private int abulm_id;

    public int getAbulm_id() {
        return abulm_id;
    }

    public void setAbulm_id(int abulm_id) {
        this.abulm_id = abulm_id;
    }
    /*    private long size;*/

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
    /*public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }*/
}
