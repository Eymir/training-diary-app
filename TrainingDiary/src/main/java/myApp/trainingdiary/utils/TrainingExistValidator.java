package myApp.trainingdiary.utils;

import android.app.Activity;

/**
 * Created by Lenovo on 29.07.13.
 */
public class TrainingExistValidator extends Validator {

    public TrainingExistValidator(Validator validator) {
        super(validator);
    }

    @Override
    protected boolean trueValidate(Activity activity, String s) {
        return ValidatorUtils.validateTraining(activity,s);
    }

    public TrainingExistValidator() {
        super(null);
    }
}
