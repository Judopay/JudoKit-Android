package com.judopay;

import android.app.Activity;
import android.support.test.internal.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;

import com.android.support.test.deps.guava.collect.Iterables;

import io.appium.droiddriver.UiElement;
import io.appium.droiddriver.finders.By;
import io.appium.droiddriver.finders.Finder;
import io.appium.droiddriver.helpers.BaseDroidDriverTest;
import io.appium.droiddriver.helpers.DroidDrivers;
import io.appium.droiddriver.util.ActivityUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public abstract class DroidDriverTest<T extends Activity> extends BaseDroidDriverTest<T> {

    protected DroidDriverTest(Class<T> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        ActivityUtils.setRunningActivitySupplier(new ActivityUtils.Supplier<Activity>() {
            @Override
            public Activity get() {
                return Iterables.getFirst(ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED), null);
            }
        });

        // For each test method invocation, the Activity will not actually be created
        // until the first time this method is called.
        getActivity();
    }

    String resourceId(int rID) {
        return getTargetContext().getResources().getResourceName(rID);
    }

    protected UiElement find(Finder finder) {
        // Note that we use on to ensure refreshUiElementTree() is called
        // the 'find' method will not refresh the uielementtree
        return DroidDrivers.get().on(finder);
    }

    protected UiElement id(int rID) {
        return find(By.resourceId(resourceId(rID)));
    }

    // DroidDriver can't hide the keyboard to use Espresso for now.
    void hideKeyboard(int viewId) {onView(withId(viewId)).perform(closeSoftKeyboard());}

}