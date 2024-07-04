package com.zor07.tgdemo.service.bot.model;

public class ReminderData {
    private Integer sec;
    private Long id;

    public ReminderData() {
    }

    public ReminderData(int sec, Long id) {
        this.sec = sec;
        this.id = id;
    }

    public Integer getSec() {
        return sec;
    }

    public void setSec(Integer sec) {
        this.sec = sec;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
