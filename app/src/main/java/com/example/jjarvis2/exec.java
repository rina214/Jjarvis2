package com.example.jjarvis2;

import java.util.Calendar;
import java.util.Map;

public class exec {
    private int date;
    private Map<String,Object> exercise;

    public exec(){};

    public exec(int date, Map<String,Object> exercise){
        this.date = date;
        this.exercise = exercise;
    }

    public int getDate() {
        return date;
    }
    public void setDate(int date) {
        this.date = date;
    }
    public int year(){
        return date/10000;
    }
    public int month(){
        return (date/100)%100;
    }
    public int day(){
        return date%100;
    }
    public int week(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year(),month()-1,day());
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public Map<String, Object> getExercise() {
        return exercise;
    }

    public void setExercise(Map<String, Object> exercise) {
        this.exercise = exercise;
    }
}
