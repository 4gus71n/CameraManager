package com.astinx.cameramanager;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;



public class App extends GenericApp {
    private static final String TAG = App.class.toString();

    public static final String[] FORBIDDEN_CHARACTERS_ON_USERNAME = {"?", "!", "#", ","};

    private static final String FILE_PATH_FORMAT = "%s" + File.separator + "Minikast" + File.separator + "%s_%s";

    public static App getInstance() {
        if (singleton == null) {
            Log.e(TAG, "Instance null");
        }
        return (App) singleton;
    }

    public static final Long USER_GUEST = 1l;
    public static final Integer DEFAULT_TAB = 2;


    public String getAppVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            return "0";
        }
    }

}