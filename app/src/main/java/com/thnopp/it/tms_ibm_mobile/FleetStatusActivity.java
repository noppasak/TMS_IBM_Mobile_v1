package com.thnopp.it.tms_ibm_mobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by THLT88 on 10/20/2017.
 */

public class FleetStatusActivity extends Activity {

    TextView lbltype, lbluser,lblstatus ;
    String param, status;
    Button reload, a, logout, u,back ;

    DatabaseHelper db;
    List<Scanvin> lst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fleet_status);


        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String username = prefs.getString("username","");
        Integer client = prefs.getInt("client",0);
        String conf = prefs.getString("password","");
        Global.user = username;
        Global.client = client;
        Global.conf = conf;
        Global.status = "";
        db = new DatabaseHelper(getApplicationContext());

        param = username;

        //lst = db.getAllVIN();

        //assign value into label
        lbltype = (TextView)findViewById(R.id.lbltype);
        lbluser = (TextView)findViewById(R.id.lbluser);

        lbluser.setText(Global.user);

        lblstatus= (TextView)findViewById(R.id.lblstatus);

        reload = (Button)findViewById(R.id.buttonReload);
        a = (Button)findViewById(R.id.buttonA);
        u = (Button)findViewById(R.id.buttonU);
        back = (Button)findViewById(R.id.buttonBack);

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FleetStatusActivity.GetUserStatus().execute();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
            }
        });

        new FleetStatusActivity.GetUserStatus().execute();
        if (Global.status.equals("")){
            // not show button
            a.setVisibility(View.INVISIBLE);
            u.setVisibility(View.INVISIBLE);
        }

        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update status A
                status = "A";
                new FleetStatusActivity.update_status().execute();
                new FleetStatusActivity.GetUserStatus().execute();
                stopService(new Intent(getApplicationContext(), LocationService.class));
                startService(new Intent(getApplicationContext(), LocationService.class));

            }
        });
        u.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update status U
                status = "U";
                new FleetStatusActivity.update_status().execute();
                new FleetStatusActivity.GetUserStatus().execute();

                stopService(new Intent(getApplicationContext(), LocationService.class));

            }
        });




       /* stopService(new Intent(this, ScheduleService.class));
        stopService(new Intent(this, LocationService.class));
        stopService(new Intent(this, ScheduleService_long.class));

        startService(new Intent(this, ScheduleService.class));
        startService(new Intent(this, LocationService.class));
        startService(new Intent(this, ScheduleService_long.class));*/

    }


    public class GetUserStatus extends AsyncTask<Void, Void, Void> {
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
                result = rs.GetJsonString(param,"USER_STATUS");
            }catch (Exception ex){
//                Toast.makeText(getBaseContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result2) {
            super.onPostExecute(result2);
            //       Toast.makeText(getBaseContext(),result,Toast.LENGTH_LONG).show();
            if (result== null){
                // not found any order
                // Toast.makeText(getBaseContext(),"Can't load the Master Data",Toast.LENGTH_LONG).show();
            }else {

                    // get data to buildings
                    try {
                        String id="";

                        JSONArray jsonarray = new JSONArray("["+result+"]");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            id = jsonobject.getString("userstatus");
                        }

                        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("status", id);
                        Global.status = id;
                        editor.commit();

                        if (id.equals("A")){
                            lblstatus.setText("Current Status: มาทำงาน");
                            a.setVisibility(View.INVISIBLE);
                            u.setVisibility(View.VISIBLE);
                        }else if (id.equals("U")){
                            lblstatus.setText("Current Status: เลิกงาน");
                            a.setVisibility(View.VISIBLE);
                            u.setVisibility(View.INVISIBLE);
                        }else{
                            lblstatus.setText("Current Status: " + id);
                            a.setVisibility(View.INVISIBLE);
                            u.setVisibility(View.INVISIBLE);
                        }

                    } catch (JSONException e) {
                        Log.e("", "unexpected JSON exception", e);
                        //  Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(getBaseContext(), "Asset Load Complete", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public class update_status extends AsyncTask<Void, Void, Void> {
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
                result = rs.SendStatus(Global.user, status);

            }catch (Exception ex){
//                Toast.makeText(getBaseContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result2) {
            super.onPostExecute(result2);
            Toast.makeText(getBaseContext(),result, Toast.LENGTH_SHORT).show();
            new FleetStatusActivity.GetUserStatus().execute();
          /*  if (result== null){
              //  Toast.makeText(getBaseContext(),"Can't post the POD Data",Toast.LENGTH_LONG).show();
            }*/
        }
    }

    @Override
    public void onBackPressed() {
    }

}
