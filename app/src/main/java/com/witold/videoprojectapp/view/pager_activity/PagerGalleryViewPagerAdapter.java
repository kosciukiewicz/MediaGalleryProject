package com.witold.videoprojectapp.view.pager_activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.witold.videoprojectapp.R;
import com.witold.videoprojectapp.model.image.Image;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.graphics.Bitmap.createBitmap;
import static android.media.ExifInterface.ORIENTATION_FLIP_HORIZONTAL;
import static android.media.ExifInterface.ORIENTATION_FLIP_VERTICAL;
import static android.media.ExifInterface.ORIENTATION_NORMAL;
import static android.media.ExifInterface.ORIENTATION_ROTATE_180;
import static android.media.ExifInterface.ORIENTATION_ROTATE_270;
import static android.media.ExifInterface.ORIENTATION_ROTATE_90;
import static android.media.ExifInterface.ORIENTATION_TRANSPOSE;
import static android.media.ExifInterface.ORIENTATION_TRANSVERSE;
import static android.media.ExifInterface.TAG_ORIENTATION;
import static com.witold.videoprojectapp.view.ViewConstants.VIEW_TAG;

public class PagerGalleryViewPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<Image> gridGalleryImageList;

    public PagerGalleryViewPagerAdapter(Context context, List<Image> gridGalleryImages) {
        this.context = context;
        this.layoutInflater = ((Activity) context).getLayoutInflater();
        this.gridGalleryImageList = gridGalleryImages;
    }

    public class ImagePageViewHolder {
        private ImageView imageView;
        private TextView textView;
        private TextView textViewDateDone;

        public ImagePageViewHolder(View view) {
            this.imageView = view.findViewById(R.id.singlePageImageView);
            this.textView = view.findViewById(R.id.singlePageTextView);
            this.textViewDateDone = view.findViewById(R.id.singlePageTextViewDateDone);
        }
    }

    @Override
    public int getCount() {
        return gridGalleryImageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.layout_single_page_image, container, false);
        ImagePageViewHolder pageViewHolder = new ImagePageViewHolder(itemView);
        Image gridGalleryImage = gridGalleryImageList.get(position);
        pageViewHolder.textView.setText(gridGalleryImage.getTitle());
        pageViewHolder.textViewDateDone.setText(formatDateDone(gridGalleryImage.getDateDone()));
        pageViewHolder.imageView.setImageBitmap(rotateBitmap(gridGalleryImage.getFileName(), BitmapFactory.decodeFile(gridGalleryImage.getFileName())));
        pageViewHolder.imageView.setTransitionName(VIEW_TAG + gridGalleryImage.getId());
        itemView.setTag(VIEW_TAG+gridGalleryImage.getId());
        container.addView(itemView);
        ((PagerHolder)context).startPostponedTransition(pageViewHolder.imageView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    private String formatDateDone(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    private Bitmap rotateBitmap(String src, Bitmap bitmap) {
        try {
            int orientation = new ExifInterface(src).getAttributeInt(TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();
            if (orientation == ORIENTATION_NORMAL) return bitmap;
            if (orientation == ORIENTATION_FLIP_HORIZONTAL) matrix.setScale(-1, 1);
            else if (orientation == ORIENTATION_ROTATE_180) matrix.setRotate(180);
            else if (orientation == ORIENTATION_FLIP_VERTICAL) {
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
            } else if (orientation == ORIENTATION_TRANSPOSE) {
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
            } else if (orientation == ORIENTATION_ROTATE_90) {
                matrix.setRotate(90);
            } else if (orientation == ORIENTATION_TRANSVERSE) {
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
            } else if (orientation == ORIENTATION_ROTATE_270) {
                matrix.setRotate(-90);
            } else return bitmap;
            try {
                Bitmap oriented = createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return oriented;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
