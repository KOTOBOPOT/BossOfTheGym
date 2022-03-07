package com.example.notetrainingapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class CustomDialogFragment extends DialogFragment {
    DialogInterface.OnClickListener positiveOnClickListener;
    DialogInterface.OnClickListener negativeOnClickListener;
    String dialogTitle;
    String dialogMessage;
    public CustomDialogFragment(DialogInterface.OnClickListener positiveOnClickListener, DialogInterface.OnClickListener negativeOnClickListener,
                                String dialogTitle, String dialogMessage ) {
        this.positiveOnClickListener = positiveOnClickListener;
        this.negativeOnClickListener = negativeOnClickListener;
        this.dialogTitle = dialogTitle;
        this.dialogMessage = dialogMessage;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setTitle(dialogTitle)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(dialogMessage)
                .setPositiveButton("OK", this.positiveOnClickListener)
                .setNegativeButton("Отмена", this.negativeOnClickListener)
                .create();
    }

}
