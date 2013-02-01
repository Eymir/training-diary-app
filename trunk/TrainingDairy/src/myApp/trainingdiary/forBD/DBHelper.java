package myApp.trainingdiary.forBD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	public final static String LOG_TAG = "test";

	// final int DB_VERSION = 1; // ������ ��

	public DBHelper(Context context) {
		super(context, "TrainingDiaryDB", null, 2); // ��������� ����� ������
													// ��!!!!
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// ������� ������� ���������� � ������ - ������ ������� � ����������
		// ���������� ����1 ����2 ���...
		db.execSQL("create table Trainingtable ("
				+ "id integer primary key autoincrement," + "name text,"
				+ "exercise text" + ");");

		Log.d(LOG_TAG, "--- onCreate BD table Trainingtable  ---");

		/*
		 * ������ ������� ���������� exercise - �������� ���������� type - ���
		 * ����������: 1 - �������; 2 - �����������;
		 */
		db.execSQL("create table ExerciseTable ("
				+ "id integer primary key autoincrement," + "exercise text,"
				+ "type text" + ");");

		Log.d(LOG_TAG, "--- onCreate BD ExerciseTable ---");

		/*
		 * ������ ������� ����������� ���������� - ���������� trainingname -
		 * �������� ���������� exercise - �������� ���������� exidintr - �����
		 * ���������� � ����������
		 */

		db.execSQL("create table TrainingProgTable ("
				+ "id integer primary key autoincrement,"
				+ "trainingname text," + "exercise text," + "exidintr integer"
				+ ");");

		Log.d("myLogs", "--- onCreate BD TrainingProgTable ---");

		/*
		 * ����������� ����������: -���� -�������� �������������� ��� -��������
		 * ���������� -��� ��� ���������� (��, �) -���������� ���������� ���
		 * ����� (����, �������)
		 */
		db.execSQL("create table TrainingStat ("
				+ "id integer primary key autoincrement,"
				+ "trainingdate text," + "trainingday text," + "exercise text,"
				+ "exercisetype text," + "power float," + "count integer"
				+ ");");

		Log.d(LOG_TAG, "--- onCreate BD TrainingStat ---");
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
	}

	public void changePositions(String strNameTr,
			List<Long> list) {

		SQLiteDatabase db = getWritableDatabase();
		
		for (int i=0;i<list.size();i++) {

			ContentValues cv = new ContentValues();
			cv.put("exidintr", i);
//			Log.d(LOG_TAG, "new_pos: " + newNumEX + " old_pos: "+old_pos+" ex_name: " + strNameEx
//					+ " trainingname: " + strNameTr);
			db.update("TrainingProgTable", cv,
					"id = ? ", new String[] {
							String.valueOf(list.get(i)) });
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
