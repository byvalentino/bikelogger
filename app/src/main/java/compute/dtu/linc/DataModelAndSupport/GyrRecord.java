package compute.dtu.linc.DataModelAndSupport;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import compute.dtu.linc.Util.GenUtils;

public class GyrRecord {
    public final double gyrX, gyrY, gyrZ;
    public final long timeStamp;
    public static int PRECISION = 4;
    
    public GyrRecord (double x, double y, double z, long timestamp) {
        this.gyrX = GenUtils.round(x,PRECISION);
        this.gyrY = GenUtils.round(y,PRECISION);
        this.gyrZ = GenUtils.round(z,PRECISION);
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