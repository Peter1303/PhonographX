/*
 * Peter1303
 * Copyright (c) 2019.
 */

package com.peter1303.phonograph.model.online.lyric;

import java.util.List;

public class OlLyric {
    /**
     * data : [{"songid":436487129,"title":"Move Your Body (Alan Walker Remix)","author":"Sia,Alan Walker","lrc":"[by:NickyRomero]\n[00:00.000] 作曲 : Sia Furler/Greg Kurstin\n[00:00.026] 作词 : Sia Furler/Greg Kurstin\n[00:00.80]编曲：Alan Walker/Greg Kurstin/Anders Froen\n[00:01.59]Oh-oh-oh, oh-oh-oh, oh, oh\n[00:06.63]Oh-oh-oh, oh-oh-oh, oh, oh\n[00:09.76]Poetry in your body\n[00:11.70]You got it in every way\n[00:17.18]And can't you see it's you I'm watching?\n[00:19.17]I am hot for you in every way\n[00:24.63]And turn around, let me see you\n[00:26.69]Wanna free you with my rhythm\n[00:32.20]I know you can't get enough\n[00:34.08]When I turn up with my rhythm\n[00:36.85]\n[00:39.20]Your body\u2019s poetry, speak to me\n[00:41.71]Won\u2019t you let me be your rhythm tonight?\n[00:44.48](Move your body, move your body)\n[00:46.96]I wanna be your muse,use my music\n[00:49.52]And let me be your rhythm tonight\n[00:52.00](Move your body, move your body)\n[00:54.20]Your body\u2019s poetry speak to me\n[00:56.73]Won\u2019t you let me be your rhythm tonight?\n[00:59.55](Move your body, move your body)\n[01:01.88]I wanna be your muse,use my music\n[01:04.28]And let me be your rhythm tonight\n[01:07.03](Move your body, move your body)\n[01:10.26]\n[01:15.99]Move your, move your body\n[01:23.56]Move your, move your body\n[01:25.83]Move your body, dy, dy\n[01:29.52]Move your body\n[01:33.33]Move your body, dy, dy\n[01:37.01]Move your body\n[01:39.39]\n[01:41.79]Poetry in your body\n[01:43.62]Got me started\n[01:44.56]May it never end\n[01:49.24]Feel my rhythm in your system\n[01:51.12]This is living, I'm your only friend\n[01:56.66]Feel the beat in your chest\n[01:58.70]Beat your chest like an animal\n[02:04.02]Free the beast from it's cage\n[02:06.09]Free the rage like an animal\n[02:09.59]\n[02:12.96]Your body\u2019s poetry, speak to me\n[02:15.44]Won\u2019t you let me be your rhythm tonight?\n[02:18.21](Move your body, move your body)\n[02:20.48]I wanna be your muse,use my music\n[02:22.99]And let me be your rhythm tonight\n[02:25.73](Move your body, move your body)\n[02:28.00]Your body\u2019s poetry speak to me\n[02:30.46]Won\u2019t you let me be your rhythm tonight?\n[02:33.20](Move your body, move your body)\n[02:35.35]I wanna be your muse,use my music\n[02:38.01]And let me be your rhythm tonight\n[02:40.73](Move your body, move your body)\n[02:44.12]\n[02:49.69]Move your, move your body\n[02:57.11]Move your, move your body\n[02:59.64]Move your body, dy, dy\n[03:03.24]Move your body\n[03:07.01]Move your body, dy, dy\n[03:10.69]Move your body\n[03:12.47]\n[03:14.03]Oh-oh-oh, oh-oh-oh, oh, oh\n[03:17.79]Oh-oh-oh, oh-oh-oh, oh, oh\n[03:21.50]Oh-oh-oh, oh-oh-oh, oh, oh\n[03:25.21]Oh-oh-oh, oh-oh-oh, oh, oh\n[03:27.83]Move your body\n","pic":"http://p1.music.126.net/9h_TA43qbHLTKwQrQzhLgQ==/18162832579997237.jpg"}]
     * code : 200
     */

    private int code;
    private List<OlLyricInfo> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<OlLyricInfo> getData() {
        return data;
    }

    public void setData(List<OlLyricInfo> data) {
        this.data = data;
    }
}
