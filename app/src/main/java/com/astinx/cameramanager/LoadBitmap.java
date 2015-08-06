package com.astinx.cameramanager;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by RegFacu on 2014-09-05.
 */
public abstract class LoadBitmap {
    public final Bitmap get(Uri uri, Context aContext) {
        return get(getOptions(uri, aContext), uri, aContext);
    }

    protected Bitmap get(BitmapFactory.Options options, Uri uri, Context aContext)  {
        return null;
    }

    protected BitmapFactory.Options getOptions(Uri uri, Context aContext) {
        return null;
    }

    public  Bitmap get(BitmapFactory.Options options) {
        return null;
    }

    public  BitmapFactory.Options getOptions() {
        return null;
    }

    public String getRealPathFromURI(Context ctx, Uri contentURI) {
        String result;
        Cursor cursor = ctx.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
