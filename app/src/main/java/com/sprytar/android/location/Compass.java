package com.sprytar.android.location;

/**
 * Created by imobdev-darshan on 2/9/17.
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Compass implements SensorEventListener {
    private static final String TAG = "Compass";

    private SensorManager sensorManager;
    private Sensor gsensor;
    private Sensor msensor;

    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float azimuth = 0f;
    private float currectAzimuth = 0;
    private Location myLocation;
    private Location target;
    private LocationManager locationManager;
    private Context mContext;
    // compass arrow to rotate
    public ImageView arrowView = null;
    public TextView tvDistance;

    public Compass(Context context, Location location, Location target, TextView tvDistance,ImageView arrowView) {
        mContext = context;
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.tvDistance =tvDistance;
        this.arrowView = arrowView;
        myLocation = location;
        this.target = target;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

}

    private void setupLocationListener() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria, true);

        //tvDistance.setText(String.valueOf(Math.round(myLocation.distanceTo(target)))+"m");

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(provider,
                    1000, 0.5f,locationListenerGPS);
            //locationManager.requestLocationUpdates(provider, 1, 0, locationListenerGPS);
        }
    }

    android.location.LocationListener locationListenerGPS=new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(mContext, "Location changed", Toast.LENGTH_SHORT).show();
            myLocation =location;
            tvDistance.setText(String.valueOf(Math.round(myLocation.distanceTo(target)))+"m");
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
  public void start() {
      PackageManager manager = mContext.getPackageManager();
      boolean hasCompass = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);

      //if(hasCompass){
          sensorManager.registerListener(this, gsensor,
                  SensorManager.SENSOR_DELAY_GAME);
          setupLocationListener();



    }

    public void stop() {
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(locationListenerGPS);
 }


    protected double bearing(double startLat, double startLng, double endLat, double endLng){
        double longitude1 = startLng;
        double longitude2 = endLng;
        double latitude1 = Math.toRadians(startLat);
        double latitude2 = Math.toRadians(endLat);
        double longDiff= Math.toRadians(longitude2-longitude1);
        double y= Math.sin(longDiff)* Math.cos(latitude2);
        double x= Math.cos(latitude1)* Math.sin(latitude2)- Math.sin(latitude1)* Math.cos(latitude2)* Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    }

    private void adjustArrow() {
        Toast.makeText(mContext, "Adjust arrow called", Toast.LENGTH_SHORT).show();
        if (arrowView == null) {
            Log.i(TAG, "arrow view is not set");
            return;
        }

        Log.i(TAG, "will set rotation from " + currectAzimuth + " to "
                + azimuth);

        Animation an = new RotateAnimation(-currectAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currectAzimuth = azimuth;

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        arrowView.startAnimation(an);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                mGravity[0] = alpha * mGravity[0] + (1 - alpha)
                        * event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha)
                        * event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha)
                        * event.values[2];

                // mGravity = event.values;

                // Log.e(TAG, Float.toString(mGravity[0]));
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                // mGeomagnetic = event.values;

                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha)
                        * event.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha)
                        * event.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha)
                        * event.values[2];
                // Log.e(TAG, Float.toString(event.values[0]));

            }

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
                    mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // Log.d(TAG, "azimuth (rad): " + azimuth);
                azimuth = (float) Math.toDegrees(orientation[0]); // orientation
                azimuth = (azimuth + 360) % 360;
                azimuth -= bearing(myLocation.getLatitude(), myLocation.getLongitude(), target.getLatitude(), target.getLongitude());
                // Log.d(TAG, "azimuth (deg): " + azimuth);
                adjustArrow();
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}