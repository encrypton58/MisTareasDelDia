package com.Mc256Design.mistareasdeldia.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.Mc256Design.mistareasdeldia.MainActivity;
import com.Mc256Design.mistareasdeldia.R;
import com.Mc256Design.mistareasdeldia.SqliteControl.SqliteManager;

public class TimerService extends Service {

    private final static String TAG = "BroadcastService";
    private final static String CHANNEL_ID = "NOTIFICACIONTIMER ";
    private final static String CHANNEL_IDSOUND = "NOTIFICACIONSOUND";

    public static TimerService instancia = null;

    Bundle datos;

    SqliteManager sql;

    private CountDownClass cdt;

    @Override
    public void onCreate() {
        super.onCreate();
        this.sql = new SqliteManager(this);
        instancia = this;
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        datos = intent.getExtras();
        Log.i(TAG, "Starting timer...");
        final long tiempo = datos.getLong("time", 0);
        final String title = datos.getString("title");
        final String details = datos.getString("details");

        createNotificationChannel(CHANNEL_ID);
        createNotificationChannel(CHANNEL_IDSOUND);

        cdt = new CountDownClass(tiempo, 20*1000);
        cdt.start();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cdt != null) {
            cdt.cancel();
        }

        instancia = null;
    }

    public void stopBecauseDeleteTask(Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.item_view_edit);
        builder.setContentTitle("Se ha detenido la tarea ");
        builder.setContentText("la tarea fallo exitosamente");
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.MAGENTA, 1000, 1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000,1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context.getApplicationContext());
        notificationManagerCompat.notify((int) (Math.random() * 255), builder.build());
        onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel(String id){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Noticacion";
            NotificationChannel notificationChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


    private void createNotification(String title, String description, int id){

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sweet);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_IDSOUND);
        builder.setSmallIcon(R.drawable.item_view_edit);
        builder.setContentTitle(title);
        builder.setContentText(description);
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.MAGENTA, 1000, 1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000,1000});
        builder.setSound(uri);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(id, builder.build());

    }

    //Class del TimerCountDown

    private class CountDownClass extends CountDownTimer{

        String restTime;

        public CountDownClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {


            Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 10000);

            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                        0, notificationIntent, 0);

            if( (millisUntilFinished / 60000) == 0){
                restTime = millisUntilFinished / 1000 + " seg.";
            }else {
                restTime = millisUntilFinished / 60000 + " mins.";
            }

            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sweet);
            Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setContentTitle("algo")
                    .setContentText("algo" + " " + restTime)
                    .setSound(uri)
                    .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .setSmallIcon(R.drawable.item_view_delete)
                    .build();
            startForeground(101, notification);

        }

        @Override
        public void onFinish() {

            Log.i(TAG, "Timer finished");
            cdt.cancel();
            createNotification("has finalizado " + datos.getString("title"),
                    "Se ha finalizado el tiempo designado", (int) (Math.random()*255));
            Cursor c = sql.querySpecialTareas(datos.getString("title"));
            if(c.moveToFirst()){
                int id = c.getInt(0);
                String titulo = c.getString(1);
                String hora = c.getInt(2) + ":" + c.getInt(3);
                String des = c.getString(4);
                String fecha = c.getString(5);
                String designado = c.getString(6);
                sql.insertDataCompleteTasks(id,titulo,hora,des,fecha,designado);
                sql.deleteTareas(c.getInt(0));
                MainActivity main  = new MainActivity();
                main.deleteWorkManagerFromTask(String.valueOf(c.getInt(0)));
                main.initialComponents();


            }
            stopForeground(true);
            stopSelf();

        }

    }


}
