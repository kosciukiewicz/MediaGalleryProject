package com.witold.videoprojectapp.model.video;

import com.witold.videoprojectapp.database.AppDatabase;
import com.witold.videoprojectapp.database.video_entity.VideoDao;
import com.witold.videoprojectapp.database.video_entity.VideoEntity;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;

public class VideoRepository {
    private VideoDao videoDao;

    @Inject
    public VideoRepository(AppDatabase appDatabase) {
        this.videoDao = appDatabase.videoDao();
    }

    public Single<List<Video>> getAllVideos(){
        return this.videoDao.getAllVideos().map(new Function<List<VideoEntity>, List<Video>>() {
            @Override
            public List<Video> apply(List<VideoEntity> videoEntities) {
                return videoEntities.stream().map(new java.util.function.Function<VideoEntity, Video>() {
                    @Override
                    public Video apply(VideoEntity videoEntity) {
                        return toVideo(videoEntity);
                    }
                }).collect(Collectors.<Video>toList());
            }
        });
    }

    public Completable addVideo(final Video video){
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                videoDao.insertVideo(toVideoEntity(video));
            }
        });
    }

    public Completable removeVideo(final Video video){
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                videoDao.deleteVideo(toVideoEntity(video));
            }
        });
    }

    private Video toVideo(VideoEntity videoEntity){
        return new Video(
               videoEntity.getId(),
               videoEntity.getTitle(),
               videoEntity.getDescription(),
               videoEntity.getDateDone(),
               videoEntity.getFileName(),
               videoEntity.getLength()
        );
    }

    private VideoEntity toVideoEntity(Video video){
        return new VideoEntity(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getDateDone(),
                video.getFileName(),
                video.getLength()
        );
    }
}
