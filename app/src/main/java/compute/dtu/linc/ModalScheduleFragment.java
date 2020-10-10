package compute.dtu.linc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.linc.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ModalScheduleFragment extends BottomSheetDialogFragment {

    private String schedule_data;
    private String entity_type;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.modal_schedule_layout, container, false);

        entity_type = getArguments().getString("entity");
        String name = getArguments().getString("name");

        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("app",0);
            schedule_data = sharedPreferences.getString("schedule_data", null);
            if(schedule_data != null) {
                System.out.println(schedule_data);
            }

            JSONArray json_schedule = new JSONArray(schedule_data);

            //Populate menu
            if(entity_type.equals("Busstop")) {
                generateLabel(v ,getActivity().getString(R.string.upcoming_service_at_stop_text) + name);
                generateRowBusStop(v, getActivity().getString(R.string.upcoming_service_from) + "bus 1", new JSONArray(json_schedule.getJSONObject(0).getString("schedule_json").replaceAll("\"","")),name);
                generateRowBusStop(v, getActivity().getString(R.string.upcoming_service_from) + "bus 2", new JSONArray(json_schedule.getJSONObject(1).getString("schedule_json").replaceAll("\"","")),name);
                generateRowBusStop(v, getActivity().getString(R.string.upcoming_service_from) + "bus 3", new JSONArray(json_schedule.getJSONObject(2).getString("schedule_json").replaceAll("\"","")),name);
                generateScheduleButton(v);
            }else{

                if(entity_type.equals("1") && json_schedule.length() >= 1){
                    generateRowDynmaically(v, getActivity().getString(R.string.upcoming_bus_stops_for)+"bus 1", new JSONArray(json_schedule.getJSONObject(0).getString("schedule_json").replaceAll("\"","")));
                }else if(entity_type.equals("2") && json_schedule.length() >= 2){
                    generateRowDynmaically(v, getActivity().getString(R.string.upcoming_bus_stops_for)+"bus 2", new JSONArray(json_schedule.getJSONObject(1).getString("schedule_json").replaceAll("\"","")));
                }else if(entity_type.equals("3")  && json_schedule.length() >= 3){
                    generateRowDynmaically(v, getActivity().getString(R.string.upcoming_bus_stops_for)+"bus 3", new JSONArray(json_schedule.getJSONObject(2).getString("schedule_json").replaceAll("\"","")));
                }
                generateScheduleButton(v);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return v;
    }


    public void generateLabel(View v, String text){
        LinearLayout ll = v.findViewById(R.id.modal_schedule);
        TextView tx = new TextView(getContext());
        tx.setText(text);
        tx.setPadding(20, 0, 0, 0);
        tx.setTextSize(16);
        tx.setTypeface(null, Typeface.BOLD_ITALIC);
        ll.addView(tx);
    }
    public void generateScheduleButton(View v){
        LinearLayout ll = v.findViewById(R.id.modal_schedule);
        Button b = new Button(getContext(),null, android.R.attr.borderlessButtonStyle);
        b.setTextColor(getResources().getColor(R.color.ButtonBlue));
        b.setText(getActivity().getString(R.string.see_full_schedule_for_today));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),Schedule.class);
                i.putExtra("entity",entity_type);
                startActivity(i);

            }
        });
        ll.addView(b);
    }

    //Template for generating a row schedule
    public void generateRowBusStop(View v, String segment_message, JSONArray json_data, String stop_name){
        try {
            LinearLayout ll = v.findViewById(R.id.modal_schedule);

            //Insert title
            if (segment_message != null) {
                TextView tx = new TextView(getContext());
                tx.setText(segment_message);
                tx.setPadding(20, 0, 0, 0);
                tx.setTextSize(14);
                tx.setTypeface(null, Typeface.BOLD_ITALIC);
                ll.addView(tx);
            }

            //Make top layer
            LinearLayout sub = new LinearLayout(getContext());
            sub.setPadding(20, 20, 20, 20);
            sub.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            sub.setOrientation(LinearLayout.HORIZONTAL);
            sub.setGravity(Gravity.CENTER_VERTICAL);


            boolean endstop_added = false;
            int stop_count = 0;
            boolean found_stop = false;
            //Dynamically build the stops up to 3
            for (int i = 0; stop_count < 4 && i < json_data.length(); i++) {

                JSONObject n = json_data.getJSONObject(i);

                //Finds the first poin the schedule the stop is mentioned then generates the data
                if (!found_stop){
                    if(n.getString("stop").equals(stop_name)){
                        found_stop = true;
                    }
                }

                if(found_stop) {
                    if (stop_count == 0) {
                        sub.addView(createSubLayout(n.getString("time"), n.getString("stop")));
                        stop_count++;
                    } else if (n.getString("end_stop").equals("0")) {
                        TextView tx = new TextView(getContext());
                        tx.setText("---");
                        sub.addView(tx);
                        sub.addView(createSubLayout(n.getString("time"), n.getString("stop")));
                        stop_count++;
                    } else {
                        TextView tx4 = new TextView(getContext());
                        tx4.setText("...");
                        sub.addView(tx4);
                        sub.addView(createSubLayout(n.getString("time"), n.getString("stop")));
                        endstop_added = true;
                        break;
                    }
                }
            }
            //if end stop not found keep going untill one is found otherwise ignore
            if (!endstop_added) {
                for (int i = 0; i < json_data.length(); i++) {
                    JSONObject n = json_data.getJSONObject(i);
                    if (n.getString("end_stop").equals("1")) {
                        TextView tx = new TextView(getContext());
                        tx.setText("...");
                        sub.addView(tx);
                        sub.addView(createSubLayout(n.getString("time"), n.getString("stop")));
                        break;
                    }

                }
            }


            ll.addView(sub);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Template for generating a row schedule
    public void generateRowDynmaically(View v, String segment_message, JSONArray json_data) {
        try {
            LinearLayout ll = v.findViewById(R.id.modal_schedule);

            //Insert title
            if (segment_message != null) {
                TextView tx = new TextView(getContext());
                tx.setText(segment_message);
                tx.setPadding(20, 0, 0, 0);
                tx.setTextSize(14);
                tx.setTypeface(null, Typeface.BOLD_ITALIC);
                ll.addView(tx);
            }

            //Make top layer
            LinearLayout sub = new LinearLayout(getContext());
            sub.setPadding(20, 20, 20, 20);
            sub.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            sub.setOrientation(LinearLayout.HORIZONTAL);
            sub.setGravity(Gravity.CENTER_VERTICAL);


            boolean endstop_added = false;
            //Dynamically build the stops up to 3
            for (int i = 0; i < 4 && i < json_data.length(); i++) {

                JSONObject n = json_data.getJSONObject(i);
                if(i == 0){
                    sub.addView(createSubLayout(n.getString("time"), n.getString("stop")));
                }else if (n.getString("end_stop").equals("0")) {
                    TextView tx = new TextView(getContext());
                    tx.setText("---");
                    sub.addView(tx);
                    sub.addView(createSubLayout(n.getString("time"), n.getString("stop")));
                } else {
                    TextView tx4 = new TextView(getContext());
                    tx4.setText("...");
                    sub.addView(tx4);
                    sub.addView(createSubLayout(n.getString("time"), n.getString("stop")));
                    endstop_added = true;
                    break;
                }
            }
            //if end stop not found keep going untill one is found otherwise ignore
            if (!endstop_added) {
                for (int i = 0; i < json_data.length(); i++) {
                    JSONObject n = json_data.getJSONObject(i);
                    if (n.getString("end_stop").equals("1")) {
                        TextView tx = new TextView(getContext());
                        tx.setText("...");
                        sub.addView(tx);
                        sub.addView(createSubLayout(n.getString("time"), n.getString("stop")));
                        break;
                    }

                }
            }


            ll.addView(sub);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Create sub layer
    public LinearLayout createSubLayout(String time, String stop){
        LinearLayout sub = new LinearLayout(getContext());
        sub.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        sub.setOrientation(LinearLayout.VERTICAL);

        TextView tx = new TextView(getContext());
        tx.setText(time);
        tx.setGravity(Gravity.CENTER_HORIZONTAL);
        sub.addView(tx);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels / 7;
        int height = (int) (width * 0.83333);
        ImageView iv = new ImageView(getContext());
        iv.setImageResource(R.drawable.new_linc_stop);
        iv.setLayoutParams(new LinearLayout.LayoutParams(width,height));
        sub.addView(iv);

        TextView tx2 = new TextView(getContext());
        tx2.setText(stop.replace("Station","")); //TODO remove replace
        tx2.setGravity(Gravity.CENTER_HORIZONTAL);

        sub.setPadding(10,0,10,0);
        sub.addView(tx2);


        return sub;
    }
    //Create sub layer TODO remove later
    public LinearLayout createSubLayout(){
        LinearLayout sub = new LinearLayout(getContext());
        sub.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        sub.setOrientation(LinearLayout.VERTICAL);

        TextView tx = new TextView(getContext());
        tx.setText("17.00");
        tx.setGravity(Gravity.CENTER_HORIZONTAL);
        sub.addView(tx);

        ImageView iv = new ImageView(getContext());
        iv.setImageResource(R.drawable.new_linc_stop);
        iv.setLayoutParams(new LinearLayout.LayoutParams(120,100));
        sub.addView(iv);

        TextView tx2 = new TextView(getContext());
        tx2.setText("324");
        tx2.setGravity(Gravity.CENTER_HORIZONTAL);

        sub.setPadding(10,0,10,0);
        sub.addView(tx2);


        return sub;
    }
}
