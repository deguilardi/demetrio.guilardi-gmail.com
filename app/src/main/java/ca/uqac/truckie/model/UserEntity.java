package ca.uqac.truckie.model;

import com.google.firebase.auth.FirebaseUser;

public class UserEntity {

    private String id;
    private String email;
    private Personal personal;
    private Address address;
    private Vehicle vehicle;

    @SuppressWarnings("unused")
    public UserEntity() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    private UserEntity(String id, String email, Personal personal, Address address, Vehicle vehicle){
        this.id = id;
        this.email = email;
        this.personal = personal;
        this.address = address;
        this.vehicle = vehicle;
    }

    public UserEntity(FirebaseUser firebaseUser, Personal personal, Address address, Vehicle vehicle){
        this(firebaseUser.getUid(), firebaseUser.getEmail(), personal, address, vehicle);
    }

    @SuppressWarnings("unused")
    public String getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public String getEmail() {
        return email;
    }

    @SuppressWarnings("unused")
    public void setEmail(String email) {
        this.email = email;
    }

    @SuppressWarnings("unused")
    public Personal getPersonal() {
        return personal;
    }

    @SuppressWarnings("unused")
    public void setPersonal(Personal personal) {
        this.personal = personal;
    }

    @SuppressWarnings("unused")
    public Address getAddress() {
        return address;
    }

    @SuppressWarnings("unused")
    public void setAddress(Address address) {
        this.address = address;
    }

    @SuppressWarnings("unused")
    public Vehicle getVehicle() {
        return vehicle;
    }

    @SuppressWarnings("unused")
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public static class Personal{
        private String name;
        private Long phone1;
        private Long phone2;
        private String driverLicense;

        public Personal(String name, Long phone1, Long phone2, String driverLicense){
            this.name = name;
            this.phone1 = phone1;
            this.phone2 = phone2;
            this.driverLicense = driverLicense;
        }

        @SuppressWarnings("unused")
        public String getName() {
            return name;
        }

        @SuppressWarnings("unused")
        public void setName(String name) {
            this.name = name;
        }

        @SuppressWarnings("unused")
        public Long getPhone1() {
            return phone1;
        }

        @SuppressWarnings("unused")
        public void setPhone1(Long phone1) {
            this.phone1 = phone1;
        }

        @SuppressWarnings("unused")
        public Long getPhone2() {
            return phone2;
        }

        @SuppressWarnings("unused")
        public void setPhone2(Long phone2) {
            this.phone2 = phone2;
        }

        @SuppressWarnings("unused")
        public String getDriverLicense() {
            return driverLicense;
        }

        @SuppressWarnings("unused")
        public void setDriverLicense(String driverLicense) {
            this.driverLicense = driverLicense;
        }
    }

    public static class Address{
        private Long country;
        private Long state;
        private String city;
        private String address;
        private Long zipCode;

        public Address(Long country, Long state, String city, String address, Long zipCode){
            this.country = country;
            this.state = state;
            this.city = city;
            this.address = address;
            this.zipCode = zipCode;
        }

        @SuppressWarnings("unused")
        public Long getCountry() {
            return country;
        }

        @SuppressWarnings("unused")
        public void setCountry(Long country) {
            this.country = country;
        }

        @SuppressWarnings("unused")
        public Long getState() {
            return state;
        }

        @SuppressWarnings("unused")
        public void setState(Long state) {
            this.state = state;
        }

        @SuppressWarnings("unused")
        public String getCity() {
            return city;
        }

        @SuppressWarnings("unused")
        public void setCity(String city) {
            this.city = city;
        }

        @SuppressWarnings("unused")
        public String getAddress() {
            return address;
        }

        @SuppressWarnings("unused")
        public void setAddress(String address) {
            this.address = address;
        }

        @SuppressWarnings("unused")
        public Long getZipCode() {
            return zipCode;
        }

        @SuppressWarnings("unused")
        public void setZipCode(Long zipCode) {
            this.zipCode = zipCode;
        }
    }

    public static class Vehicle{
        private Long type1;
        private Long type2;
        private Long size;
        private Long capacity;

        public Vehicle(Long type1, Long type2, Long size, Long capacity){
            this.type1 = type1;
            this.type2 = type2;
            this.size = size;
            this.capacity = capacity;
        }

        @SuppressWarnings("unused")
        public Long getType1() {
            return type1;
        }

        @SuppressWarnings("unused")
        public void setType1(Long type1) {
            this.type1 = type1;
        }

        @SuppressWarnings("unused")
        public Long getType2() {
            return type2;
        }

        @SuppressWarnings("unused")
        public void setType2(Long type2) {
            this.type2 = type2;
        }

        @SuppressWarnings("unused")
        public Long getSize() {
            return size;
        }

        @SuppressWarnings("unused")
        public void setSize(Long size) {
            this.size = size;
        }

        @SuppressWarnings("unused")
        public Long getCapacity() {
            return capacity;
        }

        @SuppressWarnings("unused")
        public void setCapacity(Long capacity) {
            this.capacity = capacity;
        }
    }

    public static class Bid{
        private long deliveryID;
        private Long value;
        Boolean winning;

        public Bid(long deliveryID, Long value, boolean winning) {
            this.deliveryID = deliveryID;
            this.value = value;
            this.winning = winning;
        }

        @SuppressWarnings("unused")
        public long getDeliveryID() {
            return deliveryID;
        }

        @SuppressWarnings("unused")
        public void setDeliveryID(long deliveryID) {
            this.deliveryID = deliveryID;
        }

        @SuppressWarnings("unused")
        public Long getValue() {
            return value;
        }

        @SuppressWarnings("unused")
        public void setValue(Long value) {
            this.value = value;
        }

        @SuppressWarnings("unused")
        public Boolean isWinning() {
            return winning;
        }

        @SuppressWarnings("unused")
        public void setWinning(Boolean winning) {
            this.winning = winning;
        }
    }
}
