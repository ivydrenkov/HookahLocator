package ru.hookahlocator.hooklocator.data.entities;

import android.content.ContentValues;
import android.database.Cursor;

public interface IDBItem {
	/**
	 * @param cursor - DataBase cursor positioned on model
	 */
	abstract void fromDBCursor(Cursor cursor);

	abstract void toDBValues(ContentValues cv);

}
