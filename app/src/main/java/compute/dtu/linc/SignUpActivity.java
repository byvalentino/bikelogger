package compute.dtu.linc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.linc.R;
import com.nullwire.trace.ExceptionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import compute.dtu.linc.Util.WebServicesUtil;
import compute.dtu.linc.Variables.Variables;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Activity class, containing functionality related to the signup page
public class SignUpActivity extends AppCompatActivity {
    private boolean networkError;
    private String serverError = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind remote stack trace to activity
        ExceptionHandler.register(this,"https://tchoicedtu.herokuapp.com/bugReport");

        TextView text = findViewById(R.id.maintext);
        text.setText(R.string.linc_intro);
        text.setMovementMethod(LinkMovementMethod.getInstance());


        EditText name = findViewById(R.id.nametext);
        EditText email = findViewById(R.id.emailtext);
        EditText remail = findViewById(R.id.remailtext);
        EditText phoneNum = findViewById(R.id.phonenumtext);


        CheckBox checkBox = findViewById(R.id.checkBox);

        Button submit =  findViewById(R.id.Submit);
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String n = name.getText().toString();
                String e = email.getText().toString();
                String re = remail.getText().toString();
                String ph = phoneNum.getText().toString();
                if(n.equals("") || e.equals("") || ph.equals("")){
                    Toast.makeText(getApplicationContext(),R.string.ff_toast, Toast.LENGTH_LONG).show();
                    return;
                }
                if(!checkBox.isChecked()){
                    Toast.makeText(getApplicationContext(), R.string.checkbox_not_checked_error,Toast.LENGTH_LONG).show();
                    return;
                }
                if(e.equals(re)){
                    if(WebServicesUtil.isEmailValid(e)){
                        sendRequest(n,e,ph);
                    }else{
                        Toast.makeText(getApplicationContext(),R.string.email_toast, Toast.LENGTH_LONG).show();
                        return;
                    }
                }else{
                    Toast.makeText(getApplicationContext(), R.string.email_match_error, Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        Button rejoin =  findViewById(R.id.rejoin);
        rejoin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent k = new Intent(SignUpActivity.this,ReturnActivity.class);
                k.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(k);
                finishAffinity();
            }
        });

        //Ensure permissions
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

    }

    //Webservice access class.
    public void sendRequest(String n, String e, String ph){
        networkError = false;
        serverError = null;

        JSONArray ja = new JSONArray();
        JSONObject jsonParams = new JSONObject();
        try {
            //jsonParams.put("name",Base64.encodeToString(n.getBytes(),Base64.DEFAULT));
            //jsonParams.put("email",Base64.encodeToString(e.getBytes(),Base64.DEFAULT));
            //jsonParams.put("phone",Base64.encodeToString(ph.getBytes(),Base64.DEFAULT));
            //String d = "Android, " + Build.MODEL +" "+ Build.MANUFACTURER+" "+Build.BRAND + " SDK: "+ Build.VERSION.SDK + " Version: " + Build.VERSION.RELEASE + " " + Build.DISPLAY;
            //jsonParams.put("device",Base64.encodeToString(d.getBytes(),Base64.DEFAULT));
            jsonParams.put("name",n);
            jsonParams.put("email",e);
            jsonParams.put("phone",ph);
            String d = "Android, " + Build.MODEL +" "+ Build.MANUFACTURER+" "+Build.BRAND + " SDK: "+ Build.VERSION.SDK + " Version: " + Build.VERSION.RELEASE + " " + Build.DISPLAY;
            jsonParams.put("device",d);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        ja.put(jsonParams);
        //For later:

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    String url = Variables.webServiceEndPoint+"/newID";

                    URL urlObj = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");

                    conn.setReadTimeout(4000);
                    conn.setConnectTimeout(6000);
                    conn.connect();

                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

                    String output = ja.toString();

                    //wr.writeBytes(output);
                    //wr.flush();
                    //wr.close();
                    //System.out.println("Response from server-->: " + output);

                    try {
                        InputStream in = new BufferedInputStream(conn.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder result = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        System.out.println("Response from server: " + result);
                        String response = result.toString();
                        //JSONObject jObject = new JSONObject(response);
                        //String _id = jObject.getString("_id");
                        //System.out.println("Response from server: " + jObject.getString("_id"));
                        System.out.println("Response from server>>: " + response);

                        //Handle error throwback if any:
                        if(response.contains("Error")){
                            serverError = response;
                            throw new Exception();
                        }

                        SharedPreferences.Editor sharedPreferences = getApplicationContext().getSharedPreferences("app",0).edit();

                        sharedPreferences.putString("id",response).commit();
                        sharedPreferences.putString("name",n).commit();
                        sharedPreferences.putString("email",e).commit();
                        sharedPreferences.putString("phone",ph).commit();


                        conn.disconnect(); //close network connection
                        Intent k = new Intent(SignUpActivity.this,MapViewActivity.class);
                        k.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(k);
                        finish();//do not let the user return here so remove activity from stack

                    } catch (Exception e3) {
                        e3.printStackTrace();
                        networkError = true;
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }
                } catch (IOException ee) {
                    ee.printStackTrace();
                    networkError = true;
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
        if(serverError != null){
            Toast.makeText(this, serverError, Toast.LENGTH_LONG).show();

        }else if(networkError) {
            // if we get to here something went wrong e.i. network error

            Toast.makeText(this, R.string.network_err_toast, Toast.LENGTH_LONG).show();
        }
    }
}
