package gr.hua.dit.it21525.doctorapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Patient implements Parcelable {
    private String uid;
    private String imagePath;
    private String username;
    private String email;
    private String role;

    public Patient() {
    }

    public Patient(String uid, String imagePath, String username, String email) {
        this.uid = uid;
        this.imagePath = imagePath;
        this.username = username;
        this.email = email;
    }

    public Patient(String uid, String imagePath, String username, String email, String role) {
        this.uid = uid;
        this.imagePath = imagePath;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    protected Patient(Parcel in) {
        uid = in.readString();
        imagePath = in.readString();
        username = in.readString();
        email = in.readString();
        role = in.readString();
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

    public String getUid(){
        return uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(imagePath);
        parcel.writeString(username);
        parcel.writeString(email);
        parcel.writeString(role);
    }
}
