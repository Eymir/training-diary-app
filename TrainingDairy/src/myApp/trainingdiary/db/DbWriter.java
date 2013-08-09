package myApp.trainingdiary.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import myApp.trainingdiary.utils.Consts;

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
        Log.d(Consts.LOG_TAG, "insertExerciseInTrainingAtEnd.ContentValues: "
                + cv);
        long id = db.insert("ExerciseInTraining", null, cv);
        return id;
    }

    private long getExerciseCount(SQLiteDatabase db, long training_id) {
        String sqlQuery = "select count(ex_tr.exercise_id) as _count from ExerciseInTraining ex_tr where training_id = ?";
        Cursor c = db.rawQuery(sqlQuery,
                new String[]{String.valueOf(training_id)});
        c.moveToFirst();
        int count = c.getInt(c.getColumnIndex("_count"));
        c.close();
        return count;
    }

    public void changeTrainingPositions(List<Long> list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (int i = 0; i < list.size(); i++) {
            ContentValues cv = new ContentValues();
            cv.put("position", i);
            // Log.d(Consts.LOG_TAG, "new_pos: " + newNumEX +
            // " old_pos: "+old_pos+" ex_name: " + strNameEx
            // + " trainingname: " + strNameTr);
            db.update("Training", cv, "id = ? ",
                    new String[]{String.valueOf(list.get(i))});
        }
        db.close();
    }

    public int deleteLastTrainingStatInCurrentTraining(long ex_id, long tr_id, long tr_period) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long since = System.currentTimeMillis() - tr_period;
        int deleted = db.delete("TrainingStat", " id = (SELECT MAX(id) FROM TrainingStat " +
                "WHERE training_id = ? AND exercise_id = ? AND date > ? ) ",
                new String[]{String.valueOf(tr_id), String.valueOf(ex_id), String.valueOf(since)});
        db.close();
        return deleted;
    }

    public void deleteExerciseFromTraining(long tr_id, long ex_id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("ExerciseInTraining", "training_id = ? AND exercise_id = ?",
                new String[]{String.valueOf(tr_id), String.valueOf(ex_id)});
        db.close();
    }

    public void changeExercisePositions(long tr_id, List<Long> list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (int i = 0; i < list.size(); i++) {
            ContentValues cv = new ContentValues();
            cv.put("position", i);
            Log.d(Consts.LOG_TAG, "new_pos: " + i +
                    "  ex_id: " + list.get(i)
                    + " training_id: " + tr_id);
            db.update("ExerciseInTraining", cv, "training_id = ? AND exercise_id = ? ",
                    new String[]{String.valueOf(tr_id), String.valueOf(list.get(i))});
        }
        db.close();
    }

    public void renameExercise(long ex_id, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        renameExercise(db, ex_id, name);
        db.close();
    }

    private void renameExercise(SQLiteDatabase db, long ex_id, String name) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        db.update("Exercise", cv, "id = ? ",
                new String[]{String.valueOf(ex_id)});
    }

    public void deleteTraining(long tr_id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        deleteTraining(db, tr_id);
        db.close();
    }

    public void renameTraining(long tr_id, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        renameTraining(db, tr_id, name);
        db.close();
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
        long id = insertTrainingStat(db, exercise_id, training_id, date, trainingDate, value);
        db.close();
        return id;
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
}
