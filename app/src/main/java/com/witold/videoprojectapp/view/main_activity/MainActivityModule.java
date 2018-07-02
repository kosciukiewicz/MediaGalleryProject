package com.witold.videoprojectapp.view.main_activity;

import android.content.Context;

import com.witold.videoprojectapp.dagger.scopes.FragmentScoped;
import com.witold.videoprojectapp.view.image_grid_gallery_fragment.ImageGridGalleryFragment;
import com.witold.videoprojectapp.view.video_list_fragment.VideoListFragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityModule {
    @Binds
    @Named("MainActivityContext")
    abstract Context bindContext(MainActivity mainActivity);

    @FragmentScoped
    @ContributesAndroidInjector()
    abstract VideoListFragment bindVideoListFragment();

    @FragmentScoped
    @ContributesAndroidInjector()
    abstract ImageGridGalleryFragment bindImageGridGalleryFragment();
}
