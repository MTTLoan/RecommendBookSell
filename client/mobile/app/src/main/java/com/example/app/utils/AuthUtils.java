package com.example.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AuthUtils {
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_TOKEN = "token";

    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_TOKEN, token).apply();
        Log.d("AuthUtils", "Token saved: " + token.substring(0, 10) + "...");
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(KEY_TOKEN, null);
        Log.d("AuthUtils", "Token retrieved: " + (token != null ? token.substring(0, 10) + "..." : "null"));
        return token;
    }

    public static void clearToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_TOKEN).apply();
        Log.d("AuthUtils", "Token cleared");
    }
}