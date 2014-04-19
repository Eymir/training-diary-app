package ru.adhocapp.instaprint.util;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by Lenovo on 13.04.2014.
 */
public class ResourceAccess {
    private static ResourceAccess instance;
    private final Resources resources;

    public ResourceAccess(Context context) {
        resources = context.getResources();
    }

    public static ResourceAccess getInstance(Context context) {
        if (instance == null) {
            instance = new ResourceAccess(context);
        }
        return instance;
    }

    public static ResourceAccess getInstance() {
        return getInstance(null);
    }

    public Resources getResources() {
        return resources;
    }
}
