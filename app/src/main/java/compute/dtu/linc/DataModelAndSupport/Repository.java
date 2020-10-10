package compute.dtu.linc.DataModelAndSupport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.room.Room;
import org.altbeacon.beacon.Beacon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Mediation class between domain and data mapping, Performs the database operations
public class Repository {

    private String DB_NAME = "record-database";
    private Context con;

    private AppDatabase appDatabase;

    public Repository(Context context) {
        con = context;
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
    }

    //Requires: Valid data equal to that of a Record object
    //Result: Passes a Record object to insertTask(Record)
    public void insertTask(double accX, double accY, double accZ, double rotX, double rotY, double rotZ, double magX, double magY, double magZ, double longitude, double lattitude, float speed, Date timestamp, ArrayList<Beacon> activeBeacons, int state, int confidence) {
        SharedPreferences sharedPreferences = con.getSharedPreferences("app",0);
        String userID = sharedPreferences.getString("id", null);
        System.out.println("UserID = "+userID);
        if(userID!=null){
            //prepare record
            Record rec = new Record();
            rec.setId(userID);
            rec.setAccX(accX);
            rec.setAccY(accY);
            rec.setAccZ(accZ);

            rec.setRotX(rotX);
            rec.setRotY(rotY);
            rec.setRotZ(rotZ);

            rec.setMagX(magX);
            rec.setMagY(magY);
            rec.setMagZ(magZ);

            rec.setLongitude(longitude);
            rec.setLatitude(lattitude);
            rec.setSpeed(speed);
            rec.setTimeStamp(timestamp);

            rec.setBeacons(activeBeacons);

            rec.setStateAndConfidence(state,confidence);

            //Insert record
            insertTask(rec);
        }
    }

    //Requires: A Record object
    //Result: Inserts the given record into the database.
    @SuppressLint("StaticFieldLeak")
    public void insertTask(final Record record) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                appDatabase.daoRecord().insertSingleRecord(record);
                return null;
            }
        }.execute();
    }

    //Requires:
    //Returns: Returns a list of records containing all records in the database
    public List<Record> getAllRecords(){

        return appDatabase.daoRecord().fetchAllTasks();
    }

    //Requires: a primary key
    //Result: deletes the record with that pk
    public void deleteSingleRecord(Date ts){

        appDatabase.daoRecord().deleteRecord(TimestampConverter.dateToTimestamp(ts));

    }

    //Requires:
    //Result: Deletes all entries in the database
    @SuppressLint("StaticFieldLeak")
    public void deleteAll(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                appDatabase.daoRecord().deleteAll();
                return null;
            }
        }.execute();
    }

    //Requires:
    //Returns: the number of Record rows in the database.
    public Integer getRowCount(){
        return appDatabase.daoRecord().getRowCount();

    }

}
