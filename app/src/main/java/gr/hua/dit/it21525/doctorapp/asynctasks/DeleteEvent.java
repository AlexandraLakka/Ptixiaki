package gr.hua.dit.it21525.doctorapp.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.lang.ref.WeakReference;

import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.activities.AppointmentInfoActivity;
import gr.hua.dit.it21525.doctorapp.activities.MenuActivity;
import gr.hua.dit.it21525.doctorapp.fragments.AppointmentsFragment;

public class DeleteEvent extends AsyncTask<Void, Void, Void> {
    private WeakReference<AppointmentInfoActivity> activityReference;
    private com.google.api.services.calendar.Calendar mService;
    private GoogleCredential credential;
    private String calId;
    private String eventId;

    public DeleteEvent(GoogleCredential credential, AppointmentInfoActivity context, String calendarId, String eventId){
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Calling Google Calendar")
                .build();
        this.credential = credential;
        activityReference = new WeakReference<>(context);
        this.calId = calendarId;
        this.eventId = eventId;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        return deleteEvent();
    }

    private Void deleteEvent(){
        try {
            mService.events().delete(calId, eventId).execute();
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
        deleteAppointmentFromDatabase();
        Toast.makeText(activity, "Appointment deleted", Toast.LENGTH_SHORT).show();
        activity.finish();
        activity.startActivity(new Intent(activity, MenuActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                .FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void deleteAppointmentFromDatabase(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/appointments/" + uid + "/" + eventId);
        ref.setValue(null);
    }
}
