package com.witold.videoprojectapp.view.video_player_activity;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.witold.videoprojectapp.R;

public class VideoMediaController extends MyMediaController {
    private boolean hideMediaController;

    public VideoMediaController(Context context, boolean hideMediaController, boolean hideButtons, int drawableColor, int seekBarColor) {
        super(context, hideButtons, drawableColor, seekBarColor);
        this.hideMediaController = hideMediaController;
    }

    @Override
    public void hide() {
        if(hideMediaController){
            super.hide();
        }
    }

    @Override
    public void show() {
        if(hideMediaController){
            super.show(0);
        }else {
            super.show();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            super.hide();
            Activity a = (Activity)getContext();
            a.finish();
        }
        return true;
    }
}