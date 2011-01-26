package de.swagner.homeinvasion;

import android.content.Context;

public class HomeInvasionApp extends android.app.Application {

    private static HomeInvasionApp instance;

    public HomeInvasionApp() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }
}
