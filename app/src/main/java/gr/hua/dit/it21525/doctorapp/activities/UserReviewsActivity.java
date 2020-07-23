package gr.hua.dit.it21525.doctorapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;

import java.util.ArrayList;

import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.interfaces.FirebaseCallback;
import gr.hua.dit.it21525.doctorapp.models.Appointment;
import gr.hua.dit.it21525.doctorapp.models.Doctor;
import gr.hua.dit.it21525.doctorapp.models.DoctorReview;
import gr.hua.dit.it21525.doctorapp.viewholders.MyReviewsViewHolder;
import gr.hua.dit.it21525.doctorapp.viewholders.UserReviewsViewHolder;
import gr.hua.dit.it21525.doctorapp.views.UserReviewsView;

public class UserReviewsActivity extends AppCompatActivity {

    private String doctorUid;
    private ArrayList<DoctorReview> reviews;
    private RecyclerView recyclerView;
    private GroupAdapter<GroupieViewHolder> groupAdapter;
    private FirebaseRecyclerAdapter adapter;
    private LinearLayout linlaHeaderProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reviews);

        doctorUid = getIntent().getStringExtra("doc_uid");
        reviews = new ArrayList<>();
        groupAdapter = new GroupAdapter<>();
        recyclerView = findViewById(R.id.reviews_recyclerview);
        linlaHeaderProgress = findViewById(R.id.linlaHeaderProgress);

        recyclerView.setLayoutManager(new LinearLayoutManager(UserReviewsActivity.this));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(UserReviewsActivity.this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        fetch();
        //setUpView();
    }

//    private void setUpView(){
//        getReviews(new FirebaseCallback() {
//            @Override
//            public void getDoctors(ArrayList<Doctor> doctors) {
//
//            }
//
//            @Override
//            public void getAppointments(ArrayList<Appointment> appointments) {
//
//            }
//
//            @Override
//            public void getReviews(ArrayList<DoctorReview> reviews) {
//                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//                for (DoctorReview doctorReview : reviews){
//                    groupAdapter.add(new UserReviewsView(doctorReview));
//                }
//                recyclerView.setLayoutManager(new LinearLayoutManager(UserReviewsActivity.this));
//                recyclerView.setAdapter(groupAdapter);
//                if(groupAdapter.getItemCount() != 0) {
//                    recyclerView.setLayoutManager(new LinearLayoutManager(UserReviewsActivity.this));
//                    recyclerView.setAdapter(groupAdapter);
//                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
//                            layoutManager.getOrientation());
//                    recyclerView.addItemDecoration(dividerItemDecoration);
//                }
//            }
//        });
//    }
//
//    private void getReviews(final FirebaseCallback firebaseCallback){
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/doc_reviews/" + doctorUid);
//        ref.addListenerForSingleValueEvent(new ValueEventListener(){
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot doctorReview : dataSnapshot.getChildren()){
//                    final DoctorReview doctorReview1 = doctorReview.getValue(DoctorReview.class);
//                    reviews.add(doctorReview1);
//                }
//                firebaseCallback.getReviews(reviews);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference("/doc_reviews/" + doctorUid);
        final FirebaseRecyclerOptions<DoctorReview> options =
                new FirebaseRecyclerOptions.Builder<DoctorReview>()
                        .setQuery(query, DoctorReview.class).build();

        adapter = new FirebaseRecyclerAdapter<DoctorReview, UserReviewsViewHolder>(options) {
            @NonNull
            @Override
            public UserReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_review_item, parent, false);
                return new UserReviewsViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull final UserReviewsViewHolder holder, final int position, @NonNull DoctorReview model) {
                holder.setItem(model);
            }

            @Override
            public void onDataChanged() {
                if (adapter.getItemCount() != 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    linlaHeaderProgress.setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}