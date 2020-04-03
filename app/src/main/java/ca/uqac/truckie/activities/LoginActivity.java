package ca.uqac.truckie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import ca.uqac.truckie.MyUser;
import ca.uqac.truckie.R;
import ca.uqac.truckie.model.DB;
import ca.uqac.truckie.util.FormUtil;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.lyt_email) TextInputLayout mLytEmail;
    @BindView(R.id.txt_email) AppCompatEditText mTxtEmail;
    @BindView(R.id.lyt_password) TextInputLayout mLytPassword;
    @BindView(R.id.txt_password) AppCompatEditText mTxtPassword;
    @BindView(R.id.btn_login) Button mBtnLogin;
    @BindView(R.id.link_signup) TextView mLinkSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        MyUser.init();
        if(MyUser.isLogged()) {
            Task<Void> task = MyUser.getFirebaseUser().reload();
            task.addOnCompleteListener(result -> {
                if(result.isSuccessful()){
                    goToMainActivity();
                }
                else{
                    setupUI();
                }
            });
        }
        else{
            setupUI();
        }
    }

    private void setupUI(){
        mLytEmail.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        mLytPassword.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        mBtnLogin.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        mLinkSignup.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        mBtnLogin.setOnClickListener(this);
        mLinkSignup.setOnClickListener(this);
    }

    public void actionLogin(View view) {
        FormUtil.hideKeyBoard(this);

        int errorCount = FormUtil.checkEmptyField(mTxtEmail);
        errorCount += FormUtil.checkEmptyField(mTxtPassword);

        if(errorCount > 0) {
            FormUtil.showIsNotValidErrorMessage(errorCount, this);
        }
        else{
            String email = Objects.requireNonNull(mTxtEmail.getText()).toString();
            String password = Objects.requireNonNull(mTxtPassword.getText()).toString();
            DB.getInstance().auth(email, password, logged -> {
                if(logged){
                    MyUser.init();
                    assert MyUser.getFirebaseUser() != null;
                    goToMainActivity();
                }
                else{
                    Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
                    // TODO - show snackbar with "forgot password"
                }
            }, error ->{
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    public void actionCreateAccount(View view) {
        FormUtil.hideKeyBoard(this);
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Pair<View, String> pair1 = Pair.create(mTxtEmail, getString(R.string.transition_1));
            Pair<View, String> pair2 = Pair.create(mTxtPassword, getString(R.string.transition_2));
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair1, pair2).toBundle();
            startActivityForResult(intent, REQUEST_SIGNUP, bundle);
        }
        else {
            startActivityForResult(intent, REQUEST_SIGNUP);
        }
    }

    private void goToMainActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_login:
                actionLogin(view);
                break;
            case R.id.link_signup:
                actionCreateAccount(view);
                break;
        }
    }
}
