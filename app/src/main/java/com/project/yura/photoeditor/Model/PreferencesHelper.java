package com.project.yura.photoeditor.Model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PreferencesHelper {
    private final Context context;
    private final SharedPreferences favoritePref;
    private final SharedPreferences customFilterPref;

    private static final String FAVORITE_FILTERS_PREFERENCES = "FAVORITE_FILTERS_PREFERENCES";
    private static final String CUSTOM_FILTERS_PREFERENCES = "CUSTOM_FILTERS_PREFERENCES";
    private static final String FAVORITE_FILTERS = "favorites";

    public PreferencesHelper(Context context) {
        this.context = context;
        favoritePref = context.getSharedPreferences(FAVORITE_FILTERS_PREFERENCES, Context.MODE_PRIVATE);
        customFilterPref = context.getSharedPreferences(CUSTOM_FILTERS_PREFERENCES, Context.MODE_PRIVATE);
//        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Set<String> getFavoriteFilters() {
        return favoritePref.getStringSet(FAVORITE_FILTERS, new HashSet<String>());
    }

    public void removeFavoriteFilter(String name) {
        Set<String> updatedSet = getFavoriteFilters();

        if (updatedSet.remove(name)) {
            favoritePref.edit().clear().putStringSet(FAVORITE_FILTERS, updatedSet).commit();
        }
    }

    public void addFavoriteFilter(String name) {
        Set<String> updatedSet = getFavoriteFilters();

        if (updatedSet.add(name)) {
            favoritePref.edit().clear().putStringSet(FAVORITE_FILTERS, updatedSet).commit();
        }
    }

    public Map<String, float[]> getCustomFiltersMatrix() {
        /*SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(myObject); // myObject - instance of MyObject
        prefsEditor.putString("MyObject", json);
        prefsEditor.commit();*/

        /*
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        MyObject obj = gson.fromJson(json, MyObject.class);
         */
        Map<String, ?> data = customFilterPref.getAll();
        Object[] names = data.keySet().toArray();

        Map<String, float[]> result = new HashMap<>();

        for (Object name : names) {
            String json = (String)data.get(name);
            Gson gson = new Gson();
            float[] coefficients = gson.fromJson(json, float[].class);
            result.put((String) name, coefficients);
        }
        return result;
    }

    public void addCustomFilter(String name, float[] coefficients) {
        Gson gson = new Gson();
        String json = gson.toJson(coefficients);
        customFilterPref
                .edit()
                .putString(name, json)
                .commit();
    }

    public void removeCustomFilter(String name) {
        customFilterPref
                .edit()
                .remove(name)
                .commit();
    }
}
