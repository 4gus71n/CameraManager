package com.astinx.cameramanager;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by RegFacu on 2014-09-05.
 */
public class HardwareTools {
    public static boolean hasCamera(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm != null && pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
}