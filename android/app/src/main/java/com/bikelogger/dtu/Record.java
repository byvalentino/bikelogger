package com.bikelogger.dtu;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.altbeacon.beacon.Beacon;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

//Database entity, any variable comes a colimn name in the SQLLite db.
@Entity
public class Record {

    @PrimaryKey (autoGenerate = true)
    private Integer pk;

    @NonNull
    private String id;

    private double accX;
    private double accY;
    private double accZ;

    private double rotX;
    private double rotY;
    private double rotZ;

    private double magX;
    private double magY;
    private double magZ;

    private double longitude;
    private double latitude;
    private double speed;

    @TypeConverters(TimestampConverter.class)
    private Date timeStamp;

    //Beacons are saved as a string (representing a json array) to ease local storage
    private String beacons; // Contains: uuid, major, minor, rssi, prox


    //default recognition somewhere:
    private int stationary = 0;
    private int walking = 0;
    private int running = 0;
    private int automotive = 0;
    private int cycling = 0;
    private int unknown = 0;
    private int confidence = 0; // activity detection algorithms cofidence.

    public void setStateAndConfidence(int state,int confidence){
        System.out.println("Current state: " + state);
        if(state == 0) automotive = 1; // in vehicle
        else if(state == 1) cycling = 1; // On bicycle
        else if(state == 2) walking = 1; // device is on_foot (probably walking, but could be running)
        else if(state == 3) stationary = 1; //Still not moving
        else if(state == 4) unknown = 1; // unknown
        else if(state == 5) stationary = 1; //tilted phone most likely picked up from desk
        else if(state == 7) walking = 1; //walking
        else if(state == 8) running = 1; // running
        else unknown = 1;

        this.confidence = confidence;
    }

    //Returns: A JSONObject representation of the record
    public JSONObject toJSON(){
        try {
            JSONObject js = new JSONObject();
            js.put("id", id);
            js.put("accX",accX);
            js.put("accY",accY);
            js.put("accZ",accZ);

            js.put("rotX",rotX);
            js.put("rotY",rotY);
            js.put("rotZ",rotZ);

            js.put("magX",magX);
            js.put("magY",magY);
            js.put("magZ",magZ);

            js.put("longitude",longitude);
            js.put("latitude",latitude);
            js.put("speed",speed);

            //convert timestamp:
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.GERMANY);
            String stampString = simpleDateFormat.format(timeStamp);
            js.put("TimeStamp",stampString);

            JSONArray beac = new JSONArray(beacons);
            js.put("beacons",beac);

            js.put("stationary", stationary);
            js.put("walking", walking);
            js.put("running", running);
            js.put("automotive", automotive);
            js.put("cycling", cycling);
            js.put("unknown",unknown);
            js.put("confidence",confidence);

            //geojson hack:
            JSONObject geometry = new JSONObject();
            JSONArray coordinates_array = new JSONArray();

            coordinates_array.put(longitude);
            coordinates_array.put(latitude);
            geometry.put("Coordinates",coordinates_array);
            geometry.put("type","Point");
            js.put("geometry",geometry);

            return js;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //Requires: An ArrayList of beacons
    //Result: Generates a String, containing a JSON array of beacons, with relevant info.
    public void setBeacons(ArrayList<Beacon> beacons) {
        try {
            JSONArray beac = new JSONArray();
            for (Beacon b : beacons) {
                JSONObject o = new JSONObject();
                o.put("UUID", b.getId1());
                o.put("Major", b.getId2());
                o.put("Minor",b.getId3());
                o.put("RSSI", b.getRssi());
                beac.put(o);
            }
            this.beacons = beac.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------
    //----------------------------Default getters/setters/toString------------------------------
    //------------------------------------------------------------------------------------------

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer id) {
        this.pk = id;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public double getAccX() {
        return accX;
    }

    public void setAccX(double accX) {
        this.accX = accX;
    }

    public double getAccY() {
        return accY;
    }

    public void setAccY(double accY) {
        this.accY = accY;
    }

    public double getAccZ() {
        return accZ;
    }

    public void setAccZ(double accZ) {
        this.accZ = accZ;
    }

    public double getRotX() {
        return rotX;
    }

    public void setRotX(double rotX) {
        this.rotX = rotX;
    }

    public double getRotY() {
        return rotY;
    }

    public void setRotY(double rotY) {
        this.rotY = rotY;
    }

    public double getRotZ() {
        return rotZ;
    }

    public void setRotZ(double rotZ) {
        this.rotZ = rotZ;
    }

    public double getMagX() {
        return magX;
    }

    public void setMagX(double magX) {
        this.magX = magX;
    }

    public double getMagY() {
        return magY;
    }

    public void setMagY(double magY) {
        this.magY = magY;
    }

    public double getMagZ() {
        return magZ;
    }

    public void setMagZ(double magZ) {
        this.magZ = magZ;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
    public String getBeacons() {
        return beacons;
    }

    public void setBeacons(String s){
        this.beacons = s;
    }

    @Override
    public String toString() {
        //convert timestamp:
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY);
        String stampString = simpleDateFormat.format(timeStamp);

        return "Record{" +
                "pk=" + pk +
                ", id='" + id + '\'' +
                ", accX=" + accX +
                ", accY=" + accY +
                ", accZ=" + accZ +
                ", rotX=" + rotX +
                ", rotY=" + rotY +
                ", rotZ=" + rotZ +
                ", magX=" + magX +
                ", magY=" + magY +
                ", magZ=" + magZ +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", speed=" + speed +
                ", timeStamp='" + stampString + '\'' +
                ", beacons='" + beacons + '\'' +
                ", stationary=" + stationary +
                ", walking=" + walking +
                ", running=" + running +
                ", automotive=" + automotive +
                ", cycling=" + cycling +
                ", unknown=" + unknown +
                ", confidence=" + confidence +
                '}';
    }

    public int getStationary() {
        return stationary;
    }

    public void setStationary(int stationary) {
        this.stationary = stationary;
    }

    public int getWalking() {
        return walking;
    }

    public void setWalking(int walking) {
        this.walking = walking;
    }

    public int getRunning() {
        return running;
    }

    public void setRunning(int running) {
        this.running = running;
    }

    public int getAutomotive() {
        return automotive;
    }

    public void setAutomotive(int automotive) {
        this.automotive = automotive;
    }

    public int getCycling() {
        return cycling;
    }

    public void setCycling(int cycling) {
        this.cycling = cycling;
    }

    public int getUnknown() {
        return unknown;
    }

    public void setUnknown(int unknown) {
        this.unknown = unknown;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }
}

