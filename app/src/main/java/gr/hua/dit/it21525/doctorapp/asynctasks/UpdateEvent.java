package gr.hua.dit.it21525.doctorapp.asynctasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

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

import gr.hua.dit.it21525.doctorapp.activities.AppointmentInfoActivity;
import gr.hua.dit.it21525.doctorapp.activities.MenuActivity;
import gr.hua.dit.it21525.doctorapp.models.Appointment;

public class UpdateEvent extends AsyncTask<Void, Void, Void> {
    private WeakReference<AppointmentInfoActivity> activityReference;
    private com.google.api.services.calendar.Calendar mService;
    private GoogleCredential credential;
    Appointment appointment;
    Appointment appointmentDB;

    public UpdateEvent(GoogleCredential credential, AppointmentInfoActivity context, Appointment appointment, Appointment appointmentDB){
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Calling Google Calendar")
                .build();
        this.credential = credential;
        activityReference = new WeakReference<>(context);
        this.appointment = appointment;
        this.appointmentDB = appointmentDB;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return updateEvent();
    }

    private Void updateEvent(){
        try {
            Event event = mService.events().get(appointment.getChosenDoctor().getCalendarId(), appointment.getId()).execute();
            event.setSummary(appointment.getTitle())
                    .setLocation(appointment.getLocation())
                    .setDescription(appointment.getDes());
            DateTime startDateTime = new DateTime(appointment.getStartDate() + "T"+ appointment.getStartTime() + ":00+02:00");
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Europe/Athens");
            event.setStart(start);

            DateTime endDateTime = new DateTime(appointment.getEndDate() + "T"+ appointment.getEndTime() + ":00+02:00");
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

            mService.events().update(appointment.getChosenDoctor().getCalendarId(), appointment.getId(), event).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        AppointmentInfoActivity.mProgress.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        AppointmentInfoActivity activity;
        activity = activityReference.get();
        if (activity == null || activity.isFinishing())
        {
            return;
        }
        AppointmentInfoActivity.mProgress.dismiss();
        updateEventInFirebase();
        Toast.makeText(activity, "Appointment updated", Toast.LENGTH_SHORT).show();
        activity.dialog.dismiss();
        activity.finish();
        activity.startActivity(new Intent(activity, MenuActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                .FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void updateEventInFirebase(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/appointments/" + uid + "/" + appointment.getId());
        ref.setValue(appointmentDB);
    }
}
