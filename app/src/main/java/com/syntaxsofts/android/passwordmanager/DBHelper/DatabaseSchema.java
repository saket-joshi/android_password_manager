package com.syntaxsofts.android.passwordmanager.DBHelper;

import android.provider.BaseColumns;

/**
 * Database schema class for Password Manager
 * Author: blackbeard
 * Created Date: 4/8/15
 */
public class DatabaseSchema {

    private static final String TYPE_TEXT = " TEXT ";
    private static final String TYPE_INT = " INTEGER ";
    private static final String COMMA_SEP = ", ";

    public DatabaseSchema() {}

    /**
     * Inner class that defines the password storage schema
     */
    public static abstract class PasswordSchema implements BaseColumns {
        public static final String TABLE_NAME = "tblPassword";
        public static final String COLUMN_FOLDER_ID = "folderId";
        public static final String COLUMN_LABEL = "label";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_WEBSITE = "website";
        public static final String COLUMN_NOTES = "notes";
    }

    /**
     * Inner class that defines the folder storage schema
     */
    public static abstract class FolderSchema implements BaseColumns {
        public static final String TABLE_NAME = "tblFolder";
        public static final String COLUMN_FOLDER_NAME = "folderName";
        public static final String COLUMN_FOLDER_INFO = "folderInfo";
        public static final String COLUMN_FOLDER_PASSWORD = "folderPassword";
    }

    /**
     * Static string to create folder table
     */
    public static final String CREATE_TABLE_FOLDER =
        "CREATE TABLE " + FolderSchema.TABLE_NAME + " ("
        + FolderSchema._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + FolderSchema.COLUMN_FOLDER_NAME + TYPE_TEXT + COMMA_SEP
        + FolderSchema.COLUMN_FOLDER_PASSWORD + TYPE_TEXT + COMMA_SEP
        + FolderSchema.COLUMN_FOLDER_INFO + TYPE_TEXT
        + ")";

    /**
     * Static string to create password table
     */
    public static final String CREATE_TABLE_PASSWORD =
        "CREATE TABLE " + PasswordSchema.TABLE_NAME + " ("
        + PasswordSchema._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + PasswordSchema.COLUMN_FOLDER_ID + TYPE_INT + COMMA_SEP
        + PasswordSchema.COLUMN_LABEL + TYPE_TEXT + COMMA_SEP
        + PasswordSchema.COLUMN_USERNAME + TYPE_TEXT + COMMA_SEP
        + PasswordSchema.COLUMN_PASSWORD + TYPE_TEXT + COMMA_SEP
        + PasswordSchema.COLUMN_WEBSITE + TYPE_TEXT + COMMA_SEP
        + PasswordSchema.COLUMN_NOTES + TYPE_TEXT
        + ")";

    /**
     * Method to get the DELETE TABLE string for the given table name
     */
    public static String getDropTableString(String tableName) {
        return "DROP TABLE IF EXISTS " + tableName;
    }

}
