package gr.hua.dit.it21525.doctorapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.models.Doctor;
import gr.hua.dit.it21525.doctorapp.models.DoctorReview;
import gr.hua.dit.it21525.doctorapp.models.FavouriteDoctor;

public class DoctorInfoActivity extends AppCompatActivity {

    private Doctor currentDoctor;
    private CircleImageView image;
    private TextView name;
    private TextView specialty;
    private ToggleButton favourite;
    private TextView score;
    private TextView address;
    private TextView phoneNumber;
    private TextView workingHours;
    private TextView email;
    private Button bookAppointment;
    private Button checkReviews;
    private boolean isFavourite;
    private static final String TAG = "DoctorInfoActivity";
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_info);

        currentDoctor = getIntent().getParcelableExtra("Doctor");

        rootRef = FirebaseDatabase.getInstance().getReference();

        //isFavourite = readState();

        findViewsById();
        setUpViews();
        onClick();
    }

    private void findViewsById() {
        image = findViewById(R.id.doctor_imageview);
        name = findViewById(R.id.doctor_name);
        specialty = findViewById(R.id.doctor_specialty);
        favourite = findViewById(R.id.button_favorite);
        score = findViewById(R.id.score);
        address = findViewById(R.id.address);
        phoneNumber = findViewById(R.id.phone_number);
        workingHours = findViewById(R.id.working_hours);
        email = findViewById(R.id.email);
        bookAppointment = findViewById(R.id.book_appointment);
        checkReviews = findViewById(R.id.check_reviews);
    }

    private void setUpViews() {
        Picasso.get().load(currentDoctor.getImagePath()).fit().into(image);
        name.setText(currentDoctor.getFullName());
        specialty.setText(currentDoctor.getSpecialty());
        address.setText(currentDoctor.getAddress());
        phoneNumber.setText(currentDoctor.getPhoneNumber());
        workingHours.setText(currentDoctor.getWorkingHours());
        email.setText(currentDoctor.getEmail());

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    favourite.setBackground(getDrawable(R.drawable.ic_favorite_border_black_24dp));
                }else{
                    isDoctorFavourite();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        calculateScore();
    }

    private void isDoctorFavourite(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/favourite_doctors/" + uid + "/" + currentDoctor.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    isFavourite = false;
                    favourite.setBackground(getDrawable(R.drawable.ic_favorite_border_black_24dp));
                }else{
                    isFavourite = true;
                    favourite.setBackground(getDrawable(R.drawable.ic_favorite_black_24dp));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onClick() {
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavourite) {
                    isFavourite = false;
                    favourite.setBackground(getDrawable(R.drawable.ic_favorite_border_black_24dp));
                    //saveState(isFavourite);
                } else {
                    isFavourite = true;
                    favourite.setBackground(getDrawable(R.drawable.ic_favorite_black_24dp));
                    //saveState(isFavourite);
                }

                storeFavouritesInFirebase(isFavourite);
            }
        });

        bookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoctorInfoActivity.this.startActivity(new Intent(view.getContext(), CalendarActivity.class).putExtra("Doctor", currentDoctor));
            }
        });

        checkReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoctorInfoActivity.this.startActivity(new Intent(view.getContext(), UserReviewsActivity.class).putExtra("doc_uid", currentDoctor.getUid()));
            }
        });
    }

    private void storeFavouritesInFirebase(boolean isFavourite) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/favourite_doctors/" + uid + "/" + currentDoctor.getUid());

        if (isFavourite) {
            FavouriteDoctor favourite = new FavouriteDoctor(currentDoctor);
            ref.setValue(favourite).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Finally we saved the user to favourite_doctors Database");
                }
            });
        } else {
            ref.setValue(null);
        }
    }

    private void calculateScore(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/doc_reviews/" + currentDoctor.getUid());
        final ArrayList<Float> scores = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    final DoctorReview doctorReview = dataSnapshot1.getValue(DoctorReview.class);
                    scores.add(doctorReview.getReview().getRating());
                }
                float sum = 0;
                for(Float score : scores){
                    sum += score;
                }
                if(!scores.isEmpty()){
                    //https://stackoverflow.com/questions/10959424/show-only-two-digit-after-decimal/10959430#10959430
                    score.setText(getString(R.string.score, new DecimalFormat("##.#").format(sum/scores.size()), scores.size()));
                    checkReviews.setVisibility(View.VISIBLE);
                }else{
                    score.setText(getString(R.string.score, String.valueOf(0), scores.size()));
                    checkReviews.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveState(boolean isFavourite) {
        SharedPreferences aSharedPreferences = this.getSharedPreferences(
                "Favourite", Context.MODE_PRIVATE);
        SharedPreferences.Editor aSharedPreferencesEdit = aSharedPreferences
                .edit();
        aSharedPreferencesEdit.putBoolean("State", isFavourite);
        aSharedPreferencesEdit.apply();
    }

    private boolean readState() {
        SharedPreferences aSharedPreferences = this.getSharedPreferences(
                "Favourite", Context.MODE_PRIVATE);
        return aSharedPreferences.getBoolean("State", true);
    }
}
