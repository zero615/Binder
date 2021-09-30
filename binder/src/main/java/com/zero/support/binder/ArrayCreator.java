package com.zero.support.binder;

import android.os.Parcel;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

@SuppressWarnings("ALL")
class ArrayCreator implements ParcelCreator<Object> {

    @Override
    public void writeToParcel(Parcel parcel, Object object, Type type, Class<Object> rawType) throws Exception {
        if (object == null) {
            parcel.writeInt(-1);
            return;
        }
        int len = Array.getLength(object);
        parcel.writeInt(len);
        Class<?> subType = rawType.getComponentType();
        ParcelCreator creator = Binder.getParcelCreator(subType);
        if (creator == null) {
            throw new RuntimeException("not found creator for " + subType);
        }
        for (int i = 0; i < len; i++) {
            creator.writeToParcel(parcel, Array.get(object, i), subType, subType);
        }
    }

    @Override
    public Object readFromParcel(Parcel parcel, Type type, Class<Object> rawType) throws Exception {
        int N = parcel.readInt();
        if (N == -1) {
            return null;
        }
        Class<?> subType = rawType.getComponentType();
        if (subType == null) {
            return null;
        }
        Object list = Array.newInstance(subType, N);

        ParcelCreator creator = Binder.getParcelCreator(subType);
        if (creator == null) {
            throw new RuntimeException("not found creator for " + subType);
        }
        for (int i = 0; i < N; i++) {
            Array.set(list, i, creator.readFromParcel(parcel, subType, subType));
        }
        return list;
    }
}