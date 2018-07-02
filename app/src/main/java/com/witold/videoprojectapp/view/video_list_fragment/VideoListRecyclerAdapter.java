package com.witold.videoprojectapp.view.video_list_fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.witold.videoprojectapp.R;
import com.witold.videoprojectapp.model.video.Video;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VideoListRecyclerAdapter extends RecyclerView.Adapter<VideoListRecyclerAdapter.GridVideoViewHolder> {
    private Picasso picasso;
    private List<Video> videoList;
    private Context context;
    private VideoGridHolder videoGridHolder;

    public VideoListRecyclerAdapter(VideoGridHolder videoGridHolder, Context context, Picasso picasso, List<Video> videos) {
        this.context = context;
        this.videoGridHolder = videoGridHolder;
        this.picasso = picasso;
        this.videoList = videos;
    }

    public class GridVideoViewHolder extends RecyclerView.ViewHolder {
        public GridVideoViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final Video video) {
            ImageView imageViewThumbnail = itemView.findViewById(R.id.image_view_single_grid_video);
            TextView textViewTitle = itemView.findViewById(R.id.text_view_single_grid_video_title);
            TextView textViewVideoLength = itemView.findViewById(R.id.text_view_single_grid_video_length);
            TextView textViewDateDone = itemView.findViewById(R.id.text_view_single_grid_video_date_done);
            LinearLayout linearLayout = itemView.findViewById(R.id.linear_layout_single_video_holder);
            final ImageButton imageButtonMore = itemView.findViewById(R.id.button_single_video_more);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoGridHolder.handleVideoClick(video);
                }
            });
            linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    videoGridHolder.showMoreOptionsForVideo(video, imageButtonMore);
                    return false;
                }
            });
            imageButtonMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoGridHolder.showMoreOptionsForVideo(video, imageButtonMore);
                }
            });
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(video.getFileName(), MediaStore.Images.Thumbnails.MINI_KIND);
            textViewTitle.setText(video.getTitle());
            imageViewThumbnail.setImageBitmap(bitmap);
            textViewDateDone.setText(formatDateDone(video.getDateDone()));
            textViewVideoLength.setText(formatVideoLength(video.getLength()));
        }
    }

    private String formatVideoLength(Long length) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(':');
        DecimalFormat df = new DecimalFormat("0.00", symbols);
        return df.format(length * 1.0 / 1000);
    }

    private String formatDateDone(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    @NonNull
    @Override
    public GridVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_single_video, parent, false);
        return new GridVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridVideoViewHolder holder, int position) {
        holder.bind(this.videoList.get(position));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void updateDataSet(ArrayList<Video> videos) {
        if(videos.size() == this.getItemCount()){
            updateSort(videos);
        }else {
            updateAdd(videos);
        }
    }

    private void updateSort(ArrayList<Video> videos){
        List<Video> oldList = new ArrayList<>(this.videoList);
        this.videoList = videos;
        for (int i = 0; i < oldList.size(); i++) {
            int index = videos.indexOf(oldList.get(i));
            if (i != index) {
                Video video = oldList.get(i);
                oldList.remove(video);
                oldList.add(index, video);
                notifyItemMoved(i, index);
                i = 0;
            }
        }
    }

    private void updateAdd(ArrayList<Video> videos){
       this.videoList = videos;
       notifyDataSetChanged();
    }

    public interface VideoGridHolder{
        void handleVideoClick(Video video);
        void showMoreOptionsForVideo(Video video, ImageButton optionsHolder);
    }
}
