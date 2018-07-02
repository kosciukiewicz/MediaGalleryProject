package com.witold.videoprojectapp.view.video_confirm_activity;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.witold.videoprojectapp.file_controller.FileController;
import com.witold.videoprojectapp.model.video.Video;
import com.witold.videoprojectapp.model.video.VideoRepository;
import com.witold.videoprojectapp.utils.ActionLiveData;
import com.witold.videoprojectapp.view.image_confirm_activity.ConfirmImageEvent;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class VideoConfirmViewModel extends ViewModel {
    public ActionLiveData<ConfirmVideoEvent> confirmVideoEventActionLiveData;
    private VideoRepository videoRepository;
    private FileController fileController;
    private CompositeDisposable compositeDisposable;
    public MutableLiveData<Date> dateDoneLiveData;
    public MutableLiveData<Long> lengthLiveData;

    @Inject
    public VideoConfirmViewModel(VideoRepository videoRepository, FileController fileController) {
        this.confirmVideoEventActionLiveData = new ActionLiveData<>();
        this.dateDoneLiveData = new MutableLiveData<>();
        this.lengthLiveData = new MutableLiveData<>();
        this.videoRepository = videoRepository;
        this.compositeDisposable = new CompositeDisposable();
        this.fileController = fileController;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    public void loadInformation(String fileName){
        this.lengthLiveData.setValue(this.fileController.getVideoLength(fileName));
        this.dateDoneLiveData.setValue(Calendar.getInstance().getTime());
    }

    public void addNewVideo(Video video){
        video.setLength(lengthLiveData.getValue());
        video.setDateDone(dateDoneLiveData.getValue());
        this.videoRepository.addVideo(video)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        confirmVideoEventActionLiveData.sendAction(new ConfirmVideoEvent());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    public void deleteUnusedFile(String fileName){
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
                        confirmVideoEventActionLiveData.sendAction(new ConfirmVideoEvent());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }
}
