package myApp.trainingdiary.forBD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper
{
	final String LOG_TAG = "myLogs";
	//final int DB_VERSION = 1; // ������ ��

    public DBHelper(Context context) 
    {   	
      super(context, "TrainingDiaryDB", null, 2); //��������� ����� ������ ��!!!!
    }

    @Override
    public void onCreate(SQLiteDatabase db) 
    {
   
      // ������� ������� ���������� � ������ -  ������ ������� � ���������� ���������� ����1 ����2 ���...
      db.execSQL("create table Trainingtable (" + "id integer primary key autoincrement," + "name text," + "exercise text" + ");");
      
      Log.d(LOG_TAG, "--- onCreate BD table Trainingtable  ---");
      
      /*������ ������� ����������
       * exercise - �������� ����������
       * type - ��� ����������: 1 - �������; 2 - �����������; 
       */
      db.execSQL("create table ExerciseTable (" + "id integer primary key autoincrement," + "exercise text," +"type text"+ ");");
      
      Log.d(LOG_TAG, "--- onCreate BD ExerciseTable ---");
      
      /*������ ������� ����������� ���������� - ����������
       *  trainingname - �������� ����������
       *  exercise - �������� ����������
       *  exidintr - ����� ���������� � ����������
       */
      
      db.execSQL("create table TrainingProgTable (" + "id integer primary key autoincrement," + "trainingname text," + "exercise text,"+
    		  "exidintr integer"+ ");");
      
      Log.d("myLogs", "--- onCreate BD TrainingProgTable ---");
      
            
      /*����������� ����������:
       * -����
       * -�������� �������������� ���
       * -�������� ����������
       * -��� ��� ���������� (��, �)
       * -���������� ���������� ��� ����� (����, �������)
       */
      db.execSQL("create table TrainingStat (" + "id integer primary key autoincrement," + "trainingdate text,"+"trainingday text,"+ 
    		  			"exercise text,"+"exercisetype text,"+"power float,"+"count integer"+");");
      
      Log.d(LOG_TAG, "--- onCreate BD TrainingStat ---");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
    {
    	   	
    	 if (oldVersion == 1 && newVersion == 2) 
    	 {
    		 
    		 db.beginTransaction(); 
    		 
    		 try
    		 {   		 
    			 db.execSQL("alter table TrainingProgTable add column exidintr integer;");
    			 db.setTransactionSuccessful();    		 
    			 Log.d(LOG_TAG, "--- add column sucsessful ---");    		 
    		 }
    		 
    		 finally
    		 {
    			 db.endTransaction(); 
    		 }
    	}
    }
    
}




