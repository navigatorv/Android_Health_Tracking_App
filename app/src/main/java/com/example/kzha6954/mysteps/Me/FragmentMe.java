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
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kzha6954.mysteps.Models.User;
import com.example.kzha6954.mysteps.Models.UserDaily;
import com.example.kzha6954.mysteps.Moments.FragmentMoments;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import com.example.kzha6954.mysteps.R;
import com.google.firebase.database.ValueEventListener;


public class FragmentMe extends Fragment implements View.OnClickListener {

    private TextView etStausDisplay;
    private TextView usernm;
    private TextView targetSteps;;
    private TextView groupNm;
    private TextView groupId;

    //read username,goalSteps,GroupName,status from firebase
    private String TAG = "FragmentMe";
    private FirebaseDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View meView = inflater.inflate(R.layout.fragment_me, container, false);
        //read data from sharepreferences
        usernm = (TextView) meView.findViewById(R.id.username_tv_me);
        targetSteps = (TextView) meView.findViewById(R.id.goal_display_tv_me);
        groupId = (TextView) meView.findViewById(R.id.Group_display_tv_me);
        //groupNm = (TextView) meView.findViewById(R.id.Group_Name_display_tv_me);
        etStausDisplay = (TextView) meView.findViewById(R.id.fragment_me_et_staus_display);

        meView.findViewById(R.id.Set_target_steps_ll_me).setOnClickListener(this);
        meView.findViewById(R.id.viewProfile_ll_me).setOnClickListener(this);
        meView.findViewById(R.id.viewHistory_ll_me).setOnClickListener(this);
        meView.findViewById(R.id.setUsername_ll_me).setOnClickListener(this);

        read();
        return meView;
    }
    public String getEmail(){return FirebaseAuth.getInstance().getCurrentUser().getEmail();};
    public String getUid(){return FirebaseAuth.getInstance().getCurrentUser().getUid();};

    public void read(){
        //read from user table
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.status==null){
                    etStausDisplay.setText("write something");
                }else{
                    etStausDisplay.setText(user.getStatus());
                }
                if (user.username==null){
                    usernm.setText("Default User");
                }else{
                    usernm.setText(user.getUsername());
                }
                groupId.setText(user.getGroupId());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        db.getInstance().getReference("user").child(getUid()).addValueEventListener(userListener);

        //read from userDaily table
        ValueEventListener userDailyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserDaily userDaily = dataSnapshot.getValue(UserDaily.class);
                targetSteps.setText(FragmentMoments.RemoteConfig_personalGoalSteps);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        db.getInstance().getReference("userDaily").child(getUid()).addValueEventListener(userDailyListener);
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.viewProfile_ll_me:
                startActivityForResult(new Intent(getActivity(), SettingProfileActivity.class), 2);
                break;
            case R.id.Set_target_steps_ll_me:
                startActivityForResult(new Intent(getActivity(), SettingStatusActivity.class), 1);
                break;
            case R.id.viewHistory_ll_me:
                startActivityForResult(new Intent(getActivity(), HistorylineChartActivity.class), 3);
                break;
            case R.id.setUsername_ll_me:
                startActivityForResult(new Intent(getActivity(), SetUsernameActivity.class), 4);
                break;
            default:
                break;
        }
    }

}
