package net.icebd.mdskrumi.ModelClass;

import java.io.Serializable;

public class MyImage implements Serializable {
    private String data;
    private String takenDate;
    private long size;

    public MyImage(String data, String takenDate, long size) {
        this.data = data;
        this.takenDate = takenDate;
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTakenDate() {
        return takenDate;
    }

    public void setTakenDate(String takenDate) {
        this.takenDate = takenDate;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}

