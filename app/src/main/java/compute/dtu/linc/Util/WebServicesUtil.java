package compute.dtu.linc.Util;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.linc.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedInputStream;
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

import compute.dtu.linc.Variables.Variables;
import jdk.nashorn.internal.ir.ReturnNode;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

//Class for containing general use webservice calls
public class WebServicesUtil {
    private final String TAG = "WebService util";

    //Requires: a string containing all json
    //Returns: true if data is successfully uploaded, false otherwise
    public static boolean uploadDataToService1(String jsonParams, String userID){
        boolean successState = false;
        if(userID != null){
            try {
                String url = Variables.webServiceEndPoint+"/";
                URL urlObj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setReadTimeout(40000);
                conn.setConnectTimeout(60000);
                
                conn.connect();
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(jsonParams);
                wr.flush();
                wr.close();
                try {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    String response = result.toString();
                    System.out.println("Response from server: " + response);
                    // WebServicesUtil.checkForNewQuestionnaires(response,this);

                    // if all tasks ended without error
                    successState = true;

                } catch (IOException ioe) {
                    if (conn != null && conn.getErrorStream() != null) {
                      InputStream errorStream = conn.getErrorStream();
                      try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
                        StringBuffer response = reader.lines().reduce(new StringBuffer(), StringBuffer::append, (buffer1, buffer2) -> buffer1);
                        System.out.println("Unexpected response from server. Response: "+ response);
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    } else {
                        ioe.printStackTrace();
                    }
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
        return successState;
    }

    public static int uploadDataToService(String jsonParams, String userID){
        int code = 400;
        if(userID != null){
            try {
                String urlStr = Variables.webServiceEndPoint+"/";
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setReadTimeout(40000);
                conn.setConnectTimeout(60000);
                try (DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream())) {
                    outputStream.write(jsonParams.getBytes());
                    outputStream.flush();
                    outputStream.close();
                    code = conn.getResponseCode();
                } catch (Exception e3) {
                    e3.printStackTrace();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        return code;
    }


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

    //Requires: a string url, and parameters
    //Returns: returns the response in string format
    public String getPostServiceWithResponse(String url ,String jsonParams){
        String responseData = null;
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
                responseData = result.toString();
                //Log.i(TAG,"Response from server: " + responseData);
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

        return responseData;
    }

    //Requires: a string
    //Returns: true if email is of a valid format, false if not
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //Requires: JSON formatted string response and the context
    //Result: Compares existing data to new data and makes a notification if new data is available
    public static void checkForNewQuestionnaires(String response, Context con) {
        try {
            //check if data contains new data:
            ArrayList<String> newArray = new ArrayList<String>();
            ArrayList<String> oldArray = new ArrayList<String>();

            JSONArray arrnew = new JSONArray(response);
            for (int i = 0; i < arrnew.length(); i++) {
                String n = arrnew.getJSONObject(i).getString("name");
                if(!n.equals("Feedback")) {
                    newArray.add(n);
                }
            }

            SharedPreferences sharedPreferences = con.getSharedPreferences("app",0);
            String olddata = sharedPreferences.getString("questionnaires", null);
            if (olddata != null) {
                JSONArray arrold = new JSONArray(olddata);
                for (int i = 0; i < arrold.length(); i++) {
                    oldArray.add(arrold.getJSONObject(i).getString("name"));
                }
            }
            boolean test = oldArray.containsAll(newArray);

            //if new data is present, notify user and save data:
            if (!test) {
                createNotification(con.getString(R.string.notification), "", con);
                //save the questionnaire data
                SharedPreferences.Editor edit = con.getSharedPreferences("app", 0).edit();
                edit.putString("questionnaires", response).commit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Requires: A title and text message string, and the context
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
    }

    //Result: Webservice call tells the webservice the questionnaire is completed
    public static void sendQuestionnaireCompletedRequest(String name){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("app",0);
        String id = sharedPreferences.getString("_id", null);

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("id", id);
            jsonParams.put("name", name);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        System.out.println("Params: " +jsonParams);
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    String url = Variables.webServiceEndPoint+"/completedQuestionnaire";
                    URL urlObj = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");

                    conn.setReadTimeout(2000);
                    conn.setConnectTimeout(4000);
                    conn.connect();
                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

                    String output = jsonParams.toString();
                    System.out.println(output);
                    wr.writeBytes(output);
                    System.out.println(wr.toString());
                    wr.flush();
                    wr.close();
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
                        conn.disconnect(); //close network connection

                    } catch (Exception e3) {
                        e3.printStackTrace();
                        throw new IOException();
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }
                } catch (IOException ee) {
                    ee.printStackTrace();
                    //Schedule retry of upload:
                    SharedPreferences.Editor sp = getApplicationContext().getSharedPreferences("app",0).edit();
                    Set<String> set = sharedPreferences.getStringSet("completedQuestionnaires",null);
                    if(set == null){
                        set = new HashSet<>();
                    }
                    set.add(name);
                    sp.putStringSet("completedQuestionnaires",set).commit();
                }
            }
        });

        thread.start();
        //wait to finish
        try {
            thread.join();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    //Requires: Two booleans, whether or not notifications should trigger and if we should wait for completion
    //Result: Webservice call fetches messages
    public static void sendMessageRequest(boolean notfications, boolean wait_for_completion) {

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    String url = Variables.webServiceEndPoint + "/getMessages";
                    URL urlObj = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Accept-Charset", "UTF-8");

                    conn.setReadTimeout(4000);
                    conn.setConnectTimeout(6000);

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
                        System.out.println("Response from server: " + response);

                        //Run only if we need to get notifications
                        if(notfications) {
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("app", 0);
                            String previous_json = sharedPreferences.getString("messages", null);

                            boolean new_messages = false;

                            ArrayList<String> previous_titles = new ArrayList<>();
                            ArrayList<String> new_titles = new ArrayList<>();

                            if(previous_json != null) {
                                JSONArray prev_json = new JSONArray(previous_json);
                                JSONArray new_json = new JSONArray(response);

                                for (int i = 0; i < prev_json.length(); i++) {
                                    JSONObject jo = prev_json.getJSONObject(i);
                                    previous_titles.add(jo.getString("title"));
                                }
                                for (int i = 0; i < new_json.length(); i++) {
                                    JSONObject jo = new_json.getJSONObject(i);
                                    new_titles.add(jo.getString("title"));
                                }
                                for (int i = 0; i < new_titles.size(); i++) {
                                    if (!previous_titles.contains(new_titles.get(i)))
                                        new_messages = true;
                                }
                            } else if (new_titles.size() >= 1){ // If no messages exist you clearly have new ones
                                new_messages = true;
                            }
                            if (new_messages) {
                                Log.i("Messages", "New Messages Received");
                                createNotification(getApplicationContext().getString(R.string.new_messages), getApplicationContext().getString(R.string.new_message_text), getApplicationContext());
                            }

                        }
                        SharedPreferences.Editor sp = getApplicationContext().getSharedPreferences("app",0).edit();
                        sp.putString("messages",response).commit();


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

        //Block thread or complete in background
        if(wait_for_completion){
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            thread.start();
        }
    }

    //Result: Webservice call tells the webservice the questionnaire is completed
    public static void sendScheduleRequest(Context con){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("app",0);
        String id = sharedPreferences.getString("id", null);

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    String url = Variables.webServiceEndPoint+"/getSchedule";
                    URL urlObj = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");

                    conn.setReadTimeout(2000);
                    conn.setConnectTimeout(4000);
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
                        System.out.println("Response from server: " + response);

                        SharedPreferences.Editor edit = con.getSharedPreferences("app", 0).edit();
                        edit.putString("schedule_data", response).commit();
                        conn.disconnect(); //close network connection

                    } catch (Exception e3) {
                        e3.printStackTrace();
                        throw new IOException();
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
        //wait to finish
    }
}

