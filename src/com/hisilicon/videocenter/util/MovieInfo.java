package com.hisilicon.videocenter.util;

public class MovieInfo {

    private String title;// 片名

    private String director;// 导演

    private String performer;// 主演

    private String type;// 类型

    private String area;// 区域

    private String duration;// 时长

    private String synopsis;// 剧情介绍

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    @Override
    public String toString() {
        return "title:" + title + " , director:" + director + " , performer:"
                + performer + " , area:" + area + " , duration:" + duration
                + " , type:" + type + " , synopsis:" + synopsis;
    }

}