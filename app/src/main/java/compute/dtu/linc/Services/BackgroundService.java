package compute.dtu.linc.Services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import compute.dtu.linc.DataModelAndSupport.Record;
import compute.dtu.linc.DataModelAndSupport.Repository;
import com.example.linc.R;
import compute.dtu.linc.Util.WebServicesUtil;
import compute.dtu.linc.Variables.Variables;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.nullwire.trace.ExceptionHandler;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/*
Code inspired by: https://hackernoon.com/android-location-tracking-with-a-service-80940218f561
modified by google documentation on sensor data.
 */
//Background service class which handles data recording (runs as a foreground hack-service)
public class BackgroundService extends Service implements BeaconConsumer {
    private final LocationServiceBinder binder = new LocationServiceBinder();
    private final String TAG = "BackgroundService";

    //Managers, clients etc
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private BeaconManager beaconManager;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;

    //Sensors
    private SensorManager sm;
    private Sensor gyro;
    private Sensor acc;
    private Sensor mag;
    private SensorEventListener mSensorListener;

    //Tracking settings
    private final int LOCATION_INTERVAL = 1000;
    private final int LOCATION_DISTANCE = 0;
    public boolean tracking = false;

    //Controls upload rate of Records
    private int rowCurrentCountLimit = 100;
    private int rowCountDefault = 100;
    private int rowCountIterator = 100;

    //Used to store temp data
    private Repository rep;
    private double accX;
    private double accY;
    private double accZ;

    private double rotX;
    private double rotY;
    private double rotZ;

    private double magX;
    private double magY;
    private double magZ;

    public ArrayList<Beacon> activeBeacons = new ArrayList<>();

    private int state;
    private int confidence;

    private final ReentrantLock lock = new ReentrantLock();

    private boolean alarmStarted = false;


    private int geofenceRange = 3200;
    private double geofenceLat = 55.785706;
    private double geofenceLon = 12.521729;

    //Debug
    long tracking_sleeper = 0;
    long tracking_sleep = 60000; // 2min
    boolean sleep_mode = false;

    private PowerManager.WakeLock wakeLock;


    @Override
    public void onCreate() {

        //Bind remote stack trace to activity
        ExceptionHandler.register(this,"https://tchoicedtu.herokuapp.com/bugReport");

        //Start service
        startForeground(12345678, getNotification());
        Log.i(TAG, "Foreground-background service started");

        //Set everything up
        setupSensors();
        setupBeaconManager();
        setupGeoFence();
        setupQuestionnaireUpdateAlarm();
        //get database access
        rep = new Repository(getApplicationContext());

        try {
            final PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            this.wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LINC:WAKELOCK");
            this.wakeLock.acquire();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListener);
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listners, ignore", ex);
            }
        }
        wakeLock.release();
        wakeLock = null;
    }

    //Result: Starts activity recognition and monitoring
    private void startActivityRecognition() {
        Task<Void> task = ActivityRecognition.getClient(this).requestActivityUpdates(1000, getRecognitionIntent());

        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Log.i(TAG, "Successfully added activity recognition");
                    }
                }
        );

        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.i(TAG, "Failed to add activity recognition");
                    }
                }
        );
    }
    //Result: Stops activity recognition and monitoring
    public void stopActivityRecognition(){
        Task<Void> task = ActivityRecognition.getClient(this).removeActivityUpdates(getRecognitionIntent());
        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Log.i(TAG, "Successfully removed activity recognition");
                    }
                }
        );

        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.i(TAG, "Failed to removed activity recognition");
                    }
                }
        );
    }

    //Result: Refreshes activity state and confidence
    public void updateCurrentActivity(int state, int confidence) {
        this.state = state;
        this.confidence = confidence;
        updateSleepMode(true);

    }

    //Result: Sleep mode logic
    public void updateSleepMode(boolean activityUpdate){
        // If standing still start timer:
        if(state == 3 || state == 5){
            if(tracking_sleeper == 0) {
                Log.i("SleepMode", "Starting sleep timer");
                tracking_sleeper = System.currentTimeMillis();
            }else if((tracking_sleeper + tracking_sleep) < System.currentTimeMillis() && !sleep_mode){
                startSleepmode();
                sleep_mode = true;
            }
        }else if(sleep_mode){
            tracking_sleeper = 0;
            sleep_mode = false;
            exitSleepmode();
        }else if(activityUpdate){
            Log.i("SleepMode", "Aborting sleep timer");
            tracking_sleeper = 0;
        }
    }

    //Result: Starts sleep mode when phone is not moving
    @SuppressLint("MissingPermission")
    public void startSleepmode() {
        try {
            Log.i("SleepMode", "Starting sleep mode");
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, 10, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, 10, mLocationListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //Result: Exits sleep mode when phone starts moving
    @SuppressLint("MissingPermission")
    public void exitSleepmode(){
        try {
            Log.i("SleepMode", "Exiting sleep mode");
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }



    //Support method for setting up activity recognition
    private PendingIntent getRecognitionIntent() {
        Intent intent = new Intent(this, ActivityRecognitionReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return pi;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "In start command: " + intent.getStringExtra("Geofence"));
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    //Handles the GeoFence setup procedures
    private boolean setupGeoFence() {
        geofencingClient = LocationServices.getGeofencingClient(this);

        List<Geofence> fence = new ArrayList<>();
        fence.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("DTU")
                .setCircularRegion(geofenceLat, geofenceLon, geofenceRange) //test reset to 3600meter
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(1000)
                .build());

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(fence);


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Consider calling Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return true;
        }
        geofencingClient.addGeofences(builder.build(), getGeofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Geofence generated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Geofence failed");
                    }
                });
        return false;
    }

    //Support method for setting up geofence
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }


    //Sets up an alarm that automatically checks for new questionnaires
    private void setupQuestionnaireUpdateAlarm() {
        //Start the questionnaire update alarm if it hasn't already been:
        //Prevents the alarm from being restarted, outside of system restarts
        SharedPreferences sharedPreferences = this.getSharedPreferences("app", 0);
        //boolean alarmstarted = sharedPreferences.getBoolean("alarmStarted", false);
        Log.i(TAG, "alarmstarted = " + alarmStarted);
        if(!alarmStarted) {
            QuestionnaireUpdateBroadcastReceiver alarm = new QuestionnaireUpdateBroadcastReceiver();
            alarm.setAlarm(this);
            alarmStarted = true;
        }
        //SharedPreferences.Editor editPreferences = getApplicationContext().getSharedPreferences("app", 0).edit();
        //editPreferences.putBoolean("alarmStarted", true).commit();

    }

    //Setup for beacon support
    private void setupBeaconManager() {
        //Register for notifications about beacons
        beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }

    //Setup for sensors and listener
    private void setupSensors() {
        //register sensors.
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyro = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mag = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor arg0, int arg1) {
            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor sensor = event.sensor;
                if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    //Log.i(TAG, "Gyroscope: " + event.values.toString());
                    rotX = event.values[0];
                    rotY = event.values[1];
                    rotZ = event.values[2];
                } else if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    //Log.i(TAG, "Accelerometer: " + event.values.toString());
                    accX = event.values[0];
                    accY = event.values[1];
                    accZ = event.values[2];
                } else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    //Log.i(TAG, "Magnetic: " + event.values.toString());
                    magX = event.values[0];
                    magY = event.values[1];
                    magZ = event.values[2];

                }
            }
        };
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    //Requires:
    //Returns:
    public ArrayList<Beacon> getCurrentBeaconsInRange(){
        return activeBeacons;
    }

    //Requires: GPS location permission (passively)
    //Result: Starts the gps tracking if the phone is within 3.6km of DTU. For forced tracking use startTracking
    @SuppressLint("MissingPermission")
    public void startTrackingIf() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            Location locationB = new Location("point B");
                            locationB.setLatitude(geofenceLat);
                            locationB.setLongitude(geofenceLon);

                            float distance = location.distanceTo(locationB);
                            Log.i(TAG, "Distance between points: " + distance);
                            //If distance from DTU is smaller than 3.6km, start tracking
                            if(distance < geofenceRange - 200){
                                //WebServicesUtil.createNotification("Started Tracking - IF", "",getBaseContext());
                                Log.i(TAG,"Started tracking (softly)");
                                startTracking();
                            }else{
                                //WebServicesUtil.createNotification("Tracking - Outside range", "",getBaseContext());
                                Log.i(TAG,"Tracking - outside range");
                            }
                        }
                    }
                });
    }

    //Starts location updates and data recording
    public void startTracking() {
        Log.i(TAG, "Started Tracking");
        try {
            if(!tracking) {
                initializeLocationManager();
                mLocationListener = new LocationListener(LocationManager.GPS_PROVIDER);

                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);
                //SensorManager.SENSOR_DELAY_GAME)
                sm.registerListener(mSensorListener, gyro, 10000000);
                sm.registerListener(mSensorListener, acc, 10000000);
                sm.registerListener(mSensorListener, mag, 10000000);

                startActivityRecognition();

                //WebServicesUtil.createNotification("Started Tracking", "",this);
                tracking = true;
            }else{
                //WebServicesUtil.createNotification("Additional start events are unneeded", "",this);
            }
        } catch (java.lang.SecurityException ex) {
             Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

    }

    //Stops all data recording except for beacons
    public void stopTracking() {
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListener);
                sm.unregisterListener(mSensorListener, gyro);
                sm.unregisterListener(mSensorListener, acc);
                sm.unregisterListener(mSensorListener, mag);

                stopActivityRecognition();

                //WebServicesUtil.createNotification("Stopped tracking","",this);
                tracking = false;
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listners, ignore", ex);
            }
        }
    }
    //Support method for notifications
    private Notification getNotification() {

        NotificationChannel channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), "channel_01").setAutoCancel(true);
        return builder.build();
    }

    //Location service binder
    public class LocationServiceBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    //------------------------------------------------------------------------------------------
    //----------------------------Location Listener implementation------------------------------
    //------------------------------------------------------------------------------------------
    private class LocationListener implements android.location.LocationListener
    {
        private final String TAG = "LocationListener";
        private Location mLastLocation;

        public LocationListener(String provider)
        {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            try {
                //Setup data
                mLastLocation = location;
                //Convert to the correct timestamp
                SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.GERMANY);
                String dateStr = readFormat.format(new Date());
                SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                Date date = writeFormat.parse(dateStr);

                rep.insertTask(accX, accY, accZ, rotX, rotY, rotZ, magX, magY, magZ, location.getLongitude(), location.getLatitude(), location.getSpeed(), date, activeBeacons,state ,confidence);
                //Database functionality must be on a new thread
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        lock.lock();
                        try{
                            //If we have enough data, attempt upload
                            int currentCount = rep.getRowCount();
                            Log.i(TAG, "Count: " + currentCount);
                            if (currentCount >= rowCurrentCountLimit) {
                                //If the service is accessable, attempt upload:
                                if (WebServicesUtil.getPostService(Variables.webServiceEndPoint+"/ping", null)) {
                                    List<Record> records = rep.getAllRecords();

                                    //Temp storage
                                    ArrayList<Record> recordsForDeletion = new ArrayList<Record>();
                                    JSONArray jparams = new JSONArray();

                                    int uploadBreakdown = 0;
                                    for (int i = 0; i < records.size(); i++) {
                                        jparams.put(records.get(i).toJSON());
                                        recordsForDeletion.add(records.get(i));

                                        //split the upload into chucks if too big
                                        uploadBreakdown++;
                                        if(uploadBreakdown > rowCountDefault){
                                            if(uploadDataToService(jparams.toString())){
                                                //delete all records uploaded succesfully
                                                for(Record r : recordsForDeletion){
                                                    rep.deleteSingleRecord(r.getTimeStamp());
                                                }
                                                jparams = new JSONArray();
                                                recordsForDeletion = new ArrayList<Record>(); //once all keys are deleted clear list
                                                uploadBreakdown = 0;
                                            }else{return;}
                                        }
                                    }

                                    if (uploadDataToService(jparams.toString())) {
                                        for(Record r : recordsForDeletion){
                                            rep.deleteSingleRecord(r.getTimeStamp());
                                        }
                                        Log.i(TAG, "Successfully executed data upload");
                                        rowCurrentCountLimit = rowCountDefault;

                                    }
                                } else {
                                    rowCurrentCountLimit = rowCurrentCountLimit + rowCountIterator; // try again a little later
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally{
                            lock.unlock();
                        }
                        //Attempt to re-upload completed questionnaire
                        SharedPreferences sp = getApplicationContext().getSharedPreferences("app",0);
                        SharedPreferences.Editor spEdit = getApplicationContext().getSharedPreferences("app",0).edit();
                        Set<String> set = sp.getStringSet("completedQuestionnaires",null);
                        if(set != null && set.size() > 0){
                            for(String name: set){
                                //Remove the name, commit and attempt to resend completetion event
                                set.remove(name);
                                spEdit.putStringSet("completedQuestionnaires",set).commit();
                                WebServicesUtil.sendQuestionnaireCompletedRequest(name);

                            }
                        }
                        updateSleepMode(false);


                    }
                });
                thread.start();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + status);
        }
    }

    //------------------------------------------------------------------------------------------
    //----------------------------Beacon implementation-----------------------------------------
    //------------------------------------------------------------------------------------------

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    if (beacons.size() > 0) {
                        activeBeacons = new ArrayList<Beacon>(beacons);
                        for (Beacon b : beacons) {
                            if(b.getId1().toString().equals(Variables.unique_beacon_id)){
                                if(!tracking) {
                                    Log.i(TAG,"Restarted tracking based on beacon");
                                    startTracking();
                                }
                            }
                        }
                    }
                }
            }

        });
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
                activeBeacons.clear();
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
                activeBeacons.clear();
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state);
                activeBeacons.clear();

            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
            beaconManager.startRangingBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));

        } catch (RemoteException e) {    }
    }

    //------------------------------------------------------------------------------------------
    //----------------------------Network and utility-------------------------------------------
    //------------------------------------------------------------------------------------------

    //Requires: a string containing all json
    //Returns: true if data is successfully uploaded, false otherwise
    public boolean uploadDataToService(String jsonParams){
        boolean successState = false;
        SharedPreferences sharedPreferences = getSharedPreferences("app",0);
        String userID = sharedPreferences.getString("id", null);
        if(userID != null){
            try {
                String url = Variables.webServiceEndPoint+"/";
                URL urlObj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                conn.setReadTimeout(40000);
                conn.setConnectTimeout(60000);

                conn.connect();

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(jsonParams);
                wr.flush();
                wr.close();

                try {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    String response = result.toString();
                    //System.out.println("Response from server: " + response);
                    WebServicesUtil.checkForNewQuestionnaires(response,this);


                    // if all tasks ended without error
                    successState = true;

                } catch (Exception e3) {
                    e3.printStackTrace();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            } catch (IOException ee) {
                ee.printStackTrace();

            }
        }
        return successState;
    }
}