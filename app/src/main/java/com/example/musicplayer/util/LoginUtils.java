package com.example.musicplayer.util;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginUtils {
    protected final SharedPreferences preferences;
    protected final Context context;
    private final String PREFERENCE_NAME = "login";
    private static final String LOGIN_USER_NAME = "login_user_name";;
    private static final String LOGIN_USER_ACCOUNT = "login_user_account";;

    public LoginUtils(Context context) {
        this.preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.context = context;
    }
    public String getLoginUserName() {
        return preferences.getString(LOGIN_USER_NAME,null);
    }
    public String getLoginUserAccount() {
        return preferences.getString(LOGIN_USER_ACCOUNT,null);
    }
    public void setLoginUser(String account,String name) {
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString(LOGIN_USER_ACCOUNT, account);
        editor.putString(LOGIN_USER_NAME, name);
        editor.commit();
    }
}
