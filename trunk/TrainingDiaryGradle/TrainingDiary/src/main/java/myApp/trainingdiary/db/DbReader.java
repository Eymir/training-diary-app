package myApp.trainingdiary.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import myApp.trainingdiary.db.entity.Exercise;
import myApp.trainingdiary.db.entity.ExerciseType;
import myApp.trainingdiary.db.entity.ExerciseTypeIcon;
import myApp.trainingdiary.db.entity.Measure;
import myApp.trainingdiary.db.entity.MeasureType;
import myApp.trainingdiary.db.entity.TrainingSet;
import myApp.trainingdiary.db.entity.TrainingSetValue;
import myApp.trainingdiary.db.entity.TrainingStamp;
import myApp.trainingdiary.db.entity.TrainingStampStatus;
import myApp.trainingdiary.db.entity.TrainingStat;
import myApp.trainingdiary.utils.Const;

/**
 * Created by bshestakov on 11.06.13.
 */
public class DbReader {
    private DBHelper dbHelper;

    public DbReader(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public List<TrainingStamp> getTrainingMainHistory() {
        List<TrainingStamp> stamps = new ArrayList<TrainingStamp>();
        String sqlQuery = "select * from TrainingStamp " +
                "order by start_date desc ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, null);
        try {
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long start_date = c.getLong(c.getColumnIndex("start_date"));
                Long end_date = c.getLong(c.getColumnIndex("end_date"));
                String comment = c.getString(c.getColumnIndex("comment"));
                String status = c.getString(c.getColumnIndex("status"));
                stamps.add(new TrainingStamp(id, new Date(start_date), new Date(end_date), comment, TrainingStampStatus.valueOf(status)));
            }
        } finally {
            if (c != null && !c.isClosed())
                c.close();
            if (db != null && db.isOpen())
                db.close();
        }
        return stamps;

    }

    public List<Exercise> getExercisesForHistory() {
        List<Exercise> list = new ArrayList<Exercise>();
        String sqlQuery = "select ex.name ex_name, ex.id ex_id, ex_type.icon_res type_icon, ex_type.name type_name, ex_type.id type_id " +
                "from Exercise ex, ExerciseType ex_type, TrainingSet tr_set " +
                "where tr_set.exercise_id = ex.id and ex.type_id = ex_type.id " +
                "group by ex.id " +
                "order by max(tr_set.date) desc ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, null);
        try {
            while (c.moveToNext()) {
                Long type_id = c.getLong(c.getColumnIndex("type_id"));
                Long ex_id = c.getLong(c.getColumnIndex("ex_id"));
                String ex_name = c.getString(c.getColumnIndex("ex_name"));
                String type_name = c.getString(c.getColumnIndex("type_name"));
                String type_icon = c.getString(c.getColumnIndex("type_icon"));
                list.add(new Exercise(ex_id, new ExerciseType(type_id, ExerciseTypeIcon.getByIconResName(type_icon), type_name), ex_name));
            }
        } finally {
            if (c != null) c.close();
            if (db != null) db.close();
        }
        return list;
    }

    public Cursor getExercisesWithStat() {
        String sqlQuery = "select ex.name ex_name, ex.id _id, ex_type.icon_res icon, max(stat.date) tr_date " +
                "from Exercise ex, ExerciseType ex_type, TrainingSet stat " +
                "where stat.exercise_id = ex.id and ex.type_id = ex_type.id " +
                "group by ex.id " +
                "order by ex_name asc ";
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

    public List<TrainingStamp> getTrainingStampWithExactExerciseDesc(long ex_id) {
        List<TrainingStamp> stamps = new ArrayList<TrainingStamp>();
        String sqlQuery = "select DISTINCT tr_stamp.* " +
                "from TrainingStamp tr_stamp, TrainingSet tr_set " +
                "where tr_stamp.id = tr_set.training_stamp_id and tr_set.exercise_id = ? " +
                "order by tr_stamp.id desc, tr_set.date asc ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id)});
        try {
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long start_date = c.getLong(c.getColumnIndex("start_date"));
                Long end_date = c.getLong(c.getColumnIndex("end_date"));
                String comment = c.getString(c.getColumnIndex("comment"));
                String status = c.getString(c.getColumnIndex("status"));
                stamps.add(new TrainingStamp(id, new Date(start_date), new Date(end_date), comment, TrainingStampStatus.valueOf(status), getTrainingSetsInTrainingStampByExercise(db, ex_id, id)));
            }
        } finally {
            if (c != null && !c.isClosed())
                c.close();
            if (db != null && db.isOpen())
                db.close();
        }
        return stamps;
    }

    public List<TrainingStamp> getTrainingStampWithExactExerciseAsc(long ex_id) {
        List<TrainingStamp> stamps = new ArrayList<TrainingStamp>();
        String sqlQuery = "select DISTINCT tr_stamp.* " +
                "from TrainingStamp tr_stamp, TrainingSet tr_set " +
                "where tr_stamp.id = tr_set.training_stamp_id and tr_set.exercise_id = ? " +
                "order by tr_stamp.id asc, tr_set.date asc ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id)});
        try {
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long start_date = c.getLong(c.getColumnIndex("start_date"));
                Long end_date = c.getLong(c.getColumnIndex("end_date"));
                String comment = c.getString(c.getColumnIndex("comment"));
                String status = c.getString(c.getColumnIndex("status"));
                stamps.add(new TrainingStamp(id, new Date(start_date), new Date(end_date), comment, TrainingStampStatus.valueOf(status), getTrainingSetsInTrainingStampByExercise(db, ex_id, id)));
            }
        } finally {
            if (c != null && !c.isClosed())
                c.close();
            if (db != null && db.isOpen())
                db.close();
        }
        return stamps;
    }

    public List<TrainingStamp> getTrainingStampInInterval(long startDate, long endDate) {
        List<TrainingStamp> stamps = new ArrayList<TrainingStamp>();

//        String sqlQuery = "select tr_stamp.* " +
//                "from TrainingStamp tr_stamp " +
//                "where tr_stamp.start_date >= ? and tr_stamp.start_date <= ? " +
//                "order by tr_stamp.id asc, tr_set.date asc ";
//      SQLiteDatabase db = dbHelper.getReadableDatabase();
//      Cursor c = db
//                .rawQuery(sqlQuery, new String[]{String.valueOf(startDate), String.valueOf(endDate)});

        String table = "TrainingStamp";
        String selection = "start_date >= ? and start_date <= ? ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] selectionArgs = {String.valueOf(startDate), String.valueOf(endDate)};
        Cursor c = db.query(table, null, selection, selectionArgs, null, null, null);

        try {
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long start_date = c.getLong(c.getColumnIndex("start_date"));
                Long end_date = c.getLong(c.getColumnIndex("end_date"));
                String comment = c.getString(c.getColumnIndex("comment"));
                String status = c.getString(c.getColumnIndex("status"));
                stamps.add(new TrainingStamp(id, new Date(start_date), new Date(end_date), comment, TrainingStampStatus.valueOf(status)));
            }
        } finally {
            if (c != null && !c.isClosed())
                c.close();
            if (db != null && db.isOpen())
                db.close();
        }
        return stamps;
    }

    /**
     * Возвращает список фактов тренировок за определенный интервал (без подходов)
     *
     * @param startDate начало интервала (абсолютное время в миллисекундах)
     * @param endDate   конец интервала (абсолютное время в миллисекундах)
     * @return список фактов тренировок
     */
    public List<TrainingStamp> getTrainingStampInIntervalWithTrainingSet(long startDate, long endDate) {
        List<TrainingStamp> stamps = new ArrayList<TrainingStamp>();

//        String sqlQuery = "select tr_stamp.* " +
//                "from TrainingStamp tr_stamp " +
//                "where tr_stamp.start_date >= ? and tr_stamp.start_date <= ? " +
//                "order by tr_stamp.id asc, tr_set.date asc ";
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor c = db
//                .rawQuery(sqlQuery, new String[]{String.valueOf(startDate), String.valueOf(endDate)});

        String table = "TrainingStamp";
        String selection = "start_date >= ? and start_date <= ? ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] selectionArgs = {String.valueOf(startDate), String.valueOf(endDate)};
        Cursor c = db.query(table, null, selection, selectionArgs, null, null, null);

        try {
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long start_date = c.getLong(c.getColumnIndex("start_date"));
                Long end_date = c.getLong(c.getColumnIndex("end_date"));
                String comment = c.getString(c.getColumnIndex("comment"));
                String status = c.getString(c.getColumnIndex("status"));
                stamps.add(new TrainingStamp(id, new Date(start_date), new Date(end_date), comment, TrainingStampStatus.valueOf(status), getTrainingSetListInTrainingStamp(id)));
            }
        } finally {
            if (c != null && !c.isClosed())
                c.close();
            if (db != null && db.isOpen())
                db.close();
        }
        return stamps;
    }

    /**
     * Возвращает список фактов тренировок за определенный интервал с подходами, коллекция может быть довольно тяжелая
     * //* @param startDate начало интервала (абсолютное время в миллисекундах)
     * //* @param endDate конец интервала (абсолютное время в миллисекундах)
     *
     * @return список фактов тренировок с подходами
     */
    private List<TrainingSet> getTrainingSetsInTrainingStampByExercise(SQLiteDatabase db, long ex_id, Long tr_stamp_id) {
        List<TrainingSet> stats = new ArrayList<TrainingSet>();
        String sqlQuery = "select tr_set.* from TrainingSet tr_set " +
                "where tr_set.exercise_id = ? AND tr_set.training_stamp_id = ?" +
                "order by tr_set.date asc ";
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id), String.valueOf(tr_stamp_id)});
        try {
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long date = c.getLong(c.getColumnIndex("date"));
                Long training_stamp_id = c.getLong(c.getColumnIndex("training_stamp_id"));
                Long exerciseId = c.getLong(c.getColumnIndex("exercise_id"));
                Long trainingId = c.getLong(c.getColumnIndex("training_id"));
                stats.add(new TrainingSet(id, training_stamp_id, new Date(date), exerciseId, trainingId, getTrainingSetValuesWithMeasure(db, id)));
            }
            return stats;
        } finally {
            if (c != null) c.close();
        }

    }

    public ExerciseType getExerciseTypeByName(SQLiteDatabase db, String name) {
        String sqlQuery = "select " +
                "ex_type.id type_id, ex_type.name type_name, ex_type.icon_res type_icon " +
                "from ExerciseType ex_type " +
                "where ex_type.name = ? ";
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{name});
        try {
            if (c.moveToFirst()) {
                Long type_id = c.getLong(c.getColumnIndex("type_id"));
                String type_name = c.getString(c.getColumnIndex("type_name"));
                String type_icon = c.getString(c.getColumnIndex("type_icon"));
                return new ExerciseType(type_id, ExerciseTypeIcon.getByIconResName(type_icon), type_name);
            } else {
                return null;
            }
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, e.getMessage(), e);
            return null;
        }
    }

    public Exercise getExerciseById(long ex_id) {
        String sqlQuery = "select ex.id ex_id, ex.name ex_name, " +
                "ex_type.id type_id, ex_type.name type_name, ex_type.icon_res type_icon " +
                "from Exercise ex, ExerciseType ex_type " +
                "where ex.id = ? AND ex.type_id = ex_type.id ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = null;
        try {
            c = db
                    .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id)});
            if (c.moveToFirst()) {
                Long type_id = c.getLong(c.getColumnIndex("type_id"));
                String ex_name = c.getString(c.getColumnIndex("ex_name"));
                String type_name = c.getString(c.getColumnIndex("type_name"));
                String type_icon = c.getString(c.getColumnIndex("type_icon"));
                return new Exercise(ex_id, new ExerciseType(type_id, ExerciseTypeIcon.getByIconResName(type_icon), type_name), ex_name);
            } else {
                return null;
            }
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, e.getMessage(), e);
            return null;
        } finally {
            if (c != null) c.close();
            if (db != null) db.close();

        }

    }

    public boolean isExerciseInDB(String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            return isExerciseInDB(db, name);
        } finally {
            if (db != null)
                db.close();
        }
    }

    public boolean isExerciseInDB(SQLiteDatabase db, String name) {
        String sqlQuery = "select * " +
                "from Exercise " +
                "where name = ? ";
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(name)});
        int count = c.getCount();
        if (c != null) c.close();
        return count > 0;
    }

    public boolean isTrainingInDB(String name) {
        String sqlQuery = "select * " +
                "from Training " +
                "where name = ? ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(name)});
        int count = c.getCount();
        if (c != null) c.close();
        return count > 0;
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
            if (c != null) c.close();
        }
    }

    public TrainingSet getLastTrainingSetByExerciseInLastOpenTrainingStamp(long ex_id, long tr_id) {

        String sqlQuery = "select * from TrainingSet tr_set " +
                "where tr_set.id = (SELECT MAX(t_set.id) FROM TrainingSet t_set, TrainingStamp t_stamp " +
                "WHERE t_set.training_stamp_id = t_stamp.id AND t_set.exercise_id = ? AND t_set.training_id = ? AND t_stamp.status = 'OPEN')";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id), String.valueOf(tr_id)});
        try {
            if (c.moveToFirst()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long date = c.getLong(c.getColumnIndex("date"));
                Long training_stamp_id = c.getLong(c.getColumnIndex("training_stamp_id"));
                Long exerciseId = c.getLong(c.getColumnIndex("exercise_id"));
                Long trainingId = c.getLong(c.getColumnIndex("training_id"));
                return new TrainingSet(id, training_stamp_id, new Date(date), exerciseId, trainingId, getTrainingSetValuesWithMeasure(db, id));
            } else {
                return null;
            }
        } finally {
            if (c != null) c.close();
            if (db != null) db.close();
        }
    }

    public TrainingSet getLastTrainingSetTrainingStamp(long tr_stamp_id) {

        String sqlQuery = "select * from TrainingSet tr_set " +
                "where tr_set.id = (SELECT MAX(t_set.id) FROM TrainingSet t_set, TrainingStamp t_stamp " +
                "WHERE t_set.training_stamp_id = t_stamp.id AND t_stamp.id = ?)";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(tr_stamp_id)});
        try {
            if (c.moveToFirst()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long date = c.getLong(c.getColumnIndex("date"));
                Long training_stamp_id = c.getLong(c.getColumnIndex("training_stamp_id"));
                Long exerciseId = c.getLong(c.getColumnIndex("exercise_id"));
                Long trainingId = c.getLong(c.getColumnIndex("training_id"));
                return new TrainingSet(id, training_stamp_id, new Date(date), exerciseId, trainingId);
            } else {
                return null;
            }
        } finally {
            if (c != null) c.close();
        }
    }

    public TrainingSet getLastTrainingSetByExerciseInLastTrainingStamp(long ex_id, long tr_id) {

        String sqlQuery = "select * from TrainingSet tr_set " +
                "WHERE tr_set.id = (SELECT MAX(t_set.id) FROM TrainingSet t_set " +
                "WHERE t_set.exercise_id = ? AND t_set.training_id = ?)";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id), String.valueOf(tr_id)});
        try {
            if (c.moveToFirst()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long date = c.getLong(c.getColumnIndex("date"));
                Long training_stamp_id = c.getLong(c.getColumnIndex("training_stamp_id"));
                Long exerciseId = c.getLong(c.getColumnIndex("exercise_id"));
                Long trainingId = c.getLong(c.getColumnIndex("training_id"));
                return new TrainingSet(id, training_stamp_id, new Date(date), exerciseId, trainingId, getTrainingSetValuesWithMeasure(db, id));
            } else {
                return null;
            }
        } finally {
            if (c != null && !c.isClosed()) c.close();
            if (db != null && db.isOpen()) c.close();
        }
    }

    public List<Measure> getMeasuresInExercise(Long ex_id) {
        List<Measure> measures = new ArrayList<Measure>();
        if (ex_id != null) {
            String sqlQuery = "select m.* from Measure m, MeasureExType m_ex, Exercise ex " +
                    "where ex.id = ? AND ex.type_id = m_ex.ex_type_id AND m.id = m_ex.measure_id " +
                    "order by m_ex.position ";
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor c = db
                    .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id)});

            try {
                while (c.moveToNext()) {
                    Long id = c.getLong(c.getColumnIndex("id"));
                    String name = c.getString(c.getColumnIndex("name"));
                    Integer max = c.getInt(c.getColumnIndex("max"));
                    Double step = c.getDouble(c.getColumnIndex("step"));
                    Integer type = c.getInt(c.getColumnIndex("type"));
                    measures.add(new Measure(id, name, max, step, MeasureType.valueOf(type)));
                }
            } finally {
                if (c != null && !c.isClosed()) c.close();
                if (db != null && db.isOpen()) db.close();

            }
        }
        return measures;
    }

    public List<Measure> getMeasuresInExerciseType(SQLiteDatabase db, Long ex_type_id) {
        List<Measure> measures = new ArrayList<Measure>();
        if (ex_type_id != null) {
            String sqlQuery = "select m.* from Measure m, MeasureExType m_ex " +
                    "where  m_ex.ex_type_id = ? AND m.id = m_ex.measure_id " +
                    "order by m_ex.position ";
            Cursor c = db
                    .rawQuery(sqlQuery, new String[]{String.valueOf(ex_type_id)});
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                String name = c.getString(c.getColumnIndex("name"));
                Integer max = c.getInt(c.getColumnIndex("max"));
                Double step = c.getDouble(c.getColumnIndex("step"));
                Integer type = c.getInt(c.getColumnIndex("type"));
                measures.add(new Measure(id, name, max, step, MeasureType.valueOf(type)));
            }
            if (c != null) c.close();
        }
        return measures;
    }

    public List<Measure> getMeasuresInExerciseExceptParticularMeasure(Long ex_id, Long m_id) {
        List<Measure> measures = new ArrayList<Measure>();
        if (ex_id != null) {
            String sqlQuery = "select m.* from Measure m, MeasureExType m_ex, Exercise ex " +
                    "where ex.id = ? AND ex.type_id = m_ex.ex_type_id AND m.id = m_ex.measure_id AND m.id <> ? " +
                    "order by m_ex.position ";
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor c = db
                    .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id), String.valueOf(m_id)});
            try {
                while (c.moveToNext()) {
                    Long id = c.getLong(c.getColumnIndex("id"));
                    String name = c.getString(c.getColumnIndex("name"));
                    Integer max = c.getInt(c.getColumnIndex("max"));
                    Double step = c.getDouble(c.getColumnIndex("step"));
                    Integer type = c.getInt(c.getColumnIndex("type"));
                    measures.add(new Measure(id, name, max, step, MeasureType.valueOf(type)));
                }
            } finally {
                if (c != null) c.close();
                if (db != null) db.close();
            }
        }
        return measures;
    }

    public List<TrainingSet> getTrainingSetListInTrainingStampByExercise(long ex_id, Long tr_stamp_id) {
        List<TrainingSet> stats = new ArrayList<TrainingSet>();
        String sqlQuery = "select tr_set.* from TrainingSet tr_set " +
                "where tr_set.exercise_id = ? AND tr_set.training_stamp_id = ? " +
                "order by tr_set.date asc ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id), String.valueOf(tr_stamp_id)});
        try {
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long date = c.getLong(c.getColumnIndex("date"));
                Long training_stamp_id = c.getLong(c.getColumnIndex("training_stamp_id"));
                Long exerciseId = c.getLong(c.getColumnIndex("exercise_id"));
                Long trainingId = c.getLong(c.getColumnIndex("training_id"));
                stats.add(new TrainingSet(id, training_stamp_id, new Date(date), exerciseId, trainingId, getTrainingSetValuesWithMeasure(db, id)));
            }
            return stats;
        } finally {
            if (c != null) c.close();
        }
    }

    public List<TrainingSet> getTrainingSetListInTrainingStamp(Long tr_stamp_id) {
        List<TrainingSet> stats = new ArrayList<TrainingSet>();
        String sqlQuery = "select tr_set.* from TrainingSet tr_set " +
                "where tr_set.training_stamp_id = ? " +
                "order by tr_set.exercise_id asc, tr_set.date desc ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(tr_stamp_id)});
        try {
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long date = c.getLong(c.getColumnIndex("date"));
                Long training_stamp_id = c.getLong(c.getColumnIndex("training_stamp_id"));
                Long exerciseId = c.getLong(c.getColumnIndex("exercise_id"));
                Long trainingId = c.getLong(c.getColumnIndex("training_id"));
                stats.add(new TrainingSet(id, training_stamp_id, new Date(date), exerciseId, trainingId, getTrainingSetValuesWithMeasure(db, id)));
            }
            return stats;
        } finally {
            if (c != null) c.close();
        }
    }

    public String getExerciseNameById(long ex_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            String name = getExerciseNameById(db, ex_id);
            return name;
        } finally {
            if (db != null)
                db.close();
        }
    }

    private String getExerciseNameById(SQLiteDatabase db, long ex_id) {
        String sqlQuery = "select ex.name from Exercise ex where ex.id = ?";
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id)});
        try {
            c.moveToFirst();
            String name = c.getString(c.getColumnIndex("name"));
            return name;
        } finally {
            if (c != null) c.close();
        }
    }

    public Cursor getExercisesExceptExInTr(long tr_id) {
        Cursor c = getExercisesExceptExInTr(dbHelper.getReadableDatabase(), tr_id);
        return c;
    }

    public String getTrainingNameById(long tr_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            String name = getTrainingNameById(db, tr_id);
            return name;
        } finally {
            if (db != null)
                db.close();
        }
    }

    public Cursor getTrainings() {
        Cursor c = getTrainings(dbHelper.getReadableDatabase());
        return c;
    }

    public Cursor getExercisesInTraining(long tr_id) {
        Cursor c = getExercisesInTraining(dbHelper.getReadableDatabase(), tr_id);
        return c;
    }

    public List<Exercise> getExerciseListInTraining(long tr_id) {
        List<Exercise> list = new ArrayList<Exercise>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = getExercisesInTraining(db, tr_id);
        try {
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("_id"));
                Long type_id = c.getLong(c.getColumnIndex("type_id"));
                String name = c.getString(c.getColumnIndex("name"));
                String icon_res = c.getString(c.getColumnIndex("icon_res"));
                String type_name = c.getString(c.getColumnIndex("type_name"));
                list.add(new Exercise(id, new ExerciseType(type_id, ExerciseTypeIcon.getByIconResName(icon_res), type_name), name));
            }
        } finally {
            if (c != null) c.close();
            if (db != null) db.close();
        }
        return list;
    }

    public Cursor getExerciseTypes() {
        Cursor c = getExerciseTypes(dbHelper.getReadableDatabase());
        return c;
    }

    public Cursor getExercisesExceptExInTr(SQLiteDatabase db, long tr_id) {
        String sqlQuery = "SELECT ex.id as _id, ex.name name, ex_type.icon_res icon_res "
                + "FROM Exercise as ex, "
                + "ExerciseType ex_type "
                + "WHERE ex.type_id = ex_type.id AND ex.id not in (select ex_tr.exercise_id FROM ExerciseInTraining ex_tr WHERE ex_tr.training_id = ?) " +
                " ORDER BY ex.name ASC";
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
     * ������������ ��� _id
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
     * ������������ ��� _id
     */
    public Cursor getExercisesInTraining(SQLiteDatabase db, long tr_id) {
        String sqlQuery = "select ex_tr.exercise_id as _id, ex.name name, ex_tr.position position, ex_type.icon_res icon_res, ex_type.name type_name, ex_type.id type_id "
                + "from ExerciseInTraining ex_tr, Exercise ex, ExerciseType ex_type "
                + "where ex_tr.training_id = ? and ex_tr.exercise_id = ex.id and ex.type_id = ex_type.id "
                + "order by ex_tr.position";
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(tr_id)});
        return c;
    }

    public int getTrainingDayCount(SQLiteDatabase db) {
        String sqlQuery = "select count(tr.id) as _count from Training tr";
        Cursor c = db.rawQuery(sqlQuery, null);
        c.moveToFirst();
        int count = c.getInt(c.getColumnIndex("_count"));
        if (c != null) c.close();
        return count;
    }

    public int getTrainingDayCount() {
        String sqlQuery = "select count(tr.id) as _count from Training tr";
        Cursor c = dbHelper.getReadableDatabase().rawQuery(sqlQuery, null);
        c.moveToFirst();
        int count = c.getInt(c.getColumnIndex("_count"));
        if (c != null) c.close();
        return count;
    }

    public int getExerciseCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            int count = getExerciseCount(db);
            return count;
        } finally {
            if (db != null)
                db.close();
        }
    }

    public int getExerciseCount(SQLiteDatabase db) {
        String sqlQuery = "select count(t.id) as _count from Exercise t";
        Cursor c = db.rawQuery(sqlQuery, null);
        c.moveToFirst();
        int count = c.getInt(c.getColumnIndex("_count"));
        c.close();
        return count;
    }

    public int getExerciseTypeCount(SQLiteDatabase db) {
        String sqlQuery = "select count(t.id) as _count from ExerciseType t";
        Cursor c = db.rawQuery(sqlQuery, null);
        c.moveToFirst();
        int count = c.getInt(c.getColumnIndex("_count"));
        if (c != null) c.close();
        return count;
    }

    public int getExerciseInTrainingCount(SQLiteDatabase db) {
        String sqlQuery = "select count(t.exercise_id) as _count from ExerciseInTraining t";
        Cursor c = db.rawQuery(sqlQuery, null);
        c.moveToFirst();
        int count = c.getInt(c.getColumnIndex("_count"));
        if (c != null) c.close();
        return count;
    }

    public String getTrainingNameById(SQLiteDatabase db, long tr_id) {
        String sqlQuery = "select tr.name from Training tr where tr.id = ?";
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(tr_id)});
        c.moveToFirst();
        String name = c.getString(c.getColumnIndex("name"));
        if (c != null) c.close();
        return name;
    }

    /**
     * ������� ������������ ���������� ������� �� �������� ��������
     */
    public String getStatistics(SQLiteDatabase db) {
        String result = "";
        result += "Training: " + getTrainingDayCount(db) + "\n";
        result += "Exercise: " + getExerciseCount(db) + "\n";
        result += "ExerciseInTraining: " + getExerciseInTrainingCount(db)
                + "\n";
        return result;
    }

    public int getTrainingStatCount(SQLiteDatabase db) {
        String sqlQuery = "select count(stat.id) as _count from TrainingStat stat";
        Cursor c = db.rawQuery(sqlQuery, null);
        c.moveToFirst();
        int count = c.getInt(c.getColumnIndex("_count"));
        if (c != null) c.close();
        return count;
    }


    public List<TrainingStat> getExerciseProgress(Long ex_id) {
        List<TrainingStat> stats = new ArrayList<TrainingStat>();
        String sqlQuery = "select * from TrainingStat tr_stat " +
                "where tr_stat.exercise_id = ? " +
                "order by tr_stat.date asc ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id)});
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
            if (c != null) c.close();
            if (db != null)
                db.close();
        }
    }

    public Long getMeasurePosInExercise(Long ex_id, Long m_id) {
        if (ex_id != null) {
            String sqlQuery = "select m_ex.position pos from MeasureExType m_ex, Exercise ex " +
                    "where ex.id = ? AND ex.type_id = m_ex.ex_type_id AND m_ex.measure_id = ? ";
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor c = db
                    .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id), String.valueOf(m_id)});
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("pos"));
                return id;
            }
            if (c != null) c.close();
        }
        return null;
    }

    public Measure getMeasureById(Long m_id) {
        if (m_id != null) {
            String sqlQuery = "select m.* from Measure m " +
                    "where m.id = ? ";
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor c = db
                    .rawQuery(sqlQuery, new String[]{String.valueOf(m_id)});
            if (c.moveToFirst()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                String name = c.getString(c.getColumnIndex("name"));
                Integer max = c.getInt(c.getColumnIndex("max"));
                Double step = c.getDouble(c.getColumnIndex("step"));
                Integer type = c.getInt(c.getColumnIndex("type"));
                return new Measure(id, name, max, step, MeasureType.valueOf(type));
            }
            if (c != null) c.close();
        }
        throw new RuntimeException("Measure with id: " + m_id + " not found in DB.");
    }

    public List<TrainingSetValue> getTrainingSetValuesWithMeasure(SQLiteDatabase db, Long tr_set_id) {
        if (tr_set_id != null) {
            List<TrainingSetValue> trainingSetValues = new ArrayList<TrainingSetValue>();
            String sqlQuery = "select tr_set_v.*, tr_set.exercise_id from TrainingSetValue tr_set_v, TrainingSet tr_set " +
                    "where tr_set_v.training_set_id = tr_set.id and tr_set.id = ? order by tr_set_v.position asc ";
            Cursor c = db
                    .rawQuery(sqlQuery, new String[]{String.valueOf(tr_set_id)});
            try {
                while (c.moveToNext()) {
                    Long id = c.getLong(c.getColumnIndex("id"));
                    Long training_set_id = c.getLong(c.getColumnIndex("training_set_id"));
                    Long position = c.getLong(c.getColumnIndex("position"));
                    Double value = c.getDouble(c.getColumnIndex("value"));
                    Integer exercise_id = c.getInt(c.getColumnIndex("exercise_id"));
                    trainingSetValues.add(new TrainingSetValue(id, training_set_id, value, position, getMeasureByExerciseAndPosition(db, exercise_id, position)));
                }
            } finally {
                if (c != null && !c.isClosed())
                    c.close();
            }
            return trainingSetValues;
        }
        throw new RuntimeException("Measure with id: " + tr_set_id + " not found in DB.");
    }

    private Measure getMeasureByExerciseAndPosition(SQLiteDatabase db, Integer exercise_id, Long position) {
        if (exercise_id != null) {
            String sqlQuery = "select m.* from Measure m, MeasureExType m_ex, Exercise ex " +
                    "where ex.id = ? AND ex.type_id = m_ex.ex_type_id AND m.id = m_ex.measure_id and m_ex.position = ? " +
                    "order by m_ex.position ";
            Cursor c = db
                    .rawQuery(sqlQuery, new String[]{String.valueOf(exercise_id), String.valueOf(position)});
            try {
                if (c.moveToFirst()) {
                    Long id = c.getLong(c.getColumnIndex("id"));
                    String name = c.getString(c.getColumnIndex("name"));
                    Integer max = c.getInt(c.getColumnIndex("max"));
                    Double step = c.getDouble(c.getColumnIndex("step"));
                    Integer type = c.getInt(c.getColumnIndex("type"));
                    return new Measure(id, name, max, step, MeasureType.valueOf(type));
                }
            } finally {
                if (c != null && !c.isClosed()) c.close();

            }
        }
        return null;
    }

    public List<Measure> getAllMeasures() {
        List<Measure> measures = new ArrayList<Measure>();
        String sqlQuery = "select m.* from Measure m " +
                "order by m.id ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, null);
        while (c.moveToNext()) {
            Long id = c.getLong(c.getColumnIndex("id"));
            String name = c.getString(c.getColumnIndex("name"));
            Integer max = c.getInt(c.getColumnIndex("max"));
            Double step = c.getDouble(c.getColumnIndex("step"));
            Integer type = c.getInt(c.getColumnIndex("type"));
            measures.add(new Measure(id, name, max, step, MeasureType.valueOf(type)));
        }
        if (c != null) c.close();
        return measures;
    }

    public TrainingStat getLastTrainingStat() {
        String sqlQuery = "select * from TrainingStamp tr_stat " +
                "where tr_stat.id = (SELECT MAX(id) FROM TrainingStat)";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, null);
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
            if (c != null) c.close();
        }

    }

    public int getTrainingStampCount() {
        String sqlQuery = "select count(*) as _count from TrainingStamp ";
        Cursor c = dbHelper.getReadableDatabase().rawQuery(sqlQuery, null);
        c.moveToFirst();
        int count = c.getInt(c.getColumnIndex("_count"));
        if (c != null) c.close();
        return count;
    }


    public long getTrainingDurationSumm() {
        String sqlQuery = "select sum(dur_sum1) as dur_sum from (select (stat.end_date - stat.start_date) as dur_sum1 " +
                "from TrainingStamp stat where stat.end_date not null and stat.end_date <> 0 and stat.status = 'CLOSED' and stat.end_date <> stat.start_date);";
        Cursor c = dbHelper.getReadableDatabase().rawQuery(sqlQuery, null);
        c.moveToFirst();
        long dur_sum = c.getInt(c.getColumnIndex("dur_sum"));
        if (c != null) c.close();
        return dur_sum;
    }

    public Exercise getFavoriteExercise() {
        String sqlQuery = "select ex.id ex_id, ex.name ex_name,  " +
                "ex_type.id type_id, ex_type.name type_name, ex_type.icon_res type_icon  " +
                "from Exercise ex, ExerciseType ex_type, ( " +
                "select exercise_id, count(id) ex_count from TrainingSet group by exercise_id order by count(id) desc limit 1) sel  " +
                "where ex.type_id = ex_type.id AND ex.id = sel.exercise_id";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, null);
        try {
            if (c.moveToFirst()) {
                Long ex_id = c.getLong(c.getColumnIndex("ex_id"));
                Long type_id = c.getLong(c.getColumnIndex("type_id"));
                String ex_name = c.getString(c.getColumnIndex("ex_name"));
                String type_name = c.getString(c.getColumnIndex("type_name"));
                String type_icon = c.getString(c.getColumnIndex("type_icon"));
                return new Exercise(ex_id, new ExerciseType(type_id, ExerciseTypeIcon.getByIconResName(type_icon), type_name), ex_name);
            } else {
                return null;
            }
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, e.getMessage(), e);
            return null;
        } finally {
            if (c != null) c.close();
        }

    }

    public Exercise getExerciseByName(SQLiteDatabase db, String name) {
        String sqlQuery = "select ex.id ex_id, ex.name ex_name, " +
                "ex_type.id type_id, ex_type.name type_name, ex_type.icon_res type_icon " +
                "from Exercise ex, ExerciseType ex_type " +
                "where ex.name = ? AND ex.type_id = ex_type.id ";
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(name)});
        try {
            if (c.moveToFirst()) {
                Long ex_id = c.getLong(c.getColumnIndex("ex_id"));
                Long type_id = c.getLong(c.getColumnIndex("type_id"));
                String ex_name = c.getString(c.getColumnIndex("ex_name"));
                String type_name = c.getString(c.getColumnIndex("type_name"));
                String type_icon = c.getString(c.getColumnIndex("type_icon"));
                return new Exercise(ex_id, new ExerciseType(type_id, ExerciseTypeIcon.getByIconResName(type_icon), type_name), ex_name);
            } else {
                return null;
            }
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, e.getMessage(), e);
            return null;
        } finally {
            if (c != null) c.close();
        }

    }

    public Long getExerciseTypeIdByIconRes(SQLiteDatabase db, String key) {
        String sqlQuery = "select ex_type.id type_id " +
                "from ExerciseType ex_type " +
                "where ex_type.icon_res = ? ";
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(key)});
        try {
            if (c.moveToFirst()) {
                Long type_id = c.getLong(c.getColumnIndex("type_id"));
                return type_id;
            } else {
                return null;
            }
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, e.getMessage(), e);
            return null;
        } finally {
            if (c != null) c.close();
        }
    }

    public Long getOpenTrainingStampId() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            return getOpenTrainingStampId(db);
        } finally {
            if (db != null && db.isOpen())
                db.close();
        }

    }

    public Long getOpenTrainingStampId(SQLiteDatabase db) {
        String sqlQuery = "select max(tr_st.id) tr_st_id " +
                "from TrainingStamp tr_st " +
                "where tr_st.end_date is null ";
        Cursor c = db
                .rawQuery(sqlQuery, null);
        try {
            if (c.moveToFirst()) {
                Long id = c.getLong(c.getColumnIndex("tr_st_id"));
                return id;
            } else {
                return null;
            }
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, e.getMessage(), e);
            return null;
        } finally {
            if (c != null) c.close();
        }
    }

    public List<Long> getExerciseTrainingSetIds(SQLiteDatabase db, Long tr_st_id, long ex_id) {
        List<Long> list = new ArrayList<Long>();
        String sqlQuery = "select tr_set.id tr_set_id " +
                "from TrainingSet tr_set " +
                "where tr_set.training_stamp_id = ? and tr_set.exercise_id = ? order by tr_set.id asc ";
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(tr_st_id), String.valueOf(ex_id)});
        try {
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("tr_set_id"));
                list.add(id);
            }
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, e.getMessage(), e);
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    public TrainingSet getTrainingSetWithMaxIdFromTrainingStamp(SQLiteDatabase db, Long tr_st_id) {
        String sqlQuery = " select tra_set.id tr_set_id, tra_set.training_stamp_id tr_set_stamp_id, " +
                "tra_set.date tr_set_date, tra_set.exercise_id tr_set_exercise_id, tra_set.training_id tr_set_training_id  " +
                "from TrainingSet tra_set where id = (select max(tr_set.id) " +
                "from TrainingSet tr_set " +
                "where tr_set.training_stamp_id = ?) ";
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(tr_st_id)});
        try {
            if (c.moveToFirst()) {
                Long id = c.getLong(c.getColumnIndex("tr_set_id"));
                Long training_stamp_id = c.getLong(c.getColumnIndex("tr_set_stamp_id"));
                Long date = c.getLong(c.getColumnIndex("tr_set_date"));
                Long exercise_id = c.getLong(c.getColumnIndex("tr_set_exercise_id"));
                Long training_id = c.getLong(c.getColumnIndex("tr_set_training_id"));
                return new TrainingSet(id, training_stamp_id, new Date(date), exercise_id, training_id);
            }
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, e.getMessage(), e);
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    public List<TrainingStat> getAllTrainingStat(SQLiteDatabase db) {
        List<TrainingStat> trainingStats = new ArrayList<TrainingStat>();
        String sqlQuery = "select * from TrainingStat tr_stat " +
                "order by tr_stat.id asc";
        Cursor c = db
                .rawQuery(sqlQuery, null);
        try {
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long date = c.getLong(c.getColumnIndex("date"));
                Long trainingDate = c.getLong(c.getColumnIndex("training_date"));
                Long exerciseId = c.getLong(c.getColumnIndex("exercise_id"));
                Long trainingId = c.getLong(c.getColumnIndex("training_id"));
                String value = c.getString(c.getColumnIndex("value"));
                trainingStats.add(new TrainingStat(id, new Date(date), new Date(trainingDate), exerciseId, trainingId, value));
            }
        } finally {
            if (c != null) c.close();
        }
        return trainingStats;
    }

    public TrainingStamp getLastTrainingStamp() {
        String sqlQuery = "select * from TrainingStamp tr_stat " +
                "where tr_stat.id = (SELECT MAX(id) FROM TrainingStamp)";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, null);
        try {
            if (c.moveToFirst()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long start_date = c.getLong(c.getColumnIndex("start_date"));
                Long end_date = c.getLong(c.getColumnIndex("end_date"));
                String comment = c.getString(c.getColumnIndex("comment"));
                String status = c.getString(c.getColumnIndex("status"));
                return new TrainingStamp(id, new Date(start_date), new Date(end_date), comment, TrainingStampStatus.valueOf(status), getTrainingSetListInTrainingStamp(id));
            } else {
                return null;
            }
        } finally {
            if (c != null) c.close();
            if (db != null && db.isOpen()) db.close();
        }
    }

    public TrainingStamp getLastClosedTrainingStamp() {
        String sqlQuery = "select * from TrainingStamp tr_stat " +
                "where tr_stat.id = (SELECT MAX(id) FROM TrainingStamp where status = 'CLOSED')";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, null);
        try {
            if (c.moveToFirst()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long start_date = c.getLong(c.getColumnIndex("start_date"));
                Long end_date = c.getLong(c.getColumnIndex("end_date"));
                String comment = c.getString(c.getColumnIndex("comment"));
                String status = c.getString(c.getColumnIndex("status"));
                return new TrainingStamp(id, new Date(start_date), new Date(end_date), comment, TrainingStampStatus.valueOf(status), getTrainingSetListInTrainingStamp(id));
            } else {
                return null;
            }
        } finally {
            if (c != null) c.close();
            if (db != null && db.isOpen()) db.close();
        }
    }

    public Double getMaxExerciseResultByPos(Long ex_id, int position) {
        String sqlQuery = "select max(tr_set_value.value) max_value from TrainingSet tr_set, TrainingSetValue tr_set_value " +
                "where tr_set.exercise_id = ? and  tr_set_value.training_set_id = tr_set.id and tr_set_value.position = ? ";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id), String.valueOf(position)});
        try {
            if (c.moveToFirst()) {
                Double max_value = c.getDouble(c.getColumnIndex("max_value"));
                return max_value;
            } else {
                return null;
            }
        } finally {
            if (c != null) c.close();
        }
    }

    public TrainingStamp getOpenTrainingStamp() {
        String sqlQuery = "select * from TrainingStamp tr_stat " +
                "where tr_stat.id = (SELECT MAX(id) FROM TrainingStamp where status = 'OPEN')";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, null);
        try {
            if (c.moveToFirst()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long start_date = c.getLong(c.getColumnIndex("start_date"));
                Long end_date = c.getLong(c.getColumnIndex("end_date"));
                String comment = c.getString(c.getColumnIndex("comment"));
                String status = c.getString(c.getColumnIndex("status"));
                return new TrainingStamp(id, new Date(start_date), new Date(end_date), comment, TrainingStampStatus.valueOf(status));
            } else {
                return null;
            }
        } finally {
            if (c != null) c.close();
            if (db != null && db.isOpen()) db.close();
        }

    }


    public List<TrainingStamp> getOpenTrainingStampList() {
        List<TrainingStamp> list = new ArrayList<TrainingStamp>();
        String sqlQuery = "select tr_stat.* from TrainingStamp tr_stat " +
                "where tr_stat.status = 'OPEN' order by tr_stat.id asc";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, null);
        try {
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long start_date = c.getLong(c.getColumnIndex("start_date"));
                Long end_date = c.getLong(c.getColumnIndex("end_date"));
                String comment = c.getString(c.getColumnIndex("comment"));
                String status = c.getString(c.getColumnIndex("status"));
                list.add(new TrainingStamp(id, new Date(start_date), new Date(end_date), comment, TrainingStampStatus.valueOf(status)));
            }
        } finally {
            if (c != null) c.close();
            if (db != null && db.isOpen()) db.close();
        }
        return list;
    }

    public TrainingStamp getTrainingStampByStartDate(Date training_date) {
        String sqlQuery = "select * from TrainingStamp tr_stat " +
                "where tr_stat.start_date = ?";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(training_date.getTime())});
        try {
            if (c.moveToFirst()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long start_date = c.getLong(c.getColumnIndex("start_date"));
                Long end_date = c.getLong(c.getColumnIndex("end_date"));
                String comment = c.getString(c.getColumnIndex("comment"));
                String status = c.getString(c.getColumnIndex("status"));
                return new TrainingStamp(id, new Date(start_date), new Date(end_date), comment, TrainingStampStatus.valueOf(status), getTrainingSetListInTrainingStamp(id));
            } else {
                return null;
            }
        } finally {
            if (c != null) c.close();
            if (db != null && db.isOpen()) db.close();
        }

    }

    public Long getExercisePositionInTraining(long ex_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sqlQuery = "select t.position as pos from ExerciseInTraining t where t.exercise_id = ?";
        Cursor c = null;
        try {
            c = db.rawQuery(sqlQuery, new String[]{String.valueOf(ex_id)});
            c.moveToFirst();
            Long pos = c.getLong(c.getColumnIndex("pos"));
            return pos;
        } finally {
            if (c != null) c.close();
            if (db != null) db.close();
        }

    }
}