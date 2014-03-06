package myApp.trainingdiary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by s_malugin on 24.01.14.
 */
public class Vibrator {

    private static Vibrator instance;
    Context context;
    android.os.Vibrator v;

    public Vibrator(Context c) {
        context = c;
        v = (android.os.Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static Vibrator getInstance(Context ctx){
        if (instance == null){
            instance = new Vibrator(ctx);
        }
        return instance;
    }

    public void startVibrator(){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean vibrate = sp.getBoolean("set_timer_vibro", false);

        if(vibrate){
            long[] pattern = {0, 2000, 1000, 2000, 1000};
            v.vibrate(pattern,-1);
        }
    }

    public void stopVibrator(){
        v.cancel();
    }

}
