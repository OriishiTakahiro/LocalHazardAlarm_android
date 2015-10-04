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

import com.example.takahiro.localhazardmap_01.R;
import com.example.takahiro.localhazardmap_01.entity.WarningInfo;

import java.util.ArrayList;

public class HMapFrameFragment extends Fragment {

    // --- there lists is called by HMapFragment
    static protected ListView disaster_list = null;
    static protected ArrayList<WarningInfo> war_info_list = null;
    // ---

    private int old_position = -1;
    private Fragment old_frag = null;
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

    public void backToMap() {
        swapFragment(false, this.old_position);
    }

    private void swapFragment(boolean uninitialized, int position) {
        // http://yuyakaido.hatenablog.com/entry/2014/02/16/230947
        this.frag_transaction = getChildFragmentManager().beginTransaction();
        if (uninitialized) {
            this.frag_transaction.replace(R.id.hmap_frame, new HMapFragment());
            this.old_frag = null;
            this.old_position = -1;
        } else {
            WarningInfo war_info = war_info_list.get(position);
            Fragment new_frag = new DisasterInfoFragment().setInfo(war_info, this);
            if(this.old_frag != null) this.frag_transaction.remove(this.old_frag);
            if(old_position != position) {
                this.frag_transaction.add(R.id.hmap_frame, new_frag);
                this.old_frag = new_frag;
                this.old_position = position;
            } else {
                this.old_position = -1;
            }
        }
        this.frag_transaction.addToBackStack(null);
        this.frag_transaction.commit();
    }

}
