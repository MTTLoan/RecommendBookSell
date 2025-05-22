package com.example.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AuthUtils {
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_IDENTIFIER = "identifier";
    private static final String KEY_USER_ID = "user_id";

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

    public static void saveUserId(Context context, int userId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_USER_ID, userId).apply();
        Log.d("AuthUtils", "User ID saved: " + userId);
    }

    public static int getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int userId = prefs.getInt(KEY_USER_ID, -1); // -1 là giá trị mặc định nếu không tìm thấy
        Log.d("AuthUtils", "User ID retrieved: " + userId);
        return userId;
    }

    public static void clearUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_USER_ID).apply();
        Log.d("AuthUtils", "User ID cleared");
    }
}