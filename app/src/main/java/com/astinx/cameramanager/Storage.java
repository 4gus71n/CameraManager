package com.astinx.cameramanager;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Storage {

    public static class Delete {

//		public static void DeleteMessageResources(List<MessageResource> messages){
//			for(MessageResource aMessage : messages){
//				new File(aMessage.getPath()).delete();
//			}
//		}

    }

    public static class Insert {

        public static String storeImage(Bitmap bitmap, String path) throws IOException {
            return storeImage(bitmap, path, Bitmap.CompressFormat.JPEG, 70);
        }

        public static String storeImage(Bitmap bitmap, String path, Bitmap.CompressFormat format, int quality) throws IOException {
            FileOutputStream fos = new FileOutputStream(path);
            bitmap.compress(format, quality, fos);
            fos.flush();
            fos.close();
            return path;
        }

        public static File storeImage(Bitmap scaledBitmap, File aFile) throws IOException {
            FileOutputStream fos = new FileOutputStream(aFile);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
            fos.flush();
            fos.close();
            return aFile;
        }

    }

    public static class ImageProcessing {

        private static final String TAG = "IMAGE PROCESSING";

        public static class SecretProcessing {

            private static final float RATIO = 1.5f;

            public static boolean isNormalizedByWidth(float width, float height) {
                return (height / RATIO) < width;
            }

            public static float normalizedWidth(float width, float height) {
                if (isNormalizedByWidth(width, height)) {
                    return height / RATIO;
                } else {
                    return width;
                }
            }

            public static float normalizedHeight(float width, float height) {
                boolean normalizedByWidth = isNormalizedByWidth(width, height);
                if (normalizedByWidth) {
                    return height;
                } else {
                    return width * RATIO;
                }
            }

        }

        private static float getRatio(float decodedWidth, float decodedHeight) {
            return Math.max(decodedWidth, decodedHeight) / Math.min(decodedWidth, decodedHeight);
        }

        public static float getScale(float decodedWidth, float decodedHeight, float maxBound) {
            return ImageProcessing.getMinHeight(decodedWidth, decodedHeight, maxBound) / decodedHeight;
        }

        public static float getScaleMax(float decodedWidth, float decodedHeight, float maxBound) {
            return ImageProcessing.getMaxWidth(decodedWidth, decodedHeight, maxBound) / decodedWidth;
        }

        private static float getMinBoundForRatio(float maxBound, float ratio) {
            return (maxBound / ratio);
        }

        private static float getMaxBoundForRatio(float maxBound, float ratio) {
            return (maxBound * ratio);
        }

        private static float getMinWidth(float decodedWidth, float decodedeHeight, float maxBound) {
            if (isPortrait(decodedWidth, decodedeHeight)) {
                return getMinBoundForRatio(getMinHeight(decodedWidth, decodedeHeight, maxBound), getRatio(decodedWidth, decodedeHeight));
            } else {
                return Math.min(decodedWidth, maxBound);
            }
        }

        private static float getMinHeight(float decodedWidth, float decodedeHeight, float maxBound) {
            if (isPortrait(decodedWidth, decodedeHeight)) {
                return Math.min(decodedeHeight, maxBound);
            } else {
                return getMinBoundForRatio(getMinWidth(decodedWidth, decodedeHeight, maxBound), getRatio(decodedWidth, decodedeHeight));
            }
        }

        private static float getMaxWidth(float decodedWidth, float decodedeHeight, float maxBound) {
            if (isPortrait(decodedWidth, decodedeHeight)) {
                return Math.min(decodedWidth, maxBound);
            } else {
                return getMaxBoundForRatio(getMaxHeight(decodedWidth, decodedeHeight, maxBound), getRatio(decodedWidth, decodedeHeight));
            }
        }

        private static float getMaxHeight(float decodedWidth, float decodedeHeight, float maxBound) {
            if (isPortrait(decodedWidth, decodedeHeight)) {
                return getMaxBoundForRatio(getMaxWidth(decodedWidth, decodedeHeight, maxBound), getRatio(decodedWidth, decodedeHeight));
            } else {
                return Math.min(decodedeHeight, maxBound);

            }
        }

        private static boolean isPortrait(float decodedWidth, float decodedeHeight) {
            return (decodedeHeight > decodedWidth);
        }

        public static Matrix generateMatrix() {
            Matrix matrix = new Matrix();
            return matrix;
        }

        public static float calculateRotation(Context aContext, Uri path) {
            Float degrees = aprox(getOrientation(aContext, path));
            if (degrees == -1) {
                try {
                    int orientation = new ExifInterface(Util.getRealPathFromURI(aContext, path)).getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_NORMAL:
                            degrees = 0f;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            degrees = 90f;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            degrees = 180f;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            degrees = 270f;
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
            }
            return degrees;
        }

        public static Matrix addRotationMatrix(float degrees, float width, float height, Matrix matrix) {
            if (degrees != -1 && degrees % 360 != 0) {
                switch ((int) degrees % 360) {
                    case 90:
                        matrix.postRotate(degrees);
                        matrix.postTranslate(height, 0);
                        break;
                    case 180:
                        matrix.postRotate(degrees);
                        matrix.postTranslate(width, height);
                        break;
                    case 270:
                        matrix.postRotate(degrees);
                        matrix.postTranslate(0, width);
                        break;
                }
            }
            return matrix;
        }

        public static Matrix addScaleMatrix(float scale, float width, float height, Matrix matrix) {
            matrix.postScale(scale, scale, width / 2, height / 2);
            return matrix;
        }

        public static Matrix addScaleMatrix(float scaleX, float scaleY, float width, float height, Matrix matrix) {
            matrix.postScale(scaleX, scaleY, width / 2, height / 2);
            return matrix;
        }

        public static Matrix addScaleMatrix(float scaleX, float scaleY, Matrix matrix) {
            matrix.postScale(scaleX, scaleY);
            return matrix;
        }

        public static Matrix addTranslationMatrix(int x, int y, Matrix matrix) {
            matrix.postTranslate(x, y);
            return matrix;
        }

        public static float coordByOrientation(float degrees, float coord1, float coord2) {
            if (degrees == 90 || degrees == 270) {
                return coord2;
            } else {
                return coord1;
            }
        }

        public static float getOrientation(Context context, Uri photoUri) {
            float result = -1;
            try {
                Cursor cursor = context.getContentResolver().query(photoUri,
                        new String[]{MediaStore.Images.ImageColumns.ORIENTATION},
                        null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        result = cursor.getInt(0);
                    }
                    cursor.close();
                }
            } catch (IllegalArgumentException e) {
                Log.e(TAG, e.getMessage());
            }
            return result;
        }

        public static float aprox(float angle) {
            if (angle >= 0) {
                if (angle < 45) {
                    return 0;
                } else if (angle < 135) {
                    return 90;
                } else if (angle < 225) {
                    return 180;
                } else {
                    return 270;
                }
            } else {
                return angle;
            }
        }

        public static Bitmap createNewImage(Context context, Bitmap sourceBitmap, Matrix matrix) throws FileNotFoundException {
            return createNewImage(context, sourceBitmap, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix);
        }

        public static Bitmap createNewImage(Context context, Bitmap sourceBitmap, int width, int height, Matrix matrix) throws FileNotFoundException {
            return createNewImage(context, sourceBitmap, 0, 0, width, height, matrix);
        }

        public static Bitmap createNewImage(Context context, Bitmap sourceBitmap, int x, int y, int width, int height, Matrix matrix) throws FileNotFoundException {
            Bitmap newBitmap = Bitmap.createBitmap(sourceBitmap, x, y, width, height, matrix, true);
            if (newBitmap != sourceBitmap) {
                sourceBitmap.recycle();
                System.gc();
            }
            return newBitmap;
        }

        public static BitmapFactory.Options getOptions(Context context, File aFile, int width, int height) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(aFile.getAbsolutePath(), options);
            options.inSampleSize = ImageResizer.calculateInSampleSize(options, width, height);
            options.inJustDecodeBounds = false;
            return options;
        }

        public static BitmapFactory.Options getOptions(Context context, InputStream aInputStream, int width, int height) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(aInputStream, null, options);
            options.inSampleSize = ImageResizer.calculateInSampleSize(options, width, height);
            options.inJustDecodeBounds = false;
            return options;
        }

        public static Bitmap buildSquareImage(Context aContext, Bitmap b, float degrees, int size) throws FileNotFoundException {
            b = createNewImage(
                    aContext, b,
                    addScaleMatrix(
                            getScaleMax(
                                    coordByOrientation(degrees, b.getWidth(), b.getHeight()),
                                    coordByOrientation(degrees, b.getHeight(), b.getWidth()), size)
                            , coordByOrientation(degrees, b.getWidth(), b.getHeight())
                            , coordByOrientation(degrees, b.getHeight(), b.getWidth())
                            , addRotationMatrix(degrees, b.getWidth(), b.getHeight(), generateMatrix())));
            return createNewImage(aContext, b, Math.max(((b.getWidth() - size) / 2), 0), Math.max(((b.getHeight() - size) / 2), 0), size, size, null);
        }

        public static Bitmap rotateImage(Context aContext, Bitmap sourceBitmap, Uri path) throws FileNotFoundException {
            if (sourceBitmap != null) {
                return createNewImage(aContext, sourceBitmap, addRotationMatrix(calculateRotation(aContext, path), sourceBitmap.getWidth(), sourceBitmap.getHeight(), generateMatrix()));
            } else {
                return null;
            }
        }

        public static Bitmap resizeBitmap(Bitmap sourceBitmap, Float width, Float height) {
            Bitmap result = Bitmap.createBitmap(width.intValue(), height.intValue(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(sourceBitmap, addTranslationMatrix(0, (int) ((sourceBitmap.getHeight() - height) / -2),
                    generateMatrix()), new Paint());
            return result;
        }
    }

}
