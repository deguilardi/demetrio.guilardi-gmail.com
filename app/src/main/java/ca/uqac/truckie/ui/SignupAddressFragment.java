package ca.uqac.truckie.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import ca.uqac.truckie.R;
import ca.uqac.truckie.model.UserEntity;
import ca.uqac.truckie.util.FormUtil;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupAddressFragment extends Fragment implements SignupFragmentInterface, View.OnTouchListener {

    @BindView(R.id.spn_country) AppCompatSpinner mSpnCountry;
    @BindView(R.id.spn_state) AppCompatSpinner mSpnState;
    @BindView(R.id.txt_city) AppCompatEditText mTxtCity;
    @BindView(R.id.txt_address) AppCompatEditText mTxtAddress;
    @BindView(R.id.txt_zip_code) AppCompatEditText mZipCode;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_signup_address, container, false);
        ButterKnife.bind(this, view);
        setupUI();
        return view;
    }

    private void setupUI(){
        ArrayAdapter<CharSequence> countriesAdapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getActivity()),
                R.array.countries_array, android.R.layout.simple_spinner_item);
        countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnCountry.setAdapter(countriesAdapter);
        mSpnCountry.setOnTouchListener(this);
        mSpnCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeCountry(position);
                if(position != 0) {
                    mSpnState.requestFocusFromTouch();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                changeCountry(-1);
            }
        });

        mSpnState.setOnTouchListener(this);
    }

    private void changeCountry(int position){
        int listResID = 0;
        switch (position){
            case 1: listResID = R.array.states_brazil_array; break;
            case 2: listResID = R.array.states_usa_array; break;
            default: listResID = R.array.spinner_default_placeholder_array; break;
        }

        ArrayAdapter<CharSequence> statesAdapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getActivity()),
                listResID, android.R.layout.simple_spinner_item);
        statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if(mSpnState.getAdapter() == null || mSpnState.getAdapter().getCount() != statesAdapter.getCount()){
            mSpnState.setAdapter(statesAdapter);
        }
    }

    public int validate(){
        int errorCount = FormUtil.checkEmptyField(mSpnCountry, R.string.country_default_value);
        errorCount += FormUtil.checkEmptyField(mSpnState, R.string.placeholder_default);
        errorCount += FormUtil.checkEmptyField(mTxtCity);
        errorCount += FormUtil.checkEmptyField(mTxtAddress);
        errorCount += FormUtil.checkEmptyField(mZipCode);
        return errorCount;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int id = view.getId();
        switch (id) {
            case R.id.spn_country:
            case R.id.spn_state:
                FormUtil.hideKeyBoard(getActivity());
                return false;
        }
        view.performClick();
        return true;
    }

    public UserEntity.Address getModelObject(){
        return new UserEntity.Address(getCountry(), getState(), getCity(), getAddress(), getZipCode());
    }

    public Long getCountry(){
        return (long) mSpnCountry.getSelectedItemPosition();
    }

    public Long getState(){
        return (long) mSpnState.getSelectedItemPosition();
    }

    public String getCity(){
        return mTxtCity.getText().toString();
    }

    public String getAddress(){
        return mTxtAddress.getText().toString();
    }

    public Long getZipCode(){
        return Long.parseLong(mZipCode.getText().toString().replaceAll("[^0-9]", ""));
    }
}
