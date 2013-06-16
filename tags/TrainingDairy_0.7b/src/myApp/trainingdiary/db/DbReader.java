package myApp.trainingdiary.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import myApp.trainingdiary.db.entity.Exercise;
import myApp.trainingdiary.db.entity.ExerciseType;
import myApp.trainingdiary.db.entity.Measure;
import myApp.trainingdiary.db.entity.MeasureType;
import myApp.trainingdiary.db.entity.TrainingStat;
import myApp.trainingdiary.utils.Consts;

/**
 * Created by bshestakov on 11.06.13.
 */
public class DbReader {
    private DBHelper dbHelper;

    public DbReader(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public Cursor getTrainingMainHistory() {
        String sqlQuery = "select * from TrainingStat " +
                "group by training_date " +
                "order by training_date asc ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db
                .rawQuery(sqlQuery, null);
        return c;
    }

    public Cursor getExercisesForHistory() {
        String sqlQuery = "select ex.name ex_name, ex.id ex_id, ex_type.icon_res icon, max(stat.date) tr_date " +
                "from Exercise ex, ExerciseType ex_type, TrainingStat stat " +
                "where stat.exercise_id = ex.id and ex.type_id = ex_type.id " +
                "group by ex.id " +
                "order by max(stat.date) desc ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, null);
        return c;
    }

    public Cursor getTrainingStatByTrainingDate(Date training_date) {
        String sqlQuery = "select ex.name, stat.value, stat.date,  type.icon_res icon " +
                "from TrainingStat stat, Exercise ex, ExerciseType type " +
                "where stat.training_date = ? AND ex.id = stat.exercise_id AND type.id = ex.type_id " +
                "order by stat.exercise_id, stat.date asc ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(training_date.getTime())});
        return c;
    }

    public Cursor getTrainingStatByExercise(long ex_id) {
        String sqlQuery = "select stat.value, stat.training_date " +
                "from TrainingStat stat " +
                "where stat.exercise_id = ? " +
                "order by training_date, date asc ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id)});
        return c;
    }

    public Exercise getExerciseById(long ex_id) {
        String sqlQuery = "select ex.id ex_id, ex.name ex_name, " +
                "ex_type.id type_id, ex_type.name type_name, ex_type.icon_res type_icon " +
                "from Exercise ex, ExerciseType ex_type " +
                "where ex.id = ? AND ex.type_id = ex_type.id ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id)});
        try {
            if (c.moveToFirst()) {
                Long type_id = c.getLong(c.getColumnIndex("type_id"));
                String ex_name = c.getString(c.getColumnIndex("ex_name"));
                String type_name = c.getString(c.getColumnIndex("type_name"));
                String type_icon = c.getString(c.getColumnIndex("type_icon"));
                return new Exercise(ex_id, new ExerciseType(type_id, type_icon, type_name), ex_name);
            } else {
                return null;
            }
        } catch (Throwable e) {
            Log.e(Consts.LOG_TAG, e.getMessage(), e);
            return null;
        } finally {
            c.close();
        }
    }

    public boolean isExerciseInDB(String name) {
        String sqlQuery = "select * " +
                "from Exercise " +
                "where name = ? ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(name)});
        int count = c.getCount();
        c.close();
        return count != 0;
    }

    public boolean isTrainingInDB(String name) {
        String sqlQuery = "select * " +
                "from Training " +
                "where name = ? ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(name)});
        int count = c.getCount();
        c.close();
        return count != 0;
    }

    public List<TrainingStat> getTrainingStatForLastPeriod(int period) {
        List<TrainingStat> stats = new ArrayList<TrainingStat>();
        long since = System.currentTimeMillis() - period;
        String sqlQuery = "select * from TrainingStat tr_stat " +
                "where tr_stat.date > ? " +
                "order by tr_stat.date desc ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(since)});
        try {
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long date = c.getLong(c.getColumnIndex("date"));
                Long trainingDate = c.getLong(c.getColumnIndex("training_date"));
                Long exerciseId = c.getLong(c.getColumnIndex("exercise_id"));
                Long trainingId = c.getLong(c.getColumnIndex("training_id"));
                String value = c.getString(c.getColumnIndex("value"));
                stats.add(new TrainingStat(id, new Date(date), new Date(trainingDate), exerciseId, trainingId, value));
            }
            return stats;
        } finally {
            c.close();
        }
    }

    public TrainingStat getLastTrainingStatByExerciseInTraining(long ex_id, long tr_id) {

        String sqlQuery = "select * from TrainingStat tr_stat " +
                "where tr_stat.id = (SELECT MAX(id) FROM TrainingStat WHERE training_id = ? AND exercise_id = ?)";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(tr_id), String.valueOf(ex_id)});
        try {
            if (c.moveToFirst()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long date = c.getLong(c.getColumnIndex("date"));
                Long trainingDate = c.getLong(c.getColumnIndex("training_date"));
                Long exerciseId = c.getLong(c.getColumnIndex("exercise_id"));
                Long trainingId = c.getLong(c.getColumnIndex("training_id"));
                String value = c.getString(c.getColumnIndex("value"));
                return new TrainingStat(id, new Date(date), new Date(trainingDate), exerciseId, trainingId, value);
            } else {
                return null;
            }
        } finally {
            c.close();
        }
    }

    public List<Measure> getMeasuresInExercise(long ex_id) {
        List<Measure> measures = new ArrayList<Measure>();

        String sqlQuery = "select m.* from Measure m, MeasureExType m_ex, Exercise ex " +
                "where ex.id = ? AND ex.type_id = m_ex.ex_type_id AND m.id = m_ex.measure_id " +
                "order by m_ex.position ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id)});
        while (c.moveToNext()) {
            Long id = c.getLong(c.getColumnIndex("id"));
            String name = c.getString(c.getColumnIndex("name"));
            Integer max = c.getInt(c.getColumnIndex("max"));
            Double step = c.getDouble(c.getColumnIndex("step"));
            Integer type = c.getInt(c.getColumnIndex("type"));
            measures.add(new Measure(id, name, max, step, MeasureType.valueOf(type)));
        }
        c.close();
        return measures;
    }

    public List<TrainingStat> getTrainingStatForLastPeriodByExercise(long ex_id, int period) {
        List<TrainingStat> stats = new ArrayList<TrainingStat>();
        long since = System.currentTimeMillis() - period;
        String sqlQuery = "select * from TrainingStat tr_stat " +
                "where tr_stat.exercise_id = ? AND tr_stat.date > ? " +
                "order by tr_stat.date desc ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id), String.valueOf(since)});
        try {
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long date = c.getLong(c.getColumnIndex("date"));
                Long trainingDate = c.getLong(c.getColumnIndex("training_date"));
                Long exerciseId = c.getLong(c.getColumnIndex("exercise_id"));
                Long trainingId = c.getLong(c.getColumnIndex("training_id"));
                String value = c.getString(c.getColumnIndex("value"));
                stats.add(new TrainingStat(id, new Date(date), new Date(trainingDate), exerciseId, trainingId, value));
            }
            return stats;
        } finally {
            c.close();
        }
    }

    public String getExerciseNameById(long ex_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String name = getExerciseNameById(db, ex_id);
        db.close();
        return name;
    }

    private String getExerciseNameById(SQLiteDatabase db, long ex_id) {
        String sqlQuery = "select ex.name from Exercise ex where ex.id = ?";
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id)});
        c.moveToFirst();
        String name = c.getString(c.getColumnIndex("name"));
        c.close();
        return name;
    }

    public Cursor getExercisesExceptExInTr(long tr_id) {
        Cursor c = getExercisesExceptExInTr(dbHelper.getReadableDatabase(), tr_id);
        return c;
    }

    public String getTrainingNameById(long tr_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String name = getTrainingNameById(db, tr_id);
        db.close();
        return name;
    }

    public Cursor getTrainings() {
        Cursor c = getTrainings(dbHelper.getReadableDatabase());
        return c;
    }

    public Cursor getExercisesInTraining(long tr_id) {
        Cursor c = getExercisesInTraining(dbHelper.getReadableDatabase(), tr_id);
        return c;
    }

    public Cursor getExerciseTypes() {
        Cursor c = getExerciseTypes(dbHelper.getReadableDatabase());
        return c;
    }

    public Cursor getExercisesExceptExInTr(SQLiteDatabase db, long tr_id) {
        String sqlQuery = "SELECT ex.id as _id, ex.name name, ex_type.icon_res icon_res "
                + "FROM Exercise as ex, "
                + "ExerciseType ex_type "
                + "WHERE ex.type_id = ex_type.id AND ex.id not in (select ex_tr.exercise_id FROM ExerciseInTraining ex_tr WHERE ex_tr.training_id == ?) ";
        Cursor c = db.rawQuery(sqlQuery, new String[]{String.valueOf(tr_id)});
        return c;
    }

    public Cursor getExerciseTypes(SQLiteDatabase db) {
        String sqlQuery = "select ex_type.id as _id, ex_type.name name, ex_type.icon_res icon_res "
                + "from ExerciseType ex_type";
        Cursor c = db.rawQuery(sqlQuery, null);
        return c;
    }

    /**
     * �������� ���������� � ������� ����������� position �� ������ �������
     * ���������� ������
     *
     * @return ������ � ���� �� ������������ ��� � � ����, ������ id
     *         ������������ ��� _id
     */
    public Cursor getTrainings(SQLiteDatabase db) {
        String sqlQuery = "select tr.id as _id, tr.name, tr.position from Training tr order by tr.position asc";
        Cursor c = db.rawQuery(sqlQuery, null);
        return c;
    }

    /**
     * �������� ���������� � ���������� � ������� position �� ������ �������
     * ���������� ������
     *
     * @return ������ � ���� �� ������������ ��� � � ����, ������ id
     *         ������������ ��� _id
     */
    public Cursor getExercisesInTraining(SQLiteDatabase db, long tr_id) {
        String sqlQuery = "select ex_tr.exercise_id as _id, ex.name name, ex_tr.position position, ex_type.icon_res icon_res "
                + "from ExerciseInTraining ex_tr, Exercise ex, ExerciseType ex_type "
                + "where ex_tr.training_id = ? and ex_tr.exercise_id = ex.id and ex.type_id = ex_type.id "
                + "order by ex_tr.position";
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(tr_id)});
        return c;
    }

    public int getTrainingsCount(SQLiteDatabase db) {
        String sqlQuery = "select count(tr.id) as _count from Training tr";
        Cursor c = db.rawQuery(sqlQuery, null);
        c.moveToFirst();
        int count = c.getInt(c.getColumnIndex("_count"));
        c.close();
        return count;
    }

    public int getExerciseCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = getExerciseCount(db);
        db.close();
        return count;
    }

    public int getExerciseCount(SQLiteDatabase db) {
        String sqlQuery = "select count(t.id) as _count from Exercise t";
        Cursor c = db.rawQuery(sqlQuery, null);
        c.moveToFirst();
        int count = c.getInt(c.getColumnIndex("_count"));
        c.close();
        return count;

    }

    public int getExerciseInTrainingCount(SQLiteDatabase db) {
        String sqlQuery = "select count(t.exercise_id) as _count from ExerciseInTraining t";
        Cursor c = db.rawQuery(sqlQuery, null);
        c.moveToFirst();
        int count = c.getInt(c.getColumnIndex("_count"));
        c.close();
        return count;
    }

    public String getTrainingNameById(SQLiteDatabase db, long tr_id) {
        String sqlQuery = "select tr.name from Training tr where tr.id = ?";
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(tr_id)});
        c.moveToFirst();
        String name = c.getString(c.getColumnIndex("name"));
        c.close();
        return name;
    }

    /**
     * ������� ������������ ���������� ������� �� �������� ��������
     */
    public String getStatistics(SQLiteDatabase db) {
        String result = "";
        result += "Training: " + getTrainingsCount(db) + "\n";
        result += "Exercise: " + getExerciseCount(db) + "\n";
        result += "ExerciseInTraining: " + getExerciseInTrainingCount(db)
                + "\n";
        return result;
    }

    public int getAllTrainingStats(SQLiteDatabase db) {
        String sqlQuery = "select count(stat.id) as _count from TrainingStat stat";
        Cursor c = db.rawQuery(sqlQuery, null);
        c.moveToFirst();
        int count = c.getInt(c.getColumnIndex("_count"));
        c.close();
        return count;
    }
}
