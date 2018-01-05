package com.syshuman.kadir.haircolor3.view.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.syshuman.kadir.haircolor3.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.bitmap;
import static android.content.ContentValues.TAG;


public class CameraFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2 {

    private Context context;

    @BindView(R.id.camera_view) SurfaceView surfaceView;
    private CameraBridgeViewBase mOpenCvCameraView;


    public CameraFragment() {
        // Required empty public constructor
    }

    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        return fragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_camera, container, false);

        ButterKnife.bind(this, view);

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this.getActivity(),  mLoaderCallback);


        mOpenCvCameraView = (CameraBridgeViewBase) surfaceView;


/*
        Canvas canvas = mOpenCvCameraView.getHolder().lockCanvas();
        canvas.rotate(90,0,0);
        float scale = canvas.getWidth() / (float) mOpenCvCameraView.getHeight();
        float scale2 = canvas.getHeight() / (float) mOpenCvCameraView.getWidth();
        if(scale2 > scale){
            scale = scale2;
        }
        if (scale != 0) {
            canvas.scale(scale, scale,0,0);
        }
        canvas.drawBitmap(bitmap, 0, -bitmap.getHeight(), null);
        */


        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        return view;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, context, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this.getActivity())  {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Toast.makeText(context, "OpenCV loaded ", Toast.LENGTH_LONG).show();
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    Toast.makeText(context, "OpenCV load failed. Please install from GooglePlay ", Toast.LENGTH_LONG).show();
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();

    }
}
