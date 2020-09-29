package com.bikelogger;

import android.util.Log;
import android.content.Context;
import android.hardware.SensorManager;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.bikelogger.MySensorManager;

public class AccModule extends ReactContextBaseJavaModule {
  private static ReactApplicationContext reactContext;
  private static final String SENSOR_DELAY_FASTEST_KEY = "FASTEST";
  private static final String SENSOR_DELAY_GAME_KEY = "GAME";
  private static final String SENSOR_DELAY_UI_KEY = "UI";
  private static final String SENSOR_DELAY_NORMAL_KEY = "NORMAL";
  private static MySensorManager aManager =  null;

  AccModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;
    Context conx = reactContext.getApplicationContext();
    aManager = new MySensorManager(conx);
    aManager.setMyBufferListener(new MyBufferListener() {
      @Override
      public void onBufferRead(String data) {
          onBufferReadHandler(data);
      }
    });
  }

  //name of the NativeModule class in js
  @Override
  public String getName() {
    return "AccLog";
  }
 
  //constants names to be used in js
  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put(SENSOR_DELAY_FASTEST_KEY, SensorManager.SENSOR_DELAY_FASTEST); //acc 18-20 ms
    constants.put(SENSOR_DELAY_GAME_KEY, SensorManager.SENSOR_DELAY_GAME); //acc 37-39 ms
    constants.put(SENSOR_DELAY_UI_KEY, SensorManager.SENSOR_DELAY_UI); // acc  85-87 ms
    constants.put(SENSOR_DELAY_NORMAL_KEY, SensorManager.SENSOR_DELAY_NORMAL); // acc 215-230 ms
    return constants;
  }
   
  //js method -start acc Listener
  @ReactMethod
  public void StartAcc(int periodUse) {
    Log.d("Notification","StartAcc");
    aManager.onResume(periodUse);
    //send msg to js 
    String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    WritableMap params = Arguments.createMap();
    params.putString("eventMsg", "Start Acc " + currentDateandTime );
    sendEvent(reactContext, "EventAcc", params);
  }
  
  //js method -stop acc Listener
  @ReactMethod
  public void StopAcc() {
    Log.d("Notification","StopAcc");
    int count = aManager.onPause();
    //String res = aManager.onPause();
    WritableMap params = Arguments.createMap();
    params.putString("eventMsg", "Stop Acc");
    params.putString("eventProperty", "count: " + count);
    sendEvent(reactContext, "EventAcc", params);
  }

  // if enough readings are made - send buffer data to js
  private void onBufferReadHandler(String data) {
    Log.d("Notification","onBufferRead");
    WritableMap params = Arguments.createMap();
    params.putString("eventMsg", "onBufferRead");
    params.putString("eventProperty", data);
    sendEvent(reactContext, "EventAcc", params);
  }

  //events 
  private void sendEvent(ReactContext reactContext,
                       String eventName,
                       WritableMap params) {
  reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(eventName, params); 
  }
}