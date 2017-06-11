/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.sdk.data;

import com.ironsource.sdk.data.SSAObj;

public class SSAFile
extends SSAObj {
    private String FILE = "file";
    private String PATH = "path";
    private String LAST_UPDATE_TIME = "lastUpdateTime";
    private String mFile;
    private String mPath;
    private String mErrMsg;
    private String mLastUpdateTime;

    public SSAFile(String value) {
        super(value);
        if (this.containsKey(this.FILE)) {
            this.setFile(this.getString(this.FILE));
        }
        if (this.containsKey(this.PATH)) {
            this.setPath(this.getString(this.PATH));
        }
        if (this.containsKey(this.LAST_UPDATE_TIME)) {
            this.setLastUpdateTime(this.getString(this.LAST_UPDATE_TIME));
        }
    }

    public SSAFile(String file, String path) {
        this.setFile(file);
        this.setPath(path);
    }

    public String getFile() {
        return this.mFile;
    }

    private void setFile(String file) {
        this.mFile = file;
    }

    private void setPath(String path) {
        this.mPath = path;
    }

    public String getPath() {
        return this.mPath;
    }

    public void setErrMsg(String errMsg) {
        this.mErrMsg = errMsg;
    }

    public String getErrMsg() {
        return this.mErrMsg;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.mLastUpdateTime = lastUpdateTime;
    }

    public String getLastUpdateTime() {
        return this.mLastUpdateTime;
    }
}

