package ca.uqac.truckie.activities;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import ca.uqac.truckie.R;
import ca.uqac.truckie.component.DeliveryAdapter;
import ca.uqac.truckie.model.DB;

import butterknife.BindView;
import butterknife.ButterKnife;
import durdinapps.rxfirebase2.RxFirebaseChildEvent;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.lst_my_deliveries) RecyclerView mLstMyDeliveries;
    @BindView(R.id.lst_auctions) RecyclerView mLstAuctions;
    @BindView(R.id.txt_msg_my_deliveries) TextView mTxtMsgMyDeliveries;
    @BindView(R.id.txt_msg_auctions) TextView mTxtMsgAuctions;

    private DeliveryAdapter mMyDeliveriesAdapter;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    public void onStart(){
        super.onStart();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        setupLists();

        DB.getInstance().getMyDeliveries(item -> manageAdapterItem(item, mMyDeliveriesAdapter, mLstMyDeliveries, mTxtMsgMyDeliveries, R.string.no_deliveries));
    }

    private void setupLists(){
        mLstMyDeliveries.setLayoutManager(new LinearLayoutManager(this));
        mMyDeliveriesAdapter = new DeliveryAdapter(this, new DeliveryAdapter.DeliveryAdapterOnClickHandler() {
            @Override
            public void onClick(int position, DeliveryAdapter.DeliveryAdapterViewHolder adapterViewHolder) {
//                DeliveryEntity delivery = mMyDeliveriesAdapter.getItem(position);
//                Intent intent = new Intent(self, DeliveryDetailsActivity.class);
//                intent.putExtra(DeliveryDetailsActivity.KEY_EXTRA_DELIVERY, delivery);
//                startActivity(intent);
            }
        });
        mLstMyDeliveries.setAdapter(mMyDeliveriesAdapter);
    }

    private void manageAdapterItem(RxFirebaseChildEvent<DataSnapshot> item, DeliveryAdapter adapter, RecyclerView list, TextView loadingElement, int zeroCountRes){
        adapter.manageChildItem(item);
        if(adapter.getItemCount() > 0){
            list.setVisibility(View.VISIBLE);
            loadingElement.setVisibility(View.INVISIBLE);
        }
        else{
            loadingElement.setText(zeroCountRes);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_request_delivery:
                Intent startSettingsActivity = new Intent(this, RequestDeliveryActivity.class);
                startActivity(startSettingsActivity);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.popup_quit_text)
                .setPositiveButton(R.string.popup_quit_yes, (dialog, which) -> finishAffinity())
                .setNegativeButton(R.string.popup_quit_no, null)
                .show();
    }
}
