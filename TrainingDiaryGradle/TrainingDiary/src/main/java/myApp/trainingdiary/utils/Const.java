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
    public static final String STAT_VIEW = "STAT_VIEW_";
    public static final Uri DEFAULT_SOUND_URI = Uri.parse("android.resource://myApp.trainingdiary/" + R.raw.ringringring);

    //billing const
    public static final int RC_REQUEST = 10001;
    public static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgik7dJMRCI2/Fp8tEux8Zg8obzIFLemEScdVcvpykH7vbyXXHiWRLgZKh07Oq1G87aHHiqtLf9Wv8XEkWfQD5ttWrFobV+jzOT+AeaD3Jp5+DKpTJQxywYstrXDIxFedrrcmdH4q8+g38OgNA/NlgeOfnbor4wGFT/qqPW1blHFc3wC75W0WHMQA8VRfx1SRxvwhJFE/G3jRTiEw1D/6ryWG9jsJUl+uqBpPNMZIHcZvwPOkZHGmuw0CKiiWWPMl2vhBY9G8FS1Ka3rQuqJgjyPosBRtoUUVqHYYprIqI1ZXyAFdK7WVMtruDJ1tVE6meW27x3vg82j8H9DzAij2aQIDAQAB";
    public static final String SKU_ADS_DISABLE = "myapp.trainingdiary.ads.disable";
}
