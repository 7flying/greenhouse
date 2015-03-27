package com.sevenflying.greenhouseclient.app.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/** Follow Google developer guidelines and load bitmaps in a AsyncTask.
 * See:
 *  - https://developer.android.com/training/displaying-bitmaps/index.html
 * Created by flying on 27/03/15.
 */
public class BitmapFileWorkerTask extends AsyncTask<Void, Void, Bitmap> {

    private WeakReference<ImageView> imageViewToLoad;
    private String path = null;

    public BitmapFileWorkerTask(ImageView imageViewToLoad, String path) {
        this.imageViewToLoad = new WeakReference<ImageView>(imageViewToLoad);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        return decodeSampledBitmapFromFile(path, 100, 100);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled())
            bitmap = null;
        if (imageViewToLoad != null && bitmap != null) {
            ImageView image = imageViewToLoad.get();
            if (image != null)
                image.setImageBitmap(bitmap);
        }
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        Matrix m = new Matrix();
        m.postRotate(90);
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                            int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
