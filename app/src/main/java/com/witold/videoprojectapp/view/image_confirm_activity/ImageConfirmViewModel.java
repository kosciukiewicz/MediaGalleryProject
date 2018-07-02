package com.witold.videoprojectapp.view.image_confirm_activity;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.witold.videoprojectapp.file_controller.FileController;
import com.witold.videoprojectapp.model.image.Image;
import com.witold.videoprojectapp.model.image.ImageRepository;
import com.witold.videoprojectapp.utils.ActionLiveData;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ImageConfirmViewModel extends ViewModel {
    public ActionLiveData<ConfirmImageEvent> confirmVideoEventActionLiveData;
    private ImageRepository imageRepository;
    private FileController fileController;
    private CompositeDisposable compositeDisposable;
    public MutableLiveData<Date> dateDoneLiveData;
    public MutableLiveData<Long> sizeLiveData;

    @Inject
    public ImageConfirmViewModel(ImageRepository imageRepository, FileController fileController) {
        this.confirmVideoEventActionLiveData = new ActionLiveData<>();
        this.dateDoneLiveData = new MutableLiveData<>();
        this.sizeLiveData = new MutableLiveData<>();
        this.imageRepository = imageRepository;
        this.compositeDisposable = new CompositeDisposable();
        this.fileController = fileController;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    public void loadInformation(String fileName){
        this.sizeLiveData.setValue(this.fileController.getFileSize(fileName));
        this.dateDoneLiveData.setValue(Calendar.getInstance().getTime());
    }

    public void addNewImage(Image image){
        image.setSize(sizeLiveData.getValue());
        image.setDateDone(dateDoneLiveData.getValue());
        this.imageRepository.addImage(image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        confirmVideoEventActionLiveData.sendAction(new ConfirmImageEvent());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    public void deleteUnusedFile(String fileName){
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
                        confirmVideoEventActionLiveData.sendAction(new ConfirmImageEvent());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }
}
