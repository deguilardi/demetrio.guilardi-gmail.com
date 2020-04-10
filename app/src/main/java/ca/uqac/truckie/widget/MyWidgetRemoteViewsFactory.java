package ca.uqac.truckie.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.google.firebase.database.DataSnapshot;
import ca.uqac.truckie.MyUser;
import ca.uqac.truckie.R;
import ca.uqac.truckie.activities.DeliveryDetailsActivity;
import ca.uqac.truckie.model.DB;
import ca.uqac.truckie.model.DeliveryEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MyWidgetRemoteViewsFactory implements RemoteViewsFactory {

    private Context mContext;
    private List<DeliveryEntity> mDeliveries;
    private CountDownLatch doneSignal = new CountDownLatch(1);

    public MyWidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        mDeliveries = new ArrayList<>();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        final long tokenID = Binder.clearCallingIdentity();
        if(MyUser.isLogged()) {
            DB.getInstance().getMyDeliveriesAllAtOnce(items -> {
                mDeliveries = new ArrayList<>();
                Iterable<DataSnapshot> it = items.getChildren();
                for (DataSnapshot itemSnapshot : it) {
                    DeliveryEntity delivery = itemSnapshot.getValue(DeliveryEntity.class);
                    mDeliveries.add(delivery);
                }
                Binder.restoreCallingIdentity(tokenID);
                doneSignal.countDown();
            });
        }
        else{
            doneSignal.countDown();
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (mDeliveries != null) ? mDeliveries.size() : 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (position == AdapterView.INVALID_POSITION || mDeliveries == null || position >= mDeliveries.size()) {
            return null;
        }
        DeliveryEntity delivery = mDeliveries.get(position);
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

        // data
        remoteViews.setTextViewText(R.id.txt_origin_date, delivery.getOrigin().getShortDate());
        remoteViews.setTextViewText(R.id.txt_origin_info, delivery.getOrigin().getShorAddress());
        remoteViews.setTextViewText(R.id.txt_origin_time, delivery.getOrigin().getShortTime());
        remoteViews.setTextViewText(R.id.txt_destin_info, delivery.getDestin().getShorAddress());
        remoteViews.setTextViewText(R.id.txt_destin_time, delivery.getDestin().getShortTime());

        // click listener
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(DeliveryDetailsActivity.KEY_EXTRA_DELIVERY, delivery);
        remoteViews.setOnClickFillInIntent(R.id.txt_origin_date, fillInIntent);
        // TODO widget_list_item wont work

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return mDeliveries.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
