package compute.dtu.linc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.linc.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Schedule extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String[] options = {"Bus 1", "Bus 2", "Bus 3"};

    String schedule_data;
    JSONArray json_schedule;
    TextView tx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        tx = findViewById(R.id.schedule_text);
        Spinner sp = findViewById(R.id.planets_spinner);

        getSupportActionBar().setTitle("Schedule");

        SharedPreferences sharedPreferences = getSharedPreferences("app",0);
        schedule_data = sharedPreferences.getString("schedule_data", null);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(this);

        String entity = getIntent().getStringExtra("entity");
        if(entity != null) {
            if (entity.equals("1")) {
                sp.setSelection(0);
            } else if (entity.equals("2")) {
                sp.setSelection(1);
            } else if (entity.equals("3")) {
                sp.setSelection(2);
            }
        }
    }

    //Handles selections
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            if(json_schedule == null) {
                json_schedule = new JSONArray(schedule_data);
            }
            JSONArray ja = new JSONArray(json_schedule.getJSONObject(i).getString("schedule_json").replaceAll("\"",""));
            String schedule_string = "";

            for(int j = 0; j < ja.length();j++){
                JSONObject n = ja.getJSONObject(j);

                String padded_stop = n.getString("stop");

                for(int k = padded_stop.length(); k < 20; k++){
                    padded_stop = padded_stop + "\t";
                }
                schedule_string = schedule_string + padded_stop.replaceAll(" ","\t\t") + "\t\t-\t\t" +  n.getString("time") + "\n";
            }
            tx.setTextSize(14);
            tx.setText(schedule_string);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //do nothing
    }
}
