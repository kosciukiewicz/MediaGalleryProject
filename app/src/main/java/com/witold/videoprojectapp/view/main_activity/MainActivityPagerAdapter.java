package com.witold.videoprojectapp.view.main_activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.witold.videoprojectapp.model.video.Video;
import com.witold.videoprojectapp.view.image_grid_gallery_fragment.ImageGridFragmentHolder;
import com.witold.videoprojectapp.view.image_grid_gallery_fragment.ImageGridGalleryFragment;
import com.witold.videoprojectapp.view.video_list_fragment.VideoListFragment;

import java.util.List;

import timber.log.Timber;

public class MainActivityPagerAdapter extends FragmentStatePagerAdapter {
    private static final int SIZE = 2;
    private Context context;
    private VideoListFragment videoListFragment = VideoListFragment.getInstance();
    private ImageGridGalleryFragment gridGalleryFragment = ImageGridGalleryFragment.getInstance();

    public MainActivityPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public ImageGridGalleryFragment getGridGalleryFragment(){
        return gridGalleryFragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return videoListFragment;
            case 1:
                return gridGalleryFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return SIZE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Filmy";
            case 1:
                return "Zdjecia";
        }
        return null;    }
}