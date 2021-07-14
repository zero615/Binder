package com.zero.support.binder;

import android.os.IBinder;

public interface BinderCreator<T> {
    T asInterface(IBinder binder, Class<? extends T> cls);

    IBinder asBinder(T t, Class<? extends T> cls);
}