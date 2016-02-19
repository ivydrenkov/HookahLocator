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
public class Comment extends BaseObject {
    final static String TAG = "Comment";

    @JSONField(jsonName = "comment_id") public int id;
    @JSONField(jsonName = "user_id") public int userId;
    @JSONField public int likes;
    @JSONField public int dislikes;
    @JSONField public String nickname;
    @JSONField public String date;
    @JSONField public String text;
    @JSONField public String photo;
    @JSONField public String rang;

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
