package ca.uqac.truckie.activities;

import android.location.Address;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import ca.uqac.truckie.R;
import ca.uqac.truckie.ui.RequestDeliveryDetailsFragment;
import ca.uqac.truckie.ui.RequestDeliveryLocationFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RequestDeliveryActivity extends AppCompatActivity implements
        View.OnClickListener, ViewPager.OnPageChangeListener {

    private static final int NUM_STEPS = 3;
    public static final int KEY_ORIGIN = 0;
    public static final int KEY_DESTIN = 1;
    public static final int KEY_DETAILS = 2;

    @BindView(R.id.pager) ViewPager mPager;

    private ScreenSlidePagerAdapter mPagerAdapter;
    private int mLastPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_delivery);
        ButterKnife.bind(this);
        setupUI();
    }

    private void setupUI(){
        setTitle(R.string.new_delivery_title_origin);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(this);
    }

    public String getFullAddress(int key){
        RequestDeliveryLocationFragment fragment = (RequestDeliveryLocationFragment) mPagerAdapter.getItem(key);
        return fragment.getAddressString();
    }

    public Address getAddress(int key){
        RequestDeliveryLocationFragment fragment = (RequestDeliveryLocationFragment) mPagerAdapter.getItem(key);
        return fragment.getAddress();
    }

    public void goToNextPage(){
        mPager.setCurrentItem(mPager.getCurrentItem()+1);
    }

    private void actionCreateDelivery() {
        // TODO
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_signup:
                actionCreateDelivery();
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
    }

    @Override
    public void onPageSelected(int position) {
        if(position != mLastPage){
            switch (position){
                case KEY_ORIGIN:
                    setTitle(R.string.new_delivery_title_origin);
                    break;
                case KEY_DESTIN:
                    setTitle(R.string.new_delivery_title_destin);
                    break;
                case KEY_DETAILS:
                    setTitle(R.string.new_delivery_title_details);
                    RequestDeliveryDetailsFragment fragment = (RequestDeliveryDetailsFragment) mPagerAdapter.getItem(KEY_DETAILS);
                    fragment.refresh();
                    break;
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        RequestDeliveryLocationFragment item0 = RequestDeliveryLocationFragment.factory(RequestDeliveryLocationFragment.ID_ORIGIN);
        RequestDeliveryLocationFragment item1 = RequestDeliveryLocationFragment.factory(RequestDeliveryLocationFragment.ID_DESTIN);
        RequestDeliveryDetailsFragment item2 = new RequestDeliveryDetailsFragment();

        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case KEY_ORIGIN: return item0;
                case KEY_DESTIN: return item1;
                case KEY_DETAILS: return item2;
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_STEPS;
        }
    }
}
