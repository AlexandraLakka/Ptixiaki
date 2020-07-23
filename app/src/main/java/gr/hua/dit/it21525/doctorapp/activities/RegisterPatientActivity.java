package gr.hua.dit.it21525.doctorapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.models.Patient;

public class RegisterPatientActivity extends AppCompatActivity {

    private Button registerButton;
    private TextView loginText;
    private EditText usernameText;
    private EditText emailText;
    private EditText passwordText;
    private SignInButton googleSignIn;
    private Button profileImage;
    private CircleImageView imageView;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    private static final String TAG = "RegisterPatientActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_patient);

        findViewsById();

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        onClick();
    }

    private void findViewsById(){
        registerButton = findViewById(R.id.register_button);
        loginText= findViewById(R.id.login_text);
        usernameText = findViewById(R.id.patient_username);
        emailText = findViewById(R.id.patient_email);
        passwordText = findViewById(R.id.patient_password);
        googleSignIn = findViewById(R.id.google_sign_in_button);
        profileImage = findViewById(R.id.patient_image);
        imageView = findViewById(R.id.select_patient_imageview);
    }

    private void onClick(){
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerPatient();
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterPatientActivity.this.startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                RegisterPatientActivity.this.finish();
            }
        });

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    //https://medium.com/@sfazleyrabbi/firebase-login-and-registration-authentication-99ea25388cbf
    private void registerPatient(){
        String username = usernameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }else if(password.length() < 6){
            Toast.makeText(this, "Please enter six or more characters for your password", Toast.LENGTH_SHORT).show();
        }else if(filePath == null){
            Toast.makeText(this, "Please select your profile image", Toast.LENGTH_SHORT).show();
        }else{
            Log.d(TAG, "Email is: " + email);
            Log.d(TAG, "Password is: " + password);

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterPatientActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Log.d(TAG, "Authentication failed. " + task.getException());
                                Toast.makeText(RegisterPatientActivity.this, "Please enter valid email address", Toast.LENGTH_SHORT).show();
                            }else {
                                Log.d(TAG, "New user registration: " + task.isSuccessful());
                                Log.d(TAG, "Successfully created user with uid:" +
                                        task.getResult().getUser().getUid());
                                uploadImageToFirebaseStorage();
                                //progressDialog.show();
                                addPatientToFirebase();
                            }
                        }
                    });
        }
    }

    private void addPatientToFirebase(){
        String uid = firebaseAuth.getCurrentUser().getUid();
        String role = "Patient";
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("/patients/" + uid);
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("/users/" + uid);

        String username = usernameText.getText().toString();
        String email = emailText.getText().toString();

        Patient patient1 = new Patient(uid, filePath.toString(), username, email);
        Patient patient2 = new Patient(uid, filePath.toString(), username, email, role);

        ref1.setValue(patient1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Finally we saved the user to patients Database");
            }
        });

        ref2.setValue(patient2).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Finally we saved the user to users Database");
            }
        });

        //progressDialog.dismiss();

        RegisterPatientActivity.this.startActivity(new Intent(RegisterPatientActivity.this, MenuActivity.class));
        RegisterPatientActivity.this.finish();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImageToFirebaseStorage(){
        if(filePath != null)
        {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                account = task.getResult(ApiException.class);
                if(account != null){
                    firebaseAuthWithGoogle(account);
                    startActivity(new Intent(RegisterPatientActivity.this, MenuActivity.class));
                    RegisterPatientActivity.this.finish();
                }else{
                    onBackPressed();
                }
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            Log.d(TAG, "Photo was selected");
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                profileImage.setAlpha(0);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void addGoogleSignInPatientToFirebase(GoogleSignInAccount account){
        String uid = firebaseAuth.getCurrentUser().getUid();
        String role = "Patient";
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("/patients/" + uid);
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("/users/" + uid);

        Patient patient1 = new Patient(uid, account.getPhotoUrl().toString(), account.getDisplayName(), account.getEmail());
        Patient patient2 = new Patient(uid, account.getPhotoUrl().toString(), account.getDisplayName(), account.getEmail(), role);

        ref1.setValue(patient1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Finally we saved the user to patients Database");
            }
        });

        ref2.setValue(patient2).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Finally we saved the user to users Database");
            }
        });
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
                            addGoogleSignInPatientToFirebase(account);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.register_patient_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //check if user is logged
        if (firebaseAuth.getCurrentUser() != null) {
            // User is logged in
            startActivity(new Intent(RegisterPatientActivity.this, MenuActivity.class));
            RegisterPatientActivity.this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.doctor_register, menu);
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                RegisterPatientActivity.this.startActivity(new Intent(getApplicationContext(), RegisterDoctorActivity.class));
                RegisterPatientActivity.this.finish();
                return true;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
