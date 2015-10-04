package com.example.takahiro.localhazardmap_01.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.takahiro.localhazardmap_01.R;
import com.example.takahiro.localhazardmap_01.entity.Constants;
import com.example.takahiro.localhazardmap_01.entity.WarningInfo;
import com.example.takahiro.localhazardmap_01.utility.GetHttp;

import com.example.takahiro.localhazardmap_01.utility.PostHttp;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class HMapFragment extends MapFragment {

    private GoogleMap g_map;
    private boolean uninitialized = true;
    private String enabled_rank_list = "";

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
                    if (uninitialized) {
                        LatLng curr_latlng = new LatLng(curr_location.getLatitude(), curr_location.getLongitude());
                        g_map.moveCamera(CameraUpdateFactory.newLatLngZoom(curr_latlng, 15));
                        uninitialized = false;
                    }
                }
            });
            g_map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng point) {
                    new PostLocation().execute(new String[]{String.valueOf(Constants.ID), Constants.PW, String.valueOf(point.latitude), String.valueOf(point.longitude), enabled_rank_list, "1"});
                }
            });
        }

        SharedPreferences pref_entity = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String[] list = getActivity().getApplicationContext().getResources().getStringArray(R.array.ORG_RANK);
        ArrayList<String> org_rank_list = new ArrayList<String>(Arrays.asList(list));
        enabled_rank_list = "[";
        for(int i=0;i < org_rank_list.size();i++ ) {
            enabled_rank_list += pref_entity.getBoolean(org_rank_list.get(i),false) ? (org_rank_list.size()-i-1) + "," : "";
        }
        enabled_rank_list = enabled_rank_list.replaceAll(",$","]");
        new GetMap().execute(new String[]{enabled_rank_list.replaceAll(",$", "]")});
    }

    private class GetMap extends GetHttp {
        private GetMap() {
            super(Constants.SCHEME, Constants.AUTHORITY, "layer/getMap", new ArrayList<String>(Arrays.asList("request")));
        }
        @Override
        protected void onPostExecute(String response) {
            ArrayList<ArrayList<LatLng>> polygons = new ArrayList<ArrayList<LatLng>>();
            try {
                JSONArray warnings = new JSONObject(response).getJSONArray("response");
                for(int i=0;i < warnings.length();i++) {
                    ArrayList<LatLng> apexes = new ArrayList<LatLng>();
                    JSONArray columns = warnings.getJSONArray(i);
                    for(int j=0;j < columns.getJSONArray(1).length();j++) {
                        String latitude = columns.getJSONArray(1).getJSONObject(j).keys().next();
                        apexes.add(new LatLng(Double.parseDouble(latitude), columns.getJSONArray(1).getJSONObject(j).getDouble(latitude)));
                    }
                    polygons.add(apexes);
                }

            } catch(JSONException e) {
                Log.d("error", e.toString());
            }
            for(int i=0;i < polygons.size();i++) {
                if(polygons.get(i).size() == 1) {
                    g_map.addCircle(new CircleOptions()
                            .center(polygons.get(i).get(0))
                            .radius(50)
                            .fillColor(Color.argb(50, 200, 200, 255))
                            .strokeColor(Color.BLACK)
                            .strokeWidth(1)
                    );
                } else {
                    PolygonOptions polygon_op = new PolygonOptions();
                    for (LatLng apexe : polygons.get(i)) polygon_op.add(apexe);
                    polygon_op.strokeColor(Color.BLACK);
                    polygon_op.strokeWidth(1);
                    polygon_op.fillColor(Color.argb(50, 255, 210, 210));
                    g_map.addPolygon(polygon_op);
                }
            }
        }
    }
    private class PostLocation extends PostHttp {
        private PostLocation() {
            super(Constants.SCHEME, Constants.AUTHORITY, "location/postLocation", new ArrayList<String>(Arrays.asList("id", "pw", "latitude", "longitude","rank","risk_level")));
        }
        @Override
        public void onPostExecute(String response) {
            try {
                Log.d("test",response);
                JSONArray warnings = new JSONObject(response).getJSONArray("response");
                HMapFrameFragment.war_info_list = new ArrayList<WarningInfo>();
                LinkedList<String> titles = new LinkedList<String>();
                for(int i = 0;i < warnings.length();i++) {
                    JSONObject warning = warnings.getJSONObject(i);
                    WarningInfo tmp_warning = new WarningInfo(
                            warning.getString("name"),
                            warning.getString("description"),
                            warning.getString("org"),
                            warning.getInt("risk_level"),
                            warning.has("img") ? warning.getString("img") : null
                        );
                    HMapFrameFragment.war_info_list.add(tmp_warning);
                }
                Collections.sort(HMapFrameFragment.war_info_list, new WarningInfo.WarningComparatorDecOrderByRisk());
                for(WarningInfo tmp : HMapFrameFragment.war_info_list) {
                    titles.add(tmp.risk_level + " | " + tmp.title + " (" + tmp.organization + ")");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, titles.toArray(new String[0]));
                HMapFrameFragment.disaster_list.setAdapter(adapter);
            } catch(JSONException error) {
                Log.d("error", error.toString());
            }
        }
    }
}

