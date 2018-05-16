package com.example.user.tkpm.Model;

/**
 * Created by USER on 15/05/2018.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Snippet {

    @SerializedName("channelId")
    @Expose
    private String channelId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("categoryId")
    @Expose
    private String categoryId;

    /**
     * No args constructor for use in serialization
     *
     */
    public Snippet() {
    }

    /**
     *
     * @param title
     * @param channelId
     * @param categoryId
     */
    public Snippet(String channelId, String title, String categoryId) {
        super();
        this.channelId = channelId;
        this.title = title;
        this.categoryId = categoryId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

}
