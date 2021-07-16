
package com.zero.support.binder;

import android.os.Parcel;

import com.zero.support.binder.Binder;
import com.zero.support.binder.ParcelCreator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
class MapParcelCreator implements ParcelCreator<Map<?, ?>> {


    @Override
    public void writeToParcel(Parcel parcel, Map<?, ?> target, Type type, Class<Map<?, ?>> rawType) throws Exception {
        ParameterizedType parameterizedType = (ParameterizedType) type;

        Type[] types = parameterizedType.getActualTypeArguments();
        if (types.length == 0) {
            parcel.writeMap(target);
            return;
        }
        if (target == null) {
            parcel.writeInt(-1);
            return;
        }
        parcel.writeInt(target.size());
        ParcelCreator keyCreator = Binder.getParcelCreator((Class<?>) types[0]);
        ParcelCreator valueCreator = Binder.getParcelCreator((Class<?>) types[1]);
        if (keyCreator == null || valueCreator == null) {
            throw new RuntimeException("not found creator for " + types[0] + " " + types[1]);
        }
        for (Map.Entry<?, ?> entry : target.entrySet()) {
            keyCreator.writeToParcel(parcel, entry.getKey(), types[0], (Class<?>) types[0]);
            valueCreator.writeToParcel(parcel, entry.getValue(), types[1], (Class) types[1]);
        }
    }

    @Override
    public Map<?, ?> readFromParcel(Parcel parcel, Type type, Class<Map<?, ?>> rawType) throws Exception {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        if (types.length == 0) {
            return parcel.readHashMap(getClass().getClassLoader());
        }

        int N = parcel.readInt();
        if (N == -1) {
            return null;
        }
        Map map = new HashMap(N);
        ParcelCreator keyCreator = Binder.getParcelCreator((Class<?>) types[0]);
        ParcelCreator valueCreator = Binder.getParcelCreator((Class<?>) types[1]);
        if (keyCreator == null || valueCreator == null) {
            throw new RuntimeException("not found creator for " + types[0]);
        }
        for (int i = 0; i < N; i++) {
            map.put(keyCreator.readFromParcel(parcel, types[0], (Class<?>) types[0]), valueCreator.readFromParcel(parcel, types[1], (Class<?>) types[1]));
        }
        return map;
    }
}