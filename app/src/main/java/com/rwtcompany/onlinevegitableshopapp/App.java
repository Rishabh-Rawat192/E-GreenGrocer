package com.rwtcompany.onlinevegitableshopapp;

import android.app.Application;

import androidx.multidex.MultiDexApplication;

import com.google.firebase.database.FirebaseDatabase;

public class App extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
