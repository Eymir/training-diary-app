package myApp.trainingdiary.utils;

import android.net.Uri;

import myApp.trainingdiary.R;

public class Const {

    //����� �����
    public final static String LOG_TAG = "adhoc";

    //������� ��� �������� id ���������� ����� activity
    public final static String TRAINING_ID = "tr_id";

    public final static String EXERCISE_ID = "ex_id";

    public final static String DATE_FIELD = "date";

    //It needs to be a setting
    public final static int THREE_HOURS = 10800000;
    public static final String KEY_WORKOUT_EXPIRING = "workout_expiring";

    public static final String HISTORY_TYPE = "history_type";
    public static final int TRAINING_TYPE = 0;
    public static final int EXERCISE_TYPE = 1;

    public static final String STATISTIC_TYPE = "STATISTIC_TYPE";
    public static final String CHOSEN_STATISTIC = "CHOSEN_STATISTIC";
    public static final String UTF_8 = "UTF-8";
    public static final String ACCOUNT_PREF = "account";
    public static final Uri DEFAULT_SOUND_URI = Uri.parse("android.resource://myApp.trainingdiary/" + R.raw.click3);
}
