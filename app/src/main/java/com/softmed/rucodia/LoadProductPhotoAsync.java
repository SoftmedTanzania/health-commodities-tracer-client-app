package com.softmed.rucodia;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.softmed.rucodia.helper.PhotoHelper;

import java.io.IOException;

/**
 * Created by Coze on 2016-08-09.
 */

public class LoadProductPhotoAsync extends AsyncTask<String, Void, Bitmap> {

    private static final String LOG_TAG = LoadProductPhotoAsync.class.getSimpleName();

    private Context mContext;
    private ImageView mProductPhotoImageView;

    public LoadProductPhotoAsync(Context context, ImageView productPhotoImageView) {
        mContext = context;
        mProductPhotoImageView = productPhotoImageView;
    }

    @Override
    protected Bitmap doInBackground(String... args) {
        Bitmap photoBitmap = null;
        try {
            photoBitmap = PhotoHelper.getBitmapFromPhotoPath(mContext, args[0]);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return photoBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null) mProductPhotoImageView.setImageBitmap(bitmap);
    }

}