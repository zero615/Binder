
package com.zero.support.binder;

import android.os.Parcel;
import android.util.SparseArray;

import com.zero.support.binder.Binder;
import com.zero.support.binder.ParcelCreator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
class SparseArrayParcelCreator implements ParcelCreator<SparseArray<?>> {

    @Override
    public void writeToParcel(Parcel parcel, SparseArray<?> target, Type type, Class<SparseArray<?>> rawType) throws Exception {
        ParameterizedType parameterizedType = (ParameterizedType) type;

        Type[] types = parameterizedType.getActualTypeArguments();
        if (types.length == 0) {
            parcel.writeSparseArray(target);
            return;
        }
        if (target == null) {
            parcel.writeInt(-1);
            return;
        }
        parcel.writeInt(target.size());
        ParcelCreator creator = Binder.getParcelCreator((Class<?>) types[0]);
        if (creator == null) {
            throw new RuntimeException("not found creator for " + types[0]);
        }
        for (int i = 0; i < target.size(); i++) {
            parcel.writeInt(target.keyAt(i));
            creator.writeToParcel(parcel, target.valueAt(i), types[0], (Class<?>) types[0]);
        }
    }

    @Override
    public SparseArray<?> readFromParcel(Parcel parcel, Type type, Class<SparseArray<?>> rawType) throws Exception {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        if (types.length == 0) {
            return parcel.readSparseArray(getClass().getClassLoader());
        }

        int N = parcel.readInt();
        if (N == -1) {
            return null;
        }
        SparseArray array = new SparseArray();
        ParcelCreator creator = Binder.getParcelCreator((Class<?>) types[0]);
        if (creator == null) {
            throw new RuntimeException("not found creator for " + types[0]);
        }
        for (int i = 0; i < N; i++) {
            array.put(parcel.readInt(),creator.readFromParcel(parcel, types[0], (Class<?>) types[0]));
        }
        return array;
    }
}