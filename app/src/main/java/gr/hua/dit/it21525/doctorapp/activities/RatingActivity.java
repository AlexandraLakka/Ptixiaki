package gr.hua.dit.it21525.doctorapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.models.Doctor;
import gr.hua.dit.it21525.doctorapp.models.DoctorReview;
import gr.hua.dit.it21525.doctorapp.models.MyReview;
import gr.hua.dit.it21525.doctorapp.models.Patient;

public class RatingActivity extends AppCompatActivity {

    private Doctor doctor;
    private CircleImageView image;
    private TextView name;
    private TextView specialty;
    private RatingBar ratingBar;
    private EditText review;
    private Button submit;
    private float rating;
    private static String TAG = "RatingActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        doctor = getIntent().getParcelableExtra("doctor");

        rating = 0;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewsById();
        setUpView();
        onClick();
    }

    private void findViewsById(){
        image = findViewById(R.id.doctor_image);
        name = findViewById(R.id.doctor_name);
        specialty = findViewById(R.id.doctor_specialty);
        ratingBar = findViewById(R.id.rating);
        review = findViewById(R.id.review);
        submit = findViewById(R.id.submit_feedback);
    }

    private void setUpView(){
        Picasso.get().load(doctor.getImagePath()).fit().into(image);
        name.setText(doctor.getFullName());
        specialty.setText(doctor.getSpecialty());
        Drawable stars = ratingBar.getProgressDrawable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            stars.setTint(getResources().getColor(R.color.colorPrimaryDark));
        }else{
            stars.setTint(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void onClick(){
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating = ratingBar.getRating();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rating == 0){
                    Toast.makeText(view.getContext(), "Please rate before you submit", Toast.LENGTH_SHORT).show();
                }else if (review.getText().toString().isEmpty()){
                    Toast.makeText(view.getContext(), "Please write a review", Toast.LENGTH_SHORT).show();
                }else{
                    storeReviewInFirebase();
                }
            }
        });
    }

    private void storeReviewInFirebase(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("/my_reviews/" + uid).push();
        final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("/doc_reviews/" + doctor.getUid()).push();

        final MyReview r = new MyReview(doctor, rating, review.getText().toString());

        if(!FirebaseAuth.getInstance().getCurrentUser().getProviderData().equals("google.com")){
            DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("/patients/" + uid);
            ref3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Patient patient = dataSnapshot.getValue(Patient.class);
                    DoctorReview r1 = new DoctorReview(patient, r);

                    ref1.setValue(r1).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Finally we saved to my_reviews Database");
                            Toast.makeText(getApplicationContext(), "Thank you for your feedback", Toast.LENGTH_SHORT).show();
                        }
                    });

                    ref2.setValue(r1).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Finally we saved to doc_reviews Database");
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            Patient patient = new Patient(FirebaseAuth.getInstance().getCurrentUser().getUid(), String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()),
                    FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), FirebaseAuth.getInstance().getCurrentUser().getEmail());
            DoctorReview r2 = new DoctorReview(patient, r);

            ref1.setValue(r2).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Finally we saved to my_reviews Database");
                    Toast.makeText(getApplicationContext(), "Thank you for your feedback", Toast.LENGTH_SHORT).show();
                }
            });

            ref2.setValue(r2).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Finally we saved to doc_reviews Database");
                }
            });
        }
    }
}
