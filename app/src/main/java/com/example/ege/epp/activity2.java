package com.example.ege.epp;

/**
 * Created by ege on 11.2.2017.
 */

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.view.Display;
import android.view.SurfaceView;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.Core.flip;
public class activity2 extends Activity implements CameraBridgeViewBase.CvCameraViewListener2{


        private static String TAG = "MainActivity";
        JavaCameraView javaCameraView;
        Mat mRgba;
        Mat mRgbaT;
        Mat mRgbaF;

        static {
            if(OpenCVLoader.initDebug()){
                Log.i(TAG,"Opencv loaded successfully");
            }
            else{
                Log.i(TAG,"Opencv not loaded");
            }
        }

        BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case BaseLoaderCallback.SUCCESS:{
                        javaCameraView.enableView();
                        javaCameraView.setMaxFrameSize(200,200);
                        break;
                    }
                    default:{
                        super.onManagerConnected(status);
                        break;
                    }
                }
                super.onManagerConnected(status);
            }
        };
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main2);


            javaCameraView = (JavaCameraView)findViewById(R.id.java_camera_view);
            javaCameraView.setVisibility(SurfaceView.VISIBLE);
            javaCameraView.setCvCameraViewListener(this);


        }

        @Override
        protected void onPause(){
            super.onPause();
            if(javaCameraView!=null)
                javaCameraView.disableView();
        }

        @Override
        protected void onDestroy(){
            super.onDestroy();
            if(javaCameraView!=null)
                javaCameraView.disableView();
        }

        @Override
        protected void onResume(){
            super.onResume();
            if(OpenCVLoader.initDebug()) {
                Log.i(TAG, "Opencv loaded successfully");
                mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            }
            else {
                Log.i(TAG, "Opencv not loaded");
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0,this,mLoaderCallBack);
            }
        }

        @Override
        public void onCameraViewStarted(int width, int height) {
            mRgba = new Mat(height, width, CvType.CV_8UC4);
            mRgbaT = new Mat(height, width, CvType.CV_8UC4);
            mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        }

        @Override
        public void onCameraViewStopped() {

        }

        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            mRgba = inputFrame.rgba();
            Core.transpose(mRgba,mRgbaT);
            Imgproc.resize(mRgbaT,mRgbaF,mRgbaF.size(),0,0,0);
            Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int screenOrientation = display.getRotation();

            switch (screenOrientation%360) {
                case 0:
                    Core.flip(mRgbaF, mRgba, 1);
                    //textView.setText("0degree");
                    break;
                case 90:
                    Core.flip(mRgbaF, mRgba, 1);
                    // textView.setText("90degree");
                    break;
                case 180:
                    Core.flip(mRgbaF, mRgba, -1);
                    break;
                case 270:
                    Core.flip(mRgbaF, mRgba, 0);
                    //textView.setText("270degree");
                    break;
                default:
                    break;
            }
            return mRgba;

        }

    }

