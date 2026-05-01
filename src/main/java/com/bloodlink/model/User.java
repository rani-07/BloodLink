package com.bloodlink.model;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String role;
    private String createdAt;

    public int getId()                 { return id; }
    public void setId(int id)          { this.id = id; }
    public String getFirstName()       { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }
    public String getLastName()        { return lastName; }
    public void setLastName(String v)  { this.lastName = v; }
    public String getEmail()           { return email; }
    public void setEmail(String v)     { this.email = v; }
    public String getPassword()        { return password; }
    public void setPassword(String v)  { this.password = v; }
    public String getPhone()           { return phone; }
    public void setPhone(String v)     { this.phone = v; }
    public String getRole()            { return role; }
    public void setRole(String v)      { this.role = v; }
    public String getCreatedAt()       { return createdAt; }
    public void setCreatedAt(String v) { this.createdAt = v; }
    public String getFullName()        { return firstName + " " + lastName; }
}
