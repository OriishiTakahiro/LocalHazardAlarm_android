package com.example.takahiro.localhazardmap_01.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;


public class HMapFragment extends Fragment {

    private static GoogleMap g_map;
    private  MapFragment map_frag;
    private FragmentManager frag_manager;
    private FragmentTransaction frag_transaction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //動的に生成したMapFragmentはgetMap()が使えない
        this.map_frag = new MapFragment() {
        @Override
        public void  onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                g_map = map_frag.getMap();
            }
        };
        setMapLocation(33.9252, 134.6470, 200f);
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
        super.onResume();
        HashMap<String, Double> location = GpsManager.getLocation();
        if(location != null) {
            setMapLocation(location.get("latitude"), location.get("longitude"), 200f);
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
        this.g_map.animateCamera(CameraUpdateFactory.newCameraPosition(camerapos));
    }

}
