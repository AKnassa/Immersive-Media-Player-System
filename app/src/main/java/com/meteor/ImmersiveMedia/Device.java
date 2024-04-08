package com.meteor.ImmersiveMedia;

public class Device {
    private String name;
    private int batteryPercentage;
    private boolean isWifiOn;
    private boolean isSelected;
    private boolean isCalibrated;

    // Constructor
    public Device(String name, int batteryPercentage, boolean isWifiOn, boolean isCalibrated) {
        this.name = name;
        this.batteryPercentage = batteryPercentage;
        this.isWifiOn = isWifiOn;
        this.isCalibrated = isCalibrated;
        this.isSelected = false;
    }

    // Getters and Setters for the fields

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(int batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public boolean isWifiOn() {
        return isWifiOn;
    }

    public void setWifiOn(boolean wifiOn) {
        isWifiOn = wifiOn;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isCalibrated() {
        return isCalibrated;
    }

    public void setCalibrated(boolean calibrated) {
        isCalibrated = calibrated;
    }



    // Method to convert device details to a CSV string
    public String toCsvString() {
        return name + "," + batteryPercentage + "," + isWifiOn + "," + isCalibrated;
    }
}
