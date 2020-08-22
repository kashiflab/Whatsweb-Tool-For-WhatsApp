package com.limecoders.whatsweb.Models;

public class SlidingBannerModel {
    private String id;
    private String url;
    private String title;
    private String imageURl;

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getImageURl() {
        return imageURl;
    }

    public SlidingBannerModel(String id, String url, String title, String imageURl) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.imageURl = imageURl;
    }
}
