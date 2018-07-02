package com.witold.videoprojectapp.view.pager_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import com.witold.videoprojectapp.R;
import com.witold.videoprojectapp.model.image.Image;
import com.witold.videoprojectapp.model.image.ImageRepository;
import com.witold.videoprojectapp.model.video.Video;
import com.witold.videoprojectapp.view.main_activity.sort_predicates.ImageSortPredicate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import static com.witold.videoprojectapp.view.ViewConstants.EXTRA_CURRENT_ALBUM_POSITION;
import static com.witold.videoprojectapp.view.ViewConstants.EXTRA_SORT_PREDICATE;
import static com.witold.videoprojectapp.view.ViewConstants.EXTRA_STARTING_ALBUM_POSITION;
import static com.witold.videoprojectapp.view.ViewConstants.VIEW_TAG;

public class PagerActivity extends AppCompatActivity implements PagerHolder {
    @Inject
    ImageRepository imageRepository;

    private ViewPager viewPager;
    private List<Image> gridGalleryImageList;
    private int startingPosition;
    private ImageSortPredicate imageSortPredicate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        setContentView(R.layout.activity_pager);
        this.initializeComponents();
        this.getImagesFromRepository();
          }

    @Override
    public void startPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return true;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        getWindow().setSharedElementReturnTransition(null);
        getWindow().setSharedElementReenterTransition(null);
        View sharedView = getImageAtPosition(viewPager.getCurrentItem());
        sharedView.setTransitionName(null);
        super.onBackPressed();
    }

    private void initializeComponents() {
        this.viewPager = findViewById(R.id.pagerActivityViewPager);
    }

    public void getImagesFromRepository() {
        this.imageRepository.getAllImages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Image>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Image> images) {
                        imageSortPredicate = (ImageSortPredicate) getIntent().getSerializableExtra(EXTRA_SORT_PREDICATE);
                        gridGalleryImageList = images.stream().sorted(new Comparator<Image>() {
                            @Override
                            public int compare(Image o1, Image o2) {
                                return imageSortPredicate.compare(o1, o2);
                            }
                        }).collect(Collectors.<Image>toList());
                        startingPosition = getIntent().getIntExtra(EXTRA_STARTING_ALBUM_POSITION, 0);
                        initializePager(startingPosition);
                        initializeSharedElementsCallback();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private void initializePager(int startingPosition){
        PagerGalleryViewPagerAdapter adapter = new PagerGalleryViewPagerAdapter(this, this.gridGalleryImageList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(startingPosition);
    }

    private void initializeSharedElementsCallback() {
        setEnterSharedElementCallback(new SharedElementCallback() {

            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                int startingPosition = getIntent().getIntExtra(EXTRA_STARTING_ALBUM_POSITION, 0);
                int currentPosition = getIntent().getIntExtra(EXTRA_CURRENT_ALBUM_POSITION, 0);
                Timber.d(currentPosition + "");
                Timber.d(startingPosition + "");
                if (viewPager.getCurrentItem() != PagerActivity.this.startingPosition) {
                    View sharedView = getImageAtPosition(viewPager.getCurrentItem());
                    Timber.d(sharedView.getTransitionName() + "");
                    names.clear();
                    sharedElements.clear();
                    names.add(sharedView.getTransitionName());
                    sharedElements.put(sharedView.getTransitionName(), sharedView);
                }
            }


        });
    }

    private View getImageAtPosition(int position) {
        return viewPager.findViewWithTag(VIEW_TAG + this.gridGalleryImageList.get(position).getId()).findViewById(R.id.singlePageImageView);
    }
}
