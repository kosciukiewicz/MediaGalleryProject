package com.witold.videoprojectapp.dagger.file_controller_module;

import android.content.Context;

import com.witold.videoprojectapp.dagger.scopes.ApplicationScope;
import com.witold.videoprojectapp.file_controller.FileController;

import dagger.Module;
import dagger.Provides;

@Module
public class FileControllerModule {
    @ApplicationScope
    @Provides
    public FileController providesFileController(Context context){
        return new FileController(context);
    }
}
