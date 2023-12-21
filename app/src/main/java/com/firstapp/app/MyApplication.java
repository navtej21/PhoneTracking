package com.firstapp.app;
import android.app.Application;
import android.location.Location;
import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private static MyApplication singleton;
    private List<Location> mylocations;

    public List<Location> getMylocations() {
        return mylocations;
    }

    public void setMylocations(List<Location> mylocations) {
        this.mylocations = mylocations;
    }

    public static MyApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        mylocations = new ArrayList<>();
    }
}
