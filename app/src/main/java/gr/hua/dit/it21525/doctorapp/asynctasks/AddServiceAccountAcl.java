package gr.hua.dit.it21525.doctorapp.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.model.AclRule;

import java.io.IOException;

public class AddServiceAccountAcl extends AsyncTask<Void, Void, Void> {
    private com.google.api.services.calendar.Calendar mService;
    private String calendarId;

    public AddServiceAccountAcl(GoogleAccountCredential credential, String calId) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Calling Google Calendar")
                .build();
        this.calendarId = calId;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        return addServiceAccountAcl();
    }

    private Void addServiceAccountAcl(){
        AclRule rule = new AclRule();
        AclRule.Scope scope = new AclRule.Scope();
        scope.setType("user").setValue("doctorapp@doctroapp.iam.gserviceaccount.com");
        rule.setScope(scope).setRole("writer");

        AclRule createdRule = null;
        try {
            createdRule = mService.acl().insert(calendarId, rule).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("AddServiceAccountAcl", createdRule.getId());
        return null;
    }
}
