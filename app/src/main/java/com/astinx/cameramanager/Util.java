package com.astinx.cameramanager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by agustin on 01/12/14.
 */
public class Util {

        public static boolean isOnline(Context ctx) {
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public static String getRealPathFromURI(Context aContext, Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = aContext.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            if (contentUri == null) {
                return null;
            }
            return contentUri.getPath();
        }
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (android.content.pm.Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    public static void hideKeyboard(Context context){
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public static Date getRandomDate(boolean forward) {
        Calendar c = Calendar.getInstance();
        Random r = new Random();
        c.roll(Calendar.DAY_OF_YEAR, (forward)?(1):(-1) * r.nextInt(3));
        return c.getTime();
    }

    /**
     * The SwipeRefreshLayout has a bug that when we first load an activity or either a fragment,
     * if we want to display the refresh spinner first of all, it will not be show, because the
     * spinner will be shown behind the ActionBar or some another view. This method puts the
     * spinner in a fixed place.
     *
     * @param refreshing True if you want to show the spinner, false otherwise.
     * @param vSwipeLayout The view.
     *
     * */
    public static void setSwipeRefreshLayout(SwipeRefreshLayout vSwipeLayout, Boolean refreshing) {
        if (vSwipeLayout == null) throw new IllegalArgumentException("You are trying to use a SwipeRefreshLayout that is null!");
        if (refreshing && vSwipeLayout.isRefreshing()) return;
        vSwipeLayout.setProgressViewOffset(false, -10, 50);
        vSwipeLayout.setRefreshing(refreshing);
    }

    public static void checkThreadUiException() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            throw new RuntimeException("You are running a heavy task in the UI Thread!");
        }
    }

    public static String timeZone()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        String   timeZone = new SimpleDateFormat("Z").format(calendar.getTime());
        return timeZone.substring(0, 3) + ":"+ timeZone.substring(3, 5);
    }



    public static PackageInfo getPackageInfo(Context context) {
        return getPackageInfo(context, 0);
    }

    public static PackageInfo getPackageInfo(Context context, int flags) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), flags);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return pInfo;
    }

    private static Point getSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private static double getScreenSizeInInches(Context context) {
        try {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            double density = dm.density * 160;
            double x = Math.pow(dm.widthPixels / density, 2);
            double y = Math.pow(dm.heightPixels / density, 2);
            return Math.sqrt(x + y);
        } catch (Exception e) {
            return -1d;
        }

    }

    private static boolean hasWifi(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI);
    }

    private static boolean hasGps(Context context) {
        PackageManager packageManager = context.getPackageManager();
        packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI);
        return packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    private static boolean hasBluetooth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }

    private static String getImei(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    private static String getWifiMacAddress(Context context) {
        WifiManager wifiMan = (WifiManager) context.getSystemService(
                Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        return wifiInf.getMacAddress();
    }

    private static long getHeap() {
        Runtime rt = Runtime.getRuntime();
        return rt.maxMemory();
    }

    private static float getPixelDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static String getTotalRAM() {
        RandomAccessFile reader = null;
        String load = null;
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        double totRam = 0;
        String lastValue = "";
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();

            // Get the Number value from the string
            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(load);
            String value = "";
            while (m.find()) {
                value = m.group(1);
                // System.out.println("Ram : " + value);
            }
            reader.close();

            totRam = Double.parseDouble(value);
            // totRam = totRam / 1024;

            double mb = totRam / 1024.0;
            double gb = totRam / 1048576.0;
            double tb = totRam / 1073741824.0;

            if (tb > 1) {
                lastValue = twoDecimalForm.format(tb).concat(" TB");
            } else if (gb > 1) {
                lastValue = twoDecimalForm.format(gb).concat(" GB");
            } else if (mb > 1) {
                lastValue = twoDecimalForm.format(mb).concat(" MB");
            } else {
                lastValue = twoDecimalForm.format(totRam).concat(" KB");
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Streams.close(reader);
        }

        return lastValue;
    }

}
