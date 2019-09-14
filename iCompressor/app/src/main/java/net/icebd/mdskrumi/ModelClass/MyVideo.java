package net.icebd.mdskrumi.ModelClass;

import java.io.Serializable;

public class MyVideo implements Serializable {
    private String title;
    private String data;
    private String duration;
    private long size;

    public MyVideo(String title, String data, String duration, long size) {
        this.title = title;
        this.data = data;
        this.duration = duration;
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
