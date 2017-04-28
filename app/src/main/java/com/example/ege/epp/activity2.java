package com.example.ege.epp;

/**
 * Created by ege on 11.2.2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt4;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import android.view.View.OnClickListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Vector;

import static android.R.attr.data;
import static android.R.attr.lines;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static org.opencv.core.Core.flip;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.Canny;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.HoughLines;
import static org.opencv.imgproc.Imgproc.HoughLinesP;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.line;
import static org.opencv.imgproc.Imgproc.medianBlur;

public class activity2 extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {


    private static String TAG = "MainActivity";
    JavaCameraView javaCameraView;
    Mat mRgba;
    Mat mRgbaT;
    Mat mRgbaS;
    Mat mRgbaF;
    Mat edge;
    Mat tmp;
    TextView txt;
    int screenOrientation = 0;
    Mat line;
    Point pt1, pt2,pt3,pt4;
    ArrayList<String> f = new ArrayList<String>();// list of file paths
    File[] listFile;
    Vector<Mat> data;


    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:{

                    Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                    screenOrientation = display.getRotation();
                    if(screenOrientation == 0)
                        javaCameraView.setCameraAngle(90);
                    else if(screenOrientation == 1)
                        javaCameraView.setCameraAngle(0);
                    else if(screenOrientation == 3)
                        javaCameraView.setCameraAngle(180);
                    javaCameraView.enableView();
                    break;
                }
                default: {
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

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main2);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        javaCameraView = (JavaCameraView) findViewById(R.id.java_camera_view);
        javaCameraView.setCvCameraViewListener(this);
javaCameraView.setBackgroundColor(5);





    }

    public void getFromSdcard()
    {

        InputStream stream = null;
        Uri uri = Uri.parse("android.resource://com.example.ege.epp/drawable/kimbyeong");
        try {
            stream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bmp = BitmapFactory.decodeStream(stream, null, bmpFactoryOptions);
        tmp= new Mat();
        Utils.bitmapToMat(bmp,tmp);



    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null)
            javaCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null)
            javaCameraView.disableView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.i(TAG, "Opencv loaded successfully");
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.i(TAG, "Opencv not loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallBack);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        getFromSdcard();
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaS= new Mat(height, width, CvType.CV_8UC4);
        line= new Mat(height, width, CvType.CV_8UC4);
         edge=new Mat();
        javaCameraView.setBackgroundColor(2);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        cvtColor(mRgba, mRgbaS,COLOR_RGB2GRAY);

        //Applying Canny
     Canny(mRgbaS,mRgbaS,80,90);
        HoughLines(mRgbaS, line, 1, Math.PI / 180, 150);


        int min1 = 80000, min2 = 80000, max1 = 0, max2 = 0;
        if(line.cols()>=2) {
            double[] data;
            double rho, theta;
            pt1 = new Point();
            pt2 = new Point();
            pt3 = new Point();
            pt4 = new Point();
            double a, b;
            double x0, y0;

                data = line.get(0, 0);
                rho = data[0];
                theta = data[1];
                a = Math.cos(theta);
                b = Math.sin(theta);
                x0 = a * rho;
                y0 = b * rho;
                pt1.x = Math.round(x0 + 1000 * (-b));
                pt1.y = Math.round(y0 + 1000 * a);
                pt2.x = Math.round(x0 - 1000 * (-b));
                pt2.y = Math.round(y0 - 1000 * a);
            line(mRgba,pt1,pt2,new Scalar(0,255,0));
                data = line.get(0, 1);
                rho = data[0];
            }


         ///////////////////////////////////////



       mRgba=mRgba.adjustROI(min2,max2,min1,max1);




        return mRgba;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}




