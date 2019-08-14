package com.thnopp.it.tms_ibm_mobile;

import android.app.Activity;
import android.content.Context;
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


public class TripPendingActivity   extends Activity {


    /**
     * Created by THLT88 on 10/20/2017.
     */



    ArrayList<HashMap<String, String>> MyArrList;
    String t_dealer,t_trailer;
    String sXML;

    ListView lisView1;
    DatabaseHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String username = prefs.getString("username","");
        Integer client = prefs.getInt("client",0);
        String conf = prefs.getString("conf","");
        t_dealer = prefs.getString("dealer","");
        t_trailer = username;
        String t_dealer_name = prefs.getString("dealer_name","");

        lisView1 = (ListView) findViewById(android.R.id.list);

        MyArrList = new ArrayList<HashMap<String, String>>();


        TextView cap = (TextView) findViewById(R.id.textView1);
        cap.setText("Dealer :" + t_dealer_name);
        //lisView1.setAdapter(new CountryAdapter(TripActivity.this));

        db = new DatabaseHelper(getApplicationContext());

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
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);

            }
        });
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
                convertView = inflater.inflate(R.layout.activity_trip_column, null);

            }

            // ColLT
            TextView txtID = (TextView) convertView.findViewById(R.id.ColLT);
            txtID.setText(MyArrList.get(position).get("LT"));

            TextView txtD = (TextView) convertView.findViewById(R.id.ColDealer);
            txtD.setVisibility(View.INVISIBLE);

            if (!txtID.getText().equals("LT")){
                Button cmdShared = (Button) convertView.findViewById(R.id.CmdProcess);
                cmdShared.setVisibility(View.VISIBLE);
                cmdShared.setText("Detail");
                //cmdShared.setBackgroundColor(Color.TRANSPARENT);
                cmdShared.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent newActivity = new Intent(TripPendingActivity.this,OrderPendingActivity.class);
                        newActivity.putExtra("LT", MyArrList.get(position).get("LT"));
                        startActivity(newActivity);
                    }
                });
            }else{
                Button cmdShared = (Button) convertView.findViewById(R.id.CmdProcess);
                cmdShared.setVisibility(View.INVISIBLE);
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
                result = db.SelectLT(t_trailer);

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

                    /*map.put("", "");*/
                    MyArrList.add(map);



                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);

                        map = new HashMap<String, String>();
                        map.put("LT", jsonobject.getString("ltid"));
                        MyArrList.add(map);

                    }

                    lisView1.setAdapter(new TripPendingActivity.CountryAdapter(TripPendingActivity.this));

                } catch (JSONException e) {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
                // Toast.makeText(getBaseContext(), "Asset Load Complete", Toast.LENGTH_SHORT).show();

            }


        }

    }

    @Override
    public void onBackPressed() {
    }
}

