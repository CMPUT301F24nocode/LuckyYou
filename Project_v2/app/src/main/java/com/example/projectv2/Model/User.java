package com.example.projectv2.Model;

public class User {
    private boolean isAdmin;
    private boolean adminNotif;
    private String email;
    private String name;
    private boolean isOrganizer;
    private boolean organizerNotif;
    private int phoneNumber;
    private String profileImage;

    //partial constructor that defaults to isAdmin=false and callls the full constructor, call this partical constructor if the user is not intended to be an admin
    public User(boolean adminNotif, String email,String name, boolean isOrganizer, boolean organizerNotif,int phoneNumber){
        this(false,adminNotif,email,name,isOrganizer,organizerNotif,phoneNumber);
    }

    // Constructor
    public User(boolean isAdmin, boolean adminNotif, String email, String name, boolean isOrganizer,boolean organizerNotif, int phoneNumber) {
        this.isAdmin = isAdmin;
        this.adminNotif = adminNotif;
        this.email = email;
        this.name = name;
        this.isOrganizer = isOrganizer;
        this.organizerNotif = organizerNotif;
        this.phoneNumber = phoneNumber;
    }

    // Getters
    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isAdminNotif() {
        return adminNotif;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public boolean isOrganizer() {
        return isOrganizer;
    }

    public boolean isOrganizerNotif() {
        return organizerNotif;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }
    public String getProfileImage(){
        return profileImage;
    }

    // Setters
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setAdminNotif(boolean adminNotif) {
        this.adminNotif = adminNotif;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrganizer(boolean organizer) {
        isOrganizer = organizer;
    }

    public void setOrganizerNotif(boolean organizerNotif) {
        this.organizerNotif = organizerNotif;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setProfileImage(String imageLink){
        this.profileImage=imageLink;

    }
}