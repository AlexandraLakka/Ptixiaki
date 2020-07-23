package gr.hua.dit.it21525.doctorapp.models;

public class FavouriteDoctor {
    private Doctor favouriteDoctor;

    public FavouriteDoctor() {
    }

    public FavouriteDoctor(Doctor favouriteDoctor) {
        this.favouriteDoctor = favouriteDoctor;
    }

    public Doctor getFavouriteDoctor() {
        return favouriteDoctor;
    }

    public void setFavouriteDoctor(Doctor favouriteDoctor) {
        this.favouriteDoctor = favouriteDoctor;
    }
}
