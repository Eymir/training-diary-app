package myApp.trainingdiary.utils;

import android.app.Activity;

/**
 * Класс валидации строк
 */
public abstract class Validator {
    private final Validator validator;

    public final boolean validate(Activity activity, String s) {
        if (validator != null)
            return validator.validate(activity, s) && trueValidate(activity, s);
        return trueValidate(activity, s);
    }

    protected abstract boolean trueValidate(Activity activity, String s);

    public Validator(Validator validator) {
        this.validator = validator;
    }

}
