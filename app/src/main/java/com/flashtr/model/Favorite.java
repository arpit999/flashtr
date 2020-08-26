package com.flashtr.model;

/**
 * Created by 24B on 5/7/2016.
 */
public class Favorite {
    private String album_id;
    private String album_name;


    private String user_id;

    private String cover_image;
    private String photo_id;
    private String photo;
    private String favourite_photo_id;
    private String thumb_image;
    private String date;
    private String shared;
    private String isliked;
    private String isloved;
    private String ishaha;
    private String iswow;
    private String isangry;
    private String issad;
    private String user_name;
    private String user_image;
    private String modify_date;

    private int photo_status;
    private int favourite_photo;
    private int like_performed;
    private int total_likes;

    public Favorite(){
        user_id = "";
        album_id = "";
        album_name = "";
        cover_image = "";
        photo_id = "";
        photo = "";
        favourite_photo_id = "";
        thumb_image = "";
        date = "";
        modify_date="";

        shared = "";
        isliked = "";
        isloved = "";
        ishaha = "";
        iswow = "";
        isangry = "";
        issad = "";
        user_name = "";
        user_image = "";

        photo_status = 0;
        favourite_photo = 0;
        like_performed = 0;
        total_likes = 0;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFavourite_photo_id() {
        return favourite_photo_id;
    }

    public void setFavourite_photo_id(String favourite_photo_id) {
        this.favourite_photo_id = favourite_photo_id;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShared() {
        return shared;
    }

    public void setShared(String shared) {
        this.shared = shared;
    }

    public String getIsliked() {
        return isliked;
    }

    public void setIsliked(String isliked) {
        this.isliked = isliked;
    }

    public String getIsloved() {
        return isloved;
    }

    public void setIsloved(String isloved) {
        this.isloved = isloved;
    }

    public String getIshaha() {
        return ishaha;
    }

    public void setIshaha(String ishaha) {
        this.ishaha = ishaha;
    }

    public String getIswow() {
        return iswow;
    }

    public void setIswow(String iswow) {
        this.iswow = iswow;
    }

    public String getIsangry() {
        return isangry;
    }

    public void setIsangry(String isangry) {
        this.isangry = isangry;
    }

    public String getIssad() {
        return issad;
    }

    public void setIssad(String issad) {
        this.issad = issad;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public int getPhoto_status() {
        return photo_status;
    }

    public void setPhoto_status(int photo_status) {
        this.photo_status = photo_status;
    }

    public int getFavourite_photo() {
        return favourite_photo;
    }

    public void setFavourite_photo(int favourite_photo) {
        this.favourite_photo = favourite_photo;
    }

    public int getLike_performed() {
        return like_performed;
    }

    public void setLike_performed(int like_performed) {
        this.like_performed = like_performed;
    }

    public int getTotal_likes() {
        return total_likes;
    }

    public void setTotal_likes(int total_likes) {
        this.total_likes = total_likes;
    }

    public String getModify_date() {
        return modify_date;
    }

    public void setModify_date(String modify_date) {
        this.modify_date = modify_date;
    }
}
