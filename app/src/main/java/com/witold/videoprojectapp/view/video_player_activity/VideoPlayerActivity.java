package com.witold.videoprojectapp.view.video_player_activity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.witold.videoprojectapp.R;
import com.witold.videoprojectapp.model.video.Video;
import com.witold.videoprojectapp.shared_preferences_controller.SharedPreferencesController;
import com.witold.videoprojectapp.view.ViewConstants;

import java.io.IOException;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class VideoPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MyMediaController.MediaPlayerControl {

    private SurfaceView videoSurface;
    private FrameLayout frameLayoutVideoContainer;
    private MediaPlayer player;
    private MyMediaController controller;
    private String videoPath;

    @Inject
    SharedPreferencesController sharedPreferencesController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Video video = (Video) getIntent().getSerializableExtra(ViewConstants.EXTRA_VIDEO);
        setVideoPath(video.getFileName());
        Uri vidUri = Uri.parse(videoPath);
        videoSurface = (SurfaceView) findViewById(R.id.video_view_video_player_activity);
        frameLayoutVideoContainer = (FrameLayout) findViewById(R.id.frame_layout_video_wrapper);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);

        player = new MediaPlayer();
        controller = new VideoMediaController(this, sharedPreferencesController.getHideMediaControllerField(), !sharedPreferencesController.getHideMediaControllerButtonsField(), sharedPreferencesController.getButtonsColorField(), sharedPreferencesController.getSeekBarColorField());

        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(this, vidUri);
            player.setOnPreparedListener(this);
        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocas) {
        super.onWindowFocusChanged(hasFocas);
        View decorView = getWindow().getDecorView();
        if (hasFocas) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    public void setVideoPath(String fileName) {
        videoPath = fileName;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);
        player.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView(frameLayoutVideoContainer);
        handleAspectRatio();
        player.start();
        if (!sharedPreferencesController.getPlayOnStartField()) {
            player.pause();
        }
        if (!sharedPreferencesController.getHideMediaControllerField()) {
            controller.show();
        }
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {

    }

    private void handleAspectRatio() {
        int surfaceView_Width = frameLayoutVideoContainer.getWidth();
        int surfaceView_Height = frameLayoutVideoContainer.getHeight();

        float video_Width = player.getVideoWidth();
        float video_Height = player.getVideoHeight();

        float ratio_width = surfaceView_Width / video_Width;
        float ratio_height = surfaceView_Height / video_Height;
        float aspectratio = video_Width / video_Height;

        ViewGroup.LayoutParams layoutParams = frameLayoutVideoContainer.getLayoutParams();

        if (ratio_width > ratio_height) {
            layoutParams.width = (int) (surfaceView_Height * aspectratio);
            layoutParams.height = surfaceView_Height;
        } else {
            layoutParams.width = surfaceView_Width;
            layoutParams.height = (int) (surfaceView_Width / aspectratio);
        }

        frameLayoutVideoContainer.setLayoutParams(layoutParams);
    }
}
