package com.sevenflying.greenhouseclient.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.sevenflying.greenhouseclient.net.Constants;


/** Manages the image loading in background.
 * Created by flying on 27/03/15.
 */
public class ImageLoader {

    private Context context;
    private static LruCache<String, Bitmap> memoryCache = null;

    public ImageLoader(Context context) {
        this.context = context;
        if (memoryCache == null) {
            // See: https://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
            // The max mem is given in kb
            final int maxMem = (int) (Runtime.getRuntime().maxMemory() / 1024);
            memoryCache = new LruCache<String, Bitmap>(maxMem / 8) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount() / 1024; // we use kb
                }
            };
        }
    }

    /** Loads a resource to a ImageView using a background task
     * @param resid - resource id
     * @param imageView - the resource is set to this imageView
     */
    public void loadBitmapResource(int resid, ImageView imageView) {
        final String key = String.valueOf(resid);
        final Bitmap bitmap = getBitmapFromMemoryCache(key);
        // Set the cached one if found
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            Log.i(Constants.DEBUGTAG, " $ (bit-res) The cache has: " + key);
        } else {
            Log.i(Constants.DEBUGTAG, " $ (bit-res) The cache hasn't: " + key + "\n\t requiring");
            // Load and update cache (in the task)
            BitmapResourceWorkerTask task = new BitmapResourceWorkerTask(imageView, context, this);
            task.execute(resid);
        }

    }

    /** Loads an image to a ImageView using a backgroud task
     * @param path - image path
     * @param imageView - the file is set to this imageView
     * @param whileResource - quick resource to load while the task is still executing
     */
    public void loadBitmapFile(String path, ImageView imageView, int whileResource) {
        // Check whether the image is already cached
        final Bitmap bitmap = getBitmapFromMemoryCache(path);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            Log.i(Constants.DEBUGTAG, " $ (bit-file) The cache has: " + path);
        }
        else {
            Log.i(Constants.DEBUGTAG, " $ (bit-file) The hasn't: " + path);
            // Check tasks
            if (cancelPotentialWorkFile(path, imageView)) {

                Log.i(Constants.DEBUGTAG, " $ (bit-file) Requiring bitmap via async: " + path);
                // When the task finishes we will have the image in the cache
                BitmapFileWorkerTask task = new BitmapFileWorkerTask(imageView, path, this);
                AsyncResourceDrawable asyncResourceDrawable = new AsyncResourceDrawable(
                        context.getResources(), BitmapFactory.decodeResource(context.getResources(),
                        whileResource), task);
                imageView.setImageDrawable(asyncResourceDrawable);
                task.execute();
            }
        }
    }

    /** If it is already a task for the given file, cancel it.
     * @param data - data wanted to be loaded
     * @param imageView - where to load
     * @return true if the file has to be loaded, false if i
     */
    private static boolean cancelPotentialWorkFile(String data, ImageView imageView) {
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

    /** Adds a bitmap to the cache if it is not already there.
     * @param key
     * @param bitmap
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (memoryCache.get(key) == null) {
            memoryCache.put(key, bitmap);
            Log.i(Constants.DEBUGTAG, " $ Cache add: " + key);
        }
    }

    /** Returns a bitmap from cache given its key. Null if not found.
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemoryCache(String key) {
        Bitmap bitmap = memoryCache.get(key);
        if (bitmap == null)
            Log.i(Constants.DEBUGTAG, " $ Cache return: " + key + " FAIL");
        else
            Log.i(Constants.DEBUGTAG, " $ Cache return: " + key + " HIT");
        return bitmap;
    }
}
