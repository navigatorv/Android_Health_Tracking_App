package com.example.kzha6954.mysteps.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.example.kzha6954.mysteps.Main.MainActivity;
import com.example.kzha6954.mysteps.R;

/**
 * Created by zkd on 18-08-2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    static int id =0;
    @Override
    public void onReceive(Context context, Intent intent){

        String msg = intent.getStringExtra("msg");
        //Log.i("Config", "msg received " + msg);
        //Toast.makeText(context,"notification has been create",Toast.LENGTH_SHORT).show();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.h1)
                .setContentTitle("Good job")
                .setContentText("Check your steps walked here")
                .setAutoCancel(true);

        //Connect to android activity
        intent = new Intent(context,MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        //adds the Intent that starts the activity
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,0, intent,0);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        id++;
        mNotificationManager.notify(id,mBuilder.build());

    }

}
