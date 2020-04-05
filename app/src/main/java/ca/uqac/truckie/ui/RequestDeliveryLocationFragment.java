package ca.uqac.truckie.ui;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import ca.uqac.truckie.R;
import ca.uqac.truckie.activities.RequestDeliveryActivity;
import ca.uqac.truckie.util.FormUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;

public class RequestDeliveryLocationFragment extends Fragment implements
        View.OnClickListener, OnMapReadyCallback, TextView.OnEditorActionListener {

    public static int ID_ORIGIN = 1;
    public static int ID_DESTIN = 2;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;

    @BindView(R.id.txt_search) AppCompatEditText mTxtSearch;
    @BindView(R.id.txt_result) AppCompatTextView mTxtResult;
    @BindView(R.id.lyt_result) LinearLayout mLytResult;
    @BindView(R.id.btn_use_this) AppCompatButton mBtnUseThis;

    private int mID;
    private GoogleMap mMap;
    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition; // TODO - save camera position state
    private boolean mLocationPermissionGranted;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Address mAddress;

    public static RequestDeliveryLocationFragment factory(int id) {
        RequestDeliveryLocationFragment self = new RequestDeliveryLocationFragment();
        self.mID = id;
        return self;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_request_delivery_location, container, false);
        ButterKnife.bind(this, view);
        setupUI();
        setupMap(savedInstanceState);
        validate();
        return view;
    }

    private void setupUI(){
        mTxtSearch.setOnEditorActionListener(this);
        mBtnUseThis.setOnClickListener(this);
    }

    public Address getAddress(){
        return mAddress;
    }

    public String getAddressString(){
        return mTxtResult.getText().toString();
    }

    private void setupMap(Bundle savedInstanceState){
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.setIndoorEnabled(false); // bug prevent https://issuetracker.google.com/issues/35829548#comment4
        getLocationPermission();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()).getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            updateLocationUI();
        } else {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                getDeviceLocation();
            } else {
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            searchAddressByCoordinates(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                        } else {
                            searchAddressByCoordinates(mDefaultLocation);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void searchAddressByCoordinates(LatLng latLng){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        }
        catch (IOException | IllegalArgumentException ioException) {
            // TODO - Catch network or other I/O problems.
        }

        // Handle case where no address was found.
        if (addressList.size() > 0) {
            mAddress = addressList.get(0);

            ArrayList<String> addressFragments = new ArrayList<>();
            for (int i = 0; i <= mAddress.getMaxAddressLineIndex(); i++) {
                addressFragments.add(mAddress.getAddressLine(i));
            }
            mTxtResult.setText(TextUtils.join("\r\n", addressFragments));

            LatLng addressLatLng = new LatLng(mAddress.getLatitude(), mAddress.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(addressLatLng).visible(true);
            Marker marker = mMap.addMarker(markerOptions);
            markerOptions.anchor(0f, 0.5f);
            marker.showInfoWindow();
        }

        validate();
    }

    private void validate(){
        int numError = FormUtil.checkEmptyField(mTxtResult, R.string.placeholder_default);
        if(numError > 0){
            mBtnUseThis.setBackgroundColor(getResources().getColor(R.color.lightGrey));
            mBtnUseThis.setEnabled(false);
        }
        else{
            mBtnUseThis.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mBtnUseThis.setEnabled(true);
        }
    }

    private void actionSearchLocation(String searchString){
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocationName(searchString, 1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if(addressList.size() > 0){
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.clear();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            searchAddressByCoordinates(latLng);
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_use_this:
                RequestDeliveryActivity myActivity = (RequestDeliveryActivity)getActivity();
                assert myActivity != null;
                myActivity.goToNextPage();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        if(view.getId() == R.id.txt_search) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == ACTION_DOWN
                    || event.getAction() == KEYCODE_ENTER) {
                FormUtil.hideKeyBoard(getActivity());
                actionSearchLocation(view.getText().toString());
            }
        }
        return false;
    }
}
