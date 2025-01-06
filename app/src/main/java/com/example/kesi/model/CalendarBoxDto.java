package com.example.kesi.model;

public class CalendarBoxDto {
    public static final int NEXT_MONTH = 1;
    public static final int LAST_MONTH = -1;
    public static final int NOW_MONTH = 0;
    private final int day;
    private final int monthState;
    public CalendarBoxDto(int day, int monthState) {
        this.day = day;
        this.monthState = monthState;
    }

    public int getDay() {
        return day;
    }
    public int getMonthState() {return monthState; }
}

