package com.fortysevendeg.translace_bubble.ui.commons;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import dagger.ObjectGraph;

import java.util.List;

public abstract class BaseService extends Service {

    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.create(getModules().toArray());
        objectGraph.inject(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected abstract List<Object> getModules();

}
