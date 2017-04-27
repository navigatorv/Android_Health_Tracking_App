package com.example.kzha6954.mysteps.Group;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kzha6954.mysteps.Models.User;
import com.example.kzha6954.mysteps.Models.UserDaily;
import com.example.kzha6954.mysteps.Moments.FragmentMoments;
import com.example.kzha6954.mysteps.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by kzha6954 on 3/07/2016.
 */
public class MemberDetailActivity extends AppCompatActivity {

    private TextView username;
    private TextView targetSteps;
    private TextView groupName;
    private TextView status;
    private TextView gender;
    private TextView birth;
    private TextView height;
    private TextView weight;

    private String userId;

    private String TAG = "MemberDetailActivity";
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member_details);
        userId = getIntent().getStringExtra("msg");

        //Init
        username = (TextView) findViewById(R.id.group_other_username);
        targetSteps = (TextView) findViewById(R.id.group_other_goalSteps);
        groupName = (TextView) findViewById(R.id.group_other_groupName);
        status = (TextView) findViewById(R.id.group_other_status);
        gender = (TextView) findViewById(R.id.group_other_gender);
        birth = (TextView) findViewById(R.id.group_other_birth);
        height = (TextView) findViewById(R.id.group_other_height);
        weight = (TextView) findViewById(R.id.group_other_weight);

        readUser();
        groupInfo();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Person");
        setSupportActionBar(toolbar);
        //set up btn
        ActionBar up_multiFrame = getSupportActionBar();
        up_multiFrame.setDisplayHomeAsUpEnabled(true);

    }

    public void readUser(){
        //read from user table
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.i(TAG, "changed: "+user.getWeight());
                UserDaily userDaily = dataSnapshot.getValue(UserDaily.class);
                if (user.username==null){
                    username.setText("Default name");
                }else{
                    username.setText(user.username);
                }
                if (user.status==null){
                    status.setText("No status written yet");
                }else{
                    status.setText(user.status);
                }
                if (user.gender==null){
                    gender.setText("Not set yet");
                }else{
                    gender.setText(user.gender);
                }
                if (user.height==null){
                    height.setText("Not set yet");
                }else{
                    height.setText(user.height);
                }
                if (user.weight==null){
                    weight.setText("Not set yet");
                }else{
                    weight.setText(user.weight);
                }
                if (user.birth==null){
                    birth.setText("Not set yet");
                }else{
                    birth.setText(user.birth);
                }
                targetSteps.setText(FragmentMoments.RemoteConfig_personalGoalSteps);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        db.getInstance().getReference("user").child(userId).addValueEventListener(userListener);
    }

    public void groupInfo(){
        groupName.setText(FragmentGroup.groupname);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
