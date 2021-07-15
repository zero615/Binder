package com.zero.support.sample;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import com.zero.support.binder.Binder;

public class DebugActivity extends ButtonActivity {
    private ITest test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addButton("bind", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindService(new Intent(v.getContext(), TestService.class), new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        test = Binder.asInterface(service,ITest.class);
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {

                    }
                }, Service.BIND_AUTO_CREATE);
            }
        });
        addButton("invoke test", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButton(test.getTest(),null);
            }
        });
        addButton("invoke getMap", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButton(test.getTestObject().toString(),null);
            }
        });
    }
}
