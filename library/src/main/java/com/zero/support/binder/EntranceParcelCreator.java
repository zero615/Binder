package com.zero.support.binder;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

import com.zero.support.binder.Binder;
import com.zero.support.binder.BinderCreator;
import com.zero.support.binder.BinderException;
import com.zero.support.binder.BinderSerializable;
import com.zero.support.binder.ParcelCreator;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

@SuppressWarnings("all")
class EntranceParcelCreator implements ParcelCreator<Object> {

    @Override
    public void writeToParcel(Parcel reply, Object object, Type type, Class rawType) throws Exception {
        if (rawType.isInterface()) {
            if (object == null) {
                reply.writeStrongBinder(null);
            } else {
                BinderCreator creator = Binder.getBindCreator(rawType);
                reply.writeStrongBinder(creator.asBinder(object, rawType));
            }
        } else if (rawType.isPrimitive()) {
            if (rawType == int.class) {
                reply.writeInt((Integer) object);
            } else if (rawType == float.class) {
                reply.writeFloat((float) object);
            } else if (rawType == double.class) {
                reply.writeDouble((double) object);
            } else if (rawType == boolean.class) {
                reply.writeInt(((boolean) object) ? 1 : 0);
            } else if (rawType == byte.class) {
                reply.writeByte((byte) object);
            } else if (rawType == long.class) {
                reply.writeLong((long) object);
            } else if (rawType == char.class) {
                reply.writeInt((char) object);
            } else if (rawType == short.class) {
                reply.writeInt((short) object);
            }
        } else if (rawType == String.class) {
            reply.writeString((String) object);
        } else if (Bundle.class.isAssignableFrom(rawType)) {
            reply.writeBundle((Bundle) object);
        } else if (rawType == (IBinder.class)) {
            reply.writeStrongBinder((IBinder) object);
        } else {
            ParcelCreator creator = getCreator(rawType);
            if (creator != null) {
                creator.writeToParcel(reply, object, type, rawType);
            } else if (Parcelable.class.isAssignableFrom(rawType)) {
                reply.writeParcelable((Parcelable) object, 0);
            } else {
                throw new BinderException("not found creator for " + rawType);
            }
        }
    }

    @Override
    public Object readFromParcel(Parcel data, Type type, Class rawType) throws Exception {
        Object object;
        if (rawType.isInterface()) {
            IBinder binder = data.readStrongBinder();
            if (binder == null) {
                object = null;
            } else {
                BinderCreator creator = Binder.getBindCreator(rawType);
                object = creator.asInterface(binder, rawType);
            }
        } else if (rawType.isPrimitive()) {
            if (rawType == int.class) {
                object = data.readInt();
            } else if (rawType == float.class) {
                object = data.readFloat();
            } else if (rawType == double.class) {
                object = data.readDouble();
            } else if (rawType == boolean.class) {
                object = data.readInt() == 1;
            } else if (rawType == byte.class) {
                object = data.readByte();
            } else if (rawType == long.class) {
                object = data.readLong();
            } else if (rawType == char.class) {
                object = (char) data.readInt();
            } else if (rawType == short.class) {
                object = (short) data.readInt();
            } else {
                throw new BinderException("not found creator for " + rawType);
            }
        } else if (rawType == String.class) {
            object = data.readString();
        } else if (Bundle.class.isAssignableFrom(rawType)) {
            object = data.readBundle();
        } else if (rawType == (IBinder.class)) {
            object = data.readStrongBinder();
        } else {
            ParcelCreator creator = getCreator(rawType);
            if (creator != null) {
                object = creator.readFromParcel(data, type, rawType);
            } else if (Parcelable.class.isAssignableFrom(rawType)) {
                object = data.readParcelable(rawType.getClassLoader());
            } else {
                throw new BinderException("not found creator for " + rawType);
            }
        }
        return object;
    }

    @SuppressWarnings("all")
    static ParcelCreator getCreator(Class<?> rawType) {
        ParcelCreator creator = null;
        if (BinderSerializable.class.isAssignableFrom(rawType)) {
            creator = Binder.getParcelCreator(BinderSerializable.class);
        } else if (rawType.isArray()) {
            creator = Binder.getParcelCreator(Array.class);
        }
        if (creator == null) {
            creator = Binder.getParcelCreator(rawType);
        }
        return creator;
    }
}
