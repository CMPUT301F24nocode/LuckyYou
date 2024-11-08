package com.example.projectv2.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class User {
    private boolean isAdmin;
    private boolean adminNotif;
    private String email;
    private String firstName;
    private String lastName;
    private boolean isOrganizer;
    private boolean organizerNotif;
    private long phoneNumber;
    private String profileImage;
    private String deviceID;
    private ArrayList<Map<String, Object>>  adminNotifList;
    private ArrayList<Map<String, Object>>  organizerNotifList;
    private String name;
    public User(){}

    //partial constructor that defaults to isAdmin=false and callls the full constructor, call this partial constructor if the user is not intended to be an admin
    public User( String email,String firstName,String lastName,long phoneNumber,String deviceID){
        this(false,true,email,firstName,lastName,false,true,phoneNumber,deviceID);
    }

    // Constructor
    public User(boolean isAdmin, boolean adminNotif, String email, String firstName,String lastName, boolean isOrganizer,boolean organizerNotif, long phoneNumber,String deviceID) {
        this.isAdmin = isAdmin;
        this.adminNotif = adminNotif;
        this.email = email;
        this.firstName = firstName;
        this.lastName=lastName;
        this.isOrganizer = isOrganizer;
        this.organizerNotif = organizerNotif;
        this.phoneNumber = phoneNumber;
        this.deviceID=deviceID;
        this.profileImage="";
        this.adminNotifList=new ArrayList<Map<String, Object>>();
        this.organizerNotifList=new ArrayList<Map<String, Object>>();
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

    public long getPhoneNumber() {
        return phoneNumber;
    }
    public String getProfileImage(){
        return profileImage;
    }
    public String getDeviceID(){
        return deviceID;
    }
    public ArrayList<Map<String, Object>> getAdminNotifList(){
        return adminNotifList;
    }
    public ArrayList<Map<String, Object>> getOrganizerNotifList(){
        return organizerNotifList;}

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
        this.name=name;
    }

    public void setOrganizer(boolean organizer) {
        isOrganizer = organizer;
    }

    public void setOrganizerNotif(boolean organizerNotif) {
        this.organizerNotif = organizerNotif;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setProfileImage(String imageLink){
        this.profileImage=imageLink;

    }
    public void setDeviceID(String deviceID){
        this.deviceID=deviceID;
    }
    public void setAdminNotifList(ArrayList<Map<String, Object>> adminNotifList){
        this.adminNotifList=adminNotifList;
    }
public void setOrganizerNotifList(ArrayList<Map<String, Object>> organizerNotifList){
        this.organizerNotifList=organizerNotifList;
}
}