package com.retro.emergencyqr.activities.qrReader.model;

public class ProfileData {

    private String name;
    private int age;
    private String address;
    //TODO datatype for location
    private String emergencyContactNumber;
    private String medicalProvider;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }

    public void setEmergencyContactNumber(String emergencyContactNumber) {
        this.emergencyContactNumber = emergencyContactNumber;
    }

    public String getMedicalProvider() {
        return medicalProvider;
    }

    public void setMedicalProvider(String medicalProvider) {
        this.medicalProvider = medicalProvider;
    }
}
