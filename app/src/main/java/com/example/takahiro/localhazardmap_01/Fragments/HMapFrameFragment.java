package com.example.takahiro.localhazardmap_01.fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.takahiro.localhazardmap_01.R;

import java.util.ArrayList;

public class HMapFrameFragment extends Fragment {

    // --- there lists is called by HMapFragment
    static protected ListView disaster_list = null;
    static protected ArrayList<String> descriptions = null;
    // ---

    private int old_position = -1;
    private FragmentTransaction frag_transaction = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hmap_frame, container, false);

        disaster_list = (ListView) view.findViewById(R.id.hmap_frame_disas_list);
        disaster_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter_view, View view, int position, long id) {
                ListView list = (ListView) adapter_view;
                swapFragment(false, position);
            }
        });

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swapFragment(true, 0);
    }

    private void swapFragment(boolean newfrag_is_map, int position) {
        // http://yuyakaido.hatenablog.com/entry/2014/02/16/230947
        this.frag_transaction = getChildFragmentManager().beginTransaction();
        if (newfrag_is_map || old_position == position) {
            this.frag_transaction.replace(R.id.hmap_frame, new HMapFragment());
            this.old_position = -1;
        } else {
            this.frag_transaction.replace(R.id.hmap_frame, new DisasterInfoFragment().setInfo(
                    (String)disaster_list.getItemAtPosition(position), descriptions.get(position)
            ));
            this.old_position = position;
        }
        this.frag_transaction.addToBackStack(null);
        this.frag_transaction.commit();
    }

}
