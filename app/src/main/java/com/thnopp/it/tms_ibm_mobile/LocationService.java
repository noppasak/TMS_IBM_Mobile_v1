package com.thnopp.it.tms_ibm_mobile;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

public class LocationService extends Service
{
    private static final String TAG = "TESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 10 * 1000; //1000;
    private static final float LOCATION_DISTANCE = 10f;
    List<Dealerinst> lst_inst;


    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
          //  Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
         //   Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
            String username = prefs.getString("username","");
            String password = prefs.getString("password","");
            Global.user = username;
            Global.conf=password;

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("lat", String.valueOf(location.getLatitude()));
                editor.putString("long", String.valueOf(location.getLongitude()));
            editor.commit();

           /* lst_inst = new ArrayList<Dealerinst>();
            Dealerinst newinst = new Dealerinst();
            newinst.setDealer(username);
            newinst.setInstruction1(String.valueOf(location.getLatitude()));
            newinst.setInstruction2(String.valueOf(location.getLongitude()));
            lst_inst.add(newinst);

            new LocationService.SendLocation().execute();*/

           /* lst_inst = new ArrayList<Dealerinst>();
            Dealerinst newinst = new Dealerinst();
            newinst.setDealer(username);
            newinst.setInstruction1(String.valueOf(location.getLatitude()));
            newinst.setInstruction2(String.valueOf(location.getLongitude()));
            lst_inst.add(newinst);
            new LocationService.SendLocation().execute();*/
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

   /* public class SendLocation extends AsyncTask<Void, Void, Void> {
        RestService rs;
        String result;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rs = new RestService(getBaseContext(),"","",Global.user,Global.conf);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try{
                result = rs.SendGPS(lst_inst);
            }catch (Exception ex){
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result2) {
            super.onPostExecute(result2);
            if (result== null){
                // not found any order
            }else {
                Log.e("", "send data");

            }
        }
    }
*/
}