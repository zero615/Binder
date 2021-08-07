package com.zero.support.sample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.zero.support.binder.Binder;

import java.util.HashMap;
import java.util.Map;

public class TestService  extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return Binder.asBinder(new ITest(){
            @Override
            public Map<String, TestObject> getTestObject() {
                return new HashMap<>();
            }

            @Override
            public String getTest() {
                return "xxxx";
            }

            @Override
            public String[] getTests() {
                return new String[]{"1","2"};
            }

            @Override
            public TestGeneralObject getTestGeneralObject() {
                TestGeneralObject testGeneralObject =  new TestGeneralObject();
                testGeneralObject.test="test";
                return testGeneralObject;
            }
        },ITest.class);
    }
}
