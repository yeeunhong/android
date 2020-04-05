package com.inka.example.myapp01;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("MyApp01.realm")
                .schemaVersion(0)
                .build();

        Realm.setDefaultConfiguration(config);
    }
}
