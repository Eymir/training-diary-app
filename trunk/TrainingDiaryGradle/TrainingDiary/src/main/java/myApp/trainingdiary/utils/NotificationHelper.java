package myApp.trainingdiary.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import myApp.trainingdiary.R;
import myApp.trainingdiary.SuperMainActivity;
import myApp.trainingdiary.result.ResultActivity;


/**
 * Created by malugin on 31.03.14.
 */
public class NotificationHelper{

    private Context context;
    private static NotificationHelper instance;
    private static final int NOTIFICATION_ID = 999;

    public NotificationHelper(Context c)  {
        context = c;

    }

    public static NotificationHelper getInstance(Context ctx){
        if (instance == null){
            instance = new NotificationHelper(ctx.getApplicationContext());
        }
        return instance;
    }

    public void showNotification(long ex_id, long tr_id){

        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_launch_barbell);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_barbell)
                        .setLargeIcon(bm)
                        .setContentTitle("GYM UP")
                        .setContentText("Running training")
                        .setTicker("GYM UP")
                        .setAutoCancel(true);
        Intent resultIntent = new Intent(context, ResultActivity.class);
        resultIntent.putExtra(Const.EXERCISE_ID, ex_id);
        resultIntent.putExtra(Const.TRAINING_ID, tr_id);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(SuperMainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void stopShowNotification(){
        NotificationManager nm =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
    }
}
