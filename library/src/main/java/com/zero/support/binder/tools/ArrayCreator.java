package com.zero.support.binder.tools;

import android.os.Parcel;

import com.zero.support.binder.Binder;
import com.zero.support.binder.ParcelCreator;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

@SuppressWarnings("ALL")
public class ArrayCreator implements ParcelCreator<Object[]> {

    @Override
    public void writeToParcel(Parcel parcel, Object[] object, Type type, Class<Object[]> rawType) throws Exception {
        Object[] target = (Object[]) object;
        if (object == null) {
            parcel.writeInt(-1);
            return;
        }
        parcel.writeInt(target.length);
        Class<?> subType = rawType.getComponentType();
        ParcelCreator creator = Binder.getParcelCreator(subType);
        if (creator == null) {
            throw new RuntimeException("not found creator for " + subType);
        }
        for (Object o : target) {
            creator.writeToParcel(parcel, o, subType, subType);
        }
    }

    @Override
    public Object[] readFromParcel(Parcel parcel, Type type, Class<Object[]> rawType) throws Exception {
        int N = parcel.readInt();
        if (N == -1) {
            return null;
        }
        Class<?> subType = rawType.getComponentType();
        if (subType == null) {
            return null;
        }
        Object[] list = (Object[]) Array.newInstance(subType, N);

        ParcelCreator creator = Binder.getParcelCreator(subType);
        if (creator == null) {
            throw new RuntimeException("not found creator for " + subType);
        }
        for (int i = 0; i < N; i++) {
            list[i] = (creator.readFromParcel(parcel, subType, subType));
        }
        return list;
    }
}