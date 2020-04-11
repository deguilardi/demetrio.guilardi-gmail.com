package ca.uqac.truckie.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;

import ca.uqac.truckie.MyUser;
import ca.uqac.truckie.R;
import ca.uqac.truckie.util.NightModeController;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        NightModeController.init(getApplication());
        MyUser.init();
        if(MyUser.isLogged()) {
            Task<Void> task = MyUser.getFirebaseUser().reload();
            task.addOnCompleteListener(result -> {
                if(result.isSuccessful()){
                    goToMain();
                }
                else{
                    goToLogin();
                }
            });
        }
        else{
            goToLogin();
        }
    }

    private void goToMain(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void goToLogin(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}
