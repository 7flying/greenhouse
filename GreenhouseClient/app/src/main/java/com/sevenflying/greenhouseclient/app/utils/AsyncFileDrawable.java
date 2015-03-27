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
public class AsyncFileDrawable extends BitmapDrawable{

    private WeakReference<BitmapFileWorkerTask> bitmapWorkerTaskWeakReference;

    public AsyncFileDrawable(Resources res, Bitmap bitmap, BitmapFileWorkerTask bitmapWorker) {
        super(res, bitmap);
        bitmapWorkerTaskWeakReference = new WeakReference<BitmapFileWorkerTask>(bitmapWorker);
    }

    public BitmapFileWorkerTask getBitmapWorker() {
        return bitmapWorkerTaskWeakReference.get();
    }
}
