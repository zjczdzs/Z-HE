package com.Dao;

public class Flag {

    private String information;

    private String date;

    private String ip;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    @Override
    public String toString() {
        return "Flag{" +
                "information='" + information + '\'' +
                ", date='" + date + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
