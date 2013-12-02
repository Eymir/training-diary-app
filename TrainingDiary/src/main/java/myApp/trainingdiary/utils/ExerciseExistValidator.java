package myApp.trainingdiary.utils;

import android.app.Activity;

/**
 * Created by Lenovo on 29.07.13.
 */
public class ExerciseExistValidator extends Validator {
    @Override
    protected boolean trueValidate(Activity activity, String s) {
        return ValidatorUtils.validateExercise(activity, s);
    }

    public ExerciseExistValidator(Validator validator) {
        super(validator);
    }
}
