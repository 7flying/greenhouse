package com.sevenflying.greenhouseclient.app.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.sevenflying.greenhouseclient.app.R;

/** Manages the image loading in background.
 * Created by flying on 27/03/15.
 */
public class ImageLoader {

    private Context context;

    public ImageLoader(Context context) {
        this.context = context;
    }

    /** Loads a resource to a ImageView using a background task
     * @param resid - resource id
     * @param imageView - the resource is set to this imageView
     */
    public void loadBitmapResource(int resid, ImageView imageView) {
        BitmapResourceWorkerTask task = new BitmapResourceWorkerTask(imageView,
                context);
        task.execute(resid);
    }

    /** Loads an image to a ImageView using a backgroud task
     * @param path - image path
     * @param imageView - the file is set to this imageView
     * @param whileResource - quick resource to load while the task is still executing
     */
    public void loadBitmapFile(String path, ImageView imageView, int whileResource) {
        if (cancelPotentialWorkFile(path, imageView)) {
            BitmapFileWorkerTask task = new BitmapFileWorkerTask(imageView, path);
            AsyncResourceDrawable asyncResourceDrawable = new AsyncResourceDrawable(
                    context.getResources(), BitmapFactory.decodeResource(context.getResources(),
                    whileResource), task);
            imageView.setImageDrawable(asyncResourceDrawable);
            task.execute();
        }
    }

    /** If it is already a task for the given file, cancel it.
     * @param data - data wanted to be loaded
     * @param imageView - where to load
     * @return
     */
    public static boolean cancelPotentialWorkFile(String data, ImageView imageView) {
        final BitmapFileWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.getPath();
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == null || !bitmapData.equals(data)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    /** Returns the worker the given image view has assigned.
     * @param imageView
     * @return
     */
    private static BitmapFileWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncResourceDrawable) {
                final AsyncResourceDrawable asyncDrawable = (AsyncResourceDrawable) drawable;
                return asyncDrawable.getBitmapWorker();
            }
        }
        return null;
    }
}
