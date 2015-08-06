package com.astinx.cameramanager;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Mauro on 28/01/2015.
 */
public class Media implements Serializable {
    private String url;
    private int likesAmount;
    private int dislikesAmount;
    private int lovesAmount;
    private int commentsAmount;

    public int getCommentsAmount() {
        return commentsAmount;
    }

    public void setCommentsAmount(int commentsAmount) {
        this.commentsAmount = commentsAmount;
    }

    private String text;
    private String mediaUrl;
    private int mediaId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMediaUrl() {

        if (mediaUrl.contains("http")) {
            return mediaUrl;
        } else {
            return "http://" + mediaUrl;
        }
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }


    private String relationship;
    final public String RELATION_NONE = "RELATION_NONE";
    private String path;

    public Media() {
        super();
    }

    public Media(int id, String url) {
        this.url = url;
    }

    public String getUrl() {
        if(url == null) return "";
        if (url.contains("http://"))
            return url;
        else
            return "http://" + url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLikesAmount() {
        return likesAmount;
    }

    public void setLikesAmount(int likesAmount) {
        this.likesAmount = likesAmount;
    }

    public int getDislikesAmount() {
        return dislikesAmount;
    }

    public void setDislikesAmount(int dislikesAmount) {
        this.dislikesAmount = dislikesAmount;
    }

    public int getLovesAmount() {
        return lovesAmount;
    }

    public void setLovesAmount(int lovesAmount) {
        this.lovesAmount = lovesAmount;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public static ArrayList<Media> toArray(String[] images) {
        ArrayList<Media> urls = new ArrayList<>();
        int id = 0;
        for (String url : images) {
            Media img = new Media();
            img.setUrl(url);
            urls.add(img);
        }
        return urls;
    }


    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
