package com.example.dhiraj.messagepassing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    MyReceiver myReceiver;
    TextView tx;
    SQLiteDatabase db;
    int on = 0;
    float[] values1 = new float[10];
    float[] values2 = new float[10];
    float[] values3 = new float[10];
    String[] verlabels = new String[]{"9","8","7", "6", "5", "4", "3", "2","1","0"};
    String[] horlabels = new String[]{"0", "1", "2", "3", "4", "5", "6","7","8","9"};
    GraphView g;
    private Handler mHandler;
    LinearLayout l;
    public static final String  DATABASE_FILE_PATH = "/sdcard";
    public static final String  DATABASE_NAME = "testDatabase";
    public static final String  TABLE = "accel";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String sdpath,sd1path,usbdiskpath, sd0path;
        if (new File("/storage/extSdCard/").exists()) {
            sdpath = "/storage/extSdCard/";
            Log.i("Sd Cardext Path",sdpath);
        }
        if(new File("/storage/sdcard1/").exists())
        {
            sd1path="/storage/sdcard1/";
            Log.i("Sd Card1 Path",sd1path);
        }
        if(new File("/storage/usbcard1/").exists())
        {
            usbdiskpath="/storage/usbcard1/";
            Log.i("USB Path",usbdiskpath);
        }
        if(new File("/storage/sdcard0/").exists())
        {
            sd0path="/storage/sdcard0/";
            Log.i("Sd Card0 Path",sd0path);
        }
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Mydata");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + " af0 " + DATABASE_FILE_PATH + " ");
        db = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory() + File.separator +"Mydata"+File.separator+ DATABASE_NAME, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tx = (TextView)findViewById(R.id.textView);
        l = (LinearLayout) findViewById(R.id.lay);
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
        g = new GraphView(this, values1,values2,values3, "TEST", horlabels, verlabels, GraphView.LINE);
        //l.addView(g);
        Button b1 = (Button)findViewById(R.id.button2);
        System.out.print("starting broadcast");
        myReceiver = new MyReceiver();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.removeView(g);
                getlast();
                g.setValues(values1, values2, values3);
                l.addView(g);
            }
        });

        Button b2 = (Button)findViewById(R.id.button);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(MyService.MY_ACTION);
                if(on == 0) {
                    on = 1;
                    registerReceiver(myReceiver, intentFilter);
                }
                //Start our own service


            }
        });
        Button b3 = (Button)findViewById(R.id.button3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.print("stopings broadcast");
                if(on == 1) {
                    on = 0;
                    unregisterReceiver(myReceiver);
                    //Start our own service
                }

            }
        });




    }

    private void getlast() {
        String query = "SELECT  * FROM " + TABLE + " ORDER BY created_at desc";
        Cursor cursor = null;
        // 2. get reference to writable DB
        int l = 0;
        try {
            //perform your database operations here ...
            //cursor = db.rawQuery("select * from accel order by created_at desc", null);
            cursor = db.rawQuery(query, null);

            db.setTransactionSuccessful(); //commit your changes
        } catch (Exception e) {
            System.out.print("Error is " + e);
            //report problem
        }


        // 3. go over each row, build book and add it to list

        if (cursor.moveToFirst()) {
            do {
               values1[l] = Float.parseFloat(cursor.getString(1));
                values2[l] = Float.parseFloat(cursor.getString(2));
                values3[l] = Float.parseFloat(cursor.getString(3));
                l++;
            } while (cursor.moveToNext() && l <10);
        }

        //Log.d("getAllBooks()", books.toString());

    }

    protected void onStart() {
        // TODO Auto-generated method stub



        try{

            //db = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory()+"/mydb",null);
            db.beginTransaction();
            try {
                //perform your database operations here ...
                db.execSQL("create table accel ("
                        + " created_at DATETIME DEFAULT CURRENT_TIMESTAMP, "
                        + " x float, "
                        + " y float,"
                        + " z float"
                        +
                        " ); " );

                db.setTransactionSuccessful(); //commit your changes
            } catch (SQLiteException e) {
                //report problem
            }
            finally {
                db.endTransaction();
            }
        }catch (SQLException e){

            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }




        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        if(on == 1){
        unregisterReceiver(myReceiver);}
        super.onStop();
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            float x = arg1.getFloatExtra("X", 0.0f);
            float y = arg1.getFloatExtra("Y", 0.0f);
            float z = arg1.getFloatExtra("Z", 0.0f);
            //TextView tv = (TextView) findViewById(R.id.textView);
            Log.d("msg", "hi");
            tx.setText(""+x+""+y+""+z);
            try {
                //perform your database operations here ...
                db.execSQL( "insert into accel(x,y,z) values ('"+x+"', '"+y+"','"+z+"' );" );
                //db.setTransactionSuccessful(); //commit your changes
            }
            catch (SQLiteException e) {
                //report problem
            }
            finally {
                //db.endTransaction();
            }

            //select * from tbl_name order by id desc

        }

    }
}