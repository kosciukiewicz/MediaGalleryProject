package com.witold.videoprojectapp.view.main_activity;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.witold.videoprojectapp.file_controller.FileController;
import com.witold.videoprojectapp.model.image.Image;
import com.witold.videoprojectapp.model.image.ImageRepository;
import com.witold.videoprojectapp.model.video.Video;
import com.witold.videoprojectapp.model.video.VideoRepository;
import com.witold.videoprojectapp.view.main_activity.sort_predicates.ImageSortPredicate;
import com.witold.videoprojectapp.view.main_activity.sort_predicates.VideoSortPredicate;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivityViewModel extends ViewModel {
    public MutableLiveData<Boolean> loadingLiveData;
    public MutableLiveData<Integer> testLiveData;
    public MutableLiveData<List<Video>> videoListLiveData;
    public MutableLiveData<List<Image>> imageListLiveData;
    public MutableLiveData<VideoSortPredicate> videoSortPredicateMutableLiveData;
    public MutableLiveData<ImageSortPredicate> imageSortPredicateMutableLiveData;
    private CompositeDisposable compositeDisposable;
    private ImageRepository imageRepository;
    private VideoRepository videoRepository;
    private FileController fileController;

    @Inject
    public MainActivityViewModel(ImageRepository imageRepository, VideoRepository videoRepository, FileController fileController) {
        this.testLiveData = new MutableLiveData<>();
        this.loadingLiveData = new MutableLiveData<>();
        this.loadingLiveData.setValue(false);
        this.videoSortPredicateMutableLiveData = new MutableLiveData<>();
        this.videoSortPredicateMutableLiveData.setValue(new VideoSortPredicate()); //default value
        this.imageSortPredicateMutableLiveData = new MutableLiveData<>();
        this.imageSortPredicateMutableLiveData.setValue(new ImageSortPredicate()); //default value
        this.videoListLiveData = new MutableLiveData<>();
        this.imageListLiveData = new MutableLiveData<>();
        this.compositeDisposable = new CompositeDisposable();
        this.imageRepository = imageRepository;
        this.videoRepository = videoRepository;
        this.fileController = fileController;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this.compositeDisposable.clear();
    }

    public void getTestData() {
        this.testLiveData.setValue(23);
    }

    public void getVideos() {
        this.getVideos(this.videoSortPredicateMutableLiveData.getValue());
    }

    public void getImages() {
        this.getImages(this.imageSortPredicateMutableLiveData.getValue());
    }

    public void getVideos(final VideoSortPredicate videoSortPredicate) {
        this.loadingLiveData.setValue(true);
        videoRepository.getAllVideos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Video>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<Video> videos) {
                        videoSortPredicateMutableLiveData.setValue(videoSortPredicate);
                        videoListLiveData.setValue(videos.stream().sorted(new Comparator<Video>() {
                            @Override
                            public int compare(Video o1, Video o2) {
                                return videoSortPredicate.compare(o1, o2);
                            }
                        }).collect(Collectors.<Video>toList()));
                        loadingLiveData.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingLiveData.setValue(false);
                    }
                });
    }

    public void getImages(final ImageSortPredicate imageSortPredicate) {
        loadingLiveData.setValue(true);
        imageRepository.getAllImages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Image>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<Image> images) {
                        loadingLiveData.setValue(false);
                        imageSortPredicateMutableLiveData.setValue(imageSortPredicate);
                        imageListLiveData.setValue(images.stream().sorted(new Comparator<Image>() {
                            @Override
                            public int compare(Image o1, Image o2) {
                                return imageSortPredicate.compare(o1, o2);
                            }
                        }).collect(Collectors.<Image>toList()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingLiveData.setValue(false);
                        e.printStackTrace();
                    }
                });
    }

    public void sortVideos(final VideoSortPredicate videoSortPredicate) {
        videoSortPredicateMutableLiveData.setValue(videoSortPredicate);
        videoListLiveData.setValue(videoListLiveData.getValue().stream().sorted(new Comparator<Video>() {
            @Override
            public int compare(Video o1, Video o2) {
                return videoSortPredicate.compare(o1, o2);
            }
        }).collect(Collectors.<Video>toList()));
    }

    public void sortImages(final ImageSortPredicate imageSortPredicate) {
        imageSortPredicateMutableLiveData.setValue(imageSortPredicate);
        imageListLiveData.setValue(imageListLiveData.getValue().stream().sorted(new Comparator<Image>() {
            @Override
            public int compare(Image o1, Image o2) {
                return imageSortPredicate.compare(o1, o2);
            }
        }).collect(Collectors.<Image>toList()));
    }

    public void removeVideo(final Video video) {
        loadingLiveData.setValue(true);
        videoRepository.removeVideo(video)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        loadingLiveData.setValue(false);
                        deleteUnusedVideoFile(video.getFileName());
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingLiveData.setValue(false);
                    }
                });
    }

    public void removeImage(final Image image) {
        loadingLiveData.setValue(true);
        imageRepository.removeImage(image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        loadingLiveData.setValue(false);
                        deleteUnusedImageFile(image.getFileName());
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingLiveData.setValue(false);
                    }
                });
    }

    public void deleteUnusedImageFile(String fileName) {
        this.fileController.deleteImageFile(fileName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        getImages();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    public void deleteUnusedVideoFile(String fileName) {
        this.fileController.deleteVideoFile(fileName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        getVideos();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }
}
