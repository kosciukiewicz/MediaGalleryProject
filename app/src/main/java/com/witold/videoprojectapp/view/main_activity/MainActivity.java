package com.witold.videoprojectapp.view.main_activity;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.witold.videoprojectapp.R;
import com.witold.videoprojectapp.model.image.Image;
import com.witold.videoprojectapp.model.video.Video;
import com.witold.videoprojectapp.view.ViewConstants;
import com.witold.videoprojectapp.view.image_confirm_activity.ImageConfirmActivity;
import com.witold.videoprojectapp.view.image_grid_gallery_fragment.ImageGridFragmentHolder;
import com.witold.videoprojectapp.view.image_grid_gallery_fragment.ImageGridGalleryFragment;
import com.witold.videoprojectapp.view.main_activity.sort_dialog.ImageSortDialog;
import com.witold.videoprojectapp.view.main_activity.sort_dialog.VideoSortDialog;
import com.witold.videoprojectapp.view.main_activity.sort_predicates.ImageSortPredicate;
import com.witold.videoprojectapp.view.main_activity.sort_predicates.VideoSortPredicate;
import com.witold.videoprojectapp.view.pager_activity.PagerActivity;
import com.witold.videoprojectapp.view.video_confirm_activity.VideoConfirmActivity;
import com.witold.videoprojectapp.view.video_list_fragment.VideoListFragmentHolder;
import com.witold.videoprojectapp.view.video_player_activity.VideoPlayerActivity;
import com.witold.videoprojectapp.view.video_player_settings_activity.VideoPlayerSettingsActivity;
import com.witold.videoprojectapp.view.video_recorder_activity.Camera2VideoImageActivity;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

import static com.witold.videoprojectapp.view.ViewConstants.DEFAULT_POINTER_X_POSITION;
import static com.witold.videoprojectapp.view.ViewConstants.EXTRA_CURRENT_ALBUM_POSITION;
import static com.witold.videoprojectapp.view.ViewConstants.EXTRA_FILE_NAME;
import static com.witold.videoprojectapp.view.ViewConstants.EXTRA_SORT_PREDICATE;
import static com.witold.videoprojectapp.view.ViewConstants.EXTRA_STARTING_ALBUM_POSITION;
import static com.witold.videoprojectapp.view.ViewConstants.IMAGE_CONFIRM_ACTIVITY_REQUEST_CODE;
import static com.witold.videoprojectapp.view.ViewConstants.RESULT_CODE_SUCCESS;
import static com.witold.videoprojectapp.view.ViewConstants.RESULT_CODE_SUCCESS_RECORD;
import static com.witold.videoprojectapp.view.ViewConstants.VIDEO_CONFIRM_ACTIVITY_REQUEST_CODE;
import static com.witold.videoprojectapp.view.ViewConstants.VIDEO_RECORD_ACTIVITY_REQUEST_CODE;
import static com.witold.videoprojectapp.view.ViewConstants.VIEW_TAG;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector, View.OnTouchListener, VideoListFragmentHolder, ImageGridFragmentHolder, VideoSortDialog.VideoSortDialogHolder, ImageSortDialog.ImageSortDialogHolder {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;
    private static final String[] permissions = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private MainActivityViewModel mainActivityViewModel;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MainActivityPagerAdapter mainActivityPagerAdapter;
    private VideoSortDialog videoSortDialog;
    private ImageSortDialog imageSortDialog;
    private Bundle pagerBundle;
    private int initialPointerX = DEFAULT_POINTER_X_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeComponents();
        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        this.observeViewModel();
        this.initializeViewPager();
        this.mainActivityViewModel.getTestData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case VIDEO_RECORD_ACTIVITY_REQUEST_CODE:
                if (data != null) {
                    if (resultCode == RESULT_CODE_SUCCESS_RECORD) {
                        this.handleRecordedVideo(data.getStringExtra(EXTRA_FILE_NAME));
                    } else {
                        this.handleCapturedImage(data.getStringExtra(EXTRA_FILE_NAME));
                    }
                }
                break;
            case VIDEO_CONFIRM_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_CODE_SUCCESS) {
                    this.mainActivityViewModel.getVideos();
                }
                break;
            case IMAGE_CONFIRM_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_CODE_SUCCESS) {
                    this.mainActivityViewModel.getImages();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);

        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    public void showVideoRecorderActivity(int mode) {
        Intent intent = new Intent(this, Camera2VideoImageActivity.class);
        intent.putExtra(ViewConstants.EXTRA_RECORDER_MODE, mode);
        startActivityForResult(intent, VIDEO_RECORD_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_sort:
                handleSortRequest();
                return true;
            case R.id.menu_item_player_settings:
                openVideoPlayerSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void applyFilter(VideoSortPredicate videoSortPredicate) {
        this.mainActivityViewModel.sortVideos(videoSortPredicate);
    }

    @Override
    public void applyFilter(ImageSortPredicate imageSortPredicate) {
        this.mainActivityViewModel.sortImages(imageSortPredicate);
    }

    @Override
    public void startVideoPlayer(Video video) {
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(ViewConstants.EXTRA_VIDEO, video);
        startActivity(intent);
    }

    @Override
    public void onImageClick(Image image, int position, ImageView imageView, String transitionName) {
        Intent intent = new Intent(this, PagerActivity.class);
        intent.putExtra(EXTRA_STARTING_ALBUM_POSITION, position);
        intent.putExtra(EXTRA_SORT_PREDICATE, this.mainActivityViewModel.imageSortPredicateMutableLiveData.getValue());
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, imageView, transitionName);
        startActivity(intent, options.toBundle());
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (motionEvent.getPointerCount() > 1) {
            switch (action) {
                case (MotionEvent.ACTION_POINTER_DOWN):
                    initialPointerX = (int) motionEvent.getX();
                    return true;
                case (MotionEvent.ACTION_MOVE):
                    if (initialPointerX == DEFAULT_POINTER_X_POSITION) {
                        initialPointerX = (int) motionEvent.getX();
                    }
                    if (initialPointerX != DEFAULT_POINTER_X_POSITION && (initialPointerX - (int) motionEvent.getX()) > 100) {
                        cancelSwipeGesture();
                    }
                    if (initialPointerX != DEFAULT_POINTER_X_POSITION && ((int) motionEvent.getX()) - initialPointerX > 100) {
                        Timber.d("left");
                        if (viewPager.getCurrentItem() == 1) {
                            //((MainActivityPagerAdapter) viewPager.getAdapter()).getGridGalleryFragment().setNewGridModeBySwipeRight();
                        }
                        cancelSwipeGesture();
                    }
                    return true;
                case (MotionEvent.ACTION_POINTER_UP):
                    //cancelSwipeGesture();
                    return true;
                case (MotionEvent.ACTION_CANCEL):
                    //cancelSwipeGesture();
                    return true;
                case (MotionEvent.ACTION_OUTSIDE):
                    return true;
                default:
                    return true;
            }
        } else {
            return false;
        }
    }

    private void cancelSwipeGesture() {
        initialPointerX = DEFAULT_POINTER_X_POSITION;
    }

    private void openVideoPlayerSettingsActivity() {
        Intent intent = new Intent(this, VideoPlayerSettingsActivity.class);
        startActivity(intent);
    }

    private void handleSortRequest() {
        Timber.d(this.viewPager.getCurrentItem() + "");
        if (this.viewPager.getCurrentItem() == 0) {
            this.videoSortDialog.show(getFragmentManager(), "Sort video dialog");
        } else {
            this.imageSortDialog.show(getFragmentManager(), "Sort image dialog");
        }
    }

    private void handleRecordedVideo(String fileName) {
        Intent intent = new Intent(this, VideoConfirmActivity.class);
        intent.putExtra(EXTRA_FILE_NAME, fileName);
        startActivityForResult(intent, VIDEO_CONFIRM_ACTIVITY_REQUEST_CODE);
    }

    private void handleCapturedImage(String fileName) {
        Intent intent = new Intent(this, ImageConfirmActivity.class);
        intent.putExtra(EXTRA_FILE_NAME, fileName);
        startActivityForResult(intent, IMAGE_CONFIRM_ACTIVITY_REQUEST_CODE);
    }


    private boolean checkCameraPermission() {
        boolean result = true;
        for (int i = 0; i < permissions.length && result; i++) {
            result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
        Timber.d(result + "");
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, permissions, CAMERA_PERMISSION_REQUEST_CODE);
    }

    private void initializeComponents() {
        this.tabLayout = findViewById(R.id.tab_layout);
        this.viewPager = findViewById(R.id.view_pager);
        this.swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        this.swipeRefreshLayout.setEnabled(false);
        this.videoSortDialog = VideoSortDialog.getInstance();
        this.imageSortDialog = ImageSortDialog.getInstance();
    }

    private void observeViewModel() {
        this.mainActivityViewModel.testLiveData.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                Timber.d("Jest wartość " + integer);
            }
        });

        this.mainActivityViewModel.videoSortPredicateMutableLiveData.observe(this, new Observer<VideoSortPredicate>() {
            @Override
            public void onChanged(@Nullable VideoSortPredicate videoSortPredicate) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(EXTRA_SORT_PREDICATE, videoSortPredicate);
                videoSortDialog.setArguments(bundle);
            }
        });

        this.mainActivityViewModel.imageSortPredicateMutableLiveData.observe(this, new Observer<ImageSortPredicate>() {
            @Override
            public void onChanged(@Nullable ImageSortPredicate imageSortPredicate) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(EXTRA_SORT_PREDICATE, imageSortPredicate);
                imageSortDialog.setArguments(bundle);
            }
        });

        this.mainActivityViewModel.loadingLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                swipeRefreshLayout.setRefreshing(aBoolean);
            }
        });
    }

    private void initializeViewPager() {
        this.mainActivityPagerAdapter = new MainActivityPagerAdapter(getSupportFragmentManager(), this);
        this.viewPager.setAdapter(null);
        this.viewPager.setAdapter(mainActivityPagerAdapter);
        //this.viewPager.setOnTouchListener(this);
        this.tabLayout.setupWithViewPager(this.viewPager);
    }
}
