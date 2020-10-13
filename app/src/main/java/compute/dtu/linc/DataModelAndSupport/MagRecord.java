package compute.dtu.linc.DataModelAndSupport;

import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MagRecord {
    public final double magX, magY, magZ;
    public final long timeStamp;
  
    public MagRecord (double x, double y, double z, Date timestamp) {
        this.magX = x;
        this.magY = y;
        this.magZ = z;
        this.timeStamp = timestamp.getTime();
    }

    public JSONObject toJSON(){
        try {
            JSONObject js = new JSONObject();
            js.put("magX",magX);
            js.put("magY",magY);
            js.put("magZ",magZ);
            js.put("magTS", timeStamp);
            return js;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}