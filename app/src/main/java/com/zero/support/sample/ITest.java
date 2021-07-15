package com.zero.support.sample;

import com.zero.support.binder.BinderName;

import java.util.Map;

public interface ITest {
    @BinderName("getTestObject")
    public Map<String,TestObject> getTestObject();

    @BinderName("getTest")
    String getTest();
}
