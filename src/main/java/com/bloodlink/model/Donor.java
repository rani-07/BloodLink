package com.bloodlink.model;

public class Donor {
    private int id;
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String bloodGroup;
    private int age;
    private String gender;
    private double weight;
    private String city;
    private String state;
    private String pinCode;
    private boolean available;
    private String lastDonation;
    private String nextEligible;
    private int totalDonations;
    private String createdAt;

    public int getId()                   { return id; }
    public void setId(int id)            { this.id = id; }
    public int getUserId()               { return userId; }
    public void setUserId(int v)         { this.userId = v; }
    public String getFirstName()         { return firstName; }
    public void setFirstName(String v)   { this.firstName = v; }
    public String getLastName()          { return lastName; }
    public void setLastName(String v)    { this.lastName = v; }
    public String getEmail()             { return email; }
    public void setEmail(String v)       { this.email = v; }
    public String getPhone()             { return phone; }
    public void setPhone(String v)       { this.phone = v; }
    public String getBloodGroup()        { return bloodGroup; }
    public void setBloodGroup(String v)  { this.bloodGroup = v; }
    public int getAge()                  { return age; }
    public void setAge(int v)            { this.age = v; }
    public String getGender()            { return gender; }
    public void setGender(String v)      { this.gender = v; }
    public double getWeight()            { return weight; }
    public void setWeight(double v)      { this.weight = v; }
    public String getCity()              { return city; }
    public void setCity(String v)        { this.city = v; }
    public String getState()             { return state; }
    public void setState(String v)       { this.state = v; }
    public String getPinCode()           { return pinCode; }
    public void setPinCode(String v)     { this.pinCode = v; }
    public boolean isAvailable()         { return available; }
    public void setAvailable(boolean v)  { this.available = v; }
    public String getLastDonation()      { return lastDonation; }
    public void setLastDonation(String v){ this.lastDonation = v; }
    public String getNextEligible()      { return nextEligible; }
    public void setNextEligible(String v){ this.nextEligible = v; }
    public int getTotalDonations()       { return totalDonations; }
    public void setTotalDonations(int v) { this.totalDonations = v; }
    public String getCreatedAt()         { return createdAt; }
    public void setCreatedAt(String v)   { this.createdAt = v; }
    public String getFullName()          { return firstName + " " + lastName; }
    public int getLivesSaved()           { return totalDonations * 3; }
}
