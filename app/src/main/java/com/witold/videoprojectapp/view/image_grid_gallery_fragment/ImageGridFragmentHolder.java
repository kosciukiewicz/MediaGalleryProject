package com.witold.videoprojectapp.view.image_grid_gallery_fragment;

import android.widget.ImageView;

import com.witold.videoprojectapp.model.image.Image;

public interface ImageGridFragmentHolder {
    public void showVideoRecorderActivity(int mode);
    public void onImageClick(Image image,  int position, ImageView imageView, String transitionName);
}
