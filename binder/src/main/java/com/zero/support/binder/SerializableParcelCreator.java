package com.zero.support.binder;

import android.os.Parcel;

import java.lang.reflect.Type;

public class SerializableParcelCreator<T>  implements ParcelCreator<T>{
    @Override
    public void writeToParcel(Parcel parcel, T target, Type type, Class<T> rawType) throws Exception {
        parcel.writeString(Json.toJson(target,type));
    }

    @Override
    public T readFromParcel(Parcel parcel, Type type, Class<T> rawType) throws Exception {
        return Json.fromJson(parcel.readString(),type);
    }
}
