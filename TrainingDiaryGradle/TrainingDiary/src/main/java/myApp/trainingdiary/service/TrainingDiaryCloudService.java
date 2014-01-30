package myApp.trainingdiary.service;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Lenovo on 16.01.14.
 */
public interface TrainingDiaryCloudService {

    public static final String API_URL = "http://62.109.24.88:8080/TrainingDiaryOnline/api";
    public static final String API_URL_LOCALHOST = "http://192.168.41.131:8080/TrainingDiaryPortal/api";

    @POST("/uploadDb")
    public void uploadCloudBackup(@Body UserData user, Callback<UserData> cb);

    @Headers({
            "Content-type: application/json"
    })
    @GET("/downloadDb")
    public void downloadCloudBackup(@Query("id") String id, @Query("channel") String channel, Callback<UserData> cb);

    public static TrainingDiaryCloudService API = new RestAdapter.Builder()
            .setServer(TrainingDiaryCloudService.API_URL)
            .build()
            .create(TrainingDiaryCloudService.class);
}
