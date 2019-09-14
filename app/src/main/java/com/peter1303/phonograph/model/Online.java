package com.peter1303.phonograph.model;

import java.util.List;

public class Online {
    /**
     * data : [{"songid":436487129,"title":"Move Your Body (Alan Walker Remix)","author":"Sia,Alan Walker","url":"http://music.163.com/song/media/outer/url?id=436487129.mp3","pic":"http://p1.music.126.net/9h_TA43qbHLTKwQrQzhLgQ==/18162832579997237.jpg?param=300x300"},{"songid":1331514800,"title":"Move Your Body（抖音改版）","author":"沐泽","url":"http://music.163.com/song/media/outer/url?id=1331514800.mp3","pic":"http://p1.music.126.net/W3IuavAhIPIeBZ6WzDAnXg==/109951163793076538.jpg?param=300x300"},{"songid":401382522,"title":"Move Your Body","author":"Sia","url":"http://music.163.com/song/media/outer/url?id=401382522.mp3","pic":"http://p1.music.126.net/fDUMN_6ITc4gvoDko06uKw==/18176026719076230.jpg?param=300x300"},{"songid":451520789,"title":"Move Your Body (Single Mix)","author":"Sia","url":"http://music.163.com/song/media/outer/url?id=451520789.mp3","pic":"http://p1.music.126.net/-buMweTkmGOzgftLixWX0w==/18545462627395444.jpg?param=300x300"},{"songid":1321428711,"title":"Move Your Body(Trunk Remix)","author":"TRUNK,Sia,Alan Walker","url":"http://music.163.com/song/media/outer/url?id=1321428711.mp3","pic":"http://p1.music.126.net/UEsGJCybWBTTZmLIhB---A==/109951163957083967.jpg?param=300x300"},{"songid":5265473,"title":"Move Your Body","author":"Eiffel 65","url":"http://music.163.com/song/media/outer/url?id=5265473.mp3","pic":"http://p1.music.126.net/S3bLk4Gpic3fEjAtFMnWCw==/64871186055831.jpg?param=300x300"},{"songid":436487117,"title":"Move Your Body","author":"Sia","url":"http://music.163.com/song/media/outer/url?id=436487117.mp3","pic":"http://p1.music.126.net/9h_TA43qbHLTKwQrQzhLgQ==/18162832579997237.jpg?param=300x300"},{"songid":512827004,"title":"Move Your Body","author":"Lana Grace","url":"http://music.163.com/song/media/outer/url?id=512827004.mp3","pic":"http://p1.music.126.net/yHu08fsm5fMamBCOhmJjEQ==/17861566393618619.jpg?param=300x300"},{"songid":513643899,"title":"Move Your Body","author":"Training Online","url":"http://music.163.com/song/media/outer/url?id=513643899.mp3","pic":"http://p1.music.126.net/HJ2iteMZ57MP2g7ymF_0GQ==/18856624416472109.jpg?param=300x300"},{"songid":1321696,"title":"Move Your Body","author":"Eiffel 65","url":"http://music.163.com/song/media/outer/url?id=1321696.mp3","pic":"http://p1.music.126.net/kmQ9osJc_ebo1l7uQw0vWw==/6628955604064549.jpg?param=300x300"}]
     * code : 200
     */

    private int code;
    private List<OnlineInfo> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<OnlineInfo> getData() {
        return data;
    }

    public void setData(List<OnlineInfo> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Online {" +
                "code=" + code +
                ", data='" + getData().toString() + '\'' +
                '}';
    }
}
