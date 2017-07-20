package helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sankalp on 20/1/17.
 */

public class SharedPrefs {
    private static SharedPrefs sharedPrefs;

    public static final String IS_LOGGED_IN="is_logged_in";
    public static final String USER_NAME="user_name";
    private static final String PROFILE_PIC = "PROFILE_PIC";


    SharedPreferences prefs;
    SharedPreferences.Editor editor;


    public SharedPrefs() {
        Context c = MyApplication.getINSTANCE();
        editor = c.getSharedPreferences(c.getPackageName(), Context.MODE_PRIVATE).edit();
        prefs = c.getSharedPreferences(c.getPackageName(), Context.MODE_PRIVATE);
    }


    public void save(String id,String value)
    {
        editor.putString(id,value).commit();
    }
    public void save(String id,Boolean value)
    {
        editor.putBoolean(id,value).commit();
    }
    public void save(String id,int value)
    {
        editor.putInt(id,value).commit();
    }
    public void save(String id,long value)
    {
        editor.putLong(id,value).commit();
    }

    public String getString(String id)
    {
        return prefs.getString(id,"");
    }
    public Long getLong(String id)
    {
        return prefs.getLong(id,0);
    }
    public int getInt(String id)
    {
        return prefs.getInt(id,0);
    }
    public boolean getBoolean(String id)
    {
        return prefs.getBoolean(id,false);
    }


    public void logout()
    {
        editor.clear().commit();
    }
    public static SharedPrefs getPrefs()
    {
        if(sharedPrefs==null)
        {
            sharedPrefs=new SharedPrefs();
        }
        return sharedPrefs;
    }


    public boolean isloggedIn(){
        boolean valid=true;

        if (sharedPrefs.getString(USER_NAME).isEmpty())
        {
            valid=false;
        }

        return valid;
    }



    public void setPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener)
    {
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void removeListener(SharedPreferences.OnSharedPreferenceChangeListener listener)
    {
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}