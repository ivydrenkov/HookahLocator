package ru.hookahlocator.hooklocator.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Database utils, for example to execute SQL scripts */
// TODO add unit tests
public class DBUtils {
	final static String TAG = "DBUtils";

    public static void vacuum(SQLiteDatabase db) {
        db.execSQL("VACUUM");
    }

    /**
* Calls {@link #executeSqlScript(android.content.Context, android.database.sqlite.SQLiteDatabase, String, boolean)} with transactional set to true.
*
* @return number of statements executed.
*/
    public static int executeSqlScript(Context context, SQLiteDatabase db, String assetFilename) throws IOException {
        return executeSqlScript(context, db, assetFilename, true);
    }

    /**
* Executes the given SQL asset in the given database (SQL file should be UTF-8). The database file may contain
* multiple SQL statements. Statements are split using a simple regular expression (something like
* "semicolon before a line break"), not by analyzing the SQL syntax. This will work for many SQL files, but check
* yours.
*
* @return number of statements executed.
*/
    public static int executeSqlScript(Context context, SQLiteDatabase db, String assetFilename, boolean transactional)
            throws IOException {
        byte[] bytes = readAsset(context, assetFilename);
        String sql = new String(bytes, "UTF-8");
        String[] lines = sql.split(";(\\s)*[\n\r]");
        int count;
        if (transactional) {
            count = executeSqlStatementsInTx(db, lines);
        } else {
            count = executeSqlStatements(db, lines);
        }
        Log.i(TAG, "Executed " + count + " statements from SQL script '" + assetFilename + "'");
        return count;
    }

    public static int executeSqlStatementsInTx(SQLiteDatabase db, String[] statements) {
        db.beginTransaction();
        try {
            int count = executeSqlStatements(db, statements);
            db.setTransactionSuccessful();
            return count;
        } finally {
            db.endTransaction();
        }
    }

    public static int executeSqlStatements(SQLiteDatabase db, String[] statements) {
        int count = 0;
        for (String line : statements) {
            line = line.trim();
            if (line.length() > 0) {
            	//Log.v(TAG, "Executing: " + line);
                db.execSQL(line);
                count++;
            }
        }
        return count;
    }

    /**
* Copies all available data from in to out without closing any stream.
*
* @return number of bytes copied
*/
    public static int copyAllBytes(InputStream in, OutputStream out) throws IOException {
        int byteCount = 0;
        byte[] buffer = new byte[4096];
        while (true) {
            int read = in.read(buffer);
            if (read == -1) {
                break;
            }
            out.write(buffer, 0, read);
            byteCount += read;
        }
        return byteCount;
    }

    public static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copyAllBytes(in, out);
        return out.toByteArray();
    }

    public static byte[] readAsset(Context context, String filename) throws IOException {
        InputStream in = context.getResources().getAssets().open(filename);
        try {
            return readAllBytes(in);
        } finally {
            in.close();
        }
    }

}