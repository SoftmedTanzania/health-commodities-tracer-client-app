package com.softmed.stockapp.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.softmed.stockapp.Activities.LoginActivity;

public class SessionManager {

    private static final String KEY_NAME = "name";
    private static final String KEY_UUID = "uuid";
    private static final String IS_FIRST_LOGIN = "IsFirstLogin";
    private static final String KEY_LOCATION_ID = "locationId";
    private static final String USER_PASS = "userPassword";
    // Sharedpref file name
    private static final String PREF_NAME = "HFRPref";

    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_IS_DISTRICT_USER = "IsDistrictUser";

    // Shared Preferences
    private SharedPreferences pref;
    // Editor for Shared preferences
    private SharedPreferences.Editor editor;
    // Context
    private Context _context;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        // Shared pref mode
        int PRIVATE_MODE = 0;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String name, int personUUID, String pass, int locationId, boolean isDistrictUser) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_UUID, String.valueOf(personUUID));

        //Storing user password
        editor.putString(USER_PASS, pass);

        //Storing Loation ID
        editor.putInt(KEY_LOCATION_ID, locationId);

        //Storing Loation ID
        editor.putBoolean(KEY_IS_DISTRICT_USER, isDistrictUser);

        // commit changes
        editor.apply();
    }

    public String getUserUUID() {
        return pref.getString(KEY_UUID, null);
    }

    public int getFacilityId() {
        return pref.getInt(KEY_LOCATION_ID, -1);
    }
    public void  setFacilityId(int facilityId) {
        editor.putInt(KEY_LOCATION_ID, facilityId);
        editor.commit();
    }
    public String getUserName() {
        return pref.getString(KEY_NAME, null);
    }

    public String getUserPass() {
        return pref.getString(USER_PASS, null);
    }

    public boolean getIsFirstLogin() {
        return pref.getBoolean(IS_FIRST_LOGIN, true);
    }

    public void setIsFirstLogin(boolean isFirstLogin) {
        editor.putBoolean(IS_FIRST_LOGIN, isFirstLogin);
        editor.commit();
    }

    public void  setKeyIsDistrictUser(boolean isDistrictUser) {
        editor.putBoolean(KEY_IS_DISTRICT_USER, isDistrictUser);
        editor.commit();
    }
    public  boolean getKeyIsDistrictUser() {
        return pref.getBoolean(KEY_IS_DISTRICT_USER, false);
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);

            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

        }

    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

}