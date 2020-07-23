package gr.hua.dit.it21525.doctorapp.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;

import java.util.ArrayList;

import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.activities.DoctorInfoActivity;
import gr.hua.dit.it21525.doctorapp.models.Doctor;
import gr.hua.dit.it21525.doctorapp.models.FavouriteDoctor;
import gr.hua.dit.it21525.doctorapp.viewholders.DoctorSearchViewHolder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private View empty;
    private GroupAdapter<GroupieViewHolder> groupAdapter;
    private ArrayList<Doctor> favouriteDoctorsList;
    private FirebaseRecyclerAdapter adapter;
    private LinearLayout linlaHeaderProgress;

    public FavouritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getActivity() != null) {
            getActivity().setTitle(R.string.favourites_fragment);
        }

        View root = inflater.inflate(R.layout.fragment_favourites, container, false);

        favouriteDoctorsList = new ArrayList<>();
        groupAdapter = new GroupAdapter<>();
        recyclerView = root.findViewById(R.id.favourites_recyclerview);
        empty = root.findViewById(R.id.no_favourites);
        linlaHeaderProgress = root.findViewById(R.id.linlaHeaderProgress);

        //setUpView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        fetch();
        // Inflate the layout for this fragment
        return root;
    }

//    private void setUpView(){
//        getFavouriteDoctors(new FirebaseCallback() {
//            @Override
//            public void getDoctors(final ArrayList<Doctor> favDoctors) {
//                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//                for(Doctor fav : favDoctors){
//                    groupAdapter.add(new DoctorSearchResultsView(fav));
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
//                            startActivity(new Intent(view.getContext(), DoctorInfoActivity.class).putExtra("Doctor", favDoctors.get(position)));
//                        }
//                    });
//                }else{
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
//    private void getFavouriteDoctors(final FirebaseCallback firebaseCallback){
//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/favourite_doctors/" + uid);
//
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot favourite : dataSnapshot.getChildren()){
//                    final FavouriteDoctor fav = favourite.getValue(FavouriteDoctor.class);
//                    favouriteDoctorsList.add(fav.getFavouriteDoctor());
//                }
//
//                firebaseCallback.getDoctors(favouriteDoctorsList);
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
        Query query = FirebaseDatabase.getInstance().getReference("/favourite_doctors/" + uid);
        final FirebaseRecyclerOptions<FavouriteDoctor> options =
                new FirebaseRecyclerOptions.Builder<FavouriteDoctor>()
                        .setQuery(query, FavouriteDoctor.class).build();

        adapter = new FirebaseRecyclerAdapter<FavouriteDoctor, DoctorSearchViewHolder>(options) {
            @NonNull
            @Override
            public DoctorSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_doctor_item, parent, false);
                return new DoctorSearchViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull final DoctorSearchViewHolder holder, final int position, @NonNull FavouriteDoctor model) {
                holder.setItem(model.getFavouriteDoctor());
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(view.getContext(), DoctorInfoActivity.class).putExtra("Doctor", options.getSnapshots().get(position).getFavouriteDoctor()));
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
