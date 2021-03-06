package com.transvargo.transvargo.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.transvargo.transvargo.Boot;
import com.transvargo.transvargo.Chargements;
import com.transvargo.transvargo.Principal;
import com.transvargo.transvargo.R;
import com.transvargo.transvargo.model.Transporteur;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by BW.KOFFI on 29/09/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("##Trans-FCS", "Message payload : "+remoteMessage.getData());

        JSONObject json;

        try {
            json = (new JSONObject(remoteMessage.getData().toString())).getJSONObject("data");

            long[] pattern = {500,500,500,500,500,500,500,500,500};

            Intent listeIntent = null;
            if(Boot.getTransporteurConnecte().typetransporteur_id != Transporteur.CHAUFFEUR_FLOTTE)
            {
                listeIntent = new Intent(this, Principal.class);
            }else{ //Mode chauffeur activé
                listeIntent = new Intent(this, Chargements.class);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 555, listeIntent, 0);

            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle(json.getString("title"));
            bigTextStyle.bigText(json.getString("message"));

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.pico)
                    .setLights(Color.rgb(255,92,92), 500, 500)
                    .setVibrate(pattern)
                    .setAutoCancel(true)
                    .setStyle(bigTextStyle)
                    .setContentTitle(json.getString("title"))
                    .setContentText(json.getString("message"))
                    .setSound(Uri.parse("android.resource://"+ this.getPackageName() + "/" + R.raw.incoming_exp));

            builder.setContentIntent(pendingIntent);

            // Sets an ID for the notification
            int mNotificationId = 123456;
            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, builder.build());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}