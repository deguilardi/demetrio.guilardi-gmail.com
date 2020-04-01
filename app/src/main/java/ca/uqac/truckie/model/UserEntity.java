package ca.uqac.truckie.model;

import com.google.firebase.auth.FirebaseUser;

public class UserEntity {

    private String id;
    private String email;

    @SuppressWarnings("unused")
    public UserEntity() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    @SuppressWarnings("unused")
    public String getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public void setId(String id) {
        this.id = id;
    }

}
