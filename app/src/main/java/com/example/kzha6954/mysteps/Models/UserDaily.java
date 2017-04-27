package com.example.kzha6954.mysteps.Models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zkd on 28-07-2016.
 */
public class UserDaily {
    public String goalSteps;
    public String actualSteps;
    public String date;
    public int likeCount;
    public Map<String, String> like = new HashMap<>();

    public UserDaily() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)

    }

//    public UserDaily(String goalSteps, String actualSteps,String date, int likeCount) {
//        this.goalSteps = goalSteps;
//        this.actualSteps = actualSteps;
//        this.date = date;
//        this.likeCount = likeCount;
//    }


    public String getGoalSteps(){return goalSteps;};
    public String getActualSteps(){return actualSteps;};
    public int getLikeCount(){return likeCount;};
    public String getDate(){return date;};

    public void setGoalSteps(String goalSteps){this.goalSteps = goalSteps;};
    public void setActualSteps(String actualSteps){this.actualSteps = actualSteps;};
    public void setDate(String date){this.date = date;};

}
