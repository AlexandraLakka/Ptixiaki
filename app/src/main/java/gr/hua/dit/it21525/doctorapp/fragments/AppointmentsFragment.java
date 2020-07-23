package gr.hua.dit.it21525.doctorapp.fragments;


import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;

import java.util.ArrayList;

import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.activities.AppointmentInfoActivity;
import gr.hua.dit.it21525.doctorapp.activities.DoctorInfoActivity;
import gr.hua.dit.it21525.doctorapp.activities.RatingActivity;
import gr.hua.dit.it21525.doctorapp.adapters.DoctorSearchResultsAdapter;
import gr.hua.dit.it21525.doctorapp.interfaces.FirebaseCallback;
import gr.hua.dit.it21525.doctorapp.models.Appointment;
import gr.hua.dit.it21525.doctorapp.models.Doctor;
import gr.hua.dit.it21525.doctorapp.models.DoctorReview;
import gr.hua.dit.it21525.doctorapp.models.FavouriteDoctor;
import gr.hua.dit.it21525.doctorapp.viewholders.AppointmentsViewHolder;
import gr.hua.dit.it21525.doctorapp.viewholders.DoctorSearchViewHolder;
import gr.hua.dit.it21525.doctorapp.views.AppointmentsView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private View empty;
    private GroupAdapter<GroupieViewHolder> groupAdapter;
    private ArrayList<Appointment> appointmentsList;
    private FirebaseRecyclerAdapter adapter;
    private LinearLayout linlaHeaderProgress;

    public AppointmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getActivity() != null) {
            getActivity().setTitle(R.string.appointments_fragment);
        }

        View root = inflater.inflate(R.layout.fragment_appointments, container, false);

        appointmentsList = new ArrayList<>();
        groupAdapter = new GroupAdapter<>();
        recyclerView = root.findViewById(R.id.doctor_appointments_recyclerview);
        empty = root.findViewById(R.id.no_appointments);
        linlaHeaderProgress = root.findViewById(R.id.headerProgress);

        //setUpView();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        fetch();

        return root;
    }

//    private void setUpView(){
//        getAppointments(new FirebaseCallback() {
//            @Override
//            public void getDoctors(ArrayList<Doctor> doctors) {
//
//            }
//
//            @Override
//            public void getAppointments(ArrayList<Appointment> appointments) {
//                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//                for(Appointment appointment : appointments){
//                    groupAdapter.add(new AppointmentsView(appointment, getContext()));
//                }
//
//                if(groupAdapter.getGroupCount() != 0){
//                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//                    recyclerView.setAdapter(groupAdapter);
//                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
//                            layoutManager.getOrientation());
//                    recyclerView.addItemDecoration(dividerItemDecoration);
//                    groupAdapter.setOnItemClickListener(new OnItemClickListener() {
//                        @Override
//                        public void onItemClick(@NonNull Item item, @NonNull View view) {
//                            int position = groupAdapter.getAdapterPosition(item);
//                            startActivity(new Intent(view.getContext(), AppointmentInfoActivity.class).putExtra("Appointment", appointmentsList.get(position)));
//                        }
//                    });
//                }else{
//                    empty.setVisibility(View.VISIBLE);
//                    recyclerView.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void getReviews(ArrayList<DoctorReview> reviews) {
//
//            }
//        });
//    }
//
//    private void getAppointments(final FirebaseCallback firebaseCallback){
//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/appointments/" + uid);
//
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot a : dataSnapshot.getChildren()){
//                    final Appointment appointment = a.getValue(Appointment.class);
//                    appointmentsList.add(appointment);
//                }
//
//                firebaseCallback.getAppointments(appointmentsList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void fetch() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = FirebaseDatabase.getInstance().getReference("/appointments/" + uid);
        final FirebaseRecyclerOptions<Appointment> options =
                new FirebaseRecyclerOptions.Builder<Appointment>()
                        .setQuery(query, Appointment.class).build();

        adapter = new FirebaseRecyclerAdapter<Appointment, AppointmentsViewHolder>(options) {
            @NonNull
            @Override
            public AppointmentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
                return new AppointmentsViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull final AppointmentsViewHolder holder, final int position, @NonNull Appointment model) {
                holder.setItem(model);
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(view.getContext(), AppointmentInfoActivity.class).putExtra("Appointment", options.getSnapshots().get(position)));
                    }
                });
                holder.rateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(view.getContext(), RatingActivity.class).putExtra("doctor", options.getSnapshots().get(position).getChosenDoctor()));
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter.startListening();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

}
