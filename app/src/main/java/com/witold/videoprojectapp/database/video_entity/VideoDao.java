package com.witold.videoprojectapp.database.video_entity;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.witold.videoprojectapp.model.video.Video;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface VideoDao {
    @Query("SELECT * FROM " + VideoEntity.TABLE_NAME + " ORDER BY dateDone DESC")
    Single<List<VideoEntity>> getAllVideos();

    @Insert
    public abstract Long insertVideo(VideoEntity videoEntity);

    @Delete
    public abstract void deleteVideo(VideoEntity video);
}
