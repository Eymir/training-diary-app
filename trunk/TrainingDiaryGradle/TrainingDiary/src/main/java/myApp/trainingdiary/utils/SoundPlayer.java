package myApp.trainingdiary.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

/**
 * Created by s_malugin on 26.01.14.
 */
public class SoundPlayer {

    static SoundPlayer instance;
    private Context context;
    private MediaPlayer mediaPlayer;

    public SoundPlayer(Context c)  {
        context = c;
    }

    public static SoundPlayer getInstance(Context ctx){
        if (instance == null){
            instance = new SoundPlayer(ctx);
        }
        return instance;
    }

    public void playSound(Uri uri){

        //Будем провверять ури на наличие файла и если его нет заменять на мелдию по умолчанию
        //if(uriFileExist(uri))
            //uri = Uri.parse("android.resource://ru.adhoc.truealarmfree/" + R.raw.nature);

        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

           mediaPlayer.setLooping(true);

        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    public void stopPlaySound(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public boolean uriFileExist(Uri uri){
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if(cursor == null)
            return false;
        if(cursor.moveToFirst()){
            cursor.close();
            return true;
        }
        else {
            cursor.close();
            return false;
        }
    }

}
