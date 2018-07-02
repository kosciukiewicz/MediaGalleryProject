package com.witold.videoprojectapp.view.image_confirm_activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.witold.videoprojectapp.R;
import com.witold.videoprojectapp.model.image.Image;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.witold.videoprojectapp.view.ViewConstants.EXTRA_FILE_NAME;
import static com.witold.videoprojectapp.view.ViewConstants.RESULT_CODE_SUCCESS;

public class ImageConfirmActivity extends AppCompatActivity {
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    Picasso picasso;

    private ImageConfirmViewModel imageConfirmViewModel;
    private String imageFileName;

    private EditText editTextTitle;
    private EditText editTextDescription;
    private Button buttonConfirm;
    private TextView textViewDateDone;
    private TextView textViewSize;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_confirm);
        this.imageConfirmViewModel = ViewModelProviders.of(this, viewModelFactory).get(ImageConfirmViewModel.class);
        this.imageFileName = getIntent().getStringExtra(EXTRA_FILE_NAME);
        this.initializeComponents();
        this.observeViewModel();
        this.imageConfirmViewModel.loadInformation(imageFileName);
        this.picasso.load(new File(imageFileName)).into(imageView);
    }

    @Override
    public void onBackPressed() {
        imageConfirmViewModel.deleteUnusedFile(this.imageFileName);
    }

    private void initializeComponents() {
        this.editTextDescription = findViewById(R.id.edit_text_confirm_image_description);
        this.editTextTitle = findViewById(R.id.edit_text_confirm_image_title);
        this.textViewDateDone = findViewById(R.id.text_view_confirm_image_date_done);
        this.textViewSize = findViewById(R.id.text_view_confirm_image_size);
        this.imageView = findViewById(R.id.image_view_confirm_image);
        this.buttonConfirm = findViewById(R.id.button_confirm_image);
        this.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editTextDescription.getText().toString().equals("") &&  !editTextTitle.getText().toString().equals("")){
                    addImage();
                }else {
                    Toast.makeText(getApplicationContext(), "Uzupełnij wszystkie pola", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void addImage() {
        Image image = new Image();
        image.setTitle(editTextTitle.getText().toString());
        image.setDescription(editTextDescription.getText().toString());
        image.setFileName(this.imageFileName);
        this.imageConfirmViewModel.addNewImage(image);
    }

    private void observeViewModel() {
        this.imageConfirmViewModel.confirmVideoEventActionLiveData.observe(this, new Observer<ConfirmImageEvent>() {
            @Override
            public void onChanged(@Nullable ConfirmImageEvent confirmImageEvent) {
                setResult(RESULT_CODE_SUCCESS);
                finish();
            }
        });

        this.imageConfirmViewModel.dateDoneLiveData.observe(this, new Observer<Date>() {
            @Override
            public void onChanged(@Nullable Date date) {
                textViewDateDone.setText(formatDateDone(date));
            }
        });

        this.imageConfirmViewModel.sizeLiveData.observe(this, new Observer<Long>() {
            @Override
            public void onChanged(@Nullable Long length) {
                textViewSize.setText(formatVideoLength(length) + "kb");
            }
        });
    }

    private String formatVideoLength(Long length) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        DecimalFormat df = new DecimalFormat("0.00", symbols);
        return df.format(length * 1.0 / 1000);
    }

    private String formatDateDone(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

}
