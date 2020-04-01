package ca.uqac.truckie;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class MyUser {
    private static FirebaseUser mFirebaseUser;

    public static void init(){
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public static boolean isLogged(){
        return (mFirebaseUser != null);
    }

    public static FirebaseUser getFirebaseUser(){
        return mFirebaseUser;
    }

    public static String getFBUid(){
        if(!isLogged()){
            init();
        }
        return isLogged() ? getFirebaseUser().getUid() : null;
    }
}
