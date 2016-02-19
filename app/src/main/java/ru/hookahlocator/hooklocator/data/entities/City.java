package ru.hookahlocator.hooklocator.data.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import ru.hookahlocator.hooklocator.data.entities.annotations.DBField;
import ru.hookahlocator.hooklocator.data.entities.annotations.JSONField;
import ru.hookahlocator.hooklocator.util.LocationUtils;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class City extends BaseObject {
    final static String TAG = "City";

    @JSONField()
    @DBField()
    public int id;
    @JSONField()
    @DBField()
    public String name;
    @JSONField()
    @DBField()
    public String abbr;
    @JSONField(jsonName = "count")
    @DBField()
    public int hookahsCount;
    @JSONField()
    @DBField()
    public LatLng location = LocationUtils.STUB_LOCATION;

    @Override
    public void parseFromJSON(JSONObject json) throws JSONException {
    }

    @Override
    public void fromDBCursor(Cursor c) {
    }

    @Override
    public void toDBValues(ContentValues cv) {
    }
}
