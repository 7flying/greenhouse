package com.sevenflying.greenhouseclient.app.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

/** Follow Google developer guidelines and handle concurrency by storing a reference back
 * to the worker task
 * See:
 *  - https://developer.android.com/training/displaying-bitmaps/index.html
 * Created by flying on 27/03/15.
 */
public class AsyncResourceDrawable extends BitmapDrawable{

    private WeakReference<BitmapResourceWorkerTask> bitmapWorkerTaskWeakReference;

    public AsyncResourceDrawable(Resources res, Bitmap bitmap, BitmapResourceWorkerTask bitmapWorker) {
        super(res, bitmap);
        bitmapWorkerTaskWeakReference = new WeakReference<BitmapResourceWorkerTask>(bitmapWorker);
    }

    public BitmapResourceWorkerTask getBitmapWorker() {
        return bitmapWorkerTaskWeakReference.get();
    }
}
