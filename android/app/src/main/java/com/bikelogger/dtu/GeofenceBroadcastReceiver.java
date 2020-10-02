package com.bikelogger.dtu;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

// import com.example.linc.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import com.bikelogger.dtu.WebServicesUtil;

// GeoFence Broadcast receiver, responsible for restarting services if triggered
public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = "Geofence";
    private Context context;

    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "GEOFENCE: Triggered");
        this.context = context;
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = geofencingEvent.hasError() + "";
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            //String geofenceTransitionDetails = getGeofenceTransitionDetails(this,geofenceTransition, triggeringGeofences);
            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ) {

                //Attempt to start background service and tracking if not already running
                Intent i = new Intent(context, BackgroundService.class);
                context.startService(i);
                IBinder ib = peekService(context,new Intent(context,BackgroundService.class));
                if(ib != null){
                    BackgroundService s =  ((BackgroundService.LocationServiceBinder) ib).getService();
                    s.startTracking();
                }
                Log.e(TAG, "Entered fence");

            } else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
                Log.e(TAG, "Left geofence");

                //IF Leaving the geofence stop all logging
                Intent i = new Intent(context, BackgroundService.class);
                context.startService(i);
                IBinder ib = peekService(context,new Intent(context,BackgroundService.class));
                if(ib != null){
                    BackgroundService s =  ((BackgroundService.LocationServiceBinder) ib).getService();
                    s.stopTracking();
                }
            }else{
                //WebServicesUtil.createNotification("DEBUG","Unknown geofence event",context);
                Log.e(TAG, "Event not handled");
            }
        } else {
            // Log the error.
            Log.e(TAG, "geofence error");
        }
    }


}
