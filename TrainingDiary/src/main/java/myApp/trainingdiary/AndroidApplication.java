package myApp.trainingdiary;

import android.app.Application;

public class AndroidApplication extends Application {

    private static AndroidApplication sInstance;

    public static AndroidApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

}