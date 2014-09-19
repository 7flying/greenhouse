package com.sevenflying.greenhouseclient.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


/** Application's database manager.
 * Created by 7flying on 19/09/2014.
 */
public class DBManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "devGreenhouse.db";

    public  DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                        "CREATE TABLE " + SensorEntry.TABLE_NAME + " ( "
                        + SensorEntry._ID + " INTEGER NOT NULL PRIMARY KEY, "
                        + SensorEntry.S_NAME + " TEXT NOT NULL, "
                        + SensorEntry.S_TYPE + " TEXT NOT NULL, "
                        + SensorEntry.S_REFRESH + " INTEGER NOT NULL, "
                        + SensorEntry.S_LAST_VALUE + " REAL, "
                        + SensorEntry.S_UPDATED_AT + " TEXT "
                        + " )"
        );
        sqLiteDatabase.execSQL(
                        "CREATE TABLE " + AlertEntry.TABLE_NAME + " ( "
                        + AlertEntry._ID + " INTEGER NOT NULL PRIMARY KEY, "
                        + AlertEntry.A_SENSOR_REF + " INTEGER NOT NULL REFERENCES "
                          + SensorEntry.TABLE_NAME + "("+ SensorEntry._ID +") ON DELETE CASCADE,"
                        + AlertEntry.A_TYPE + " TEXT NOT NULL,"
                        + AlertEntry.A_COMPARE_VALUE + " REAL NOT NULL,"
                        + AlertEntry.A_ACTIVE + " TEXT NOT NULL"
                        +" )"
        );
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + MoniItemEntry.TABLE_NAME + " ( "
                + MoniItemEntry._ID + " INTEGER NOT NULL PRIMARY KEY, "
                + MoniItemEntry.M_NAME + " TEXT NOT NULL,"
                + MoniItemEntry.M_PHOTO_PATH + " TEXT,"
                + MoniItemEntry.M_IS_WARNING + " TEXT NOT NULL"
                + " )"
        );
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + MoniItemSensorEntry.TABLE_NAME + " ( "
                + MoniItemSensorEntry.MS_SENSOR_REF + " INTEGER NOT NULL REFERENCES "
                        + SensorEntry.TABLE_NAME + " (" + SensorEntry._ID + "),"
                + MoniItemSensorEntry.MS_MONI_REF + " INTEGER NOT NULL REFERENCES "
                        + MoniItemEntry.TABLE_NAME + " (" + MoniItemEntry._ID + "),"
                + "PRIMARY KEY (" + MoniItemSensorEntry.MS_SENSOR_REF + ", "
                        + MoniItemSensorEntry.MS_MONI_REF + ")"
                + ") "
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        String statement = "DELETE TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(statement + MoniItemSensorEntry.TABLE_NAME);
        sqLiteDatabase.execSQL(statement + MoniItemEntry.TABLE_NAME);
        sqLiteDatabase.execSQL(statement + AlertEntry.TABLE_NAME);
        sqLiteDatabase.execSQL(statement + SensorEntry.TABLE_NAME);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    /** Class representing a Sensor on the DB  **/
    public static abstract class SensorEntry implements BaseColumns {

        public static final String TABLE_NAME = "Sensors";
        public static final String S_NAME = "name";
        public static final String S_TYPE = "type";
        public static final String S_REFRESH = "refreshrate";
        public static final String S_LAST_VALUE = "lastvalue";
        public static final String S_UPDATED_AT = "updatedat";
    }

    /** Class representing an Alert on the DB **/
    public  static abstract class AlertEntry implements BaseColumns {

        public static final String TABLE_NAME = "Alerts";
        public static final String A_TYPE = "type";
        public static final String A_COMPARE_VALUE = "comparevalue";
        public static final String A_ACTIVE = "active";
        public static final String A_SENSOR_REF = "sensorid";
    }

    /** Class representing a MonitoringItem on the DB **/
    public static abstract class MoniItemEntry implements BaseColumns {

        public static final String TABLE_NAME = "Monitems";
        public static final String M_NAME = "name";
        public static final String M_PHOTO_PATH = "photopath";
        public static final String M_IS_WARNING = "iswarning";
    }

    /** Class for the relationship of Sensor <---> MonitoringItem**/
    public static abstract class MoniItemSensorEntry implements BaseColumns {

        public static final String TABLE_NAME = "MoniSensors";
        public static final String MS_SENSOR_REF = "sensorid";
        public static final String MS_MONI_REF = "monitemid";
    }
}
