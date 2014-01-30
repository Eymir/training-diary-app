package myApp.trainingdiary.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.telly.groundy.Groundy;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;
import com.telly.groundy.annotations.OnFailure;
import com.telly.groundy.annotations.OnSuccess;
import com.telly.groundy.annotations.Param;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import myApp.trainingdiary.customview.stat.StatItem;
import myApp.trainingdiary.customview.stat.StatisticEnum;
import myApp.trainingdiary.customview.stat.StatisticValueFactory;
import myApp.trainingdiary.utils.Const;

/**
 * Created by Lenovo on 17.01.14.
 */
public class GetCommonStatisticsTask extends GroundyTask {
    private static final String DATABASE = "DATABASE";
    private static final String USERDATA = "USERDATA";
    private Boolean success = false;

    @Override
    protected TaskResult doInBackground() {
        SharedPreferences sp = getContext().getSharedPreferences(Const.CHOSEN_STATISTIC, getContext().MODE_PRIVATE);
        ArrayList<StatItem> list = new ArrayList<StatItem>();
        for (StatisticEnum stat : StatisticEnum.values()) {
            if (sp.getBoolean(stat.name(), false)) {
                list.add(StatisticValueFactory.create(stat));
                Log.d(Const.LOG_TAG, "stat.name() is true:  " + stat.name());
            }
        }
        return succeeded().add("result_list", list);

    }

    public static void start(Context context, GetCommonStatisticsTaskCallback callback) {
        Groundy.create(GetCommonStatisticsTask.class)
                .callback(callback)
                .queueUsing(context);
    }

    public static abstract class GetCommonStatisticsTaskCallback {

        @OnSuccess(GetCommonStatisticsTask.class)
        public void handleSuccess(@Param("result_list") Serializable result) {
            onUploadSuccess((List<StatItem>) result);
        }

        @OnFailure(GetCommonStatisticsTask.class)
        public void handleFailure() {
            onUploadError();
        }

        protected abstract void onUploadSuccess(List<StatItem> result);

        protected abstract void onUploadError();
    }
}
