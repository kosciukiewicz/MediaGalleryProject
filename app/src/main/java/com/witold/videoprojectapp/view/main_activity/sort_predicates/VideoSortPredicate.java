package com.witold.videoprojectapp.view.main_activity.sort_predicates;

import com.witold.videoprojectapp.model.video.Video;

import java.io.Serializable;

public class VideoSortPredicate implements Serializable{
    public static final int MODE_TITLE = 123;
    public static final int MODE_DATE_DONE = 144;
    public static final int MODE_LENGTH = 142;
    public static final int ORDER_ASC = 145;
    public static final int ORDER_DESC = 147;

    private int mode;
    private int order;

    public VideoSortPredicate() {
        mode = MODE_DATE_DONE;
        order = ORDER_DESC;
    }

    public int compare(Video video1, Video video2){
        if(order == ORDER_DESC){
            return -compareMode(video1, video2);
        }else {
            return compareMode(video1, video2);
        }
    }

    private int compareMode(Video video1, Video video2){
        int result = 0;
        switch (mode) {
            case VideoSortPredicate.MODE_DATE_DONE:
                result = video1.getDateDone().compareTo(video2.getDateDone());
                break;
            case VideoSortPredicate.MODE_LENGTH:
                result = video1.getLength().compareTo(video2.getLength());
                break;
            case VideoSortPredicate.MODE_TITLE:
                result = video1.getTitle().compareTo(video2.getTitle());
                break;
        }
        return result;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
