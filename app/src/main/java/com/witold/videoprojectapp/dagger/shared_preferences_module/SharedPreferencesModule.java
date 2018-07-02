package com.witold.videoprojectapp.dagger.shared_preferences_module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.witold.videoprojectapp.dagger.scopes.ApplicationScope;

import dagger.Module;
import dagger.Provides;

@Module
public class SharedPreferencesModule {
    @ApplicationScope
    @Provides
    public SharedPreferences providesSharedPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
