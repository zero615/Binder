package com.zero.support.sample;

import java.util.Arrays;
import java.util.List;

public class TestGeneralObject {
    public String test = "xx";
    public List<String> list = Arrays.asList("xx", "xx2");
    public List<TestGeneralObject> generalObjects;
    public TestObject object = new TestObject();

    @Override
    public String toString() {
        return "TestGeneralObject{" +
                "test='" + test + '\'' +
                ", list=" + list +
                ", generalObjects=" + generalObjects +
                ", object=" + object +
                '}';
    }
}
