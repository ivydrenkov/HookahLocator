package ru.hookahlocator.hooklocator.data.entities;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import ru.hookahlocator.hooklocator.data.entities.annotations.JSONField;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class Tag extends BaseObject {
    final static String TAG = "Tag";

    @JSONField public int id;
    @JSONField public String name;
    @JSONField public String rubric;

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
