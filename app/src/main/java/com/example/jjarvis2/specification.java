package com.example.jjarvis2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class specification {
    private int date;
    private int breakfast;
    private int lunch;
    private int dinner;
    private int cal;
    private String breakfast_menu, lunch_menu, dinner_menu;
    private Map<String,Integer> exercise = new HashMap<>();

    public specification(){};
    public specification(int date, Map<String,Integer> exercise){
        this.date = date;
        this.exercise = exercise;
    }
    public specification(int date, int breakfast, int lunch, int dinner, int cal, String breakfast_menu, String lunch_menu, String dinner_menu, Map<String,Integer> exercise){
        this.date = date;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.cal = cal;
        this.breakfast_menu = breakfast_menu;
        this.lunch_menu = lunch_menu;
        this.dinner_menu = dinner_menu;
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

    public int getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(int breakfast) {
        this.breakfast = breakfast;
    }

    public int getLunch() {
        return lunch;
    }

    public void setLunch(int lunch) {
        this.lunch = lunch;
    }

    public int getDinner() {
        return dinner;
    }

    public void setDinner(int dinner) {
        this.dinner = dinner;
    }

    public int getCal() {
        return cal;
    }

    public void setCal(int cal) {
        this.cal = cal;
    }

    public String getBreakfast_menu() {
        return breakfast_menu;
    }

    public void setBreakfast_menu(String breakfast_menu) {
        this.breakfast_menu = breakfast_menu;
    }

    public String getLunch_menu() {
        return lunch_menu;
    }

    public void setLunch_menu(String lunch_menu) {
        this.lunch_menu = lunch_menu;
    }

    public String getDinner_menu() {
        return dinner_menu;
    }

    public void setDinner_menu(String dinner_menu) {
        this.dinner_menu = dinner_menu;
    }

    public Map<String,Integer>  getExercise(){
        return exercise;
    }

    public void setExercise(Map<String,Integer>  exercise) {
        this.exercise = exercise;
    }

}
