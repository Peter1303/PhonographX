package com.peter1303.phonograph.model;

public class Purchase {

    public int code = 0;
    public int msg = 0;

    public int getCode() {
        return code;
    }

    public boolean isPurchased() {
        return msg == 1;
    }
}
