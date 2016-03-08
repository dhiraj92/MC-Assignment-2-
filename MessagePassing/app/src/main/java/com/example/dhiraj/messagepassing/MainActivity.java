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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    MyReceiver myReceiver;
    TextView txtStatus;
    SQLiteDatabase db;
    int on = 0;
    float[] Xvalues = new float[10];
    float[] Yvalues = new float[10];
    float[] Zvalues = new float[10];
    String[] verlabels = new String[]{"9", "8", "7", "6", "5", "4", "3", "2", "1", "0"};
    String[] horlabels = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    GraphView g;
    private Handler mHandler;
    LinearLayout l;
    //    public static final String  DATABASE_FILE_PATH = "/sdcard";
    public static final String DATABASE_NAME = "svellangDatabase";
    public static final String DATABASE_LOCATION = Environment.getExternalStorageDirectory() + File.separator + "Mydata" + File.separator + DATABASE_NAME;
    public static final String TABLE = "accel";
    int serverResponseCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mydata");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        db = SQLiteDatabase.openOrCreateDatabase(DATABASE_LOCATION, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtStatus = (TextView) findViewById(R.id.textView);
        l = (LinearLayout) findViewById(R.id.lay);
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
        g = new GraphView(this, Xvalues, Yvalues, Zvalues, "Acceleration", horlabels, verlabels, GraphView.LINE);
        Button dispButton = (Button) findViewById(R.id.dispButton);
        System.out.print("starting broadcast");
        myReceiver = new MyReceiver();

        dispButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.removeView(g);
                getlast();
                g.setValues(Xvalues, Yvalues, Zvalues);
                l.addView(g);
            }
        });

        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(MyService.MY_ACTION);
                if (on == 0) {
                    on = 1;
                    registerReceiver(myReceiver, intentFilter);
                }
                //Start our own service


            }
        });
        Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.print("stopings broadcast");
                if (on == 1) {
                    on = 0;
                    unregisterReceiver(myReceiver);
                    //Start our own service
                }

            }
        });

        Button uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog = ProgressDialog.show(UploadToServer.this, "", "Uploading file...", true);
                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                txtStatus.setText("uploading started.....");
                            }
                        });

                        uploadFile(DATABASE_LOCATION, "https://impact.asu.edu/CSE535Spring16Folder/UploadToServer.php", DATABASE_NAME);
                    }
                }).start();
            }
        });
    }

    //@Begin upload
    public int uploadFile(final String sourceFileUri, String strDestinationUri, String fileName) {
        final String uploadErrorMsg="Upload failed.";
        //Referred to http://tinyurl.com/or8wql2
        HttpsURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :"
                    + sourceFileUri);

            runOnUiThread(new Runnable() {
                public void run() {
                    txtStatus.setText("Source File not exist :"
                            + sourceFileUri);
                }
            });

            return 0;

        } else {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);


                // Create a trust manager that does not validate certificate chains
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }

                            public void checkClientTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                            }

                            public void checkServerTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                            }
                        }
                };

// Install the all-trusting trust manager
                try {
                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                } catch (Exception e) {
                }
                URL url = new URL(strDestinationUri);


                // Open a HTTP  connection to  the URL
                conn = (HttpsURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.";

                            txtStatus.setText(msg);
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        txtStatus.setText(uploadErrorMsg);
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        txtStatus.setText(uploadErrorMsg);
                    }
                });
                Log.e("File upload Exception", "Exception : " + e.getMessage(), e);
            }
            return serverResponseCode;

        } // End else block
    }
    //@End upload


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
                Xvalues[l] = Float.parseFloat(cursor.getString(1));
                Yvalues[l] = Float.parseFloat(cursor.getString(2));
                Zvalues[l] = Float.parseFloat(cursor.getString(3));
                l++;
            } while (cursor.moveToNext() && l < 10);
        }

        //Log.d("getAllBooks()", books.toString());

    }

    protected void onStart() {
        // TODO Auto-generated method stub


        try {

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
                        " ); ");

                db.setTransactionSuccessful(); //commit your changes
            } catch (SQLiteException e) {
                //report problem
            } finally {
                db.endTransaction();
            }
        } catch (SQLException e) {

            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        if (on == 1) {
            unregisterReceiver(myReceiver);
        }
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
            txtStatus.setText("" + x + "" + y + "" + z);
            try {
                //perform your database operations here ...
                db.execSQL("insert into accel(x,y,z) values ('" + x + "', '" + y + "','" + z + "' );");
                //db.setTransactionSuccessful(); //commit your changes
            } catch (SQLiteException e) {
                //report problem
            } finally {
                //db.endTransaction();
            }

            //select * from tbl_name order by id desc

        }

    }
}