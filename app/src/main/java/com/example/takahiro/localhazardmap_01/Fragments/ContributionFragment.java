package com.example.takahiro.localhazardmap_01.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
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

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ContributionFragment extends Fragment {

    private Camera camera;
    private SurfaceView photo_view;
    private SurfaceHolder holder;

    private boolean photo_is_taken = false;
    private byte[] photo;
    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contribution, container, false);

        final EditText title_editer = (EditText)view.findViewById(R.id.title_editer);
        final EditText description_editer = (EditText)view.findViewById(R.id.description_editer);

        // Set button for send request to "contribution/postContribution".
        Button send_btn = (Button)view.findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Double> user_location = GpsManager.getLocation();
                if(user_location != null ) {
                    String photo_data = photo != null ? new String(photo, Charset.forName("ISO-8859-1")) : "";
                    new PostContribution().execute(new String[]{String.valueOf(Constants.ID), Constants.PW, String.valueOf(user_location.get("latitude")), String.valueOf(user_location.get("longitude")), title_editer.getText().toString(), description_editer.getText().toString(), photo_data});
                } else {
                    Toast.makeText(getActivity(), "投稿機能を利用するにはGPS設定をONにしてください\n(既にONならばGPSが位置を検出するまでお待ちください)", Toast.LENGTH_LONG).show();
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

