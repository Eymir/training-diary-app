package myApp.trainingdiary.service;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.telly.groundy.Groundy;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;
import com.telly.groundy.annotations.OnFailure;
import com.telly.groundy.annotations.OnSuccess;

import java.util.Arrays;

import myApp.trainingdiary.utils.Const;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Lenovo on 17.01.14.
 */
public class CloudBackupDownloadTask extends GroundyTask {
    private static final String DATABASE = "DATABASE";
    private static final String USERDATA = "USERDATA";

    @Override
    protected TaskResult doInBackground() {
        UserData userData = (UserData) getArgs().getSerializable(USERDATA);
        Log.d(Const.LOG_TAG, "userData.getRegistrationId: " + userData.getRegistrationId());
        Log.d(Const.LOG_TAG, "userData.getRegistrationChannel: " + userData.getRegistrationChannel());
        if (userData != null) {
            TrainingDiaryCloudService.API.downloadCloudBackup(userData.getRegistrationId(), userData.getRegistrationChannel(), new Callback<UserData>() {
                @Override
                public void success(UserData userData, Response response) {
                    if (userData != null)
                        Log.d(Const.LOG_TAG, userData.toString());
                    else {
                        Log.e(Const.LOG_TAG, "userData is null");
                    }
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    Log.e(Const.LOG_TAG, "retrofitError: " + retrofitError);
                    Log.e(Const.LOG_TAG, "retrofitError.getMessage: " + retrofitError.getLocalizedMessage());
                    Log.e(Const.LOG_TAG, "retrofitError.getReason: " + retrofitError.getResponse().getReason());
                    Log.e(Const.LOG_TAG, "retrofitError.getStatus: " + retrofitError.getResponse().getStatus());
                    Log.e(Const.LOG_TAG, "retrofitError.getBody().mimeType: " + retrofitError.getResponse().getBody().mimeType());
                    Log.e(Const.LOG_TAG, "retrofitError.getBody: " + retrofitError.getResponse().getBody());
                    Log.e(Const.LOG_TAG, "retrofitError.getHeaders: " + retrofitError.getResponse().getHeaders());
                    Log.e(Const.LOG_TAG, "retrofitError.getStackTrace: " + Arrays.asList(retrofitError.getStackTrace()));
                    Log.e(Const.LOG_TAG, "retrofitError.getUrl: " + retrofitError.getUrl());
                }
            });
//
        }
        return succeeded().add(USERDATA, userData);
    }

    public static void start(Context context, BaseCloudBackupDownloadTaskCallback callback, UserData userData) {
        Bundle b = new Bundle();
        b.putSerializable(USERDATA, userData);
        Groundy.create(CloudBackupDownloadTask.class)
                .args(b)
                .callback(callback)
                .queueUsing(context);
    }

    public static abstract class BaseCloudBackupDownloadTaskCallback {

        @OnSuccess(CloudBackupDownloadTask.class)
        public void handleSuccess() {
            onUploadSuccess();
        }

        @OnFailure(CloudBackupDownloadTask.class)
        public void handleFailure() {
            onUploadError();
        }

        protected abstract void onUploadSuccess();

        protected abstract void onUploadError();
    }
}
