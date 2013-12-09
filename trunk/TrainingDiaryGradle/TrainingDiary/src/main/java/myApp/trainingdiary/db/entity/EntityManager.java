package myApp.trainingdiary.db.entity;

import android.database.sqlite.SQLiteDatabase;

import myApp.trainingdiary.db.DBHelper;

/**
 * Created by Boris on 17.06.13.
 */
public class EntityManager {
    private DBHelper dBHelper;

    public EntityManager(DBHelper dBHelper) {
        this.dBHelper = dBHelper;
    }


    public ExerciseType persist(ExerciseType ex_type) {
        SQLiteDatabase db = dBHelper.getWritableDatabase();
        ExerciseType id = persist(db, ex_type);
        db.close();
        return id;
    }

    public ExerciseType persist(SQLiteDatabase db, ExerciseType ex_type) {
        if (ex_type.getId() != null) {
            return ex_type;
        }
        long type_id = dBHelper.WRITE.insertExerciseType(db,
                ex_type.getName(), ex_type.getIcon().getIconResName());
        ex_type.setId(type_id);
        int i = 0;
        for (Measure m : ex_type.getMeasures()) {
            long bw_m_id = dBHelper.WRITE.insertMeasure(db,
                    m.getName(), m.getMax(), m.getStep(), m.getType().code);
            m.setId(bw_m_id);
            dBHelper.WRITE.insertMeasureExType(db, type_id, bw_m_id, i);
            i++;
        }
        return ex_type;

    }

    public Exercise persist(SQLiteDatabase db, Exercise exercise) {
        if (exercise.getId() != null) {
            return exercise;
        }
        ExerciseType type = exercise.getType();
        if (type != null)
            type = persist(db, type);

        long id = dBHelper.WRITE.insertExercise(db, exercise.getName(), type.getId());
        exercise.setId(id);
        return exercise;
    }


}
