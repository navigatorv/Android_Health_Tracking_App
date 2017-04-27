package com.example.kzha6954.mysteps.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zkd on 31-07-2016.
 */
public class Group {
    public String groupName;
    public String motto;
    //public String members;
    //public Map<String, Boolean> members = new HashMap<>();

    public Group() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Group(String groupName, String motto) {
        this.groupName = groupName;
        this.motto = motto;
    }

//    public Group(String groupName, String motto,String members) {
//        this.groupName = groupName;
//        this.motto = motto;
//        //this.members = members;
//    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("groupName", groupName);
        result.put("motto", motto);
        return result;
    }
    // [END post_to_map]

    public String getGroupName() {
        return groupName;
    }

    public String getMotto() {
        return motto;
    }

//    public String getMembers() {
//        return members;
//    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

//    public void setMembers(String members) {
//        this.members = members;
//    }
}

