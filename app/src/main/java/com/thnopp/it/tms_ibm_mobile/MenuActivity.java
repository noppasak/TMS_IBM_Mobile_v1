package com.thnopp.it.tms_ibm_mobile;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by THLT88 on 10/20/2017.
 */

public class MenuActivity extends Activity {

    TextView lbltype, lbluser ;

    Button btntrip, btnadd, logout, btnstatus,upload,wi ;

    DatabaseHelper db;
    List<Scanvin> lst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String username = prefs.getString("username","");
        Integer client = prefs.getInt("client",0);
        String conf = prefs.getString("conf","");
        Global.user = username;
        Global.client = client;
        Global.conf = conf;

        db = new DatabaseHelper(getApplicationContext());



        //lst = db.getAllVIN();

        //assign value into label
        lbltype = (TextView)findViewById(R.id.lbltype);
        lbluser = (TextView)findViewById(R.id.lbluser);
        lbltype.setText(Global.conf);
        lbluser.setText(Global.user);

        btnstatus = (Button)findViewById(R.id.buttonStatus);
        btnstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FleetStatusActivity.class);
                startActivity(intent);
            }
        });

        btnadd = (Button)findViewById(R.id.buttonAddOrder);
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddOrderActivity.class);
                startActivity(intent);
            }
        });

        btntrip = (Button)findViewById(R.id.buttonTrip);
        btntrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TripPendingActivity.class);
                startActivity(intent);
            }
        });


        logout = (Button)findViewById(R.id.buttonLogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Global.user=null;

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });
        upload = (Button) findViewById(R.id.buttonReload);
        upload.setText("เชื่อมต่อกับ Server");
       /* lst = db.getAllVIN();
        if (lst.isEmpty()){
            upload.setVisibility(View.INVISIBLE);
        }else{
            upload.setVisibility(View.VISIBLE);

        }*/

        upload.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      new read_item_bal().execute();
                      if (isMyServiceRunning(Location1Service.class)==false)
                          startService(new Intent(getApplicationContext(), Location1Service.class));

                      if (isMyServiceRunning(ScheduleService.class)==false)
                          startService(new Intent(getApplicationContext(), ScheduleService.class));


                      Toast.makeText(getBaseContext(),"Syncing...", Toast.LENGTH_SHORT).show();
                  }
              }

        );


       /* stopService(new Intent(this, ScheduleService.class));
        stopService(new Intent(this, LocationService.class));
        stopService(new Intent(this, ScheduleService_long.class));

        startService(new Intent(this, ScheduleService.class));
        startService(new Intent(this, LocationService.class));
        startService(new Intent(this, ScheduleService_long.class));*/

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

    private class read_item_bal extends AsyncTask<Void,Integer, String> {
        @Override
        protected  void onPreExecute(){

            super.onPreExecute();

        }
        @Override
        protected String doInBackground(Void... params) {
            return uploadProcess();

        }

        @SuppressWarnings("deprecation")
        private String uploadProcess(){
            String responseString = null;
            // add data to table via json

            RequestQueue queue = Volley.newRequestQueue(MenuActivity.this);
            StringRequest request = new StringRequest(Request.Method.POST, Config.GET_ORDER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    //   Toast.makeText(FModelActivity.this, "my success"+response, Toast.LENGTH_LONG).show();
                    Log.i("My success",""+response);

                    try {

                        JSONArray jsonarray = new JSONArray(response);
                        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                        String username = prefs.getString("username","");


                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);

                            Vinmaster v = new Vinmaster();
                            v.setVin(jsonobject.getString("vin"));
                            v.setId(jsonobject.getString("delid"));
                            v.setLtcode(jsonobject.getString("ltid"));
                            v.setStatus("A");
                            v.setTrailer(username);

                            db.InsertDelivery(v);


                        }



                    } catch (JSONException e) {
                        Log.e("", "unexpected JSON exception", e);
                        // Do something to recover ... or kill the app.
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MenuActivity.this, "my error :"+error, Toast.LENGTH_LONG).show();
                    Log.i("My error",""+error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Config.HEAD_KEY, Config.HEAD_VALUE);
                    return params;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                    String username = prefs.getString("username","");

                    Map<String,String> map = new HashMap<String, String>();
                    map.put("user",username);
                    return map;
                }
            };
            queue.add(request);

            return  responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            super.onPostExecute(result);

        }

    }
    private class PostMobileCount extends AsyncTask<Void, Void, Void> {
        RestService rs;
        String result;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //rs = new RestService(getBaseContext(),"http://54.254.134.225:92","MSBS","admin","123");
            rs = new RestService(getBaseContext(),"","","","");
            Toast.makeText(getBaseContext(),"Sending...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // android.os.Debug.waitForDebugger();

            try{

                // result = rs.PostDataL(lst);
                result = rs.delVIN();
                Toast.makeText(getBaseContext(),result, Toast.LENGTH_LONG).show();

            }catch (Exception ex){
                //  Toast.makeText(getBaseContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
                result =ex.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result2) {
            super.onPostExecute(result2);

            Toast.makeText(getBaseContext(),"ส่งข้อมูลแล้ว (ถ้าส่งไม่หมด ปุ่ม ส่งข้อมุล ยังแสดงอยู่)", Toast.LENGTH_LONG).show();
            /*lst = db.getAllVIN();
            if (lst.isEmpty()){
                upload.setVisibility(View.INVISIBLE);
            }else {
                upload.setVisibility(View.VISIBLE);
            }*/

        }

    }


    @Override
    public void onBackPressed() {
    }

}
