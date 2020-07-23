package gr.hua.dit.it21525.doctorapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import gr.hua.dit.it21525.doctorapp.R;

public class LoginActivity extends AppCompatActivity {

    private TextView registerText;
    private EditText emailText;
    private EditText passwordText;
    private Button loginButton;
    private FirebaseAuth firebaseAuth;
    private final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerText = findViewById(R.id.register_text);
        emailText = findViewById(R.id.patient_email);
        passwordText = findViewById(R.id.patient_password);
        loginButton = findViewById(R.id.login_button);

        firebaseAuth = FirebaseAuth.getInstance();

        onCLick();
    }

    private void onCLick() {
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.this.startActivity(new Intent(getApplicationContext(), RegisterPatientActivity.class));
                LoginActivity.this.finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginPatient();
            }
        });
    }

    private void loginPatient() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
        }else{
            Log.d(TAG, "Attempt login with email: " + email);
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Log.d(TAG, "Login failed. " + task.getException());
                                Toast.makeText(LoginActivity.this, "Please enter valid email and password",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "Successfully logged user with uid: " + task.getResult().getUser().getUid());
                                LoginActivity.this.startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                                LoginActivity.this.finish();
                            }
                        }
                    });
        }
    }
}
