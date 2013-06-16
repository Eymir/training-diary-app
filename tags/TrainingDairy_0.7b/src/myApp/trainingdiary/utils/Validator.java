package myApp.trainingdiary.utils;

import android.content.Context;
import android.widget.Toast;

import myApp.trainingdiary.R;
import myApp.trainingdiary.db.DBHelper;

/**
 * Created by Boris on 04.06.13.
 */
public class Validator {
    public static boolean validateEmpty(Context context, String name) {
        if (name.length() <= 0) {
            Toast.makeText(context,
                    R.string.zero_input_notif, Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    public static boolean validateTraining(Context context, String name) {
        DBHelper dbHelper = DBHelper.getInstance(context);

        if (dbHelper.READ.isTrainingInDB(name)) {
            Toast.makeText(context,
                    R.string.training_exist_notif, Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    public static boolean validateExercise(Context context, String name) {
        DBHelper dbHelper = DBHelper.getInstance(context);

        if (dbHelper.READ.isExerciseInDB(name)) {
            Toast.makeText(context,
                    R.string.exercise_exist_notif, Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

}
