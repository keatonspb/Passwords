package ru.discode.passwords.entry;

import android.provider.BaseColumns;

/**
 * Created by broadcaster on 12.10.2016.
 */

public class PasswordEntry implements BaseColumns {
    public static final String TABLE_NAME = "password";
    public static final String _ID = "password_id";
    public static final String COLUMN_NAME_TITLE = "password_title";
    public static final String COLUMN_NAME_PASSWORD = "password_password";

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + PasswordEntry.TABLE_NAME + " ("+
                    PasswordEntry._ID + " INTEGER PRIMARY KEY," +
                    PasswordEntry.COLUMN_NAME_TITLE + " TEXT," +
                    PasswordEntry.COLUMN_NAME_PASSWORD + " TEXT" + " )";
    public static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS "+TABLE_NAME;
}
