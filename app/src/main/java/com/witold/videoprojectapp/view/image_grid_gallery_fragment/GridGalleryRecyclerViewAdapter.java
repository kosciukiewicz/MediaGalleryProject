package com.witold.videoprojectapp.view.image_grid_gallery_fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.witold.videoprojectapp.R;
import com.witold.videoprojectapp.model.image.Image;
import com.witold.videoprojectapp.model.video.Video;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

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

public class GridGalleryRecyclerViewAdapter extends RecyclerView.Adapter<GridGalleryRecyclerViewAdapter.GridImageViewHolder> {
    private GridHolder gridHolder;
    private List<Image> gridGalleryImages;
    private Picasso picasso;
    private int offset;
    private int colSpan;
    private int rowSpan;
    private int computedHeight = -1;
    private int computedWidth = -1;

    public GridGalleryRecyclerViewAdapter(GridHolder gridHolder, List<Image> gridGalleryImages, Picasso picasso, int offset, int colSpan, int rowSpan) {
        this.gridGalleryImages = gridGalleryImages;
        this.picasso = picasso;
        this.gridHolder = gridHolder;
        this.offset = offset;
        this.colSpan = colSpan;
        this.rowSpan = rowSpan;
    }

    public class GridImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView imageView;
        private CardView cardView;
        private int width;
        private int height;

        public GridImageViewHolder(View itemView, int width, int height) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.singleGridImageImageView);
            this.cardView = (CardView) itemView.findViewById(R.id.singleGridImageCardView);
            this.imageView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            this.width = width;
            this.height = height;
            this.imageView.setOnClickListener(this);
            this.imageView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            gridHolder.handleImageClick(gridGalleryImages.get(this.getAdapterPosition()), this.getAdapterPosition(), this.imageView, this.imageView.getTransitionName());
        }

        @Override
        public boolean onLongClick(View v) {
            gridHolder.onImageLongClick(gridGalleryImages.get(this.getAdapterPosition()), this.imageView);
            return false;
        }

        public void setImage(File file) {
         /*   imageView.setMinimumWidth(width);
            imageView.setMinimumHeight(height);
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER);*/

            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Bitmap bitmap = rotateBitmap(file.getAbsolutePath(), BitmapFactory.decodeFile(file.getAbsolutePath()));
            imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, width, height, false));
        }
    }

    public Bitmap getResizedBitmap(int targetW, int targetH, String imagePath) {

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        //inJustDecodeBounds = true <-- will not load the bitmap into memory
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        return rotateBitmap(imagePath, bitmap);
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

    @Override
    public GridImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_single_image, parent, false);
        Timber.d("NEW HOLDER " +  parent.getMeasuredHeight() + " " +         parent.getMeasuredWidth());
        parent.getMeasuredHeight();
        this.computedHeight = computeViewHolderHeight(parent.getMeasuredHeight());
        this.computedWidth = computeViewHolderWidth(parent.getMeasuredWidth());
        Timber.d(computedHeight + " " + computedWidth);
        return new GridImageViewHolder(view, this.computedWidth, this.computedHeight);
    }

    @Override
    public void onBindViewHolder(GridImageViewHolder holder, int position) {
        Image gridGalleryImage = this.gridGalleryImages.get(position);
        holder.setImage(new File(gridGalleryImage.getFileName()));
       /* picasso.load(R.drawable.sample_image_1)
                .resize(100, 100)
                .centerCrop()
                .into(holder.imageView);*/
        holder.imageView.setTransitionName(VIEW_TAG + gridGalleryImage.getId());
        holder.imageView.setTag(VIEW_TAG + gridGalleryImage.getId());
    }

    @Override
    public int getItemCount() {
        return gridGalleryImages.size();
    }

    public void updateDataSet(ArrayList<Image> images) {
        if (images.size() == this.getItemCount()) {
            Timber.d("update sort");
            updateSort(images);
        } else {
            Timber.d("update add");

            updateAdd(images);
        }
    }

    private void updateSort(ArrayList<Image> images) {
        List<Image> oldList = new ArrayList<>(this.gridGalleryImages);
        this.gridGalleryImages = images;
        for (int i = 0; i < oldList.size(); i++) {
            int index = images.indexOf(oldList.get(i));
            if (i != index) {
                Image image = oldList.get(i);
                oldList.remove(image);
                oldList.add(index, image);
                notifyItemMoved(i, index);
                i = 0;
            }
        }
    }

    private void updateAdd(ArrayList<Image> images) {
        this.gridGalleryImages = images;
        notifyDataSetChanged();
    }

    private int computeViewHolderHeight(int parentHeight) {
        return (parentHeight - (offset * (rowSpan - 1))) / rowSpan;
    }

    private int computeViewHolderWidth(int parentWidth) {
        return (parentWidth - (offset * (colSpan - 1))) / colSpan;
    }

    public List<Image> getImagesList() {
        return this.gridGalleryImages;
    }

    public interface GridHolder {
        public void handleImageClick(Image image, int position, ImageView imageView, String transitionName);

        public void onImageLongClick(Image image, ImageView imageView);
    }
}
