package com.witold.videoprojectapp.view.main_activity.sort_dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.witold.videoprojectapp.R;
import com.witold.videoprojectapp.view.ViewConstants;
import com.witold.videoprojectapp.view.main_activity.sort_predicates.VideoSortPredicate;

import timber.log.Timber;

public class VideoSortDialog extends DialogFragment {
    public static VideoSortDialog getInstance() {
        return new VideoSortDialog();
    }

    private VideoSortDialogHolder videoSortDialogHolder;
    private int checkedOrder;
    private int checkedMode;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_video_sort, null,
                false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        RadioGroup radioGroup = rootView.findViewById(R.id.radio_group_sort_video_order);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_button_video_sort_asc) {
                    checkedOrder = VideoSortPredicate.ORDER_ASC;
                } else {
                    checkedOrder = VideoSortPredicate.ORDER_DESC;
                }
            }
        });

        RadioGroup radioGroupMode = rootView.findViewById(R.id.radio_group_sort_video_mode);
        radioGroupMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_button_video_sort_date_done:
                        checkedMode = VideoSortPredicate.MODE_DATE_DONE;
                        break;
                    case R.id.radio_button_video_sort_length:
                        checkedMode = VideoSortPredicate.MODE_LENGTH;
                        break;
                    case R.id.radio_button_video_sort_title:
                        checkedMode = VideoSortPredicate.MODE_TITLE;
                        break;
                }
            }
        });

        VideoSortPredicate videoSortPredicate = (VideoSortPredicate) getArguments().getSerializable(ViewConstants.EXTRA_SORT_PREDICATE);
        setOrder(rootView, videoSortPredicate.getOrder());
        setMode(rootView, videoSortPredicate.getMode());
        Timber.d("asdasd");
        builder.setView(rootView);
        builder.setTitle("Sortuj...");
        builder.setPositiveButton("Potwierdź", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                videoSortDialogHolder.applyFilter(getFilterPredicate());
            }
        });
        // Do something else
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.videoSortDialogHolder = (VideoSortDialogHolder) context;
    }

    private VideoSortPredicate getFilterPredicate() {
        VideoSortPredicate videoSortPredicate = new VideoSortPredicate();
        videoSortPredicate.setOrder(this.checkedOrder);
        videoSortPredicate.setMode(this.checkedMode);
        return videoSortPredicate;
    }

    private void setOrder(View view, int order) {
        if (order == VideoSortPredicate.ORDER_ASC) {
            RadioButton radioButton = view.findViewById(R.id.radio_button_video_sort_asc);
            radioButton.toggle();
        } else {
            RadioButton radioButton = view.findViewById(R.id.radio_button_video_sort_desc);
            radioButton.toggle();
        }
    }

    private void setMode(View view, int mode) {
        RadioButton radioButton;
        switch (mode) {
            case VideoSortPredicate.MODE_DATE_DONE:
                radioButton = view.findViewById(R.id.radio_button_video_sort_date_done);
                radioButton.toggle();
                break;
            case VideoSortPredicate.MODE_LENGTH:
                radioButton = view.findViewById(R.id.radio_button_video_sort_length);
                radioButton.toggle();
                break;
            case VideoSortPredicate.MODE_TITLE:
                radioButton = view.findViewById(R.id.radio_button_video_sort_title);
                radioButton.toggle();
                break;
        }
    }


    public interface VideoSortDialogHolder {
        void applyFilter(VideoSortPredicate videoSortPredicate);
    }
}
