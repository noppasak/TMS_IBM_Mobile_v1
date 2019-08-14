package com.thnopp.it.tms_ibm_mobile;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.thcvl.it.tms.Config.UPDATE_URL;


public class MainActivity extends Activity {

    ProgressDialog bar;
    TextView textView, NGTime, txtver ;

    Button start, exit;

    EditText txtusr, txtpaw;


    RadioButton c1, c2;

    private String AppVersion = "1.2 / 13 August 19";



    private static final int REQUEST_WRITE_STORAGE = 112;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String username = prefs.getString("username","");
        Integer client = prefs.getInt("client",0);
        String conf = prefs.getString("conf","");
        Global.user = username;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        boolean hasPermission = (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(MainActivity.this   ,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }

        checkLocationPermission();


        start = (Button)findViewById(R.id.buttonG);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //test login
                //test1();
                Global.client = 1;
                Global.conf  = "";


                new MainActivity.GetLogin().execute();
            }
        });


        Button update_btn = (Button) findViewById(R.id.buttonUpdate);
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DownloadNewVersion().execute();


            }
        });


        txtver = (TextView) findViewById(R.id.txtversion);
        txtver.setText("Version: " + AppVersion);

        txtusr = (EditText) findViewById(R.id.txtUser);
        txtusr.setText(Global.user.toString());

        txtpaw = (EditText)findViewById(R.id.txtPass);



        if (isMyServiceRunning(Location1Service.class)==false)
            startService(new Intent(this, Location1Service.class));
        //   startService(new Intent(this, LocationService.class));
        //  startService(new Intent(this, ScheduleService_long.class));

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
    public void exit_prog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        int imageResource = android.R.drawable.ic_dialog_alert;
        Drawable image = getResources().getDrawable(imageResource);

        builder.setTitle("Exit").setMessage("want to exit?").setIcon(image).setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);


            }
        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();


    }



    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Permission")
                        .setMessage("Request Location Permission")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        // locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    public class GetLogin extends AsyncTask<Void, Void, Void> {
        RestService rs;
        Result result;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rs = new RestService(getBaseContext(),"","",txtusr.getText().toString(),txtpaw.getText().toString());
            result = new Result();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try{

                result = rs.LogIn();


            }catch (Exception ex){
                Toast.makeText(getBaseContext(),ex.getMessage(), Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result2) {
            super.onPostExecute(result2);
            //       Toast.makeText(getBaseContext(),result,Toast.LENGTH_LONG).show();
            if(result.Check){
                //Login.setText("Login Success");
                //Toast.makeText(getBaseContext(),"Login Successful",Toast.LENGTH_LONG).show();
                SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("username",txtusr.getText().toString() );
                editor.putString("password", txtpaw.getText().toString());
                editor.putInt("client",Global.client);
                editor.putString("conf",Global.conf);


                editor.commit();
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);


            }else{
                Toast.makeText(getBaseContext(),"Login Error"+result.Message, Toast.LENGTH_LONG).show();

            }
        }
    }

    private class read_data extends AsyncTask<Void,Integer, String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadProcess();

        }

        @SuppressWarnings("deprecation")
        private String uploadProcess() {
            String responseString = null;
            // add data to table via json


            final String username = "CVL"+ txtusr.getText().toString();
            final String userpassword = txtpaw.getText().toString();


            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            StringRequest request = new StringRequest(Request.Method.POST, Config.LOGIN_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    //  Toast.makeText(MainActivity.this, "my success"+response, Toast.LENGTH_LONG).show();
                    Log.i("My success", "" + response);

                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(response);
                        String msgCode = jsonObject.getString("result");
                        if (msgCode.equals("OK")) {

                            Global.user = username;
                            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("username", username);
                            editor.putInt("client",Global.client);
                            editor.putString("conf",Global.conf);

                            editor.commit();

                            //  Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                            //   startActivity(intent);

                        } else {
                            // display error
                            Toast.makeText(MainActivity.this, "Login Error รหัส/password ผิด :" + msgCode, Toast.LENGTH_LONG).show();
                            Log.i("My error", "" + msgCode);

                        }

                        // txtusr.setText(msgCode);
                    } catch (JSONException e) {

                    } catch (Exception e) {

                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(MainActivity.this, "my error :" + error, Toast.LENGTH_LONG).show();
                    Log.i("My error", "" + error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> map = new HashMap<String, String>();

                    map.put("txtuser", username);
                    map.put("txtpass", userpassword);
                    map.put("clientcd", String.valueOf(Global.client).toString());


                    return map;
                }
            };
            queue.add(request);

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);


            super.onPostExecute(result);

        }
    }



    class DownloadNewVersion extends AsyncTask<String,Integer,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            bar = new ProgressDialog(MainActivity.this);
            bar.setCancelable(false);

            bar.setMessage("Downloading...");

            bar.setIndeterminate(true);
            bar.setCanceledOnTouchOutside(false);
            bar.show();

        }

        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            bar.setIndeterminate(false);
            bar.setMax(100);
            bar.setProgress(progress[0]);
            String msg = "";
            if(progress[0]>99){

                msg="Finishing... ";

            }else {

                msg="Downloading... "+progress[0]+"%";
            }
            bar.setMessage(msg);

        }
        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            bar.dismiss();

            if(result){

                Toast.makeText(getApplicationContext(),"Update Done",
                        Toast.LENGTH_SHORT).show();

            }else{

                Toast.makeText(getApplicationContext(),"Error: Try Again",
                        Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            Boolean flag = false;

            try {


                URL url = new URL(UPDATE_URL);

                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();


                File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES),  Config.IMAGE_DIRECTORY_NAME);

                //String PATH = Environment.getExternalStorageDirectory()+"/Download/";
                String PATH = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).toString();
                File file = new File(PATH);
                file.mkdirs();

                File outputFile = new File(file,"/app-debug.apk");

                if(outputFile.exists()){
                    outputFile.delete();
                }

                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream is = c.getInputStream();

                //int total_size = 1431692;//size of apk--old
                int total_size = 8431692;//size of apk--modify

                byte[] buffer = new byte[1024];
                int len1 = 0;
                int per = 0;
                int downloaded=0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                    downloaded +=len1;
                    per = (int) (downloaded * 100 / total_size);
                    publishProgress(per);
                }
                fos.close();
                is.close();

                OpenNewVersion(PATH);

                flag = true;
            } catch (Exception e) {
                Log.e(TAG, "Update Error: " + e.getMessage());
                flag = false;
            }
            return flag;

        }

    }

    void OpenNewVersion(String location) {

    /*    Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(location + "app-debug.apk")),
                "application/vnd.android.package-archive");

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/

        File toInstall = new File(location + "/app-debug.apk");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
/*            Uri apkUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", toInstall);
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            MainActivity.this.startActivity(intent);*/

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "app.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);



        } else {
            Uri apkUri = Uri.fromFile(toInstall);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.this.startActivity(intent);
        }

    }


    public void scheduleJob() {
        ComponentName componentName = new ComponentName(this, JobServiceData.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(1 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }

    public void cancelJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d(TAG, "Job cancelled");
    }

    @Override
    public void onBackPressed() {


    }





}
