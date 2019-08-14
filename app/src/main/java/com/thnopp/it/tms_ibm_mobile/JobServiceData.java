package com.thnopp.it.tms_ibm_mobile;
import android.app.ActivityManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class JobServiceData extends JobService {

    private static final String TAG = "SchduleJobService";
    private boolean jobCancelled = false;

    String param, status;
    DatabaseHelper db;
    List<Scanvin> lst;
    List<Vinmaster> lst_pod, lst_pod_del,lst_pod_user;
    List<Dealerinst> lst_inst;
    //for location tracking
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        doBackgroundWork(params);

        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                String username = prefs.getString("username","");
                String password = prefs.getString("password","");
                String lat  = prefs.getString("lat","");
                String lon  = prefs.getString("long","");
                Global.user = username;
                Global.conf = password;
                /*
                 * 1. send update time status & change status to U (if disp, arriv is not null)
                 * 2.
                 * */
                //do here
                status = "A";
                param = username;



                //open database -> gen data to lst
                db = new DatabaseHelper(getApplicationContext());



                lst_inst = new ArrayList<Dealerinst>();
                Dealerinst newinst = new Dealerinst();
                newinst.setDealer(username);
                newinst.setInstruction1(lat);
                newinst.setInstruction2(lon);
                lst_inst.add(newinst);
                new JobServiceData.SendLocation().execute();

                int chkPendingPOD_r = db.chkPendingPOD();

                if(chkPendingPOD_r >0)
                    if (isMyServiceRunning(LocationService.class)==false)
                        startService(new Intent(getApplicationContext(), LocationService.class));
                    else
                    if (isMyServiceRunning(LocationService.class)==true)
                        stopService(new Intent(getApplicationContext(), LocationService.class));


                lst_pod = db.getPOD();
                if (!lst_pod.isEmpty()){
                    new JobServiceData.update_pod().execute();
                    //    startService(new Intent(getApplicationContext(), LocationService.class));

                }
                db.DeleteVIN_Old("");
                db.closeDB();

                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public class update_pod extends AsyncTask<Void, Void, Void> {
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
                //  rs.Connect();
                //result = rs.GetJsonString("FixedAsset");
                result = rs.PostPOD_F(lst_pod);

            }catch (Exception ex){
//                Toast.makeText(getBaseContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result2) {
            super.onPostExecute(result2);
            Toast.makeText(getBaseContext(),result, Toast.LENGTH_SHORT).show();
          /*  if (result== null){
              //  Toast.makeText(getBaseContext(),"Can't post the POD Data",Toast.LENGTH_LONG).show();
            }*/
        }
    }

    public class SendLocation extends AsyncTask<Void, Void, Void> {
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

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }

}
