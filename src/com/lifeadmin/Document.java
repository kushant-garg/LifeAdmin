package com.lifeadmin;

public class Document {
    private int id;
    private String title;
    private String filePath;
    private String uploadDate;

    public Document(int id, String title, String filePath, String uploadDate) {
        this.id = id;
        this.title = title;
        this.filePath = filePath;
        this.uploadDate = uploadDate;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getFilePath() { return filePath; }
    public String getUploadDate() { return uploadDate; }
}