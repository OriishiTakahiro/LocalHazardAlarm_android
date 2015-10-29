package com.example.takahiro.localhazardmap_01.utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.takahiro.localhazardmap_01.BaseActivity;
import com.example.takahiro.localhazardmap_01.R;
import com.example.takahiro.localhazardmap_01.entity.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by takahiro on 西暦15/08/22.
 */
public class GpsManager extends Service {

    private SharedPreferences.Editor pref_editor;
    private SharedPreferences pref_entity;

    // minimum time interval between location updates, in milliseconds
    private final int MINTIME_FOR_NOTIFICATION = 180000;
    // minimum distance between location updates, in meters
    private final int MINLENGTH_FOR_NOTIFICATION = 10;

    private LocationManager location_manager;
    private LocationListener location_listener;
    private static HashMap<String, Double> user_location = null;
    private int notif_id = 0;

    private NotificationManager notif_manager = null;


    public static HashMap<String, Double> getLocation() { return user_location; }

    @Override
    public void onCreate() {

        this.pref_entity = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.pref_editor = this.pref_entity.edit();

        String[] list = getApplicationContext().getApplicationContext().getResources().getStringArray(R.array.ORG_RANK);
        ArrayList<String> org_rank_list = new ArrayList<String>(Arrays.asList(list));
        String enabled_rank_list = "[";
        for(int i=0;i < org_rank_list.size();i++ ) {
            enabled_rank_list += pref_entity.getBoolean(org_rank_list.get(i),false) ? (org_rank_list.size()-i-1) + "," : "";
        }
        enabled_rank_list = enabled_rank_list.replaceAll(",$","]");
        enabled_rank_list = enabled_rank_list.replaceAll(",$", "]");

        final String tmp_level = pref_entity.getInt(Constants.PREF_RANK_NOTIF,1) + "";
        final String tmp_rank = enabled_rank_list;

        this.location_listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(user_location == null) user_location = new HashMap<String,Double>();
                user_location.put("latitude", location.getLatitude());
                user_location.put("longitude", location.getLongitude());
                pref_editor.putFloat("latitude", (float)location.getLatitude());
                pref_editor.putFloat("longitude", (float)location.getLongitude());
                pref_editor.commit();
                new PostLocation().execute(String.valueOf(Constants.ID), Constants.PW, String.valueOf(user_location.get("latitude")), String.valueOf(user_location.get("longitude")), tmp_rank, tmp_level);
            }
            @Override
            public void onProviderDisabled(String provider) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
        };
        this.location_manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        this.location_manager.requestLocationUpdates(this.location_manager.GPS_PROVIDER, MINTIME_FOR_NOTIFICATION, MINLENGTH_FOR_NOTIFICATION, this.location_listener);
        boolean gps_flag = this.location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() { location_manager.removeUpdates(this.location_listener); }

    public void notificateMessageToUser(ArrayList<String> ticker_texts, ArrayList<String> texts, int icon) {
        if(this.notif_manager==null) this.notif_manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        else this.notif_manager.cancelAll();
        Notification notif = new Notification();
        Intent intent = new Intent(getApplicationContext(), BaseActivity.class);
        PendingIntent pend_intent = PendingIntent.getActivity(getApplicationContext(),0,intent,0);
        for(int i = 0;i < ticker_texts.size();i++) {
            notif.icon = icon;
            notif.tickerText = ticker_texts.get(i);
            notif.vibrate = new long[]{1000, 200, 700, 200, 400, 200};
            notif.number = notif_id;
            notif.setLatestEventInfo(getApplicationContext(), ticker_texts.get(i), texts.get(i), pend_intent);
            this.notif_manager.notify(this.notif_id++, notif);
        }
    }

    // WrapperClass for accessor
    private class PostLocation extends PostHttp {
        public PostLocation() {
            super(Constants.SCHEME, Constants.AUTHORITY, "location/postLocation", new ArrayList<String>(Arrays.asList("id", "pw", "latitude", "longitude", "rank", "risk_level")));
        }
        @Override
        protected void onPostExecute(String response) {
            try {
                JSONArray warnings = new JSONObject(response).getJSONArray("response");
                ArrayList<String> titles = new ArrayList<String>();
                ArrayList<String> descriptions = new ArrayList<String>();
                for(int i = 0;i < warnings.length();i++) {
                    titles.add(warnings.getJSONObject(i).getString("name"));
                    descriptions.add(warnings.getJSONObject(i).getString("description"));
                }
                notificateMessageToUser(titles, descriptions, R.drawable.icon_notify);
            } catch(JSONException error) {
                Log.d("error", error.toString());
            }
        }
    }

}