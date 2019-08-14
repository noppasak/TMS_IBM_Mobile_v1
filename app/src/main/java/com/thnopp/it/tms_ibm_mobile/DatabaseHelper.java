package com.thnopp.it.tms_ibm_mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by THLT88 on 2/7/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {


    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "TMSDB_IBM";

    // Table Names


    private static final String TABLE_DELIVERY = "delivery";

    private static final String TABLE_LT = "deliverylt";



    // Common column names

    // delivery column names

    private static final String KEY_DEL_ID = "delid";
    private static final String KEY_DEL_ORDER = "vin";
    private static final String KEY_DEL_ARRIVAL = "arrivaldt";
    private static final String KEY_DEL_DEPART = "departdt";
    private static final String KEY_DEL_STATUS = "status";
    private static final String KEY_DEL_TRAILER = "trailer";
    // lt column
    private static final String KEY_LT_ID = "ltid";
    private static final String KEY_LT_STATUS = "status";


    // Table Create Statements
    // upload picture table create statement
    private static final String CREATE_TABLE_DELIVERY = "CREATE TABLE "
            + TABLE_DELIVERY + "(" + KEY_DEL_ID + " TEXT PRIMARY KEY ," + KEY_DEL_ORDER + " TEXT  , " +
            ""  + KEY_DEL_STATUS + " TEXT , " + KEY_LT_ID + " TEXT , "+ KEY_DEL_ARRIVAL + " DATETIME  , "
            + KEY_DEL_TRAILER + " TEXT  , "  + KEY_DEL_DEPART + " DATETIME" + ")";

    // master table create statement
    private static final String CREATE_TABLE_LT = "CREATE TABLE "
            + TABLE_LT + "(" + KEY_LT_ID + " TEXT PRIMARY KEY," + KEY_LT_STATUS
            + " TEXT )";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);


    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_DELIVERY);
        db.execSQL(CREATE_TABLE_LT);

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELIVERY);
        // create new tables
        onCreate(db);
    }

    //----DDL ----//


    public void InsertDelivery(Vinmaster v){
        SQLiteDatabase db = this.getWritableDatabase();
        Vinmaster chkV = findVIN(v.getVin(),v.getTrailer());
        if (chkV != null){
            if (chkV.getScandt() ==null){
                // delALLVIN_by_VIN(chkV.getVin());
                // do nothing
            }

        }else{
            ContentValues values = new ContentValues();
            values.put(KEY_DEL_ID, v.getId());
            values.put(KEY_DEL_ORDER, v.getVin());
            values.put(KEY_LT_ID, v.getLtcode());
            values.put(KEY_DEL_STATUS, v.getStatus());
            values.put(KEY_DEL_TRAILER, v.getTrailer());
            // values.put(KEY_MASTER_SCANDT, v.getVin());
            //  values.put(KEY_MASTER_SCANDT, getDateTime(v.getScandt()));

            db.insert(TABLE_DELIVERY,null,values);
        }

    }


    private String getDateTime(Date datevalue) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date =datevalue;
        return dateFormat.format(date);
    }


    public void updateDepart(String v, String lt){
        Date cur = new Date();

        SQLiteDatabase db = this.getReadableDatabase();
        //check ceva wh
        //if ceva wh not depart , not allow to update
        if (v.equals(lt + "-CEVA WH")){
            String strSQL = "UPDATE " + TABLE_DELIVERY + " SET " + KEY_DEL_DEPART + " = '" +
                    getDateTime(cur) + "' WHERE " + KEY_DEL_ORDER + " = '"+ v +"' and " + KEY_DEL_DEPART  + " IS NULL";

            db.execSQL(strSQL);
        }else{
            Vinmaster chkv = findVINNonAbyVIN(v);


            if (chkv == null){
                int count = chkArriveCEVA(v,lt);
                int count_d = chkDepartCEVA(v,lt);

                if (count ==0 && count_d >0){
                    String strSQL = "UPDATE " + TABLE_DELIVERY + " SET " + KEY_DEL_DEPART + " = '" +
                            getDateTime(cur) + "' WHERE " + KEY_DEL_ORDER + " = '"+ v +"' and " + KEY_DEL_DEPART  + " IS NULL";

                    db.execSQL(strSQL);
                }
            }else{
                //  public int chkDepartCEVA(String vin,String lt){


            }
        }




    }

    public void updateArrival(String v, String lt){
        Date cur = new Date();
        //if this order is ceva wh, check all order is complete or not
        //if not, not update
        SQLiteDatabase db = this.getReadableDatabase();
        int count=0, count_a=0;

        if (v.equals(lt +"-CEVA WH")){
            count =  chkDepartCEVA(v,lt);
            if (count ==0){
                String strSQL = "UPDATE " + TABLE_DELIVERY + " SET " + KEY_DEL_ARRIVAL + " = '" +
                        getDateTime(cur) + "' WHERE " + KEY_DEL_ORDER + " = '"+ v +"' and " + KEY_DEL_ARRIVAL  + " IS NULL";
                db.execSQL(strSQL);

            }

        }else{
            count =  chkDepartCEVA(v,lt);
            count_a = chkDepartCEVAWH(lt);
            if (count >=1 && count_a ==0){
                String strSQL = "UPDATE " + TABLE_DELIVERY + " SET " + KEY_DEL_ARRIVAL + " = '" +
                        getDateTime(cur) + "' WHERE " + KEY_DEL_ORDER + " = '"+ v +"' and " + KEY_DEL_ARRIVAL  + " IS NULL";
                db.execSQL(strSQL);

            }

        }


    }

    public void updateVIN_upload_status(String v){
        SQLiteDatabase db = this.getReadableDatabase();
        String strSQL = "UPDATE " + TABLE_DELIVERY + " SET " + KEY_DEL_STATUS + " = 'U'" +
                " WHERE " + KEY_DEL_ORDER + " = '"+ v +"'";

        db.execSQL(strSQL);

    }

    public void DeleteVIN(String v){
        SQLiteDatabase db = this.getReadableDatabase();
        String strSQL = "DELETE FROM " + TABLE_DELIVERY +
                " WHERE " + KEY_DEL_ORDER + " = '"+ v +"' AND " + KEY_DEL_DEPART + " IS NULL" ;

        db.execSQL(strSQL);

    }


    public void DeleteVIN_Old(String v){
        SQLiteDatabase db = this.getReadableDatabase();
        Date now = new Date();
        Long days = 5L;
        now.setTime( now.getTime() - days*1000*60*60*24 );

        android.text.format.DateFormat df = new android.text.format.DateFormat();


        String strSQL = "DELETE FROM " + TABLE_DELIVERY +
                " WHERE " + KEY_DEL_DEPART + "<= '" + df.format("yyyy-MM-dd hh:mm:ss",now) + "'" ;

        db.execSQL(strSQL);

    }


    public JSONArray SelectDeliverybyLT(String LT){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String strSQL = "SELECT  * FROM " + TABLE_DELIVERY + " WHERE " + KEY_LT_ID + " = '"+LT+"'";
            Cursor c = db.rawQuery(strSQL, null);
            JSONArray resultSet     = new JSONArray();
            c.moveToFirst();
            while (c.isAfterLast() == false){

                int totalColumn = c.getColumnCount();
                JSONObject rowObject = new JSONObject();

                for( int i=0 ;  i< totalColumn ; i++ )
                {
                    if( c.getColumnName(i) != null )
                    {
                        try
                        {
                            if( c.getString(i) != null )
                            {
                                rowObject.put(c.getColumnName(i) ,  c.getString(i) );
                            }
                            else
                            {
                                rowObject.put( c.getColumnName(i) ,  "" );
                            }
                        }
                        catch( Exception e )
                        {
                            Log.d("TAG_NAME", e.getMessage()  );
                        }
                    }
                }
                resultSet.put(rowObject);
                c.moveToNext();
            }
            c.close();
            return resultSet;
        } catch (Exception e){
            return null;
        }
    }

    public JSONArray SelectLT(String user){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String strSQL = "SELECT  DISTINCT "+ KEY_LT_ID +" FROM " + TABLE_DELIVERY + " WHERE " + KEY_DEL_STATUS + " = 'A' "
                    + " AND " + KEY_DEL_TRAILER +" = '" + user +"'";
            Cursor c = db.rawQuery(strSQL, null);
            JSONArray resultSet     = new JSONArray();
            c.moveToFirst();
            while (c.isAfterLast() == false){

                int totalColumn = c.getColumnCount();
                JSONObject rowObject = new JSONObject();

                for( int i=0 ;  i< totalColumn ; i++ )
                {
                    if( c.getColumnName(i) != null )
                    {
                        try
                        {
                            if( c.getString(i) != null )
                            {
                                rowObject.put(c.getColumnName(i) ,  c.getString(i) );
                            }
                            else
                            {
                                rowObject.put( c.getColumnName(i) ,  "" );
                            }
                        }
                        catch( Exception e )
                        {
                            Log.d("TAG_NAME", e.getMessage()  );
                        }
                    }
                }
                resultSet.put(rowObject);
                c.moveToNext();
            }
            c.close();
            return resultSet;
        } catch (Exception e){
            return null;
        }
    }





    public int chkRecord_LTPending(String LT){
        int result = 0;
        List<Scanvin> todos = new ArrayList<Scanvin>();
        String selectQuery = "SELECT  COUNT(" + KEY_DEL_ID +") AS " + KEY_DEL_ID + " FROM " + TABLE_DELIVERY + " WHERE "
                + KEY_DEL_STATUS + " = 'A' AND " + KEY_LT_ID + " = '"+ LT + "'";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                //  result = Long.parseLong(c.getString(c.getColumnIndex(KEY_DESC1)));
                result = c.getInt(c.getColumnIndex(KEY_DEL_ID)); // getLong(c,c.getColumnIndex(KEY_DESC1));
            } while (c.moveToNext());
        }

        return result;
    }

    public int chkRecord_LTPending1(String LT){
        int result = 0;
        List<Scanvin> todos = new ArrayList<Scanvin>();
        String selectQuery = "SELECT  COUNT(" + KEY_DEL_ID +") AS " + KEY_DEL_ID + " FROM " + TABLE_DELIVERY + " WHERE "
                + "("+ KEY_DEL_ARRIVAL + " IS NULL AND " + KEY_DEL_DEPART + " IS NULL) AND " + KEY_LT_ID + " = '"+ LT + "'";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                //  result = Long.parseLong(c.getString(c.getColumnIndex(KEY_DESC1)));
                result = c.getInt(c.getColumnIndex(KEY_DEL_ID)); // getLong(c,c.getColumnIndex(KEY_DESC1));
            } while (c.moveToNext());
        }

        return result;
    }

    public int chkRecord_OrderPending(String LT){
        int result = 0;
        List<Scanvin> todos = new ArrayList<Scanvin>();
        String selectQuery = "SELECT  COUNT(" + KEY_DEL_ID +") AS " + KEY_DEL_ID + " FROM " + TABLE_DELIVERY + " WHERE ("
                + KEY_DEL_ARRIVAL + " IS NULL AND " + KEY_DEL_DEPART + " IS NULL) AND " + KEY_DEL_ORDER + " = '"+ LT + "'";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                //  result = Long.parseLong(c.getString(c.getColumnIndex(KEY_DESC1)));
                result = c.getInt(c.getColumnIndex(KEY_DEL_ID)); // getLong(c,c.getColumnIndex(KEY_DESC1));
            } while (c.moveToNext());
        }

        return result;
    }
    public int chkDepartCEVA(String vin, String lt){
        int result = 0;
        List<Scanvin> todos = new ArrayList<Scanvin>();
        String selectQuery = "SELECT  COUNT(" + KEY_DEL_ORDER + ") AS " + KEY_DEL_ORDER + " FROM " + TABLE_DELIVERY + " WHERE "
                + KEY_DEL_ORDER + " = '" + vin + "' AND " + KEY_LT_ID + " ='" + lt +"' AND "
                + KEY_DEL_DEPART + " IS NULL";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                result = c.getInt(c.getColumnIndex(KEY_DEL_ORDER)); // getLong(c,c.getColumnIndex(KEY_DESC1));
            } while (c.moveToNext());
        }

        return result;
    }

    public int chkDepartCEVAWH(String lt){
        int result = 0;
        List<Scanvin> todos = new ArrayList<Scanvin>();
        String selectQuery = "SELECT  COUNT(" + KEY_DEL_ORDER + ") AS " + KEY_DEL_ORDER + " FROM " + TABLE_DELIVERY + " WHERE "
                + KEY_DEL_ORDER + " like '%CEVA%' AND " + KEY_LT_ID + " ='" + lt +"' AND "
                + KEY_DEL_DEPART + " IS NULL";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                result = c.getInt(c.getColumnIndex(KEY_DEL_ORDER)); // getLong(c,c.getColumnIndex(KEY_DESC1));
            } while (c.moveToNext());
        }

        return result;
    }

    public int chkArriveCEVA(String vin, String lt){
        int result = 0;
        List<Scanvin> todos = new ArrayList<Scanvin>();
        String selectQuery = "SELECT  COUNT(" + KEY_DEL_ORDER + ") AS " + KEY_DEL_ORDER + " FROM " + TABLE_DELIVERY + " WHERE "
                + KEY_DEL_ORDER + " = '" + vin + "' AND " + KEY_LT_ID + " ='" + lt +"' AND "
                + KEY_DEL_ARRIVAL + " IS NULL";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                result = c.getInt(c.getColumnIndex(KEY_DEL_ORDER)); // getLong(c,c.getColumnIndex(KEY_DESC1));
            } while (c.moveToNext());
        }

        return result;
    }

    public int chkPendingPOD(){
        int result = 0;
        List<Scanvin> todos = new ArrayList<Scanvin>();
        String selectQuery = "SELECT  COUNT(" + KEY_DEL_ORDER + ") AS " + KEY_DEL_ORDER + " FROM " + TABLE_DELIVERY + " WHERE "
                + KEY_DEL_STATUS + "='A'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                result = c.getInt(c.getColumnIndex(KEY_DEL_ORDER)); // getLong(c,c.getColumnIndex(KEY_DESC1));
            } while (c.moveToNext());
        }

        return result;
    }


    public long getLong(Cursor cursor, int columnIndex )
    {
        long value = 0;

        try
        {
            if ( !cursor.isNull( columnIndex ) )
            {
                value = cursor.getLong( columnIndex );
            }
        }
        catch ( Throwable tr )
        {

        }

        return value;
    }
    public List<Vinmaster> getPOD() {
        List<Vinmaster> todos = new ArrayList<Vinmaster>();
        String selectQuery = "SELECT  * FROM " + TABLE_DELIVERY +" WHERE " + KEY_DEL_STATUS + "='A' and " + KEY_DEL_ARRIVAL +
                " IS NOT NULL AND " + KEY_DEL_DEPART + " IS NOT NULL";

        // String selectQuery = "SELECT  * FROM " + TABLE_DELIVERY +" WHERE " + KEY_DEL_STATUS + "='A'";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                String s= c.getString(c.getColumnIndex(KEY_DEL_ARRIVAL));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
                Date d=new Date();

                try {
                    d=  dateFormat.parse(s);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                    s="N";
                }


                Date d1=new Date();
                String s1= c.getString(c.getColumnIndex(KEY_DEL_DEPART));

                try {
                    d1=  dateFormat.parse(s1);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                    s1 = "N";
                }



                Vinmaster td = new Vinmaster();
                td.setId(c.getString((c.getColumnIndex(KEY_DEL_ID))));
                if (!s.equals("N"))
                    td.setArrivaldt(d);
                if (!s1.equals("N"))
                    td.setScandt(d1);
                td.setVin(c.getString(c.getColumnIndex(KEY_DEL_ORDER)));
                td.setLtcode(c.getString(c.getColumnIndex(KEY_LT_ID)));




                // adding to todo list
                todos.add(td);
            } while (c.moveToNext());
        }

        return todos;
    }

    public List<Vinmaster> getVIN_Old_data() {
        List<Vinmaster> todos = new ArrayList<Vinmaster>();
        Date m = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(m);
        cal.add(Calendar.DATE, -5); // 10 is the days you want to add or subtract
        m = cal.getTime();


        String t_d = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd H:mm:ss", m));


        String selectQuery = "SELECT  * FROM " + TABLE_DELIVERY +" WHERE " + KEY_DEL_DEPART + " IS NOT NULL  AND "
                +  KEY_DEL_DEPART + " <= '" + t_d +"';";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {


                Vinmaster td = new Vinmaster();
                td.setId(c.getString((c.getColumnIndex(KEY_DEL_ID))));
                td.setVin(c.getString(c.getColumnIndex(KEY_DEL_ORDER)));
                td.setLtcode(c.getString(c.getColumnIndex(KEY_LT_ID)));


                // adding to todo list
                todos.add(td);
            } while (c.moveToNext());
        }

        return todos;
    }

    public Vinmaster findVIN(String id, String trailer){
        List<Vinmaster> todos = new ArrayList<Vinmaster>();
        SQLiteDatabase db = this.getReadableDatabase();
        Vinmaster C1 = null;
        Cursor cursor;
        String selectQuery = "SELECT   * FROM " + TABLE_DELIVERY +" WHERE " + KEY_DEL_ID + " = '" + id + "'";


        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                C1 = new Vinmaster();
                C1.setId(c.getString((c.getColumnIndex(KEY_DEL_ID))));
                C1.setVin(c.getString(c.getColumnIndex(KEY_DEL_ORDER)));
                C1.setLtcode(c.getString(c.getColumnIndex(KEY_LT_ID)));
                todos.add(C1);
            } while (c.moveToNext());
        }

        return C1;


    }

    public Vinmaster findVINbyVIN(String id){
        List<Vinmaster> todos = new ArrayList<Vinmaster>();
        SQLiteDatabase db = this.getReadableDatabase();
        Vinmaster C1 = null;
        Cursor cursor;
        String selectQuery = "SELECT   * FROM " + TABLE_DELIVERY +" WHERE " + KEY_DEL_ORDER + " = '" + id + "'";

        Cursor c = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                C1 = new Vinmaster();
                C1.setId(c.getString((c.getColumnIndex(KEY_DEL_ID))));
                C1.setVin(c.getString(c.getColumnIndex(KEY_DEL_ORDER)));
                C1.setLtcode(c.getString(c.getColumnIndex(KEY_LT_ID)));

                todos.add(C1);
            } while (c.moveToNext());
        }

        return C1;


    }

    public Vinmaster findVINNonAbyVIN(String id){
        List<Vinmaster> todos = new ArrayList<Vinmaster>();
        SQLiteDatabase db = this.getReadableDatabase();
        Vinmaster C1 = null;
        Cursor cursor;
        String selectQuery = "SELECT   * FROM " + TABLE_DELIVERY +" WHERE " + KEY_DEL_ORDER + " = '" + id + "' AND "
                + "" + KEY_DEL_ARRIVAL + " IS NULL";

        Cursor c = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                C1 = new Vinmaster();
                C1.setId(c.getString((c.getColumnIndex(KEY_DEL_ID))));
                C1.setVin(c.getString(c.getColumnIndex(KEY_DEL_ORDER)));
                C1.setLtcode(c.getString(c.getColumnIndex(KEY_LT_ID)));

                todos.add(C1);
            } while (c.moveToNext());
        }

        return C1;


    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}
