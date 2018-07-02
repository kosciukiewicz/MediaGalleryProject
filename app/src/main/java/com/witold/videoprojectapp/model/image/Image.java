package com.witold.videoprojectapp.model.image;

import java.util.Date;

public class Image {
    private int id;
    private String title;
    private String description;
    private Date dateDone;
    private String fileName;
    private Long size;

    public Image(){
        this.id = 0;
    }

    public Image(int id, String title, String description, Date dateDone, String fileName, Long size) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateDone = dateDone;
        this.fileName = fileName;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateDone() {
        return dateDone;
    }

    public void setDateDone(Date dateDone) {
        this.dateDone = dateDone;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object obj) {
        return this.id == ((Image)obj).id;
    }
}
