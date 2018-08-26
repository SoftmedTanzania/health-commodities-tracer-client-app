package com.softmed.rucodia.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Coze on 2016-08-04.
 */

public class PhotoHelper {

    public static final String CONTENT_URI_PREFIX = "content://";
    public static final String FILE_URI_PREFIX = "file://";

    /**
     * Helper method for creating a new blank File object in the private external directory.
     * The file will be saved at: /sdcard/Android/data/package_name/files/Pictures.
     * @param context - The activity which calls this method.
     * @return File - The blank new File object.
     * @throws IOException
     */
    public static File createPhotoFile(Context context) throws IOException {
        File photoFile = null;
        // Ensure that the external storage is available.
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // Use time stamp format to create a collision-proof file name.
            String timeStamp = new SimpleDateFormat("ddMMyy_HHmmss", Locale.getDefault()).format(new Date());
            String photoFileName = "IMG_" + timeStamp + "_";
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            photoFile = File.createTempFile(photoFileName, ".jpg", storageDir);
        }
        return photoFile;
    }

    /**
     * Helper method for getting a Bitmap object from a photo file path. Since the photo file is
     * either a captured image or a selected image, it is stored at a different location.
     * @param context - The activity which calls this method.
     * @param photoPath - The photo path where the photo file is located in the storage.
     * @return Bitmap - The generated Bitmap object.
     * @throws IOException
     */
    public static Bitmap getBitmapFromPhotoPath(Context context, String photoPath) throws IOException {
        Bitmap photoBitmap;
        // Determine how to get the Bitmap object based on the photo path.
        if (photoPath.contains(PhotoHelper.CONTENT_URI_PREFIX)) {
            // The photo file is a selected image from the gallery.
            photoBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(photoPath));
        } else {
            // The photo file is a captured image from the camera intent.
            photoBitmap = BitmapFactory.decodeFile(photoPath);
        }
        return photoBitmap;
    }

    /**
     * Helper method for deleting a previously captured photo file which was set to an ImageView.
     * The purpose of this method is to free up device storage by deleting unused image files.
     * @param tag - The ImageView tag where the file path is stored.
     */
    public static void deleteCapturedPhotoFile(Object tag) {
        // If tag is null, there's nothing to do. Return early.
        if (tag == null) return;
        String photoPath = tag.toString();
        // Make sure that the file is a captured photo not a selected image from the gallery.
        if (!photoPath.contains(CONTENT_URI_PREFIX)) {
            File photoFile = new File(photoPath);
            photoFile.delete();
        }
    }

    /**
     * Helper method for dispatching an ACTION_VIEW intent to open an image in full screen.
     * @param context - The activity which calls this method.
     * @param tag - The ImageView tag where the file path is stored.
     */
    public static void dispatchViewImageIntent(Context context, Object tag) {
        // If tag is null, there's nothing to do. Return early.
        if (tag == null) return;
        String photoPath = tag.toString();
        // Determine the valid photo uri for the view intent. There are two file path format
        // possibilities: "/storage/emulated/0/..." or "content://other_app_package_name/..."
        Uri photoUri = (photoPath.contains(CONTENT_URI_PREFIX) ? Uri.parse(photoPath) : Uri.parse(FILE_URI_PREFIX + photoPath));
        if (photoUri != null) {
            Intent viewImageIntent = new Intent(Intent.ACTION_VIEW);
            viewImageIntent.setDataAndType(photoUri, "image");
            context.startActivity(viewImageIntent);
        }
    }

}