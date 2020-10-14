package compute.dtu.linc.DataModelAndSupport;

import org.json.JSONObject;
import androidx.room.Entity;
import androidx.room.TypeConverters;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import compute.dtu.linc.Util.GenUtils;

public class AccRecord {
    public final double accX, accY, accZ;
    public final long timeStamp;
    public static int PRECISION = 4;

    public AccRecord (double x, double y, double z, long timestamp) {
        this.accX = GenUtils.round(x,PRECISION);
        this.accY = GenUtils.round(y,PRECISION);
        this.accZ = GenUtils.round(z,PRECISION);
        // this.accX = x;
        // this.accY = y;
        // this.accZ = z;
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