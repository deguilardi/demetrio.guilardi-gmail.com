package ca.uqac.truckie.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import ca.uqac.truckie.MyUser;
import ca.uqac.truckie.R;
import ca.uqac.truckie.activities.MainActivity;
import ca.uqac.truckie.activities.RequestDeliveryActivity;
import ca.uqac.truckie.model.DB;
import ca.uqac.truckie.model.DeliveryEntity;
import ca.uqac.truckie.util.FormUtil;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RequestDeliveryDetailsFragment extends Fragment implements
        View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    @BindView(R.id.txt_origin_address) AppCompatTextView mTxtOriginAddress;
    @BindView(R.id.txt_origin_extra_info) AppCompatEditText mOriginExtraInfo;
    @BindView(R.id.txt_origin_date) AppCompatTextView mTxtOriginDate;
    @BindView(R.id.txt_origin_time) AppCompatTextView mTxtOriginTime;
    @BindView(R.id.btn_origin_date) AppCompatButton mBtnOriginDate;
    @BindView(R.id.btn_origin_time) AppCompatButton mBtnOriginTime;
    @BindView(R.id.txt_destin_address) AppCompatTextView mTxtDestinAddress;
    @BindView(R.id.txt_destin_extra_info) AppCompatEditText mDestinExtraInfo;
    @BindView(R.id.txt_destin_date) AppCompatTextView mTxtDestinDate;
    @BindView(R.id.txt_destin_time) AppCompatTextView mTxtDestinTime;
    @BindView(R.id.btn_destin_date) AppCompatButton mBtnDestinDate;
    @BindView(R.id.btn_destin_time) AppCompatButton mBtnDestinTime;
    @BindView(R.id.btn_submit) AppCompatButton mBtnSubmit;

    private Calendar mOriginDate = Calendar.getInstance();
    private Calendar mDestinDate = Calendar.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_request_delivery_details, container, false);
        ButterKnife.bind(this, view);
        setupUI();
        validate();
        return view;
    }

    // called when the page is changed
    public void refresh(){
        setupUI();
    }

    private void setupUI(){
        mBtnOriginDate.setOnClickListener(this);
        mBtnOriginTime.setOnClickListener(this);
        mBtnDestinDate.setOnClickListener(this);
        mBtnDestinTime.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
        mTxtOriginAddress.setText(getMyActivity().getFullAddress(RequestDeliveryActivity.KEY_ORIGIN));
        mTxtDestinAddress.setText(getMyActivity().getFullAddress(RequestDeliveryActivity.KEY_DESTIN));
    }

    private void actionOpenDatePicker(View view){
        Calendar now = Calendar.getInstance();
        DatePickerDialog piker = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        String tag = "DatePickerDialog-";
        tag += (view.getId() == R.id.btn_origin_date ? "origin" : "destin");
        FragmentManager fm = getFragmentManager();
        piker.show(Objects.requireNonNull(fm), tag);
    }

    private void actionOpenTimePicker(View view){
        Calendar now = Calendar.getInstance();
        TimePickerDialog piker = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR),
                0,
                false
        );

        String tag = "TimePickerDialog-";
        tag += (view.getId() == R.id.btn_origin_time ? "origin" : "destin");
        FragmentManager fm = getFragmentManager();
        piker.show(Objects.requireNonNull(fm), tag);
    }

    private void actionSubmit(){
        DeliveryEntity.MyAddress origin = new DeliveryEntity.MyAddress(
                getMyActivity().getAddress(RequestDeliveryActivity.KEY_ORIGIN),
                getMyActivity().getFullAddress(RequestDeliveryActivity.KEY_ORIGIN),
                mOriginDate,
                Objects.requireNonNull(mOriginExtraInfo.getText()).toString());
        DeliveryEntity.MyAddress destin = new DeliveryEntity.MyAddress(
                getMyActivity().getAddress(RequestDeliveryActivity.KEY_DESTIN),
                getMyActivity().getFullAddress(RequestDeliveryActivity.KEY_DESTIN),
                mDestinDate,
                Objects.requireNonNull(mDestinExtraInfo.getText()).toString());
        DeliveryEntity delivery = new DeliveryEntity(origin, destin);
        delivery.setUserID(MyUser.getFBUid());
        DB.getInstance().addDelivery(delivery, onComplete -> {
            if(onComplete.isSuccessful()){
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
            else if(onComplete.getException() != null) {
                Toast.makeText(getActivity(), onComplete.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validate(){
        int errorCount = FormUtil.checkEmptyField(mTxtOriginDate, R.string.placeholder_set_date);
        errorCount += FormUtil.checkEmptyField(mTxtOriginTime, R.string.placeholder_set_time);
        errorCount += FormUtil.checkEmptyField(mTxtDestinDate, R.string.placeholder_set_date);
        errorCount += FormUtil.checkEmptyField(mTxtDestinTime, R.string.placeholder_set_time);

        Resources res = Objects.requireNonNull(getActivity()).getResources();
        if(res != null) {
            if (errorCount > 0) {
                mBtnSubmit.setEnabled(false);
                mBtnSubmit.setBackgroundColor(res.getColor(R.color.lightGrey));
            } else {
                mBtnSubmit.setEnabled(true);
                mBtnSubmit.setBackgroundColor(res.getColor(R.color.colorPrimary));
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = (monthOfYear+1) + "/" + (dayOfMonth < 10 ? "0" : "") + dayOfMonth + "/"+year;
        if(view.getTag().equals("DatePickerDialog-origin")) {
            mTxtOriginDate.setText(date);
            mTxtOriginDate.setTextColor(getResources().getColor(R.color.darkGrey));
            mOriginDate.set(Calendar.YEAR, year);
            mOriginDate.set(Calendar.MONTH, monthOfYear);
            mOriginDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }
        else{
            mTxtDestinDate.setText(date);
            mTxtDestinDate.setTextColor(getResources().getColor(R.color.darkGrey));
            mDestinDate.set(Calendar.YEAR, year);
            mDestinDate.set(Calendar.MONTH, monthOfYear);
            mDestinDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }
        validate();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time =  (hourOfDay < 10 ? "0" : "") + hourOfDay + "h" + (minute < 10 ? "0" : "") + minute;
        if(view.getTag().equals("TimePickerDialog-origin")) {
            mTxtOriginTime.setText(time);
            mTxtOriginTime.setTextColor(getResources().getColor(R.color.darkGrey));
            mOriginDate.set(Calendar.HOUR, hourOfDay);
            mOriginDate.set(Calendar.MINUTE, minute);
            mOriginDate.set(Calendar.SECOND, 0);
        }
        else{
            mTxtDestinTime.setText(time);
            mTxtDestinTime.setTextColor(getResources().getColor(R.color.darkGrey));
            mDestinDate.set(Calendar.HOUR, hourOfDay);
            mDestinDate.set(Calendar.MINUTE, minute);
            mDestinDate.set(Calendar.SECOND, 0);
        }
        validate();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_origin_date:
            case R.id.btn_destin_date: actionOpenDatePicker(view); break;
            case R.id.btn_origin_time:
            case R.id.btn_destin_time: actionOpenTimePicker(view); break;
            case R.id.btn_submit: actionSubmit(); break;
        }
    }

    private RequestDeliveryActivity getMyActivity(){
        return (RequestDeliveryActivity) getActivity();
    }
}
