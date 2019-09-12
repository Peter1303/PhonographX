package com.peter1303.phonograph.model;

import java.util.List;

public class Music {
    /**
     * data : [{"songid":436487129,"title":"Move Your Body (Alan Walker Remix)"}]
     * code : 200
     * error :
     */

    private int code;
    private String error;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
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

        public void setSongid(int songid) {
            this.songid = songid;
        }

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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        @Override
        public String toString() {
            return "DataBean {" +
                    "songid=" + songid +
                    ", title='" + title + '\'' +
                    ", author='" + author + '\'' +
                    ", url='" + url + '\'' +
                    ", pic='" + pic + '\'' +
                    '}';
        }
    }

    public class Lyric {
        /**
         * data : [by:NickyRomero]
         [00:00.000] 作曲 : Sia Furler/Greg Kurstin
         [00:00.026] 作词 : Sia Furler/Greg Kurstin
         * code : 200
         * error :
         */

        private String data;
        private int code;
        private String error;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        @Override
        public String toString() {
            return "Lyric {" +
                    "code=" + code +
                    ", data='" + getData().toString() + '\'' +
                    ", error='" + error + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Music {" +
                "code=" + code +
                ", data='" + getData().toString() + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
