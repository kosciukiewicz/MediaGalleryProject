package com.witold.videoprojectapp.database.image_entity;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ImageDao {
    @Query("SELECT * FROM " + ImageEntity.TABLE_NAME + " ORDER BY dateDone DESC")
    Single<List<ImageEntity>> getAllImages();

    @Insert
    public abstract Long insertImage(ImageEntity imageEntity);

    @Delete
    public abstract void deleteImage(ImageEntity image);
}
