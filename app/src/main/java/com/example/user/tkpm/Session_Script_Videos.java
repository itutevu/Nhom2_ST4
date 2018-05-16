package com.example.user.tkpm;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 12/05/2018.
 */

public class Session_Script_Videos {
    public static final int NUMBER_H = 1000;
    private final String TAG = Session_Script.class.getName();

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;
    private String PREF_NAME = "Session_Script_Videos";

    private static final String KEY_LICHSU = "lichsu";


    public Session_Script_Videos(Context context, String name) {
        this._context = context;
        PREF_NAME = name;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }

    public void createSession(List<HScript> hScripts) {
        if (hScripts == null) {
            //Log.e(TAG + " createLoginSession cuahang = null");
            return;
        }
        Gson gson = new Gson();
        for (int i = 0; i < hScripts.size(); i++) {
            HScript hScript = hScripts.get(i);
            String json = gson.toJson(hScript);
            editor.putString(KEY_LICHSU + i, json);
        }

        editor.commit();
    }

    public List<HScript> getLichSuTimKiemTDList() {
        List<HScript> hScripts = new ArrayList<>();
        Gson gson = new Gson();

        for (int i = 0; i < NUMBER_H; i++) {
            String json = pref.getString(KEY_LICHSU + i, "");
            if (json.isEmpty())
                continue;
            HScript hScript = gson.fromJson(json, HScript.class);
            hScripts.add(hScript);
        }

        return hScripts;
    }

    public void clearData() {
        editor.clear();
        editor.commit();
    }
}
