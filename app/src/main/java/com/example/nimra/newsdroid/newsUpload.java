package com.example.nimra.newsdroid;

public class newsUpload {
    private String newstitle;
    private String newsdescription;
    private String newstime;
    private String newsimage;

    public newsUpload(){

    }

    public newsUpload(String title,String description,String time,String img){
        newstitle = title;
        newsdescription = description;
        newstime = time;
        newsimage = img;
    }

    public String getNewstitle() {
        return newstitle;
    }

    public void setNewstitle(String ntitle) {
        newstitle = ntitle;
    }

    public String getNewsdescription() {
        return newsdescription;
    }

    public void setNewsdescription(String ndescription) {
        newsdescription = ndescription;
    }

    public String getNewstime() {
        return newstime;
    }

    public void setNewstime(String ntime) {
        newstime = ntime;
    }

    public String getNewsimage() {
        return newsimage;
    }

    public void setNewsimage(String nimg) {
        newsimage = nimg;
    }
}
