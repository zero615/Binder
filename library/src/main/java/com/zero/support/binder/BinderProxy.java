package com.zero.support.binder;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

class BinderProxy implements InvocationHandler, IBinder.DeathRecipient {
    private final IBinder remote;
    private final ClassHolder holder;
    private static final Map<IBinder, Object> localBinders = new HashMap<>();

    public static IBinder peekBinder(Object object) {
        synchronized (localBinders) {
            for (Map.Entry<IBinder, Object> entry : localBinders.entrySet()) {
                if (entry.getValue() == object) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public BinderProxy(IBinder remote, Class<?> cls) throws RemoteException {
        this.remote = remote;
        this.holder = new ClassHolder(cls, true);
        String descriptor = remote.getInterfaceDescriptor();
        if (!TextUtils.equals(descriptor, holder.getName())) {
            throw new BinderException("not exist " + descriptor);
        }
        remote.linkToDeath(this, 0);
    }

    @SuppressWarnings("all")
    public static <T> T asInterface(IBinder remote, Class<T> cls) {
        if (remote == null) {
            return null;
        }
        synchronized (localBinders) {
            Object object = localBinders.get(remote);
            if (object == null) {
                try {
                    object = Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, new BinderProxy(remote, cls));
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return null;
                }
                localBinders.put(remote, object);
            }
            return (T) object;
        }
    }


    @Override
    @SuppressWarnings("all")
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            MethodHolder methodHolder = holder.getBinderMethod(method.getName());
            data.writeString(methodHolder.name);
            Type[] types = methodHolder.types;

            for (int i = 0; i < types.length; i++) {
                ParcelCreator creator = Binder.getParcelCreator(methodHolder.params[i]);
                creator.writeToParcel(data, args[i], methodHolder.types[i], methodHolder.params[i]);
            }
            remote.transact(IBinder.FIRST_CALL_TRANSACTION, data, reply, 0);
            reply.readException();
            if (method.getReturnType() != void.class) {
                ParcelCreator creator = Binder.getParcelCreator(methodHolder.returnCls);
                return creator.readFromParcel(reply, methodHolder.returnType, methodHolder.returnCls);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(String.valueOf(e));
        } finally {
            data.recycle();
            reply.recycle();
        }

    }

    @Override
    public void binderDied() {
        synchronized (localBinders) {
            localBinders.remove(remote);
            remote.unlinkToDeath(this, 0);
        }
    }
}