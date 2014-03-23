package myApp.trainingdiary.billing;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by malugin on 23.03.14.
 */
public class BillingPreferencesHelper {

    private static final String TAG_DISABLED_ADS = "disabledADS";

    private static boolean disabledADS;

    public static boolean isAdsDisabled() {
        return disabledADS;
    }

    public static enum Purchase {
        DISABLE_ADS
    };

    public static void savePurchase(Context c, Purchase p, boolean v) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = settings.edit();
        switch (p) {
            case DISABLE_ADS:
                editor.putBoolean("TAG_DISABLED_ADS", v);
                disabledADS = v;
                break;
        }
        editor.commit();
    }

    public static void loadSettings(Context c) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(c);
        if (settings.getAll().size() == 0) {
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();

            disabledADS = false;
        } else
            disabledADS = settings.getBoolean(TAG_DISABLED_ADS, false);

    }

    public static void saveSettings(Context c) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(TAG_DISABLED_ADS, disabledADS);

        editor.commit();
    }


}
