package com.example.dhiraj.messagepassing;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service implements SensorEventListener {
    private SensorManager accelManage;
    private Sensor senseAccel;
    float accelValuesX = 0.0f;
    float accelValuesY = 0.0f;
    float accelValuesZ = 0.0f;
    int index = 0;

    final static String MY_ACTION = "MY_ACTION";

    @Override
    public void onCreate(){
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        accelManage = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senseAccel = accelManage.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelManage.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_NORMAL);

    }
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub



        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;


        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            index++;
            accelValuesX = event.values[0];
            accelValuesY = event.values[1];
            accelValuesZ = event.values[2];
            Intent intent = new Intent();
            intent.setAction(MY_ACTION);

            intent.putExtra("X", accelValuesX);
            intent.putExtra("Y", accelValuesY);
            intent.putExtra("Z", accelValuesZ);

            sendBroadcast(intent);

            /*if(index == 10){
                accelManage.unregisterListener(this);

            }*/

            }
        }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



}