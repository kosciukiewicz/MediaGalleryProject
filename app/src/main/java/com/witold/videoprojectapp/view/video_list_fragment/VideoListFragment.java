package com.witold.videoprojectapp.view.video_list_fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.squareup.picasso.Picasso;
import com.witold.videoprojectapp.R;
import com.witold.videoprojectapp.file_controller.FileController;
import com.witold.videoprojectapp.model.video.Video;
import com.witold.videoprojectapp.utils.ItemOffsetDecorator;
import com.witold.videoprojectapp.view.ViewConstants;
import com.witold.videoprojectapp.view.main_activity.MainActivityViewModel;
import com.witold.videoprojectapp.view.video_recorder_activity.Camera2VideoImageActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import timber.log.Timber;

import static com.witold.videoprojectapp.view.ViewConstants.OFFSET;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoListFragment extends Fragment implements VideoListRecyclerAdapter.VideoGridHolder {
    private VideoListRecyclerAdapter gridGalleryRecyclerViewAdapter;
    public static VideoListFragment getInstance() {
        return new VideoListFragment();
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    Picasso picasso;

    private MainActivityViewModel mainActivityViewModel;
    private RecyclerView recyclerViewVideoList;
    private FloatingActionButton floatingActionButton;
    private VideoListFragmentHolder videoListFragmentHolder;
    private VideoInformationDialog videoInformationDialog;

    public VideoListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        this.initializeComponents(view);
        this.mainActivityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainActivityViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("onViewCreated");
        this.observeViewModel();
        mainActivityViewModel.getVideos();
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        this.videoListFragmentHolder = (VideoListFragmentHolder) context;
    }

    @Override
    public void handleVideoClick(Video video) {
        videoListFragmentHolder.startVideoPlayer(video);
    }

    @Override
    public void showMoreOptionsForVideo(final Video video, ImageButton optionsHolder) {
        PopupMenu popup = new PopupMenu(getActivity(), optionsHolder);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_single_video_show_info:
                        showMoreVideoInfoDialog(video);
                        break;
                    case R.id.item_single_video_delete:
                        deleteVideo(video);
                        break;
                    default:
                        return false;
                }
                return false;
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_single_video, popup.getMenu());
        popup.show();
    }

    private void showMoreVideoInfoDialog(Video video) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ViewConstants.EXTRA_VIDEO, video);
        videoInformationDialog.setArguments(bundle);
        videoInformationDialog.show(getFragmentManager(), "VideoInformationDialog");
    }

    private void deleteVideo(Video video) {
        this.mainActivityViewModel.removeVideo(video);
    }

    private void initializeComponents(View view) {
        this.videoInformationDialog = VideoInformationDialog.getInstance();
        this.recyclerViewVideoList = view.findViewById(R.id.recycler_view_video_list);
        this.floatingActionButton = view.findViewById(R.id.fab_add_video);
        this.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoListFragmentHolder.showVideoRecorderActivity(Camera2VideoImageActivity.RECORDER_MODE);
            }
        });
    }

    private void observeViewModel() {
        this.mainActivityViewModel.videoListLiveData.observe(this, new Observer<List<Video>>() {
            @Override
            public void onChanged(@Nullable List<Video> videos) {
                if (gridGalleryRecyclerViewAdapter == null) {
                    initializeImagesGrid(videos);
                } else {
                    gridGalleryRecyclerViewAdapter.updateDataSet((ArrayList<Video>) videos);
                }
            }
        });
    }

    private void initializeImagesGrid(List<Video> videoList) {
        gridGalleryRecyclerViewAdapter = new VideoListRecyclerAdapter(this, getActivity(), picasso, videoList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), ViewConstants.DEFAULT_COL_SPAN);
        this.recyclerViewVideoList.setLayoutManager(mLayoutManager);
        this.recyclerViewVideoList.setAdapter(gridGalleryRecyclerViewAdapter);
    }
}
