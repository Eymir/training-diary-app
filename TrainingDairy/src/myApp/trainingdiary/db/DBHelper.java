package myApp.trainingdiary.db;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import myApp.trainingdiary.R;
import myApp.trainingdiary.db.entity.EntityManager;
import myApp.trainingdiary.db.entity.ExerciseType;
import myApp.trainingdiary.db.entity.Measure;
import myApp.trainingdiary.db.entity.MeasureType;
import myApp.trainingdiary.utils.Consts;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {


    private static DBHelper mInstance = null;


    private final static int DB_VERSION = 4;

    public DbReader READ;
    public DbWriter WRITE;
    public Context CONTEXT;
    private EntityManager EM;

    public static DBHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }


    private DBHelper(Context context) {
        super(context, "TrainingDiaryDB", null, DB_VERSION);
        READ = new DbReader(this);
        WRITE = new DbWriter(this);
        EM = new EntityManager(this);
        this.CONTEXT = context;
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
                + "date datetime,"
                + "training_date datetime,"
                + "exercise_id integer,"
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


    private void createInitialExercises(SQLiteDatabase db) {
        // TODO: ������� ��������� ����������
    }

    private void createInitialTypes(SQLiteDatabase db) {
        ExerciseType power_weight_type = new ExerciseType(null,
                CONTEXT.getResources().getResourceName(R.drawable.power),
                CONTEXT.getString(R.string.bar_ex_type_base));
        power_weight_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.power_weight_measure_base),
                        500, 0.5, MeasureType.Numeric));
        power_weight_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.repeat_measure_base),
                        99, 1.0, MeasureType.Numeric));
        EM.persist(db, power_weight_type);

        ExerciseType long_dist_type = new ExerciseType(null,
                CONTEXT.getResources().getResourceName(R.drawable.cycle),
                CONTEXT.getString(R.string.long_dist_ex_type_base));
        long_dist_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.long_distance_measure_base),
                        99, 0.1, MeasureType.Numeric));
        long_dist_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.baseMeasure_time),
                        2, 1.0, MeasureType.Temporal));
        EM.persist(db, long_dist_type);

        ExerciseType count_type = new ExerciseType(null,
                CONTEXT.getResources().getResourceName(R.drawable.count),
                CONTEXT.getString(R.string.count_ex_type_base));
        count_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.repeat_measure_base),
                        500, 1.0, MeasureType.Numeric));
        EM.persist(db, count_type);

        ExerciseType weight_type = new ExerciseType(null,
                CONTEXT.getResources().getResourceName(R.drawable.weight),
                CONTEXT.getString(R.string.weight_ex_type_base));
        weight_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.weight_measure_base),
                        500, 0.001, MeasureType.Numeric));
        EM.persist(db, weight_type);

        ExerciseType size_type = new ExerciseType(null,
                CONTEXT.getResources().getResourceName(R.drawable.size),
                CONTEXT.getString(R.string.size_ex_type_base));
        size_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.size_measure_base),
                        99, 0.1, MeasureType.Numeric));
        EM.persist(db, size_type);
    }

    private void createInitialTypes_ver_3(SQLiteDatabase db) {
        long bw_m_id = WRITE.insertMeasure(db,
                CONTEXT.getString(R.string.power_weight_measure_base), 500, 0.5, 0);

        long r_m_id = WRITE.insertMeasure(db,
                CONTEXT.getString(R.string.repeat_measure_base), 99, 1, 0);

        long power_id = WRITE.insertExerciseType(db,
                CONTEXT.getString(R.string.bar_ex_type_base), CONTEXT
                .getResources().getResourceName(R.drawable.power));

        long cycle_id = WRITE.insertExerciseType(db,
                CONTEXT.getString(R.string.long_dist_ex_type_base), CONTEXT
                .getResources().getResourceName(R.drawable.cycle));

        WRITE.insertMeasureExType(db, power_id, bw_m_id, 0);
        WRITE.insertMeasureExType(db, power_id, r_m_id, 1);

        long d_m_id = WRITE.insertMeasure(db,
                CONTEXT.getString(R.string.long_distance_measure_base), 99, 0.1, 0);

        long t_m_id = WRITE.insertMeasure(db,
                CONTEXT.getString(R.string.baseMeasure_time), 2, 1, 1);

        WRITE.insertMeasureExType(db, cycle_id, d_m_id, 0);
        WRITE.insertMeasureExType(db, cycle_id, t_m_id, 1);
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
        if (oldVersion == 3 && newVersion == 4) {
            upgradeFrom_3_To_4(db);
        }
    }

    private void upgradeFrom_3_To_4(SQLiteDatabase db) {

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

            createInitialTypes_ver_3(db);

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
                    CONTEXT.getString(R.string.bar_ex_type_base));
            long cycle_id = findExTypeByName(db,
                    CONTEXT.getString(R.string.long_dist_ex_type_base));
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
                        WRITE.insertTrainingStat(db, ex_id, 0, date.getTime(), date.getTime(), value);
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
                        WRITE.insertExerciseInTraining(db, tr_id, ex_id, position);
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
                    CONTEXT.getString(R.string.bar_ex_type_base));
            long cycle_id = findExTypeByName(db,
                    CONTEXT.getString(R.string.long_dist_ex_type_base));
            Log.i(Consts.LOG_TAG, "findPowerExType: " + power_id);
            Log.i(Consts.LOG_TAG, "findCycleExType: " + cycle_id);
            if (c.moveToFirst()) {
                do {
                    String name = c.getString(c.getColumnIndex("exercise"));
                    int type = c.getInt(c.getColumnIndex("type"));
                    switch (type) {
                        case 1:
                            WRITE.insertExercise(db, name, power_id);
                            break;
                        case 2:
                            WRITE.insertExercise(db, name, cycle_id);
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
                    WRITE.insertTraining(db, name, position);
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

}
