package gr.hua.dit.it21525.doctorapp.fragments;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.activities.DoctorSearchResultsActivity;
import gr.hua.dit.it21525.doctorapp.activities.MenuActivity;
import gr.hua.dit.it21525.doctorapp.activities.RegisterPatientActivity;
import gr.hua.dit.it21525.doctorapp.broadcastreceiver.NotificationReceiver;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private ArrayList<String> specialitiesUidList;
    private ArrayList<String> specialitiesActorList;
    private SpinnerDialog spinnerDialog;
    private Button searchDoctorSpecialty;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener fireAuthListener;
    private FirebaseUser user;
    private final String TAG = "Home Fragment";
    private String specialty;
    private boolean revoked;

    private GoogleSignInClient mGoogleSignInClient;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getActivity() != null) {
            getActivity().setTitle(R.string.home_fragmnent);
        }

        //https://stackoverflow.com/questions/15653737/oncreateoptionsmenu-inside-fragments
        setHasOptionsMenu(true);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        fireAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //get current user
                user = firebaseAuth.getCurrentUser();
            }
        };

        searchDoctorSpecialty = root.findViewById(R.id.doctor_specialty);

        specialitiesUidList = new ArrayList<>();
        specialitiesActorList = new ArrayList<>();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        revoked = false;

        onClick();

        // Inflate the layout for this fragment
        return root;
    }

    private void onClick() {
        searchDoctorSpecialty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSpecialty();
            }
        });
    }

    private void getSpecialtiesList() {
        try {
            InputStream is = getContext().getAssets().open("specialties.xml");

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
                    specialitiesUidList.add(getValue("uid", element2));
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

    private void selectSpecialty() {

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
        spinnerDialog = new SpinnerDialog(
                getActivity(),
                specialitiesActorList,
                "Select or Search Specialty",
                R.style.DialogAnimations_SmileWindow,
                "Close");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String s, int i) {
                searchDoctorSpecialty.setText(s);
                specialty = searchDoctorSpecialty.getText().toString();
                startActivity(new Intent(getContext(), DoctorSearchResultsActivity.class).putExtra("doctor_specialty", specialty));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(fireAuthListener);
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        firebaseAuth.signOut();
                        HomeFragment.this.startActivity(new Intent(getActivity(), RegisterPatientActivity.class));
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                        Log.d(TAG, "Access revoked for user: " + user.getUid());
                        // [END_EXCLUDE]
                    }
                });
    }

    private void cancelAlarm(){
        Intent intent = new Intent(getActivity(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.sign_out, menu);
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                revokeAccess();
                cancelAlarm();
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fireAuthListener != null) {
            firebaseAuth.removeAuthStateListener(fireAuthListener);
        }
    }

}
