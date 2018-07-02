package com.witold.videoprojectapp.view.main_activity.sort_dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.witold.videoprojectapp.R;
import com.witold.videoprojectapp.view.ViewConstants;
import com.witold.videoprojectapp.view.main_activity.sort_predicates.ImageSortPredicate;
import com.witold.videoprojectapp.view.main_activity.sort_predicates.VideoSortPredicate;

import timber.log.Timber;

public class ImageSortDialog extends DialogFragment {
    public static ImageSortDialog getInstance() {
        return new ImageSortDialog();
    }

    private ImageSortDialogHolder imageSortDialogHolder;
    private int checkedOrder;
    private int checkedMode;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_image_sort, null,
                false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        RadioGroup radioGroup = rootView.findViewById(R.id.radio_group_sort_image_order);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_button_image_sort_asc) {
                    checkedOrder = ImageSortPredicate.ORDER_ASC;
                } else {
                    checkedOrder = ImageSortPredicate.ORDER_DESC;
                }
            }
        });

        RadioGroup radioGroupMode = rootView.findViewById(R.id.radio_group_sort_image_mode);
        radioGroupMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_button_image_sort_date_done:
                        checkedMode = ImageSortPredicate.MODE_DATE_DONE;
                        break;
                    case R.id.radio_button_image_sort_size:
                        checkedMode = ImageSortPredicate.MODE_SIZE;
                        break;
                    case R.id.radio_button_image_sort_title:
                        checkedMode = ImageSortPredicate.MODE_TITLE;
                        break;
                }
            }
        });

        ImageSortPredicate imageSortPredicate = (ImageSortPredicate) getArguments().getSerializable(ViewConstants.EXTRA_SORT_PREDICATE);
        setOrder(rootView, imageSortPredicate.getOrder());
        setMode(rootView, imageSortPredicate.getMode());
        Timber.d("asdasd");
        builder.setView(rootView);
        builder.setTitle("Sortuj...");
        builder.setPositiveButton("Potwierdź", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                imageSortDialogHolder.applyFilter(getFilterPredicate());
            }
        });
        // Do something else
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.imageSortDialogHolder = (ImageSortDialogHolder) context;
    }

    private ImageSortPredicate getFilterPredicate() {
        ImageSortPredicate imageSortPredicate = new ImageSortPredicate();
        imageSortPredicate.setOrder(this.checkedOrder);
        imageSortPredicate.setMode(this.checkedMode);
        return imageSortPredicate;
    }

    private void setOrder(View view, int order) {
        if (order == ImageSortPredicate.ORDER_ASC) {
            RadioButton radioButton = view.findViewById(R.id.radio_button_image_sort_asc);
            radioButton.toggle();
        } else {
            RadioButton radioButton = view.findViewById(R.id.radio_button_image_sort_desc);
            radioButton.toggle();
        }
    }

    private void setMode(View view, int mode) {
        RadioButton radioButton;
        switch (mode) {
            case ImageSortPredicate.MODE_DATE_DONE:
                radioButton = view.findViewById(R.id.radio_button_image_sort_date_done);
                radioButton.toggle();
                break;
            case ImageSortPredicate.MODE_SIZE:
                radioButton = view.findViewById(R.id.radio_button_image_sort_size);
                radioButton.toggle();
                break;
            case ImageSortPredicate.MODE_TITLE:
                radioButton = view.findViewById(R.id.radio_button_image_sort_title);
                radioButton.toggle();
                break;
        }
    }


    public interface ImageSortDialogHolder {
        void applyFilter(ImageSortPredicate imageSortPredicate);
    }
}
