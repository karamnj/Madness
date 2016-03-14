package com.sirius.madness.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.sirius.madness.R;
import com.sirius.madness.ui.activities.MyScheduleActivity;
import com.sirius.madness.ui.activities.SessionDetailActivity;
import com.sirius.madness.ui.activities.SurveyActivity;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        String desc = intent.getStringExtra("desc");
        String title = intent.getStringExtra("title");
        Integer mId = intent.getIntExtra("mId", 0);
        boolean flag = intent.getBooleanExtra("flag",false);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(title)
                        .setContentText(desc)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Desc"))
                        .setWhen(System.currentTimeMillis())
                ;


        // Creates an explicit intent for a MyScheduleActivity
        Intent resultIntent;

        if(flag){
            resultIntent = new Intent(context, SurveyActivity.class);
            intent.putExtra("sessionId",mId);
        }else{

            Log.d("SessionID: ", mId.toString());
            resultIntent = new Intent(context, SessionDetailActivity.class);
            resultIntent.putExtra("Session Id",mId);

            //resultIntent = new Intent(context, MyScheduleActivity.class);
            //intent.putExtra("sessionId",mId);
        }

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MyScheduleActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());
    }
}
