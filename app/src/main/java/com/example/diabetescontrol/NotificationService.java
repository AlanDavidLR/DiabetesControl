package com.example.diabetescontrol;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class NotificationService extends Service {

    private static final String CHANNEL_ID = "DiabetesControlChannel";
    private static final int NOTIFICATION_ID = 1;
    private Notification notification;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String titulo = intent.getStringExtra("titulo");

        String nota = intent.getStringExtra("nota");


        createNotificationChannel(); // Crear el canal de notificación

        createNotification(titulo, nota); // Crear la notificación


        // Iniciar el servicio en primer plano

        startForeground(NOTIFICATION_ID, notification);


        return START_STICKY;

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Método para crear el canal de notificación
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Método para crear la notificación

    private void createNotification(String titulo, String nota) {

        // Crear la notificación

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_id")

                .setSmallIcon(R.drawable.ic_notification)

                .setContentTitle(titulo)

                .setContentText(nota)

                .setPriority(NotificationCompat.PRIORITY_HIGH)

                .setAutoCancel(true);

        // Crear un intent para abrir la actividad cuando se haga clic en la notificación
        Intent resultIntent = new Intent(this, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(pendingIntent);

        // Asignar la notificación construida a la variable notification
        notification = builder.build();
    }
}
