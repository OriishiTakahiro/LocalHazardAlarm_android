package com.example.takahiro.localhazardmap_01.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.takahiro.localhazardmap_01.R;
import com.example.takahiro.localhazardmap_01.entity.WarningInfo;


public class DisasterInfoFragment extends Fragment {

    private WarningInfo warning_info;
    private HMapFrameFragment frame;

    public DisasterInfoFragment setInfo(WarningInfo warning_info, HMapFrameFragment frame) {
        this.warning_info = warning_info;
        this.frame = frame;
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
        title_view.setText(this.warning_info.title);

        TextView organization_view = (TextView) view.findViewById(R.id.disas_info_org);
        organization_view.setText("発令元 : " + this.warning_info.organization);

        TextView posted_date = (TextView) view.findViewById(R.id.posted_date);
        posted_date.setText("投稿日時 : " + this.warning_info.posted_date);

        TextView risk_level_view = (TextView) view.findViewById(R.id.disas_info_risklevel);
        risk_level_view.setText("危険度 : " + this.warning_info.risk_level);

        TextView description_view = (TextView) view.findViewById(R.id.disas_info_description);
        description_view.setText(this.warning_info.description);

        Button back_to_map = (Button) view.findViewById(R.id.back_to_map);
        back_to_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frame.backToMap();
            }

        });

        Bitmap bmp = null;
        if (this.warning_info.img != null) {
            //set config for Image View.
            ImageView img_view = (ImageView) view.findViewById(R.id.disas_info_img);
            img_view.setScaleType(ImageView.ScaleType.CENTER);
            // set Bitmap to ImageView.
            bmp = BitmapFactory.decodeByteArray(this.warning_info.img, 0, this.warning_info.img.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            img_view.setImageBitmap(Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true));
        }

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
