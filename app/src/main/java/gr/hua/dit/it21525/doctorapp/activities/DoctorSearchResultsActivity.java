package gr.hua.dit.it21525.doctorapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;

import java.util.ArrayList;

import gr.hua.dit.it21525.doctorapp.interfaces.FirebaseCallback;
import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.models.Appointment;
import gr.hua.dit.it21525.doctorapp.models.Doctor;
import gr.hua.dit.it21525.doctorapp.models.DoctorReview;
import gr.hua.dit.it21525.doctorapp.viewholders.DoctorSearchViewHolder;
import gr.hua.dit.it21525.doctorapp.views.DoctorSearchResultsView;

public class DoctorSearchResultsActivity extends AppCompatActivity {

    private String specialty;
    private final String TAG = "DoctorSearchResults";

    //for when i want to use adapter and viewholder
    private ArrayList<Doctor> doctorArrayList;
    //private DoctorSearchResultsAdapter adapter;

    private RecyclerView recyclerView;
    private View empty;
    private GroupAdapter<GroupieViewHolder> groupAdapter;
    private FirebaseRecyclerAdapter adapter;
    private LinearLayout linlaHeaderProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_search_results);

        specialty = getIntent().getStringExtra("doctor_specialty");
        if (specialty == null) {
            Log.d(TAG, "Specialty is null");
        } else {
            Log.d(TAG, specialty);
        }

        doctorArrayList = new ArrayList<>();
        groupAdapter = new GroupAdapter<>();
        recyclerView = findViewById(R.id.doctor_results_recyclerview);
        empty = findViewById(R.id.no_results);
        linlaHeaderProgress = findViewById(R.id.linlaHeaderProgress);

        //setUpView();
        recyclerView.setLayoutManager(new LinearLayoutManager(DoctorSearchResultsActivity.this));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(DoctorSearchResultsActivity.this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        fetch();
    }

//    private void setUpView() {
//        getDoctorsArrayListBySpecialty(new FirebaseCallback() {
//            @Override
//            public void getDoctors(final ArrayList<Doctor> doctors) {
//                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
////                adapter = new DoctorSearchResultsAdapter(DoctorSearchResultsActivity.this, doctors);
//                for (Doctor doc : doctors) {
//                    groupAdapter.add(new DoctorSearchResultsView(doc));
//                }
//                recyclerView.setLayoutManager(new LinearLayoutManager(DoctorSearchResultsActivity.this));
//                recyclerView.setAdapter(groupAdapter);
//                if (groupAdapter.getItemCount() != 0) {
//                    recyclerView.setLayoutManager(new LinearLayoutManager(DoctorSearchResultsActivity.this));
//                    //adapter.setClickListener((DoctorSearchResultsAdapter.ItemClickListener) getApplicationContext());
//                    recyclerView.setAdapter(groupAdapter);
//                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
//                            layoutManager.getOrientation());
//                    recyclerView.addItemDecoration(dividerItemDecoration);
//
//                    groupAdapter.setOnItemClickListener(new OnItemClickListener() {
//                        @Override
//                        public void onItemClick(@NonNull Item item, @NonNull View view) {
//                            int position = groupAdapter.getAdapterPosition(item);
//                            startActivity(new Intent(view.getContext(), DoctorInfoActivity.class).putExtra("Doctor", doctors.get(position)));
//                            finish();
//                        }
//                    });
//                } else {
//                    empty.setVisibility(View.VISIBLE);
//                    recyclerView.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void getAppointments(ArrayList<Appointment> appointments) {
//
//            }
//
//            @Override
//            public void getReviews(ArrayList<DoctorReview> reviews) {
//
//            }
//        });
//    }
//
//    private void getDoctorsArrayListBySpecialty(final FirebaseCallback firebaseCallback) {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/doctors");
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot doctor : dataSnapshot.getChildren()) {
//                    final Doctor doc = doctor.getValue(Doctor.class);
//                    if (doc != null && doctor.child("specialty").getValue(String.class).equalsIgnoreCase(specialty)) {
//                        doctorArrayList.add(new Doctor(doc.getUid(), doc.getFullName(), doc.getAddress(), doc.getPhoneNumber(),
//                                doc.getWorkingHours(), doc.getUserName(), doc.getEmail(), doc.getSpecialty(), doc.getImagePath(), doc.getCalendarId()));
//                    }
//                }
//                firebaseCallback.getDoctors(doctorArrayList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference("/doctors").orderByChild("specialty").equalTo(specialty);
        final FirebaseRecyclerOptions<Doctor> options =
                new FirebaseRecyclerOptions.Builder<Doctor>()
                        .setQuery(query, Doctor.class).build();

        adapter = new FirebaseRecyclerAdapter<Doctor, DoctorSearchViewHolder>(options) {
            @NonNull
            @Override
            public DoctorSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_doctor_item, parent, false);
                return new DoctorSearchViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull final DoctorSearchViewHolder holder, final int position, @NonNull Doctor model) {
                if (model.getSpecialty().equals(specialty)) {
                    holder.setItem(model);
                }
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(view.getContext(), DoctorInfoActivity.class).putExtra("Doctor", options.getSnapshots().get(position)));
                        finish();
                    }
                });
            }

            @Override
            public void onDataChanged() {
                if (adapter.getItemCount() == 0) {
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    linlaHeaderProgress.setVisibility(View.GONE);
                }else{
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
