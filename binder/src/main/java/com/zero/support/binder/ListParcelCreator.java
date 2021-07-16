package com.zero.support.binder;

import android.os.Parcel;

import com.zero.support.binder.Binder;
import com.zero.support.binder.ParcelCreator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class ListParcelCreator implements ParcelCreator<List<?>> {

    @Override
    public void writeToParcel(Parcel parcel, List<?> target, Type type, Class<List<?>> rawType) throws Exception {
        ParameterizedType parameterizedType = (ParameterizedType) type;

        Type[] types = parameterizedType.getActualTypeArguments();
        if (types.length == 0) {
            parcel.writeList(target);
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
        for (Object o : target) {
            creator.writeToParcel(parcel, o, types[0], (Class<?>) types[0]);
        }
    }

    @Override
    public List<?> readFromParcel(Parcel parcel, Type type, Class<List<?>> rawType) throws Exception {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        if (types.length == 0) {
            return parcel.readArrayList(getClass().getClassLoader());
        }

        int N = parcel.readInt();
        if (N == -1) {
            return null;
        }
        List list = new ArrayList();
        ParcelCreator creator = Binder.getParcelCreator((Class<?>) types[0]);
        if (creator == null) {
            throw new RuntimeException("not found creator for " + types[0]);
        }
        for (int i = 0; i < N; i++) {
            list.add(creator.readFromParcel(parcel, types[0], (Class<?>) types[0]));
        }
        return list;
    }
}