package com.zero.support.binder;

import android.os.Parcel;

import java.io.File;
import java.lang.reflect.Type;

public class FileParcelCreator implements ParcelCreator<File> {
    @Override
    public void writeToParcel(Parcel parcel, File target, Type type, Class<File> rawType) throws Exception {
        if (target == null) {
            parcel.writeString(null);
        } else {
            parcel.writeString(target.getAbsolutePath());
        }
    }

    @Override
    public File readFromParcel(Parcel parcel, Type type, Class<File> rawType) throws Exception {
        String path = parcel.readString();
        if (path == null) {
            return null;
        } else {
            return new File(path);
        }
    }
}
