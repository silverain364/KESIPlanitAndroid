package com.example.kesi.data.model;

public class CalendarDayDto {
    private String day;
    private boolean sunday = false;
    private boolean saturday = false;

    public CalendarDayDto(String day) {
        this.day = day;
    }

    public String getDay(){
        return day;
    }
}
