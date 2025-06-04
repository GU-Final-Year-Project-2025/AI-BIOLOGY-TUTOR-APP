package com.example.aibiotutor;

public class VideoItem {
    private String title;
    private int rawResId;

    public VideoItem(String title, int rawResId) {
        this.title = title;
        this.rawResId = rawResId;
    }

    public String getTitle() { return title; }
    public int getRawResId() { return rawResId; }
}

