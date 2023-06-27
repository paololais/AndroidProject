package com.example.zenaparty;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Objects;

public class FilterDialogFragment extends androidx.fragment.app.DialogFragment{
    private static final String TAG = "DialogFragment";

    private CheckBox cbMusic;
    private CheckBox cbSport;
    private CheckBox cbParties;
    private CheckBox cbSagre;
    private CheckBox cbOther;

    private FilterDialogListener listener;

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.filter_dialog, container,false);

        cbMusic = dialogView.findViewById(R.id.cbMusic);
        cbSport = dialogView.findViewById(R.id.cbSport);
        cbParties = dialogView.findViewById(R.id.cbParties);
        cbSagre = dialogView.findViewById(R.id.cbSagre);
        cbOther = dialogView.findViewById(R.id.cbOther);

        TextView mActionCancel = dialogView.findViewById(R.id.action_cancel);
        TextView mActionOk = dialogView.findViewById(R.id.action_ok);

        mActionCancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: closing dialog");
                        Objects.requireNonNull(getDialog()).dismiss();
                    }
                });

        mActionOk.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: capturing input");
                        boolean filterParties = cbParties.isChecked();
                        boolean filterFestivals = cbSagre.isChecked();
                        boolean filterMusic = cbMusic.isChecked();
                        boolean filterSport = cbSport.isChecked();
                        boolean filterOther = cbOther.isChecked();

                        if (listener != null) {
                            listener.onCheckboxSelected(filterParties, filterFestivals, filterMusic, filterSport, filterOther);
                        }
                        Objects.requireNonNull(getDialog()).dismiss();
                    }
                });
        return dialogView;
    }

    public void setOnInputListener(FilterDialogListener listener) {
        this.listener = listener;
    }
}