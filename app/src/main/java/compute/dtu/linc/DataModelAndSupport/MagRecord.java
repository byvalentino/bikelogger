package compute.dtu.linc.DataModelAndSupport;

import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import compute.dtu.linc.Util.GenUtils;

public class MagRecord {
    public final double magX, magY, magZ;
    public final long timeStamp;
    public static int PRECISION = 4;
    public MagRecord (double x, double y, double z, long timestamp) {
        this.magX = GenUtils.round(x,PRECISION);
        this.magY = GenUtils.round(y,PRECISION);
        this.magZ = GenUtils.round(z,PRECISION);
        this.timeStamp = timestamp;
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