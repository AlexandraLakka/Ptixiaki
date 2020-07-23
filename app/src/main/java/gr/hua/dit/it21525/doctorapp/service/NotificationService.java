package gr.hua.dit.it21525.doctorapp.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.activities.AppointmentInfoActivity;
import gr.hua.dit.it21525.doctorapp.models.Appointment;

public class NotificationService extends IntentService {

    public NotificationService() {
        super("NotificationService");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        notificationDialog();
        Log.d("NotificationService", "Service running");
    }

    private void notificationDialog() {
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "doctor_app";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("DoctorApp channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        final Calendar c = Calendar.getInstance();

        final String date = c.get(Calendar.YEAR) + "-"
                + String.format(Locale.getDefault(), "%02d", c.get(Calendar.MONTH) + 1)
                + "-" + String.format(Locale.getDefault(), "%02d", c.get(Calendar.DAY_OF_MONTH));

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("/appointments/" + uid);
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot appointment : dataSnapshot.getChildren()) {
                    final Appointment appointment1 = appointment.getValue(Appointment.class);
                    if(appointment1 != null && appointment1.getStartDate().equals(date) && c.get(Calendar.HOUR_OF_DAY)  == Integer.parseInt(appointment1.getStartTime().substring(0,2)) - 1
                        && c.get(Calendar.MINUTE) == Integer.parseInt(appointment1.getStartTime().substring(3,5))) {
                        try {
                            notificationBuilder.setAutoCancel(true)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setWhen(System.currentTimeMillis())
                                    .setSmallIcon(R.drawable.ic_date_range_black_24dp)
                                    .setTicker("DoctorApp")
                                    //.setPriority(Notification.PRIORITY_MAX)
                                    .setContentTitle(appointment1.getTitle())
                                    .setContentText(AppointmentInfoActivity.formatDate(appointment1.getStartDate(), "yyyy-mm-dd", "dd/mm/yyyy") + ", " + appointment1.getStartTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    //.setContentInfo("Information");
                    notificationManager.notify(1, notificationBuilder.build());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
