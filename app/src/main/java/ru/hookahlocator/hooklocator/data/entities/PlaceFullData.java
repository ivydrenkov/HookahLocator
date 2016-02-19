package ru.hookahlocator.hooklocator.data.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.Html;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import ru.hookahlocator.hooklocator.dagger.Injector;
import ru.hookahlocator.hooklocator.data.entities.annotations.JSONField;
import ru.hookahlocator.hooklocator.data.JSONParser;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class PlaceFullData extends BaseObject {
    final static String TAG = "PlaceFullData";

    @Inject JSONParser jsonParser;

    public Info info;
    public ArrayList<Tag> tags;
    private JSONArray tagsJsonArray;
    public ArrayList<Comment> comments;
    private JSONArray commentsJsonArray;

    public PlaceFullData() {
        Injector.getDataProviderComponent().inject(this);
    }

    public static class Info implements IParseable {
        @JSONField public int id;
        @JSONField public String name;
        @JSONField public String logo;
        @JSONField public String food;
        @JSONField public String drinks;
        @JSONField public int rates;
        @JSONField public String metro;
        @JSONField public String phone;
        @JSONField(jsonName = "rate_all")
        public String rate;
        @JSONField(jsonName = "rate_atmosphere")
        public String rateAtmosphere;
        @JSONField(jsonName = "rate_service")
        public String rateService;
        @JSONField(jsonName = "rate_calian")
        public String rateHookah;
        @JSONField(jsonName = "cost_calian")
        public String costHookah;
        @JSONField(jsonName = "cost_tea")
        public String costTea;
        @JSONField(jsonName = "crossStreet")
        public String address;
        @JSONField(jsonName = "open")
        public String timeOpen;
        @JSONField(jsonName = "close")
        public String timeClose;
        @JSONField(jsonName = "weekend_open")
        public String timeWeekendOpen;
        @JSONField(jsonName = "weekend_close")
        public String timeWeekendClose;
        @JSONField(jsonName = "main_image")
        public String mainImage;

        public String currency;
        public boolean isAllTime;
        public String timeWorking;
        public String timeWeekendWorking;
        public ArrayList<String> photos;
        private JSONArray photosJsonArray;

        @Override
        public void parseFromJSON(JSONObject json) throws JSONException { }
    }

    @Override
    public void parseFromJSON(JSONObject jsonRoot) throws JSONException {
        JSONObject jsonInfo = jsonRoot.getJSONObject("info");
        {
            info = jsonParser.parseObject(jsonInfo, Info.class);
            info.currency = Html.fromHtml(jsonInfo.getString("currency")).toString();
            String _isAllTime = jsonInfo.getString("is_alltime");
            info.isAllTime = !_isAllTime.equalsIgnoreCase("no");
            info.timeWorking = Html.fromHtml(jsonInfo.getString("working")).toString();
            info.timeWeekendWorking = Html.fromHtml(jsonInfo.optString("w_working")).toString();
            if (info.timeWeekendWorking.length() == 0) { // if no weekend work time
                info.timeWeekendWorking = info.timeWorking; // using normal work time
            }
            info.photosJsonArray = jsonInfo.optJSONArray("photos");
            if ((info.photosJsonArray != null)&&(info.photosJsonArray.length()>0)) {
                info.photos = getPhotosFromJSONArray(info.photosJsonArray);
            }
        }
        tagsJsonArray = jsonRoot.optJSONArray("tags");
        if ((tagsJsonArray != null)&&(tagsJsonArray.length() > 0)) {
            tags = getTagsFromJSONArray(tagsJsonArray);
        }
        commentsJsonArray = jsonRoot.optJSONArray("comments");
        if ((commentsJsonArray != null)&&(commentsJsonArray.length() > 0)) {
            comments = getCommentsFromJSONArray(commentsJsonArray);
        }
    }

    @Override
    public void fromDBCursor(Cursor c) {
        info.id = c.getInt(c.getColumnIndex("id"));
        info.name = c.getString(c.getColumnIndex("name"));
        info.rate = c.getString(c.getColumnIndex("rate_all"));
        info.rateAtmosphere = c.getString(c.getColumnIndex("rate_atmosphere"));
        info.rateService = c.getString(c.getColumnIndex("rate_service"));
        info.rateHookah = c.getString(c.getColumnIndex("rate_calian"));
        info.rates = c.getInt(c.getColumnIndex("rates"));
        info.currency = c.getString(c.getColumnIndex("currency"));
        info.costHookah = c.getString(c.getColumnIndex("cost_calian"));
        info.costTea = c.getString(c.getColumnIndex("cost_tea"));
        info.food = c.getString(c.getColumnIndex("food"));
        info.drinks = c.getString(c.getColumnIndex("drinks"));
        info.address = c.getString(c.getColumnIndex("address"));
        info.metro = c.getString(c.getColumnIndex("metro"));
        info.phone = c.getString(c.getColumnIndex("phone"));
        info.isAllTime = c.getInt(c.getColumnIndex("is_alltime"))!=0;
        info.timeOpen = c.getString(c.getColumnIndex("open"));
        info.timeClose = c.getString(c.getColumnIndex("close"));
        info.timeWeekendOpen = c.getString(c.getColumnIndex("weekend_open"));
        info.timeWeekendClose = c.getString(c.getColumnIndex("weekend_close"));
        info.timeWorking = c.getString(c.getColumnIndex("working"));
        info.timeWeekendWorking = c.getString(c.getColumnIndex("w_working"));
        if (info.timeWeekendWorking.length() == 0) { // if no weekend work time
            info.timeWeekendWorking = info.timeWorking; // using normal work time
        }
        info.logo = c.getString(c.getColumnIndex("logo"));
        info.mainImage = c.getString(c.getColumnIndex("main_image"));
        String photosString = c.getString(c.getColumnIndex("photos_json_array"));
        if (photosString != null) {
            try {
                info.photosJsonArray = new JSONArray(photosString);
                info.photos = getPhotosFromJSONArray(info.photosJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Log.w(TAG, "No photos for object: " + info.name);
        }
        String tagsString = c.getString(c.getColumnIndex("tags_json_array"));
        if (tagsString != null) {
            try {
                tagsJsonArray = new JSONArray(tagsString);
                tags = getTagsFromJSONArray(tagsJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Log.w(TAG, "No tags for object: " + info.name);
        }
        String commentsString = c.getString(c.getColumnIndex("comments_json_array"));
        if (commentsString != null) {
            try {
                commentsJsonArray = new JSONArray(commentsString);
                comments = getCommentsFromJSONArray(commentsJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Log.w(TAG, "No comments for object: " + info.name);
        }
    }

    @Override
    public void toDBValues(ContentValues cv) {
        cv.put("id", info.id);
        cv.put("name", info.name);
        cv.put("rate_all", info.rate);
        cv.put("rate_atmosphere", info.rateAtmosphere);
        cv.put("rate_service", info.rateService);
        cv.put("rate_calian", info.rateHookah);
        cv.put("rates", info.rates);
        cv.put("currency", info.currency);
        cv.put("cost_calian", info.costHookah);
        cv.put("cost_tea", info.costTea);
        cv.put("food", info.food);
        cv.put("drinks", info.drinks);
        cv.put("address", info.address);
        cv.put("metro", info.metro);
        cv.put("phone", info.phone);
        cv.put("is_alltime", info.isAllTime?1:0);
        cv.put("open", info.timeOpen);
        cv.put("close", info.timeClose);
        cv.put("weekend_open", info.timeWeekendOpen);
        cv.put("weekend_close", info.timeWeekendClose);
        cv.put("working", info.timeWorking);
        cv.put("w_working", info.timeWeekendWorking);
        cv.put("logo", info.logo);
        cv.put("main_image", info.mainImage);
        if (info.photosJsonArray != null) {
            cv.put("photos_json_array", info.photosJsonArray.toString());
        }
        if (tagsJsonArray != null) {
            cv.put("tags_json_array", tagsJsonArray.toString());
        }
        if (commentsJsonArray != null) {
            cv.put("comments_json_array", commentsJsonArray.toString());
        }
    }

    private ArrayList<Tag> getTagsFromJSONArray(JSONArray jsonTags) {
        ArrayList<Tag> parsedTags = jsonParser.parseObjects(jsonTags, Tag.class);
        return parsedTags;
    }

    private ArrayList<Comment> getCommentsFromJSONArray(JSONArray jsonComments) {
        ArrayList<Comment> parsedComments = jsonParser.parseObjects(jsonComments, Comment.class);
        return parsedComments;
    }

    private ArrayList<String> getPhotosFromJSONArray(JSONArray jsonPhotos) {
        int numPhotos = jsonPhotos.length();
        ArrayList<String> photos = new ArrayList<>(numPhotos);
        for (int i = 0; i < numPhotos; i++) {
            try {
                photos.add(jsonPhotos.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return photos;
    }
}
