package com.example.takahiro.localhazardmap_01.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.takahiro.localhazardmap_01.R;
import com.example.takahiro.localhazardmap_01.utility.GpsManager;

import java.util.HashMap;


public class ConfigFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private static String[] config_items = {"ENABLE_GPS"};

    private Switch gps_activator;
    private SharedPreferences.Editor pref_editor;
    private SharedPreferences pref_entity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_config, container, false);

        this.pref_entity = PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.pref_editor = this.pref_entity.edit();

        //Set ToggleSwitch that setting to activate gps.
        gps_activator = (Switch)view.findViewById(R.id.switch_gps_activator);
        gps_activator.setOnCheckedChangeListener(this);
        gps_activator.setSwitchTypeface(Typeface.DEFAULT_BOLD, Typeface.ITALIC);
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            getActivity().startService(new Intent(getActivity(), GpsManager.class));
            Toast.makeText(getActivity(), "GPS start", Toast.LENGTH_SHORT).show();
        }
        else {
            getActivity().stopService(new Intent(getActivity(), GpsManager.class));
            HashMap<String, Double> location = GpsManager.getLocation();
            Toast.makeText(getActivity(),location.get("latitude") +  "\n" + location.get("longitude"), Toast.LENGTH_SHORT).show();
        }
    }

}
