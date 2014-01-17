package myApp.trainingdiary.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myApp.trainingdiary.R;
import myApp.trainingdiary.db.entity.EntityManager;
import myApp.trainingdiary.db.entity.Exercise;
import myApp.trainingdiary.db.entity.ExerciseType;
import myApp.trainingdiary.db.entity.ExerciseTypeIcon;
import myApp.trainingdiary.db.entity.Measure;
import myApp.trainingdiary.db.entity.MeasureType;
import myApp.trainingdiary.db.entity.TrainingSet;
import myApp.trainingdiary.db.entity.TrainingStampStatus;
import myApp.trainingdiary.db.entity.TrainingStat;
import myApp.trainingdiary.utils.Const;
import myApp.trainingdiary.utils.MeasureFormatter;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper mInstance = null;

    private final static int DB_VERSION = 7;
    public final static String DATABASE_NAME = "TrainingDiaryDB";

    public DbReader READ;
    public DbWriter WRITE;
    public Context CONTEXT;
    public EntityManager EM;

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
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(Const.LOG_TAG, "onDowngrade. oldVer: " + oldVersion + " newVer: " + newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTrainingTable(db);
        createExerciseTypeTable(db);
        createExerciseTable(db);
        createExerciseInTrainingTable(db);
        createMeasureTable(db);
        createMeasureExTypeTable(db);

        createTrainingSetTable(db);
        createTrainingStampTable(db);
        createTrainingSetValueTable(db);

        Log.d(Const.LOG_TAG, "before - createInitialTypes count: "
                + READ.getExerciseTypeCount(db));
        createInitialTypes(db);
        Log.d(Const.LOG_TAG, "after - createInitialTypes count: "
                + READ.getExerciseTypeCount(db));
        Log.d(Const.LOG_TAG, "before - createInitialExercises count: "
                + READ.getExerciseCount(db));
        createInitialExercises(db);
        Log.d(Const.LOG_TAG, "after - createInitialExercises count: "
                + READ.getExerciseCount(db));
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(Const.LOG_TAG, "onUpgrade. oldVer: " + oldVersion + " newVer: "
                + newVersion);
        switch (oldVersion) {
            case 1:
                upgradeFrom_1_To_2(db);
            case 2:
                upgradeFrom_2_To_3(db);
            case 3:
                upgradeFrom_3_To_4(db);
            case 4:
                upgradeFrom_4_To_5(db);
            case 5:
                upgradeFrom_5_To_6(db);
            case 6:
                upgradeFrom_6_To_7(db);
        }
    }

    private void upgradeFrom_6_To_7(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            Log.i(Const.LOG_TAG, "upgradeFrom_6_To_7 start");
            alterDeleted_6_7(db);
            alterPresetUid_6_7(db);
            createTrainingStampTable(db);
            createTrainingSetTable(db);
            createTrainingSetValueTable(db);
            transferTrainingStatDataToSetStampValue_6_7(db);
            db.setTransactionSuccessful();
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, "upgradeFrom_6_To_7 problem", e);
        } finally {
            db.endTransaction();
        }
    }

    private void transferTrainingStatDataToSetStampValue_6_7(SQLiteDatabase db) {
        List<TrainingStat> trainingStats = READ.getAllTrainingStat(db);
        Date trainingDate = new Date(0L);
        Long tr_stamp_id = null;
        for (TrainingStat stat : trainingStats) {
            if (!trainingDate.equals(stat.getTrainingDate())) {
                if (tr_stamp_id != null) {
                    TrainingSet trainingSet = READ.getTrainingSetWithMaxIdFromTrainingStamp(db, tr_stamp_id);
                    WRITE.closeTrainingStamp(db, tr_stamp_id, trainingSet.getDate());
                }
                tr_stamp_id = WRITE.insertTrainingStamp(db, stat.getTrainingDate(), null, null, TrainingStampStatus.OPEN.name());
                trainingDate = stat.getTrainingDate();
            }
            long tr_set_id = WRITE.insertTrainingSet(db, tr_stamp_id, stat.getExerciseId(), stat.getTrainingId(), stat.getDate());
            createTrainingValuesByString(db, tr_set_id, stat.getValue());
        }
    }

    private void createTrainingValuesByString(SQLiteDatabase db, long tr_set_id, String value) {
        List<String> str_values = MeasureFormatter.toMeasureValues(value);
        for (int i = 0; i < str_values.size(); i++) {
            Double d_v = 0D;
            try {
                String str_v = str_values.get(i);
                if (str_v.contains(":")) {
                    String[] time_list = str_v.split(":");
                    int min = Integer.valueOf(time_list[0]);
                    int sec = Integer.valueOf(time_list[1]);
                    d_v = Integer.valueOf((min * 60 + sec) * 1000).doubleValue();
                } else {
                    d_v = Double.valueOf(str_v);
                }
            } catch (Throwable e) {
                Log.e(Const.LOG_TAG, e.getMessage(), e);
            }
            WRITE.insertTrainingSetValue(db, tr_set_id, i, d_v);
        }


    }

    private void alterPresetUid_6_7(SQLiteDatabase db) {
        Log.i(Const.LOG_TAG, "alterPresetUid_6_7 start");
        db.execSQL("alter table Exercise add column deleted integer;");
        db.execSQL("alter table ExerciseType add column deleted integer;");
        db.execSQL("alter table Measure add column deleted integer;");
    }

    private void alterDeleted_6_7(SQLiteDatabase db) {
        Log.i(Const.LOG_TAG, "alterDeleted_6_7 start");
        db.execSQL("alter table Exercise add column preset_uid text;");
        db.execSQL("alter table ExerciseType add column preset_uid text;");
        db.execSQL("alter table Measure add column preset_uid text;");
    }

    private void upgradeFrom_5_To_6(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            Log.i(Const.LOG_TAG, "upgradeFrom_5_To_6 start");
            renameExerciseTypeIcon_ver_6(db);
            db.setTransactionSuccessful();
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, "upgradeFrom_5_To_6 problem", e);
        } finally {

            db.endTransaction();
        }
    }

    private void renameExerciseTypeIcon_ver_6(SQLiteDatabase db) {
        Map<String, ExerciseTypeIcon> map = new HashMap<String, ExerciseTypeIcon>();
        map.put("myApp.trainingdiary:drawable/power", ExerciseTypeIcon.icon_ex_power);
        map.put("myApp.trainingdiary:drawable/dumbbell", ExerciseTypeIcon.icon_ex_dumbbell);
        map.put("myApp.trainingdiary:drawable/cycle", ExerciseTypeIcon.icon_ex_cycle);
        map.put("myApp.trainingdiary:drawable/count", ExerciseTypeIcon.icon_ex_count);
        map.put("myApp.trainingdiary:drawable/weight", ExerciseTypeIcon.icon_ex_weight);
        map.put("myApp.trainingdiary:drawable/size", ExerciseTypeIcon.icon_ex_size);
        for (String key : map.keySet()) {
            Long id = READ.getExerciseTypeIdByIconRes(db, key);
            if (id != null)
                WRITE.changeExerciseTypeIconRes(db, id, map.get(key).getIconResName());
        }
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

        Log.d(Const.LOG_TAG, "--- onCreate BD TrainingStat ---");
    }

    private void createMeasureExTypeTable(SQLiteDatabase db) {
        db.execSQL("create table MeasureExType (" + "ex_type_id integer,"
                + "measure_id integer," + "position integer,"
                + "FOREIGN KEY(ex_type_id) REFERENCES ExerciseType(id),"
                + "FOREIGN KEY(measure_id) REFERENCES Measure(id),"
                + "PRIMARY KEY(ex_type_id, measure_id)" + ");");

        Log.d(Const.LOG_TAG, "--- onCreate table MeasureExType ---");
    }

    /**
     * ������ ������� ���������
     */
    private void createMeasureTable(SQLiteDatabase db) {

        db.execSQL("create table Measure ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "preset_uid text,"
                + "deleted integer,"
                + "max integer,"
                + "step float,"
                + "type integer" + ");");

        Log.d(Const.LOG_TAG, "--- onCreate table Measure ---");
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

        Log.d(Const.LOG_TAG, "--- onCreate table ExerciseInTraining ---");
    }

    /**
     * ������ ������� ���������� exercise - �������� ���������� type - ���
     * ����������;
     */
    private void createExerciseTable(SQLiteDatabase db) {

        db.execSQL("create table Exercise ("
                + "id integer primary key autoincrement," + "name text,"
                + "type_id integer,"
                + "deleted integer,"
                + "preset_uid text,"
                + "FOREIGN KEY(type_id) REFERENCES ExerciseType(id)" + ");");

        Log.d(Const.LOG_TAG, "--- onCreate table Exercise ---");
    }

    /**
     * ��� ����������
     */
    private void createExerciseTypeTable(SQLiteDatabase db) {

        db.execSQL("create table ExerciseType ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "deleted integer,"
                + "preset_uid text,"
                + "icon_res text" + ");");

        Log.d(Const.LOG_TAG, "--- onCreate table ExerciseType ---");
    }

    /**
     * ������� ������� ���������� � ������ - ������ ������� � ����������
     * ���������� ����1 ����2 ���...
     */
    private void createTrainingTable(SQLiteDatabase db) {
        db.execSQL("create table Training ("
                + "id integer primary key autoincrement," + "name text,"
                + "position integer" + ");");
        Log.d(Const.LOG_TAG, "--- onCreate table Training  ---");
    }

    private void createTrainingSetValueTable(SQLiteDatabase db) {
        db.execSQL("create table TrainingSetValue ("
                + "id integer primary key autoincrement,"
                + "training_set_id integer,"
                + "position integer,"
                + "value double,"
                + "FOREIGN KEY(training_set_id) REFERENCES TrainingSet(id));");
    }

    private void createTrainingStampTable(SQLiteDatabase db) {
        db.execSQL("create table TrainingStamp ("
                + "id integer primary key autoincrement,"
                + "start_date datetime,"
                + "end_date datetime,"
                + "comment text,"
                + "status text);");
    }

    private void createTrainingSetTable(SQLiteDatabase db) {
        db.execSQL("create table TrainingSet ("
                + "id integer primary key autoincrement,"
                + "date datetime,"
                + "training_stamp_id integer,"
                + "exercise_id integer,"
                + "training_id integer,"
                + "FOREIGN KEY(exercise_id) REFERENCES Exercise(id),"
                + "FOREIGN KEY(training_stamp_id) REFERENCES TrainingStamp(id)" + ");");
    }


    private void createInitialExercises(SQLiteDatabase db) {
        Log.d(Const.LOG_TAG, "--- onCreate createInitialExercises ---");
        //Jim
        Exercise jim = new Exercise(null,
                READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.bar_ex_type_base)),
                CONTEXT.getString(R.string.jim_ex_name_base));
        EM.persist(db, jim);
        //Ganteli
        Exercise dumbbell = new Exercise(null,
                READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.dumbbell_ex_type_base)),
                CONTEXT.getString(R.string.dumbbell_ex_name_base));
        EM.persist(db, dumbbell);
        //Prised
        Exercise prised = new Exercise(null,
                READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.bar_ex_type_base)),
                CONTEXT.getString(R.string.prised_ex_name_base));
        EM.persist(db, prised);
        //Stanovaya
        Exercise stanovaya = new Exercise(null,
                READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.bar_ex_type_base)),
                CONTEXT.getString(R.string.stanovaya_ex_name_base));
        EM.persist(db, stanovaya);
        //Штанга на бицепс
        Exercise bar_bitceps = new Exercise(null,
                READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.bar_ex_type_base)),
                CONTEXT.getString(R.string.bitceps_ex_name_base));
        EM.persist(db, bar_bitceps);
        //Французкий жим
        Exercise french_jim = new Exercise(null,
                READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.bar_ex_type_base)),
                CONTEXT.getString(R.string.french_ex_name_base));
        EM.persist(db, french_jim);
        //Бег (км)
        Exercise run = new Exercise(null,
                READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.long_dist_ex_type_base)),
                CONTEXT.getString(R.string.run_ex_name_base));
        EM.persist(db, run);
        //Отжимания
        Exercise pushups = new Exercise(null,
                READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.count_ex_type_base)),
                CONTEXT.getString(R.string.pushups_ex_name_base));
        EM.persist(db, pushups);
        //Подтягивания
        Exercise podtyagivaniya = new Exercise(null,
                READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.count_ex_type_base)),
                CONTEXT.getString(R.string.podtyagivaniya_ex_name_base));
        EM.persist(db, podtyagivaniya);
        //Вес
        Exercise weight = new Exercise(null,
                READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.weight_ex_type_base)),
                CONTEXT.getString(R.string.weight_ex_name_base));
        EM.persist(db, weight);
        //Объем бицепса
        Exercise bitceps_size = new Exercise(null,
                READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.size_ex_type_base)),
                CONTEXT.getString(R.string.bitceps_size_ex_name_base));
        EM.persist(db, bitceps_size);
    }

    private void createInitialTypes(SQLiteDatabase db) {
        Log.d(Const.LOG_TAG, "--- onCreate createInitialTypes ---");

        ExerciseType power_weight_type = new ExerciseType(null,
                ExerciseTypeIcon.icon_ex_power,
                CONTEXT.getString(R.string.bar_ex_type_base));
        Log.d(Const.LOG_TAG, "icon_res power: " + CONTEXT.getResources().getResourceName(R.drawable.icon_ex_power));
        power_weight_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.power_weight_measure_base),
                        500, 0.5, MeasureType.Numeric));
        power_weight_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.repeat_measure_base),
                        99, 1.0, MeasureType.Numeric));
        EM.persist(db, power_weight_type);

        ExerciseType dumbbells_type = new ExerciseType(null,
                ExerciseTypeIcon.icon_ex_dumbbell,
                CONTEXT.getString(R.string.dumbbell_ex_type_base));
        dumbbells_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.power_weight_measure_base),
                        99, 1.0, MeasureType.Numeric));
        dumbbells_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.repeat_measure_base),
                        99, 1.0, MeasureType.Numeric));
        EM.persist(db, dumbbells_type);

        ExerciseType long_dist_type = new ExerciseType(null,
                ExerciseTypeIcon.icon_ex_cycle,
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
                ExerciseTypeIcon.icon_ex_count,
                CONTEXT.getString(R.string.count_ex_type_base));
        count_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.repeat_measure_base),
                        500, 1.0, MeasureType.Numeric));
        EM.persist(db, count_type);

        ExerciseType weight_type = new ExerciseType(null,
                ExerciseTypeIcon.icon_ex_weight,
                CONTEXT.getString(R.string.weight_ex_type_base));
        weight_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.weight_measure_base),
                        500, 0.001, MeasureType.Numeric));
        EM.persist(db, weight_type);

        ExerciseType size_type = new ExerciseType(null,
                ExerciseTypeIcon.icon_ex_size,
                CONTEXT.getString(R.string.size_ex_type_base));
        size_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.size_measure_base),
                        299, 0.1, MeasureType.Numeric));
        EM.persist(db, size_type);
    }

    private void createInitialTypes_ver_3(SQLiteDatabase db) {
        long bw_m_id = WRITE.insertMeasure(db,
                CONTEXT.getString(R.string.power_weight_measure_base), 500, 0.5, 0);

        long r_m_id = WRITE.insertMeasure(db,
                CONTEXT.getString(R.string.repeat_measure_base), 99, 1, 0);

        long power_id = WRITE.insertExerciseType(db,
                CONTEXT.getString(R.string.bar_ex_type_base), CONTEXT
                .getResources().getResourceName(R.drawable.icon_ex_power));

        long cycle_id = WRITE.insertExerciseType(db,
                CONTEXT.getString(R.string.long_dist_ex_type_base), CONTEXT
                .getResources().getResourceName(R.drawable.icon_ex_cycle));

        WRITE.insertMeasureExType(db, power_id, bw_m_id, 0);
        WRITE.insertMeasureExType(db, power_id, r_m_id, 1);

        long d_m_id = WRITE.insertMeasure(db,
                CONTEXT.getString(R.string.long_distance_measure_base), 99, 0.1, 0);

        long t_m_id = WRITE.insertMeasure(db,
                CONTEXT.getString(R.string.baseMeasure_time), 2, 1, 1);

        WRITE.insertMeasureExType(db, cycle_id, d_m_id, 0);
        WRITE.insertMeasureExType(db, cycle_id, t_m_id, 1);
    }

    private void createTypes_ver_4(SQLiteDatabase db) {
        Log.d(Const.LOG_TAG, "--- createTypes_ver_4 ---");
        ExerciseType dumbbells_type = new ExerciseType(null,
                ExerciseTypeIcon.icon_ex_dumbbell,
                CONTEXT.getString(R.string.dumbbell_ex_type_base));
        dumbbells_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.power_weight_measure_base),
                        99, 1.0, MeasureType.Numeric));
        dumbbells_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.repeat_measure_base),
                        99, 1.0, MeasureType.Numeric));
        EM.persist(db, dumbbells_type);
        ExerciseType count_type = new ExerciseType(null,
                ExerciseTypeIcon.icon_ex_count,
                CONTEXT.getString(R.string.count_ex_type_base));
        count_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.repeat_measure_base),
                        500, 1.0, MeasureType.Numeric));
        EM.persist(db, count_type);

        ExerciseType weight_type = new ExerciseType(null,
                ExerciseTypeIcon.icon_ex_weight,
                CONTEXT.getString(R.string.weight_ex_type_base));
        weight_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.weight_measure_base),
                        300, 0.1, MeasureType.Numeric));
        EM.persist(db, weight_type);

        ExerciseType size_type = new ExerciseType(null,
                ExerciseTypeIcon.icon_ex_size,
                CONTEXT.getString(R.string.size_ex_type_base));
        size_type.getMeasures()
                .add(new Measure(null,
                        CONTEXT.getString(R.string.size_measure_base),
                        99, 0.1, MeasureType.Numeric));
        EM.persist(db, size_type);
    }


    private void upgradeFrom_3_To_4(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            Log.d(Const.LOG_TAG, "before - createTypes_ver_4 count: "
                    + READ.getExerciseTypeCount(db));
            renameTypes_ver_4(db);
            createTypes_ver_4(db);
            Log.d(Const.LOG_TAG, "after - createTypes_ver_4 count: "
                    + READ.getExerciseTypeCount(db));

            Log.d(Const.LOG_TAG, "before - createExercise_ver_4 count: "
                    + READ.getExerciseCount(db));
            createExercise_ver_4(db);
            Log.d(Const.LOG_TAG, "after - createExercise_ver_4 count: "
                    + READ.getExerciseCount(db));
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void upgradeFrom_4_To_5(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            Log.i(Const.LOG_TAG, "extendTypes_ver_5 start");
            extendTypes_ver_5(db);
            db.setTransactionSuccessful();
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, "extendTypes_ver_5 problem", e);
        } finally {
            db.endTransaction();
        }
        try {
            Log.i(Const.LOG_TAG, "renameExercise_ver_5 start");
            renameExercise_ver_5(db);
            db.setTransactionSuccessful();
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, "renameExercise_ver_5 problem", e);
        } finally {
            db.endTransaction();
        }
    }

    private void renameExercise_ver_5(SQLiteDatabase db) {
        Exercise exercise = null;
        exercise = READ.getExerciseByName(db, CONTEXT.getString(R.string.french_ex_name_base_wrong));
        Log.i(Const.LOG_TAG, "renameExercise_ver_5 looking for " + CONTEXT.getString(R.string.french_ex_name_base_wrong) + ": " + exercise);
        if (exercise != null)
            WRITE.renameExercise(db, exercise.getId(), CONTEXT.getString(R.string.french_ex_name_base));

    }

    private void extendTypes_ver_5(SQLiteDatabase db) {
        ExerciseType ex_type = READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.size_ex_type_base));
        Log.i(Const.LOG_TAG, "extendTypes_ver_5 looking for " + CONTEXT.getString(R.string.size_ex_type_base) + ": " + ex_type);
        if (ex_type != null) {
            List<Measure> measures = READ.getMeasuresInExerciseType(db, ex_type.getId());
            if (measures != null && !measures.isEmpty()) {
                Measure m = measures.get(0);
                m.setMax(299);
                WRITE.updateMeasure(db, m);
            }
        }
    }

    private void renameTypes_ver_4(SQLiteDatabase db) {
        ExerciseType ex_type = READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.baseExType_power));
        if (ex_type != null)
            WRITE.renameExerciseType(db, ex_type.getId(), CONTEXT.getString(R.string.bar_ex_type_base));

        ExerciseType ex_type_1 = READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.baseExType_cycle));
        if (ex_type_1 != null)
            WRITE.renameExerciseType(db, ex_type_1.getId(), CONTEXT.getString(R.string.long_dist_ex_type_base));
    }

    private void createExercise_ver_4(SQLiteDatabase db) {
        Log.d(Const.LOG_TAG, "--- createExercise_ver_4 ---");

        //Jim
        if (!READ.isExerciseInDB(db, CONTEXT.getString(R.string.jim_ex_name_base))) {
            Exercise jim = new Exercise(null,
                    READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.bar_ex_type_base)),
                    CONTEXT.getString(R.string.jim_ex_name_base));
            EM.persist(db, jim);
        }
        //Ganteli
        if (!READ.isExerciseInDB(db, CONTEXT.getString(R.string.dumbbell_ex_name_base))) {
            Exercise dumbbell = new Exercise(null,
                    READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.dumbbell_ex_type_base)),
                    CONTEXT.getString(R.string.dumbbell_ex_name_base));
            EM.persist(db, dumbbell);
        }
        //Prised
        if (!READ.isExerciseInDB(db, CONTEXT.getString(R.string.prised_ex_name_base))) {
            Exercise prised = new Exercise(null,
                    READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.bar_ex_type_base)),
                    CONTEXT.getString(R.string.prised_ex_name_base));
            EM.persist(db, prised);
        }
        //Stanovaya
        if (!READ.isExerciseInDB(db, CONTEXT.getString(R.string.stanovaya_ex_name_base))) {
            Exercise stanovaya = new Exercise(null,
                    READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.bar_ex_type_base)),
                    CONTEXT.getString(R.string.stanovaya_ex_name_base));
            EM.persist(db, stanovaya);
        }
        //Штанга на бицепс
        if (!READ.isExerciseInDB(db, CONTEXT.getString(R.string.bitceps_ex_name_base))) {
            Exercise bar_bitceps = new Exercise(null,
                    READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.bar_ex_type_base)),
                    CONTEXT.getString(R.string.bitceps_ex_name_base));
            EM.persist(db, bar_bitceps);
        }
        //Французский жим
        if (!READ.isExerciseInDB(db, CONTEXT.getString(R.string.french_ex_name_base))) {
            Exercise french_jim = new Exercise(null,
                    READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.bar_ex_type_base)),
                    CONTEXT.getString(R.string.french_ex_name_base));
            EM.persist(db, french_jim);
        }
        //Бег (км)
        if (!READ.isExerciseInDB(db, CONTEXT.getString(R.string.run_ex_name_base))) {
            Exercise run = new Exercise(null,
                    READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.long_dist_ex_type_base)),
                    CONTEXT.getString(R.string.run_ex_name_base));
            EM.persist(db, run);
        }
        //Отжимания
        if (!READ.isExerciseInDB(db, CONTEXT.getString(R.string.pushups_ex_name_base))) {
            Exercise pushups = new Exercise(null,
                    READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.count_ex_type_base)),
                    CONTEXT.getString(R.string.pushups_ex_name_base));
            EM.persist(db, pushups);
        }
        //Подтягивания
        if (!READ.isExerciseInDB(db, CONTEXT.getString(R.string.podtyagivaniya_ex_name_base))) {
            Exercise podtyagivaniya = new Exercise(null,
                    READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.count_ex_type_base)),
                    CONTEXT.getString(R.string.podtyagivaniya_ex_name_base));
            EM.persist(db, podtyagivaniya);
        }
        //Вес
        if (!READ.isExerciseInDB(db, CONTEXT.getString(R.string.weight_ex_name_base))) {
            Exercise weight = new Exercise(null,
                    READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.weight_ex_type_base)),
                    CONTEXT.getString(R.string.weight_ex_name_base));
            EM.persist(db, weight);
        }
        //Объем бицепса
        if (!READ.isExerciseInDB(db, CONTEXT.getString(R.string.bitceps_size_ex_name_base))) {
            Exercise bitceps_size = new Exercise(null,
                    READ.getExerciseTypeByName(db, CONTEXT.getString(R.string.size_ex_type_base)),
                    CONTEXT.getString(R.string.bitceps_size_ex_name_base));
            EM.persist(db, bitceps_size);
        }
    }

    private void upgradeFrom_1_To_2(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL("alter table TrainingProgTable add column exidintr integer;");
            db.setTransactionSuccessful();
            Log.d(Const.LOG_TAG, "--- add column sucsessful ---");
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

            Log.d(Const.LOG_TAG, "--- upgradeFrom_2_To_3 done ---");
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
            DecimalFormat formatter = new DecimalFormat("#.#");
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
                    String value = MeasureFormatter.toStatValue(formatter.format(power),
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
                        Log.e(Const.LOG_TAG,
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
                        Log.e(Const.LOG_TAG,
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
            Log.i(Const.LOG_TAG, "findPowerExType: " + power_id);
            Log.i(Const.LOG_TAG, "findCycleExType: " + cycle_id);
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

    public int getVersion() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.getVersion();
        } finally {
            db.close();
        }
    }
}
