package com.example.kzha6954.mysteps.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zkd on 28-07-2016.
 */
public class User {
    public String username;
    public String email;
    public String gender;
    public String birth;
    public String height;
    public String weight;
    public String status;
    public String groupId;
    public String lastActiveDate;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email) {
        this.email = email;
    }


    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User(String gender,String birth, String height,String weight) {
        this.gender = gender;
        this.birth = birth;
        this.height = height;
        this.weight = weight;

    }

    public User(String username, String email, String gender,String birth, String height,String weight,String status,String groupId) {
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.birth = birth;
        this.height = height;
        this.weight = weight;
        this.status = status;
        this.groupId = groupId;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("gender", gender);
        result.put("birth", birth);
        result.put("height", height);
        result.put("weight", weight);
        return result;
    }
    // [END post_to_map]

    public String getUsername(){return username;};
    public String getEmail(){return email;};
    public String getGender(){return gender;};
    public String getBirth(){return birth;};
    public String getHeight(){return height;};
    public String getWeight(){return weight;};
    public String getStatus(){return status;};
    public String getGroupId(){return groupId;};

    public void setUsername(String username){this.username = username;};
    public void setEmail(String email){this.email = email;};
    public void setGender(String gender){this.gender = gender;};
    public void setBirth(String birth){this.birth = birth;};
    public void setHeight(String height){this.height = height;};
    public void setWeight(String weight){this.weight = weight;};
    public void setStatus(String status){this.status = status;};
    public void setGroupId(String groupId){this.groupId = groupId;};
}
