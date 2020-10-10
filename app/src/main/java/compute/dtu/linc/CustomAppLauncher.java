package compute.dtu.linc;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.Collection;

import compute.dtu.linc.Services.BackgroundService;
import compute.dtu.linc.Variables.Variables;

//TODO Currently not in use, scheduled for deletion

//No longer in use as it blocks beacons and does not restart the app.
public class CustomAppLauncher extends Application implements BootstrapNotifier, BeaconConsumer, RangeNotifier {
    private static final String TAG = ".MyApplicationName";
    public BackgroundService gpsService;
    private RegionBootstrap regionBootstrap;
    private BeaconManager beaconManager;
    /*
    private RegionBootstrap regionBootstrap;
    BeaconManager beaconManager;
    */
    @Override
    public void onCreate() {
        super.onCreate();

        //Start background service for tracking:
        Intent intent = new Intent(this, BackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        //this.startService(intent);
        this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        System.out.println("I Finished the first bit");
    }


    //***********************************************************************************
    // Background service functionality
    //***********************************************************************************
    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();
            if (name.endsWith("BackgroundService")) {
                gpsService = ((BackgroundService.LocationServiceBinder) service).getService();
                gpsService.startTrackingIf();
                //gpsService.startTracking(); //use only for force debug
            }

            Log.d(TAG, "App started up");
            //Register for notifications about beacons
            beaconManager = BeaconManager.getInstanceForApplication(CustomAppLauncher.this);
            // To detect proprietary beacons, you must add a line like below corresponding to your beacon
            // type.  Do a web search for "setBeaconLayout" to get the proper expression.
            //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
            beaconManager.getBeaconParsers().add(new BeaconParser().
                    setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
            beaconManager.bind(CustomAppLauncher.this);

            // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
            Region region = new Region(Variables.unique_beacon_id, null, null, null);
            regionBootstrap = new RegionBootstrap(CustomAppLauncher.this, region);
        }


        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("BackgroundService")) {
                gpsService = null;
            }
        }
    };

    //------------------------------------------------------------------------------------------
    //----------------------------Beacon implementation-----------------------------------------
    //------------------------------------------------------------------------------------------

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

        if (gpsService != null && beacons.size() > 0) {
            gpsService.activeBeacons = new ArrayList<Beacon>(beacons);
            for (Beacon b : beacons) {
                if(b.getId1().toString().equals(Variables.unique_beacon_id)){
                    if(!gpsService.tracking) {
                        //System.out.println("Restarting tracking based on beacon event");
                        gpsService.startTracking();
                    }
                }
            }
        }
    }

        @Override
        public void didEnterRegion(Region region) {
            Log.i(TAG, "I just saw an beacon for the first time!");
            if(gpsService != null) {
                gpsService.activeBeacons.clear();
            }
        }

        @Override
        public void didExitRegion(Region region) {
            Log.i(TAG, "I no longer see an beacon");
            if(gpsService != null) {
                gpsService.activeBeacons.clear();
            }
        }

        @Override
        public void didDetermineStateForRegion(int state, Region region) {
            Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state);
            if(gpsService != null) {
                gpsService.activeBeacons.clear();
            }
        }

    @Override
    public void onBeaconServiceConnect() {
        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
            beaconManager.startRangingBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));

        } catch (RemoteException e) {    }
    }


        /*
        Log.d(TAG, "App started up");
        beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));



        //Start monitoring
        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
            beaconManager.startRangingBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));

        } catch (RemoteException e) {e.printStackTrace();    }*/
    /*
    @Override
    public void didDetermineStateForRegion(int arg0, Region arg1) {
        // Don't care
    }

    @Override
    public void didEnterRegion(Region arg0) {
        Log.d(TAG, "Got a didEnterRegion call - in Custom app launcher");
        // This call to disable will make it so the activity below only gets launched the first time a beacon is seen (until the next time the app is launched)
        // if you want the Activity to launch every single time beacons come into view, remove this call.
        regionBootstrap.disable();
        //Intent intent = new Intent(this, SignUpActivity.class);
        //MY CODE - DAN
        //Start Background service is not already running.
        Intent i = new Intent(getApplicationContext(), BackgroundService.class);
        getApplicationContext().startService(i);

        // IMPORTANT: in the AndroidManifest.xml definition of this activity, you must set android:launchMode="singleInstance" or you will get two instances
        // created when a user launches the activity manually and it gets launched from here.
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //this.startActivity(i);
    }

    @Override
    public void didExitRegion(Region arg0) {
        // Don't care
    }

    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        Log.d(TAG, "Got a didrange beacons call - in Custom app launcher");
        if (beacons.size() > 0) {
            Intent i = new Intent(getApplicationContext(), BackgroundService.class);
            getApplicationContext().startService(i);
        }
    }
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Intent i = new Intent(getApplicationContext(), BackgroundService.class);
                    getApplicationContext().startService(i);
                }

            }

        });
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time! -launch");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon -launch");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: -launch"+state);

            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
            beaconManager.startRangingBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));

        } catch (RemoteException e) {    }
    }
*/
}
