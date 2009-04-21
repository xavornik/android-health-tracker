/*
 * Copyright 2009 Dan Morrill
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.morrildl.health;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * A utility class providing convenient access to database operations. This class is a singleton to minimize
 * the number of open connections floating around, and is thread-safe. Note that since this app is mostly
 * about collection (...mostly), there are more write operations here than read. The read operations
 * are generally intended to be used to fetch time-series data to use for charting purposes.
 */
public class DBUtil {
	private static DBHelper dbHelper;
	private static final String lock = "";

	/**
	 * Android SQLite helper class, for creating & upgrading DBs cleanly.
	 */
	private static class DBHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "net.morrildl.health";
		private static final int DATABASE_VERSION = 1;
		private static final String[] CREATE_TABLES_V0 = new String[] {
			"CREATE TABLE blood_pressure (_id INTEGER PRIMARY KEY, systolic INTEGER, diastolic INTEGER, created INTEGER);",
			/* Note: weight is metric, in units of kg */
			"CREATE TABLE weight (_id INTEGER PRIMARY KEY, weight INTEGER, created INTEGER);",
			"CREATE TABLE calories (_id INTEGER PRIMARY KEY, calories INTEGER, created INTEGER);",
			"CREATE TABLE points (_id INTEGER PRIMARY KEY, points INTEGER, created INTEGER);",
		};

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			for (int i = 0; i < CREATE_TABLES_V0.length; ++i) {
				db.execSQL(CREATE_TABLES_V0[i]);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
			// No-op, since there is currently only 1 version
		}
	}

	/** @see getInstance(Context) */
	private DBUtil(Context context) {
		dbHelper = new DBHelper(context);
	}
	
	private static DBUtil instance = null;

	/** Singleton instance fetcher.*/
	public static DBUtil getInstance(Context context) {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new DBUtil(context);
				}
			}
		}
		return instance;
	}
	
	public String toCSV() {
		return "";
	}
	
	// The theory:  rather than storing straight arrays of integers as time-series data in the DB, we store arrays
	// of *Pairs*, of the form (integer-value, moving-average-value).  Each time the user gives us a new integer
	// value to store, we compute a new moving-average to go with it (based on the new value and prior moving-average).
	// Because moving averages trend up and down in tiny increments, we make them floats rounded to the nearest tenth.
	// This allows us to graph subtle upward/downward trends over longer time ranges.
	
	// Tip: when beginning a new series, let the first moving average be equal to the first value.
	// Then let subsequent values generate subsequent moving averages.
	
	// This function calculates a moving average roughly based on the last 10 values.
	private float getNewMovingAverage(int new_value, float old_moving_average) {
		float delta = (new_value - old_moving_average) / 10;
		delta = Math.round(delta * 10) / 10;
		return (old_moving_average + delta);
	}
	
	public void addBloodPressureRecord(int systolic, int diastolic) {
		addBloodPressureRecord(systolic, diastolic, System.currentTimeMillis());
	}
	
	public void addBloodPressureRecord(int systolic, int diastolic, long created) {
		ContentValues values = new ContentValues();
		values.put("systolic", systolic);
		values.put("diastolic", diastolic);
		values.put("created", created);
		synchronized (dbHelper) {
			@SuppressWarnings("unused")
			long rowId = dbHelper.getWritableDatabase().insert("blood_pressure", null, values);
			// could check rowId for an error, but we can't do anything anyway
		}
	}
	
	public void addMetricWeightRecord(int weight) {
		addWeightRecord(weight, true, System.currentTimeMillis());
	}
	
	public void addMetricWeightRecord(int weight, long created) {
		addWeightRecord(weight, true, created);
	}
	
	public void addWeightRecord(int weight) {
		addWeightRecord(weight, false, System.currentTimeMillis());
	}
	
	public void addWeightRecord(int weight, boolean isMetric, long created) {
		weight = isMetric ? weight : (int)Math.round(weight * 2.20462262); // convert to kg
		// yes we round to int: it's not healthy to track < 1 pound increments, IMO.
		ContentValues values = new ContentValues();
		values.put("weight", weight);
		values.put("created", created);
		synchronized (dbHelper) {
			@SuppressWarnings("unused")
			long rowId = dbHelper.getWritableDatabase().insert("weight", null, values);
			// could check rowId for an error, but we can't do anything anyway
		}
	}
	
	public void addCaloriesRecord(int kCal) {
		addCaloriesRecord(kCal, System.currentTimeMillis());
	}
	
	public void addCaloriesRecord(int kCal, long created) {
		ContentValues values = new ContentValues();
		values.put("calories", kCal);
		values.put("created", created);
		synchronized (dbHelper) {
			@SuppressWarnings("unused")
			long rowId = dbHelper.getWritableDatabase().insert("calories", null, values);
			// could check rowId for an error, but we can't do anything anyway
		}
	}
	
	public void addPointsRecord(int points) {
		addPointsRecord(points, System.currentTimeMillis());
	}
	
	public void addPointsRecord(int points, long created) {
		ContentValues values = new ContentValues();
		values.put("points", points);
		values.put("created", created);
		synchronized (dbHelper) {
			@SuppressWarnings("unused")
			long rowId = dbHelper.getWritableDatabase().insert("points", null, values);
			// could check rowId for an error, but we can't do anything anyway
		}
	}
}
