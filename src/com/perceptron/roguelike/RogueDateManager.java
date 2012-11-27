package com.perceptron.roguelike;

/**
 * Created with IntelliJ IDEA.
 * User: Eric
 * Date: 7/22/12
 * Time: 7:52 PM
 */
public class RogueDateManager {
    private float millisecond;
    private int second;
    private boolean newSecond;
    private int minute;
    private boolean newMinute;
    private int hour;
    private boolean newHour;
    private int day;
    private boolean newDay;
    private int month;
    private boolean newMonth;
    private int year;
    private boolean newYear;

    public RogueDateManager(){
        millisecond = 0;
        second = 0;
        minute = 0;
        hour = 6;
        day = 0;
        month = 0;
        year = 0;
        clearFlags();
    }

    /**
     * Based on the time of MotherNature's update loop, we increment the world time, rolling
     * changes into the higher orders as necessary
     * @param dT a millesecond interval. At a timeScale of 1, this should be ~16 ms. When the
     *           millisecond count exceeds 1000, the second counter is incremented, and so on
     *           up the chain
     */
    public void update(float dT){
        millisecond += dT;
        if(millisecond >= 1000){
            int extraSeconds = (int)(millisecond / 1000);
            millisecond = 0;
            second += extraSeconds;
            newSecond = true;
        }
        if(second >= 60){
            second = 0;
            minute++;
            newMinute = true;
        }
        if(minute >= 60){
            minute = 0;
            hour++;
            newHour = true;
        }
        if(hour >= 24){
            hour = 0;
            day++;
            newDay = true;
        }
        if(day >= 30){
            day = 0;
            month++;
            newMonth = true;
        }
        if(month >= 12){
            month = 0;
            year++;
            newYear = true;
        }
    }

    public String toString(){
        return hour + ":" + minute + ":" + second + "." + (int)millisecond + " of " + month + "/" + day + "/" + year;
    }

    public void clearFlags(){
        newSecond = false;
        newMinute = false;
        newHour = false;
        newDay = false;
        newMonth = false;
        newYear = false;
    }

    public float getMillisecond() {
        return millisecond;
    }

    public int getSecond() {
        return second;
    }

    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public boolean isNewSecond() {
        return newSecond;
    }

    public boolean isNewMinute() {
        return newMinute;
    }

    public boolean isNewHour() {
        return newHour;
    }

    public boolean isNewDay() {
        return newDay;
    }

    public boolean isNewMonth() {
        return newMonth;
    }

    public boolean isNewYear() {
        return newYear;
    }
}