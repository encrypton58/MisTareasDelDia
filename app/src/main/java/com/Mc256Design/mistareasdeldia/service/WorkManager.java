package com.Mc256Design.mistareasdeldia.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.Mc256Design.mistareasdeldia.R;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class WorkManager extends Worker {

    private static final String CHANNEL_WORK = "CHANNEL WORK NT";

    Context context;

    public WorkManager(Context context, WorkerParameters workerParameters){
        super(context, workerParameters);
        this.context = context;
    }


    public static void saveNoti(long duration, Data data, String tag, Context context){

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(WorkManager.class)
                .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                .addTag(tag)
                .setInputData(data)
                .build();
        androidx.work.WorkManager.getInstance(context).enqueue(workRequest);
    }

    @NonNull
    @Override
    public Result doWork() {

        String title = getInputData().getString("titleTask");
        String details = getInputData().getString("descriptionTask");
        int id = getInputData().getInt("idTask", 0);

        long time = Long.parseLong(Objects.requireNonNull(getInputData().getString("designado"))) * 60000;
        Intent in = new Intent(context, TimerService.class);
        in.putExtra("time", time);
        in.putExtra("title", title);
        in.putExtra("details",details);
        createNotificationChannel();

        if(TimerService.instancia == null) {
            createNotification(title, details, id);
            ContextCompat.startForegroundService(context,in);
        }

        return Result.success();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "NOTIFICACION WORKERMANAGER";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_WORK, name, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void createNotification(String title, String description, int id){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_WORK);
        builder.setSmallIcon(R.drawable.adduser_image_user);
        builder.setContentTitle(title);
        builder.setContentText(description);
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setLights(Color.MAGENTA, 1000, 1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000,1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(id, builder.build());
    }

}
