package myApp.trainingdiary.forBD;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import myApp.trainingdiary.R;
import myApp.trainingdiary.constant.Consts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper mInstance = null;

    Context context;

    private final static int DB_VERSION = 3; // ������ ��

    public static DBHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private DBHelper(Context context) {
        super(context, "TrainingDiaryDB", null, DB_VERSION); // ��������� �����
        // ������
        // ��!!!!
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTrainingTable(db);
        createExerciseTypeTable(db);
        createExerciseTable(db);
        createExerciseInTrainingTable(db);
        createMeasureTable(db);
        createMeasureExTypeTable(db);
        createTrainingStatTable(db);

        createInitialTypes(db);
        createInitialExercises(db);
    }

    /**
     * �������
     */
    private void createTrainingStatTable(SQLiteDatabase db) {

        db.execSQL("create table TrainingStat ("
                + "id integer primary key autoincrement,"
                + "date datetime," + "exercise_id integer,"
                + "value text,"
                + "training_id integer,"
                + "FOREIGN KEY(exercise_id) REFERENCES Exercise(id)" + ");");

        Log.d(Consts.LOG_TAG, "--- onCreate BD TrainingStat ---");
    }

    private void createMeasureExTypeTable(SQLiteDatabase db) {
        db.execSQL("create table MeasureExType (" + "ex_type_id integer,"
                + "measure_id integer," + "position integer,"
                + "FOREIGN KEY(ex_type_id) REFERENCES ExerciseType(id),"
                + "FOREIGN KEY(measure_id) REFERENCES Measure(id),"
                + "PRIMARY KEY (ex_type_id, measure_id)" + ");");

        Log.d(Consts.LOG_TAG, "--- onCreate table MeasureExType ---");
    }

    /**
     * ������ ������� ���������
     */
    private void createMeasureTable(SQLiteDatabase db) {

        db.execSQL("create table Measure ("
                + "id integer primary key autoincrement," + "name text,"
                + "max integer," + "step float," + "type integer" + ");");

        Log.d(Consts.LOG_TAG, "--- onCreate table Measure ---");
    }

    /**
     * ������ ������� ����������� ���������� - ���������� trainingname -
     * �������� ���������� exercise - �������� ���������� exidintr - �����
     * ���������� � ����������
     */
    private void createExerciseInTrainingTable(SQLiteDatabase db) {

        db.execSQL("create table ExerciseInTraining (" + "training_id integer,"
                + "exercise_id integer," + "position integer,"
                + "FOREIGN KEY(training_id) REFERENCES Training(id),"
                + "FOREIGN KEY(exercise_id) REFERENCES Exercise(id),"
                + "PRIMARY KEY (training_id, exercise_id)" + ");");

        Log.d(Consts.LOG_TAG, "--- onCreate table ExerciseInTraining ---");
    }

    /**
     * ������ ������� ���������� exercise - �������� ���������� type - ���
     * ����������;
     */
    private void createExerciseTable(SQLiteDatabase db) {

        db.execSQL("create table Exercise ("
                + "id integer primary key autoincrement," + "name text,"
                + "type_id integer,"
                + "FOREIGN KEY(type_id) REFERENCES ExerciseType(id)" + ");");

        Log.d(Consts.LOG_TAG, "--- onCreate table Exercise ---");
    }

    /**
     * ��� ����������
     */
    private void createExerciseTypeTable(SQLiteDatabase db) {

        db.execSQL("create table ExerciseType ("
                + "id integer primary key autoincrement," + "name text,"
                + "icon_res text" + ");");

        Log.d(Consts.LOG_TAG, "--- onCreate table ExerciseType ---");
    }

    /**
     * ������� ������� ���������� � ������ - ������ ������� � ����������
     * ���������� ����1 ����2 ���...
     */
    private void createTrainingTable(SQLiteDatabase db) {
        db.execSQL("create table Training ("
                + "id integer primary key autoincrement," + "name text,"
                + "position integer" + ");");
        Log.d(Consts.LOG_TAG, "--- onCreate table Training  ---");
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

    public long insertTrainingStat(SQLiteDatabase db, long exercise_id, long training_id,
                                   long trainingDate, String value) {

        ContentValues cv = new ContentValues();
        cv.put("date", trainingDate);
        cv.put("value", value);
        cv.put("exercise_id", exercise_id);
        cv.put("training_id", training_id);
        long id = db.insert("TrainingStat", null, cv);

        return id;
    }

    public long insertTrainingStat(long exercise_id, long training_id,
                                   long trainingDate, String value) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date", trainingDate);
        cv.put("value", value);
        cv.put("exercise_id", exercise_id);
        cv.put("training_id", training_id);
        long id = db.insert("TrainingStat", null, cv);
        db.close();
        return id;
    }

    private void createInitialExercises(SQLiteDatabase db) {
        // TODO: ������� ��������� ����������
    }

    private void createInitialTypes(SQLiteDatabase db) {
        long bw_m_id = insertMeasure(db,
                context.getString(R.string.baseMeasure_bar_weight), 500, 0.5, 0);

        long r_m_id = insertMeasure(db,
                context.getString(R.string.baseMeasure_repeat), 99, 1, 0);

        long power_id = insertExerciseType(db,
                context.getString(R.string.baseExType_power), context
                .getResources().getResourceName(R.drawable.power));

        long cycle_id = insertExerciseType(db,
                context.getString(R.string.baseExType_cycle), context
                .getResources().getResourceName(R.drawable.cycle));

        insertMeasureExType(db, power_id, bw_m_id, 0);
        insertMeasureExType(db, power_id, r_m_id, 1);

        long d_m_id = insertMeasure(db,
                context.getString(R.string.baseMeasure_distance), 99, 0.1, 0);

        long t_m_id = insertMeasure(db,
                context.getString(R.string.baseMeasure_time), 2, 1, 1);

        insertMeasureExType(db, cycle_id, d_m_id, 0);
        insertMeasureExType(db, cycle_id, t_m_id, 1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(Consts.LOG_TAG, "onUpgrade. oldVer: " + oldVersion + " newVer: "
                + newVersion);
        if (oldVersion == 1 && newVersion == 2) {
            upgradeFrom_1_To_2(db);
        }
        if (oldVersion == 1 && newVersion == 3) {
            upgradeFrom_1_To_2(db);
            upgradeFrom_2_To_3(db);
        }
        if (oldVersion == 2 && newVersion == 3) {
            upgradeFrom_2_To_3(db);
        }
    }

    private void upgradeFrom_1_To_2(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL("alter table TrainingProgTable add column exidintr integer;");
            db.setTransactionSuccessful();
            Log.d(Consts.LOG_TAG, "--- add column sucsessful ---");
        } finally {
            db.endTransaction();
        }
    }

    private void upgradeFrom_2_To_3(SQLiteDatabase db) {
        try {
            db.beginTransaction();

            renameTrainingStatTable(db);

            Cursor trainingTable_cursor = db.query("TrainingTable", null, null,
                    null, null, null, null);
            Cursor exerciseTable_cursor = db.query("ExerciseTable", null, null,
                    null, null, null, null);
            Cursor trainingProgTable_cursor = db.query("TrainingProgTable",
                    null, null, null, null, null, null);
            Cursor trainingStat_cursor = db.query("TrainingStatOld", null,
                    null, null, null, null, null);

            createTrainingTable(db);
            createExerciseTypeTable(db);
            createExerciseTable(db);
            createExerciseInTrainingTable(db);
            createMeasureTable(db);
            createMeasureExTypeTable(db);
            createTrainingStatTable(db);

            createInitialTypes(db);

            transferTrainingTableData(db, trainingTable_cursor);
            transferExerciseTableData(db, exerciseTable_cursor);
            transferTrainingProgTableData(db, trainingProgTable_cursor);
            transferTrainingStatData(db, trainingStat_cursor);

            dropAllTablesVer2(db);

            Log.d(Consts.LOG_TAG, "--- upgradeFrom_2_To_3 done ---");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void renameTrainingStatTable(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE TrainingStat RENAME TO TrainingStatOld");
    }

    private void transferTrainingStatData(SQLiteDatabase db, Cursor c) {
        if (c != null) {
            long power_id = findExTypeByName(db,
                    context.getString(R.string.baseExType_power));
            long cycle_id = findExTypeByName(db,
                    context.getString(R.string.baseExType_cycle));
            DecimalFormat formatter = new java.text.DecimalFormat("#.#");
            if (c.moveToFirst()) {
                do {
                    String trainingDate = c.getString(c
                            .getColumnIndex("trainingdate"));
                    String trainingDay = c.getString(c
                            .getColumnIndex("trainingday"));
                    String exercise = c.getString(c.getColumnIndex("exercise"));
                    String exerciseType = c.getString(c
                            .getColumnIndex("exercisetype"));
                    double power = c.getDouble(c.getColumnIndex("power"));
                    int count = c.getInt(c.getColumnIndex("count"));
                    long ex_id = findExerciseByName(db, exercise);
                    String value = DbFormatter.toStatValue(formatter.format(power),
                            String.valueOf(count));
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    Date date = null;
                    try {
                        date = sdf.parse(trainingDate);
                    } catch (ParseException e) {
                    }

                    if (ex_id > 0 && date != null) {
                        insertTrainingStat(db, ex_id, 0, date.getTime(), value);
                    } else {
                        Log.e(Consts.LOG_TAG,
                                "Cannot insert TrainingStat cause - ex_id: "
                                        + ex_id + " date: " + date);
                    }
                } while (c.moveToNext());
            }
        }
    }

    private void transferTrainingProgTableData(SQLiteDatabase db, Cursor c) {
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    String ex_name = c.getString(c.getColumnIndex("exercise"));
                    String tr_name = c.getString(c
                            .getColumnIndex("trainingname"));
                    int position = c.getInt(c.getColumnIndex("exidintr"));
                    long ex_id = findExerciseByName(db, ex_name);
                    long tr_id = findTrainingByName(db, tr_name);
                    if (ex_id > 0 && tr_id > 0)
                        insertExerciseInTraining(db, tr_id, ex_id, position);
                    else {
                        Log.e(Consts.LOG_TAG,
                                "Cannot insert ExerciseInTraining cause - ex_id: "
                                        + ex_id + "tr_id: " + tr_id);
                    }
                } while (c.moveToNext());
            }
        }
    }

    private long findTrainingByName(SQLiteDatabase db, String tr_name) {
        long id = -1;
        Cursor c = db.rawQuery("select id from Training where name='" + tr_name
                + "'", null);

        if (c != null) {
            if (c.moveToFirst()) {
                id = c.getInt(c.getColumnIndex("id"));
            }
        }
        return id;
    }

    private long findExerciseByName(SQLiteDatabase db, String ex_name) {
        long id = -1;
        Cursor c = db.rawQuery("select id from Exercise where name='" + ex_name
                + "'", null);

        if (c != null) {
            if (c.moveToFirst()) {
                id = c.getInt(c.getColumnIndex("id"));
            }
        }
        return id;
    }

    private void transferExerciseTableData(SQLiteDatabase db, Cursor c) {
        if (c != null) {
            long power_id = findExTypeByName(db,
                    context.getString(R.string.baseExType_power));
            long cycle_id = findExTypeByName(db,
                    context.getString(R.string.baseExType_cycle));
            Log.i(Consts.LOG_TAG, "findPowerExType: " + power_id);
            Log.i(Consts.LOG_TAG, "findCycleExType: " + cycle_id);
            if (c.moveToFirst()) {
                do {
                    String name = c.getString(c.getColumnIndex("exercise"));
                    int type = c.getInt(c.getColumnIndex("type"));
                    switch (type) {
                        case 1:
                            insertExercise(db, name, power_id);
                            break;
                        case 2:
                            insertExercise(db, name, cycle_id);
                            break;
                    }

                } while (c.moveToNext());
            }
        }
    }

    private long findExTypeByName(SQLiteDatabase db, String name) {
        Cursor c = db.rawQuery("select id from ExerciseType where name='"
                + name + "'", null);
        long id = -1;
        if (c != null) {
            if (c.moveToFirst()) {
                id = c.getInt(c.getColumnIndex("id"));
            }
        }
        return id;
    }

    private void transferTrainingTableData(SQLiteDatabase db, Cursor c) {
        int position = 0;
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    String name = c.getString(c.getColumnIndex("name"));
                    insertTraining(db, name, position);
                    position++;
                } while (c.moveToNext());
            }
        }
    }

    private void dropAllTablesVer2(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS TrainingTable");
        db.execSQL("DROP TABLE IF EXISTS ExerciseTable");
        db.execSQL("DROP TABLE IF EXISTS TrainingProgTable");
        db.execSQL("DROP TABLE IF EXISTS TrainingStatOld");
    }

    public void changeTrainingPositions(List<Long> list) {
        SQLiteDatabase db = getWritableDatabase();
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

    public Cursor getExersices(SQLiteDatabase db, String strNameTr) {
        String sqlQuery = "select " + "prog.id as _id, "
                + "prog.exercise as ex_name, " + "prog.trainingname as Tr, "
                + "prog.exidintr as id," + "Ex.type as type "
                + "from TrainingProgTable as prog "
                + "inner join ExerciseTable as Ex "
                + "on prog.exercise=Ex.exercise " + "where Tr = ? "
                + "ORDER BY id";

        String[] args = {strNameTr};
        Cursor c = db.rawQuery(sqlQuery, args);
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

    public Cursor getExercisesExceptExInTr(SQLiteDatabase db, long tr_id) {
        // TODO: ����� �������� ������ ������� ���������� ������ �� ����������
        // ������� �� ������ � ������ ����������
        String sqlQuery = "select ex.id as _id, ex.name name, ex_type.icon_res icon_res "
                + "from Exercise ex, ExerciseType ex_type "
                + "where (ex.type_id = ex_type.id)";
        Cursor c = db.rawQuery(sqlQuery, null);
        return c;
    }

    public Cursor getExerciseTypes(SQLiteDatabase db) {
        String sqlQuery = "select ex_type.id as _id, ex_type.name name, ex_type.icon_res icon_res "
                + "from ExerciseType ex_type";
        Cursor c = db.rawQuery(sqlQuery, null);
        return c;
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

    public Long createTraining(String name) {
        SQLiteDatabase db = getWritableDatabase();
        int position = getTrainingsCount(db);
        Long id = insertTraining(db, name, position);
        return id;
    }

    public Cursor getExercisesExceptExInTr(long tr_id) {
        Cursor c = getExercisesExceptExInTr(getWritableDatabase(), tr_id);
        return c;
    }

    public String getTrainingNameById(long tr_id) {
        SQLiteDatabase db = getWritableDatabase();
        String name = getTrainingNameById(db, tr_id);
        db.close();
        return name;
    }

    public void renameTraining(long tr_id, String name) {
        SQLiteDatabase db = getWritableDatabase();
        renameTraining(db, tr_id, name);
        db.close();
    }

    public Cursor getTrainings() {
        Cursor c = getTrainings(getWritableDatabase());
        return c;
    }

    public Cursor getExercisesInTraining(long tr_id) {
        Cursor c = getExercisesInTraining(getWritableDatabase(), tr_id);
        return c;
    }

    public void deleteTraining(long tr_id) {
        SQLiteDatabase db = getWritableDatabase();
        deleteTraining(db, tr_id);
        db.close();
    }

    public Cursor getExerciseTypes() {
        Cursor c = getExerciseTypes(getWritableDatabase());
        return c;
    }

    public void renameExercise(long ex_id, String name) {
        SQLiteDatabase db = getWritableDatabase();
        renameExercise(db, ex_id, name);
        db.close();
    }

    private void renameExercise(SQLiteDatabase db, long ex_id, String name) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        db.update("Exercise", cv, "id = ? ",
                new String[]{String.valueOf(ex_id)});
    }

    public String getExerciseNameById(long ex_id) {
        SQLiteDatabase db = getWritableDatabase();
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

    public void deleteExerciseFromTraining(long tr_id, long ex_id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("ExerciseInTraining", "training_id = ? AND exercise_id = ?",
                new String[]{String.valueOf(tr_id), String.valueOf(ex_id)});
        db.close();
    }

    public void changeExercisePositions(long tr_id, List<Long> list) {
        SQLiteDatabase db = getWritableDatabase();
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

    public TrainingStat getLastTrainingStatByExerciseInTraining(long ex_id, long tr_id) {

        String sqlQuery = "select * from TrainingStat tr_stat " +
                "where tr_stat.id = (SELECT MAX(id) FROM TrainingStat WHERE training_id = ? AND exercise_id = ?)";
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id), String.valueOf(tr_id)});
        try {
            if (c.moveToFirst()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long trainingDate = c.getLong(c.getColumnIndex("date"));
                Long exerciseId = c.getLong(c.getColumnIndex("exercise_id"));
                Long trainingId = c.getLong(c.getColumnIndex("training_id"));
                String value = c.getString(c.getColumnIndex("value"));
                return new TrainingStat(id, new Date(trainingDate), exerciseId, trainingId, value);
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
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id)});
        while (c.moveToNext()) {
            Long id = c.getLong(c.getColumnIndex("id"));
            String name = c.getString(c.getColumnIndex("name"));
            Integer max = c.getInt(c.getColumnIndex("max"));
            Double step = c.getDouble(c.getColumnIndex("step"));
            Integer type = c.getInt(c.getColumnIndex("type"));
            measures.add(new Measure(id, name, max, step, type));
        }
        c.close();

        return measures;
    }

    public List<TrainingStat> getTrainingStatForLastPeriod(long ex_id, int period) {
        List<TrainingStat> stats = new ArrayList<TrainingStat>();
        long since = new Date().getTime() - period;
        String sqlQuery = "select * from TrainingStat tr_stat " +
                "where tr_stat.exercise_id = ? AND tr_stat.date > ? " +
                "order by tr_stat.date ";
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(ex_id), String.valueOf(since)});
        try {
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("id"));
                Long trainingDate = c.getLong(c.getColumnIndex("date"));
                Long exerciseId = c.getLong(c.getColumnIndex("exercise_id"));
                Long trainingId = c.getLong(c.getColumnIndex("training_id"));
                String value = c.getString(c.getColumnIndex("value"));
                stats.add(new TrainingStat(id, new Date(trainingDate), exerciseId, trainingId, value));
            }
            return stats;
        } finally {
            c.close();
        }
    }

    public void deleteLastTrainingStat(long ex_id, long tr_id) {
        SQLiteDatabase db = getWritableDatabase();
//        String sqlQuery = "select * from TrainingStat tr_stat " +
//                "where tr_stat.exercise_id = ? AND tr_stat.date > ? " +
//                "order by tr_stat.date ";
//        db.execSQL();
        db.delete("TrainingStat", " id = (SELECT MAX(id) FROM TrainingStat WHERE training_id = ? AND exercise_id = ?) ",
                new String[]{String.valueOf(tr_id), String.valueOf(ex_id)});
        db.close();
    }
}
