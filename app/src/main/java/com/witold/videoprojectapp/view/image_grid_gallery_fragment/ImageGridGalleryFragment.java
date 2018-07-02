package com.witold.videoprojectapp.view.image_grid_gallery_fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.witold.videoprojectapp.R;
import com.witold.videoprojectapp.model.image.Image;
import com.witold.videoprojectapp.view.main_activity.MainActivityViewModel;
import com.witold.videoprojectapp.view.video_recorder_activity.Camera2VideoImageActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import timber.log.Timber;

import static com.witold.videoprojectapp.view.ViewConstants.DEFAULT_COL_SPAN;
import static com.witold.videoprojectapp.view.ViewConstants.DEFAULT_POINTER_X_POSITION;
import static com.witold.videoprojectapp.view.ViewConstants.DEFAULT_ROW_SPAN;
import static com.witold.videoprojectapp.view.ViewConstants.EXTRA_GRID_MODE_COL_SPAN;
import static com.witold.videoprojectapp.view.ViewConstants.EXTRA_GRID_MODE_ROW_SPAN;
import static com.witold.videoprojectapp.view.ViewConstants.EXTRA_SCREEN_ORIENTATION;
import static com.witold.videoprojectapp.view.ViewConstants.OFFSET;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageGridGalleryFragment extends Fragment implements GridGalleryRecyclerViewAdapter.GridHolder, View.OnTouchListener {
    public static ImageGridGalleryFragment getInstance() {
        return new ImageGridGalleryFragment();
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    Picasso picasso;

    private GridGalleryRecyclerViewAdapter gridGalleryRecyclerViewAdapter;
    private ImageGridFragmentHolder imageGridFragmentHolder;
    private MainActivityViewModel mainActivityViewModel;
    private RecyclerView recyclerViewGrid;
    private FloatingActionButton floatingActionButton;
    private List<GridMode> gridModeList;
    private List<Image> imageList;
    private GridMode currentGridMode;
    private boolean userScroll;
    private int scroll;
    private int initialPointerX = DEFAULT_POINTER_X_POSITION;

    public ImageGridGalleryFragment() {
        // Required empty public constructor
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
                        setNewGridModeBySwipeRight();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_grid_gallery, container, false);
        this.initializeComponents(view);
        this.mainActivityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainActivityViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("onViewCreated");
        if (savedInstanceState != null) {
            restoreCurrentGridMode(savedInstanceState);
        }
        this.observeViewModel();
        this.mainActivityViewModel.getImages();
    }

    @Override
    public void onAttach(Context context) {
        this.imageGridFragmentHolder = (ImageGridFragmentHolder) context;
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void handleImageClick(Image image, int position, ImageView imageView, String transitionName) {
        this.imageGridFragmentHolder.onImageClick(image, position, imageView, transitionName);
    }

    @Override
    public void onImageLongClick(final Image image, ImageView imageView) {
        PopupMenu popup = new PopupMenu(getActivity(), imageView);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_single_image_delete:
                        deleteImage(image);
                        break;
                    default:
                        return false;
                }
                return false;
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_single_image, popup.getMenu());
        popup.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_GRID_MODE_COL_SPAN, this.currentGridMode.getColSpan());
        outState.putInt(EXTRA_GRID_MODE_ROW_SPAN, this.currentGridMode.getRowSpan());
        outState.putInt(EXTRA_SCREEN_ORIENTATION, getResources().getConfiguration().orientation);
    }

    private void cancelSwipeGesture() {
        initialPointerX = DEFAULT_POINTER_X_POSITION;
    }

    private void restoreCurrentGridMode(Bundle savedInstanceState) {
        int colSpan = savedInstanceState.getInt(EXTRA_GRID_MODE_COL_SPAN, DEFAULT_COL_SPAN);
        int rowSpan = savedInstanceState.getInt(EXTRA_GRID_MODE_ROW_SPAN, DEFAULT_ROW_SPAN);
        this.currentGridMode = new GridMode(colSpan, rowSpan);
    }

    private void deleteImage(Image image) {
        this.mainActivityViewModel.removeImage(image);
    }

    private Image getSampleImage() {
        return new Image(
                R.drawable.sample_image_1,
                "",
                "",
                Calendar.getInstance().getTime(),
                "",
                123L
        );
    }

    private void setNewGridModeBySwipeRight() {
        if (this.gridModeList.indexOf(this.currentGridMode) != this.gridModeList.size() - 1) {
            updateGridMode(gridModeList.get(this.gridModeList.indexOf(this.currentGridMode) + 1));
        } else {
            updateGridMode(gridModeList.get(0));
        }
    }

    private void updateGridMode(GridMode gridMode) {
        this.currentGridMode = gridMode;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            initializeImagesGrid(this.currentGridMode.getColSpan(), this.currentGridMode.getRowSpan());
        } else {
            initializeImagesGrid(this.currentGridMode.getLandscapeColSpan(), this.currentGridMode.getLandscapeRowSpan());
        }

    }

    private void initializeImagesGrid(int colSpan, int rowSpan) {
        Timber.d(colSpan + " " + rowSpan + " " + OFFSET + " " + recyclerViewGrid.getMeasuredHeight() + " ");
        GridGalleryRecyclerViewAdapter gridGalleryRecyclerViewAdapter = new GridGalleryRecyclerViewAdapter(this, this.imageList, picasso, OFFSET, colSpan, rowSpan);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), colSpan);
        recyclerViewGrid.setLayoutManager(mLayoutManager);
        recyclerViewGrid.setAdapter(gridGalleryRecyclerViewAdapter);
        scrollToDefaultPosition();
    }

    private void initializeComponents(View view) {
        this.floatingActionButton = view.findViewById(R.id.fab_add_photo);
        this.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageGridFragmentHolder.showVideoRecorderActivity(Camera2VideoImageActivity.CAPTURE_MODE);
            }
        });
        this.recyclerViewGrid = view.findViewById(R.id.recycler_view_image_grid);
        this.initializeGridModes();
        this.initializeRecyclerViewScrollBehaviour();
    }

    private void initializeGridModes() {
        this.gridModeList = new ArrayList<>();
        this.gridModeList.add(new GridMode(2, 4));
        this.gridModeList.add(new GridMode(3, 6));
        this.gridModeList.add(new GridMode(4, 8));
        if (this.currentGridMode == null) {
            this.currentGridMode = this.gridModeList.get(0);
        }
    }

    private void initializeImagesGrid(List<Image> images) {
        initializeImagesGrid(images, this.currentGridMode);
    }

    private void initializeImagesGrid(List<Image> images, GridMode gridMode) {
        this.currentGridMode = gridMode;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            initializeImagesGrid(images, this.currentGridMode.getColSpan(), this.currentGridMode.getRowSpan());
        } else {
            initializeImagesGrid(images, this.currentGridMode.getLandscapeColSpan(), this.currentGridMode.getLandscapeRowSpan());
        }
    }

    private void initializeRecyclerViewScrollBehaviour() {
        this.scroll = 0;
        recyclerViewGrid.setOnTouchListener(this);
        recyclerViewGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && userScroll) {
                    int scrollOffset = checkHover(recyclerView);
                    recyclerView.smoothScrollBy(0, scrollOffset); // moment scrolla
                    userScroll = false;

                } else {
                    userScroll = true;
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            private int checkHover(RecyclerView recyclerView) {

                GridLayoutManager layoutManager = ((GridLayoutManager) recyclerView.getLayoutManager());
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                View view = layoutManager.findViewByPosition(layoutManager.findFirstCompletelyVisibleItemPosition());
                int height;
                if (view != null) {
                    height = view.getHeight();
                } else {
                    height = 0;
                }
                int colSpan;
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    colSpan = currentGridMode.getColSpan();
                } else {
                    colSpan = currentGridMode.getLandscapeColSpan();
                }
                int downScroll = (firstVisiblePosition / colSpan) * (height + OFFSET * 2) - scroll;
                int upScroll = ((height + 2 * OFFSET) + (firstVisiblePosition / colSpan) * (height + OFFSET * 2)) - scroll;
                Timber.d(downScroll + " " + upScroll);
                return Math.abs(upScroll) > Math.abs(downScroll) ? downScroll : upScroll;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scroll += dy;
            }
        });
    }

    private void observeViewModel() {
        this.mainActivityViewModel.imageListLiveData.observe(this, new Observer<List<Image>>() {
            @Override
            public void onChanged(@Nullable List<Image> images) {
                imageList = images;
                if (gridGalleryRecyclerViewAdapter == null) {
                    initializeImagesGrid(images);
                } else {
                    if(gridGalleryRecyclerViewAdapter.getImagesList().size() != images.size()){
                        initializeImagesGrid(images);
                    }else{
                        Timber.d("UPDATE IMAGES");
                        gridGalleryRecyclerViewAdapter.updateDataSet((ArrayList<Image>) images);
                        scrollToDefaultPosition();
                    }

                }
            }
        });
    }

    private void initializeImagesGrid(List<Image> imagesList, int colSpan, int rowSpan) {
        this.gridGalleryRecyclerViewAdapter = new GridGalleryRecyclerViewAdapter(this, imagesList, picasso, OFFSET, colSpan, rowSpan);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), colSpan);
        this.recyclerViewGrid.addItemDecoration(new ItemOffsetDecorator(OFFSET));
        this.recyclerViewGrid.setLayoutManager(mLayoutManager);
        this.recyclerViewGrid.setAdapter(gridGalleryRecyclerViewAdapter);
        scrollToDefaultPosition();
    }

    private void scrollToDefaultPosition() {
        recyclerViewGrid.scrollToPosition(0);
        this.scroll = 0;
    }
}
