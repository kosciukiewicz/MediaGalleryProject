package com.witold.videoprojectapp.view.video_confirm_activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.witold.videoprojectapp.R;
import com.witold.videoprojectapp.model.video.Video;
import com.witold.videoprojectapp.view.main_activity.MainActivityViewModel;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import timber.log.Timber;

import static com.witold.videoprojectapp.view.ViewConstants.EXTRA_FILE_NAME;
import static com.witold.videoprojectapp.view.ViewConstants.RESULT_CODE_SUCCESS;

public class VideoConfirmActivity extends AppCompatActivity {
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private VideoConfirmViewModel videoConfirmViewModel;
    private String videoFileName;

    private EditText editTextTitle;
    private EditText editTextDescription;
    private Button buttonConfirm;
    private TextView textViewDateDone;
    private TextView textViewLenght;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_confirm);
        this.videoConfirmViewModel = ViewModelProviders.of(this, viewModelFactory).get(VideoConfirmViewModel.class);
        this.videoFileName = getIntent().getStringExtra(EXTRA_FILE_NAME);
        this.initializeComponents();
        this.observeViewModel();
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoFileName, MediaStore.Images.Thumbnails.MINI_KIND);
        imageView.setImageBitmap(bitmap);
        this.videoConfirmViewModel.loadInformation(videoFileName);
    }


    @Override
    public void onBackPressed() {
        videoConfirmViewModel.deleteUnusedFile(this.videoFileName);
    }

    private void initializeComponents() {
        this.editTextDescription = findViewById(R.id.edit_text_description);
        this.editTextTitle = findViewById(R.id.edit_text_title);
        this.textViewDateDone = findViewById(R.id.text_view_confirm_video_date_done);
        this.textViewLenght = findViewById(R.id.text_view_confirm_video_length);
        this.imageView = findViewById(R.id.image_view_confirm_video);
        this.buttonConfirm = findViewById(R.id.button_confirm_video);
        this.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextDescription.getText().toString().equals("") && !editTextTitle.getText().toString().equals("")) {
                    addVideo();
                } else {
                    Toast.makeText(getApplicationContext(), "Uzupełnij wszystkie pola", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addVideo() {
        Video video = new Video();
        video.setTitle(editTextTitle.getText().toString());
        video.setDescription(editTextDescription.getText().toString());
        video.setFileName(this.videoFileName);
        this.videoConfirmViewModel.addNewVideo(video);
    }

    private void observeViewModel() {
        this.videoConfirmViewModel.confirmVideoEventActionLiveData.observe(this, new Observer<ConfirmVideoEvent>() {
            @Override
            public void onChanged(@Nullable ConfirmVideoEvent confirmVideoEvent) {
                setResult(RESULT_CODE_SUCCESS);
                finish();
            }
        });

        this.videoConfirmViewModel.dateDoneLiveData.observe(this, new Observer<Date>() {
            @Override
            public void onChanged(@Nullable Date date) {
                textViewDateDone.setText(formatDateDone(date));
            }
        });

        this.videoConfirmViewModel.lengthLiveData.observe(this, new Observer<Long>() {
            @Override
            public void onChanged(@Nullable Long length) {
                textViewLenght.setText(formatVideoLength(length));
            }
        });
    }

    private String formatVideoLength(Long length) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(':');
        DecimalFormat df = new DecimalFormat("0.00", symbols);
        return df.format(length * 1.0 / 1000);
    }

    private String formatDateDone(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
        return simpleDateFormat.format(date);
    }
}
