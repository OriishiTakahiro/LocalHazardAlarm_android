package com.example.takahiro.localhazardmap_01.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.takahiro.localhazardmap_01.R;
import com.example.takahiro.localhazardmap_01.entity.Constants;
import com.example.takahiro.localhazardmap_01.utility.DBAccesor;
import com.example.takahiro.localhazardmap_01.utility.GetHttp;
import com.example.takahiro.localhazardmap_01.utility.GpsManager;
import com.google.android.gms.drive.widget.DataBufferAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class ConfigFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private static String[] config_items = {"ENABLE_GPS"};

    private Switch gps_activator;
    private ArrayList<CheckBox> org_list;

    private DBAccesor db_accesor;
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
        this.gps_activator = (Switch)view.findViewById(R.id.switch_gps_activator);
        this.gps_activator.setOnCheckedChangeListener(this);
        this.gps_activator.setSwitchTypeface(Typeface.DEFAULT_BOLD, Typeface.ITALIC);

        this.org_list = new ArrayList<CheckBox>();
        LinearLayout linear_layout = (LinearLayout)view.findViewById(R.id.org_list);
        db_accesor = DBAccesor.getInstance(getActivity().getApplicationContext());
        ArrayList<ArrayList<String>> raws = db_accesor.getRaws(0,null,null,null,null);
        Log.d("test", raws.toString());
        for(ArrayList<String> raw : raws) {
            final int id = Integer.parseInt(raw.get(0));
            final CheckBox tmp = new CheckBox(getActivity().getApplicationContext());
            tmp.setText(raw.get(1));
            tmp.setChecked(raw.get(2).equals("1"));
            tmp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db_accesor.updateRaw("organizations", id, new String[]{"enable"}, tmp.isChecked() ? new String[]{"1"} : new String[]{"0"});
                }

            });
            tmp.setTextColor(Color.BLACK);
            tmp.setVisibility(View.VISIBLE);
            linear_layout.addView(tmp);
            org_list.add(tmp);
        }
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

    private class GetOrgList extends GetHttp {
        public GetOrgList(){
            super(Constants.SCHEME, Constants.AUTHORITY, "location/postLocation", new ArrayList<String>(Arrays.asList("request")));
        }
        @Override
        protected void onPostExecute(String response) {
        }

    }

}
