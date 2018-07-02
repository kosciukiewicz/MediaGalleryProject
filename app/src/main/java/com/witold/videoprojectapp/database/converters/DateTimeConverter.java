package com.witold.videoprojectapp.database.converters;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;

import java.util.Date;

public class DateTimeConverter {
    @TypeConverter
    public static Date toDate(Long value){
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date value){
        return value == null ? null : value.getTime();
    }
}
