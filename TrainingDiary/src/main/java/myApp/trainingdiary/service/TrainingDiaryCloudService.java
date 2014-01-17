package myApp.trainingdiary.service;

import android.app.Activity;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Lenovo on 16.01.14.
 */
public interface TrainingDiaryCloudService {

    public static final String API_URL = "http://62.109.24.88:8080/TrainingDiaryOnline";

    @POST("/uploadDb")
    public void saveCloudBackup(@Body UserData user, Callback<UserData> cb);

    public static TrainingDiaryCloudService API = new RestAdapter.Builder()
            .setServer(TrainingDiaryCloudService.API_URL)
            .build()
            .create(TrainingDiaryCloudService.class);
}
