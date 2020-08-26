package com.flashtr.model;

import java.io.Serializable;

/**
 * Created by 24B on 5/9/2016.
 */
public class MemberDetail implements Serializable {

    private String member_id;
    private String member_name;
    private String member_image;
    private String member_email;
    private String member_number;
    private int isadmin;

    public MemberDetail(){
        member_id = "";
        member_name = "";
        member_image = "";
        member_number = "";
        member_email = "";
        isadmin = 0;
    }

    public String getMember_email() {
        return member_email;
    }

    public void setMember_email(String member_email) {
        this.member_email = member_email;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getMember_image() {
        return member_image;
    }

    public void setMember_image(String member_image) {
        this.member_image = member_image;
    }

    public String getMember_number() {
        return member_number;
    }

    public void setMember_number(String member_number) {
        this.member_number = member_number;
    }

    public int getIsadmin() {
        return isadmin;
    }

    public void setIsadmin(int isadmin) {
        this.isadmin = isadmin;
    }


}
