package com.witold.videoprojectapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.witold.videoprojectapp.database.converters.DateTimeConverter;
import com.witold.videoprojectapp.database.image_entity.ImageDao;
import com.witold.videoprojectapp.database.image_entity.ImageEntity;
import com.witold.videoprojectapp.database.video_entity.VideoDao;
import com.witold.videoprojectapp.database.video_entity.VideoEntity;

@Database(entities = {ImageEntity.class, VideoEntity.class}, version = 1)
@TypeConverters({DateTimeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "VideoProjectAppDatabase";
    public abstract ImageDao imageDao();
    public abstract VideoDao videoDao();
}
