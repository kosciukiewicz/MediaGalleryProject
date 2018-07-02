package com.witold.videoprojectapp.database.video_entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = VideoEntity.TABLE_NAME)
public class VideoEntity {
    public static final String TABLE_NAME = "Videos";

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private Date dateDone;
    private String fileName;
    private Long length;

    public VideoEntity(int id, String title, String description, Date dateDone, String fileName, Long length) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateDone = dateDone;
        this.fileName = fileName;
        this.length = length;
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

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }
}
