package com.bikelogger.dtu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.DetectedActivity;
import java.util.ArrayList;
import java.util.List;

public class ActivityRecognitionReceiver extends BroadcastReceiver {

    private Context context;
    private String TAG = "ActivityRecognitionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        //System.out.println("In onReceive Recognition");
        try{
            if(ActivityRecognitionResult.hasResult(intent)) {
                ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
                ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
                int state = -1;
                int conf = 0;

                DetectedActivity most_likely_activity = null;
                for (DetectedActivity activity : detectedActivities) {
                    //Log.e(TAG, "Detected activity: " + activity.getType() + ", " + activity.getConfidence());
                    if(most_likely_activity == null){
                        most_likely_activity = activity;
                    }else if(activity.getConfidence() > most_likely_activity.getConfidence()) {
                        most_likely_activity = activity;
                    }



                }
                //Log.e(TAG, "Most likely activity: " + most_likely_activity.getType() + ", " + most_likely_activity.getConfidence());
                conf = most_likely_activity.getConfidence();
                state = most_likely_activity.getType();

                IBinder ib = peekService(context,new Intent(context,BackgroundService.class));
                if(ib != null) {
                    BackgroundService s = ((BackgroundService.LocationServiceBinder) ib).getService();
                    s.updateCurrentActivity(state,conf);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public List<ActivityTransition> getTransitionList(){
        List<ActivityTransition> transitions = new ArrayList<>();

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_BICYCLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_BICYCLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.RUNNING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.RUNNING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());
        return transitions;
    }
}
