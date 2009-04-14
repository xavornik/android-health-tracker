package net.morrildl.health;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper {
	private static DBHelper dbHelper = null;
	private static final String lock = "";

	private class OpenHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "net.morrildl.health";
		private static final int DATABASE_VERSION = 0;

		public OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase arg0) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		}
	}

	@SuppressWarnings("unused")
	private OpenHelper openHelper;
	
	private DBHelper(Context context) {
		openHelper = new OpenHelper(context);
	}
	
	public static DBHelper getInstance(Context context) {
		if (dbHelper == null) {
			synchronized (lock) {
				if (dbHelper == null) {
					dbHelper = new DBHelper(context);
				}
			}
		}
		return dbHelper;
	}
	
	public String toCSV() {
		return "";
	}
	
	public void addBloodPressureRecord(int systolic, int diastolic) {
	}
	
	public void addWeightRecord(int weight) {
	}
	
	public void addCaloriesRecord(int kCal) {
	}
	
	public void addPointsRecord(int points) {
	}
}
