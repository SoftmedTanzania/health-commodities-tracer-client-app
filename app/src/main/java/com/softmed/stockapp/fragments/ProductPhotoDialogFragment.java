package com.softmed.stockapp.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import com.softmed.stockapp.R;

/**
 * Created by Coze on 2016-08-03.
 */

public class ProductPhotoDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_dialog_product_photo);
        builder.setItems(R.array.product_photo_dialog_options, (DialogInterface.OnClickListener) getActivity());
        return builder.create();
    }

}