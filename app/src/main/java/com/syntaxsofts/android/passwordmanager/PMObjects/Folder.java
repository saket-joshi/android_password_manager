package com.syntaxsofts.android.passwordmanager.PMObjects;

/**
 * Class containing folder data structure
 * Author: blackbeard
 * Created Date: 4/8/15
 */
public class Folder {

    private long Id;
    private String folderName;
    private String folderInfo;
    private String folderPassword;

    public Folder() {
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        this.Id = id;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderInfo() {
        return folderInfo;
    }

    public void setFolderInfo(String folderInfo) {
        this.folderInfo = folderInfo;
    }

    public String getFolderPassword() {
        return folderPassword;
    }

    public void setFolderPassword(String folderPassword) {
        this.folderPassword = folderPassword;
    }

}
