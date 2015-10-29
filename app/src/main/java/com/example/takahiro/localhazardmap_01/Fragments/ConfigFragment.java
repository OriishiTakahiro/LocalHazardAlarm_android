package com.example.takahiro.localhazardmap_01.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.takahiro.localhazardmap_01.R;
import com.example.takahiro.localhazardmap_01.entity.Constants;
import com.example.takahiro.localhazardmap_01.utility.DBAccesor;
import com.example.takahiro.localhazardmap_01.utility.GetHttp;
import com.example.takahiro.localhazardmap_01.utility.GpsManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


public class ConfigFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private static String[] config_items = {"ENABLE_GPS"};

    private Switch gps_activator;
    private ArrayList<CheckBox> org_list;

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
        this.gps_activator = (Switch) view.findViewById(R.id.switch_gps_activator);
        this.gps_activator.setOnCheckedChangeListener(this);
        this.gps_activator.setChecked(pref_entity.getBoolean(Constants.NOTIF_ENABLED, false));
        this.gps_activator.setSwitchTypeface(Typeface.DEFAULT_BOLD, Typeface.ITALIC);

        // Generate checkbox list.
        this.org_list = new ArrayList<CheckBox>();
        LinearLayout linear_layout = (LinearLayout) view.findViewById(R.id.org_list);

        String[] list = getActivity().getApplicationContext().getResources().getStringArray(R.array.ORG_RANK);
        ArrayList<String> org_rank_list = new ArrayList<String>(Arrays.asList(list));

        for (String org_rank : org_rank_list) {
            final CheckBox tmp = new CheckBox(getActivity().getApplicationContext());
            tmp.setText(org_rank);
            tmp.setChecked(pref_entity.getBoolean(org_rank, false));
            final String tmp_rank = org_rank;
            tmp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pref_editor.putBoolean(tmp_rank, !pref_entity.getBoolean(tmp_rank, false));
                    pref_editor.commit();
                }
            });
            tmp.setTextColor(Color.BLACK);
            tmp.setVisibility(View.VISIBLE);
            linear_layout.addView(tmp);
            org_list.add(tmp);
        }

        Spinner risk_rank_spinner = (Spinner) view.findViewById(R.id.num_spinner);
        risk_rank_spinner.setSelection(pref_entity.getInt(Constants.PREF_RANK_NOTIF, 1));
        risk_rank_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                String item = (String) spinner.getSelectedItem();
                pref_editor.putInt(Constants.PREF_RANK_NOTIF, position);
                pref_editor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        pref_editor.putBoolean(Constants.NOTIF_ENABLED, isChecked);
        pref_editor.commit();
        if (isChecked) {
            getActivity().startService(new Intent(getActivity(), GpsManager.class));
            Toast.makeText(getActivity(), "GPS start", Toast.LENGTH_SHORT).show();
        } else {
            getActivity().stopService(new Intent(getActivity(), GpsManager.class));
            HashMap<String, Double> location = GpsManager.getLocation();
            if(location != null) Toast.makeText(getActivity(), location.get("latitude") + "\n" + location.get("longitude"), Toast.LENGTH_SHORT).show();
        }
    }
}
