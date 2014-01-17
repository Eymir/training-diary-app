package myApp.trainingdiary.service;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.telly.groundy.Groundy;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;
import com.telly.groundy.annotations.OnFailure;
import com.telly.groundy.annotations.OnSuccess;

import myApp.trainingdiary.utils.Const;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Lenovo on 17.01.14.
 */
public class CloudBackupUploadTask extends GroundyTask {
    private static final String DATABASE = "DATABASE";
    private static final String USERDATA = "USERDATA";

    @Override
    protected TaskResult doInBackground() {
        UserData userData = (UserData) getArgs().getSerializable(USERDATA);
        TrainingDiaryCloudService.API.saveCloudBackup(userData, new Callback<UserData>() {
            @Override
            public void success(UserData userData, Response response) {
                Log.i(Const.LOG_TAG, "Callback.success");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(Const.LOG_TAG, "Callback.failure: " + retrofitError);
                Log.i(Const.LOG_TAG, "Callback.failure.getMessage(): " + retrofitError.getMessage());
                Log.i(Const.LOG_TAG, "Callback.failure.getResponse().getReason(): " + retrofitError.getResponse().getReason());
            }
        });
        return succeeded().add("the_result", "some result");
    }

    public static void start(Context context, BaseCloudBackupUploadTaskCallback callback, UserData userData) {
        Bundle b = new Bundle();
        b.putSerializable(USERDATA, userData);
        Groundy.create(CloudBackupUploadTask.class)
                .args(b)
                .callback(callback)
                .queueUsing(context);
    }

    public static abstract class BaseCloudBackupUploadTaskCallback {

        @OnSuccess(CloudBackupUploadTask.class)
        public void handleSuccess() {
            onUploadSuccess();
        }

        @OnFailure(CloudBackupUploadTask.class)
        public void handleFailure() {
            onUploadError();
        }

        protected abstract void onUploadSuccess();

        protected abstract void onUploadError();
    }
}
