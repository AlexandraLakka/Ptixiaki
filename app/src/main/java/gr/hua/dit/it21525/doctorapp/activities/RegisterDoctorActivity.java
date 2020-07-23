package gr.hua.dit.it21525.doctorapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import de.hdodenhof.circleimageview.CircleImageView;
import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.adapters.NothingSelectedSpinnerAdapter;
import gr.hua.dit.it21525.doctorapp.asynctasks.AddServiceAccountAcl;
import gr.hua.dit.it21525.doctorapp.asynctasks.GetCalendarsList;
import gr.hua.dit.it21525.doctorapp.models.Doctor;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class RegisterDoctorActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private ArrayList<String> specialitiesActorList;
    private SpinnerDialog spinnerDialog;
    private String mSpecialtyUid = "";

    private static final int RC_SIGN_IN = 9001;
    public static final String TAG = "RegisterDoctorActivity";

    private EditText fullNameText;
    private EditText addressText;
    private EditText phoneNumberText;
    private EditText workingHoursText;
    private EditText userNameText;
    private EditText emailText;
    private EditText passwordText;
    private Button specialtyButton;
    private TextView choosenAccountText;
    public  Spinner userCalendars;
    private CheckBox agreementCheckBox;
    private Button doctorRegisterButton;
    //https://code.tutsplus.com/tutorials/image-upload-to-firebase-in-android-application--cms-29934
    private Button profileImage;
    private CircleImageView imageView;

    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton googleSignIn;
    private GoogleSignInAccount account;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    private String specialty;
    private boolean clicked;
    private boolean googleClicked;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private GoogleAccountCredential mCredential;
    public static ArrayAdapter<String> adapter;
    public static ArrayList<String> arrayList;
    public static String calendarId;

    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY, "https://www.googleapis.com/auth/calendar",
            "https://www.googleapis.com/auth/calendar.events", "https://www.googleapis.com/auth/calendar.events.readonly"};
    public static ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_doctor);

        findViewsById();

        instantiateVariables();

        onClick();
    }

    private void instantiateVariables(){
        specialitiesActorList = new ArrayList<>();
        arrayList = new ArrayList<>();
        calendarId = "";
        clicked = false;
        googleClicked = false;

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Calendar API ...");

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }

    private void findViewsById() {
        fullNameText = findViewById(R.id.doctor_fullname);
        addressText = findViewById(R.id.doctor_address);
        phoneNumberText = findViewById(R.id.doctor_phone_number);
        workingHoursText = findViewById(R.id.doctor_working_hours);
//        userNameText = findViewById(R.id.doctor_username);
//        emailText = findViewById(R.id.doctor_email);
//        passwordText = findViewById(R.id.doctor_password);
        specialtyButton = findViewById(R.id.select_specialty);
        googleSignIn = findViewById(R.id.google_sign_in_button);
        choosenAccountText = findViewById(R.id.chosen_account);
        userCalendars = findViewById(R.id.calendar_list);
        agreementCheckBox = findViewById(R.id.checkbox);
        doctorRegisterButton = findViewById(R.id.doctor_register_button);
//        profileImage = findViewById(R.id.doctor_image);
//        imageView = findViewById(R.id.select_doctor_imageview);
    }

    private void onClick() {
//        profileImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                chooseImage();
//            }
//        });

        specialtyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSpecialty(view);
                clicked = true;
            }
        });

        doctorRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(registerDoctor()){
//                    Snackbar snackbar = Snackbar.make(view, R.string.snackbar_message, Snackbar.LENGTH_INDEFINITE);
//                    snackbar.setAction(R.string.snackbar_action, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            RegisterDoctorActivity.this.startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//                            RegisterDoctorActivity.this.finish();
//                        }
//                    });
//                    snackbar.show();
//                }
                if (fullNameText.getText().toString().isEmpty() || addressText.getText().toString().isEmpty()
                        || phoneNumberText.getText().toString().isEmpty() || workingHoursText.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterDoctorActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else if (!clicked) {
                    Toast.makeText(RegisterDoctorActivity.this, "Please choose your specialty", Toast.LENGTH_SHORT).show();
                } else if (!googleClicked) {
                    Toast.makeText(RegisterDoctorActivity.this, "Please select a google account", Toast.LENGTH_SHORT).show();
                } else if(!agreementCheckBox.isChecked()){
                    Toast.makeText(RegisterDoctorActivity.this, "Please agree to terms", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuthWithGoogle(account);
                    new AddServiceAccountAcl(mCredential, calendarId).execute();
                    Snackbar.make(view, R.string.snackbar_message, Snackbar.LENGTH_INDEFINITE).show();
                }
            }
        });

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    account = task.getResult(ApiException.class);
                    if (account != null) {
                        choosenAccountText.setText(account.getEmail());
                        googleClicked = true;
                        getResultsFromApi();
                    } else {
                        onBackPressed();
                    }
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e);
                    // ...
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    private void addGoogleSignInDoctorInFirebase(GoogleSignInAccount account) {
        String uid = firebaseAuth.getCurrentUser().getUid();
        String role = "Doctor";
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("/doctors/" + uid);
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("/users/" + uid);

        String fullName = fullNameText.getText().toString();
        String address = addressText.getText().toString();
        String phoneNumber = phoneNumberText.getText().toString();
        String workingHours = workingHoursText.getText().toString();

        Doctor doctor1 = new Doctor(uid, fullName, address, phoneNumber, workingHours,
                account.getDisplayName(), account.getEmail(), specialty, account.getPhotoUrl().toString(), calendarId);
        Doctor doctor2 = new Doctor(uid, account.getPhotoUrl().toString(), fullName, address, phoneNumber, workingHours,
                account.getDisplayName(), account.getEmail(), specialty, role, calendarId);

        ref1.setValue(doctor1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Finally we saved the user to doctors Database");
            }
        });

        ref2.setValue(doctor2).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Finally we saved the user to users Database");
            }
        });

        revokeAccess();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.d(TAG, "Current user: " + user.getDisplayName());
                            addGoogleSignInDoctorInFirebase(account);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.register_patient_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(RegisterDoctorActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        firebaseAuth.signOut();
                        Log.d(TAG, "Access revoked for: " + account.getEmail());
                        // [END_EXCLUDE]
                    }
                });
    }

    private void getSpecialtiesList() {
        try {
            InputStream is = getApplicationContext().getAssets().open("specialties.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("element");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    specialitiesActorList.add(getValue("actor", element2));
                }
            }

            spinnerDialog.showSpinerDialog();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    public void selectSpecialty(View view) {

        if (specialitiesActorList.size() == 0) {
            getSpecialtiesList();
            setSpinnerDialog();
            spinnerDialog.showSpinerDialog();
        } else {
            setSpinnerDialog();
            spinnerDialog.showSpinerDialog();
        }

    }

    private void setSpinnerDialog() {
        final Button selectSpecialty = findViewById(R.id.select_specialty);
        spinnerDialog = new SpinnerDialog(
                this,
                specialitiesActorList,
                "Select or Search Specialty",
                R.style.DialogAnimations_SmileWindow,
                "Close");

        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String s, int i) {
                mSpecialtyUid = specialitiesActorList.get(i);
                selectSpecialty.setText(mSpecialtyUid);
                specialty = specialtyButton.getText().toString();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        revokeAccess();
        if(mProgress != null){
            mProgress.dismiss();
            mProgress = null;
        }
        //Log.d(TAG, firebaseAuth.getUid() + "has signed out successfully");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.patient_register, menu);
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                RegisterDoctorActivity.this.startActivity(new Intent(getApplicationContext(), RegisterPatientActivity.class));
                RegisterDoctorActivity.this.finish();
                return true;
            }
        });
        return true;
    }

    public void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                RegisterDoctorActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private void getResultsFromApi() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            if (account != null) {
                mCredential.setSelectedAccountName(account.getEmail());
                new GetCalendarsList(mCredential, this).execute();
            }
        }else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> list) {
        // Do nothing.
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> list) {
        // Do nothing.
    }

//    private class GetCalendarsList extends AsyncTask<Void, Void, HashMap<String, String>> {
//        private com.google.api.services.calendar.Calendar mService;
//        private Exception mLastError = null;
//
//        GetCalendarsList(GoogleAccountCredential credential) {
//            HttpTransport transport = AndroidHttp.newCompatibleTransport();
//            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
//            mService = new com.google.api.services.calendar.Calendar.Builder(
//                    transport, jsonFactory, credential)
//                    .setApplicationName("Calling Google Calendar")
//                    .build();
//        }
//
//        @Override
//        protected HashMap<String, String> doInBackground(Void... voids) {
//            try {
//                return getDataFromApi();
//            } catch (Exception e) {
//                mLastError = e;
//                cancel(true);
//                return null;
//            }
//        }
//
//        private HashMap<String, String> getDataFromApi() throws IOException {
//            String pageToken = null;
//            HashMap<String, String> calendars = new HashMap<>();
//
//            do {
//                CalendarList list = mService.calendarList().list().setPageToken(pageToken).execute();
//                List<CalendarListEntry> items = list.getItems();
//
//
//                for (CalendarListEntry calendarListEntry : items) {
//                    calendars.put(calendarListEntry.getId(), calendarListEntry.getSummary());
//                }
//                pageToken = list.getNextPageToken();
//            } while (pageToken != null);
//
//            return calendars;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            RegisterDoctorActivity.getInstance().mProgress.show();
//        }
//
//        @Override
//        protected void onPostExecute(final HashMap<String, String> output) {
//            RegisterDoctorActivity.getInstance().mProgress.hide();
//            if (output == null || output.size() == 0) {
//                output.put("", "No results returned");
//            } else {
//                for (Map.Entry summary : output.entrySet()){
//                    arrayList.add(summary.getValue().toString());
//                }
//                adapter = new ArrayAdapter<>(RegisterDoctorActivity.getInstance(),
//                        android.R.layout.simple_spinner_item, arrayList);
//                userCalendars.setAdapter(
//                        new NothingSelectedSpinnerAdapter(
//                                adapter,
//                                R.layout.spinner_row_nothing_selected,
//                                RegisterDoctorActivity.getInstance()));
//
//                //https://stackoverflow.com/questions/18312043/how-to-change-spinner-text-color
//                userCalendars.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                        ((TextView)adapterView.getChildAt(0)).setTextColor(Color.WHITE);
//                        ((TextView)adapterView.getChildAt(0)).setTextSize(14);
//                        ((TextView)adapterView.getChildAt(0)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//                        for (Map.Entry<String, String> key : output.entrySet()){
//                            if(((TextView)adapterView.getChildAt(0)).getText().toString().equals(key.getValue())){
//                                calendarId = key.getKey();
//                                Log.d(TAG, calendarId);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> adapterView) {}
//                });
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            RegisterDoctorActivity.getInstance().mProgress.hide();
//            if (mLastError != null) {
//                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
//                    showGooglePlayServicesAvailabilityErrorDialog(
//                            ((GooglePlayServicesAvailabilityIOException) mLastError)
//                                    .getConnectionStatusCode());
//                } else if (mLastError instanceof UserRecoverableAuthIOException) {
//                    startActivityForResult(
//                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
//                            RegisterDoctorActivity.REQUEST_AUTHORIZATION);
//                } else {
//                    Log.d(TAG, "The following error occurred:\n"
//                            + mLastError.getMessage());
//                }
//            } else {
//                Log.d(TAG, "Request cancelled.");
//            }
//        }
//    }


}


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
//                && data != null && data.getData() != null )
//        {
//            Log.d(TAG, "Photo was selected");
//            filePath = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                imageView.setImageBitmap(bitmap);
//                profileImage.setAlpha(0);
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
//    private boolean registerDoctor(){
//        boolean isSuccessful = false;
//        String fullName = fullNameText.getText().toString();
//        String address = addressText.getText().toString();
//        String phoneNumber = phoneNumberText.getText().toString();
//        String workingHours = workingHoursText.getText().toString();
//        String username = userNameText.getText().toString();
//        String email = emailText.getText().toString();
//        String password = passwordText.getText().toString();
//
//        if (fullName.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() || workingHours.isEmpty()||
//                username.isEmpty() || email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
//        }else if(password.length() < 6){
//            Toast.makeText(this, "Please enter six or more characters for your password", Toast.LENGTH_SHORT).show();
//        }else if(!clicked){
//            Toast.makeText(this, "Please choose your specialty", Toast.LENGTH_SHORT).show();
//        }else if(filePath == null){
//            Toast.makeText(this, "Please select your profile image", Toast.LENGTH_SHORT).show();
//        }else{
//            Log.d(TAG, "Email is: " + email);
//            Log.d(TAG, "Password is: " + password);
//
//            firebaseAuth.createUserWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(RegisterDoctorActivity.this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            Log.d(TAG, "New doctor registration: " + task.isSuccessful());
//                            Log.d(TAG, "Successfully created doctor with uid:" +
//                                    task.getResult().getUser().getUid());
//                            uploadImageToFirebaseStorage();
//                            if (!task.isSuccessful()) {
//                                Log.d(TAG, "Authentication failed. " + task.getException());
//                            }else {
//                                addDoctorToFirebase();
//                            }
//                        }
//                    });
//            isSuccessful = true;
//        }
//        return isSuccessful;
//    }
//
//    private void addDoctorToFirebase(){
//        String uid = firebaseAuth.getCurrentUser().getUid();
//        String role = "Doctor";
//        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("/doctors/" + uid);
//        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("/users/" + uid);
//
//        String fullName = fullNameText.getText().toString();
//        String address = addressText.getText().toString();
//        String phoneNumber = phoneNumberText.getText().toString();
//        String workingHours = workingHoursText.getText().toString();
//        String username = userNameText.getText().toString();
//        String email = emailText.getText().toString();
//
//        Doctor doctor1 = new Doctor(uid, fullName, address, phoneNumber, workingHours, username, email, specialty, filePath.toString());
//        Doctor doctor2 = new Doctor(uid, filePath.toString(), fullName, address, phoneNumber, workingHours, username, email, specialty, role);
//
//        ref1.setValue(doctor1).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "Finally we saved the user to doctors Database");
//            }
//        });
//
//        ref2.setValue(doctor2).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "Finally we saved the user to users Database");
//            }
//        });
//    }
//
//    private void chooseImage() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//    }
//
//    private void uploadImageToFirebaseStorage(){
//        if(filePath != null)
//        {
//            final ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();
//
//            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
//            ref.putFile(filePath)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            progressDialog.dismiss();
//                            Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            Toast.makeText(getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
//                                    .getTotalByteCount());
//                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
//                        }
//                    });
//        }
//    }
