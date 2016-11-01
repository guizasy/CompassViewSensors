package com.taberu.compassviewsensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private CompassView compassView;
    private SensorManager sensorManager;

    private float[] aValues = new float[3];
    private float[] mValues = new float[3];

    private int rotation;
    public boolean mInitialized;

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                aValues = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mValues = event.values;
            updateOrientation(calculateOrientation());
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Update the onCreate method to get references to the CompassView and SensorManager ,
        // to determine the current screen orientation relative to the natural device orientation, and
        // to initialize the heading, pitch, and roll: (Meier cap 12 p 503)
        compassView = (CompassView) findViewById(R.id.compassView);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        String windoSrvc = Context.WINDOW_SERVICE;
        WindowManager wm = ((WindowManager) getSystemService(windoSrvc));
        Display display = wm.getDefaultDisplay();

        rotation = display.getRotation();
        mInitialized = false;

        updateOrientation(new float[]{0, 0, 0});
    }

    private void updateOrientation(float[] values) {
        if (compassView != null) {
            compassView.setBearing(values[0]);
//            compassView.setPitch(values[1]);
//            compassView.setRoll(-values[2]);
            compassView.invalidate();
        }
    }

    public void updateText(float[] rotationMatrix, float[] values) {
//        TextView tvR = (TextView) findViewById(R.id.x_roll);
//        TextView tvP = (TextView) findViewById(R.id.y_pitch);
//        TextView tvA = (TextView) findViewById(R.id.z_azimuth);

        TextView tv1 = (TextView) findViewById(R.id.rot_1);
        TextView tv2 = (TextView) findViewById(R.id.rot_2);
        TextView tv3 = (TextView) findViewById(R.id.rot_3);
        TextView tv4 = (TextView) findViewById(R.id.rot_4);
        TextView tv5 = (TextView) findViewById(R.id.rot_5);
        TextView tv6 = (TextView) findViewById(R.id.rot_6);
        TextView tv7 = (TextView) findViewById(R.id.rot_7);
        TextView tv8 = (TextView) findViewById(R.id.rot_8);
        TextView tv9 = (TextView) findViewById(R.id.rot_9);

        if (!mInitialized) {
//            tvR.setText("0.0");
//            tvP.setText("0.0");
//            tvA.setText("0.0");

            tv1.setText("0.0");
            tv2.setText("0.0");
            tv3.setText("0.0");
            tv4.setText("0.0");
            tv5.setText("0.0");
            tv6.setText("0.0");
            tv7.setText("0.0");
            tv8.setText("0.0");
            tv9.setText("0.0");

            mInitialized = true;
        } else {
//            tvA.setText(Float.toString(values[0])); // z
//            tvR.setText(Float.toString(values[1])); // x
//            tvP.setText(Float.toString(values[2])); // y

            tv1.setText(Float.toString(rotationMatrix[0]));
            tv2.setText(Float.toString(rotationMatrix[1]));
            tv3.setText(Float.toString(rotationMatrix[2]));
            tv4.setText(Float.toString(rotationMatrix[3]));
            tv5.setText(Float.toString(rotationMatrix[4]));
            tv6.setText(Float.toString(rotationMatrix[5]));
            tv7.setText(Float.toString(rotationMatrix[6]));
            tv8.setText(Float.toString(rotationMatrix[7]));
            tv9.setText(Float.toString(rotationMatrix[8]));
        }
    }

    private float[] calculateOrientation() {
        float[] values = new float[3];
        float[] inR = new float[9];
        float[] outR = new float[9];

        // Determine the rotation matrix
        SensorManager.getRotationMatrix(inR, null, aValues, mValues);

        // Remap the coordinates based on the natural device orientation.
        int x_axis = SensorManager.AXIS_X;
        int y_axis = SensorManager.AXIS_Y;
        
        switch (rotation) {
            case (Surface.ROTATION_90):
                x_axis = SensorManager.AXIS_Y;
                y_axis = SensorManager.AXIS_MINUS_X;
                break;
            case (Surface.ROTATION_180):
                y_axis = SensorManager.AXIS_MINUS_Y;
                x_axis = SensorManager.AXIS_MINUS_X;
                break;
            case (Surface.ROTATION_270):
                x_axis = SensorManager.AXIS_MINUS_Y;
                y_axis = SensorManager.AXIS_X;
                break;
            default:
                break;
        }

        SensorManager.remapCoordinateSystem(inR, x_axis, y_axis, outR);

        // Obtain the current, corrected orientation.
        SensorManager.getOrientation(outR, values);

        // Convert from Radians to Degrees.
        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);

        updateText(outR, values);
        return values;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(sensorEventListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);

        sensorManager.registerListener(sensorEventListener,
                magField,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(sensorEventListener);
        super.onPause();
    }
}