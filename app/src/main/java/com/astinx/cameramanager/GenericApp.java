package com.astinx.cameramanager;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GenericApp extends Application {
    private static final String TAG = GenericApp.class.toString();

    public static final String MY_APP = "Startup";
    public static final double RESPONSE_NEGATIVE_DOUBLE = -1d;

    protected static GenericApp singleton;

    private static final Set<String> RESPONSE_NEGATIVE_STRING_SET = new HashSet<String>();
    public static final Boolean RESPONSE_NEGATIVE_BOOLEAN = false;
    public static final int RESPONSE_NEGATIVE_INTEGER = -1;
    public static final float RESPONSE_NEGATIVE_FLOAT = -1f;
    public static final long RESPONSE_NEGATIVE_LONG = -1l;
    public static final String RESPONSE_NEGATIVE_STRING = null;

    private static WindowManager mWindowsManager;
    private static DisplayMetrics metrics;

    private static String PATH;
    private static String PATH_DATA;
    private static final String PATH_TEMP = "temp";

    private static File directoryTemp;
    private static File directoryDataTemp;

    private static File fileTemp;

    public static GenericApp getInstance() {
        if (singleton == null) {
            Log.e(TAG, "Instance null");
        }
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        mWindowsManager = (WindowManager) getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        getDisplayMetrics();
    }

    private static String getPath() {
        if (PATH == null) {
            try {
                PATH = getInstance().getExternalCacheDir().getPath();
            } catch (Exception e) {
                PATH = Environment.getExternalStorageDirectory() + "/Android/data/"
                        + getInstance().getPackageName() + "/cache/temp";
            }
        }
        return PATH;
    }

    private static String getPathData() {
        if (PATH_DATA == null) {
            try {
                PATH_DATA = getInstance().getPackageManager().getPackageInfo(
                        getInstance().getPackageName(), 0).applicationInfo.dataDir;
            } catch (Exception e) {
                PATH_DATA = "/data/data/" + getInstance().getPackageName();
            }
            if (PATH_DATA == null) {
                PATH_DATA = PATH;
            }
        }
        return PATH_DATA;
    }

    public static File getDirectoryTemp() {
        if (directoryTemp == null) {
            directoryTemp = new File(getPath() + File.separator + PATH_TEMP);
            directoryTemp.mkdirs();
        }
        return directoryTemp;
    }

    public static File getFileTemp() {
        if (fileTemp == null) {
            fileTemp = new File(App.getDirectoryTemp().getAbsolutePath() + File.separator
                    + "image.jpg");
            fileTemp.getParentFile().mkdirs();
            try {
                fileTemp.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileTemp;
    }

    public static File getDirectoryDataTemp() {
        if (directoryDataTemp == null) {
            directoryDataTemp = new File(getPathData() + File.separator + PATH_TEMP);
            directoryDataTemp.mkdirs();
        }
        return directoryDataTemp;
    }

    private static DisplayMetrics getDisplayMetrics() {
        metrics = new DisplayMetrics();
        if (mWindowsManager != null) {
            mWindowsManager.getDefaultDisplay().getMetrics(metrics);
        }
        return metrics;
    }

    public static Float getDisplayDensity() {
        if (metrics != null) {
            return metrics.density;
        } else {
            return null;
        }
    }

    public static Float getDisplayDpiY() {
        if (getDisplayMetrics() != null) {
            return metrics.xdpi;
        } else {
            return null;
        }
    }

    public static Float getDisplayDpiX() {
        if (getDisplayMetrics() != null) {
            return metrics.ydpi;
        } else {
            return null;
        }
    }

    public static Integer getDisplayWidth() {
        if (getDisplayMetrics() != null) {
            return metrics.widthPixels;
        } else {
            return null;
        }
    }

    public static Integer getDisplayHeight() {
        if (getDisplayMetrics() != null) {
            return metrics.heightPixels;
        } else {
            return null;
        }
    }

    public static void clearPreferences() {
        if (getInstance() != null) {
            SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();
        }
    }

    public static boolean getBoolean(String key) {
        if (getInstance() == null) {
            return false;
        }
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        return settings.getBoolean(key, RESPONSE_NEGATIVE_BOOLEAN);
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean hasKey(String key) {
        return ((getInstance() != null) && getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE).contains(key));
    }

    public static long getIntAsLong(String key) {
        if (getInstance() == null) {
            return RESPONSE_NEGATIVE_INTEGER;
        }
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        return settings.getLong(key, RESPONSE_NEGATIVE_INTEGER);
    }

    public static int getInt(String key) {
        if (getInstance() == null) {
            return RESPONSE_NEGATIVE_INTEGER;
        }
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        return settings.getInt(key, RESPONSE_NEGATIVE_INTEGER);
    }

    public static void putInt(String key, int value) {
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static float getFloat(String key) {
        if (getInstance() == null) {
            return RESPONSE_NEGATIVE_FLOAT;
        }
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        return settings.getFloat(key, RESPONSE_NEGATIVE_FLOAT);
    }

    public static void putFloat(String key, float value) {
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static long getLong(String key) {
        if (getInstance() == null) {
            return RESPONSE_NEGATIVE_LONG;
        }
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        return settings.getLong(key, RESPONSE_NEGATIVE_LONG);
    }

    public static void putLong(String key, long value) {
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static String getString(String key) {
        if (getInstance() == null) {
            return RESPONSE_NEGATIVE_STRING;
        }
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        return settings.getString(key, RESPONSE_NEGATIVE_STRING);
    }

    public static Set<String> getStringSet(String key) {
        if (getInstance() == null) {
            return RESPONSE_NEGATIVE_STRING_SET;
        }
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        return settings.getStringSet(key, RESPONSE_NEGATIVE_STRING_SET);
    }


    public static void putString(String key, String value) {
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void putStringSet(String key, Set<String> value) {
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public static Boolean remove(String key) {
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key);
        return editor.commit();
    }

    public static void putDouble(String key, double value) {
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, Double.doubleToLongBits(value));
        editor.commit();
    }

    public static double getDouble(String key) {
        if (getInstance() == null) {
            return RESPONSE_NEGATIVE_DOUBLE;
        }
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        return Double.longBitsToDouble(settings.getLong(key, RESPONSE_NEGATIVE_LONG));
    }

    public static void deleteSharedsPreferences() {
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        settings.edit().clear().commit();
    }

    public static void delete(String key) {
        SharedPreferences settings = getInstance().getSharedPreferences(MY_APP, MODE_PRIVATE);
        settings.edit().remove(key).commit();
    }
}
