package me.doozzik;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class Mod {
    private String name;
    private String link;
    private int monthlyDown;
    private int totalDown;
    private int likes;
    private String version;
    private Date created;
    private Date updated;

    Mod(String name, String link, int monthlyDown, int totalDown, int likes, String version, Date created, Date updated){
        this.name = name;
        this.link = link;
        this. monthlyDown = monthlyDown;
        this.totalDown = totalDown;
        this.likes = likes;
        this.version = version;
        this.created = created;
        this.updated = updated;
    }

    public String getName() {
        return name.replace("'", "").replace(",", "").replace("\"", "");
    }

    public String getLink() {
        return link;
    }

    public int getMonthlyDown() {
        return monthlyDown;
    }

    public int getTotalDown() {
        return totalDown;
    }

    public int getLikes() {
        return likes;
    }

    public String getVersion() {
        return version;
    }

    public String getUpdated() {
        DateFormat format = new SimpleDateFormat("M/yyyy");
        return format.format(updated);
    }

    public String getCreated() {
        DateFormat format = new SimpleDateFormat("M/yyyy");
        return format.format(created);
    }
}
