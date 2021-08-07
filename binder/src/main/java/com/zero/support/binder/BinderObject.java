package com.zero.support.binder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BinderObject {
    private Object value;

    public BinderObject(Object o) {
        this.value = o;
    }

    public void reset() {
        value = null;
    }

    public BinderObject() {
    }

    public boolean isArray() {
        return value instanceof JSONArray;
    }

    public boolean isPrimitive() {
        return !isArray() && isObject();
    }

    private boolean isObject() {
        return value instanceof JSONObject;
    }

    JSONArray getJsonArray() {
        return (JSONArray) value;
    }

    JSONObject getJsonObject() {
        return (JSONObject) value;
    }

    public Object getValue() {
        return value;
    }

    void addArrayItem(Object o) {
        if (value == null) {
            value = new JSONArray();
        }
        JSONArray array = (JSONArray) value;
        array.put(o);
    }

    @SuppressWarnings("all")
    static class ArrayObjectCreator implements Json.ObjectCreator<Object[]> {

        @Override
        public void writeBinderObject(BinderObject object, Object[] objects, Type type, Class<Object[]> rawCls) throws BinderException {
            Type subType = Json.getSubType(type, 0);
            if (subType == null) {
                subType = String.class;
            }
            if (objects==null){
                return;
            }
            Class<?> subCls = Json.getRawClass(subType);
            Json.ObjectCreator creator = Json.getCreator(subType);
            for (Object o : objects) {
                BinderObject binderObject = new BinderObject();
                creator.writeBinderObject(binderObject, o, subType, subCls);
                if (binderObject.value != null) {
                    object.addArrayItem(binderObject.value);
                }
            }
        }

        @Override
        public Object[] createObject(BinderObject object, Type type, Class<Object[]> rawCls) throws BinderException {
            if (object==null||object.value==null){
                return null;
            }
            JSONArray array  = object.getJsonArray();
            Class<?> subType = rawCls.getComponentType();
            Object[] result = (Object[]) Array.newInstance(rawCls,array.length());
            BinderObject binderObject = new BinderObject();
            for (int i = 0; i < array.length(); i++) {
                binderObject.reset();
                binderObject.setValue(array.opt(i));
                Json.ObjectCreator creator = Json.getCreator(subType);
                creator.createObject(binderObject, subType, Json.getRawClass(subType));
            }
            return result;
        }
    }

    static class MapObjectCreator implements Json.ObjectCreator<Map> {

        @Override
        public void writeBinderObject(BinderObject object, Map map, Type type, Class<Map> rawCls) throws BinderException {
            if (map == null) {
                return;
            }
            Type subType = Json.getSubType(type, 1);
            if (subType == null) {
                subType = String.class;
            }
            Json.ObjectCreator creator = Json.getCreator(subType);
            Class<?> subClass = Json.getRawClass(subType);
            BinderObject binderObject = new BinderObject();
            for (Object o : map.values()) {
                binderObject.reset();
                creator.writeBinderObject(binderObject, o, subType, subClass);
                object.addArrayItem(binderObject.value);
            }
        }

        @Override
        public Map createObject(BinderObject object, Type type, Class<Map> rawCls) throws BinderException {
            if (object == null || object.value == null) {
                return null;
            }
            Map map = new HashMap();
            if (!object.isArray()) {
                throw new RuntimeException("object is not array");
            }
            JSONObject array = object.getJsonObject();
            BinderObject binderObject = new BinderObject();
            Iterator<String> iterator = array.keys();
            Type subType = Json.getSubType(type, 0);
            if (subType == null) {
                subType = String.class;
            }
            while (iterator.hasNext()) {
                binderObject.reset();
                String key = iterator.next();
                binderObject.setValue(object.opt(key));
                Json.ObjectCreator creator = Json.getCreator(subType);
                map.put(key, creator.createObject(binderObject, subType, Json.getRawClass(subType)));

            }
            return map;
        }
    }

    @SuppressWarnings("all")
    static class ListObjectCreator implements Json.ObjectCreator<List> {
        @Override
        public void writeBinderObject(BinderObject object, List list, Type type, Class<List> rawCls) throws BinderException {
            if (list == null) {
                return;
            }
            Type subType = Json.getSubType(type, 0);
            if (subType == null) {
                subType = String.class;
            }
            Json.ObjectCreator creator = Json.getCreator(subType);
            Class<?> subClass = Json.getRawClass(subType);
            BinderObject binderObject = new BinderObject();
            for (Object o : list) {
                binderObject.reset();
                creator.writeBinderObject(binderObject, o, subType, subClass);
                object.addArrayItem(binderObject.value);
            }
        }


        @Override
        public List createObject(BinderObject object, Type type, Class<List> rawCls) throws BinderException {
            if (object == null || object.value == null) {
                return null;
            }
            List list = new ArrayList();
            if (!object.isArray()) {
                throw new RuntimeException("object is not array");
            }
            JSONArray array = object.getJsonArray();
            BinderObject binderObject = new BinderObject();
            Type subType = Json.getSubType(type, 0);
            if (subType == null) {
                subType = String.class;
            }
            for (int i = 0; i < array.length(); i++) {
                binderObject.reset();
                binderObject.setValue(array.opt(i));
                Json.ObjectCreator creator = Json.getCreator(subType);
                list.add(creator.createObject(binderObject, subType, Json.getRawClass(subType)));
            }
            return list;
        }
    }

    @SuppressWarnings("all")
    static class FieldObjectCreator implements Json.ObjectCreator<Object> {

        @Override
        public void writeBinderObject(BinderObject object, Object o, Type type, Class<Object> rawCls) throws BinderException {
            ClassHolder classHolder = new ClassHolder(rawCls, false);
            BinderObject binderObject = new BinderObject();
            for (FieldHolder holder : classHolder.fields().values()) {
                binderObject.reset();
                Json.ObjectCreator creator = Json.getCreator(holder.type);
                creator.writeBinderObject(binderObject, holder.get(o), holder.type, holder.cls);
                object.put(holder.name, binderObject.value);
            }
        }

        @Override
        public Object createObject(BinderObject object, Type type, Class<Object> rawCls) throws BinderException {
            try {
                if (object.value == null) {
                    return null;
                }
                Object result = rawCls.newInstance();
                ClassHolder classHolder = new ClassHolder(rawCls, false);
                BinderObject binderObject = new BinderObject();

                for (FieldHolder holder : classHolder.fields().values()) {
                    binderObject.reset();
                    binderObject.setValue(object.opt(holder.name));
                    Json.ObjectCreator creator = Json.getCreator(holder.type);
                    Object value = creator.createObject(binderObject, holder.type, holder.cls);
                    if (value != null) {
                        holder.set(result, value);
                    }
                }
                return result;
            } catch (Throwable e) {
                e.printStackTrace();
                throw new BinderException(e.getMessage() + "  for " + rawCls);
            }
        }
    }

    private Object opt(String name) {
        JSONObject object = (JSONObject) value;
        return object.opt(name);
    }

    private void put(String name, Object value) throws BinderException {
        if (this.value == null) {
            this.value = new JSONObject();
        }
        JSONObject jsonObject = (JSONObject) this.value;
        try {
            jsonObject.putOpt(name, value);
        } catch (JSONException e) {
            throw new BinderException(e.getMessage());
        }
    }

    static class ValueObjectCreator implements Json.ObjectCreator {

        @Override
        public void writeBinderObject(BinderObject object, Object o, Type type, Class rawCls) {
            if (rawCls.isPrimitive()) {
                object.setValue(o);
            } else if (rawCls == String.class) {
                object.setValue(String.valueOf(o));
            }
        }

        @Override
        public Object createObject(BinderObject object, Type type, Class rawCls) {
            if (rawCls.isPrimitive()) {
                return object.value;
            } else if (rawCls == String.class) {
                return String.valueOf(object.value);
            }
            return null;
        }
    }

    private void setValue(Object o) {
        value = o;
    }


}
