package compute.dtu.linc.DataModelAndSupport;

import org.json.JSONObject;
import androidx.room.Entity;
import androidx.room.TypeConverters;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//Database entity, any variable comes a colimn name in the SQLLite db.

// public class ProductTypeConverters {
//     @TypeConverter
//     public static List<AccRecord> stringToMeasurements(String json) {
//         JSONObject gson = new JSONObject();
//         Type type = new TypeToken<List<AccRecord>>() {}.getType();
//         List<AccRecord> accList = JSONObject.fromJson(json, type);
//         return accList;
//     }

//     @TypeConverter
//     public static String measurementsToString(List<Measurement> list) {
//         Gson gson = new Gson();
//         Type type = new TypeToken<List<Measurement>>() {}.getType();
//         String json = gson.toJson(list, type);
//         return json;
//     }
// }

public class AccRecord {
    public final double accX, accY, accZ;
    public final long timeStamp;
    
    public AccRecord (double x, double y, double z, long timestamp) {
        this.accX = x;
        this.accY = y;
        this.accZ = z;
        this.timeStamp = timestamp;
    }
    
    public JSONObject toJSON(){
        try {
            JSONObject js = new JSONObject();
            js.put("accX",accX);
            js.put("accY",accY);
            js.put("accZ",accZ);
            js.put("accTS", timeStamp);
            return js;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}