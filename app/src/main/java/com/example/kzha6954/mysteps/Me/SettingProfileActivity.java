package com.example.kzha6954.mysteps.Me;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kzha6954.mysteps.Models.User;
import com.example.kzha6954.mysteps.Models.UserDaily;
import com.example.kzha6954.mysteps.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kzha6954 on 2/07/2016.
 */
public class SettingProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivIcon;
    private EditText etGender;
    private EditText etBirthday;
    private EditText etHeight;
    private EditText etWeight;
    private Button saveBtn;

    //read username,goalSteps,GroupName,status from firebase
    private String TAG = "SettingProfileActivity";
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_me_profile);
        etGender = (EditText) findViewById(R.id.fragment_me_et_gender);
        etBirthday = (EditText) findViewById(R.id.fragment_me_et_birthday);
        etHeight = (EditText) findViewById(R.id.fragment_me_et_height);
        etWeight = (EditText) findViewById(R.id.fragment_me_et_weight);
        saveBtn = (Button) findViewById(R.id.fragment_me_btn_setProfile);
        saveBtn.setOnClickListener(this);
        read();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_me_set_profile);
        toolbar.setTitle("Set Profile");
        setSupportActionBar(toolbar);


        //set up btn
        ActionBar up_multiFrame = getSupportActionBar();
        up_multiFrame.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.fragment_me_btn_setProfile:
                save();
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 读取数据，显示
     */
    private void read(){
//read from user table
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                etGender.setText(user.getGender());
                etBirthday.setText(user.getBirth());
                etHeight.setText(user.getHeight());
                etWeight.setText(user.getWeight());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        db.getInstance().getReference("user").child(getUid()).addValueEventListener(userListener);
    }

    public String getEmail(){return FirebaseAuth.getInstance().getCurrentUser().getEmail();};
    public String getUid(){return FirebaseAuth.getInstance().getCurrentUser().getUid();};


    private void save(){
        db.getInstance().getReference("user").child(getUid()).push().getKey();
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        db.getInstance().getReference("user").child(getUid()).child("gender").setValue(etGender.getText().toString());
        db.getInstance().getReference("user").child(getUid()).child("birth").setValue(etBirthday.getText().toString());
        db.getInstance().getReference("user").child(getUid()).child("weight").setValue(etWeight.getText().toString());
        db.getInstance().getReference("user").child(getUid()).child("height").setValue(etHeight.getText().toString());
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
