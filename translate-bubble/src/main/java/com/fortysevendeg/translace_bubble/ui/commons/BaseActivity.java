package com.fortysevendeg.translace_bubble.ui.commons;

import android.app.Activity;
import android.os.Bundle;
import dagger.ObjectGraph;

import java.util.List;

public abstract class BaseActivity extends Activity {

    private ObjectGraph activityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGraph = ObjectGraph.create(getModules().toArray());
        activityGraph.inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityGraph = null;
    }

    protected abstract List<Object> getModules();
}