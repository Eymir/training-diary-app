package myApp.trainingdiary.utils;

import android.content.Context;

import java.util.Date;
import java.util.List;

import myApp.trainingdiary.AndroidApplication;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.TrainingSet;
import myApp.trainingdiary.db.entity.TrainingStamp;
import myApp.trainingdiary.db.entity.TrainingStampStatus;

/**
 * Created by Lenovo on 04.01.14.
 */
public class TrainingDurationManger {
    private static final DBHelper dbHelper = DBHelper.getInstance(null);

    public static Long getTrainingStamp(int period) {
        closeExpiredTrainingStamps(period);
        TrainingStamp tr_stamp = dbHelper.READ.getOpenTrainingStamp();
        if (tr_stamp == null) {
            tr_stamp = dbHelper.EM.persist(new TrainingStamp(null, new Date(), null, null, TrainingStampStatus.OPEN));
        }
        return tr_stamp.getId();
    }

    public static void closeExpiredTrainingStamps(int period) {
        List<TrainingStamp> stamps = dbHelper.READ.getOpenTrainingStampList();
        for (TrainingStamp tr_stamp : stamps) {
            TrainingSet set = dbHelper.READ.getLastTrainingSetTrainingStamp(tr_stamp.getId());
            Long date = tr_stamp.getStartDate().getTime();
            if (set != null) date = set.getDate().getTime();
            if (System.currentTimeMillis() - date > period) {
                if (set == null) {
                    dbHelper.WRITE.deleteTrainingStamp(tr_stamp.getId());
                } else {
                    dbHelper.WRITE.closeTrainingStamp(tr_stamp.getId(), set.getDate());
                }
                NotificationHelper.getInstance(AndroidApplication.getAppContext()).stopShowNotification();
                NotificationBroadcastReceiver.getInstance(AndroidApplication.getAppContext()).stopNotificationReceiver();
            }
        }
    }

    public static void closeOpenTrainingStamps() {
        List<TrainingStamp> stamps = dbHelper.READ.getOpenTrainingStampList();
        for (TrainingStamp tr_stamp : stamps) {
            TrainingSet set = dbHelper.READ.getLastTrainingSetTrainingStamp(tr_stamp.getId());
            Long date = tr_stamp.getStartDate().getTime();
            if (set != null) date = set.getDate().getTime();
            if (set == null) {
                dbHelper.WRITE.deleteTrainingStamp(tr_stamp.getId());
            } else {
                dbHelper.WRITE.closeTrainingStamp(tr_stamp.getId(), set.getDate());
            }
        }
    }
}
