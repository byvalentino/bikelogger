package compute.dtu.linc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linc.R;
import com.nullwire.trace.ExceptionHandler;

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

import compute.dtu.linc.Util.WebServicesUtil;
import compute.dtu.linc.Variables.Variables;

public class AccountActivity extends AppCompatActivity {

    private String serverError = null;
    private Boolean networkError = false;
    private String id = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //Bind remote stack trace to activity
        ExceptionHandler.register(this,"https://tchoicedtu.herokuapp.com/bugReport");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        //fetch preferences
        SharedPreferences sp = getApplicationContext().getSharedPreferences("app",0);
        String name_val = sp.getString("name","null");
        String email_val = sp.getString("email","null");
        String phone_val = sp.getString("phone","null");
        id = sp.getString("id","null");

        //fetch fields:
        TextView accInfo = findViewById(R.id.account_info);
        TextView parID = findViewById(R.id.participant_id);
        EditText email = findViewById(R.id.change_email_field);
        EditText remail = findViewById(R.id.change_repeat_email_field);
        EditText phoneNum = findViewById(R.id.change_phone_num_field);
        Button update = findViewById(R.id.update_account);

        //Set relevant data
        Resources res = getResources();
        String acc_text = String.format(res.getString(R.string.account_info_placeholder), name_val, email_val, phone_val);
        accInfo.setText(acc_text);
        parID.setText(id);
        email.setText(email_val);
        phoneNum.setText(phone_val);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String e = email.getText().toString();
                String re = remail.getText().toString();
                String ph = phoneNum.getText().toString();
                if(e.equals("") || ph.equals("")){
                    Toast.makeText(getApplicationContext(),R.string.ff_toast, Toast.LENGTH_LONG).show();
                    return;
                }
                if(e.equals(re)){
                    if(WebServicesUtil.isEmailValid(e)){
                        sendRequest(e,ph);
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

    }
    //Webservice access class.
    public void sendRequest( String e, String ph){
        networkError = false;
        serverError = null;

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("email",Base64.encodeToString(e.getBytes(),Base64.DEFAULT));
            jsonParams.put("phone",Base64.encodeToString(ph.getBytes(),Base64.DEFAULT));
            jsonParams.put("id",Base64.encodeToString(id.getBytes(),Base64.DEFAULT));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        //For later:

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    String url = Variables.webServiceEndPoint+"/updateUser";

                    URL urlObj = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");

                    conn.setReadTimeout(4000);
                    conn.setConnectTimeout(6000);
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

                        //Handle error throwback if any:
                        if(response.contains("Error")){
                            serverError = response;
                            throw new Exception();
                        }

                        SharedPreferences.Editor sharedPreferences = getApplicationContext().getSharedPreferences("app",0).edit();
                        sharedPreferences.putString("email",e).commit();
                        sharedPreferences.putString("phone",ph).commit();


                        conn.disconnect(); //close network connection
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
            if(networkError = false){
                recreate(); // refresh activity
            }
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
