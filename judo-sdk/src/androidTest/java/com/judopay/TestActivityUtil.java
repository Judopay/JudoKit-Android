package com.judopay;

import android.app.Activity;
import android.os.Looper;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitor;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import java.util.Collection;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

public class TestActivityUtil {

    public static Activity getCurrentActivity() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return getCurrentActivityOnMainThread();
        } else {
            final Activity[] topActivity = new Activity[1];
            getInstrumentation().runOnMainSync(() -> topActivity[0] = getCurrentActivityOnMainThread());
            return topActivity[0];
        }
    }

    private static Activity getCurrentActivityOnMainThread() {
        ActivityLifecycleMonitor registry = ActivityLifecycleMonitorRegistry.getInstance();
        Collection<Activity> activities = registry.getActivitiesInStage(Stage.RESUMED);
        return activities.iterator().hasNext() ? activities.iterator().next() : null;
    }
}