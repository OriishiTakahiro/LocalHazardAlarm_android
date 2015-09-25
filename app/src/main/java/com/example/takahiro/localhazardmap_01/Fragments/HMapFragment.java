package com.example.takahiro.localhazardmap_01.fragments;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.takahiro.localhazardmap_01.entity.Constants;
import com.example.takahiro.localhazardmap_01.utility.DBAccesor;
import com.example.takahiro.localhazardmap_01.utility.GetHttp;

import com.example.takahiro.localhazardmap_01.utility.PostHttp;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class HMapFragment extends MapFragment {

    private GoogleMap g_map;
    private boolean uninitialized = true;
    private String enabled_org_list = "";

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
                    new PostLocation().execute(new String[]{String.valueOf(Constants.ID), Constants.PW, String.valueOf(point.latitude), String.valueOf(point.longitude), enabled_org_list});
                }
            });
        }

        DBAccesor db_accesor = DBAccesor.getInstance(null);
        enabled_org_list = "[";
        for(ArrayList<String> raw : db_accesor.getRaws(0,null,"enable=1",null,null)) {
            if(raw.size() == 0) {
                enabled_org_list += "]";
                break;
            }
            enabled_org_list += raw.get(0)+",";
        }
        enabled_org_list = enabled_org_list.replaceAll(",$","]");
        new GetMap().execute(new String[]{enabled_org_list.replaceAll(",$","]")});
    }

    private class GetMap extends GetHttp {
        private GetMap() {
            super(Constants.SCHEME, Constants.AUTHORITY, "layer/getMap", new ArrayList<String>(Arrays.asList("request")));
        }
        @Override
        protected void onPostExecute(String response) {
            Log.d("test",response);
            ArrayList<ArrayList<LatLng>> polygons = new ArrayList<ArrayList<LatLng>>();
            try {
                JSONArray warnings = new JSONObject(response).getJSONArray("response");
                for(int i=0;i < warnings.length();i++) {
                    Log.d("test", "warnings " + warnings);
                    ArrayList<LatLng> apexes = new ArrayList<LatLng>();
                    JSONArray columns = warnings.getJSONArray(i);
                    for(int j=0;j < columns.getJSONArray(1).length();j++) {
                        Log.d("test", "columns " + columns);
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
                            .radius(300)
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
            super(Constants.SCHEME, Constants.AUTHORITY, "location/postLocation", new ArrayList<String>(Arrays.asList("id", "pw", "latitude", "longitude","orgs")));
        }
        @Override
        public void onPostExecute(String response) {
            try {
                JSONArray warnings = new JSONObject(response).getJSONArray("response");
                ArrayList<String> titles = new ArrayList<String>();
                ArrayList<String> descriptions = new ArrayList<String>();
                String message = "";
                for(int i = 0;i < warnings.length();i++) {
                    titles.add(warnings.getJSONObject(i).getString("name"));
                    descriptions.add(warnings.getJSONObject(i).getString("description"));
                    message += titles.get(i) + " : " + descriptions.get(i)+"\n";
                }
                Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
            } catch(JSONException error) {
                Log.d("error", error.toString());
            }
        }
    }
}

