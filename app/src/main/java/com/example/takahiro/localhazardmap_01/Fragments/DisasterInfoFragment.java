package com.example.takahiro.localhazardmap_01.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.takahiro.localhazardmap_01.R;


public class DisasterInfoFragment extends Fragment {

    private String title;
    private String description;

    public DisasterInfoFragment setInfo(String title, String description) {
        this.title = title;
        this.description = description;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_disaster_info, container, false);

        TextView title_view = (TextView) view.findViewById(R.id.disas_info_title);
        title_view.setText(this.title);

        TextView description_view = (TextView) view.findViewById(R.id.disas_info_description);
        description_view.setText(this.description);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
