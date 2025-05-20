package com.example.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AuthUtils {
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_IDENTIFIER = "identifier";

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

    public static void saveIdentifier(Context context, String identifier) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_IDENTIFIER, identifier).apply();
        Log.d("AuthUtils", "Identifier saved: " + identifier);
    }
    public static String getIdentifier(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String identifier = prefs.getString(KEY_IDENTIFIER, null);
        Log.d("AuthUtils", "Identifier retrieved: " + (identifier != null ? identifier : "null"));
        return identifier;
    }
}