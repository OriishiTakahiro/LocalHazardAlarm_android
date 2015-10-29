package com.example.takahiro.localhazardmap_01.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.takahiro.localhazardmap_01.R;
import com.example.takahiro.localhazardmap_01.entity.Constants;
import com.example.takahiro.localhazardmap_01.utility.GpsManager;
import com.example.takahiro.localhazardmap_01.utility.PostHttp;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ContributionFragment extends Fragment {

    private SharedPreferences pref_entity;

    private Camera camera;
    private SurfaceView photo_view;
    private SurfaceHolder holder;

    private boolean photo_is_taken = false;
    private byte[] photo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contribution, container, false);

        this.pref_entity = PreferenceManager.getDefaultSharedPreferences(getActivity());

        final EditText title_editer = (EditText)view.findViewById(R.id.title_editer);
        final EditText description_editer = (EditText)view.findViewById(R.id.description_editer);

        // Set button for send request to "contribution/postContribution".
        Button send_btn = (Button)view.findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Double> user_location = GpsManager.getLocation();
                String photo_data = photo != null ? new String(photo, Charset.forName("ISO-8859-1")) : "";
                if(user_location != null ) {
                    new PostContribution().execute(new String[]{String.valueOf(Constants.ID), Constants.PW, String.valueOf(user_location.get("latitude")), String.valueOf(user_location.get("longitude")), title_editer.getText().toString(), description_editer.getText().toString(), photo_data});
                } else {
                    double lat = (double) pref_entity.getFloat("latitude", 35.67966f);
                    double lon = (double) pref_entity.getFloat("longitude", 139.7681f);
                    String text = convertCooridnateToAddress(lat, lon);
                    if(text != null) {
                        showDialog(text, lat, lon, new String[]{String.valueOf(Constants.ID), Constants.PW, String.valueOf(lat), String.valueOf(lon), title_editer.getText().toString(), description_editer.getText().toString(), photo_data});
                    } else {
                        Toast.makeText(getActivity(), "投稿機能を利用するにはGPS設定をONにしてください\n(ONの場合GPSが位置を検出するまでお待ちください)", Toast.LENGTH_LONG).show();
                    }
                }
            }

            private void showDialog(String address, final double lat, final double lon, final String[] params) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("GPSでの位置情報取得が完了していません");
                builder.setMessage("前回取得した位置\n" + address + "に投稿しますか？");
                builder.setPositiveButton("送信", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new PostContribution().execute(params);
                    }
                });
                builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }

            private String convertCooridnateToAddress(double lat, double lon) {
                Geocoder g_coder = new Geocoder(getActivity(), Locale.JAPAN);
                try {
                    List<Address> address_list = g_coder.getFromLocation(lat, lon, 1);
                    if(!address_list.isEmpty()) {
                        Address candidate_address = address_list.get(0);
                        StringBuffer address_buf = new StringBuffer();
                        String tmp;
                        for(int i=0;(tmp = candidate_address.getAddressLine(i)) != null;i++) {
                            address_buf.append(tmp + "\n");
                        }
                        tmp = address_buf.toString();
                        return tmp;
                    } else {
                        return null;
                    }
                } catch(Exception e) {
                    return null;
                }
            }

        });

        // Set button for shutter for camera.
        Button shutter_btn = (Button)view.findViewById(R.id.shutter_btn);
        photo_view = (SurfaceView) view.findViewById(R.id.photo_view);
        shutter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(camera != null) {
                    if(!photo_is_taken) {
                        camera.takePicture(shutter_listener, null, picture_litener);
                        photo_is_taken = true;
                    } else {
                        camera.startPreview();
                        photo_is_taken = false;
                    }
                }
            }
        });

        this.holder = photo_view.getHolder();
        this.holder.addCallback(holder_callback);
        this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        return view;
    }

    // When the SurfaceView is holden on display, this callback is called.
    private SurfaceHolder.Callback holder_callback = new SurfaceHolder.Callback(){

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            camera = Camera.open();
            try {
                camera.setPreviewDisplay(holder);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            camera.stopPreview();
            camera.setParameters(getCameraParameters());
            camera.setDisplayOrientation(90);
            camera.startPreview();
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder){
            camera.stopPreview();
            camera.release();
        }

        private Camera.Parameters getCameraParameters() {
            Camera.Parameters camera_params = camera.getParameters();
            List<Camera.Size> previewSizes = camera_params.getSupportedPreviewSizes();
            Camera.Size size = previewSizes.get(0);
            camera_params.setPreviewSize(size.width, size.height);
            setParamPicSize(camera_params, size.width, size.height);
            camera_params.getSupportedPictureSizes();
            return camera_params;
        }

        private void setParamPicSize(Camera.Parameters camera_params, int width, int height) {
            List<Camera.Size> list_supported_pic_sizes = camera_params.getSupportedPictureSizes();
            Camera.Size valid_size = list_supported_pic_sizes.get(0);
            if(list_supported_pic_sizes != null) {
                for(Camera.Size supported_pic_size : list_supported_pic_sizes) {
                    if(Constants.MAX_PIC_SIZE >= Math.max(supported_pic_size.width, supported_pic_size.height)) {
                        valid_size = supported_pic_size;
                        break;
                    }
                }
            }
            camera_params.setPictureSize(valid_size.width, valid_size.height);
        }

    };

    // When the shutter is pushed, this callback is called.
    private Camera.ShutterCallback shutter_listener = new Camera.ShutterCallback()  {
        @Override
        public void onShutter() {
        }
    };

    // After the JPEG image is generated, this callback is called.
    private Camera.PictureCallback picture_litener = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if(data != null) {
                photo = data;
                camera.stopPreview();
            }
        }
    };

    private class PostContribution extends PostHttp {
        private PostContribution() {
            super(Constants.SCHEME, Constants.AUTHORITY, "contribution/postContribution", new ArrayList<String>(Arrays.asList("id","pw","latitude","longitude","title","description","img")));
        }
        @Override
        protected void onPostExecute(String response) {
            Toast.makeText(getActivity(), response, Toast.LENGTH_LONG);
        }
    }
}

