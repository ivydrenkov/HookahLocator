package ru.hookahlocator.hooklocator.data.entities;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import ru.hookahlocator.hooklocator.data.entities.annotations.DBField;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class Favorite extends BaseObject {
    final static String TAG = "Favorite";

    @DBField(columnName = "place_id")
    public int placeId;
    @DBField(columnName = "city_abbr")
    public String cityAbbr;

    @Override
    public void parseFromJSON(JSONObject json) throws JSONException {
        //Don't keeps on server yet
    }

    @Override
    public void fromDBCursor(Cursor c) {
    }

    @Override
    public void toDBValues(ContentValues cv) {
    }
}
