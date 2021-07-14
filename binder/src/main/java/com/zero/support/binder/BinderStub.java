package com.zero.support.binder;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.util.WeakHashMap;

class BinderStub extends android.os.Binder implements IInterface {
    private final static WeakHashMap<Object, BinderStub> localBinders = new WeakHashMap<>();
    private final Object target;
    private final ClassHolder holder;

    private BinderStub(Object target, Class<?> cls) {
        this.target = target;
        this.holder = new ClassHolder(cls, false);
        attachInterface(this, holder.getName());
    }

    public static android.os.Binder peek(Object object) {
        synchronized (localBinders) {
            return localBinders.get(object);
        }
    }

    public static android.os.Binder of(Object object, Class<?> cls) {
        if (object == null) {
            return null;
        }
        synchronized (localBinders) {
            BinderStub binder = localBinders.get(object);
            if (binder == null) {
                binder = new BinderStub(object, cls);
                localBinders.put(object, binder);
            }
            return binder;
        }
    }

    @SuppressWarnings("all")
    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (code == INTERFACE_TRANSACTION) {
            reply.writeString(holder.getName());
            return true;
        } else if (code != IBinder.FIRST_CALL_TRANSACTION) {
            return false;
        }
        String name = data.readString();
        MethodHolder method = holder.getBinderMethod(name);
        if (method == null) {
            return super.onTransact(code, data, reply, flags);
        }
        try {
            Object[] params = new Object[method.types.length];
            for (int i = 0; i < method.types.length; i++) {
                ParcelCreator creator = Binder.getParcelCreator(method.params[i]);
                params[i] = creator.readFromParcel(data, method.types[i], method.params[i]);
            }
            Object object = method.method.invoke(target, params);
            reply.writeNoException();
            if (method.returnType != Void.class) {
                ParcelCreator creator = Binder.getParcelCreator(method.returnCls);
                creator.writeToParcel(reply, object, method.returnType, method.returnCls);
            }

        } catch (Exception e) {
            reply.writeException(e);
        }
        return true;
    }


    @Override
    public IBinder asBinder() {
        return this;
    }


}