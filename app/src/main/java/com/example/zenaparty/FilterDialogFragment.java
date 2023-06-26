package com.example.zenaparty;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class FilterDialogFragment extends AppCompatDialogFragment {
    private CheckBox cbAll;
    private CheckBox cbMusic;
    private CheckBox cbSport;
    private CheckBox cbParties;
    private CheckBox cbSagre;
    private CheckBox cbOther;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.filter_dialog, null);

        cbAll = dialogView.findViewById(R.id.cbAll);
        cbMusic = dialogView.findViewById(R.id.cbMusic);
        cbSport = dialogView.findViewById(R.id.cbSport);
        cbParties = dialogView.findViewById(R.id.cbParties);
        cbSagre = dialogView.findViewById(R.id.cbSagre);
        cbOther = dialogView.findViewById(R.id.cbOther);

        // Imposta il listener per la checkbox "Tutte"
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Se la checkbox "Tutte" è selezionata, deseleziona le altre checkbox
                    cbMusic.setChecked(false);
                    cbSport.setChecked(false);
                    cbParties.setChecked(false);
                    cbSagre.setChecked(false);
                    cbOther.setChecked(false);
                }
            }
        });

        builder.setView(dialogView)
                .setTitle("Categoria eventi")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean filterAll = cbAll.isChecked();
                        boolean filterMusic = cbMusic.isChecked();
                        boolean filterSport = cbSport.isChecked();
                        boolean filterParties = cbParties.isChecked();
                        boolean filterSagre = cbSagre.isChecked();
                        boolean filterOther = cbOther.isChecked();

                    }
                }).setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Azione da eseguire quando si fa clic su annulla
                    }
                });
        return builder.create();
    }


    private void applyFilter() {
        boolean filterAll = cbAll.isChecked();
        boolean filterMusic = cbMusic.isChecked();
        boolean filterSport = cbSport.isChecked();
        boolean filterParties = cbParties.isChecked();
        boolean filterFestivals = cbSagre.isChecked();
        boolean filterOther = cbOther.isChecked();
    }
}