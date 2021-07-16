package com.zero.support.sample;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import com.zero.support.binder.Binder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
                        test = Binder.asInterface(service, ITest.class);
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
                addButton(test.getTest(), null);
            }
        });
        addButton("invoke getMap", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButton(test.getTestObject().toString(), null);
            }
        });
        addButton("lib", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButton("x"+getApplicationInfo().nativeLibraryDir,null);
            }
        });
        addButton("testCopy", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getApplicationInfo().sourceDir);
                try {
                    InputStream inputStream = new FileInputStream(file);
                    OutputStream stream = new FileOutputStream(new File(getCacheDir(), "test.apk"));
                    FileUtils.copy(inputStream, stream);
                    inputStream.close();
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
