package com.example.jjarvis2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class specification {
    private int date;
    private int breakfast;
    private int lunch;
    private int dinner;
    private int cal;
    private String[] exercise;
    private String week_goal;

    public specification(){};
    public specification(int date, int breakfast, int lunch, int dinner, int cal, String[] exercise, String week_goal){
        this.date = date;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.cal = cal;
        this.exercise = exercise;
        this.week_goal = week_goal;
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
    public String[] exercise(){
        return exercise;
    }
    public List getExercise() {
        List exe = new ArrayList();
        for(int i = 0; i < exercise.length; i++){
            exe.add(exercise[i]);
        }
        return exe;
    }

    public void setExercise(String[] exercise) {
        this.exercise = exercise;
    }

    public String getWeek_goal() {
        return week_goal;
    }

    public void setWeek_goal(String week_goal) {
        this.week_goal = week_goal;
    }
}
