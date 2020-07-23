package gr.hua.dit.it21525.doctorapp.interfaces;

import java.util.ArrayList;

import gr.hua.dit.it21525.doctorapp.models.Appointment;
import gr.hua.dit.it21525.doctorapp.models.Doctor;
import gr.hua.dit.it21525.doctorapp.models.DoctorReview;

public interface FirebaseCallback {
    void getDoctors(ArrayList<Doctor> doctors);
    void getAppointments(ArrayList<Appointment> appointments);
    void getReviews(ArrayList<DoctorReview> reviews);
}
