package compute.dtu.linc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.linc.R;

import compute.dtu.linc.Variables.Variables;
import com.google.android.material.card.MaterialCardView;
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


//Fragment class, for creating the questionnaire fragment tab
public class QuestionnaireFragment extends Fragment {

    ArrayList<MaterialCardView> cardViews = new ArrayList<MaterialCardView>();
    LinearLayout rl;
    Context con;
    Boolean returning = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.questionnaire);

        returning = false;
        con = getContext();
        View v = inflater.inflate(R.layout.questionnaire,container, false);
        rl = v.findViewById(R.id.questionnairelayout);


        //Fetch new data only if a few seconds have passed (stops refresh on receive data from chaining)
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("app",0);
        long current_time= System.currentTimeMillis();
        long old_time_stamp = sharedPreferences.getLong("LastUpdate",-1);
        if((current_time >= (old_time_stamp + 1000)) || old_time_stamp == -1) {
            sendQuestionniareRequest();
        }
        generateCards();
        return v;
    }

    //Requires:
    //Result: Creates all card views for the view
    private void generateCards(){
        //Handle data
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("app",0);
        String quests = sharedPreferences.getString("questionnaires", null);
        System.out.println("quests = "+quests);
        try{
            if(quests != null) {
                JSONArray arr = new JSONArray(quests);
                for (int i = 0; i < arr.length(); i++) {
                    System.out.println(arr.getJSONObject(i).getString("name"));
                    createQuestionnaireView(arr.getJSONObject(i).getString("name"),arr.getJSONObject(i).getString("desc"),arr.getJSONObject(i).getString("link"));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    //Requires: Strings, representing the card's title, description and link
    //Result: Dynamically generates a card view on screen
    private void createQuestionnaireView(String title, String desc, String link) {
        final MaterialCardView mcv = new MaterialCardView(con);

        mcv.setStrokeColor(ContextCompat.getColor(con,R.color.text));
        RelativeLayout.LayoutParams lm = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lm.setMargins(8,8,8,0);
        if(cardViews.size() > 0){
            System.out.println(cardViews.size()-1);
            lm.addRule(RelativeLayout.BELOW,cardViews.get(cardViews.size()-1).getId());
        }
        mcv.setLayoutParams(lm);
        mcv.setMinimumHeight(200);
        mcv.setClickable(true);
        mcv.setFocusable(true);
        mcv.setStrokeColor(ContextCompat.getColor(con,R.color.colorPrimary));
        mcv.setId(cardViews.size());
        mcv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("clicked card " + view.getId());
                //Intent bi = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                //startActivity(bi);
                Intent k = new Intent(getActivity(),WebViewActivity.class);
                k.putExtra("name", title);
                k.putExtra("link",link);
                startActivity(k);
                returning = true;
            }
        });
        mcv.setUseCompatPadding(true);

        LinearLayout subview = new LinearLayout(con);
        subview.setOrientation(LinearLayout.VERTICAL);
        String t = "ll" + cardViews.size();

        TextView tx = new TextView(con);
        tx.setText(title);
        tx.setTypeface(null, Typeface.BOLD);
        tx.setTextSize(16);
        System.out.println("Test " + cardViews.size());

        TextView tx2 = new TextView(con);
        tx2.setText(desc);

        subview.addView(tx);
        subview.addView(tx2);
        mcv.addView(subview);
        rl.addView(mcv);
        cardViews.add(mcv);

    }


    //Result: Fetches the newest questionnaires and updates fragment upon completion
    public void sendQuestionniareRequest(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("app",0);
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

                        conn.setReadTimeout(6000);
                        conn.setConnectTimeout(8000);

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

                            //save the questionnaire data
                            SharedPreferences.Editor edit = getContext().getSharedPreferences("app",0).edit();
                            edit.putString("questionnaires",response).commit();
                            edit.putLong("LastUpdate", System.currentTimeMillis()).commit();
                            //Refresh fragment:
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            if (Build.VERSION.SDK_INT >= 26) {
                                ft.setReorderingAllowed(false);
                            }
                            ft.detach(QuestionnaireFragment.this).attach(QuestionnaireFragment.this).commit();
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
