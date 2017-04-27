package com.example.kzha6954.mysteps.Models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zkd on 12-08-2016.
 */
public class GroupDaily {
    public String groupGoalSteps;
    public String groupActualSteps;
    public String date;

    public GroupDaily() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)

    }


    public String getGroupGoalSteps(){return groupGoalSteps;};
    public String getGroupActualSteps(){return groupActualSteps;};

    public void setGroupGoalSteps(String goalSteps){this.groupGoalSteps = goalSteps;};
    public void setGroupActualSteps(String actualSteps){this.groupActualSteps = actualSteps;};
}
