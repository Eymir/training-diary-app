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

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getExtras().getInt("id");
    }

    public void SetAlarm(Context context, long alarmTime, int requestCode){
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimerAlarmBroadcastReceiver.class);
        intent.setAction(Integer.toString(requestCode));
        intent.putExtra("id", requestCode);
        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, alarmTime, pi);
        am.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, 86400000 , pi);
    }

    public void CancelAlarm(Context context, int requestCode){
        Intent intent = new Intent(context, TimerAlarmBroadcastReceiver.class);
        intent.setAction(Integer.toString(requestCode));
        intent.putExtra("id", requestCode);
        PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);

    }

}
