package ca.uqac.truckie.component;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ca.uqac.truckie.R;
import ca.uqac.truckie.activities.MainActivity;
import ca.uqac.truckie.model.DeliveryEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import durdinapps.rxfirebase2.RxFirebaseRecyclerAdapter;

public class DeliveryAdapter extends RxFirebaseRecyclerAdapter<DeliveryAdapter.DeliveryAdapterViewHolder, DeliveryEntity> {
    private MainActivity mContext;
    private List<DeliveryEntity> mData;
    final private DeliveryAdapterOnClickHandler mClickHandler;

    public interface DeliveryAdapterOnClickHandler {
        void onClick(int position, DeliveryAdapterViewHolder adapterViewHolder);
    }

    public DeliveryAdapter(@NonNull MainActivity context, DeliveryAdapterOnClickHandler clickHandler) {
        super(DeliveryEntity.class);
        mContext = context;
        mClickHandler = clickHandler;
        mData = new ArrayList<>();
    }

    @Override
    protected void itemAdded(DeliveryEntity delivery, String key, int position) {
        mData.add(delivery);
        Collections.sort(mData, new ReorderTaskComparator());
        notifyDataSetChanged();
    }

    @Override
    protected void itemChanged(DeliveryEntity oldDelivery, DeliveryEntity newDelivery, String key, int position) {
        boolean isUpdated = Collections.replaceAll(mData, oldDelivery, newDelivery);
        if(isUpdated) {
            Collections.sort(mData, new ReorderTaskComparator());
            notifyDataSetChanged();
        }

    }

    @Override
    protected void itemRemoved(DeliveryEntity delivery, String key, int position) {
        mData.remove(delivery);
        notifyDataSetChanged();
    }

    @Override
    protected void itemMoved(DeliveryEntity delivery, String key, int oldPosition, int newPosition) {
        // ignore
    }

    @NonNull
    @Override
    public DeliveryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_main_delivery, viewGroup, false);
        view.setFocusable(true);
        return new DeliveryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryAdapterViewHolder viewHolder, int position) {
        DeliveryEntity delivery = mData.get(position);
        viewHolder.mTxtOriginDate.setText(delivery.getOrigin().getShortDate());
        viewHolder.mTxtOriginInfo.setText(delivery.getOrigin().getShorAddress());
        viewHolder.mTxtOriginTime.setText(delivery.getOrigin().getShortTime());
        viewHolder.mTxtDestinInfo.setText(delivery.getDestin().getShorAddress());
        viewHolder.mTxtDestinTime.setText(delivery.getDestin().getShortTime());
        viewHolder.mColoredBar.setBackgroundColor(mContext.getResources().getColor(delivery.isMine() ? R.color.colorAccent : R.color.colorPrimary));
    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;
        return mData.size();
    }

    public List<DeliveryEntity> getData(){
        return mData;
    }

    public DeliveryEntity getItem(int position){
        return mData.get(position);
    }

    public class DeliveryAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.txt_origin_date) TextView mTxtOriginDate;
        @BindView(R.id.txt_origin_info) TextView mTxtOriginInfo;
        @BindView(R.id.txt_origin_time) TextView mTxtOriginTime;
        @BindView(R.id.txt_destin_info) TextView mTxtDestinInfo;
        @BindView(R.id.txt_destin_time) TextView mTxtDestinTime;
        @BindView(R.id.colored_bar) View mColoredBar;

        DeliveryAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickHandler.onClick(position, this);
        }
    }

    public class ReorderTaskComparator implements Comparator<DeliveryEntity>{
        public int compare(DeliveryEntity one, DeliveryEntity two) {
            return (int) (one.getOrigin().getTimestamp() - two.getOrigin().getTimestamp());
        }
    }
}