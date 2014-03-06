package myApp.trainingdiary.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by s_malugin on 24.01.14.
 */
public class TimerAlarmBroadcastReceiver extends BroadcastReceiver {

    private static TimerAlarmBroadcastReceiver mInstance = null;
    private Context context;
    public static long TIME;
    public static boolean RUN;

    public static TimerAlarmBroadcastReceiver getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new TimerAlarmBroadcastReceiver(ctx.getApplicationContext());
        }
        return mInstance;
    }

    public TimerAlarmBroadcastReceiver() {

    }

    public TimerAlarmBroadcastReceiver(Context c) {
        context = c;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getExtras().getInt("id");
        RUN = false;
        TIME = 0L;
        Vibrator.getInstance(context).startVibrator();
        SoundPlayer.getInstance(context).playSound(Const.DEFAULT_SOUND_URI, false);    }

    public void SetAlarm(long alarmTime, int requestCode) {
        TIME = alarmTime;
        RUN = true;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimerAlarmBroadcastReceiver.class);
        intent.setAction(Integer.toString(requestCode));
        intent.putExtra("id", requestCode);
        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, alarmTime, pi);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, 86400000 , pi);
    }

    public void CancelAlarm(int requestCode) {
        RUN = false;
        TIME = 0L;
        Intent intent = new Intent(context, TimerAlarmBroadcastReceiver.class);
        intent.setAction(Integer.toString(requestCode));
        intent.putExtra("id", requestCode);
        PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);

    }

}
