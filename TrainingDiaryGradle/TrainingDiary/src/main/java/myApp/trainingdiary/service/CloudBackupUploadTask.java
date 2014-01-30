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
    private Boolean success = false;

    @Override
    protected TaskResult doInBackground() {
        UserData userData = (UserData) getArgs().getSerializable(USERDATA);

        if (success)
            return succeeded();
        else
            return failed();
    }

    public static void start(Context context, BaseCloudBackupUploadTaskCallback<UserData> callback, UserData userData) {
        Bundle b = new Bundle();
        b.putSerializable(USERDATA, userData);
        Groundy.create(CloudBackupUploadTask.class)
                .args(b)
                .callback(callback)
                .queueUsing(context);
    }

    public static abstract class BaseCloudBackupUploadTaskCallback<UserData> implements Callback<UserData> {

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
