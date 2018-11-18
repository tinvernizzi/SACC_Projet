package fr.polytech.business;


import java.io.Serializable;

public class DownloadRequest implements Serializable {

    private int userId;
    private String fileId;

    public DownloadRequest(int userId, String fileId){
        this.userId = userId;
        this.fileId = fileId;
    }

    public int getUserId() {
        return userId;
    }

    public String getFileId() {
        return fileId;
    }
}
