package com.syntaxsofts.android.passwordmanager.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.syntaxsofts.android.passwordmanager.PMObjects.Folder;

import java.util.ArrayList;

/**
 * Class for implementing database handling operations
 * Author: blackbeard
 * Created Date: 4/8/15
 */
public abstract class DbHandler {

    public class FolderAlreadyExistsException extends Exception {}

    private Context context;

    public DbHandler(Context context) {
        this.context = context;
    }

    /**
     * Method to check if the folder exists with the given name
     */
    public boolean isFolderExists(String folderName) {
        SQLiteDatabase db = new PasswordManagerDbHelper(context).getReadableDatabase();

        Cursor mCursor = db.query(DatabaseSchema.FolderSchema.TABLE_NAME,
            new String[] { DatabaseSchema.FolderSchema._ID },
            DatabaseSchema.FolderSchema.COLUMN_FOLDER_NAME + " = ?",
            new String[] { folderName },
            null,
            null,
            null);

        return mCursor.getCount() != 0;
    }

    /**
     * Method to insert a new folder entry in the database
     */
    public Folder insertFolder(Folder folder) throws FolderAlreadyExistsException {
        if (isFolderExists(folder.getFolderName()) == true) {
            throw new FolderAlreadyExistsException();
        }

        SQLiteDatabase db = new PasswordManagerDbHelper(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSchema.FolderSchema.COLUMN_FOLDER_NAME, folder.getFolderName());
        values.put(DatabaseSchema.FolderSchema.COLUMN_FOLDER_PASSWORD, folder.getFolderPassword());
        values.put(DatabaseSchema.FolderSchema.COLUMN_FOLDER_INFO, folder.getFolderInfo());

        folder.setId(db.insert(DatabaseSchema.FolderSchema.TABLE_NAME, null, values));

        db.close();
        return folder;
    }

    /**
     * Method to get a list of all the folders in the database
     */
    public ArrayList<Folder> getAllFolders() {
        ArrayList<Folder> folderList = new ArrayList<>();

        SQLiteDatabase db = new PasswordManagerDbHelper(context).getReadableDatabase();

        String[] columnList = new String[] {
            DatabaseSchema.FolderSchema._ID,
            DatabaseSchema.FolderSchema.COLUMN_FOLDER_NAME,
            DatabaseSchema.FolderSchema.COLUMN_FOLDER_PASSWORD,
            DatabaseSchema.FolderSchema.COLUMN_FOLDER_INFO
        };

        Cursor mCursor = db.query(DatabaseSchema.FolderSchema.TABLE_NAME,
            columnList,
            null,
            null,
            null,
            null,
            DatabaseSchema.FolderSchema.COLUMN_FOLDER_NAME);
        mCursor.moveToFirst();

        while (mCursor.isAfterLast() == false) {
            folderList.add(getFolderFromCursor(mCursor));
            mCursor.moveToNext();
        }

        return folderList;
    }

    /**
     * Method to get folder object from database cursor
     */
    private static Folder getFolderFromCursor(Cursor cursor) {
        Folder mFolder = new Folder();
        mFolder.setId(cursor.getLong(0));
        mFolder.setFolderName(cursor.getString(1));
        mFolder.setFolderPassword(cursor.getString(2));
        mFolder.setFolderInfo(cursor.getString(3));

        return mFolder;
    }

}
