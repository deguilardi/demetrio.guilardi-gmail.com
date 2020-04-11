package ca.uqac.truckie.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatDelegate;

public class NightModeController {

    private static final String SP_FILE = "NIGHT_MODE";
    private static final String SP_KEY = "ON_OFF";
    private static final String SP_VALUE_YES = "YES";
    private static final String SP_VALUE_NO = "NO";

    private static Application applicationContext;
    private static boolean onOff = false;

    public static void init(Application applicationContext){
        NightModeController.applicationContext = applicationContext;
        Boolean savedValue = getSavedValue();
        if(savedValue == null){
            onOff = getSystemValue();
        }
        else{
            onOff = savedValue;
        }
        if(onOff){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static boolean isOn(){
        return onOff;
    }

    public static void turnOn(){
        onOff = true;
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        saveValue(SP_VALUE_YES);
    }

    public static void turnOff(){
        onOff = false;
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        saveValue(SP_VALUE_NO);
    }

    private static Boolean getSavedValue(){
        SharedPreferences sp = applicationContext.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        String savedValue = sp.getString(SP_KEY, null);
        return (savedValue == null) ? null : savedValue.equals(SP_VALUE_YES);
    }

    private static void saveValue(String value){
        SharedPreferences sp = applicationContext.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        sp.edit().putString(SP_KEY, value).apply();
    }

    private static boolean getSystemValue(){
        Resources resources = applicationContext.getResources();
        int uiMode = resources.getConfiguration().uiMode;
        int mask = Configuration.UI_MODE_NIGHT_MASK;
        int flag = Configuration.UI_MODE_NIGHT_YES;
        return (uiMode & mask) == flag;
    }
}
