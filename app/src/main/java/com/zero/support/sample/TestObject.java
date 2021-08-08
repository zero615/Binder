package com.zero.support.sample;

import android.os.Parcel;
import android.os.Parcelable;

import com.zero.support.binder.BinderSerializable;

import java.util.Arrays;

public class TestObject implements Parcelable , BinderSerializable {
    public String test="ff";
    public String[] tests=new String[]{"xxx"};

    public TestObject() {
    }

    protected TestObject(Parcel in) {
    }

    public static final Creator<TestObject> CREATOR = new Creator<TestObject>() {
        @Override
        public TestObject createFromParcel(Parcel in) {
            return new TestObject(in);
        }

        @Override
        public TestObject[] newArray(int size) {
            return new TestObject[size];
        }
    };

    @Override
    public String toString() {
        return "TestObject{" +
                "test='" + test + '\'' +
                ", tests=" + Arrays.toString(tests) +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
