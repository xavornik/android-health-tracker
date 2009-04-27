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
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * A utility class providing convenient access to database operations. This
 * class is a singleton to minimize the number of open connections floating
 * around, and is thread-safe. Note that since this app is mostly about data
 * collection (...mostly), there are more write operations here than read. The
 * read operations are generally intended to be used to fetch time-series data
 * to use for charting purposes.
 * 
 * This class (and the database it abstracts) store data in SI units; conversion
 * takes place in this method. Note that weight records are stored in whole
 * units, to not encourage an unhealthy level of fixation on fractional weight
 * changes.
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
				"CREATE TABLE points (_id INTEGER PRIMARY KEY, points INTEGER, created INTEGER);", };

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

	/** Singleton instance fetcher. */
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

	/**
	 * Returns a gigantic CSV representation of the accumulated health data.
	 * 
	 * @param delimeter
	 *            a String to use as the field delimiter; if null, "," is used
	 * @return a String comprising multi-line, quoted CSV, delimited by the
	 *         indicated string
	 */
	public String toCSV(String delimiter) {
		return "";
	}

	/**
	 * Computes a new moving average from an old average and a new value.
	 * Essentially computes the impact of a new member of a series on the series
	 * average, by scaling the new member by the length of the average period.
	 * Rounds to the indicated precision. Do not alter the 'sampleDepth' value
	 * for a moving average across samples; this is meaningless and ruins the
	 * moving average.
	 * 
	 * @param newValue
	 *            the new sample to be included in the moving average
	 * @param oldAverage
	 *            the current moving average (before inclusion of the new
	 *            sample)
	 * @param sampleDepth
	 *            the depth of the average, in number of samples (e.g. if you
	 *            want a moving average of 10 samples, use 10 here)
	 * @param precision
	 *            the precision to use (e.g. 10ths, 100ths) (actual precision
	 *            will be 1/precision; e.g. if you want the average comptued to
	 *            the nearest tenth, use 10 here)
	 * @return the new moving average, including the new sample. Essentially
	 *         oldAverage + (newValue - oldAverage) / depth rounded to the
	 *         indicated precision
	 * @see #getNewMovingAverage(int, float)
	 */
	public float getNewMovingAverage(int newValue, float oldAverage,
			int sampleDepth, int precision) {
		float delta = (newValue - oldAverage) / sampleDepth;
		delta = Math.round(delta * precision) / precision;
		return (oldAverage + delta);
	}

	/**
	 * Computes the new moving average based on the old average and a new sample
	 * to include. Equivalent to getNewMovingAverage(newValue, oldAverage, 10,
	 * 10).
	 * 
	 * @see #getNewMovingAverage(int, float, int, int)
	 * @param newValue
	 *            the new sample to be included in the moving average
	 * @param oldAverage
	 *            the current moving average (before inclusion of the new
	 *            sample)
	 * @return the new moving average, including the new sample.
	 */
	public float getNewMovingAverage(int newValue, float oldAverage) {
		return getNewMovingAverage(newValue, oldAverage, 10, 10);
	}

	/**
	 * Adds a blood pressure record to the database, using current time.
	 * 
	 * @param systolic
	 *            the systolic (high/top) reading
	 * @param diastolic
	 *            the diastolic (low/bottom) reading
	 * @return 'true' if the record was added successfully; 'false' if not
	 */
	public boolean addBloodPressureRecord(int systolic, int diastolic) {
		return addBloodPressureRecord(systolic, diastolic, System
				.currentTimeMillis());
	}

	/**
	 * Adds a blood pressure record to the database.
	 * 
	 * @param systolic
	 *            the systolic (high/top) reading
	 * @param diastolic
	 *            the diastolic (low/bottom) reading
	 * @param created
	 *            the date of the record, in System.currentTimeMillis() format
	 * @return 'true' if the record was added successfully; 'false' if not
	 */
	public boolean addBloodPressureRecord(int systolic, int diastolic,
			long created) {
		ContentValues values = new ContentValues();
		values.put("systolic", systolic);
		values.put("diastolic", diastolic);
		values.put("created", created);
		synchronized (dbHelper) {
			try {
				@SuppressWarnings("unused")
				long rowId = dbHelper.getWritableDatabase().insert(
						"blood_pressure", null, values);
				// could check rowId for an error, but we can't do anything
				// anyway
			} catch (SQLException ex) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Records a weight record, in SI units (kilograms.) Uses the current time.
	 * 
	 * @param kg
	 *            the current sample, in kilograms
	 * @return 'true' if the record was added successfully; 'false' if not
	 */
	public boolean addWeightRecord(int kg) {
		return addWeightRecord(kg, true, System.currentTimeMillis());
	}

	/**
	 * Records a weight record, in SI units (kilograms.)
	 * 
	 * @param kg
	 *            the current sample, in kilograms
	 * @return 'true' if the record was added successfully; 'false' if not
	 */
	public boolean addWeightRecord(int kg, long created) {
		return addWeightRecord(kg, true, created);
	}

	/**
	 * Records a weight record, in Imperial units (pounds.) Uses the current
	 * time.
	 * 
	 * @param pounds
	 *            the current sample, in pounds
	 * @return 'true' if the record was added successfully; 'false' if not
	 */
	public boolean addImperialWeightRecord(int pounds) {
		return addWeightRecord(pounds, false, System.currentTimeMillis());
	}

	/**
	 * Records a weight record, converting from Imperial to SI units, as
	 * necessary. Note that this method rounds to
	 * 
	 * @param weight
	 *            the weight to record
	 * @param isSI
	 *            'true' if 'weight' is already in SI kilograms, or 'false' if
	 *            it's in Imperial pounds
	 * @param created
	 *            the date of the record, in System.currentTimeMillis() format
	 * @return 'true' if the record was added successfully; 'false' if not
	 */
	public boolean addWeightRecord(int weight, boolean isSI, long created) {
		weight = isSI ? weight : (int) Math.round(weight * 2.20462262);
		ContentValues values = new ContentValues();
		values.put("weight", weight);
		values.put("created", created);
		synchronized (dbHelper) {
			try {
				@SuppressWarnings("unused")
				long rowId = dbHelper.getWritableDatabase().insert("weight",
						null, values);
				// could check rowId for an error, but we can't do anything
				// anyway
			} catch (SQLException ex) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Adds a calories record to the database. Note that the input is expected
	 * to be SI kilocalories (i.e. nutritional calories). This probably means
	 * you can just call this method with the number of calories from the food
	 * packagin. Uses the current time.
	 * 
	 * @param kCal
	 *            the calories (in kilocalories -- that is, nutritional
	 *            calories; i.e. 1000 heat calories) to record
	 * @return 'true' if the record was added successfully; 'false' if not
	 */
	public boolean addCaloriesRecord(int kCal) {
		return addCaloriesRecord(kCal, System.currentTimeMillis());
	}

	/**
	 * Adds a calories record to the database. Note that the input is expected
	 * to be SI kilocalories (i.e. nutritional calories). This probably means
	 * you can just call this method with the number of calories from the food
	 * packagin. Uses the current time.
	 * 
	 * @param kCal
	 *            the calories (in kilocalories -- that is, nutritional
	 *            calories; i.e. 1000 heat calories) to record
	 * @param created
	 *            the date of the record, in System.currentTimeMillis() format
	 * @return 'true' if the record was added successfully; 'false' if not
	 */
	public boolean addCaloriesRecord(int kCal, long created) {
		ContentValues values = new ContentValues();
		values.put("calories", kCal);
		values.put("created", created);
		synchronized (dbHelper) {
			try {
				@SuppressWarnings("unused")
				long rowId = dbHelper.getWritableDatabase().insert("calories",
						null, values);
				// could check rowId for an error, but we can't do anything
				// anyway
			} catch (SQLException ex) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Adds a "diet points" record to the database. (A "diet point" is an
	 * alternative food nutrition metric popular with some dieters.) Uses the
	 * current time.
	 * 
	 * @param points
	 *            the points to record
	 * @return 'true' if the record was added successfully; 'false' if not
	 */
	public boolean addPointsRecord(int points) {
		return addPointsRecord(points, System.currentTimeMillis());
	}

	/**
	 * Adds a "diet points" record to the database. (A "diet point" is an
	 * alternative food nutrition metric popular with some dieters.)
	 * 
	 * @param points
	 *            the points to record
	 * @param created
	 *            the date of the record, in System.currentTimeMillis() format
	 * @return 'true' if the record was added successfully; 'false' if not
	 */
	public boolean addPointsRecord(int points, long created) {
		ContentValues values = new ContentValues();
		values.put("points", points);
		values.put("created", created);
		synchronized (dbHelper) {
			try {
				@SuppressWarnings("unused")
				long rowId = dbHelper.getWritableDatabase().insert("points",
						null, values);
				// could check rowId for an error, but we can't do anything
				// anyway
			} catch (SQLException ex) {
				return false;
			}
		}
		return true;
	}
}
