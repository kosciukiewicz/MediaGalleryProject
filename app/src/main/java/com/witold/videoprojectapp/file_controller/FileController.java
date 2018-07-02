package com.witold.videoprojectapp.file_controller;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import io.reactivex.Completable;
import io.reactivex.functions.Action;
import timber.log.Timber;

public class FileController {
    private Context context;

    public FileController(Context context) {
        this.context = context;
    }

    public Completable deleteVideoFile(final String filename) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                context.getContentResolver().delete(getVdeoUriFromPath(filename), null, null);
            }
        });
    }

    public Completable deleteImageFile(final String filename) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                context.getContentResolver().delete(getImageUriFromPath(filename), null, null);
            }
        });
    }

    public Long getVideoLength(String filename) {
        File file = new File(filename);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, Uri.fromFile(file));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMilisec = Long.parseLong(time);
        return timeInMilisec;
    }

    public Long getFileSize(String filename) {
        File file = new File(filename);
        return file.length();
    }

    private Uri getVdeoUriFromPath(String path) {
        long videoId;
        Uri videoUri = MediaStore.Video.Media.getContentUri("external");
        String[] projection = {MediaStore.Video.Media._ID};
        Cursor cursor = context.getContentResolver().query(videoUri, projection, MediaStore.Video.Media.DATA + " LIKE ?", new String[]{path}, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(projection[0]);
        videoId = cursor.getLong(columnIndex);
        cursor.close();
        return ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoId);
    }

    private Uri getImageUriFromPath(String path) {
        long imageUri;
        Uri videoUri = MediaStore.Images.Media.getContentUri("external");
        String[] projection = {MediaStore.Images.Media._ID};
        Cursor cursor = context.getContentResolver().query(videoUri, projection, MediaStore.Images.Media.DATA + " LIKE ?", new String[]{path}, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(projection[0]);
        imageUri = cursor.getLong(columnIndex);
        cursor.close();
        return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageUri);
    }
}
