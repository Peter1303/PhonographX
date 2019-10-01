/*
 * Peter1303
 * Copyright (c) 2019.
 */

package com.peter1303.phonograph.model.online.lyric;

import androidx.annotation.NonNull;

public class OlLyricInfo {
    private int songid;
    private String title;
    private String author;
    private String lrc;
    private String pic;

    public int getSongid() {
        return songid;
    }

    /*
    public void setSongid(int songid) {
        this.songid = songid;
    }
     */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    public String getPic() {
        return pic;
    }

    /*
    public void setPic(String pic) {
        this.pic = pic;
    }
     */

    @NonNull
    @Override
    public String toString() {
        return "OlLyricInfo {" +
                "songid=" + songid +
                ", title='" + title + "'" +
                ", author='" + author + "'" +
                ", lrc='" + lrc + "'" +
                ", pic='" + pic + "'" +
                "}";
    }
}
