package com.sevenflying.greenhouseclient.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.sevenflying.greenhouseclient.app.utils.GreenhouseUtils;
import com.sevenflying.greenhouseclient.domain.Actuator;
import com.sevenflying.greenhouseclient.domain.Alert;
import com.sevenflying.greenhouseclient.domain.AlertType;
import com.sevenflying.greenhouseclient.domain.MonitoringItem;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.domain.SensorType;
import com.sevenflying.greenhouseclient.net.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Application's database manager.
 * Created by 7flying on 19/09/2014.
 */
public class DBManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = Constants.DB_NAME;

    public GreenhouseUtils greenhouseUtils;

    public  DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.greenhouseUtils = new GreenhouseUtils(context);
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
                        "CREATE TABLE " + ActuatorEntry.TABLE_NAME + " ( "
                        + ActuatorEntry.AC_NAME + " TEXT NOT NULL, "
                        + ActuatorEntry.AC_PIN  + " TEXT NOT NULL PRIMARY KEY, "
                        + ActuatorEntry.AC_SENSOR_REF + " INTEGER REFERENCES "
                            + SensorEntry.TABLE_NAME + "("+ SensorEntry._ID +") ON DELETE CASCADE, "
                        + ActuatorEntry.AC_COMPARE_TYPE + " TEXT, "
                        + ActuatorEntry.AC_COMPARE_VALUE + " REAL"
                        + " )"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX " + SensorIndex.INDEX_NAME + " ON "
                        + SensorEntry.TABLE_NAME + "(" + SensorEntry.S_PIN_ID + ", "
                        + SensorEntry.S_TYPE  +" )");

        sqLiteDatabase.execSQL(
                        "CREATE TABLE " + AlertEntry.TABLE_NAME + " ( "
                        + AlertEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                        + AlertEntry.A_SENSOR_REF + " INTEGER NOT NULL REFERENCES "
                          + SensorEntry.TABLE_NAME + "(" + SensorEntry._ID +") ON DELETE CASCADE,"
                        + AlertEntry.A_TYPE + " TEXT NOT NULL,"
                        + AlertEntry.A_COMPARE_VALUE + " REAL NOT NULL,"
                        + AlertEntry.A_ACTIVE + " TEXT NOT NULL"
                      //  + "PRIMARY KEY (" + AlertEntry.A_SENSOR_REF + ", " + AlertEntry.A_TYPE + ")"
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
                        + MoniItemEntry.TABLE_NAME + " (" + MoniItemEntry._ID +") ON DELETE CASCADE,"
                + "PRIMARY KEY (" + MoniItemSensorEntry.MS_SENSOR_REF + ", "
                        + MoniItemSensorEntry.MS_MONI_REF + ")"
                + ") "
        );
        sqLiteDatabase.execSQL(
		         "CREATE TABLE " + SensorHistory.TABLE_NAME + " ( "
                + SensorHistory._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + SensorHistory.SH_SENSOR + " INTEGER NOT NULL REFERENCES "
                        + SensorEntry.TABLE_NAME + " (" + SensorEntry._ID + ") ON DELETE CASCADE, "
                + SensorHistory.SH_DATE + " TEXT NOT NULL,"
                + SensorHistory.SH_TIME + " TEXT NOT NULL,"
                + SensorHistory.SH_VALUE + " REAL NOT NULL"
                + ")"
         );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        String indexStatement = "DELETE INDEX IF EXISTS ";
        sqLiteDatabase.execSQL(indexStatement + SensorIndex.INDEX_NAME);
        String statement = "DELETE TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(statement + MoniItemSensorEntry.TABLE_NAME);
        sqLiteDatabase.execSQL(statement + MoniItemEntry.TABLE_NAME);
        sqLiteDatabase.execSQL(statement + AlertEntry.TABLE_NAME);
        sqLiteDatabase.execSQL(statement + ActuatorEntry.TABLE_NAME);
        sqLiteDatabase.execSQL(statement + SensorEntry.TABLE_NAME);
        sqLiteDatabase.execSQL(statement + SensorHistory.TABLE_NAME);
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

    /** Index on sensors' unique (S_PIN_ID, S_TYPE) */
    public static abstract class SensorIndex {

        public static  final String INDEX_NAME = "indxSensor";
    }

    public static abstract class SensorHistory implements BaseColumns {
        public static final String TABLE_NAME = "SensorHistory";
        public static final String SH_VALUE = "value";
        public static final String SH_SENSOR = "pinid";
        public static final String SH_TIME = "time";
        public static final String SH_DATE = "date";
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

    /** Class for the relationship of Sensor <---> MonitoringItem **/
    public static abstract class MoniItemSensorEntry implements BaseColumns {

        public static final String TABLE_NAME = "MoniSensors";
        public static final String MS_SENSOR_REF = "sensorid";
        public static final String MS_MONI_REF = "monitemid";
    }

    /** Class representing an Actuator on the DB */
    public static abstract class ActuatorEntry implements BaseColumns {
        public static final String TABLE_NAME = "Actuators";
        public static final String AC_NAME = "name";
        public static final String AC_PIN = "pinid";
        public static final String AC_SENSOR_REF = "sensorid";
        public static final String AC_COMPARE_TYPE = "comparetype";
        public static final String AC_COMPARE_VALUE = "comparevalue";
    }

    // -- Sensors ---

    /** Adds a Sensor to the manager. Discarded if repeated.
     * @param s - sensor to add    */
    public synchronized void addSensor(Sensor s) {
        Log.v(Constants.DEBUGTAG, " $ addSensor " + s.toString());
        if(getSensorID(s.getPinId(), String.valueOf(s.getType().getIdentifier())) == -1) {
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
    }

    /** Deletes a sensor from the DB.
     * @param s - sensor to delete
     */
    public synchronized void deleteSensor(Sensor s) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(SensorEntry.TABLE_NAME, SensorEntry.S_PIN_ID +
                " = ? AND " + SensorEntry.S_TYPE + " = ?", new String [] { s.getPinId(),
                Character.toString(s.getType().getIdentifier()) });
        Log.d(Constants.DEBUGTAG, " $ deteteSensor  arg:" + s.toString());
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

    /** Updates the last value of a sensor
     * @param s - sensor to update
     * @param lastValue - last known value
     * @param updatedAt - when it was updated
     */
    public synchronized void updateSensor(Sensor s, double lastValue, String updatedAt) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SensorEntry.S_LAST_VALUE, lastValue);
        values.put(SensorEntry.S_UPDATED_AT, updatedAt);
        db.update(SensorEntry.TABLE_NAME, values, SensorEntry._ID + " = ?",
                new String[]{Integer.toString(getSensorID(s.getPinId(),
                        String.valueOf(s.getType().getIdentifier())))});
    }

    /** Given a sensor edits its main data
     * @param s - sensor to edit
     */
    public void editSensor(Sensor s) {
        String id = Integer.toString(getSensorID(s.getPinId(),
                    String.valueOf(s.getType().getIdentifier())));
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SensorEntry.S_NAME, s.getName());
        values.put(SensorEntry.S_REFRESH, s.getRefreshRate());
        db.update(SensorEntry.TABLE_NAME, values, SensorEntry._ID + " = ?", new String[]{id});
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
        Log.d(Constants.DEBUGTAG, " $ getSensors size:" + ret.size());
        return ret;
    }

    private Sensor getSensorBy(String bid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + SensorEntry.TABLE_NAME
                + " WHERE " + SensorEntry._ID + " = ? ", new String[]{bid});
        Sensor temp = new Sensor();
        if(c.moveToFirst())
            temp = handleSensor(c);
        c.close();
        Log.d(Constants.DEBUGTAG, " $ getSensorBy(String bid)  arg:" + bid);
        return temp;
    }

    private int getSensorID(String pinId, String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT _ID FROM " + SensorEntry.TABLE_NAME
                             + " WHERE " + SensorEntry.S_PIN_ID + " = ? AND "
                             +  SensorEntry.S_TYPE + " = ?",  new String [] { pinId, type});
        int ret = -1;
        if (c.moveToFirst())
           ret = c.getInt(c.getColumnIndex(SensorEntry._ID));
        c.close();
        return ret;
    }

    /** Returns all the sensors at the manager as formatted strings.
     * @return  map holding a sensor with its formatted representation  */
    public  Map<String, Sensor> getFormattedSensors() {
        Map<String, Sensor> ret = new HashMap<String, Sensor>();
        List<Sensor> sensors = getSensors();
        Log.d(Constants.DEBUGTAG, " $ getFormattedSensors: num: " + sensors.size());
        for (Sensor s : sensors) {
            Log.d(Constants.DEBUGTAG, " $ getFormattedSensors: sensor:" + s.toString());
            ret.put(greenhouseUtils.getFormattedSensor(s), s);
        }
        Log.d(Constants.DEBUGTAG, " $ getFormattedSensors map: " + ret.toString());
        return ret;
    }

    // --- Alerts ---

    private int getAlertID(int sensorId, String alertType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT _ID FROM " + AlertEntry.TABLE_NAME
                             + " WHERE " + AlertEntry.A_SENSOR_REF + " = ? AND "
                             + AlertEntry.A_TYPE + " = ?", new String[]{ Integer.toString(sensorId),
                                                                         alertType});
        int ret = -1;
        if (c.moveToFirst())
            ret = c.getInt(c.getColumnIndex(AlertEntry._ID));
        c.close();
        Log.d(Constants.DEBUGTAG, " $ alertId: " + ret);
        return ret;
    }

    /** Adds an alert to the Manager. Alerts cannot be repeated.
     * @param a - Alert to add
     */
    public synchronized void addAlert(Alert a) {
        Log.d(Constants.DEBUGTAG, " $ addAlert " + a.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AlertEntry.A_SENSOR_REF, getSensorID(a.getSensorPinId(),
                String.valueOf(a.getSensorType().getIdentifier())));
        values.put(AlertEntry.A_TYPE, a.getAlertType().toString());
        values.put(AlertEntry.A_COMPARE_VALUE, a.getCompareValue());
        values.put(AlertEntry.A_ACTIVE, a.isOn());
        db.insert(AlertEntry.TABLE_NAME, null, values);
        Log.d(Constants.DEBUGTAG, " $ After addAlert size:" + getAlerts().size());
    }

    /** Removes and alert from the Manager.
     * @param a - Alert to remove
     */
    public synchronized void removeAlert(Alert a) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = AlertEntry.A_SENSOR_REF + " = ? AND " + AlertEntry.A_TYPE + " = ?";
        String[] selectionArgs = {
                Integer.toString(getSensorID(a.getSensorPinId(),
                        String.valueOf(a.getSensorType().getIdentifier()))),
                a.getAlertType().toString()
        };
        db.delete(AlertEntry.TABLE_NAME, selection, selectionArgs);
        Log.d(Constants.DEBUGTAG, " $ deleteAlert  arg:" + a.toString());
    }

    /** Checks whether the manager has alerts created concerning a sensor of a certain alert type
     * @param pinId - pin id from the Sensor
     * @param sensorType - sensor type
     * @param alertType - alert type
     * @return true if it has
     */
    public boolean hasAlertsCreatedFrom(String pinId, SensorType sensorType, AlertType alertType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Alerts WHERE sensorid = ? AND type = ? ",
                new String[]{
                        Integer.valueOf(
                                getSensorID(pinId, String.valueOf(sensorType.getIdentifier())))
                                .toString(),
                        alertType.toString()});
        int ret = 0;
        if(c.moveToFirst())
            ret = c.getCount();
        c.close();
        Log.d(Constants.DEBUGTAG, " $ hasAlertsCreatedFrom  ret:" + ret);
       return ret > 0;
    }

    /** Returns a list of the stored alerts.
     * @return list containing the alerts
     */
    public List<Alert> getAlerts() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + AlertEntry.TABLE_NAME, new String [] {});
        List<Alert> ret = new ArrayList<Alert>();
        if(c.moveToFirst()) {
            do {
                try {
                    Alert temp = new Alert();
                    temp.setAlertType(AlertType.valueOf(c.getString(c.getColumnIndex(AlertEntry.A_TYPE))));
                    temp.setCompareValue(c.getDouble(c.getColumnIndex(AlertEntry.A_COMPARE_VALUE)));
                    temp.setOn(Boolean.valueOf(c.getString(c.getColumnIndex(AlertEntry.A_ACTIVE))));
                    Sensor s = getSensorBy(Integer.toString(c.getInt(c.getColumnIndex(
                            AlertEntry.A_SENSOR_REF))));
                    temp.setSensorName(s.getName());
                    temp.setSensorPinId(s.getPinId());
                    temp.setSensorType(s.getType());
                    Log.d(Constants.DEBUGTAG, " $ getAlerts, sensor:" + s.toString());
                    ret.add(temp);
                }catch(Exception e) { e.printStackTrace();}
            } while(c.moveToNext());
        }
        Log.d(Constants.DEBUGTAG, " $ getAlerts  size:" + ret.size());
        if(ret.size() > 0)
            Log.d(Constants.DEBUGTAG, " $[!!] Manager::getAlerts: " + ret.get(0).getSensorPinId()
                    + ": " + ret.get(0).isOn());
        c.close();
        return ret;
    }

    /** Sets on or off a certain alert.
     * @param alert - alert to modify
     * @param enabled - status to put
     */
    public synchronized void setEnabled(Alert alert, boolean enabled) {
        Log.d(Constants.DEBUGTAG, " $ setEnabled " + alert.toString() + " : to " + enabled );
        int sensorID = getSensorID(alert.getSensorPinId(), String.valueOf(
                alert.getSensorType().getIdentifier()));
        int alertID = getAlertID(sensorID, alert.getAlertType().toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AlertEntry.A_ACTIVE, Boolean.toString(enabled));
        int result = db.update(AlertEntry.TABLE_NAME, values, AlertEntry._ID + " = ?",
                new String[]{Integer.toString(alertID)});
        Log.d(Constants.DEBUGTAG, " $ setEnabled db result: " + result );
        try {
            boolean after = isEnabled(alert);
            Log.d(Constants.DEBUGTAG, " $ setEnabled read has:      " + after);
            if (enabled == after)
                Log.d(Constants.DEBUGTAG, " $ setEnabled SUCCESSFUL");
            else
                Log.e(Constants.DEBUGTAG, " $ setEnabled ERROR");
        } catch (Exception e) {
            Log.e(Constants.DEBUGTAG, " $ setEnabled EXCEPTION");
            e.printStackTrace();
        }
    }

    /** Updates the given alert's compare value
     * @param alert - alert to update with the new compare value
     */
    public void updateAlertCompareValue(Alert alert) {
        int sensorID = getSensorID(alert.getSensorPinId(), String.valueOf(
                alert.getSensorType().getIdentifier()));
        int alertID = getAlertID(sensorID, alert.getAlertType().toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AlertEntry.A_COMPARE_VALUE, alert.getCompareValue());
        int result = db.update(AlertEntry.TABLE_NAME, values, AlertEntry._ID + " = ?",
                new String[]{Integer.toString(alertID)});
        if (result > 0)
            Log.d(Constants.DEBUGTAG, " $ updated: Alert CompareValue" );
        else
            Log.e(Constants.DEBUGTAG, " $ NOT updated: Alert CompareValue" );
    }

    /** Checks if a certain alert is enabled
     * @param alert - alert to check
     * @return true/false
     */
    public boolean isEnabled(Alert alert) throws Exception{
        boolean ret = false;
        Log.d(Constants.DEBUGTAG, " $ request isEnabled,  alert:" + alert.toString());
        int sensorID = getSensorID(alert.getSensorPinId(), String.valueOf(
                alert.getSensorType().getIdentifier()));
        int alertID = getAlertID(sensorID, alert.getAlertType().toString());
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + AlertEntry.TABLE_NAME + " WHERE "
                             + AlertEntry._ID + " = ?", new String[]{Integer.toString(alertID)});
        if (c.moveToFirst()) {
            Alert temp = new Alert();
            temp.setAlertType(AlertType.valueOf(c.getString(c.getColumnIndex(AlertEntry.A_TYPE))));
            temp.setCompareValue(c.getDouble(c.getColumnIndex(AlertEntry.A_COMPARE_VALUE)));
            temp.setOn(Boolean.valueOf(c.getString(c.getColumnIndex(AlertEntry.A_ACTIVE))));
            Log.d(Constants.DEBUGTAG, " $ isEnabled, ret alert (sensor omitted): " + temp.toString());
            ret = temp.isOn();
            Log.d(Constants.DEBUGTAG, " $ isEnabled " + temp.toString() + "? :" + ret );
            c.close();
        } else{
            c.close();
            Log.e(Constants.DEBUGTAG, " $ isEnabled: Couldn't get the alert's status");
            throw new Exception("Couldn't get the alert's status");
        }
        return ret;
    }

    // --- Monitoring Items ---

    /** Retrieves all the items.
     * @return list with all the monitoring items.     */
    public List<MonitoringItem> getItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + MoniItemEntry.TABLE_NAME, new String [] {});
        List<MonitoringItem> ret = new ArrayList<MonitoringItem>();
        if(c.moveToFirst()) {
            do {
                MonitoringItem temp = new MonitoringItem(c.getString
                        (c.getColumnIndex(MoniItemEntry.M_NAME)));
                temp.setId(c.getInt(c.getColumnIndex(MoniItemEntry._ID)));
                temp.setPhotoPath(c.getString(c.getColumnIndex(MoniItemEntry.M_PHOTO_PATH)));
                temp.setWarningEnabled(Boolean.valueOf(c.getString(
                        c.getColumnIndex(MoniItemEntry.M_IS_WARNING))));
                for (Sensor sensor : getSensorsFromMoniItem(Integer.toString(temp.getId()))) {
                    temp.addSensor(sensor);
                    Log.d(Constants.DEBUGTAG, " $ getMonitoringItems adding sensor: "
                            + sensor.toString());
                }
                ret.add(temp);
            } while(c.moveToNext());
        }
        c.close();
        Log.d(Constants.DEBUGTAG, " $ getMonitoringItems  ret:" + ret.size());
        return ret;
    }

    private List<Sensor> getSensorsFromMoniItem(String moniId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + MoniItemSensorEntry.TABLE_NAME + " WHERE "
                + MoniItemSensorEntry.MS_MONI_REF + " = ?", new String [] {moniId});
        List<Sensor> ret = new ArrayList<Sensor>();
        if(c.moveToFirst()) {
            do {
                String sensorId = Integer.toString(c.getInt(c.getColumnIndex(
                        MoniItemSensorEntry.MS_SENSOR_REF)));
                Log.d(Constants.DEBUGTAG, " $ getSensorsFromMoniItem  sensorId: " + sensorId);
                Sensor temp = getSensorBy(sensorId);
                Log.d(Constants.DEBUGTAG, " $ getSensorsFromMoniItem  arg:" + temp.toString());
                ret.add(temp);
            } while(c.moveToNext());
        }
        c.close();
        Log.d(Constants.DEBUGTAG, " $ getSensorsFromMoniItem  total:" + ret.size());
        return ret;
    }

    private int getMoniItemIdBy(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + MoniItemEntry.TABLE_NAME + " WHERE "
            + MoniItemEntry.M_NAME + " = ?", new String[]{name.trim()});
        int ret = -1;
        if (c.moveToFirst())
            ret = c.getInt(c.getColumnIndex(MoniItemEntry._ID));
        c.close();
        return ret;
    }

    /** Returns a monitoring item given its id.
     * @param id - monitoring item's id
     * @return item
     */
    public MonitoringItem getMoniItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + MoniItemEntry.TABLE_NAME + " WHERE "
                + MoniItemEntry._ID + " = ?", new String [] {Integer.toString(id)});
        MonitoringItem ret = null;
        if(c.moveToFirst()) {
            ret = new MonitoringItem(c.getString
                    (c.getColumnIndex(MoniItemEntry.M_NAME)));
            ret.setId(c.getInt(c.getColumnIndex(MoniItemEntry._ID)));
            ret.setPhotoPath(c.getString(c.getColumnIndex(MoniItemEntry.M_PHOTO_PATH)));
            ret.setWarningEnabled(Boolean.valueOf(c.getString(
                    c.getColumnIndex(MoniItemEntry.M_IS_WARNING))));
            for (Sensor sensor : getSensorsFromMoniItem(Integer.toString(ret.getId()))) {
                ret.addSensor(sensor);
                Log.d(Constants.DEBUGTAG, " $ getMonitoringItems adding sensor: "
                        + sensor.toString());
            }
        }
        c.close();
        return ret;
    }

    /** Adds a MonitoringItem to the manager.
     * @param item - item to add     */
    public synchronized void addItem(MonitoringItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MoniItemEntry.M_NAME, item.getName().trim());
        values.put(MoniItemEntry.M_PHOTO_PATH, item.getPhotoPath());
        values.put(MoniItemEntry.M_IS_WARNING, Boolean.toString(item.isWarningEnabled()));
        db.insert(MoniItemEntry.TABLE_NAME, null, values);
        item.setId(getMoniItemIdBy(item.getName()));
        Log.d(Constants.DEBUGTAG, " $ addItem arg:" + item.toString());
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
        values.put(MoniItemSensorEntry.MS_MONI_REF, item.getId());
        values.put(MoniItemSensorEntry.MS_SENSOR_REF, getSensorID(sensor.getPinId(),
                String.valueOf(sensor.getType().getIdentifier())));
        db.insert(MoniItemSensorEntry.TABLE_NAME, null, values);
        Log.d(Constants.DEBUGTAG, " $ addSensorToMoniItem  arg:" + sensor.toString());
    }

    /** Deletes a monitoring item
     * @param item - to delete
     */
    public synchronized void deleteItem(MonitoringItem item) {
        deleteItem(item.getId());
    }

    /** Deletes a monitoring item
     * @param id - id of the monitoring item to delete
     */
    public synchronized void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = MoniItemEntry._ID + " = ? ";
        db.delete(MoniItemEntry.TABLE_NAME, selection, new String[] {Integer.toString(id)});
        Log.d(Constants.DEBUGTAG, " $ deteteMoniItem  arg:" + id);
    }

    /** Sets the warning to the requested status
     * @param enabled
     * @param monitoringItem
     */
    public void setWarning(boolean enabled, MonitoringItem monitoringItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MoniItemEntry.M_IS_WARNING, Boolean.toString(enabled));
        int result = db.update(MoniItemEntry.TABLE_NAME, values, MoniItemEntry._ID + " = ?",
                new String[] {Integer.toString(monitoringItem.getId())});
        if (result > 0)
            Log.d(Constants.DEBUGTAG, "$ updated: MoniItem warningStatus");
        else
            Log.e(Constants.DEBUGTAG, "$ NOT updated: MoniItem warningStatus");
    }

    /** Checks if a MonitoringItem's warning is enabled
     * @param item - item to check
     * @return true or false
     */
    public boolean isWarningEnabled(MonitoringItem item) {
        boolean ret = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + MoniItemEntry.TABLE_NAME + " WHERE "
            + MoniItemEntry._ID + " = ?", new String[] {Integer.toString(item.getId())});
        if (c.moveToFirst())
            ret = Boolean.valueOf(c.getString(c.getColumnIndex(MoniItemEntry.M_IS_WARNING)));
        c.close();
        return ret;
    }

    // --- Actuators ---

    /** Adds an Actuator to the DB
     * @param actuator - actuator to add
     */
    public boolean addActuator(Actuator actuator) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put(ActuatorEntry.AC_PIN, actuator.getPinId());
        vals.put(ActuatorEntry.AC_NAME, actuator.getName());
        if (actuator.hasControlSensor()) {
            vals.put(ActuatorEntry.AC_SENSOR_REF,
                    getSensorID(actuator.getControlSensor().getPinId(),
                            String.valueOf(actuator.getControlSensor().getType().getIdentifier())));
            vals.put(ActuatorEntry.AC_COMPARE_TYPE, actuator.getCompareType().toString());
            vals.put(ActuatorEntry.AC_COMPARE_VALUE, actuator.getCompareValue());
        }
        if (db.insert(ActuatorEntry.TABLE_NAME, null, vals) == -1) {
            Log.d(Constants.DEBUGTAG, " $ addActuator ERROR adding: " + actuator.toString());
            return false;
        } else
            return true;
    }

    /** Updates an Actuator
     * @param actuator - actuator to update
     */
    public boolean updateActuator(Actuator actuator) {
        Log.d(Constants.DEBUGTAG, " $ updateActuator: actuator: " + actuator.toString());
        if (!actuator.hasControlSensor()) {
            deleteActuator(actuator);
            return addActuator(actuator);

        } else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues vals = new ContentValues();
            vals.put(ActuatorEntry.AC_NAME, actuator.getName());
            vals.put(ActuatorEntry.AC_SENSOR_REF,
                        getSensorID(actuator.getControlSensor().getPinId(),
                                String.valueOf(actuator.getControlSensor().getType().getIdentifier())));
            vals.put(ActuatorEntry.AC_COMPARE_TYPE, actuator.getCompareType().toString());
            vals.put(ActuatorEntry.AC_COMPARE_VALUE, actuator.getCompareValue());

            int result = db.update(ActuatorEntry.TABLE_NAME, vals, ActuatorEntry.AC_PIN + " = ?",
                    new String[] {actuator.getPinId()});
            if (result > 0) {
                Log.d(Constants.DEBUGTAG, " $ updateActuator: oks");
                return true;
            } else {
                Log.e(Constants.DEBUGTAG, " $ updateActuator: NOT updated");
                return false;
            }
        }
    }

    /** Deletes an Actuator
     * @param actuator - actuator to update
     */
    public void deleteActuator(Actuator actuator) {
        SQLiteDatabase db = this.getWritableDatabase();
        String select = ActuatorEntry.AC_PIN + " = ? ";
        db.delete(ActuatorEntry.TABLE_NAME, select, new String[] {actuator.getPinId()});
        Log.d(Constants.DEBUGTAG, " $ deleteActuator deleted: " + actuator.toString());
    }

    /** Obtains all the actuators in the db
     * @return - list of actuators
     */
    public List<Actuator> getAllActuators() {
        List<Actuator> ret = new ArrayList<Actuator>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ActuatorEntry.TABLE_NAME, new String[]{});
        if (cursor.moveToFirst()) {
            do {
                Actuator actTemp = new Actuator(
                        cursor.getString(cursor.getColumnIndex(ActuatorEntry.AC_NAME)),
                        cursor.getString(cursor.getColumnIndex(ActuatorEntry.AC_PIN)));
                if (cursor.getColumnIndex(ActuatorEntry.AC_SENSOR_REF) != -1 &&
                   cursor.getString(cursor.getColumnIndex(ActuatorEntry.AC_COMPARE_TYPE)) != null)
                {
                    // The column exists, there is a control sensor for this actuator
                    int sensorId = cursor.getInt(cursor.getColumnIndex(
                            ActuatorEntry.AC_SENSOR_REF));
                    Sensor s = getSensorBy(Integer.toString(sensorId));
                    actTemp.setControlSensor(s);

                    String compareType = cursor.getString(
                            cursor.getColumnIndex(ActuatorEntry.AC_COMPARE_TYPE));
                    actTemp.setCompareType(AlertType.valueOf(compareType));
                    actTemp.setCompareValue(cursor.getDouble(
                            cursor.getColumnIndex(ActuatorEntry.AC_COMPARE_VALUE)));
                }
                ret.add(actTemp);
                Log.d(Constants.DEBUGTAG, " $ getActuators end: " + actTemp.toString());
            } while(cursor.moveToNext());
        }
        cursor.close();
        return ret;
    }

    // --- Sesors's cached values --

    /** Given a set of sensor's values updates the db.
     * @param values - values to save
     * @param sensor - sensor's reference
     */
    public void cacheData(List<Map<String, Float>> values, Sensor sensor) {
        cacheData(values, sensor.getPinId(), String.valueOf(sensor.getType().getIdentifier()));
    }

    /** Given a set of sensor's values updates the db.
     * @param values - values to save
     * @param pinId - sensor's pinid
     * @param sensorType - sensor's type
     */
    public synchronized void cacheData(List<Map<String, Float>> values, String pinId,
    String sensorType)
    {
        SQLiteDatabase db = getWritableDatabase();
        int sensorId = getSensorID(pinId, sensorType);
        Log.d(Constants.DEBUGTAG, " $ catching sensor: pinId " + pinId + " type: " + sensorType);
        for (Map<String, Float> map : values) {
            String key = (String) map.keySet().toArray()[0];
            Log.d(Constants.DEBUGTAG, " $ catching value: " + map.get(key) + ", time: "
                    + key.substring(0, key.indexOf('-')) + ", date: "
                    + key.substring(key.indexOf('-') + 1));
            ContentValues toWrite = new ContentValues();
            toWrite.put(SensorHistory.SH_VALUE, map.get(key));
            toWrite.put(SensorHistory.SH_SENSOR, sensorId);
            toWrite.put(SensorHistory.SH_TIME, key.substring(0, key.indexOf('-')).trim());
            toWrite.put(SensorHistory.SH_DATE, key.substring(key.indexOf('-') + 1).trim());
            if (db.insert(SensorHistory.TABLE_NAME, null, toWrite) == -1)
                Log.e(Constants.DEBUGTAG, " $ error caching: " + map.toString());
        }
    }

    /** Given a sensor retrieves it's last cached values
     * @param sensor -
     * @return
     */
    public List<Map<String, String>> getLastCachedValues(Sensor sensor) {
        int sensorId = getSensorID(sensor.getPinId(),
                String.valueOf(sensor.getType().getIdentifier()));
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "  + SensorHistory.TABLE_NAME + " WHERE "
                + SensorHistory.SH_SENSOR  + " = ?", new String[]{ Integer.toString(sensorId)});
        List<Map<String, String>> ret = new ArrayList<Map<String, String>>();
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                i++;
                Map<String, String> value = new HashMap<String, String>();
                value.put(SensorHistory.SH_DATE, cursor
                        .getString(cursor.getColumnIndex(SensorHistory.SH_DATE)));
                value.put(SensorHistory.SH_TIME, cursor
                        .getString(cursor.getColumnIndex(SensorHistory.SH_TIME)));
                value.put(SensorHistory.SH_VALUE, Double.toString(cursor
                        .getDouble(cursor.getColumnIndex(SensorHistory.SH_VALUE))));
                ret.add(value);
                Log.d(Constants.DEBUGTAG, " $ returning value: " + value.get(SensorHistory.SH_VALUE)
                            + ", time: " + value.get(SensorHistory.SH_TIME) + ", date: "
                            + value.get(SensorHistory.SH_DATE));
            } while(cursor.moveToNext());
        }
        cursor.close();
        Log.d(Constants.DEBUGTAG, " $ processed cache values: " + i);
        return ret;
    }

    /** Clears the db's cache of historic data of the given sensor
     * @param sensor -
     */
    public void cleanCacheData(Sensor sensor) {
        cleanCacheData(sensor.getPinId(), String.valueOf(sensor.getType().getIdentifier()));

    }
    /** Clears the db's cache of historic data of the given sensor
     * @param sensorPin - sensor's pinid
     * @param sensorTypeIdentifier - sensor's type identifier
     */
    public void cleanCacheData(String sensorPin, String sensorTypeIdentifier) {

        SQLiteDatabase db = getWritableDatabase();
        int sensorId = getSensorID(sensorPin, sensorTypeIdentifier);
        int deleted = db.delete(SensorHistory.TABLE_NAME, SensorHistory.SH_SENSOR + " = ?",
                new String [] { Integer.toString(sensorId)});
        Log.d(Constants.DEBUGTAG, " $ clean cache values: " + deleted);
    }

    /** Checks whether the given sensor has associated cache values.
     * @param sensor - sensor to check
     */
    public boolean doesSensorHaveCache(Sensor sensor) {
        boolean ret = false;
        int sensorId = getSensorID(sensor.getPinId(),
                String.valueOf(sensor.getType().getIdentifier()));
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "  + SensorHistory.TABLE_NAME + " WHERE "
                + SensorHistory.SH_SENSOR  + " = ?", new String[]{ Integer.toString(sensorId)});
        ret = cursor.getCount() > 0;
        cursor.close();
        return ret;
    }
}
