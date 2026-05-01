package com.bloodlink.model;

public class BloodRequest {
    private int id;
    private int requesterId;
    private String bloodGroup;
    private String hospitalName;
    private String location;
    private String contactPhone;
    private int unitsNeeded;
    private String urgency;
    private String notes;
    private String status;
    private String createdAt;

    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }
    public int getRequesterId()             { return requesterId; }
    public void setRequesterId(int v)       { this.requesterId = v; }
    public String getBloodGroup()           { return bloodGroup; }
    public void setBloodGroup(String v)     { this.bloodGroup = v; }
    public String getHospitalName()         { return hospitalName; }
    public void setHospitalName(String v)   { this.hospitalName = v; }
    public String getLocation()             { return location; }
    public void setLocation(String v)       { this.location = v; }
    public String getContactPhone()         { return contactPhone; }
    public void setContactPhone(String v)   { this.contactPhone = v; }
    public int getUnitsNeeded()             { return unitsNeeded; }
    public void setUnitsNeeded(int v)       { this.unitsNeeded = v; }
    public String getUrgency()              { return urgency; }
    public void setUrgency(String v)        { this.urgency = v; }
    public String getNotes()                { return notes; }
    public void setNotes(String v)          { this.notes = v; }
    public String getStatus()               { return status; }
    public void setStatus(String v)         { this.status = v; }
    public String getCreatedAt()            { return createdAt; }
    public void setCreatedAt(String v)      { this.createdAt = v; }
}
