package com.witold.videoprojectapp.view.video_list_fragment;

import com.witold.videoprojectapp.model.video.Video;

public interface VideoListFragmentHolder {
    public void showVideoRecorderActivity(int mode);
    public void startVideoPlayer(Video video);
}
