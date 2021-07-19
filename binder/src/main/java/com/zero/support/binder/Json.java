package com.zero.support.binder;

import com.zero.support.binder.BinderObject.ValueObjectCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class Json {
    private static final Map<Class<?>, ObjectCreator<?>> creators = new HashMap<>();
    private static final ValueObjectCreator value = new ValueObjectCreator();
    private static final BinderObject.FieldObjectCreator field = new BinderObject.FieldObjectCreator();

    static {
        BinderObject.ListObjectCreator creator = new BinderObject.ListObjectCreator();
        registerCreator(List.class, creator);
        registerCreator(ArrayList.class, creator);
        registerCreator(Array.class, new BinderObject.ArrayObjectCreator());
        registerCreator(Map.class, new BinderObject.MapObjectCreator());
    }

    @SuppressWarnings("all")
    public static <T> T fromJson(String json, Type type) throws BinderException {
        if (json == null) {
            return null;
        }
        BinderObject object;
        try {
            if (json.startsWith("[")) {
                object = new BinderObject(new JSONArray(json));
            } else {
                object = new BinderObject(new JSONObject(json));
            }
        } catch (JSONException e) {
            throw new BinderException(e.getMessage());
        }
        return (T) getCreator(type).createObject(object, type, getRawClass(type));
    }

    @SuppressWarnings("all")
    public static String toJson(Object value, Type type) throws BinderException {
        if (value == null) {
            return null;
        }
        BinderObject object = new BinderObject();
        getCreator(value.getClass()).writeBinderObject(object, value, type, getRawClass(type));
        if (object.getValue() == null) {
            return null;
        }
        return String.valueOf(object.getValue());
    }

    public static void registerCreator(Class<?> cls, ObjectCreator<?> creator) {
        creators.put(cls, creator);
    }

    static ObjectCreator getCreator(Type type) {
        Class<?> cls = getRawClass(type);
        if (cls.isArray()) {
            return creators.get(Array.class);
        } else if (cls.isPrimitive()) {
            return value;
        } else if (cls == String.class) {
            return value;
        } else {
            ObjectCreator<?> creator = creators.get(cls);
            if (creator == null) {
                creator = field;
            }
            return creator;
        }
    }


    static Class getRawClass(Type type) {
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            return (Class<?>) type;
        }
    }

    static Type getSubType(Type type, int index) {
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            if (types.length <= index) {
                return null;
            }
            return types[index];
        } else {
            return null;
        }
    }

    interface ObjectCreator<T> {
        void writeBinderObject(BinderObject object, T t, Type type, Class<T> rawCls) throws BinderException;

        T createObject(BinderObject object, Type type, Class<T> rawCls) throws BinderException;
    }


}
