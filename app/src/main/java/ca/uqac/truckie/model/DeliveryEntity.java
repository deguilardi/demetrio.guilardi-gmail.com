package ca.uqac.truckie.model;

import android.annotation.SuppressLint;
import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.format.DateFormat;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;
import ca.uqac.truckie.MyUser;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class DeliveryEntity implements Parcelable {

    private Long id;
    private String userID;
    private MyAddress origin;
    private MyAddress destin;
    private HashMap<String,Bid> bids;
    private Bid currentBid;
    private Boolean auctionEnded;
    private Long timestamp;

    public DeliveryEntity() {
        // Default constructor required for calls to DataSnapshot.getValue(Delivery.class)
    }

    public DeliveryEntity(MyAddress origin, MyAddress destin) {
        this.origin = origin;
        this.destin = destin;
        this.timestamp = System.currentTimeMillis()/1000;
    }

    protected DeliveryEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        userID = in.readString();
        origin = in.readParcelable(MyAddress.class.getClassLoader());
        destin = in.readParcelable(MyAddress.class.getClassLoader());
        currentBid = in.readParcelable(Bid.class.getClassLoader());
        byte tmpAuctionEnded = in.readByte();
        auctionEnded = tmpAuctionEnded == 0 ? null : tmpAuctionEnded == 1;
        if (in.readByte() == 0) {
            timestamp = null;
        } else {
            timestamp = in.readLong();
        }
        int bidsSize = in.readInt();
        bids = new HashMap<>();
        for(int i = 0; i < bidsSize; i ++){
            Bid bid = new Bid();
            bid.setUserID(in.readString());
            bid.setValue(in.readLong());
            bids.put(bid.getUserID(), bid);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(userID);
        dest.writeParcelable(origin, flags);
        dest.writeParcelable(destin, flags);
        dest.writeParcelable(currentBid, flags);
        dest.writeByte((byte) (auctionEnded == null ? 0 : auctionEnded ? 1 : 2));
        if (timestamp == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(timestamp);
        }
        if(bids != null) {
            dest.writeInt(bids.size());
            for (HashMap.Entry<String, Bid> entry : bids.entrySet()) {
                Bid bid = entry.getValue();
                dest.writeString(bid.userID);
                dest.writeLong(bid.value);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DeliveryEntity> CREATOR = new Creator<DeliveryEntity>() {
        @Override
        public DeliveryEntity createFromParcel(Parcel in) {
            return new DeliveryEntity(in);
        }

        @Override
        public DeliveryEntity[] newArray(int size) {
            return new DeliveryEntity[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        return (getClass() == obj.getClass() && id.equals(((DeliveryEntity) obj).getId()));
    }

    @Exclude
    public boolean isCurrentBidMine(){
        return currentBid != null && currentBid.userID.equals(MyUser.getFBUid());
    }

    @Exclude
    public boolean isMine(){
        return userID.equals(MyUser.getFBUid());
    }

    @Exclude
    public boolean isEnded(){
        return auctionEnded != null && auctionEnded;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MyAddress getOrigin() {
        return origin;
    }

    public void setOrigin(MyAddress origin) {
        this.origin = origin;
    }

    public MyAddress getDestin() {
        return destin;
    }

    public void setDestin(MyAddress destin) {
        this.destin = destin;
    }

    public HashMap<String, Bid> getBids() {
        return bids;
    }

    public void setBids(HashMap<String, Bid> bids) {
        this.bids = bids;
    }

    @Nullable
    public Bid getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(Bid currentBid) {
        this.currentBid = currentBid;
    }

    public Boolean getAuctionEnded() {
        return auctionEnded;
    }

    public void setAuctionEnded(Boolean auctionEnded) {
        this.auctionEnded = auctionEnded;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public static class Bid implements Parcelable{
        private String userID;
        private Long value;

        @SuppressWarnings("unused")
        public Bid(){
            // Default constructor required for calls to DataSnapshot.getValue(Bid.class)
        }

        public Bid(String userID, long value) {
            this.userID = userID;
            this.value = value;
        }

        protected Bid(Parcel in) {
            userID = in.readString();
            value = in.readLong();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(userID);
            dest.writeLong(value);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Bid> CREATOR = new Creator<Bid>() {
            @Override
            public Bid createFromParcel(Parcel in) {
                return new Bid(in);
            }

            @Override
            public Bid[] newArray(int size) {
                return new Bid[size];
            }
        };

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }
    }

    public static class MyAddress implements Parcelable {
        private String featureName;
        private int maxAddressLineIndex = -1;
        private String adminArea;
        private String subAdminArea;
        private String locality;
        private String subLocality;
        private String thoroughfare;
        private String subThoroughfare;
        private String premises;
        private String postalCode;
        private String countryCode;
        private String countryName;
        private double latitude;
        private double longitude;
        private boolean hasLatitude = false;
        private boolean hasLongitude = false;
        private String phone;
        private String url;
        private String fullAddress;
        private long timestamp;
        private String extraInfo;

        @SuppressWarnings("unused")
        public MyAddress() {
            // Default constructor required for calls to DataSnapshot.getValue(AddressEntity.class)
        }

        @SuppressLint("UseSparseArrays")
        public MyAddress(Address address, String fullAddress, Calendar date, String extraInfo) {
            this.featureName = address.getFeatureName();
            this.maxAddressLineIndex = address.getMaxAddressLineIndex();
            this.adminArea = address.getAdminArea();
            this.subAdminArea = address.getSubAdminArea();
            this.locality = address.getLocality();
            this.subLocality = address.getSubLocality();
            this.thoroughfare = address.getThoroughfare();
            this.subThoroughfare = address.getSubThoroughfare();
            this.premises = address.getPremises();
            this.postalCode = address.getPostalCode();
            this.countryCode = address.getCountryCode();
            this.countryName = address.getCountryName();
            this.latitude = address.getLatitude();
            this.longitude = address.getLongitude();
            this.hasLatitude = address.hasLatitude();
            this.hasLongitude = address.hasLongitude();
            this.phone = address.getPhone();
            this.url = address.getUrl();

            this.fullAddress = fullAddress;
            this.timestamp = date.getTime().getTime() / 1000;
            this.extraInfo = extraInfo;
        }

        protected MyAddress(Parcel in) {
            featureName = in.readString();
            maxAddressLineIndex = in.readInt();
            adminArea = in.readString();
            subAdminArea = in.readString();
            locality = in.readString();
            subLocality = in.readString();
            thoroughfare = in.readString();
            subThoroughfare = in.readString();
            premises = in.readString();
            postalCode = in.readString();
            countryCode = in.readString();
            countryName = in.readString();
            latitude = in.readDouble();
            longitude = in.readDouble();
            hasLatitude = in.readByte() != 0;
            hasLongitude = in.readByte() != 0;
            phone = in.readString();
            url = in.readString();
            fullAddress = in.readString();
            timestamp = in.readLong();
            extraInfo = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(featureName);
            dest.writeInt(maxAddressLineIndex);
            dest.writeString(adminArea);
            dest.writeString(subAdminArea);
            dest.writeString(locality);
            dest.writeString(subLocality);
            dest.writeString(thoroughfare);
            dest.writeString(subThoroughfare);
            dest.writeString(premises);
            dest.writeString(postalCode);
            dest.writeString(countryCode);
            dest.writeString(countryName);
            dest.writeDouble(latitude);
            dest.writeDouble(longitude);
            dest.writeByte((byte) (hasLatitude ? 1 : 0));
            dest.writeByte((byte) (hasLongitude ? 1 : 0));
            dest.writeString(phone);
            dest.writeString(url);
            dest.writeString(fullAddress);
            dest.writeLong(timestamp);
            dest.writeString(extraInfo);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<MyAddress> CREATOR = new Creator<MyAddress>() {
            @Override
            public MyAddress createFromParcel(Parcel in) {
                return new MyAddress(in);
            }

            @Override
            public MyAddress[] newArray(int size) {
                return new MyAddress[size];
            }
        };

        @Exclude
        public String getShortDate() {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(timestamp * 1000);
            return DateFormat.format("dd/MM", cal).toString();
        }

        @Exclude
        public String getShortTime() {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(timestamp * 1000);
            return "@" + DateFormat.format("hh:mm a", cal).toString();
        }

        @Exclude
        public String getShorAddress() {
            return !TextUtils.isEmpty(thoroughfare) ? thoroughfare :
                    !TextUtils.isEmpty(subAdminArea) ? subAdminArea : featureName;
        }

        @Exclude
        public String getMediumAddress() {
            return (!TextUtils.isEmpty(thoroughfare) ? thoroughfare + ", " : "") +
                    (!TextUtils.isEmpty(subAdminArea) ? subAdminArea + ", " :
                            (!TextUtils.isEmpty(featureName) ? featureName + ", " : "")) +
                    (!TextUtils.isEmpty(adminArea) ? adminArea + ", " : "") + countryCode;
        }

        @SuppressWarnings("unused")
        public String getFeatureName() {
            return featureName;
        }

        @SuppressWarnings("unused")
        public void setFeatureName(String featureName) {
            this.featureName = featureName;
        }

        @SuppressWarnings("unused")
        public int getMaxAddressLineIndex() {
            return maxAddressLineIndex;
        }

        @SuppressWarnings("unused")
        public void setMaxAddressLineIndex(int maxAddressLineIndex) {
            this.maxAddressLineIndex = maxAddressLineIndex;
        }

        @SuppressWarnings("unused")
        public String getAdminArea() {
            return adminArea;
        }

        @SuppressWarnings("unused")
        public void setAdminArea(String adminArea) {
            this.adminArea = adminArea;
        }

        @SuppressWarnings("unused")
        public String getSubAdminArea() {
            return subAdminArea;
        }

        @SuppressWarnings("unused")
        public void setSubAdminArea(String subAdminArea) {
            this.subAdminArea = subAdminArea;
        }

        @SuppressWarnings("unused")
        public String getLocality() {
            return locality;
        }

        @SuppressWarnings("unused")
        public void setLocality(String locality) {
            this.locality = locality;
        }

        @SuppressWarnings("unused")
        public String getSubLocality() {
            return subLocality;
        }

        @SuppressWarnings("unused")
        public void setSubLocality(String subLocality) {
            this.subLocality = subLocality;
        }

        @SuppressWarnings("unused")
        public String getThoroughfare() {
            return thoroughfare;
        }

        @SuppressWarnings("unused")
        public void setThoroughfare(String thoroughfare) {
            this.thoroughfare = thoroughfare;
        }

        @SuppressWarnings("unused")
        public String getSubThoroughfare() {
            return subThoroughfare;
        }

        @SuppressWarnings("unused")
        public void setSubThoroughfare(String subThoroughfare) {
            this.subThoroughfare = subThoroughfare;
        }

        @SuppressWarnings("unused")
        public String getPremises() {
            return premises;
        }

        @SuppressWarnings("unused")
        public void setPremises(String premises) {
            this.premises = premises;
        }

        @SuppressWarnings("unused")
        public String getPostalCode() {
            return postalCode;
        }

        @SuppressWarnings("unused")
        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        @SuppressWarnings("unused")
        public String getCountryCode() {
            return countryCode;
        }

        @SuppressWarnings("unused")
        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        @SuppressWarnings("unused")
        public String getCountryName() {
            return countryName;
        }

        @SuppressWarnings("unused")
        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

        @SuppressWarnings("unused")
        public double getLatitude() {
            return latitude;
        }

        @SuppressWarnings("unused")
        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        @SuppressWarnings("unused")
        public double getLongitude() {
            return longitude;
        }

        @SuppressWarnings("unused")
        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        @SuppressWarnings("unused")
        public boolean isHasLatitude() {
            return hasLatitude;
        }

        @SuppressWarnings("unused")
        public void setHasLatitude(boolean hasLatitude) {
            this.hasLatitude = hasLatitude;
        }

        @SuppressWarnings("unused")
        public boolean isHasLongitude() {
            return hasLongitude;
        }

        @SuppressWarnings("unused")
        public void setHasLongitude(boolean hasLongitude) {
            this.hasLongitude = hasLongitude;
        }

        @SuppressWarnings("unused")
        public String getPhone() {
            return phone;
        }

        @SuppressWarnings("unused")
        public void setPhone(String phone) {
            this.phone = phone;
        }

        @SuppressWarnings("unused")
        public String getUrl() {
            return url;
        }

        @SuppressWarnings("unused")
        public void setUrl(String url) {
            this.url = url;
        }

        @SuppressWarnings("unused")
        public long getTimestamp() {
            return timestamp;
        }

        @SuppressWarnings("unused")
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        @SuppressWarnings("unused")
        public String getExtraInfo() {
            return extraInfo;
        }

        @SuppressWarnings("unused")
        public void setExtraInfo(String extraInfo) {
            this.extraInfo = extraInfo;
        }

        @SuppressWarnings("unused")
        public String getFullAddress() {
            return fullAddress;
        }

        @SuppressWarnings("unused")
        public void setFullAddress(String fullAddress) {
            this.fullAddress = fullAddress;
        }
    }
}
