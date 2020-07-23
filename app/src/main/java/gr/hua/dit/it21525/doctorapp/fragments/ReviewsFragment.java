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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.activities.AppointmentInfoActivity;
import gr.hua.dit.it21525.doctorapp.models.Appointment;
import gr.hua.dit.it21525.doctorapp.models.DoctorReview;
import gr.hua.dit.it21525.doctorapp.models.MyReview;
import gr.hua.dit.it21525.doctorapp.viewholders.AppointmentsViewHolder;
import gr.hua.dit.it21525.doctorapp.viewholders.MyReviewsViewHolder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewsFragment extends Fragment {

    private RecyclerView recyclerView;
    private View empty;
    private FirebaseRecyclerAdapter adapter;
    private LinearLayout linlaHeaderProgress;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getActivity() != null) {
            getActivity().setTitle(R.string.reviews_fragment);
        }

        View root = inflater.inflate(R.layout.fragment_reviews, container, false);
        recyclerView = root.findViewById(R.id.reviews_recyclerview);
        empty = root.findViewById(R.id.no_reviews);
        linlaHeaderProgress = root.findViewById(R.id.linlaHeaderProgress);

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

    private void fetch() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = FirebaseDatabase.getInstance().getReference("/my_reviews/" + uid);
        final FirebaseRecyclerOptions<DoctorReview> options =
                new FirebaseRecyclerOptions.Builder<DoctorReview>()
                        .setQuery(query, DoctorReview.class).build();

        adapter = new FirebaseRecyclerAdapter<DoctorReview, MyReviewsViewHolder>(options) {
            @NonNull
            @Override
            public MyReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
                return new MyReviewsViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull final MyReviewsViewHolder holder, final int position, @NonNull DoctorReview model) {
                holder.setItem(model);
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