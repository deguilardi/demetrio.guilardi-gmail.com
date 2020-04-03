package ca.uqac.truckie.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

import ca.uqac.truckie.R;
import ca.uqac.truckie.model.UserEntity;
import ca.uqac.truckie.util.FormUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupPersonalFragment extends Fragment implements SignupFragmentInterface {

    @BindView(R.id.txt_name) AppCompatEditText mTxtName;
    @BindView(R.id.txt_phone_1) AppCompatEditText mTxtPhone1;
    @BindView(R.id.txt_phone_2) AppCompatEditText mTxtPhone2;
    @BindView(R.id.txt_driver_license) AppCompatEditText mTxtDriverLicense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_signup_personal, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public int validate(){
        int errorCount = FormUtil.checkEmptyField(mTxtName);
        errorCount += FormUtil.checkEmptyField(mTxtPhone1);
        errorCount += FormUtil.checkEmptyField(mTxtPhone2);
        errorCount += FormUtil.checkEmptyField(mTxtDriverLicense);
        return errorCount;
    }

    public UserEntity.Personal getModelObject(){
        return new UserEntity.Personal(getName(), getPhone1(), getPhone2(), getDriverLicense());
    }

    public String getName(){
        return mTxtName.getText().toString();
    }

    public Long getPhone1(){
        return Long.parseLong( mTxtPhone1.getText().toString().replaceAll("[^0-9]", "") );
    }

    public Long getPhone2(){
        return Long.parseLong( mTxtPhone2.getText().toString().replaceAll("[^0-9]", "") );
    }

    public String getDriverLicense(){
        return mTxtDriverLicense.getText().toString();
    }
}
