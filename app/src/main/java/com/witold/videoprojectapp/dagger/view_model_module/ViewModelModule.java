package com.witold.videoprojectapp.dagger.view_model_module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.witold.videoprojectapp.dagger.scopes.ViewModelKey;
import com.witold.videoprojectapp.utils.view_model_factory.ViewModelFactory;
import com.witold.videoprojectapp.view.image_confirm_activity.ImageConfirmViewModel;
import com.witold.videoprojectapp.view.main_activity.MainActivityViewModel;
import com.witold.videoprojectapp.view.video_confirm_activity.VideoConfirmViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {
    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory viewModelFactory);

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel.class)
    abstract ViewModel bindMainViewModel(MainActivityViewModel mainActivityViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(VideoConfirmViewModel.class)
    abstract ViewModel bindVideoConfirmViewModel(VideoConfirmViewModel videoConfirmViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ImageConfirmViewModel.class)
    abstract ViewModel bindImageConfirmViewModel(ImageConfirmViewModel imageConfirmViewModel);

}
