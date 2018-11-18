package fr.polytech.business;


import java.io.Serializable;

public class DownloadRequest implements Serializable {

    private long userId;
    private String fileId;

    public DownloadRequest(long userId, String fileId){
        this.userId = userId;
        this.fileId = fileId;
    }

    public long getUserId() {
        return userId;
    }

    public String getFileId() {
        return fileId;
    }
}
