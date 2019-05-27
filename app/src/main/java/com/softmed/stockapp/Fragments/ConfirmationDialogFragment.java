package com.softmed.stockapp.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Coze on 2016-08-08.
 */

public class ConfirmationDialogFragment extends DialogFragment {

    private static final String ARGS_TITLE_ID = "ARGS_TITLE_ID";
    private static final String ARGS_MESSAGE_ID = "ARGS_MESSAGE_ID";

    private DialogInterface.OnClickListener mOnPositiveClickListener;
    private DialogInterface.OnClickListener mOnNegativeClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    };

    public static ConfirmationDialogFragment newInstance(int titleId, int messageId) {
        Bundle args = new Bundle();
        args.putInt(ARGS_TITLE_ID, titleId);
        args.putInt(ARGS_MESSAGE_ID, messageId);
        ConfirmationDialogFragment dialogFragment = new ConfirmationDialogFragment();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    public void setOnPositiveClickListener(DialogInterface.OnClickListener listener) {
        mOnPositiveClickListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getInt(ARGS_TITLE_ID));
        builder.setMessage(getArguments().getInt(ARGS_MESSAGE_ID));
        builder.setPositiveButton(android.R.string.ok, mOnPositiveClickListener);
        builder.setNegativeButton(android.R.string.cancel, mOnNegativeClickListener);
        return builder.create();
    }

}