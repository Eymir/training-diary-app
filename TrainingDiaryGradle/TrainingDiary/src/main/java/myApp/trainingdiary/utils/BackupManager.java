package myApp.trainingdiary.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import myApp.trainingdiary.R;
import myApp.trainingdiary.db.DBHelper;

/**
 * Created by Lenovo on 09.12.13.
 */
public class BackupManager {
    public final static String BACKUP_FOLDER = "TrainingDiaryBackup";

    public static void backupToSD(Context context) throws BackupException {
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String backupDBPath = DBHelper.DATABASE_NAME;
                File currentDB = context.getDatabasePath(DBHelper.DATABASE_NAME);
                File backupDB = new File(sd + "/" + BACKUP_FOLDER, backupDBPath);
                if (backupDB.getParentFile() != null) backupDB.getParentFile().mkdirs();
                Log.d(Consts.LOG_TAG, "currentDB: " + currentDB.getAbsolutePath() + " backupDB: " + backupDB.getAbsolutePath());
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            throw new BackupException(context.getString(R.string.backup_problem), e);
        }
    }

    public static void restoreFromSD(Context context) throws BackupException {
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String backupDBPath = DBHelper.DATABASE_NAME;
                File currentDB = context.getDatabasePath(DBHelper.DATABASE_NAME);
                File backupDB = new File(sd + "/" + BACKUP_FOLDER, backupDBPath);
                if (backupDB.getParentFile() != null) backupDB.getParentFile().mkdirs();
                Log.d(Consts.LOG_TAG, "currentDB: " + currentDB.getAbsolutePath() + " backupDB: " + backupDB.getAbsolutePath());
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(backupDB).getChannel();
                    FileChannel dst = new FileOutputStream(currentDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            throw new BackupException(context.getString(R.string.restore_problem), e);
        }
    }

}
