package compute.dtu.linc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.linc.R;
import com.google.android.material.card.MaterialCardView;
import com.nullwire.trace.ExceptionHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import compute.dtu.linc.Util.WebServicesUtil;
import compute.dtu.linc.Variables.Variables;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class CommunicationsModule extends AppCompatActivity {

    ArrayList<MaterialCardView> cardViews = new ArrayList<MaterialCardView>();
    public LinearLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communications_module);

        //Bind remote stack trace to activity
        ExceptionHandler.register(this,"http://python-service-linc-dtu.eu-de.mybluemix.net/bugReport");

        getSupportActionBar().setTitle(R.string.communication_module_title);


        rl = findViewById(R.id.messagelist);

        WebServicesUtil.sendMessageRequest(false, true);
        generateCards();

    }

    //Requires:
    //Result: Creates all card views for the view
    private void generateCards(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("app",0);
        String response = sharedPreferences.getString("messages", null);
        try {

            JSONArray ja = new JSONArray(response);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);

                String content;
                String message_type = jo.getString("message_type");
                if (jo.getString("message_type").equals("HTML")) {
                    content = new String(Base64.decode(jo.getString("content").getBytes(), Base64.DEFAULT), StandardCharsets.UTF_8);
                } else {
                    content = jo.getString("content");

                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        createQuestionnaireView(content, message_type);
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Requires: Strings, representing the card's title, description and link
    //Result: Dynamically generates a card view on screen
    private void createQuestionnaireView(String htmL_data, String content_type) {
        final MaterialCardView mcv = new MaterialCardView(this);
        mcv.setStrokeColor(ContextCompat.getColor(this,R.color.text));
        RelativeLayout.LayoutParams lm = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lm.setMargins(12,8,12,0);
        if(cardViews.size() > 0){
            System.out.println(cardViews.size()-1);
            lm.addRule(RelativeLayout.BELOW,cardViews.get(cardViews.size()-1).getId());
        }
        mcv.setLayoutParams(lm);
        mcv.setMinimumHeight(200);
        mcv.setClickable(true);
        mcv.setFocusable(true);
        mcv.setStrokeColor(ContextCompat.getColor(this,R.color.colorPrimary));
        mcv.setId(cardViews.size());
        mcv.setUseCompatPadding(true);
        mcv.setCardElevation(14);
        mcv.setStrokeColor(Color.BLACK);
        mcv.setStrokeWidth(4);

        WebView wb = new WebView(this);
        wb.loadDataWithBaseURL("",htmL_data,"text/html","utf-8","");
        wb.getSettings().setBuiltInZoomControls(true);
        wb.getSettings().setJavaScriptEnabled(true);
        wb.getSettings().setDomStorageEnabled(true);
        if(content_type.equals("HTML")){
            wb.setInitialScale(100);
        }
        mcv.addView(wb);
        rl.addView(mcv);
        cardViews.add(mcv);

    }


}
