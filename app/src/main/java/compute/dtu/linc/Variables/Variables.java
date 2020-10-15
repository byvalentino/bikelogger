package compute.dtu.linc.Variables;

import android.hardware.SensorManager;

public class Variables {
    public static String webServiceEndPoint = "https://tchoicedtu.herokuapp.com";
    public static String unique_beacon_id = "00000000-0000-0000-0000-000000000001";

    //Controls upload rate of Records
    public static int rowCountDefault = 5;
    public static int rowCountIterator = 5;

    //Tracking settings
    public static final int LOCATION_INTERVAL = 1000;
    public static final int LOCATION_DISTANCE = 0; 
    public static final int LOCATION_SLEEP_DISTANCE = 10; //meters

    // sensors consts: in microseconds - Android 2.3 (API level 9) onwards. or in 
    // const values : SENSOR_DELAY_FASTEST (18-20 ms), SENSOR_DELAY_GAME (37-39 ms), 
    // SENSOR_DELAY_UI (85-87 ms), SENSOR_DELAY_NORMAL (215-230 ms).
    // actual times, might be different by sensors.   
    public static final int ACC_SAMPLE_PERIOD = SensorManager.SENSOR_DELAY_FASTEST;
    public static final int GYR_SAMPLE_PERIOD = SensorManager.SENSOR_DELAY_FASTEST;
    public static final int MAG_SAMPLE_PERIOD = SensorManager.SENSOR_DELAY_FASTEST;

    //limit size of samples of sensors to avoid size uplaod problem
    public static final int ACC_SAMPLE_LIMIT = 100;
    public static final int GYR_SAMPLE_LIMIT = 100;
    public static final int MAG_SAMPLE_LIMIT = 100;

    // geo-Fence vars
    public static int geofenceRange = 320000;
    public static double geofenceLat = 32.08;
    public static double geofenceLon = 34.78;

}
