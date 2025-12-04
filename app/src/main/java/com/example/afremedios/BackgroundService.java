package com.example.afremedios;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

public class BackgroundService extends Service {
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String nome = intent.getStringExtra("nome");
            String descricao = intent.getStringExtra("descricao");
            long tempoEspera = intent.getLongExtra("tempo_espera", 0);

            if (tempoEspera <= 0) {
                showNotification(nome, descricao);
            } else {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    showNotification(nome, descricao);
                    stopSelf();
                }, tempoEspera);
            }
        }
        return START_STICKY;
    }


    private void showNotification(String nome, String descricao) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, "default")
                .setContentTitle("Hora do remédio: " + nome)
                .setContentText(descricao)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }


}
