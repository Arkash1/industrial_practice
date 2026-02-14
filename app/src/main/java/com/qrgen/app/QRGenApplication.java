package com.qrgen.app;

import android.app.Application;

import com.qrgen.app.utils.ThemeManager;

public class QRGenApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ThemeManager.applyTheme(this);
    }
}