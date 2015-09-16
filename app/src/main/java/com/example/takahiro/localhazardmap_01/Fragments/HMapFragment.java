package com.example.takahiro.localhazardmap_01.fragments;

import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

public class HMapFragment extends MapFragment {

    private static GoogleMap g_map;
    private CameraPosition center_pos = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        if(g_map == null) g_map = getMap();
        if(g_map != null) {
            g_map.setMyLocationEnabled(true);
            g_map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location curr_location) {
                    LatLng curr_latlng = new LatLng(curr_location.getLatitude(), curr_location.getLongitude());
                    g_map.moveCamera(CameraUpdateFactory.newLatLngZoom(curr_latlng, 15));
                }
            });
            g_map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng point) {
                }
            });
            center_pos = g_map.getCameraPosition();
        }
    }

    private class PostLocation extends HttpPost {
    }
    private class GetMap extends HttpGet {
    }
}

