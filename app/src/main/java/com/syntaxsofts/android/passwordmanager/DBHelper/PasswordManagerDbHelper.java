package com.syntaxsofts.android.passwordmanager.DBHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper class for Password Manager
 * Author: blackbeard
 * Created Date: 4/8/15
 */
public class PasswordManagerDbHelper extends SQLiteOpenHelper{

    private Context context;
    public static final Integer DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "android_pwd_mgr.db";

    public PasswordManagerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseSchema.CREATE_TABLE_FOLDER);
        db.execSQL(DatabaseSchema.CREATE_TABLE_PASSWORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseSchema.getDropTableString(DatabaseSchema.FolderSchema.TABLE_NAME));
        db.execSQL(DatabaseSchema.getDropTableString(DatabaseSchema.PasswordSchema.TABLE_NAME));

        onCreate(db);
    }

}
