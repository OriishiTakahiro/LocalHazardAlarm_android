package com.example.takahiro.localhazardmap_01.utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.example.takahiro.localhazardmap_01.BaseActivity;

import java.util.HashMap;

/**
 * Created by takahiro on 西暦15/08/22.
 */
public class GpsManager extends Service {

    private final int MINTIME_FOR_NOTIFICATION = 6000;
    private final int MINLENGTH_FOR_NOTIFICATION = 10;

    private LocationManager location_manager;
    private LocationListener location_listener;
    private static HashMap<String, Double> user_location = null;


    public static HashMap<String, Double> getLocation() {
        return user_location;
    }

    @Override
    public void onCreate() {
        this.location_listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                    user_location.put("latitude", location.getLatitude());
                    user_location.put("longitude", location.getLongitude());
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
        user_location = new HashMap<String,Double>();
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
    public void onDestroy() {
        location_manager.removeUpdates(this.location_listener);
    }

    public void notificateMessageToUser(String message, int icon) {
        Notification notif = new Notification();
        notif.icon = icon;
        notif.tickerText = message;
        notif.defaults |= Notification.DEFAULT_VIBRATE;
        notif.defaults |= Notification.DEFAULT_LIGHTS;
        Intent intent = new Intent(getApplicationContext(), BaseActivity.class);
        NotificationManager notif_manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notif_manager.notify(1, notif);
    }

}