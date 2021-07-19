package com.zero.support.binder;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class FieldHolder {
    public String name;
    public Field field;
    public Type type;
    public Class<?> cls;

    public FieldHolder(Field field) {
        this.field = field;
        this.type = field.getGenericType();
        this.cls = field.getType();
        BinderName nameAno = field.getAnnotation(BinderName.class);
        if (nameAno != null) {
            this.name = nameAno.value();
        } else {
            this.name = field.getName();
        }
    }

    public Object get(Object o) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return field.get(o);
        } catch (Throwable e) {
            return null;
        }
    }

    public void set(Object o, Object value) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(o, value);
        } catch (Throwable e) {
            return;
        }
    }
}