package com.novp.sprytar.data.model;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.realm.PoiFileRealmProxy;
import io.realm.RealmObject;

@Parcel(implementations = { PoiFileRealmProxy.class }, value = Parcel.Serialization
        .BEAN, analyze = { PoiFile.class })
public class PoiFile extends RealmObject {
    @StringDef({VIDEO, IMAGE, AUDIO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static final String VIDEO = "video";
    public static final String IMAGE = "image";
    public static final String AUDIO = "audio";

    @SerializedName("ioi_file_id")
    public int id;

    @SerializedName("ioi_file_path")
    public String filePath;

    @Type
    @SerializedName("ioi_file_type")
    public String fileType;

    public PoiFile() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(@Type String fileType) {
        this.fileType = fileType;
    }
}
