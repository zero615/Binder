package com.zero.support.binder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class ClassHolder {
    private final Map<String, MethodHolder> methods;
    private final Map<String, FieldHolder> fields;
    private final String name;

    public ClassHolder(Class<?> cls, boolean proxy) {
        Method[] methods = cls.getMethods();
        this.methods = new HashMap<>(methods.length);
        MethodHolder methodHolder;
        BinderName nameAno = cls.getAnnotation(BinderName.class);
        if (nameAno != null) {
            this.name = nameAno.value();
        } else {
            this.name = cls.getName();
        }
        for (Method method : methods) {
            if (proxy) {
                this.methods.put(method.getName(), new MethodHolder(method));
            } else {
                methodHolder = new MethodHolder(method);
                this.methods.put(methodHolder.name, methodHolder);
            }
        }
        Field[] fields = cls.getFields();
        FieldHolder fieldHolder;
        this.fields = new LinkedHashMap<>(fields.length);
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            fieldHolder = new FieldHolder(field);
            this.fields.put(field.getName(), fieldHolder);
        }
    }

    public String getName() {
        return name;
    }

    public MethodHolder getBinderMethod(String name) {
        return methods.get(name);
    }

    public Map<String, FieldHolder> fields() {
        return fields;
    }

    public FieldHolder getBinderField(String name) {
        return this.fields.get(name);
    }
}