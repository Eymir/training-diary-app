package myApp.trainingdiary.forBD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import myApp.trainingdiary.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	public final static String LOG_TAG = "test";

	// final int DB_VERSION = 1; // версия БД

	public DBHelper(Context context) {
		super(context, "TrainingDiaryDB", null, 3); // Последняя цифра версия
													// БД!!!!
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// создаем таблицу тренировок с полями - просто таблица с названиями
		// тренировок день1 день2 итд...
		db.execSQL("create table Training ("
				+ "id integer primary key autoincrement," + "name text" + ");");

		Log.d(LOG_TAG, "--- onCreate table Training  ---");

		/*
		 * Тип тренировки
		 */
		db.execSQL("create table ExerciseType ("
				+ "id integer primary key autoincrement," + "name text,"
				+ "icon_res integer" + ");");

		Log.d(LOG_TAG, "--- onCreate table ExerciseType ---");

		/*
		 * Создаём таблицу упражнений exercise - название упражнения type - тип
		 * упражнения;
		 */
		db.execSQL("create table Exercise ("
				+ "id integer primary key autoincrement," + "name text,"
				+ "type_id integer,"
				+ "FOREIGN KEY(type_id) REFERENCES ExerciseType(id)" + ");");

		Log.d(LOG_TAG, "--- onCreate table Exercise ---");

		/*
		 * Создаём таблицу соответивий тренировка - упражнение trainingname -
		 * название тренировки exercise - название упражнения exidintr - номер
		 * упражнения в тренировке
		 */

		db.execSQL("create table ExcerciseInTraining ("
				+ "training_id integer," + "exercise_id integer,"
				+ "position integer,"
				+ "FOREIGN KEY(training_id) REFERENCES Training(id),"
				+ "FOREIGN KEY(exercise_id) REFERENCES Exercise(id),"
				+ "PRIMARY KEY (training_id, exercise_id)"+ ");");

		Log.d("myLogs", "--- onCreate table ExcerciseInTraining ---");

		/*
		 * Создаём таблицу измерений
		 */
		db.execSQL("create table Mesure ("
				+ "id integer primary key autoincrement," + "name text,"
				+ "max integer," + "step float" + ");");

		Log.d("myLogs", "--- onCreate table Mesure ---");

		db.execSQL("create table MesureExType (" + "exType_id integer,"
				+ "mesure_id integer," + "position integer,"
				+ "FOREIGN KEY(exType_id) REFERENCES ExerciseType(id),"
				+ "FOREIGN KEY(mesure_id) REFERENCES Mesure(id),"
				+ "PRIMARY KEY (exType_id, mesure_id)" + ");");
		
		Log.d("myLogs", "--- onCreate table MesureExType ---");
		/*
		 * Подходы
		 */
		db.execSQL("create table TrainingStat ("
				+ "id integer primary key autoincrement,"
				+ "trainingdate datetime," 
				+ "exercise_id integer,"
				+ "FOREIGN KEY(exercise_id) REFERENCES Exercise(id)"
				+ ");");

		Log.d(LOG_TAG, "--- onCreate BD TrainingStat ---");
		
		/*
		 * Подходы
		 */
		db.execSQL("create table MesureValue ("
				+ "trainingstat_id integer," 
				+ "mesure_id integer,"
				+ "value float,"
				+ "FOREIGN KEY(trainingstat_id) REFERENCES TrainingStat(id),"
				+ "FOREIGN KEY(mesure_id) REFERENCES Mesure(id),"
				+ "PRIMARY KEY (trainingstat_id, mesure_id)"
				+ ");");

		Log.d(LOG_TAG, "--- onCreate BD MesureValue ---");
		
		createInitialMesures(db);
		createInitialTypes(db);
		
	}

	private void createInitialMesures(SQLiteDatabase db) {

	}

	private void createInitialTypes(SQLiteDatabase db) {
		ContentValues cv_bar_weight = new ContentValues();	 
		cv_bar_weight.put("name", R.string.baseMesure_bar_weight);
		cv_bar_weight.put("max", 500);
		cv_bar_weight.put("step", 0.5);
		long bw_m_id = db.insert("Mesure", null, cv_bar_weight);
		
		ContentValues cv_repeat = new ContentValues();	 
		cv_repeat.put("name", R.string.baseMesure_repeat);
		cv_repeat.put("max", 99);
		cv_repeat.put("step", 1);
		long r_m_id = db.insert("Mesure", null, cv_repeat);
		
		ContentValues cv_power = new ContentValues();	 
		cv_power.put("name", R.string.baseExType_power);
		cv_power.put("icon_res", R.drawable.power);
		long power_id = db.insert("ExerciseType", null, cv_power);
		
		ContentValues cv_cycle = new ContentValues();	 
		cv_cycle.put("name", R.string.baseExType_cycle);
		cv_cycle.put("icon_res", R.drawable.cycle);
		db.insert("ExerciseType", null, cv_cycle);
		
		ContentValues cv_mesureExType1 = new ContentValues();	 
		cv_mesureExType1.put("exType_id", power_id);
		cv_mesureExType1.put("mesure_id", bw_m_id);
		cv_mesureExType1.put("position", 0);
		db.insert("MesureExType", null, cv_mesureExType1);
		
		ContentValues cv_mesureExType2 = new ContentValues();	 
		cv_mesureExType2.put("exType_id", power_id);
		cv_mesureExType2.put("mesure_id", r_m_id);
		cv_mesureExType2.put("position", 1);
		db.insert("MesureExType", null, cv_mesureExType2);
		//TODO: Написать функции для каждой таблицы на insert
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 1 && newVersion == 2) {
			db.beginTransaction();
			try {
				db.execSQL("alter table TrainingProgTable add column exidintr integer;");
				db.setTransactionSuccessful();
				Log.d(LOG_TAG, "--- add column sucsessful ---");
			}
			finally {
				db.endTransaction();
			}
		}
		if (oldVersion == 2 && newVersion == 3) {
			upgradeFrom_2_To_3(db);
		}
	}

	private void upgradeFrom_2_To_3(SQLiteDatabase db) {
		try {
			db.beginTransaction();
			db.execSQL("alter table TrainingProgTable add column exidintr integer;");
			db.setTransactionSuccessful();
			Log.d(LOG_TAG, "--- add column sucsessful ---");
		}

		finally {
			db.endTransaction();
		}
	}

	public void changePositions(String strNameTr, List<Long> list) {

		SQLiteDatabase db = getWritableDatabase();

		for (int i = 0; i < list.size(); i++) {

			ContentValues cv = new ContentValues();
			cv.put("exidintr", i);
			// Log.d(LOG_TAG, "new_pos: " + newNumEX +
			// " old_pos: "+old_pos+" ex_name: " + strNameEx
			// + " trainingname: " + strNameTr);
			db.update("TrainingProgTable", cv, "id = ? ",
					new String[] { String.valueOf(list.get(i)) });
		}
		db.close();
	}

	public Cursor getExcersices(String strNameTr) {
		SQLiteDatabase db = getWritableDatabase();
		String sqlQuery = "select " + "prog.id as _id, "
				+ "prog.exercise as ex_name, " + "prog.trainingname as Tr, "
				+ "prog.exidintr as id," + "Ex.type as type "
				+ "from TrainingProgTable as prog "
				+ "inner join ExerciseTable as Ex "
				+ "on prog.exercise=Ex.exercise " + "where Tr = ? "
				+ "ORDER BY id";

		String[] args = { strNameTr };
		Cursor c = db.rawQuery(sqlQuery, args);
		return c;
	}

}
