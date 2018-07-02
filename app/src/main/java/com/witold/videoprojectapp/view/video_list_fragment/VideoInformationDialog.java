package com.witold.videoprojectapp.view.video_list_fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.witold.videoprojectapp.R;
import com.witold.videoprojectapp.model.video.Video;
import com.witold.videoprojectapp.view.ViewConstants;
import com.witold.videoprojectapp.view.main_activity.sort_dialog.VideoSortDialog;
import com.witold.videoprojectapp.view.main_activity.sort_predicates.VideoSortPredicate;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class VideoInformationDialog extends DialogFragment {
    public static VideoInformationDialog getInstance() {
        return new VideoInformationDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_video_information, null,
                false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Video video = (Video) getArguments().getSerializable(ViewConstants.EXTRA_VIDEO);
        builder.setView(rootView);
        builder.setTitle("Informacje");
        this.setVideo(rootView, video);
        builder.setNegativeButton("Zamknij", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        // Do something else
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void setVideo(View view, Video video) {
        TextView textViewTitle = view.findViewById(R.id.text_view_video_information_title);
        TextView textViewDescription = view.findViewById(R.id.text_view_video_information_description);
        TextView textViewDateDone = view.findViewById(R.id.text_view_video_information_date_done);
        TextView textViewLength = view.findViewById(R.id.text_view_video_information_length);

        textViewTitle.setText(video.getTitle());
        textViewDescription.setText(video.getDescription());
        textViewLength.setText(this.formatVideoLength(video.getLength()));
        textViewDateDone.setText(this.formatDateDone(video.getDateDone()));
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
}
