package com.zero.support.binder;

import android.os.Parcel;

import java.lang.reflect.Type;

public interface ParcelCreator<T> {
    void writeToParcel(Parcel parcel, T target, Type type, Class<T> rawType) throws Exception;

    T readFromParcel(Parcel parcel, Type type, Class<T> rawType) throws Exception;
}