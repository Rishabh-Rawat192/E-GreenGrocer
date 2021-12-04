package com.rwtcompany.onlinevegitableshopapp;


import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.database.FirebaseDatabase;

public class App extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
