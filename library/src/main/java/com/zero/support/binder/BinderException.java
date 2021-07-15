package com.zero.support.binder;

import android.os.RemoteException;

public class BinderException extends RemoteException {
    public BinderException() {
    }

    public BinderException(String message) {
        super(message);
    }
}
