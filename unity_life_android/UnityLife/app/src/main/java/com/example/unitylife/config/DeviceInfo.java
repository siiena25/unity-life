package com.example.unitylife.config;

public class DeviceInfo {
    private String appVersion;
    private String osVersion;
    private String osType;
    private String deviceId;
    private String manufacturer;
    private String deviceModel;

    private static final DeviceInfo ourInstance = new DeviceInfo();

    public static DeviceInfo getInstance() {
        return ourInstance;
    }

    private DeviceInfo() {
    }

    public String getAppVersion() {
        return appVersion;
    }

    public DeviceInfo setAppVersion(String appVersion) {
        this.appVersion = appVersion;
        return ourInstance;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public DeviceInfo setOsVersion(String osVersion) {
        this.osVersion = osVersion;
        return ourInstance;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public DeviceInfo setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
        return ourInstance;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public DeviceInfo setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
        return ourInstance;
    }

    public String getOsType() {
        return osType;
    }

    public DeviceInfo setOsType(String osType) {
        this.osType = osType;
        return ourInstance;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
