package com.witold.videoprojectapp.shared_preferences_controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.google.gson.Gson;
import com.witold.videoprojectapp.R;

import javax.inject.Inject;

import timber.log.Timber;

public class SharedPreferencesController {
    private Context context;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Inject
    public SharedPreferencesController(Context context, SharedPreferences sharedPreferences) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    public boolean getPlayOnStartField() {
        return sharedPreferences.getBoolean("play_on_start", true);
    }

    public boolean getHideMediaControllerField() {
        return sharedPreferences.getBoolean("hide_media_player", true);
    }

    public boolean getHideMediaControllerButtonsField() {
        return sharedPreferences.getBoolean("show_media_player_buttons", true);
    }

    public int getSeekBarColorField() {
        switch (sharedPreferences.getString("seek_bar_color", "Pomarańczowy")) {
            case "Pomarańczowy":
                return ContextCompat.getColor(context, R.color.colorOrange);
            case "Niebieski":
                return ContextCompat.getColor(context, R.color.colorBlue);
            case "Fioletowy":
                return ContextCompat.getColor(context, R.color.colorViolet);
            default:
                return ContextCompat.getColor(context, R.color.colorAccent);
        }
    }

    public int getButtonsColorField() {
        switch (sharedPreferences.getString("buttons_color", "Pomarańczowy")) {
            case "Pomarańczowy":
                return ContextCompat.getColor(context, R.color.colorOrange);
            case "Niebieski":
                return ContextCompat.getColor(context, R.color.colorBlue);
            case "Fioletowy":
                return ContextCompat.getColor(context, R.color.colorViolet);
            default:
                return ContextCompat.getColor(context, R.color.colorAccent);
        }
    }

}
