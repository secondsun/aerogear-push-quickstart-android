package org.jboss.aerogear.android.unifiedpush.quickstart;

import java.io.Serializable;

public class Lead implements Serializable {

    private Long id;
    private String name;
    private String location;
    private String phoneNumber;
    private String saleAgent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSaleAgent() {
        return saleAgent;
    }

    public void setSaleAgent(String saleAgent) {
        this.saleAgent = saleAgent;
    }

    @Override
    public String toString() {
        return name;
    }

}
