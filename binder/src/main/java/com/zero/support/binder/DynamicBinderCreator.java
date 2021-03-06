package com.zero.support.binder;

import android.os.IBinder;

import com.zero.support.binder.Binder;
import com.zero.support.binder.BinderCreator;

class DynamicBinderCreator<T> implements BinderCreator<T> {

    @Override
    public T asInterface(IBinder binder, Class<? extends T> cls) {
        return Binder.asInterface(binder, cls);
    }

    @Override
    public IBinder asBinder(T t, Class<? extends T> cls) {
        return Binder.asBinder(t, cls);
    }
}
