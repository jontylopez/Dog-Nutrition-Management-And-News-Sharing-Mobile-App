package com.example.woof;

public class News {
    private int id;
    private String nDescription;
    private byte[] nImage;

    public News(int id, String nDescription, byte[] nImage) {
        this.id = id;
        this.nDescription = nDescription;
        this.nImage = nImage;
    }

    public int getId() {
        return id;
    }

    public String getnDescription() {
        return nDescription;
    }

    public byte[] getnImage() {
        return nImage;
    }
}
