package ru.hookahlocator.hooklocator.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ru.hookahlocator.hooklocator.data.entities.IDBItem;
import ru.hookahlocator.hooklocator.data.entities.annotations.DBField;

public class DataBase {
    private static final String TAG = "Database";
    private static SQLiteDatabase DB = null;

    public final static String TABLE_CITIES = "cities";
    public final static String TABLE_PLACES = "places";
    public final static String TABLE_PLACES_DATA = "places_data";
    public final static String TABLE_PLACES_ADDITIONAL = "places_additional";
    public final static String TABLE_FAVORITE_PLACES = "favorite_places";

    public DataBase(Context context) {
        init(context);
    }

    private void init(Context context) {
        if (DB == null) {
            DbOpenHelper h = new DbOpenHelper(context);
            try {
                DB = h.getWritableDatabase();
            } catch (SQLiteException e) {
                String msg = String.format("Cannot get writable database with error: %s", e.getLocalizedMessage());
                Log.d(TAG, msg, e);
            }
        }
    }

    public SQLiteDatabase getDataBase() {
        return DB;
    }

    public boolean saveItemToTable(IDBItem item, String table) {
        if (DB != null) {
            ContentValues cv = new ContentValues();
            writeObjectToCV(item, cv);
            DB.insert(table, null, cv);
            return true;
        }
        return false;
    }

    public void deleteTable(String table) {
        if (DB != null) {
            DB.delete(table, null, null);
        }
    }

    /**
     * Clear old data from table before write
     * @param items
     * @param table
     * @return true on success
     */
    public boolean saveItemsToTableDestructively(List<? extends IDBItem> items, String table) {
        deleteTable(table);
        return saveItemsToTable(items, table);
    }

    public boolean saveItemsToTable(List<? extends IDBItem> items, String table) {
        if (DB != null) {
            Log.v(TAG, "Saving " + items.size() + " data items to table " + table);
            DB.beginTransaction();
            for (IDBItem item : items) {
                ContentValues cv = new ContentValues();
                writeObjectToCV(item, cv);
                DB.insert(table, null, cv);
            }
            DB.setTransactionSuccessful();
            DB.endTransaction();
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public <T extends IDBItem> ArrayList<T> getItemsFromTable(String table, Class<T> type) {
        return getItemsFromTable(table, type, null);
    }

    @Nullable
    public <T extends IDBItem> ArrayList<T> getItemsFromTable(String table, Class<T> type, @Nullable String where) {
        ArrayList<T> list = null;
        if (DB != null) {
            Cursor c = DB.query(table, null, where, null, null, null, null);
            if (c.getCount() > 0) { //Found data
                list = new ArrayList<T>(c.getCount());
                while (c.moveToNext()) {
                    T item = null;
                    try {
                        item = type.newInstance();
                        fillObjectFromCursor(item, c);
                        item.fromDBCursor(c);
                        list.add(item);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            c.close();
        }
        return list;
    }

    @Nullable
    public <T extends IDBItem> T getItemFromTable(String table, Class<T> type, @Nullable String where) {
        T item = null;
        if (DB != null) {
            Cursor c = DB.query(table, null, where, null, null, null, null);
            if (c.getCount() > 0) { //Found data
                c.moveToFirst();
                try {
                    item = type.newInstance();
                    fillObjectFromCursor(item, c);
                    item.fromDBCursor(c);
                    return item;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            c.close();
        }
        return item;
    }

    private void writeObjectToCV(IDBItem object, ContentValues contentValues) {
        for (Field field: object.getClass().getFields()) {
            if (field.isAnnotationPresent(DBField.class)) {
                try {
                    writeFieldToCV(object, field, contentValues);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeFieldToCV(IDBItem object, Field field, ContentValues contentValues) throws IllegalAccessException {
        field.setAccessible(true);
        DBField dbField = field.getAnnotation(DBField.class);
        String columnName = dbField.columnName();
        if (columnName.equals(DBField.DEFAULT)) {
            columnName = field.getName();
        }
        Type type = field.getType();
        if (type == int.class) {
            int value = field.getInt(object);
            contentValues.put(columnName, value);
        } else if (type == String.class) {
            String value = (String) field.get(object);
            if (value == null) {
                Log.e(TAG, "No value for " + columnName);
                value = "";
            }
            contentValues.put(columnName, value);
        } else if (type == LatLng.class) {
            LatLng latLngValue = (LatLng) field.get(object);
            contentValues.put("latitude", latLngValue.latitude);
            contentValues.put("longitude", latLngValue.longitude);
        }
    }

    private void fillObjectFromCursor(IDBItem object, Cursor cursor) {
        for (Field field: object.getClass().getFields()) {
            if (field.isAnnotationPresent(DBField.class)) {
                try {
                    readColumnToField(object, field, cursor);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readColumnToField(IDBItem object, Field field, Cursor cursor) throws IllegalAccessException {
        field.setAccessible(true);
        DBField dbField = field.getAnnotation(DBField.class);
        String columnName = dbField.columnName();
        if (columnName.equals(DBField.DEFAULT)) {
            columnName = field.getName();
        }
        Type type = field.getType();
        if (type == int.class) {
            int value = cursor.getInt(cursor.getColumnIndex(columnName));
            field.set(object, value);
        } else if (type == String.class) {
            String value = cursor.getString(cursor.getColumnIndex(columnName));
            if (value == null) {
                Log.e(TAG, "No value for " + columnName);
                value = "";
            }
            field.set(object, value);
        } else if (type == LatLng.class) {
            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
            field.set(object, new LatLng(latitude, longitude));
        }
    }
}

