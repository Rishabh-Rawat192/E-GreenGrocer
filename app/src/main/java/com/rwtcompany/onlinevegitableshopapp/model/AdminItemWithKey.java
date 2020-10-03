package com.rwtcompany.onlinevegitableshopapp.model;

public class AdminItemWithKey {
    private AdminItem adminItem;
    private String key;

    public AdminItemWithKey(AdminItem adminItem, String key) {
        this.adminItem = adminItem;
        this.key = key;
    }

    public AdminItem getAdminItem() {
        return adminItem;
    }

    public void setAdminItem(AdminItem adminItem) {
        this.adminItem = adminItem;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
