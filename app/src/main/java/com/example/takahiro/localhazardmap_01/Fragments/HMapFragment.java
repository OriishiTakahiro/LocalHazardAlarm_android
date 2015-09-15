package com.example.takahiro.localhazardmap_01.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.takahiro.localhazardmap_01.R;
import com.example.takahiro.localhazardmap_01.utility.GpsManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;


public class HMapFragment extends Fragment {

    private static GoogleMap g_map;
    private MapFragment map_frag;
    private FragmentTransaction frag_transaction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // これをやっておかないとCameraFactoryのstatics関数の実行時にCameraFactory is not initializedエラーが出る
        try{
            MapsInitializer.initialize(getActivity());
        }
        catch(Exception e){
            Log.v("test","Error: "+e);
        }

        //動的に生成したMapFragmentはgetMap()が使えない
        this.map_frag = new MapFragment() {
            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                while(map_frag.getMap()==null) {
                    try { Thread.sleep(100); } catch(InterruptedException error) {}
                    Log.d("test",map_frag + " 0");
                    Log.d("test",map_frag.getMap() + " 1");
                }
                if(g_map==null) {
                    g_map = map_frag.getMap();
                    g_map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange(Location curr_location) {
                            LatLng curr_latlng = new LatLng(curr_location.getLatitude(), curr_location.getLongitude());
                            g_map.moveCamera(CameraUpdateFactory.newLatLng(curr_latlng));
                        }
                    });
                }
                Log.d("test","exec");
            }
        };
        //setMapLocation(33.9252, 134.6470, 200f);
        Log.d("test", g_map + " 2");
        this.frag_transaction = getActivity().getFragmentManager().beginTransaction();
        this.frag_transaction.add(R.id.hmap_frame, this.map_frag,"MAP");
        this.frag_transaction.commit();

        /*
        this.map_frag = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.hmap));
        Log.d("test", (getActivity().getFragmentManager().findFragmentById(R.id.hmap) == null) + "");
        if(savedInstanceState == null) {
            this.map_frag.setRetainInstance(true);
        }else {
            g_map = this.map_frag.getMap();
        }
        if(g_map == null) {
           g_map = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.hmap)).getMap();
        }
        */
    }

    @Override
    public void onResume() {
        /*
        super.onResume();
        HashMap<String, Double> location = GpsManager.getLocation();
        if(location != null) {
            setMapLocation(location.get("latitude"), location.get("longitude"), 200f);
        }
        */
        super.onResume();
        if(g_map != null) {
            g_map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location curr_location) {
                    LatLng curr_latlng = new LatLng(curr_location.getLatitude(), curr_location.getLongitude());
                    g_map.moveCamera(CameraUpdateFactory.newLatLng(curr_latlng));
                }

            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hmap , container, false);

        /*
        this.frag_manager = getFragmentManager();
        this.frag_transaction = frag_manager.beginTransaction();
        this.frag_transaction.replace(R.id.hmap_frame, this.map_frag);
        this.frag_transaction.addToBackStack(null);
        this.frag_transaction.commit();
        */

        // Inflate the layout for this fragment
        return view;
    }

    public void setMapLocation(double latitude, double longitude, float scale) {
        CameraPosition camerapos = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(scale).bearing(0).build();
        Log.d("test", g_map + " 3");
        if(g_map!=null) {
            g_map.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));
            Log.d("test", g_map.getCameraPosition().toString() + " 4");
        }
    }

}
