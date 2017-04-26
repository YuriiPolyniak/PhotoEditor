package com.project.yura.photoeditor.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class PreferencesHelper {
    private Context context;
    SharedPreferences preferences;

    public static String FAVORITE_FILTERS = "favorites";

    public PreferencesHelper(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Set<String> getFavoriteFilters() {
        return preferences.getStringSet(FAVORITE_FILTERS, new HashSet<String>());
    }

    public void remove(String name) {
        Set<String> updatedSet = getFavoriteFilters();

        if (updatedSet.remove(name)) {
            preferences.edit().clear().putStringSet(FAVORITE_FILTERS, updatedSet).commit();
        }
    }

    public void add(String name) {
        Set<String> updatedSet = getFavoriteFilters();

        if (updatedSet.add(name)) {
            preferences.edit().clear().putStringSet(FAVORITE_FILTERS, updatedSet).commit();
        }
    }
}
