package ca.uqac.truckie.activities;


import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import ca.uqac.truckie.R;
import ca.uqac.truckie.component.DirectionsJSONParser;
import ca.uqac.truckie.model.DeliveryEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeliveryDetailsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    public final static String KEY_EXTRA_DELIVERY = "delivery";
    private static final int ROUTE_PADDING = 100;

    @BindView(R.id.txt_origin_date) TextView mTxtOriginDate;
    @BindView(R.id.txt_origin_address) TextView mTxtOriginAddress;
    @BindView(R.id.txt_origin_extra_info) TextView mTxtOriginExtraInfo;
    @BindView(R.id.txt_destin_date) TextView mTxtDestinDate;
    @BindView(R.id.txt_destin_address) TextView mTxtDestinAddress;
    @BindView(R.id.txt_destin_extra_info) TextView mTxtDestinExtraInfo;

    DeliveryEntity mDelivery;
    private GoogleMap mMap;
    protected boolean mIsAuction = false;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        // get the delivery
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        mDelivery = (DeliveryEntity) bundle.get(KEY_EXTRA_DELIVERY);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadView();
    }

    protected void loadView(){
        setContentView(R.layout.activity_delivery_details);
        ButterKnife.bind(this);
        setupUI();
        setupMap();
    }

    private void setupUI(){
        DeliveryEntity.MyAddress origin = mDelivery.getOrigin();
        mTxtOriginDate.setText(String.format("%s %s", origin.getShortDate(), origin.getShortTime()));
        mTxtOriginAddress.setText(origin.getFullAddress());
        if(!TextUtils.isEmpty(origin.getExtraInfo())) {
            mTxtOriginExtraInfo.setText(origin.getExtraInfo());
        }

        DeliveryEntity.MyAddress destin = mDelivery.getDestin();
        mTxtDestinDate.setText(String.format("%s %s", destin.getShortDate(), destin.getShortTime()));
        mTxtDestinAddress.setText(destin.getFullAddress());
        if(!TextUtils.isEmpty(destin.getExtraInfo())) {
            mTxtDestinExtraInfo.setText(destin.getExtraInfo());
        }
    }

    protected void setupMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // set basic vars
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this);
        LatLng originLatLng = new LatLng(mDelivery.getOrigin().getLatitude(), mDelivery.getOrigin().getLongitude());
        LatLng destinLatLng = new LatLng(mDelivery.getDestin().getLatitude(), mDelivery.getDestin().getLongitude());

        if(!mIsAuction) {

            // create origin marker
            MarkerOptions originMarker = new MarkerOptions();
            originMarker.position(originLatLng);
            originMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(originMarker);

            // create destin marker
            MarkerOptions destinMarker = new MarkerOptions();
            destinMarker.position(destinLatLng);
            destinMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(destinMarker);

        }
        else{
            mMap.getUiSettings().setAllGesturesEnabled(false);
        }

        // request / download route
        String url = getDirectionsUrl(originLatLng, destinLatLng);
        RequestRouteTask downloadTask = new RequestRouteTask();
        downloadTask.execute(url);
    }

    @Override
    public void onMapLoaded() {
        LatLng originLatLng = new LatLng(mDelivery.getOrigin().getLatitude(), mDelivery.getOrigin().getLongitude());
        LatLng destinLatLng = new LatLng(mDelivery.getDestin().getLatitude(), mDelivery.getDestin().getLongitude());

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boundsBuilder.include(originLatLng);
        boundsBuilder.include(destinLatLng);
        LatLngBounds latLngBounds = boundsBuilder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, ROUTE_PADDING));

    }

    /**
     * Make url to request route
     *
     * @param origin the start LatLng point
     * @param dest the end LatLng point
     * @return url to request
     */
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String originStr = "origin=" + origin.latitude + "," + origin.longitude;
        String destinStr = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String parameters = "key=" +getString(R.string.google_maps_key)+ "&" + originStr + "&" + destinStr + "&" + sensor + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    /**
     * Download the route from google server
     *
     * @param strUrl url from getDirectionsUrl
     * @return downloaded content
     * @throws IOException
     */
    private String downloadRoute(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception ignore) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * Request the route info on background
     */
    private class RequestRouteTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadRoute(url[0]);
            }
            catch (Exception e) {
                // TODO deal with this error
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParseRouteTask parserTask = new ParseRouteTask();
            parserTask.execute(result);
        }
    }

    /**
     * Parse the route info on background
     */
    private class ParseRouteTask extends AsyncTask<String, Integer, List<LatLng>> {

        @Override
        protected List<LatLng> doInBackground(String... jsonData) {
            List<LatLng> route = null;
            try {
                route = DirectionsJSONParser.parseRoute0(jsonData[0]);
            }
            catch (Exception ignore) {}
            return route;
        }

        @Override
        protected void onPostExecute(List<LatLng> result) {
            if(result == null){
                return;
            }
            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.addAll(result);
            lineOptions.width(12);
            lineOptions.color(R.color.orange);
            lineOptions.geodesic(true);
            mMap.addPolyline(lineOptions);
        }
    }
}

