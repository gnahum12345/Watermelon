package com.weebly.watermelon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;

import com.weebly.watermelon.R;

//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import javax.imageio.ImageIO;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    final int IDEAL_RED = 210;
    final int IDEAL_GREEN = 190;
    final int IDEAL_BLUE = 130;
    private static final int PROGRESS = 0x1;
    Camera camera;
    Camera.Parameters param;
    TextView num;
    ProgressBar mProgress;
    ImageButton b;
    TextView check;
    final int MAX_AVERAGE = 10;
    final int MIN_YELLOW = 10;
    long [] average = new long[MAX_AVERAGE];
    int currIndex = 0;
    int count= 1;
    // number of pixels//transforms NV21 pixel data into RGB pixels
    long [] rgb;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean recording = false;
    Camera.PictureCallback rawCallback;
    Camera.ShutterCallback shutterCallback;
    Camera.PictureCallback jpegCallback;

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        b = (ImageButton) findViewById(R.id.imageButton);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
       /*
        jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream = null;
                try {
                    outStream = new FileOutputStream(String.format("/sdcard/%d.jpg", System.currentTimeMillis()));
                    outStream.write(data);
                    outStream.close();
                    Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //do nothing
                    System.out.print("it went to finally");
                }
                Toast.makeText(getApplicationContext(), "Picture Saved", Toast.LENGTH_SHORT ).show();
                refreshCamera();
            }
        };
        */

        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mProgress.setVisibility(View.VISIBLE);
        mProgress.setMax(100);
        check = (TextView) findViewById(R.id.textView);

        num = (TextView) findViewById(R.id.num);

        //start button listener

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recording = true;
                num.setText("-.--%");
              //  param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
              //  camera.setParameters(param);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(400);
            }
        });

    }

    public double getClosest(){
        double closest = distanceFromPerfect(average[0]);
        for(int i = 1; i < MAX_AVERAGE; i++){
            if(closest < distanceFromPerfect(average[i])){
                closest = distanceFromPerfect(average[i]);
            }
        }
        return closest;

    }

    public void addAverage(long aver){
        if(recording){
            this.average[currIndex] = aver;
            currIndex++;
     //       Log.w("worked", "adding the average worked");
            //circular array for measurements.
            if(currIndex == MAX_AVERAGE){
                Log.w("going to calc", "maxAverage was reached");
                currIndex = 0;
                recording = false;
                DecimalFormat decimalFormat = new DecimalFormat("0.00");

     //           Log.w("Average", "getting average was successful");
                double percentage = getClosest();
       //         Log.w("Percentage", "percentage was successful");
                num.setText(decimalFormat.format(percentage) + "%");
                mProgress.setProgress((int) percentage);

             //   param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
             //   camera.setParameters(param);
            }
        }
    }
     public double distanceFromPerfect(long color){
        long red = getRed(color);
        long blue = getBlue(color);
        long green = getGreen(color);

        final double maxDistance = Math.sqrt( Math.pow((IDEAL_RED),2) + Math.pow((IDEAL_GREEN),2) + Math.pow((IDEAL_BLUE), 2));
    //    Log.w("distance", "calculating perfect distance was successful");
        double distance = Math.sqrt(Math.pow((IDEAL_RED-red),2)+ Math.pow((IDEAL_GREEN-green),2) + Math.pow((IDEAL_BLUE-blue),2));
        Log.w("color", "Red: " + red + " blue" + blue + " green " + green);
        return ((100)- ((distance /maxDistance) *100));

    }


    public long checkYellow(long color){

        int r = getRed(color);
        int g = getGreen(color);
        int b = getBlue(color);
      /*  Log.w("Check", " \nred: " + red +"\nBlue: "+ blue + "\nGreen: " + green);

       if((r >= 0xE0 && g >=0xE0) && (b <= 80)) {
            return color;
       }
        return 0;
        */
        if(((r<=(IDEAL_RED +40)) && (r>=(IDEAL_RED -40))) && ((g<=(IDEAL_GREEN +40))&&(g>=(IDEAL_GREEN-40))) && ((b<=(IDEAL_BLUE+40))&&(b>=(IDEAL_BLUE-40)))){
            return color;
        }
        return 0;
    }

    //return 0-255
    public int getRed(long color){
        return  ((int) (((color & 0xFF0000) >>16) & 0xFF));

    }
    //return 0-255
    public int getGreen(long color){
        return  ((int) (((color & 0xFF00) >> 8) & 0xFF));
    }
    //return 0-255
    public int getBlue(long color){
        return  ((int) (color & 0xFF));

    }

    public void captureImage(View v) throws IOException {
        //take the picture
        camera.takePicture(null, null, jpegCallback);
    }

    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    int frameHeight = camera.getParameters().getPreviewSize().height;
                    int frameWidth = camera.getParameters().getPreviewSize().width;
                    // number of pixels//transforms NV21 pixel data into RGB pixels
                    // convert
                    rgb = decodeYUV420SP(rgb, data, frameWidth, frameHeight);
                  //  Log.w("decoding", "decoding was successful");
                    long num;
                    long avg =0;
                    for(int  i = 0; i< rgb.length; i++){
                        num = checkYellow(rgb[i]);
                   //     Log.w("color", "checkYellow was successful");
                        //if it is within range, than num = rgb[i] else it = 0;
                        if(num != 0){
                            avg += num;
                            count++;
                        }
                    }
                    Log.w("count", " " + count + " length: " + rgb.length);
                    avg = avg/count;
                    int accounted = ((count *100)/rgb.length);
                    count = 1;
                 //  Log.w("accounted", "accounted was successful");
                    if(accounted >= MIN_YELLOW){
                        addAverage(avg);
                     ///   Log.w("addAverage", "was successful");
                    }else{
                        addAverage(0);
                   //     Log.w("addAverage", "was successful");
                    }

                }
            });
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {

        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        refreshCamera();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // open the camera
            camera = Camera.open();
        } catch (RuntimeException e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
        param = camera.getParameters();
        param.setPreviewFormat(ImageFormat.NV21);
        camera.setDisplayOrientation(90);
       // Log.i("Camera", "camera is displayed properly.");
        // modify parameter
        param.setPreviewSize(352, 288);
        param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        int maxZoom = param.getMaxZoom();
        param.setZoom(maxZoom/2);
        param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(param);
        try {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            int frameHeight = camera.getParameters().getPreviewSize().height;
            int frameWidth = camera.getParameters().getPreviewSize().width;
            // number of pixels//transforms NV21 pixel data into RGB pixels
            rgb = new long[frameWidth * frameHeight];
        } catch (Exception e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // stop preview and release camera
        camera.stopPreview();
        camera.release();
        camera = null;
    }



    //  Byte decoder : ---------------------------------------------------------------------
    public long[] decodeYUV420SP(long[] rgb, byte[] yuv420sp, int width, int height) {
        // Pulled directly from:
        // http://ketai.googlecode.com/svn/trunk/ketai/src/edu/uic/ketai/inputService/KetaiCamera.java
        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) { int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;

                rgb[yp] =  0x00ffffff & (((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff));
            }
        }
        return rgb;
    }

    @Override
    protected void onPause() {
        super.onPause();
        param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(param);
        try
        {
            // release the camera immediately on pause event
            //releaseCamera();
            camera.stopPreview();
            camera.setPreviewCallback(null);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
     //   param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
      //  camera.setParameters(param);
        refreshCamera();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.scan).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Toast toast = Toast.makeText(getApplicationContext(),"Option 1", Toast.LENGTH_SHORT);


        switch(item.getItemId()){
            case R.id.scan:
                toast.setText("Scan");
                toast.show();
                return true;
           // case R.id.maps:
             //   toast.setText("Map");
            //    toast.show();
             //   param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
             //   camera.setParameters(param);
             //   changeActivityToMap();
             //   return true;
            case R.id.help:
                toast.setText("help");
                toast.show();
                param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(param);
                changeActivityToHelp();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeActivityToMap(){
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

    private void changeActivityToHelp(){
        Intent intent = new Intent(MainActivity.this, Help.class);
        startActivity(intent);
    }
}

