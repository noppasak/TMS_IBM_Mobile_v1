package com.thnopp.it.tms_ibm_mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class AddOrderActivity extends Activity {

    TextView lbltype, lbluser;
    Button scan, next, back,reload;
    EditText location, qty;
    DatabaseHelper db;
    Long HID ;
    String Bal, ID,HRef;
    String t_detid;
    String t_location, t_qty;
    RadioGroup rg;
    ArrayList<HashMap<String, String>> MyArrList;
    ListView lisView1;
    RadioButton c1, c2,c3,c4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addorder);
        db = new DatabaseHelper(getApplicationContext());

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String username = prefs.getString("username","");
        Integer client = prefs.getInt("client",0);
        String conf = prefs.getString("password","");
        Global.user = username;
        Global.client = client;
        Global.conf = conf;
        Global.status = "";

        lisView1 = (ListView) findViewById(android.R.id.list);

        // ArrayList<Dua> arrayList = new ArrayList<>();
        MyArrList = new ArrayList<HashMap<String, String>>();
        new read_item_bal().execute();

        //assign value into label

        lbluser = (TextView) findViewById(R.id.lbluser);

        if (lbluser.getText() != null)
            lbluser.setText(Global.user);


        if (Global.user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        c1 = (RadioButton) findViewById(R.id.c1);
        c2 = (RadioButton) findViewById(R.id.c2);
        c3 = (RadioButton) findViewById(R.id.c3);
        c4 = (RadioButton) findViewById(R.id.c4);

        location = (EditText) findViewById(R.id.txtorder);
        location.setText(null);


        scan = (Button) findViewById(R.id.buttonScan);
        next = (Button) findViewById(R.id.buttonNext);
        back = (Button) findViewById(R.id.buttonBack);
        reload = (Button) findViewById(R.id.buttonReload);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new read_item_bal().execute();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
            }
        });


        location.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    if (location.getText().toString().equals("")) {
                        Toast.makeText(AddOrderActivity.this, "กรุณากรอก Order", Toast.LENGTH_SHORT).show();
                    } else if (location.getText().toString().length()==0){
                        Toast.makeText(AddOrderActivity.this, "กรุณากรอก Order", Toast.LENGTH_SHORT).show();
                    }else{
                        if (c1.isChecked()) {
                            Calendar c = Calendar.getInstance();

                            int digyear = c.get(Calendar.YEAR);
                            String yrStr = Integer.toString(digyear);
                            String yrStrEnd = yrStr.substring(yrStr.length() - 2);

                            HRef = yrStrEnd + "-" + location.getText();
                        }
                        else if (c2.isChecked()) {
                            HRef= "T" + location.getText();
                        }else if (c3.isChecked()) {
                            HRef= "A" + location.getText();
                        }
                        else if (c4.isChecked()) {
                            HRef= "N" + location.getText();

                        }

                        new AddOrderActivity.add_order().execute();
                    }


                }else if(keyCode == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace

                    return false;
                }

                return true;
            }
            //  return false;
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
            txtID.setText(MyArrList.get(position).get("ID"));

            // ColDealer
            TextView txtCode = (TextView) convertView.findViewById(R.id.ColDealer);
            txtCode.setText(MyArrList.get(position).get("Order"));

            Button cmdShared = (Button) convertView.findViewById(R.id.CmdProcess);

            if ((!txtID.getText().equals("ID")) && (!txtCode.getText().toString().contains("CEVA"))){


                cmdShared.setVisibility(View.VISIBLE);
                cmdShared.setText("ลบ");
                //cmdShared.setBackgroundColor(Color.TRANSPARENT);
                cmdShared.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        final AlertDialog.Builder adbd = new AlertDialog.Builder(AddOrderActivity.this);
                        adbd.setTitle("Confirm?");
                        adbd.setMessage("Confirm การลบ " + MyArrList.get(position).get("Order"));
                        adbd.setNegativeButton("Cancel", null);
                        adbd.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {
                                //update and hide
                                //check order

                                HRef=MyArrList.get(position).get("Order");
                                int chk = db.chkRecord_OrderPending(HRef);
                                if (chk > 0){
                                    new del_order().execute();
                                }else{
                                    Toast.makeText(getBaseContext(),"Can't Delete. Order already update Time.", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });
                        adbd.show();

                    }
                });

            }else{
                // cmdOK.setVisibility(View.INVISIBLE);
                cmdShared.setVisibility(View.INVISIBLE);
            }

            return convertView;

        }

    }
    public class add_order extends AsyncTask<Void, Void, Void> {
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
                result = rs.AddOrder(Global.user,HRef);

            }catch (Exception ex){
//                Toast.makeText(getBaseContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result2) {
            super.onPostExecute(result2);
            Toast.makeText(getBaseContext(),result, Toast.LENGTH_SHORT).show();
            location.setText("");
            new AddOrderActivity.read_item_bal().execute();
          /*  if (result== null){
              //  Toast.makeText(getBaseContext(),"Can't post the POD Data",Toast.LENGTH_LONG).show();
            }*/
        }
    }

    public class del_order extends AsyncTask<Void, Void, Void> {
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
                result = rs.DelOrder(Global.user,HRef);

            }catch (Exception ex){
//                Toast.makeText(getBaseContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result2) {
            super.onPostExecute(result2);
            Toast.makeText(getBaseContext(),result, Toast.LENGTH_SHORT).show();
            db.DeleteVIN(HRef);
            location.setText("");

            new AddOrderActivity.read_item_bal().execute();
          /*  if (result== null){
              //  Toast.makeText(getBaseContext(),"Can't post the POD Data",Toast.LENGTH_LONG).show();
            }*/
        }
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

            RequestQueue queue = Volley.newRequestQueue(AddOrderActivity.this);
            StringRequest request = new StringRequest(Request.Method.POST, Config.GET_ORDER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    //   Toast.makeText(FModelActivity.this, "my success"+response, Toast.LENGTH_LONG).show();
                    Log.i("My success",""+response);

                    try {
                        MyArrList.clear();
                        JSONArray jsonarray = new JSONArray(response);
                        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                        String username = prefs.getString("username","");

                        HashMap<String, String> map;
                        map = new HashMap<String, String>();

                        map.put("ID", "ID");
                        map.put("Order", "Order");


                        MyArrList.add(map);

                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);

                            map = new HashMap<String, String>();

                            map.put("ID", jsonobject.getString("delid"));
                            map.put("Order", jsonobject.getString("vin"));

                            Vinmaster v = new Vinmaster();
                            v.setVin(jsonobject.getString("vin"));
                            v.setId(jsonobject.getString("delid"));
                            v.setLtcode(jsonobject.getString("ltid"));
                            v.setStatus("A");
                            v.setTrailer(username);

                            db.InsertDelivery(v);

                            MyArrList.add(map);
                        }

                        lisView1.setAdapter(new AddOrderActivity.CountryAdapter(AddOrderActivity.this));

                    } catch (JSONException e) {
                        Log.e("", "unexpected JSON exception", e);
                        // Do something to recover ... or kill the app.
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AddOrderActivity.this, "my error :"+error, Toast.LENGTH_LONG).show();
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



    @Override
    public void onBackPressed() {
    }
}
