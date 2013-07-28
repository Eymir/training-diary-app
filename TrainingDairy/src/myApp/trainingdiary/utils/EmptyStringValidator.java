package myApp.trainingdiary.utils;

import android.app.Activity;

/**
 * Created by Lenovo on 29.07.13.
 */
public class EmptyStringValidator extends Validator {
    @Override
    protected boolean trueValidate(Activity activity, String s) {
        return ValidatorUtils.validateEmpty(activity, s);
    }

    public EmptyStringValidator(Validator validator) {
        super(validator);
    }

    public EmptyStringValidator() {
        super(null);
    }
}
