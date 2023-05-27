package com.Dao;

public class Flag {

    private String information;

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
                '}';
    }
}
