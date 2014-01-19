package myApp.trainingdiary.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Date;
import java.util.List;

import myApp.trainingdiary.db.entity.Measure;
import myApp.trainingdiary.db.entity.TrainingStampStatus;
import myApp.trainingdiary.utils.Const;

/**
 * Created by bshestakov on 11.06.13.
 */
public class DbWriter {
    private DBHelper dbHelper;

    public DbWriter(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insertTraining(SQLiteDatabase db, String name, int position) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("position", position);
        long id = db.insert("Training", null, cv);
        return id;
    }

    public long insertExercise(SQLiteDatabase db, String name, long type_id) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("type_id", type_id);
        long id = db.insert("Exercise", null, cv);
        return id;
    }

    public long insertMeasure(SQLiteDatabase db, String name, int max,
                              double step, int type) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("max", max);
        cv.put("step", step);
        cv.put("type", type);
        long id = db.insert("Measure", null, cv);
        return id;
    }

    public long insertExerciseType(SQLiteDatabase db, String name,
                                   String icon_res) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("icon_res", icon_res);
        long id = db.insert("ExerciseType", null, cv);
        return id;
    }

    public long insertMeasureExType(SQLiteDatabase db, long exType_id,
                                    long measure_id, int position) {
        ContentValues cv = new ContentValues();
        cv.put("ex_type_id", exType_id);
        cv.put("measure_id", measure_id);
        cv.put("position", position);
        long id = db.insert("MeasureExType", null, cv);
        return id;
    }

    public long insertExerciseInTraining(SQLiteDatabase db, long training_id,
                                         long exercise_id, int position) {
        ContentValues cv = new ContentValues();
        cv.put("training_id", training_id);
        cv.put("exercise_id", exercise_id);
        cv.put("position", position);
        long id = db.insert("ExerciseInTraining", null, cv);
        return id;
    }

    public long insertExerciseInTrainingAtEnd(SQLiteDatabase db,
                                              long training_id, long exercise_id) {
        long position = getExerciseCount(db, training_id);
        ContentValues cv = new ContentValues();
        cv.put("training_id", training_id);
        cv.put("exercise_id", exercise_id);
        cv.put("position", position);
        Log.d(Const.LOG_TAG, "insertExerciseInTrainingAtEnd.ContentValues: "
                + cv);
        long id = db.insert("ExerciseInTraining", null, cv);
        return id;
    }

    private long getExerciseCount(SQLiteDatabase db, long training_id) {
        String sqlQuery = "select count(ex_tr.exercise_id) as _count from ExerciseInTraining ex_tr where training_id = ?";
        Cursor c = db.rawQuery(sqlQuery,
                new String[]{String.valueOf(training_id)});
        try {
            c.moveToFirst();
            int count = c.getInt(c.getColumnIndex("_count"));
            return count;
        } finally {
            if (c != null) c.close();

        }
    }

    public void changeTrainingPositions(List<Long> list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            for (int i = 0; i < list.size(); i++) {
                ContentValues cv = new ContentValues();
                cv.put("position", i);
                // Log.d(Const.LOG_TAG, "new_pos: " + newNumEX +
                // " old_pos: "+old_pos+" ex_name: " + strNameEx
                // + " trainingname: " + strNameTr);
                db.update("Training", cv, "id = ? ",
                        new String[]{String.valueOf(list.get(i))});
            }
        } finally {
            if (db != null) db.close();
        }

    }

    public int deleteLastTrainingSetInCurrentTrainingStamp(long ex_id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            return deleteLastTrainingSetInCurrentTrainingStamp(db, ex_id);
        } finally {
            if (db != null) db.close();
        }
    }


    protected int deleteLastTrainingSetInCurrentTrainingStamp(SQLiteDatabase db, long ex_id) {

        Long tr_st_id = dbHelper.READ.getOpenTrainingStampId(db);
        if (tr_st_id != null) {
            List<Long> exerciseTrainingSets = dbHelper.READ.getExerciseTrainingSetIds(db, tr_st_id, ex_id);
            int count = exerciseTrainingSets.size();
            int deleted = 0;
            switch (count) {
                case 1:
                    deleted = db.delete("TrainingSet", " id = ? ",
                            new String[]{String.valueOf(exerciseTrainingSets.get(0))});
                    break;
                default:
                    deleted = db.delete("TrainingSet", " id = ? ",
                            new String[]{String.valueOf(exerciseTrainingSets.get(exerciseTrainingSets.size() - 1))});
                    break;
            }
            return deleted;
        } else {
            return 0;
        }
    }

    public void deleteExerciseFromTraining(long tr_id, long ex_id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.delete("ExerciseInTraining", "training_id = ? AND exercise_id = ?",
                    new String[]{String.valueOf(tr_id), String.valueOf(ex_id)});
        } finally {
            if (db != null) db.close();
        }
    }

    public void changeExercisePositions(long tr_id, List<Long> list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            for (int i = 0; i < list.size(); i++) {
                ContentValues cv = new ContentValues();
                cv.put("position", i);
                Log.d(Const.LOG_TAG, "new_pos: " + i +
                        "  ex_id: " + list.get(i)
                        + " training_id: " + tr_id);
                db.update("ExerciseInTraining", cv, "training_id = ? AND exercise_id = ? ",
                        new String[]{String.valueOf(tr_id), String.valueOf(list.get(i))});
            }
        } finally {
            if (db != null) db.close();
        }
    }

    public void renameExercise(long ex_id, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            renameExercise(db, ex_id, name);
        } finally {
            if (db != null) db.close();
        }

    }

    public void renameExercise(SQLiteDatabase db, long ex_id, String name) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        db.update("Exercise", cv, "id = ? ",
                new String[]{String.valueOf(ex_id)});
    }


    public void deleteTraining(long tr_id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            deleteTraining(db, tr_id);
        } finally {
            if (db != null) db.close();
        }
    }

    public void renameTraining(long tr_id, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            renameTraining(db, tr_id, name);
        } finally {
            if (db != null) db.close();
        }
    }

    public void renameTraining(SQLiteDatabase db, long cur_tr_id, String name) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        db.update("Training", cv, "id = ? ",
                new String[]{String.valueOf(cur_tr_id)});
    }

    public void deleteTraining(SQLiteDatabase db, long cur_tr_id) {
        db.delete("Training", "id = ? ",
                new String[]{String.valueOf(cur_tr_id)});
    }

    public Long createTraining(String name, int position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Long id = insertTraining(db, name, position);
        return id;
    }

    public long insertTrainingStat(long exercise_id, long training_id,
                                   long date, long trainingDate, String value) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            long id = insertTrainingStat(db, exercise_id, training_id, date, trainingDate, value);
            return id;
        } finally {
            if (db != null) db.close();
        }
    }

    public long insertTrainingStat(SQLiteDatabase db, long exercise_id, long training_id,
                                   long date, long trainingDate, String value) {
        ContentValues cv = new ContentValues();
        cv.put("date", date);
        cv.put("training_date", trainingDate);
        cv.put("value", value);
        cv.put("exercise_id", exercise_id);
        cv.put("training_id", training_id);
        long id = db.insert("TrainingStat", null, cv);
        return id;
    }

    public void renameExerciseType(SQLiteDatabase db, Long id, String name) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        db.update("ExerciseType", cv, "id = ? ",
                new String[]{String.valueOf(id)});
    }


    public boolean deleteExerciseWithStat(long ex_id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            int countExerciseInTraining = db.delete("ExerciseInTraining", "exercise_id=" + ex_id, null);
            Log.d(Const.LOG_TAG, "countExerciseInTraining: "
                    + countExerciseInTraining);
            int countTrainingStat = db.delete("TrainingStat", "exercise_id=" + ex_id, null);
            Log.d(Const.LOG_TAG, "countTrainingStat: "
                    + countTrainingStat);
            int countExercise = db.delete("Exercise", "id=" + ex_id, null);
            Log.d(Const.LOG_TAG, "countExercise: "
                    + countExercise);
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(Const.LOG_TAG, "deleteExerciseWithStat problem", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public void updateMeasure(SQLiteDatabase db, Measure m) {
        ContentValues cv = new ContentValues();
        cv.put("name", m.getName());
        cv.put("max", m.getMax());
        cv.put("step", m.getStep());
        cv.put("type", m.getType().code);
        Log.d(Const.LOG_TAG, "Measure: " + m);
        db.update("Measure", cv, "id = ? ",
                new String[]{String.valueOf(m.getId())});
    }

    public void changeExerciseTypeIconRes(SQLiteDatabase db, Long id, String icon_res) {
        ContentValues cv = new ContentValues();
        cv.put("icon_res", icon_res);
        db.update("ExerciseType", cv, "id = ? ",
                new String[]{String.valueOf(id)});
    }

    public void closeTrainingStamp(SQLiteDatabase db, Long tr_stamp_id, Date date) {
        ContentValues cv = new ContentValues();
        cv.put("end_date", date.getTime());
        cv.put("status", TrainingStampStatus.CLOSED.name());
        db.update("TrainingStamp", cv, "id = ? ",
                new String[]{String.valueOf(tr_stamp_id)});
    }

    public void closeTrainingStamp(Long tr_stamp_id, Date date) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            closeTrainingStamp(db, tr_stamp_id, date);
        } finally {
            if (db != null && db.isOpen())
                db.close();
        }
    }

    public Long insertTrainingStamp(Date startDate, Date endDate, String comment, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            return insertTrainingStamp(db, startDate, endDate, comment, status);
        } finally {
            if (db != null && db.isOpen())
                db.close();
        }
    }

    public Long insertTrainingStamp(SQLiteDatabase db, Date startDate, Date endDate, String comment, String status) {
        ContentValues cv = new ContentValues();
        if (startDate != null)
            cv.put("start_date", startDate.getTime());
        if (endDate != null)
            cv.put("end_date", endDate.getTime());
        if (comment != null)
            cv.put("comment", comment);
        if (status != null)
            cv.put("status", status);
        long id = db.insert("TrainingStamp", null, cv);
        return id;
    }

    public long insertTrainingSet(SQLiteDatabase db, Long training_stamp_Id, Long exercise_id, Long training_id, Date date) {
        ContentValues cv = new ContentValues();
        if (training_stamp_Id != null)
            cv.put("training_stamp_Id", training_stamp_Id);
        if (date != null)
            cv.put("date", date.getTime());
        if (exercise_id != null)
            cv.put("exercise_id ", exercise_id);
        if (training_id != null)
            cv.put("training_id ", training_id);
        long id = db.insert("TrainingSet", null, cv);
        return id;
    }

    public long insertTrainingSetValue(SQLiteDatabase db, long tr_set_id, int position, Double value) {
        ContentValues cv = new ContentValues();
        cv.put("training_set_id", tr_set_id);
        if (value != null)
            cv.put("value", value);
        cv.put("position ", position);
        long id = db.insert("TrainingSetValue", null, cv);
        return id;
    }

    public void deleteTrainingStamp(Long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            deleteTraininStamp(db, id);
        } finally {
            if (db != null) db.close();
        }
    }

    public void deleteTraininStamp(SQLiteDatabase db, Long id) {
        db.delete("TrainingStamp", "id = ? ",
                new String[]{String.valueOf(id)});
    }
}
