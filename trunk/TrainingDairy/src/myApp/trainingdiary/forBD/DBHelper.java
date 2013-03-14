package myApp.trainingdiary.forBD;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import myApp.trainingdiary.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	public final static String LOG_TAG = "test";
	Context context;

	private final static int DB_VERSION = 3; // версия БД

	public DBHelper(Context context) {
		super(context, "TrainingDiaryDB", null, DB_VERSION); // Последняя цифра
																// версия
		// БД!!!!
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTrainingTable(db);
		createExerciseTypeTable(db);
		createExerciseTable(db);
		createExerciseInTrainingTable(db);
		createMesureTable(db);
		createMesureExTypeTable(db);
		createTrainingStatTable(db);

		createInitialTypes(db);
	}

	/**
	 * Подходы
	 */
	private void createTrainingStatTable(SQLiteDatabase db) {

		db.execSQL("create table TrainingStat ("
				+ "id integer primary key autoincrement,"
				+ "training_date datetime," + "exercise_id integer,"
				+ "value text,"
				+ "FOREIGN KEY(exercise_id) REFERENCES Exercise(id)" + ");");

		Log.d(LOG_TAG, "--- onCreate BD TrainingStat ---");
	}

	private void createMesureExTypeTable(SQLiteDatabase db) {
		db.execSQL("create table MesureExType (" + "ex_type_id integer,"
				+ "mesure_id integer," + "position integer,"
				+ "FOREIGN KEY(ex_type_id) REFERENCES ExerciseType(id),"
				+ "FOREIGN KEY(mesure_id) REFERENCES Mesure(id),"
				+ "PRIMARY KEY (ex_type_id, mesure_id)" + ");");

		Log.d(LOG_TAG, "--- onCreate table MesureExType ---");
	}

	/**
	 * Создаём таблицу измерений
	 */
	private void createMesureTable(SQLiteDatabase db) {

		db.execSQL("create table Mesure ("
				+ "id integer primary key autoincrement," + "name text,"
				+ "max integer," + "step float," + "type integer" + ");");

		Log.d(LOG_TAG, "--- onCreate table Mesure ---");
	}

	/**
	 * Создаём таблицу соответивий тренировка - упражнение trainingname -
	 * название тренировки exercise - название упражнения exidintr - номер
	 * упражнения в тренировке
	 */
	private void createExerciseInTrainingTable(SQLiteDatabase db) {

		db.execSQL("create table ExerciseInTraining (" + "training_id integer,"
				+ "exercise_id integer," + "position integer,"
				+ "FOREIGN KEY(training_id) REFERENCES Training(id),"
				+ "FOREIGN KEY(exercise_id) REFERENCES Exercise(id),"
				+ "PRIMARY KEY (training_id, exercise_id)" + ");");

		Log.d(LOG_TAG, "--- onCreate table ExerciseInTraining ---");
	}

	/**
	 * Создаём таблицу упражнений exercise - название упражнения type - тип
	 * упражнения;
	 */
	private void createExerciseTable(SQLiteDatabase db) {

		db.execSQL("create table Exercise ("
				+ "id integer primary key autoincrement," + "name text,"
				+ "type_id integer,"
				+ "FOREIGN KEY(type_id) REFERENCES ExerciseType(id)" + ");");

		Log.d(LOG_TAG, "--- onCreate table Exercise ---");
	}

	/**
	 * Тип тренировки
	 */
	private void createExerciseTypeTable(SQLiteDatabase db) {

		db.execSQL("create table ExerciseType ("
				+ "id integer primary key autoincrement," + "name text,"
				+ "icon_res text" + ");");

		Log.d(LOG_TAG, "--- onCreate table ExerciseType ---");
	}

	/**
	 * создаем таблицу тренировок с полями - просто таблица с названиями
	 * тренировок день1 день2 итд...
	 */
	private void createTrainingTable(SQLiteDatabase db) {
		db.execSQL("create table Training ("
				+ "id integer primary key autoincrement," + "name text,"
				+ "position integer" + ");");
		Log.d(LOG_TAG, "--- onCreate table Training  ---");
	}

	public long insertTraining(SQLiteDatabase db, String name, int position) {
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("position", position);
		long id = db.insert("Training", null, cv);
		return id;
	}

	public long insertExercise(SQLiteDatabase db, String name, long type_id) {
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("type_id", type_id);
		long id = db.insert("Exercise", null, cv);
		return id;
	}

	public long insertMesure(SQLiteDatabase db, String name, int max,
			double step, int type) {
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("max", max);
		cv.put("step", step);
		cv.put("type", type);
		long id = db.insert("Mesure", null, cv);
		return id;
	}

	public long insertExerciseType(SQLiteDatabase db, String name,
			String icon_res) {
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("icon_res", icon_res);
		long id = db.insert("ExerciseType", null, cv);
		return id;
	}

	public long insertMesureExType(SQLiteDatabase db, long exType_id,
			long mesure_id, int position) {
		ContentValues cv = new ContentValues();
		cv.put("ex_type_id", exType_id);
		cv.put("mesure_id", mesure_id);
		cv.put("position", position);
		long id = db.insert("MesureExType", null, cv);
		return id;
	}

	public long insertExerciseInTraining(SQLiteDatabase db, long training_id,
			long exercise_id, int position) {
		ContentValues cv = new ContentValues();
		cv.put("training_id", training_id);
		cv.put("exercise_id", exercise_id);
		cv.put("position", position);
		long id = db.insert("ExerciseInTraining", null, cv);
		return id;
	}

	public long insertTrainingStat(SQLiteDatabase db, long exercise_id,
			long trainingDate, String value) {
		ContentValues cv = new ContentValues();
		cv.put("training_date", trainingDate);
		cv.put("value", value);
		cv.put("exercise_id", exercise_id);
		long id = db.insert("TrainingStat", null, cv);
		return id;
	}

	private void createInitialTypes(SQLiteDatabase db) {
		long bw_m_id = insertMesure(db,
				context.getString(R.string.baseMesure_bar_weight), 500, 0.5, 0);

		long r_m_id = insertMesure(db,
				context.getString(R.string.baseMesure_repeat), 99, 1, 0);

		long power_id = insertExerciseType(db,
				context.getString(R.string.baseExType_power), context
						.getResources().getResourceName(R.drawable.power));

		long cycle_id = insertExerciseType(db,
				context.getString(R.string.baseExType_cycle), context
						.getResources().getResourceName(R.drawable.cycle));

		insertMesureExType(db, power_id, bw_m_id, 0);
		insertMesureExType(db, power_id, r_m_id, 1);

		long d_m_id = insertMesure(db,
				context.getString(R.string.baseMesure_distance), 99, 0.1, 0);

		long t_m_id = insertMesure(db,
				context.getString(R.string.baseMesure_time), 2, 1, 1);

		insertMesureExType(db, cycle_id, d_m_id, 0);
		insertMesureExType(db, cycle_id, t_m_id, 1);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(LOG_TAG, "onUpgrade. oldVer: " + oldVersion + " newVer: "
				+ newVersion);
		if (oldVersion == 1 && newVersion == 2) {
			upgradeFrom_1_To_2(db);
		}
		if (oldVersion == 1 && newVersion == 3) {
			upgradeFrom_1_To_2(db);
			upgradeFrom_2_To_3(db);
		}
		if (oldVersion == 2 && newVersion == 3) {
			upgradeFrom_2_To_3(db);
		}
	}

	private void upgradeFrom_1_To_2(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			db.execSQL("alter table TrainingProgTable add column exidintr integer;");
			db.setTransactionSuccessful();
			Log.d(LOG_TAG, "--- add column sucsessful ---");
		} finally {
			db.endTransaction();
		}
	}

	private void upgradeFrom_2_To_3(SQLiteDatabase db) {
		try {
			db.beginTransaction();

			renameTrainingStatTable(db);

			Cursor trainingTable_cursor = db.query("TrainingTable", null, null,
					null, null, null, null);
			Cursor exerciseTable_cursor = db.query("ExerciseTable", null, null,
					null, null, null, null);
			Cursor trainingProgTable_cursor = db.query("TrainingProgTable",
					null, null, null, null, null, null);
			Cursor trainingStat_cursor = db.query("TrainingStatOld", null,
					null, null, null, null, null);

			createTrainingTable(db);
			createExerciseTypeTable(db);
			createExerciseTable(db);
			createExerciseInTrainingTable(db);
			createMesureTable(db);
			createMesureExTypeTable(db);
			createTrainingStatTable(db);

			createInitialTypes(db);

			transferTrainingTableData(db, trainingTable_cursor);
			transferExerciseTableData(db, exerciseTable_cursor);
			transferTrainingProgTableData(db, trainingProgTable_cursor);
			transferTrainingStatData(db, trainingStat_cursor);

			dropAllTablesVer2(db);

			Log.d(LOG_TAG, "--- upgradeFrom_2_To_3 done ---");
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	private void renameTrainingStatTable(SQLiteDatabase db) {
		db.execSQL("ALTER TABLE TrainingStat RENAME TO TrainingStatOld");
	}

	private void transferTrainingStatData(SQLiteDatabase db, Cursor c) {
		if (c != null) {
			long power_id = findExTypeByName(db,
					context.getString(R.string.baseExType_power));
			long cycle_id = findExTypeByName(db,
					context.getString(R.string.baseExType_cycle));
			DecimalFormat formatter = new java.text.DecimalFormat("#.#");
			if (c.moveToFirst()) {
				do {
					String trainingDate = c.getString(c
							.getColumnIndex("trainingdate"));
					String trainingDay = c.getString(c
							.getColumnIndex("trainingday"));
					String exercise = c.getString(c.getColumnIndex("exercise"));
					String exerciseType = c.getString(c
							.getColumnIndex("exercisetype"));
					double power = c.getDouble(c.getColumnIndex("power"));
					int count = c.getInt(c.getColumnIndex("count"));
					long ex_id = findExerciseByName(db, exercise);
					String value = DbFormatter.toValue(formatter.format(power),
							String.valueOf(count));
					SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
					Date date = null;
					try {
						date = sdf.parse(trainingDate);
					} catch (ParseException e) {
					}

					if (ex_id > 0 && date != null) {
						insertTrainingStat(db, ex_id, date.getTime(), value);
					} else {
						Log.e(LOG_TAG,
								"Cannot insert TrainingStat cause - ex_id: "
										+ ex_id + " date: " + date);
					}
				} while (c.moveToNext());
			}
		}
	}

	private void transferTrainingProgTableData(SQLiteDatabase db, Cursor c) {
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					String ex_name = c.getString(c.getColumnIndex("exercise"));
					String tr_name = c.getString(c
							.getColumnIndex("trainingname"));
					int position = c.getInt(c.getColumnIndex("exidintr"));
					long ex_id = findExerciseByName(db, ex_name);
					long tr_id = findTrainingByName(db, tr_name);
					if (ex_id > 0 && tr_id > 0)
						insertExerciseInTraining(db, tr_id, ex_id, position);
					else {
						Log.e(LOG_TAG,
								"Cannot insert ExerciseInTraining cause - ex_id: "
										+ ex_id + "tr_id: " + tr_id);
					}
				} while (c.moveToNext());
			}
		}
	}

	private long findTrainingByName(SQLiteDatabase db, String tr_name) {
		long id = -1;
		Cursor c = db.rawQuery("select id from Training where name='" + tr_name
				+ "'", null);

		if (c != null) {
			if (c.moveToFirst()) {
				id = c.getInt(c.getColumnIndex("id"));
			}
		}
		return id;
	}

	private long findExerciseByName(SQLiteDatabase db, String ex_name) {
		long id = -1;
		Cursor c = db.rawQuery("select id from Exercise where name='" + ex_name
				+ "'", null);

		if (c != null) {
			if (c.moveToFirst()) {
				id = c.getInt(c.getColumnIndex("id"));
			}
		}
		return id;
	}

	private void transferExerciseTableData(SQLiteDatabase db, Cursor c) {
		if (c != null) {
			long power_id = findExTypeByName(db,
					context.getString(R.string.baseExType_power));
			long cycle_id = findExTypeByName(db,
					context.getString(R.string.baseExType_cycle));
			Log.i(LOG_TAG, "findPowerExType: " + power_id);
			Log.i(LOG_TAG, "findCycleExType: " + cycle_id);
			if (c.moveToFirst()) {
				do {
					String name = c.getString(c.getColumnIndex("exercise"));
					int type = c.getInt(c.getColumnIndex("type"));
					switch (type) {
					case 1:
						insertExercise(db, name, power_id);
						break;
					case 2:
						insertExercise(db, name, cycle_id);
						break;
					}

				} while (c.moveToNext());
			}
		}
	}

	private long findExTypeByName(SQLiteDatabase db, String name) {
		Cursor c = db.rawQuery("select id from ExerciseType where name='"
				+ name + "'", null);
		long id = -1;
		if (c != null) {
			if (c.moveToFirst()) {
				id = c.getInt(c.getColumnIndex("id"));
			}
		}
		return id;
	}

	private void transferTrainingTableData(SQLiteDatabase db, Cursor c) {
		int position = 0;
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					String name = c.getString(c.getColumnIndex("name"));
					insertTraining(db, name, position);
					position++;
				} while (c.moveToNext());
			}
		}
	}

	private void dropAllTablesVer2(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS TrainingTable");
		db.execSQL("DROP TABLE IF EXISTS ExerciseTable");
		db.execSQL("DROP TABLE IF EXISTS TrainingProgTable");
		db.execSQL("DROP TABLE IF EXISTS TrainingStatOld");
	}

	public void changeTrainingPositions(List<Long> list) {

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

	public Cursor getExersices(String strNameTr) {
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

	/**
	 * Получить тренировки в порядке возрастания position Не забыть закрыть
	 * полученный курсор
	 * 
	 * @return курсор с теми же тренировками что и в базе, только id
	 *         возвращается как _id
	 */
	public Cursor getTrainings() {
		SQLiteDatabase db = getWritableDatabase();
		String sqlQuery = "select tr.id as _id, tr.name, tr.position from Training tr order by tr.position asc";
		Cursor c = db.rawQuery(sqlQuery, null);
		return c;
	}

	/**
	 * Получить упражнения в тренировке в порядке position Не забыть закрыть
	 * полученный курсор
	 * 
	 * @return курсор с теми же тренировками что и в базе, только id
	 *         возвращается как _id
	 */
	public Cursor getExercisesInTraining(long tr_id) {
		SQLiteDatabase db = getWritableDatabase();
		String sqlQuery = "select ex_tr.exercise_id as _id, ex.name name, ex_tr.position position, ex_type.icon_res icon_res "
				+ "from ExerciseInTraining ex_tr, Exercise ex, ExerciseType ex_type "
				+ "where ex_tr.training_id = ? and ex_tr.exercise_id = ex.id and ex.type_id = ex_type.id";
		Cursor c = db
				.rawQuery(sqlQuery, new String[] { String.valueOf(tr_id) });
		return c;
	}

	public int getTrainingsCount() {
		SQLiteDatabase db = null;
		try {
			db = getWritableDatabase();
			String sqlQuery = "select count(tr.id) as _count from Training tr";
			Cursor c = db.rawQuery(sqlQuery, null);
			c.moveToFirst();
			int count = c.getInt(c.getColumnIndex("_count"));
			c.close();
			return count;
		} finally {
			if (db != null)
				db.close();
		}
	}

	public String getTrainingNameById(long tr_id) {
		SQLiteDatabase db = null;
		try {
			db = getWritableDatabase();
			String sqlQuery = "select tr.name from Training tr where tr.id = ?";
			Cursor c = db.rawQuery(sqlQuery,
					new String[] { String.valueOf(tr_id) });
			c.moveToFirst();
			String name = c.getString(c.getColumnIndex("name"));
			c.close();
			return name;
		} finally {
			if (db != null)
				db.close();
		}
	}

	public void renameTraining(long cur_tr_id, String name) {
		SQLiteDatabase db = null;
		try {
			db = getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("name", name);
			db.update("Training", cv, "id = ? ",
					new String[] { String.valueOf(cur_tr_id) });
		} finally {
			if (db != null)
				db.close();
		}
	}

	public void deleteTraining(long cur_tr_id) {
		SQLiteDatabase db = null;
		try {
			db = getWritableDatabase();
			db.delete("Training", "id = ? ",
					new String[] { String.valueOf(cur_tr_id) });
		} finally {
			if (db != null)
				db.close();
		}
	}

	public Cursor getExercisesExceptExInTr(long tr_id) {
		SQLiteDatabase db = getWritableDatabase();
		String sqlQuery = "select ex.id as _id, ex.name name, ex_type.icon_res icon_res "
				+ "from ExerciseInTraining ex_tr, Exercise ex, ExerciseType ex_type "
				+ "where ex_tr.training_id <> ? and ex_tr.exercise_id = ex.id and ex.type_id = ex_type.id";
		Cursor c = db
				.rawQuery(sqlQuery, new String[] { String.valueOf(tr_id) });
		return c;
	}

}
