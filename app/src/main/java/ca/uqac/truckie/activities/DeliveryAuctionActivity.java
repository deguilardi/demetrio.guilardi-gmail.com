package ca.uqac.truckie.activities;

import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import ca.uqac.truckie.MyUser;
import ca.uqac.truckie.R;
import ca.uqac.truckie.model.DB;
import ca.uqac.truckie.model.DeliveryEntity;
import ca.uqac.truckie.model.UserEntity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeliveryAuctionActivity extends DeliveryDetailsActivity
        implements View.OnClickListener, DialogInterface.OnClickListener {

    @BindView(R.id.main_content) View mMainContent;
    @BindView(R.id.txt_my_delivery_title) TextView mTxtMyDeliveryTitle;
    @BindView(R.id.txt_current_bid) TextView mTxtCurrentBid;
    @BindView(R.id.btn_bid) AppCompatButton mBtnBid;
    AppCompatEditText mMyBidInput;

    @Override
    protected void loadView(){
        setContentView(R.layout.activity_delivery_auction);
        ButterKnife.bind(this);
        setupUI();
        super.mIsAuction = true;
        super.setupMap();
    }

    private void setupUI(){
        if(mDelivery.isMine()){
            mTxtMyDeliveryTitle.setVisibility(View.VISIBLE);
            mBtnBid.setText(R.string.accept);
            mTxtCurrentBid.setText(getString(R.string.no_bids));
            if(mDelivery.getCurrentBid() != null) {
                mBtnBid.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
            else{
                disableBidBtn();
            }
        }

        if (mDelivery.getCurrentBid() != null) {
            if(mDelivery.getCurrentBid().getUserID().equals(MyUser.getFBUid())){
                userHasCurrentBid(mDelivery.getCurrentBid().getValue());
            }
            else{
                mTxtCurrentBid.setText(getString(R.string.current_bid, String.valueOf(mDelivery.getCurrentBid().getValue())));
            }
        }

        DeliveryEntity.MyAddress origin = mDelivery.getOrigin();
        mTxtOriginDate.setText(String.format("%s %s", origin.getShortDate(), origin.getShortTime()));
        mTxtOriginAddress.setText(origin.getMediumAddress());
        mTxtOriginExtraInfo.setVisibility(View.GONE);

        DeliveryEntity.MyAddress destin = mDelivery.getDestin();
        mTxtDestinDate.setText(String.format("%s %s", destin.getShortDate(), destin.getShortTime()));
        mTxtDestinAddress.setText(destin.getMediumAddress());
        mTxtDestinExtraInfo.setVisibility(View.GONE);

        mBtnBid.setOnClickListener(this);
    }

    private void showBidDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View viewInflated = getLayoutInflater().inflate(R.layout.popup_bid, (ViewGroup) mMainContent, false);
        mMyBidInput = viewInflated.findViewById(R.id.txt_bid_input);
        builder.setView(viewInflated);
        builder.setPositiveButton(R.string.bid, this);
        builder.setNegativeButton(android.R.string.cancel, this);
        final AlertDialog popup = builder.create();
        final DeliveryAuctionActivity self = this;
        popup.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button buttonPosition = popup.getButton(AlertDialog.BUTTON_POSITIVE);
                buttonPosition.setOnClickListener(self);
            }
        });
        builder.show();
    }

    private void userHasCurrentBid(long bidNumericValue){
        mTxtCurrentBid.setText(getString(R.string.your_current_bid, String.valueOf(bidNumericValue)));
        disableBidBtn();
    }

    private void disableBidBtn(){
        mBtnBid.setAlpha(0.4f);
        mBtnBid.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_bid:
                if(mDelivery.isMine()) {
                    DB.getInstance().acceptBid(mDelivery, onComplete -> onBackPressed());
                }
                else{
                    showBidDialog();
                }

                break;
        }
    }

    @Override
    public void onClick(final DialogInterface dialog, int which) {
        switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                final String bidStringValue = Objects.requireNonNull(mMyBidInput.getText()).toString();
                final Long bidNumericValue = Long.valueOf(bidStringValue);
                long currentBidNumericValue = 0;
                if (mDelivery.getCurrentBid() != null) {
                    currentBidNumericValue = mDelivery.getCurrentBid().getValue();
                }
                if (bidNumericValue > currentBidNumericValue){
                    final DeliveryEntity.Bid deliveryBid = new DeliveryEntity.Bid(MyUser.getFBUid(), bidNumericValue);
                    DB.getInstance().saveBid(mDelivery,
                            deliveryBid,
                            new UserEntity.Bid(mDelivery.getId(), bidNumericValue, true),
                            onComplete -> {
                                mDelivery.setCurrentBid(deliveryBid);
                                userHasCurrentBid(bidNumericValue);
                                dialog.dismiss();
                            });
                }
                else{
                    mMyBidInput.setError(getString(R.string.bid_too_low, currentBidNumericValue));
                    dialog.cancel();
                }
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                dialog.cancel();
                break;
        }
    }
}