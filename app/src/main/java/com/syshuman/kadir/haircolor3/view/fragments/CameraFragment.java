package com.syshuman.kadir.haircolor3.view.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;


public class CameraFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2 {

    static {
        OpenCVLoader.initDebug();
    }

    private Context context;

    Mat mHaystack;

    int match_method = Imgproc.TM_CCORR_NORMED;

    Scalar RED = new Scalar(255,0,0);
    Scalar GRE = new Scalar(0.255,0);
    Scalar BLE = new Scalar(0,0,255);

    @BindView(R.id.camera_view)
    SurfaceView surfaceView;
    private CameraBridgeViewBase mOpenCvCameraView;


    public CameraFragment() {
        // Required empty public constructor
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

        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        ButterKnife.bind(this, view);
        mOpenCvCameraView = (CameraBridgeViewBase) surfaceView;
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
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, context, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this.getActivity()) {
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


    public boolean match(int needleResource) {
        Mat mNeedle = new Mat();
        Bitmap needle = BitmapFactory.decodeResource(getResources(), needleResource);
        Utils.bitmapToMat(needle, mNeedle);


        Mat result = new Mat(mNeedle.rows(), mNeedle.cols(), CvType.CV_32FC1);
        Imgproc.matchTemplate(mHaystack, mNeedle, result, match_method);
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        Point matchLoc = mmr.maxLoc;
        if(mmr.maxVal>0.80) {
            Imgproc.rectangle(mHaystack, matchLoc, new Point(matchLoc.x + mNeedle.cols(), matchLoc.y + mNeedle.rows()), RED);

            Rect rect1 = new Rect(100,100,280,280);
            Imgproc.rectangle(mHaystack, rect1.tl(), rect1.br(), new Scalar(255, 0, 0), 2, 8, 0);
            Log.d("MATCH", "SUCC Max Val = " + mmr.maxVal + " " + mmr.minVal + " Res ID : " + needleResource);
            return true;
        } else {
            Log.d("MATCH", "FAIL Max Val = " + mmr.maxVal + " " + mmr.minVal + " res ID : " + needleResource);
            return false;
        }

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mHaystack = inputFrame.rgba();

        if(match(R.raw.left_up))
            if(match(R.raw.left_down))
                if(match(R.raw.right_up))
                    if(match(R.raw.right_down)) {
                        Log.d("MATCH", "ALL MATCHED");
                    }
        return mHaystack;
    }
}
