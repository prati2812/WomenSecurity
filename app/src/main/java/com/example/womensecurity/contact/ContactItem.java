package com.example.womensecurity.contact;

import java.io.Serializable;

public class ContactItem implements Serializable {

    private boolean isSelected;

    private String contact_name;
    private String contact_no;


    public ContactItem() {
    }

    public ContactItem( String contact_name, String contact_no ,boolean isSelected) {
        this.isSelected = isSelected;
        this.contact_name = contact_name;
        this.contact_no = contact_no;
    }

    public ContactItem(String contact_name, String contact_no) {
        this.contact_name = contact_name;
        this.contact_no = contact_no;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
