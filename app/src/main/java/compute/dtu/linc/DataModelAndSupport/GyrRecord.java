package compute.dtu.linc.DataModelAndSupport;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GyrRecord {
    public final double gyrX, gyrY, gyrZ;
    public final long timeStamp;
    
    public GyrRecord (double x, double y, double z, long timestamp) {
        this.gyrX = x;
        this.gyrY = y;
        this.gyrZ = z;
        this.timeStamp = timestamp;
    }

    public JSONObject toJSON(){
        try {
            JSONObject js = new JSONObject();
            js.put("gyrX",gyrX);
            js.put("gyrY",gyrY);
            js.put("gyrZ",gyrZ);
            js.put("gyrTS", timeStamp);
            return js;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}