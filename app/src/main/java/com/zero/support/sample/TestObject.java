package com.zero.support.sample;

import android.os.Parcel;
import android.os.Parcelable;

public class TestObject implements Parcelable {
    public String test;


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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
