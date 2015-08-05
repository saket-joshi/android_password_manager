package com.syntaxsofts.android.passwordmanager.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.syntaxsofts.android.passwordmanager.Constants;
import com.syntaxsofts.android.passwordmanager.PMObjects.Folder;
import com.syntaxsofts.android.passwordmanager.PMObjects.Password;

import java.util.ArrayList;

/**
 * Class for implementing database handling operations
 * Author: blackbeard
 * Created Date: 4/8/15
 */
public abstract class DbHandler {

    public class FolderAlreadyExistsException extends Exception {}
    public class FolderNotFoundException extends Exception {}
    public class PasswordAlreadyExistsException extends Exception {}
    public class PasswordNotFoundException extends Exception {}

    private Context context;

    public DbHandler(Context context) {
        this.context = context;
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

    /**
     * Method to get password object from database cursor
     */
    private static Password getPasswordFromCursor(Cursor cursor) {
        Password password = new Password();
        password.setId(cursor.getLong(0));
        password.setFolderId(cursor.getLong(1));
        password.setLabel(cursor.getString(2));
        password.setUsername(cursor.getString(3));
        password.setPassword(cursor.getString(4));
        password.setNotes(cursor.getString(5));
        password.setWebsite(cursor.getString(6));

        return password;
    }

    /**
     * Method to get specific folder from name
     */
    public ArrayList<Folder> getFoldersFromName(String folderName) {
        ArrayList<Folder> folderList = new ArrayList<>();

        SQLiteDatabase db = new PasswordManagerDbHelper(context).getReadableDatabase();

        Cursor mCursor = db.query(DatabaseSchema.FolderSchema.TABLE_NAME,
            new String[] { DatabaseSchema.FolderSchema._ID },
            DatabaseSchema.FolderSchema.COLUMN_FOLDER_NAME + " LIKE ?",
            new String[] { folderName },
            null,
            null,
            null);
        mCursor.moveToFirst();

        while (mCursor.isAfterLast() == false) {
            folderList.add(getFolderFromCursor(mCursor));
            mCursor.moveToNext();
        }

        return folderList;
    }

    /**
     * Method to check if the folder exists with the given name
     */
    public boolean isFolderExists(String folderName) {
        return getFoldersFromName(folderName).isEmpty() == false;
    }

    /**
     * Method to insert a new folder entry in the database
     */
    public Folder insertFolder(Folder folder) throws Exception {
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
     * Method to update the folder information
     */
    public boolean updateFolderInfo(String folderName, Folder folder) throws Exception {
        if (isFolderExists(folderName) == false) {
            throw new FolderNotFoundException();
        }

        if (isFolderExists(folder.getFolderName()) == true) {
            throw new FolderAlreadyExistsException();
        }

        SQLiteDatabase db = new PasswordManagerDbHelper(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSchema.FolderSchema.COLUMN_FOLDER_NAME, folder.getFolderName());
        values.put(DatabaseSchema.FolderSchema.COLUMN_FOLDER_PASSWORD, folder.getFolderPassword());
        values.put(DatabaseSchema.FolderSchema.COLUMN_FOLDER_INFO, folder.getFolderInfo());

        return db.update(DatabaseSchema.FolderSchema.TABLE_NAME,
            values,
            DatabaseSchema.FolderSchema.COLUMN_FOLDER_NAME + " = ?",
            new String[] { folderName }) != 0;
    }

    /**
     * Method to delete a folder from the database
     */
    public boolean deleteFolder(String folderName) throws Exception {
        ArrayList<Folder> folderList = getFoldersFromName(folderName);
        if (folderList.isEmpty() == true) {
            throw new FolderNotFoundException();
        }

        // Update all the passwords under this folder to default folder
        updatePasswordFolder(folderList.get(0).getId(), Constants.DEFAULT_FOLDER_ID);

        SQLiteDatabase db = new PasswordManagerDbHelper(context).getWritableDatabase();

        return db.delete(DatabaseSchema.FolderSchema.TABLE_NAME,
            DatabaseSchema.FolderSchema.COLUMN_FOLDER_NAME + " = ?",
            new String[] { folderName }) != 0;
    }

    /**
     * Method to check if password exists checked with label
     */
    public boolean isPasswordExists(String label) {
        return getPasswordFromLabel(label).isEmpty() == false;
    }

    /**
     * Method to add a new password entry
     */
    public Password insertPassword(Password password) throws Exception {
        if (isPasswordExists(password.getLabel()) == true) {
            throw new PasswordAlreadyExistsException();
        }

        SQLiteDatabase db = new PasswordManagerDbHelper(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSchema.PasswordSchema.COLUMN_LABEL, password.getLabel());
        values.put(DatabaseSchema.PasswordSchema.COLUMN_FOLDER_ID, password.getFolderId());
        values.put(DatabaseSchema.PasswordSchema.COLUMN_NOTES, password.getNotes());
        values.put(DatabaseSchema.PasswordSchema.COLUMN_USERNAME, password.getUsername());
        values.put(DatabaseSchema.PasswordSchema.COLUMN_PASSWORD, password.getPassword());
        values.put(DatabaseSchema.PasswordSchema.COLUMN_WEBSITE, password.getWebsite());

        password.setId(db.insert(DatabaseSchema.PasswordSchema.TABLE_NAME,
            null,
            values));

        db.close();
        return password;
    }

    /**
     * Method to get list of password entries from label
     */
    public ArrayList<Password> getPasswordFromLabel(String label) {
        ArrayList<Password> passwordList = new ArrayList<>();

        SQLiteDatabase db = new PasswordManagerDbHelper(context).getReadableDatabase();

        Cursor mCursor = db.query(DatabaseSchema.PasswordSchema.TABLE_NAME,
            new String[] {DatabaseSchema.PasswordSchema._ID},
            DatabaseSchema.FolderSchema.COLUMN_FOLDER_NAME + " = ?",
            new String[] { label },
            null,
            null,
            null);
        mCursor.moveToFirst();

        while (mCursor.isAfterLast() == false) {
            passwordList.add(getPasswordFromCursor(mCursor));
            mCursor.moveToNext();
        }

        return passwordList;
    }

    /**
     * Method to get all password entries
     */
    public ArrayList<Password> getAllPasswords() {
        ArrayList<Password> passwordList = new ArrayList<>();

        SQLiteDatabase db = new PasswordManagerDbHelper(context).getReadableDatabase();

        String[] columnList = new String[] {
            DatabaseSchema.PasswordSchema._ID,
            DatabaseSchema.PasswordSchema.COLUMN_FOLDER_ID,
            DatabaseSchema.PasswordSchema.COLUMN_LABEL,
            DatabaseSchema.PasswordSchema.COLUMN_USERNAME,
            DatabaseSchema.PasswordSchema.COLUMN_PASSWORD,
            DatabaseSchema.PasswordSchema.COLUMN_NOTES,
            DatabaseSchema.PasswordSchema.COLUMN_WEBSITE
        };

        Cursor mCursor = db.query(DatabaseSchema.PasswordSchema.TABLE_NAME,
            columnList,
            null,
            null,
            null,
            null,
            DatabaseSchema.PasswordSchema.COLUMN_LABEL);
        mCursor.moveToFirst();

        while (mCursor.isAfterLast() == false) {
            passwordList.add(getPasswordFromCursor(mCursor));
            mCursor.moveToNext();
        }

        return passwordList;
    }

    /**
     * Method to update only the password folder
     */
    public boolean updatePasswordFolder(long oldFolder, long newFolder) {
        SQLiteDatabase db = new PasswordManagerDbHelper(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSchema.PasswordSchema.COLUMN_FOLDER_ID, newFolder);

        return db.update(DatabaseSchema.PasswordSchema.TABLE_NAME,
            values,
            DatabaseSchema.PasswordSchema.COLUMN_FOLDER_ID + " = ?",
            new String[] { String.valueOf(oldFolder) }) != 0;
    }

    /**
     * Method to update a password entry searched from label
     */
    public boolean updatePassword(String label, Password password) throws Exception {
        if (isPasswordExists(label) == false) {
            throw new PasswordNotFoundException();
        }

        if (isPasswordExists(password.getLabel()) == true) {
            throw new PasswordAlreadyExistsException();
        }

        SQLiteDatabase db = new PasswordManagerDbHelper(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSchema.PasswordSchema.COLUMN_FOLDER_ID, password.getFolderId());
        values.put(DatabaseSchema.PasswordSchema.COLUMN_LABEL, password.getLabel());
        values.put(DatabaseSchema.PasswordSchema.COLUMN_USERNAME, password.getUsername());
        values.put(DatabaseSchema.PasswordSchema.COLUMN_PASSWORD, password.getPassword());
        values.put(DatabaseSchema.PasswordSchema.COLUMN_NOTES, password.getNotes());
        values.put(DatabaseSchema.PasswordSchema.COLUMN_WEBSITE, password.getWebsite());

        return db.update(DatabaseSchema.PasswordSchema.TABLE_NAME,
            values,
            DatabaseSchema.PasswordSchema.COLUMN_LABEL + " = ?",
            new String[] { label }) != 0;
    }

    /**
     * Method to delete a password entry searched from label
     */
    public boolean deletePassword(String label) throws Exception {
        if (isPasswordExists(label) == false) {
            throw new PasswordNotFoundException();
        }

        SQLiteDatabase db = new PasswordManagerDbHelper(context).getWritableDatabase();
        return db.delete(DatabaseSchema.PasswordSchema.TABLE_NAME,
            DatabaseSchema.PasswordSchema.COLUMN_LABEL + " = ?",
            new String[] { label }) != 0;
    }

}
