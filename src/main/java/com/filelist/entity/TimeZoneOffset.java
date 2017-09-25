package com.filelist.entity;

public class TimeZoneOffset {

    private int dayOffset;
    private float timeOffset;

    public TimeZoneOffset(int dayOffset, float timeOffset) {
        this.dayOffset = dayOffset;
        this.timeOffset = timeOffset;
    }

    public int getDayOffset() {
        return dayOffset;
    }

    public float getTimeOffset() {
        return timeOffset;
    }

    @Override
    public String toString() {
        return "TimeZoneOffset [dayOffset=" + dayOffset + ", timeOffset=" + timeOffset + "]";
    }
}
