package com.sevenflying.greenhouseclient.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.sevenflying.greenhouseclient.domain.Alert;
import com.sevenflying.greenhouseclient.domain.AlertType;
import com.sevenflying.greenhouseclient.domain.MonitoringItem;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.domain.SensorType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
                        + SensorEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                        + SensorEntry.S_NAME + " TEXT NOT NULL, "
                        + SensorEntry.S_PIN_ID + " TEXT NOT NULL, "
                        + SensorEntry.S_TYPE + " TEXT NOT NULL, "
                        + SensorEntry.S_REFRESH + " INTEGER NOT NULL, "
                        + SensorEntry.S_LAST_VALUE + " REAL, "
                        + SensorEntry.S_UPDATED_AT + " TEXT "
                        + " )"
        );
        sqLiteDatabase.execSQL(
                        "CREATE TABLE " + AlertEntry.TABLE_NAME + " ( "
                        + AlertEntry.A_SENSOR_REF + " INTEGER NOT NULL REFERENCES "
                          + SensorEntry.TABLE_NAME + "("+ SensorEntry._ID +") ON DELETE CASCADE,"
                        + AlertEntry.A_TYPE + " TEXT NOT NULL,"
                        + AlertEntry.A_COMPARE_VALUE + " REAL NOT NULL,"
                        + AlertEntry.A_ACTIVE + " TEXT NOT NULL,"
                        + "PRIMARY KEY (" + AlertEntry.A_SENSOR_REF + ", " + AlertEntry.A_TYPE + ")"
                        +" )"
        );
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + MoniItemEntry.TABLE_NAME + " ( "
                + MoniItemEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + MoniItemEntry.M_NAME + " TEXT NOT NULL UNIQUE,"
                + MoniItemEntry.M_PHOTO_PATH + " TEXT,"
                + MoniItemEntry.M_IS_WARNING + " TEXT NOT NULL"
                + " )"
        );
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + MoniItemSensorEntry.TABLE_NAME + " ( "
                + MoniItemSensorEntry.MS_SENSOR_REF + " INTEGER NOT NULL REFERENCES "
                        + SensorEntry.TABLE_NAME + " (" + SensorEntry._ID + ") ON DELETE CASCADE,"
                + MoniItemSensorEntry.MS_MONI_REF + " INTEGER NOT NULL REFERENCES "
                        + MoniItemEntry.TABLE_NAME + " (" +MoniItemEntry._ID +") ON DELETE CASCADE,"
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
        public static final String S_PIN_ID = "pinid";
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

    /** Adds a Sensor to the manager. Discarded if repeated.
     * @param s - sensor to add    */
    public synchronized void addSensor(Sensor s) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SensorEntry.S_NAME, s.getName());
        values.put(SensorEntry.S_PIN_ID, s.getPinId());
        values.put(SensorEntry.S_TYPE, Character.toString(s.getType().getIdentifier()));
        values.put(SensorEntry.S_REFRESH, s.getRefreshRate());
        values.put(SensorEntry.S_LAST_VALUE, s.getValue());
        values.put(SensorEntry.S_UPDATED_AT, s.getUpdatedAt());
        db.insert(SensorEntry.TABLE_NAME, null, values);
    }

    private Sensor handleSensor(Cursor c) {
        Sensor temp = new Sensor();
        temp.setName(c.getString(c.getColumnIndex(SensorEntry.S_NAME)));
        temp.setPinId(c.getString(c.getColumnIndex(SensorEntry.S_PIN_ID)));
        temp.setType(SensorType.getType((c.getString(c.getColumnIndex(SensorEntry.S_TYPE)))
                .charAt(0)));
        temp.setRefreshRate(c.getLong(c.getColumnIndex(SensorEntry.S_REFRESH)));
        temp.setValue(c.getDouble(c.getColumnIndex(SensorEntry.S_LAST_VALUE)));
        temp.setUpdatedAt(c.getString(c.getColumnIndex(SensorEntry.S_UPDATED_AT)));
        return temp;
    }

    /** Returns all the sensors at the manager.
     * @return list of sensors  */
    public  List<Sensor> getSensors() {
        List<Sensor> ret = new ArrayList<Sensor>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Sensors", new String[]{});
        if(c.moveToFirst()) {
            do {
                Sensor temp = handleSensor(c);
                ret.add(temp);
            } while(c.moveToNext());
        }
        c.close();
        return ret;
    }

    /** Returns a sensor given its pinId and type
     * @param pinId - pin id from the Sensor
     * @param type - sensor's type
     * @return Sensor
     */
    public Sensor getSensorBy(String pinId, String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Sensors WHERE pinid = ? AND TYPE = ?",
                new String [] { pinId, type});

        Sensor temp = new Sensor();
        if(c.moveToFirst())
            temp = handleSensor(c);
        c.close();
        return temp;
    }

    public Sensor getSensorBy(String bid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Sensors WHERE _ID = ? ", new String [] {bid});
        Sensor temp = new Sensor();
        if(c.moveToFirst())
            temp = handleSensor(c);
        c.close();
        return temp;
    }

    private int getSensorID(String pinId, String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT _ID FROM Sensors WHERE pinid = ? AND TYPE = ?",
                new String [] { pinId, type});
        int ret = -1;
        if(c.moveToFirst())
           ret = c.getInt(c.getColumnIndex(SensorEntry._ID));
        c.close();
        return ret;
    }

    /** Returns all the sensors at the manager as formatted strings.
     * @return  map holding a sensor with its formatted representation  */
    public  Map<String, Sensor> getFormattedSensors() {
        Map<String, Sensor> ret = new HashMap<String, Sensor>();
        for(Sensor s : getSensors())
            ret.put(s.getName() + " (" + s.getPinId() + ") " + s.getType().toString(), s);
        return ret;
    }

    /** Adds an alert to the Manager. Alerts cannot be repeated.
     * @param a - Alert to add
     */
    public synchronized void addAlert(Alert a) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AlertEntry.A_SENSOR_REF, getSensorID(a.getSensorPinId(),
                String.valueOf(a.getSensorType().getIdentifier())));
        values.put(AlertEntry.A_TYPE, a.getAlertType().getSymbol());
        values.put(AlertEntry.A_COMPARE_VALUE, a.getCompareValue());
        values.put(AlertEntry.A_ACTIVE, a.isActive());
        db.insert(AlertEntry.TABLE_NAME, null, values);
    }

    /** Removes and alert from the Manager.
     * @param a - Alert to remove
     */
    public synchronized  void removeAlert(Alert a) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = AlertEntry.A_SENSOR_REF + " = ? AND " + AlertEntry.A_TYPE + " = ?";
        String[] selectionArgs = {
                Integer.valueOf(getSensorID(a.getSensorPinId(),
                        String.valueOf(a.getSensorType().getIdentifier()))).toString(),
                a.getAlertType().getSymbol()
        };
        db.delete(AlertEntry.TABLE_NAME, selection, selectionArgs);
    }

    /** Checks whether the manager has alerts created concerning a sensor of a certain alert type
     * @param pinId - pin id from the Sensor
     * @param sensorType - sensor type
     * @param alertType - alert type
     * @return true if it has
     */
    public boolean hasAlertsCreatedFrom(String pinId, SensorType sensorType,
                                                     AlertType alertType)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Alerts WHERE sensorid = ? AND type = ? ",
                new String[]{
                        Integer.valueOf(
                                getSensorID(pinId, String.valueOf(sensorType.getIdentifier())))
                                .toString(),
                        alertType.getSymbol()});
        int ret = 0;
        if(c.moveToFirst())
            ret = c.getCount();
        c.close();
       return ret == 0;
    }


    /** Returns a list of the stored alerts.
     * @return list containing the alerts
     */
    public List<Alert> getAlerts() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Alerts", new String [] {});
        List<Alert> ret = new ArrayList<Alert>();
        if(c.moveToFirst()) {
            do {
                try {
                    Alert temp = new Alert();
                    temp.setAlertType(c.getColumnName(c.getColumnIndex(AlertEntry.A_TYPE)));
                    temp.setCompareValue(Double.valueOf
                            (c.getColumnName(c.getColumnIndex(AlertEntry.A_COMPARE_VALUE))));
                    temp.setActive(Boolean.valueOf(c.getColumnName(
                            c.getColumnIndex(AlertEntry.A_ACTIVE))));
                    Sensor s = getSensorBy(c.getColumnName(
                            c.getColumnIndex(AlertEntry.A_SENSOR_REF)));
                    temp.setSensorPinId(s.getPinId());
                    temp.setSensorType(s.getType());
                    ret.add(temp);
                }catch(Exception e) { e.printStackTrace();}
            } while(c.moveToNext());
        }
        c.close();
        return ret;
    }

    private List<Sensor> getSensorsFromMoniItem(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM MoniSensors WHERE monitemid = ?", new String [] {id});
        List<Sensor> ret = new ArrayList<Sensor>();
        if(c.moveToFirst()) {
            do {
                Sensor temp = getSensorBy(c.getColumnName(
                        c.getColumnIndex(MoniItemSensorEntry.MS_SENSOR_REF)));
                ret.add(temp);
            } while(c.moveToNext());
        }
        c.close();
        return ret;
    }

    /** Retrieves all the items.
     * @return list with all the monitoring items.     */
    public List<MonitoringItem> getItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Monitems", new String [] {});
        List<MonitoringItem> ret = new ArrayList<MonitoringItem>();
        if(c.moveToFirst()) {
            do {
                MonitoringItem temp = new MonitoringItem(
                        c.getColumnName(c.getColumnIndex(MoniItemEntry.M_NAME)));
                temp.setPhotoPath(c.getColumnName(c.getColumnIndex(MoniItemEntry.M_PHOTO_PATH)));
                temp.setWarningEnabled(Boolean.valueOf(c.getColumnName(
                        c.getColumnIndex(MoniItemEntry.M_IS_WARNING))));
                String moniId = c.getColumnName(c.getColumnIndex(MoniItemEntry._ID));
                for(Sensor sensor : getSensorsFromMoniItem(moniId))
                    temp.addSensor(sensor);
                ret.add(temp);
            } while(c.moveToNext());
        }
        c.close();
        return ret;
    }

    private String getMoniItemId(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Monitems WHERE name = ?", new String [] {name});
        String ret = null;
        if(c.moveToFirst())
            ret = c.getColumnName(c.getColumnIndex(MoniItemEntry._ID));
        c.close();
        return ret;
    }

    /** Adds a MonitoringItem to the manager.
     * @param item - item to add     */
    public synchronized void addItem(MonitoringItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MoniItemEntry.M_NAME, item.getName());
        values.put(MoniItemEntry.M_PHOTO_PATH, item.getPhotoPath());
        values.put(MoniItemEntry.M_IS_WARNING, item.isWarningEnabled());
        db.insert(MoniItemEntry.TABLE_NAME, null, values);
        for(Sensor s : item.getAttachedSensors())
            addSensorToMoni(item, s);
    }

    /** Adds a sensor to the monitoring items
     * @param item - monitoring item
     * @param sensor - sensor to add
     */
    public synchronized void addSensorToMoni(MonitoringItem item, Sensor sensor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MoniItemSensorEntry.MS_MONI_REF, getMoniItemId(item.getName()));
        values.put(MoniItemSensorEntry.MS_SENSOR_REF, getSensorID(sensor.getPinId(),
                String.valueOf(sensor.getType().getIdentifier())));
        db.insert(MoniItemSensorEntry.TABLE_NAME, null, values);
    }

    /** Deletes a monitoring item
     * @param item - to delete
     */
    public synchronized void deleteItem(MonitoringItem item) {
        deleteItem(item.getName());
    }

    /** Deletes a monitoring item
     * @param name - name of the monitoring item to delete
     */
    public synchronized void deleteItem(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = MoniItemEntry.M_NAME + " = ? ";
        db.delete(AlertEntry.TABLE_NAME, selection, new String[] { name });
    }

}
