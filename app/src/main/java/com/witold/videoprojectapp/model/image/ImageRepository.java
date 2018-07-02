package com.witold.videoprojectapp.model.image;

import com.witold.videoprojectapp.database.AppDatabase;
import com.witold.videoprojectapp.database.image_entity.ImageDao;
import com.witold.videoprojectapp.database.image_entity.ImageEntity;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;

public class ImageRepository {
    private ImageDao imageDao;

    @Inject
    public ImageRepository(AppDatabase appDatabase) {
        this.imageDao = appDatabase.imageDao();
    }

    public Single<List<Image>> getAllImages(){
        return this.imageDao.getAllImages().map(new Function<List<ImageEntity>, List<Image>>() {
            @Override
            public List<Image> apply(List<ImageEntity> imageEntities) {
                return imageEntities.stream().map(new java.util.function.Function<ImageEntity, Image>() {
                    @Override
                    public Image apply(ImageEntity imageEntity) {
                        return toImage(imageEntity);
                    }
                }).collect(Collectors.<Image>toList());
            }
        });
    }

    public Completable addImage(final Image image){
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                imageDao.insertImage(toImageEntity(image));
            }
        });
    }

    public Completable removeImage(final Image image){
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                imageDao.deleteImage(toImageEntity(image));
            }
        });
    }
    private Image toImage(ImageEntity imageEntity){
        return new Image(
                imageEntity.getId(),
                imageEntity.getTitle(),
                imageEntity.getDescription(),
                imageEntity.getDateDone(),
                imageEntity.getFileName(),
                imageEntity.getSize()
        );
    }

    private ImageEntity toImageEntity(Image image){
        return new ImageEntity(
                image.getId(),
                image.getTitle(),
                image.getDescription(),
                image.getDateDone(),
                image.getFileName(),
                image.getSize()
        );
    }
}
