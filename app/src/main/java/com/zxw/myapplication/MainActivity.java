package com.zxw.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.zxw.mappoint.MapPointHelper;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {


    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(new StudyView(this));
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);

//        imageView.setImageBitmap(MapPointHelper.getInstance(this).createBitmap());
        imageView.setImageBitmap(MapPointHelper.getInstance(this).generateBitmap(MapPointHelper.State.CHOOSE, MapPointHelper.Station.UP, "龙华", "19:20"));
//        fun1();
    }

    public void cu(View view){
        imageView.setImageBitmap(MapPointHelper.getInstance(this).generateBitmap(MapPointHelper.State.CHOOSE, MapPointHelper.Station.UP, 30, ":幼儿园", "19：20"));
    }
    public void cd(View view){
        imageView.setImageBitmap(MapPointHelper.getInstance(this).generateBitmap(MapPointHelper.State.CHOOSE, MapPointHelper.Station.DOWN, "龙华弓村", "1920"));
    }

    public void fu(View view){
        imageView.setImageBitmap(MapPointHelper.getInstance(this).generateBitmap(MapPointHelper.State.FAR, MapPointHelper.Station.UP, "龙华弓村幼儿", "1920"));
    }
    public void fd(View view){
        imageView.setImageBitmap(MapPointHelper.getInstance(this).generateBitmap(MapPointHelper.State.FAR, MapPointHelper.Station.DOWN, "龙华弓村幼儿园", "1920"));
    }

    public void nu(View view){
        imageView.setImageBitmap(MapPointHelper.getInstance(this).generateBitmap(MapPointHelper.State.NEAR, MapPointHelper.Station.UP, "龙华弓村幼儿园", "1920"));
    }
    public void nd(View view){
        imageView.setImageBitmap(MapPointHelper.getInstance(this).generateBitmap(MapPointHelper.State.NEAR, MapPointHelper.Station.DOWN, "龙华弓村幼儿园", "1920"));
    }

    private void fun1() {

        BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
        BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test1, BitmapFactoryOptionsbfo);

        final List<Bitmap> cutBitmapList = fun(myBitmap);


        new Thread(){
            @Override
            public void run() {
                super.run();
                for (int i = 0; i<= 100; i++){
                    final Bitmap bitmap = cutBitmapList.get(i % 3);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private List<Bitmap> fun(Bitmap myBitmap) {
        List<Bitmap> cutBitmapList = new ArrayList<>();
        // 最多在图片中可找5张人脸
        int numberOfFace = 5;
        // 偏移系数默认值
        float offsetCoefficient = 1.5f;

        int imageWidth = myBitmap.getWidth();
        int imageHeight = myBitmap.getHeight();
        FaceDetector.Face[] myFace = new FaceDetector.Face[numberOfFace];
        FaceDetector myFaceDetect = new FaceDetector(imageWidth, imageHeight, numberOfFace);
        int numberOfFaceDetected = myFaceDetect.findFaces(myBitmap, myFace);
        for (int i = 0; i < numberOfFaceDetected; i++) {
            FaceDetector.Face face = myFace[i];
            PointF myMidPoint = new PointF();
            face.getMidPoint(myMidPoint);
            float myEyesDistance = face.eyesDistance();
            float offsetDistance = myEyesDistance * offsetCoefficient;

            int firstPoint_X = (int) (myMidPoint.x - offsetDistance);
            int firstPoint_Y = (int) (myMidPoint.y - offsetDistance);
            int width = (int) (offsetDistance * 2);
            int height = (int) (offsetDistance * 2);
            Bitmap cutBitmap = Bitmap.createBitmap(myBitmap, firstPoint_X, firstPoint_Y, width, height);
            cutBitmapList.add(cutBitmap);
        }
        return cutBitmapList;
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
