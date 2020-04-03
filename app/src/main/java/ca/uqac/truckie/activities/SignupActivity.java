package ca.uqac.truckie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import ca.uqac.truckie.R;
import ca.uqac.truckie.model.DB;
import ca.uqac.truckie.model.UserEntity;
import ca.uqac.truckie.ui.SignupAddressFragment;
import ca.uqac.truckie.ui.SignupFragmentInterface;
import ca.uqac.truckie.ui.SignupLoginFragment;
import ca.uqac.truckie.ui.SignupPersonalFragment;
import ca.uqac.truckie.ui.SignupVehicleFragment;
import ca.uqac.truckie.util.FormUtil;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private static final int NUM_STEPS = 4;

    @BindView(R.id.btn_signup) Button mBtnSignUp;
    @BindView(R.id.pager) ViewPager mPager;

    private FirebaseAuth mAuth;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private int mLastPage = 0;
    private boolean mFirstOpen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.postponeEnterTransition(this);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        setupUI();
    }

    private void setupUI(){
        mBtnSignUp.setOnClickListener(this);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(this);
    }

    private void actionSignup(){
        FormUtil.hideKeyBoard(this);

        int currentPage = mPager.getCurrentItem();
        if(currentPage < 3){
            int errorCount = mPagerAdapter.getItemFrag(currentPage).validate();
            if(errorCount == 0){
                mPager.setCurrentItem(currentPage+1);
            }
        }
        else{
            int errorCount = mPagerAdapter.getItemFrag(0).validate();
            errorCount += mPagerAdapter.getItemFrag(1).validate();
            errorCount += mPagerAdapter.getItemFrag(2).validate();
            errorCount += mPagerAdapter.getItemFrag(3).validate();

            if (errorCount > 0) {
                FormUtil.showIsNotValidErrorMessage(errorCount, this);
            }
            else {
                final SignupActivity self = this;
                SignupLoginFragment loginFragment = (SignupLoginFragment)mPagerAdapter.getItemFrag(0);
                mAuth.createUserWithEmailAndPassword(loginFragment.getEmail(), loginFragment.getPassword())
                        .addOnCompleteListener(this, onComplete -> {
                            if (onComplete.isSuccessful()) {
                                SignupPersonalFragment personalFragment = (SignupPersonalFragment)mPagerAdapter.getItemFrag(1);
                                SignupAddressFragment addressFragment = (SignupAddressFragment)mPagerAdapter.getItemFrag(2);
                                SignupVehicleFragment vehicleFragment = (SignupVehicleFragment)mPagerAdapter.getItemFrag(3);
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                UserEntity user = new UserEntity(Objects.requireNonNull(firebaseUser), personalFragment.getModelObject(), addressFragment.getModelObject(), vehicleFragment.getModelObject());
                                DB.getInstance().addUser(user, onComplete1 -> {
                                    if(onComplete1.isSuccessful()){
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(self).toBundle();
                                            startActivity(intent, bundle);
                                        }
                                        else {
                                            startActivity(intent);
                                        }
                                    }
                                    else {
                                        if(onComplete1.getException() != null) {
                                            Toast.makeText(SignupActivity.this, onComplete1.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                if(onComplete.getException() != null) {
                                    Toast.makeText(SignupActivity.this, onComplete.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_signup:
                actionSignup();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        }
        else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(positionOffset == 0.0){
            mLastPage = position;
        }
        if(mFirstOpen){
            mFirstOpen = false;
            ActivityCompat.startPostponedEnterTransition(this);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if(position != mLastPage){
            mPagerAdapter.getItemFrag(mLastPage).validate();
            if(position < NUM_STEPS-1){
                mBtnSignUp.setText(R.string.next);
            }
            else{
                mBtnSignUp.setText(R.string.signup);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        int i = 0;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        SignupLoginFragment item0 = new SignupLoginFragment();
        SignupPersonalFragment item1 = new SignupPersonalFragment();
        SignupAddressFragment item2 = new SignupAddressFragment();
        SignupVehicleFragment item3 = new SignupVehicleFragment();

        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        SignupFragmentInterface getItemFrag(int position){
            return (SignupFragmentInterface) getItem(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return item0;
                case 1: return item1;
                case 2: return item2;
                case 3: return item3;
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_STEPS;
        }
    }
}
