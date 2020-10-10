package compute.dtu.linc;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.linc.R;
import com.nullwire.trace.ExceptionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import compute.dtu.linc.Util.WebServicesUtil;
import compute.dtu.linc.Variables.Variables;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class ReturnActivity extends AppCompatActivity {

    private  EditText email;
    private boolean networkError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);

        //Bind remote stack trace to activity
        ExceptionHandler.register(this,"http://python-service-linc-dtu.eu-de.mybluemix.net/bugReport");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        email = findViewById(R.id.emailrejoin);

        Button submit = (Button) findViewById(R.id.submitrejoin);
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String e = email.getText().toString();
                if(e.equals("")){
                    Toast.makeText(getApplicationContext(),R.string.ff_toast, Toast.LENGTH_LONG).show();
                    return;
                }
                if(WebServicesUtil.isEmailValid(e)){
                    sendRequest(e);
                }else{
                    Toast.makeText(getApplicationContext(), R.string.email_toast, Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

    }


    //Webservice class
    private void sendRequest(String e){
        networkError = false;

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("email", e);
            //String d = "Android, " + Build.MODEL +" "+ Build.MANUFACTURER+" "+Build.BRAND + " SDK: "+ Build.VERSION.SDK + " Version: " + Build.VERSION.RELEASE + " " + Build.DISPLAY;
            //jsonParams.put("device", d.getBytes());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    String url = Variables.webServiceEndPoint+"/rejoinByEmail";
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
                        JSONObject jsonobj = new JSONObject(response);
                        //System.out.println("Response from server: " + response);

                        SharedPreferences.Editor sharedPreferences = getApplicationContext().getSharedPreferences("app",0).edit();
                        sharedPreferences.putString("id",jsonobj.getString("_id")).commit();
                        sharedPreferences.putString("name",jsonobj.getString("name")).commit();
                        sharedPreferences.putString("email",jsonobj.getString("email")).commit();
                        sharedPreferences.putString("phone",jsonobj.getString("phone")).commit();


                        conn.disconnect(); //close network connection
                        Intent k = new Intent(ReturnActivity.this,MapViewActivity.class);
                        k.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(k);
                        finishAffinity();
                        //finish();//do not let the user return here so remove activity from stack

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
        try {
            thread.join();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        if(networkError) {
            // if we get to here something went wrong e.i. network error
            Toast.makeText(this, R.string.network_err_toast, Toast.LENGTH_LONG).show();
        }
    }
}
