package ca.uqac.truckie.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

import ca.uqac.truckie.R;
import ca.uqac.truckie.util.FormUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupLoginFragment extends Fragment implements SignupFragmentInterface {

    @BindView(R.id.txt_email) AppCompatEditText mTxtEmail;
    @BindView(R.id.txt_password) AppCompatEditText mTxtPassword;
    @BindView(R.id.txt_repeat_password) AppCompatEditText mTxtRepeatPassword;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_signup_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public int validate(){
        int errorCount = FormUtil.checkEmptyField(mTxtEmail);
        errorCount += FormUtil.checkEmptyField(mTxtPassword);
        errorCount += FormUtil.checkEmptyField(mTxtRepeatPassword);
        if(!mTxtPassword.getText().toString().equals(mTxtRepeatPassword.getText().toString())){
            if(getContext() != null) {
                mTxtRepeatPassword.setError(getString(R.string.err_passwords_match));
            }
            errorCount++;
        }
        return errorCount;
    }

    public String getEmail() {
        return mTxtEmail.getText().toString();
    }

    public String getPassword() {
        return mTxtPassword.getText().toString();
    }

    public AppCompatEditText getEmailElement() {
        return mTxtEmail;
    }

    public AppCompatEditText getPasswordElement() {
        return mTxtPassword;
    }
}
