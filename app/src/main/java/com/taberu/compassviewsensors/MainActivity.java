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

public class MainActivity extends AppCompatActivity {
    private CompassView compassView;
    private SensorManager sensorManager;

    private float[] aValues = new float[3];
    private float[] mValues = new float[3];

    private int rotation;
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