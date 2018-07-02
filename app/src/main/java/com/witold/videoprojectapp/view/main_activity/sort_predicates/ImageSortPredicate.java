package com.witold.videoprojectapp.view.main_activity.sort_predicates;

import com.witold.videoprojectapp.model.image.Image;
import com.witold.videoprojectapp.model.video.Video;

import java.io.Serializable;

import timber.log.Timber;

public class ImageSortPredicate implements Serializable{
    public static final int MODE_TITLE = 123;
    public static final int MODE_DATE_DONE = 144;
    public static final int MODE_SIZE = 142;
    public static final int ORDER_ASC = 145;
    public static final int ORDER_DESC = 147;

    private int mode;
    private int order;

    public ImageSortPredicate() {
        mode = MODE_DATE_DONE;
        order = ORDER_DESC;
    }

    public int compare(Image image1, Image image2){
        Timber.d(order + "");
        if(order == ORDER_DESC){
            return -compareMode(image1, image2);
        }else {
            return compareMode(image1, image2);
        }
    }

    private int compareMode(Image image1, Image image2){
        int result = 0;
        switch (mode) {
            case ImageSortPredicate.MODE_DATE_DONE:
                result = image1.getDateDone().compareTo(image2.getDateDone());
                break;
            case ImageSortPredicate.MODE_SIZE:
                result = image1.getSize().compareTo(image2.getSize());
                break;
            case ImageSortPredicate.MODE_TITLE:
                result = image1.getTitle().compareTo(image2.getTitle());
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
