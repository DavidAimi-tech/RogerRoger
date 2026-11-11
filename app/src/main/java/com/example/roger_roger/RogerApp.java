package com.example.roger_roger;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class RogerApp extends Application implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        GameAudio.get(this);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        GameAudio.get(this).screenResumed(!(activity instanceof BattleActivity));
    }

    @Override
    public void onActivityPaused(Activity activity) {
        GameAudio.get(this).screenPaused();
    }

    @Override public void onActivityCreated(Activity a, Bundle b) { }
    @Override public void onActivityStarted(Activity a) { }
    @Override public void onActivityStopped(Activity a) { }
    @Override public void onActivitySaveInstanceState(Activity a, Bundle b) { }
    @Override public void onActivityDestroyed(Activity a) { }
}