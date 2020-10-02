package com.bikelogger.dtu;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

// import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bikelogger.dtu.Variables;

// import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

//Class for containing general use webservice calls
public class WebServicesUtil {
    // private final String TAG = "WebService util";

    //Requires: a string url, and parameters
    //Returns: true if operation succeeds
    public static boolean getPostService(String url ,String jsonParams){
        boolean successState = false;
        try {
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", "UTF-8");

            conn.setReadTimeout(1000);
            conn.setConnectTimeout(2000);

            conn.connect();

            if(jsonParams != null) {
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes("PostData=" + jsonParams);
                wr.flush();
                wr.close();
            }

            try {
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                String response = result.toString();
                //System.out.println("Response from server: " + response);

                // if all tasks ended without error
                successState = true;

            } catch (Exception e3) {
                e3.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        } catch (IOException ee) {
            ee.printStackTrace();

        }

        return successState;
    }

    //Requires: a string
    //Returns: true if email is of a valid format, false if not
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

/*     //Requires: A title and text message string, and the context
    //Result: Creates a notification with title and text
    public static void createNotification(String title,String text, Context con){
        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "LINC";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(con, "New id")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.logo)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId(CHANNEL_ID);
        NotificationManager notificationManager = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(mChannel);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notifyID, builder.build());
    } */


}
