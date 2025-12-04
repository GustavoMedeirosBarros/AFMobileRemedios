package com.example.afremedios;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String nome = intent.getStringExtra("nome");
        String descricao = intent.getStringExtra("descricao");
        int notificationId = intent.getIntExtra("notificationId", (int) System.currentTimeMillis());

        // Toast para debug - vai aparecer na tela quando o alarme disparar
        Toast.makeText(context, "ALARME DISPARADO: " + nome, Toast.LENGTH_LONG).show();

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(context, "default")
                .setContentTitle("Hora do remédio: " + nome)
                .setContentText(descricao)
                .setSmallIcon(R.drawable.person_pin_circle_24px)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }
}
