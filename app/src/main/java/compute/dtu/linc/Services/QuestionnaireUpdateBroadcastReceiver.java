package compute.dtu.linc.Services;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.app.NotificationCompat;
import com.example.linc.R;

import compute.dtu.linc.Util.WebServicesUtil;
import compute.dtu.linc.Variables.Variables;

import org.json.JSONArray;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

//Class responsible for handling questionnaire updates
public class QuestionnaireUpdateBroadcastReceiver extends BroadcastReceiver
{
    //Result: Alarm receiver, triggers a questionnaire fetch
    @Override
    public void onReceive(Context context, Intent intent)
    {
        sendQuestionniareRequest(context);
    }

    //Result: Starts the questionnaire fetching alarm
    public void setAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, QuestionnaireUpdateBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 30, pi); // 30 min
    }

    //Result: Stops the questionnaire fetching alarm
    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, QuestionnaireUpdateBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    //Result: Webservice fetch new questionnaire data
    public void sendQuestionniareRequest(Context con){

        SharedPreferences sharedPreferences = con.getSharedPreferences("app",0);
        String userID = sharedPreferences.getString("id", null);
        if(userID != null){
            HashMap<String, String> params = new HashMap<>();
            params.put("id",userID);


            StringBuilder sbParams = new StringBuilder();
            int i = 0;
            for (String key : params.keySet()) {
                try {
                    if (i != 0){
                        sbParams.append("&");
                    }
                    sbParams.append(key).append("=")
                            .append(URLEncoder.encode(params.get(key), "UTF-8"));

                } catch (UnsupportedEncodingException error) {
                    error.printStackTrace();
                }
                i++;
            }
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        String url = Variables.webServiceEndPoint+"/getQuestionnaires?"+sbParams.toString();
                        URL urlObj = new URL(url);
                        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Accept-Charset", "UTF-8");

                        conn.setReadTimeout(10000);
                        conn.setConnectTimeout(15000);

                        conn.connect();

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
                            WebServicesUtil.checkForNewQuestionnaires(response,con);

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
                }
            });

            thread.start();
        }
    }
}