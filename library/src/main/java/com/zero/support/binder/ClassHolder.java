package com.zero.support.binder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ClassHolder {
    private final Map<String, MethodHolder> methods;
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
    }

    public String getName() {
        return name;
    }

    public MethodHolder getBinderMethod(String name) {
        return methods.get(name);
    }
}