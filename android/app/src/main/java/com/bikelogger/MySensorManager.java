package com.bikelogger;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.content.Context;

import com.bikelogger.MyBufferListener;
// import com.rnbikemap.BufferedFileWriter;

public class MySensorManager implements SensorEventListener {
    private Context mContext;
    private int countEvents; 
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    // private BufferedFileWriter fileWriter;
    private StringBuffer sBuffer;
    // buffer size - number of rows in the buffer
    private int mBufferSize = 100;
    
    // The listener must implement the events interface and passes messages up to the parent.
    private MyBufferListener listener;

    public MySensorManager(Context mContext) {
        this.mContext = mContext;
        this.listener = null;
        String context = Context.SENSOR_SERVICE;
        mSensorManager = (SensorManager)mContext.getSystemService(context);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
    // Assign the listener implementing events interface that will receive the events
    public void setMyBufferListener(MyBufferListener listener) {
        this.listener = listener;
    }

    protected void onResume(int periodUse) {
        Log.d("Notification","ACCELEROMETER onResume");
        countEvents = 0;
        // this.fileWriter = new BufferedFileWriter("output.txt");
        this.sBuffer = new StringBuffer();
        mSensorManager.registerListener(this, mAccelerometer, periodUse);
    }

    protected int onPause() {
        // this.fileWriter.close();
        Log.d("Notification","ACCELEROMETER onPause");
        mSensorManager.unregisterListener(this);
        // this.sBuffer.append(this.countEvents);
        // return this.sBuffer.toString();
        return this.countEvents;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("Notification","onAccuracyChanged" + accuracy);
    }

    public void onSensorChanged(SensorEvent event) {
        Log.d("Notification","acc: " + event.timestamp);
        this.countEvents++;
        String data = event.timestamp +"," + event.values[0] +"," +event.values[1] +"," +event.values[2] + ';';
        this.sBuffer.append(data);
        //this.fileWriter.write(data);
        if ( this.countEvents % this.mBufferSize == 0) {
            // fire listener here
            if (this.listener != null){
                this.listener.onBufferRead(this.sBuffer.toString()); 
            }
            //reset buffer
            this.sBuffer = new StringBuffer();
        }         
    }
}