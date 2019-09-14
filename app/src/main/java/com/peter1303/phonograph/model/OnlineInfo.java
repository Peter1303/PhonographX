package com.peter1303.phonograph.model;

import androidx.annotation.NonNull;

public class OnlineInfo {
    /**
     * songid : 436487129
     * title : Move Your Body (Alan Walker Remix)
     * author : Sia,Alan Walker
     * url : http://music.163.com/song/media/outer/url?id=436487129.mp3
     * pic : http://p1.music.126.net/9h_TA43qbHLTKwQrQzhLgQ==/18162832579997237.jpg?param=300x300
     */

    private int songid;
    private String title;
    private String author;
    private String url;
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
    /*
    public String getUrl() {
        return url;
    }
    */

    /*
    public void setUrl(String url) {
        this.url = url;
    }
    */

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
        return "OnlineInfo {" +
                "songid=" + songid +
                ", title='" + title + "'" +
                ", author='" + author + "'" +
                ", url='" + url + "'" +
                ", pic='" + pic + "'" +
                "}";
    }
}
