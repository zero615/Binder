package com.zero.support.binder;

import android.os.IBinder;
import android.util.SparseArray;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class Binder {
    private static final Map<Class<?>, BinderCreator<?>> creators = new HashMap<>();

    private static final Map<Type, ParcelCreator> parcelCreators = new HashMap<>();
    private static final EntranceParcelCreator entranceParcelCreator = new EntranceParcelCreator();
    private static final BinderCreator<Object> DEFAULT_CREATOR = new DynamicBinderCreator<>();

    static {
        registerParcelCreator(Array.class, new ArrayCreator());
        registerParcelCreator(List.class, new ListParcelCreator());
        registerParcelCreator(SparseArray.class, new SparseArrayParcelCreator());
        registerParcelCreator(Map.class, new MapParcelCreator());
        registerParcelCreator(File.class, new FileParcelCreator());
        registerParcelCreator(BinderSerializable.class,EntranceParcelCreator.GENERAL);
    }


    public static void registerBinderCreator(Class<?> cls, BinderCreator<?> creator) {
        creators.put(cls, creator);
    }

    public static void registerParcelCreator(Class<?> cls, ParcelCreator<?> creator) {
        parcelCreators.put(cls, creator);
    }



    public static BinderCreator<?> getBindCreator(Class<?> cls) {
        BinderCreator<?> creator = creators.get(cls);
        if (creator == null) {
            creator = DEFAULT_CREATOR;
        }
        return creator;
    }


    public static ParcelCreator<?> getParcelCreator(Class<?> cls) {
        ParcelCreator<?> creator = parcelCreators.get(cls);
        if (creator == null) {
            creator = entranceParcelCreator;
        }
        return creator;
    }

    public static IBinder peekBinder(Object object) {
        if (object == null) {
            return null;
        }
        return BinderProxy.peekBinder(object);
    }

    public static IBinder asBinder(Object object, Class<?> cls) {
        return BinderStub.of(object, cls);
    }

    public static <T> T asInterface(IBinder binder, Class<T> cls) {
        return BinderProxy.asInterface(binder, cls);
    }

}
