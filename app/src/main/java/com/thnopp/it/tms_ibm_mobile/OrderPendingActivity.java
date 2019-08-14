package com.thnopp.it.tms_ibm_mobile;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class OrderPendingActivity extends Activity {


    /**
     * Created by THLT88 on 10/20/2017.
     */



        ArrayList<HashMap<String, String>> MyArrList;
        String t_lt,t_trailer;
        String sXML;
        String arrive, dispatch;
        ListView lisView1;
        DatabaseHelper db;
        Button btnCA, btnCD;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_order);
            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
            String username = prefs.getString("username","");
            Integer client = prefs.getInt("client",0);
            String conf = prefs.getString("conf","");
            t_trailer = username;

            final AlertDialog.Builder adb1 = new AlertDialog.Builder(this);

            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                if(extras == null) {
                    t_lt = null;
                } else {
                    t_lt =(String) extras.getSerializable("LT");
                }
            } else {
                t_lt = (String) savedInstanceState.getSerializable("LT");
            }

            lisView1 = (ListView) findViewById(android.R.id.list);

            MyArrList = new ArrayList<HashMap<String, String>>();

            TextView cap = (TextView) findViewById(R.id.textView1);
            cap.setText("LT ID :" + t_lt);

            db = new DatabaseHelper(getApplicationContext());

            btnCA = (Button) findViewById(R.id.btnBackCEVA);
            btnCA.setText("ถึง CEVA W/H");
            btnCA.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    adb1.setTitle("Confirm?");
                    adb1.setMessage("Plese Confirm เวลาถึง W/H CEVA");
                    adb1.setNegativeButton("Cancel", null);
                    adb1.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            // TODO Auto-generated method stub
                            //check pending order
                            int count = db.chkRecord_LTPending1(t_lt);
                            if (count >0){
                                Toast.makeText(getBaseContext(),"Can't Update. Order ยังมีค้างอยู่", Toast.LENGTH_SHORT).show();
                            }else{
                                db.updateArrival(t_lt + "-CEVA WH",t_lt);
                                btnCD.setVisibility(View.INVISIBLE);
                                btnCA.setVisibility(View.INVISIBLE);

                                //stopService(new Intent(getApplicationContext(), LocationService.class));
                            }

                        }
                    });
                    adb1.show();
                    //update ceva wh arrive time

                }
            });

            btnCD= (Button) findViewById(R.id.btnOutCEVA);
            btnCD.setText("ออกจาก CEVA W/H");
            btnCD.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    adb1.setTitle("Confirm?");
                    adb1.setMessage("Plese Confirm เวลาออก CEVA W/H");
                    adb1.setNegativeButton("Cancel", null);
                    adb1.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            // TODO Auto-generated method stub
                            //check pending order

                            db.updateDepart(t_lt + "-CEVA WH",t_lt);
                            btnCD.setVisibility(View.INVISIBLE);
                            btnCA.setVisibility(View.VISIBLE);

                            if (isMyServiceRunning(LocationService.class)==false)
                                startService(new Intent(getApplicationContext(), LocationService.class));


                        }
                    });
                    adb1.show();
                    //update ceva dispatch time

                }
            });

            new read_data1pd().execute();



            Button btnReload = (Button) findViewById(R.id.btnReload);
            btnReload.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new  read_data1pd().execute();
                    // lisView1.setAdapter(new CountryAdapter(TripActivity.this));
                }
            });

            Button btnBack = (Button) findViewById(R.id.btnGetItem);
            btnBack.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), TripPendingActivity.class);
                    startActivity(intent);

                }
            });
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
        public class CountryAdapter extends BaseAdapter
        {
            private Context context;



            public CountryAdapter(Context c)
            {
                //super( c, R.layout.activity_column, R.id.rowTextView, );
                // TODO Auto-generated method stub
                context = c;
            }

            public int getCount() {
                // TODO Auto-generated method stub
                return MyArrList.size();
            }

            public Object getItem(int position) {
                // TODO Auto-generated method stub
                return position;
            }

            public long getItemId(int position) {
                // TODO Auto-generated method stub
                return position;
            }
            public View getView(final int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub

                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.activity_order_column, null);

                }

                // ColLT
                TextView txtID = (TextView) convertView.findViewById(R.id.ColID);
                txtID.setText(MyArrList.get(position).get("LT"));
                TextView txtOrder = (TextView) convertView.findViewById(R.id.ColOrder);
                txtOrder.setText(MyArrList.get(position).get("Order"));
                if (!txtID.getText().equals("LT")){
                    Button cmdA = (Button) convertView.findViewById(R.id.CmdArrive);
                    Button cmdD = (Button) convertView.findViewById(R.id.CmdDepart);

                    if (!MyArrList.get(position).get("Arrive").equals("")){
                        cmdA.setVisibility(View.INVISIBLE);
                    }else{
                        cmdA.setVisibility(View.VISIBLE);
                        cmdA.setText("ถึง site");
                        //cmdShared.setBackgroundColor(Color.TRANSPARENT);
                        cmdA.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                //update and hide
                                final AlertDialog.Builder adb = new AlertDialog.Builder(OrderPendingActivity.this);
                                adb.setTitle("Confirm?");
                                adb.setMessage("Plese Confirm เวลาถึง Order " + MyArrList.get(position).get("Order"));
                                adb.setNegativeButton("Cancel", null);
                                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        // TODO Auto-generated method stub
                                        db.updateArrival(MyArrList.get(position).get("Order"),MyArrList.get(position).get("LT"));
                                        new OrderPendingActivity.read_data1pd().execute();
                                    }
                                });
                                adb.show();


                            }
                        });
                    }

                    if (!MyArrList.get(position).get("Depart").equals("")){
                        cmdD.setVisibility(View.INVISIBLE);
                    }else{
                        cmdD.setVisibility(View.VISIBLE);
                        cmdD.setText("ออกจาก site");
                        //cmdShared.setBackgroundColor(Color.TRANSPARENT);
                        cmdD.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                final AlertDialog.Builder adbd = new AlertDialog.Builder(OrderPendingActivity.this);
                                adbd.setTitle("Confirm?");
                                adbd.setMessage("Plese Confirm เวลาออก Order " + MyArrList.get(position).get("Order"));
                                adbd.setNegativeButton("Cancel", null);
                                adbd.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        //update and hide
                                        db.updateDepart(MyArrList.get(position).get("Order"),MyArrList.get(position).get("LT"));
                                        new OrderPendingActivity.read_data1pd().execute();
                                    }
                                });
                                adbd.show();

                            }
                        });
                    }

                }else{
                    Button cmdA = (Button) convertView.findViewById(R.id.CmdArrive);
                    cmdA.setVisibility(View.INVISIBLE);
                    Button cmdD = (Button) convertView.findViewById(R.id.CmdDepart);
                    cmdD.setVisibility(View.INVISIBLE);

                }
                return convertView;
            }

        }


        private class read_data1pd extends AsyncTask<Void,Void, Void> {
            JSONArray result;
            @Override
            protected  void onPreExecute(){

                super.onPreExecute();

            }
            @Override
            protected Void doInBackground(Void... arg0) {
                try{
                    result = db.SelectDeliverybyLT(t_lt);

                }catch (Exception ex){
                }
                return null;

            }


            @Override
            protected void onPostExecute(Void result1) {
                super.onPostExecute(result1);

                if (result== null){
                    // not found any order
                }else {

                    try {
                        MyArrList.clear();
                        JSONArray jsonarray = result;

                        HashMap<String, String> map;
                        map = new HashMap<String, String>();

                        map.put("LT", "LT");
                        map.put("Order", "Order");
                        map.put("Arrive", "Arrive");
                        map.put("Depart", "Depart");

                        /*map.put("", "");*/
                        MyArrList.add(map);

                        arrive=""; dispatch="";

                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);

                            if (jsonobject.getString("vin").contains("CEVA")){
                                arrive = jsonobject.getString("arrivaldt");
                                dispatch= jsonobject.getString("departdt");
                            }else{
                                map = new HashMap<String, String>();
                                map.put("LT", jsonobject.getString("ltid"));
                                map.put("Order", jsonobject.getString("vin"));
                                map.put("Arrive", jsonobject.getString("arrivaldt"));
                                map.put("Depart", jsonobject.getString("departdt"));
                                MyArrList.add(map);
                            }


                        }

                        if (arrive.equals("") && dispatch.equals("")){
                            btnCA.setVisibility(View.INVISIBLE);
                            btnCD.setVisibility(View.VISIBLE);
                        }else if (!arrive.equals("") && !dispatch.equals("")){
                            btnCA.setVisibility(View.INVISIBLE);
                            btnCD.setVisibility(View.INVISIBLE);
                            stopService(new Intent(getApplicationContext(), LocationService.class));
                        }else if (arrive.equals("") && !dispatch.equals("")){
                            btnCA.setVisibility(View.VISIBLE);
                            btnCD.setVisibility(View.INVISIBLE);
                            stopService(new Intent(getApplicationContext(), LocationService.class));
                            startService(new Intent(getApplicationContext(), LocationService.class));

                            //update status = D
                        }

                        lisView1.setAdapter(new OrderPendingActivity.CountryAdapter(OrderPendingActivity.this));

                    } catch (JSONException e) {
                        Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        @Override
        public void onBackPressed() {
        }
    }

