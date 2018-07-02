package com.witold.videoprojectapp.dagger;
import com.witold.videoprojectapp.dagger.scopes.ActivityScoped;
import com.witold.videoprojectapp.view.image_confirm_activity.ImageConfirmActivity;
import com.witold.videoprojectapp.view.main_activity.MainActivity;
import com.witold.videoprojectapp.view.main_activity.MainActivityModule;
import com.witold.videoprojectapp.view.pager_activity.PagerActivity;
import com.witold.videoprojectapp.view.video_confirm_activity.VideoConfirmActivity;
import com.witold.videoprojectapp.view.video_player_activity.VideoPlayerActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Klasa do udostępnianie wszysktich aktywności, dagger wtrzykuje zależności.
 */
@Module
public abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = {MainActivityModule.class})
    abstract MainActivity mainActivity();

    @ActivityScoped
    @ContributesAndroidInjector()
    abstract VideoConfirmActivity videoConfirmActivity();

    @ActivityScoped
    @ContributesAndroidInjector()
    abstract ImageConfirmActivity imageConfirmActivity();

    @ActivityScoped
    @ContributesAndroidInjector()
    abstract VideoPlayerActivity videoPlayerActivity();

    @ActivityScoped
    @ContributesAndroidInjector()
    abstract PagerActivity pagerActivity();
}
