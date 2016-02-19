/**
 * Author:    Igor Vydrenkov
 * Created:   April 2013
 **/

package ru.hookahlocator.hooklocator.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;

public class DbOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = "DbOpenHelper";
	private static final int DB_VERSION = 5;

	private Context context;

	public DbOpenHelper(Context context) {
		super(context, "Base.db", null,DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v(TAG, "Crating new base in: " + db.getPath());
		try {
			DBUtils.executeSqlScript(context, db, "db_scheme.sql");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            String sql_request = "alter table appointments " +
                    "add column service_id INTEGER;";
            db.execSQL(sql_request);
        }
        if (oldVersion < 3) {
            String sql_request = "alter table clinics " +
                    "add column profiles TEXT;";
            db.execSQL(sql_request);
        }
        if (oldVersion < 4) { // for create bookmarks table
            try {
                DBUtils.executeSqlScript(context, db, "db_scheme.sql");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		if (oldVersion < 5) { // for create favorite places table
			try {
				DBUtils.executeSqlScript(context, db, "db_scheme.sql");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}