package com.witold.videoprojectapp.dagger;

import android.app.Application;
import com.witold.videoprojectapp.VideoProjectApplication;
import com.witold.videoprojectapp.dagger.database_module.DatabaseModule;
import com.witold.videoprojectapp.dagger.file_controller_module.FileControllerModule;
import com.witold.videoprojectapp.dagger.picasso_module.PicassoModule;
import com.witold.videoprojectapp.dagger.scopes.ApplicationScope;
import com.witold.videoprojectapp.dagger.shared_preferences_module.SharedPreferencesModule;
import com.witold.videoprojectapp.dagger.view_model_module.ViewModelModule;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.support.AndroidSupportInjectionModule;

@ApplicationScope
@Component(modules = {
        SharedPreferencesModule.class,
        FileControllerModule.class,
        DatabaseModule.class,
        ViewModelModule.class,
        PicassoModule.class,
        AppModule.class,
        ActivityBindingModule.class,
        AndroidSupportInjectionModule.class})

public interface ApplicationComponent extends AndroidInjector<DaggerApplication> {

    void inject(VideoProjectApplication videoProjectApplication);

    @Override
    void inject(DaggerApplication instance);

    @Component.Builder
    interface Builder {
        @BindsInstance
        ApplicationComponent.Builder application(Application application);
        ApplicationComponent build();
    }}
