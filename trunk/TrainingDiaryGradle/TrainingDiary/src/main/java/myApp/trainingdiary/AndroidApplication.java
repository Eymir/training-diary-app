package myApp.trainingdiary;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;

public class AndroidApplication extends Application {

    private static AndroidApplication sInstance;
    private Locale locale;
    private String lang;

    public static AndroidApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        lang = preferences.getString("lang", "default");
        if (lang.equals("default")) {
            lang = getResources().getConfiguration().locale.getCountry();
        }
        locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        Log.i("Lang change", "Locale=" + locale);
        getBaseContext().getResources().updateConfiguration(config, null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, null);
    }
}