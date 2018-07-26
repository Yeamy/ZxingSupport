package com.yeamy.support.zxing;

public class Size {
    public int width;
    public int height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void rotate() {
        int tmp = width;
        width = height;
        height = tmp;
    }
}
