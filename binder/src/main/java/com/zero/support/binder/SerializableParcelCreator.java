package com.zero.support.binder;

import android.os.Parcel;
import android.util.Log;

import java.lang.reflect.Type;

public class SerializableParcelCreator<T>  implements ParcelCreator<T>{
    @Override
    public void writeToParcel(Parcel parcel, T target, Type type, Class<T> rawType) throws Exception {
        String json  =Json.toJson(target,type);
        Log.e("xgf", "writeToParcel: "+json );
        parcel.writeString(Json.toJson(target,type));
    }

    @Override
    public T readFromParcel(Parcel parcel, Type type, Class<T> rawType) throws Exception {
        String json = parcel.readString();
        Log.e("xgf", "readFromParcel: "+json );
        return Json.fromJson(json,type);
    }
}
