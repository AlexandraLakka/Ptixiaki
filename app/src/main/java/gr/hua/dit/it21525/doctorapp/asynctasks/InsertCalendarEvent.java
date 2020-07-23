package gr.hua.dit.it21525.doctorapp.asynctasks;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;

import gr.hua.dit.it21525.doctorapp.activities.CalendarActivity;
import gr.hua.dit.it21525.doctorapp.models.Appointment;
import gr.hua.dit.it21525.doctorapp.models.Doctor;

public class InsertCalendarEvent extends AsyncTask<Void, Void, String> {

    private WeakReference<CalendarActivity> activityReference;
    private com.google.api.services.calendar.Calendar mService;
    private GoogleCredential credential;
    private String calId;
    private static String title;
    private static String location;
    private static String des;
    private static String startDate;
    private static String startTime;
    private static String startTimeDB;
    private static String endDate;
    private static String endTime;
    private static String endTimeDB;
    private static Doctor currentDoctor;
    private static final String TAG = "InsertCalendarEvent";

    public InsertCalendarEvent(GoogleCredential credential, CalendarActivity context, Doctor currentDoctor, String title, String des, String startDate, String startTime, String startTimeDB, String endDate, String endTime, String endTimeDB) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Calling Google Calendar")
                .build();
        this.credential = credential;
        activityReference = new WeakReference<>(context);
        InsertCalendarEvent.currentDoctor = currentDoctor;
        this.calId = currentDoctor.getCalendarId();
        InsertCalendarEvent.title = title;
        location = currentDoctor.getAddress();
        InsertCalendarEvent.des = des;
        InsertCalendarEvent.startDate = startDate;
        InsertCalendarEvent.startTime = startTime;
        InsertCalendarEvent.startTimeDB = startTimeDB;
        InsertCalendarEvent.endDate = endDate;
        InsertCalendarEvent.endTime = endTime;
        InsertCalendarEvent.endTimeDB = endTimeDB;
    }

    @Override
    protected String doInBackground(Void... voids) {
        return insertEvent();
    }

    private String insertEvent(){
        Event event = new Event()
                .setSummary(title)
                .setLocation(location)
                .setDescription(des);
        DateTime startDateTime = new DateTime(startDate + "T"+ startTime + ":00+02:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Europe/Athens");
        event.setStart(start);

        DateTime endDateTime = new DateTime(endDate + "T"+ endTime + ":00+02:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Europe/Athens");
        event.setEnd(end);

        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        try {
            mService.events().insert(calId, event).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return event.getDescription();
    }

    @Override
    protected void onPreExecute() {
        CalendarActivity.mProgress.show();
    }

    @Override
    protected void onPostExecute(String des) {
        CalendarActivity activity;
        activity = activityReference.get();
        if (activity == null || activity.isFinishing())
        {
            return;
        }
        CalendarActivity.mProgress.dismiss();
        new GetCalendarEventId(credential, calId, des).execute();
        Toast.makeText(activity, "Appointment added", Toast.LENGTH_SHORT).show();
        activity.eventDialog.dismiss();
        activity.finish();
        activity.startActivity(new Intent(activity, CalendarActivity.class).putExtra("Doctor", currentDoctor));
    }

    static void saveEventToFirebaseDatabase(String key){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/appointments/" + uid + "/" + key);

        Appointment appointment = new Appointment(key, title, location, des, startDate, startTimeDB, endDate, endTimeDB, currentDoctor);

        ref.setValue(appointment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Finally we saved to appointments Database");
            }
        });
    }
}
