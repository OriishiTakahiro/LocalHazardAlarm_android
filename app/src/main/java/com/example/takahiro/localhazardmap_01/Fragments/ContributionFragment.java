package com.example.takahiro.localhazardmap_01.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.takahiro.localhazardmap_01.R;
import com.example.takahiro.localhazardmap_01.entity.Constants;
import com.example.takahiro.localhazardmap_01.utility.GpsManager;
import com.example.takahiro.localhazardmap_01.utility.PostHttp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ContributionFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contribution, container, false);

        final EditText title_editer = (EditText)view.findViewById(R.id.title_editer);
        final EditText description_editer = (EditText)view.findViewById(R.id.description_editer);

        Button send_btn = (Button)view.findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Double> user_location = GpsManager.getLocation();
                if(user_location != null) {
                    new PostContribution().execute(new String[]{String.valueOf(Constants.ID), Constants.PW, String.valueOf(user_location.get("latitude")), String.valueOf(user_location.get("longitude")), title_editer.getText().toString(), description_editer.getText().toString()});
                } else {
                    Toast.makeText(getActivity(), "投稿機能を利用するにはGPS設定をONにしてください", Toast.LENGTH_LONG).show();
                }
            }

        });

        return view;
    }

    private class PostContribution extends PostHttp {
        private PostContribution() {
            super(Constants.SCHEME, Constants.AUTHORITY, "contribution/postContribution", new ArrayList<String>(Arrays.asList("id","pw","latitude","longitude","title","description")));
        }
        @Override
        protected void onPostExecute(String response) {
            Toast.makeText(getActivity(), response, Toast.LENGTH_LONG);
        }
    }
}

