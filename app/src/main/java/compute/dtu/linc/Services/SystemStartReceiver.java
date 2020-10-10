package compute.dtu.linc.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.linc.R;

import compute.dtu.linc.Util.WebServicesUtil;

//Restarts tracking systems if phone has been turned off and is turned back on
public class SystemStartReceiver extends BroadcastReceiver
{
    QuestionnaireUpdateBroadcastReceiver alarm = new QuestionnaireUpdateBroadcastReceiver();

    //Result: Starts alarm and background service on restart
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //Start questionnaire updates
        alarm.setAlarm(context);

        //Start Background service is not already running.
        Intent i = new Intent(context, BackgroundService.class);
        context.startForegroundService(i);
        IBinder ib = peekService(context,new Intent(context,BackgroundService.class));
        if(ib != null){
            BackgroundService s =  ((BackgroundService.LocationServiceBinder) ib).getService();
            s.startTrackingIf();
        }
    }

}