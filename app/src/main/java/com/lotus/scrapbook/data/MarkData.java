package com.lotus.scrapbook.data;

public class MarkData {
    String title;
    String scrap;
    String create_time;
    String check;

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public MarkData(String title, String scrap, String create_time, String check, String password) {
        this.title = title;
        this.scrap = scrap;
        this.create_time = create_time;
        this.check = check;
        this.password = password;
    }

    String password;

    public MarkData() {
    }

    public MarkData(String title, String scrap) {
        this.title = title;
        this.scrap = scrap;
    }

//    public MarkData(String title, String scrap, String create_time) {
//        this.title = title;
//        this.scrap = scrap;
//        this.create_time = create_time;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScrap() {
        return scrap;
    }

    public void setScrap(String scrap) {
        this.scrap = scrap;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
