package com.witold.videoprojectapp.dagger.picasso_module;

import android.content.Context;

import com.squareup.picasso.Picasso;
import com.witold.videoprojectapp.dagger.scopes.ApplicationScope;

import dagger.Module;
import dagger.Provides;

@Module()
public class PicassoModule {
    @Provides
    @ApplicationScope
    public Picasso picasso(Context context) {
        return new Picasso.Builder(context)
                .build();
    }
}
