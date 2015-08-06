package com.astinx.cameramanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jonatan on 18/09/2014.
 */
public class CameraManager implements Serializable {

    private static final String TAG = "CameraManager";

    private static final String SAVE_INSTANCE_TEMP_FILE = "SAVE_INSTANCE_TEMP_FILE";
    private static final String SAVE_INSTANCE_WIDTH = "SAVE_INSTANCE_WIDTH";
    private static final String SAVE_INSTANCE_ID = "SAVE_INSTANCE_ID";

    private static final String SAVE_INSTANCE_HEIGHT = "SAVE_INSTANCE_HEIGHT";
    public static final String ADVERT_FOLDER = "photos";

    private static final String FILE_EXT = ".jpg";
    private static final String FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "Startup" + File.separator + "%1$s" + File.separator + "%2$s" + FILE_EXT;
    public static final String INTENT_ACTION = "CamaraManager";
    public static final String INTENT_ACTION_ID = "Id";
    public static final String INTENT_ACTION_RESULT = "Result";

    private File tempFile;

    private int mWidth;
    private int mHeight;
    private final String mImageFolder;
    private String mID;

    public CameraManager(int aWidth, int aHeight, String aImageFolder, String mID) {
        this.mWidth = aWidth;
        this.mHeight = aHeight;
        this.mImageFolder = aImageFolder;
        this.mID = mID;
    }

    public void saveInstance(Bundle aBundle) {
        aBundle.putSerializable(SAVE_INSTANCE_TEMP_FILE, tempFile);
        aBundle.putInt(SAVE_INSTANCE_WIDTH, mWidth);
        aBundle.putInt(SAVE_INSTANCE_HEIGHT, mHeight);
        aBundle.putString(SAVE_INSTANCE_ID, mID);
    }

    public void restoreInstance(Bundle aSavedInstance) {
        tempFile = (File) aSavedInstance.getSerializable(SAVE_INSTANCE_TEMP_FILE);
        mWidth = aSavedInstance.getInt(SAVE_INSTANCE_WIDTH);
        mHeight = aSavedInstance.getInt(SAVE_INSTANCE_HEIGHT);
        mID = aSavedInstance.getString(SAVE_INSTANCE_ID);
    }

    public void getImageFromCamera(Activity aContext) {
        try {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            tempFile = App.getFileTemp();
            tempFile.mkdirs();
            cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            aContext.startActivityForResult(Intent.createChooser(cameraIntent, aContext.getString(R.string.intent_chooser_camera_title)), NewPublicationActivity.REQUEST_CODE_CAMERA);
        } catch (Exception e) {
            Toast.makeText(aContext, aContext.getString(R.string.alert_dialog_error_open_camera), Toast.LENGTH_SHORT).show();
        }
    }

    public void getImageFromGallery(Activity aContext) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //TODO Read this http://stackoverflow.com/questions/19068842/can-we-use-intent-extra-allow-multiple-for-older-versions-of-android-api-levels
        if (Build.VERSION.SDK_INT > 15) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        }

        aContext.startActivityForResult(
                Intent.createChooser(intent, aContext.getString(R.string.intent_chooser_gallery_title)),
                NewPublicationActivity.REQUEST_CODE_GALLERY);
    }

    public boolean onCameraActivityResult(final Context aContext, int requestCode, int resultCode, final Intent data) {
        boolean aResult = Boolean.FALSE;
        switch (requestCode) {
            case NewPublicationActivity.REQUEST_CODE_CAMERA:
                aResult = Boolean.TRUE;
                if (resultCode == Activity.RESULT_OK) {
                    Set<Uri> uris = new HashSet<Uri>();
                    uris.add(Uri.fromFile(tempFile));
                    if (tempFile != null) {
                        imageProcessing(aContext, mImageLoaderCamera, uris, Boolean.FALSE);
                    } else {
                        Toast.makeText(aContext, aContext.getString(R.string.error_loading_image), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case NewPublicationActivity.REQUEST_CODE_GALLERY:
                aResult = Boolean.TRUE;
                if (resultCode == Activity.RESULT_OK) {
                    Set<Uri> uris = new HashSet<Uri>();
                    //TODO Read this http://stackoverflow.com/questions/19068842/can-we-use-intent-extra-allow-multiple-for-older-versions-of-android-api-levels
                    if (Build.VERSION.SDK_INT > 15 && data.getClipData() != null) {
                        int max = data.getClipData().getItemCount();
                        for (int index = 0 ; index < max; index++) {
                            uris.add(data.getClipData().getItemAt(index).getUri());
                        }
                    } else {
                        uris.add(data.getData());
                    }
                    imageProcessing(aContext, mImageLoader, uris, Boolean.TRUE);
                }
                break;
        }
        return aResult;
    }

    private final LoadBitmap mImageLoaderCamera = new LoadBitmap() {
        @Override
        protected BitmapFactory.Options getOptions(Uri uri, Context aContext) {
            return Storage.ImageProcessing.getOptions(aContext, tempFile, mWidth, mHeight);
        }

        @Override
        protected Bitmap get(BitmapFactory.Options options, Uri uri, Context aContext) {
            if (options == null) {
                return BitmapFactory.decodeFile(tempFile.getAbsolutePath());
            }
            return BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        }
    };

    private final LoadBitmap mImageLoader = new LoadBitmap() {
        @Override
        public Bitmap get(BitmapFactory.Options options, Uri uri, Context aContext) {
            try {
                if (options == null) {
                    return BitmapFactory.decodeStream(aContext.getContentResolver().openInputStream(uri));
                }
                return BitmapFactory.decodeStream(aContext.getContentResolver().openInputStream(uri), null, options);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        public BitmapFactory.Options getOptions(Uri uri, Context aContext) {
            InputStream aInputStream = null;
            try {
                aInputStream = aContext.getContentResolver().openInputStream(uri);
                return Storage.ImageProcessing.getOptions(aContext, aInputStream, mWidth, mHeight);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }

        }
    };

    protected void imageProcessing(final Context aContext, LoadBitmap loadBitmap, final Set<Uri> aImageUris, final boolean comesFromGallery) {
        new AsyncTask<LoadBitmap, Void, HashSet<String>>() {
            @Override
            protected void onPreExecute() {
                // TODO4 Mostrar en pantalla que se está procesando
            }

            @Override
            protected HashSet<String> doInBackground(LoadBitmap... params) {
                LoadBitmap l = params[0];
                HashSet<String> paths = new HashSet<String>();
                for (Uri aImageUri : aImageUris) {
                    processSingleImageUri(paths, aImageUri, l);
                }
                return paths;
            }

            private void processSingleImageUri(HashSet<String> paths, Uri aImageUri, LoadBitmap loadBitmap) {
                String path = null;
                Bitmap finalBitmap = null;
                Bitmap bitmap = null;
                try {
                    bitmap = loadBitmap.get(aImageUri, aContext);
                    // TODO1 Verificar dimensiones. El ancho/alto no puede superar los 800x1200 o 1200x800
                    finalBitmap = Storage.ImageProcessing
                            .createNewImage(aContext, bitmap, Storage.ImageProcessing
                                    .addRotationMatrix(
                                            Storage.ImageProcessing.calculateRotation(aContext, aImageUri), bitmap.getWidth(), bitmap.getHeight(),
                                            Storage.ImageProcessing.addScaleMatrix(
                                                    Storage.ImageProcessing.getScale(
                                                            bitmap.getWidth(),
                                                            bitmap.getHeight(),
                                                            mHeight), bitmap
                                                            .getWidth(), bitmap.getHeight()
                                                    , Storage.ImageProcessing.generateMatrix()
                                            )
                                    ));
                    if (bitmap != null && !bitmap.isRecycled() && bitmap != finalBitmap) {
                        bitmap.recycle();
                    }

                    String filePath = String.format(FILE_PATH, mImageFolder, new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(Calendar.getInstance().getTime()));
                    new File(filePath).getParentFile().mkdirs();
                    path = Storage.Insert.storeImage(finalBitmap, filePath);
                } catch (OutOfMemoryError e) {
                    Log.e(TAG, e.getMessage(), e);
                    System.gc();
                    path = null;
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    path = null;
                } finally {
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    if (finalBitmap != null && !finalBitmap.isRecycled()) {
                        finalBitmap.recycle();
                    }
                }
                paths.add(path);
            }

            @Override
            protected void onPostExecute(HashSet<String> result) {
                if (!result.isEmpty()) {
                    App.getInstance().sendBroadcast(getIntent(mID, result));
                }
            }
        }.execute(loadBitmap);
    }

    public static Intent getIntent(String id, HashSet<String> result) {
        Intent intent = new Intent();
        intent.setAction(CameraManager.INTENT_ACTION);
        intent.putExtra(CameraManager.INTENT_ACTION_ID, id);
        intent.putExtra(CameraManager.INTENT_ACTION_RESULT, result);
        return intent;
    }

    public static abstract class CameraManagerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context aContext, Intent intent) {
            String id = intent.getStringExtra(CameraManager.INTENT_ACTION_ID);
            HashSet<String> result = (HashSet<String>) intent.getSerializableExtra(CameraManager.INTENT_ACTION_RESULT);
            if (!result.isEmpty()) {
                onResultOK(id, result);
            } else {
                onResultError(id);
            }
        }

        protected abstract void onResultOK(String aId, HashSet<String> aResult);

        protected abstract void onResultError(String aId);
    }
}