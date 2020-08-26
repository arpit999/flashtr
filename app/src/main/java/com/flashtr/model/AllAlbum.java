package com.flashtr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 24B on 5/7/2016.
 */
public class AllAlbum implements Serializable {
    private String album_id;
    private String album_name;
    private String create_date;
    private String modify_date;
    private String album_cover_thumb_image;
    private String album_cover_image;
    private String admin_id;
    private String admin_name;
    private String admin_mobileno;
    private String admin_dp;
    private String photo_count;
    private int shared_with_me;
    private int created_by_me;
    private String count;
    private int member_count;
    private List<MemberDetail> member_details;

    public AllAlbum() {
        album_id = "";
        album_name = "";
        create_date = "";
        modify_date = "";
        album_cover_thumb_image = "";
        album_cover_image = "";
        admin_id = "";
        admin_name = "";
        admin_mobileno = "";
        admin_dp = "";
        photo_count = "";
        shared_with_me = 0;
        created_by_me = 0;
        count = "0";
        member_count = 0;
        member_details = new ArrayList<MemberDetail>();
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getModify_date() {
        return modify_date;
    }

    public void setModify_date(String modify_date) {
        this.modify_date = modify_date;
    }

    public String getAlbum_cover_thumb_image() {
        return album_cover_thumb_image;
    }

    public void setAlbum_cover_thumb_image(String album_cover_thumb_image) {
        this.album_cover_thumb_image = album_cover_thumb_image;
    }

    public String getAlbum_cover_image() {
        return album_cover_image;
    }

    public void setAlbum_cover_image(String album_cover_image) {
        this.album_cover_image = album_cover_image;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }

    public String getAdmin_name() {
        return admin_name;
    }

    public void setAdmin_name(String admin_name) {
        this.admin_name = admin_name;
    }

    public String getAdmin_mobileno() {
        return admin_mobileno;
    }

    public void setAdmin_mobileno(String admin_mobileno) {
        this.admin_mobileno = admin_mobileno;
    }

    public String getAdmin_dp() {
        return admin_dp;
    }

    public void setAdmin_dp(String admin_dp) {
        this.admin_dp = admin_dp;
    }

    public String getPhoto_count() {
        return photo_count;
    }

    public void setPhoto_count(String photo_count) {
        this.photo_count = photo_count;
    }

    public int getShared_with_me() {
        return shared_with_me;
    }

    public void setShared_with_me(int shared_with_me) {
        this.shared_with_me = shared_with_me;
    }

    public int getCreated_by_me() {
        return created_by_me;
    }

    public void setCreated_by_me(int created_by_me) {
        this.created_by_me = created_by_me;
    }

    public int getMember_count() {
        return member_count;
    }

    public void setMember_count(int member_count) {
        this.member_count = member_count;
    }

    public List<MemberDetail> getMember_details() {
        return member_details;
    }

    public void setMember_details(List<MemberDetail> member_details) {
        this.member_details = member_details;
    }
}
