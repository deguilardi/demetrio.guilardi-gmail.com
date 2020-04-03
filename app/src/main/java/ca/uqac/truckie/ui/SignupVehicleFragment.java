package ca.uqac.truckie.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import ca.uqac.truckie.R;
import ca.uqac.truckie.model.UserEntity;
import ca.uqac.truckie.util.FormUtil;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupVehicleFragment extends Fragment implements SignupFragmentInterface, View.OnTouchListener {

    @BindView(R.id.spn_vehicle_type_1) AppCompatSpinner mSpnType1;
    @BindView(R.id.spn_vehicle_type_2) AppCompatSpinner mSpnType2;
    @BindView(R.id.spn_vehicle_size) AppCompatSpinner mSpnSize;
    @BindView(R.id.spn_vehicle_capacity) AppCompatSpinner mSpnCapacity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_signup_vehicle, container, false);
        ButterKnife.bind(this, view);
        setupUI();
        return view;
    }

    private void setupUI(){
        ArrayAdapter<CharSequence> types1Adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getActivity()),
                R.array.vehicle_types, android.R.layout.simple_spinner_item);
        types1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnType1.setAdapter(types1Adapter);
        mSpnType1.setOnTouchListener(this);
        mSpnType1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeType1(position);
                if(position != 0) {
                    mSpnType2.requestFocusFromTouch();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                changeType1(-1);
            }
        });
        mSpnType1.setOnTouchListener(this);

        ArrayAdapter<CharSequence> sizesAdapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getActivity()),
                R.array.vehicle_sizes, android.R.layout.simple_spinner_item);
        sizesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnSize.setAdapter(sizesAdapter);

        ArrayAdapter<CharSequence> capacitiesAdapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getActivity()),
                R.array.vehicle_capacities, android.R.layout.simple_spinner_item);
        capacitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnCapacity.setAdapter(capacitiesAdapter);
    }

    private void changeType1(int position){
        int listResID = 0;
        switch (position){
            case 1: listResID = R.array.light_vehicle_types; break;
            case 2: listResID = R.array.medium_vehicle_types; break;
            case 3: listResID = R.array.heavy_vehicle_types; break;
            default: listResID = R.array.spinner_default_placeholder_array; break;
        }

        ArrayAdapter<CharSequence> statesAdapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getActivity()),
                listResID, android.R.layout.simple_spinner_item);
        statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnType2.setAdapter(statesAdapter);
    }

    public int validate(){
        int errorCount = FormUtil.checkEmptyField(mSpnType1, R.string.vehicle_type_default_value);
        errorCount += FormUtil.checkEmptyField(mSpnType2, R.string.placeholder_default);
        errorCount += FormUtil.checkEmptyField(mSpnSize, R.string.vehicle_size_default_value);
        errorCount += FormUtil.checkEmptyField(mSpnCapacity, R.string.vehicle_capacity_default_value);
        return errorCount;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int id = view.getId();
        switch (id) {
            case R.id.spn_vehicle_type_1:
            case R.id.spn_vehicle_type_2:
                FormUtil.hideKeyBoard(getActivity());
                return false;
        }
        view.performClick();
        return true;
    }

    public UserEntity.Vehicle getModelObject(){
        return new UserEntity.Vehicle(getType1(), getType2(), getSize(), getCapacity());
    }

    public Long getType1(){
        return (long) mSpnType1.getSelectedItemPosition();
    }

    public Long getType2(){
        return (long) mSpnType2.getSelectedItemPosition();
    }

    public Long getSize(){
        return (long) mSpnSize.getSelectedItemPosition();
    }

    public Long getCapacity(){
        return (long) mSpnCapacity.getSelectedItemPosition();
    }
}
