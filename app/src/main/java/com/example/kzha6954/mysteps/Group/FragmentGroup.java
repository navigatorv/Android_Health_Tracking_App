package com.example.kzha6954.mysteps.Group;

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
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kzha6954.mysteps.Models.Group;
import com.example.kzha6954.mysteps.Models.User;
import com.example.kzha6954.mysteps.Models.UserDaily;
import com.example.kzha6954.mysteps.Moments.FragmentMoments;
import com.example.kzha6954.mysteps.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class FragmentGroup extends Fragment implements View.OnClickListener{

    static public String groupname;
    private TextView etStausDisplay;
    private TextView groupnm;
    private TextView groupGoals;
    //read username,goalSteps,GroupName,status from firebase
    private String TAG = "FragmentGroup";
    private FirebaseDatabase db;
    private DatabaseReference dbRef;


    String groupId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        groupnm = (TextView) view.findViewById(R.id.groupname_tv_group);
        etStausDisplay = (TextView) view.findViewById(R.id.motto_display_et_me);
        groupGoals = (TextView) view.findViewById(R.id.goal_display_tv_group);

        //validate();
        groupId = FragmentMoments.mGroupId;
        readUserTable();
        readUserDailyTable();

        view.findViewById(R.id.fragment_group_ll_view_member).setOnClickListener(this);
        view.findViewById(R.id.fragment_group_ll_setting).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.fragment_group_ll_view_member:
                Intent it_memberlist = new Intent(getActivity(),GroupMemberActivity.class);
                it_memberlist.putExtra(GroupMemberActivity.EXTRA_GROUPID_KEY,groupId);
                startActivity(it_memberlist);
                break;
            case R.id.fragment_group_ll_setting:
                Intent it_setting = new Intent(getActivity(),FragGroupSettingActivity.class);
                it_setting.putExtra(FragGroupSettingActivity.EXTRA_GROUPID_KEY,groupId);
                startActivity(it_setting);
                break;
            default:
                break;
        }
    }

    public String getUid(){return FirebaseAuth.getInstance().getCurrentUser().getUid();};

//    public void validate(){
//        db.getInstance().getReference("user").child(getUid()).child("groupId")
//                .addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    groupId = dataSnapshot.getValue(String.class);
//                    Log.w(TAG, groupId);
//                    read();
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//            }
//        });
//    }

    public void readUserTable(){
        Log.w(TAG, "out"+groupId);
        if (groupId == null){
            etStausDisplay.setText("Write sth to your teammate");
            groupnm.setText("No Group yet");
        }else{
            //read from user table
            ValueEventListener groupListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Group group = dataSnapshot.getValue(Group.class);
                    Log.i(TAG, "changed: "+group.getGroupName());
                    if (group.getGroupName()==null){
                        groupnm.setText("No Group yet");
                        groupname = "No Group yet";
                    }else{
                        groupnm.setText(group.getGroupName());
                        groupname = group.getGroupName();
                    }
                    if (group.getMotto()==null){
                        etStausDisplay.setText("Write sth to your teammate");
                    }else{
                        etStausDisplay.setText(group.getMotto());
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };
            db.getInstance().getReference("group").child(groupId).addValueEventListener(groupListener);
        }
    }

    public void readUserDailyTable() {
        if (groupId == null) {
            groupGoals.setText("20000");
        } else {
            groupGoals.setText(FragmentMoments.RemoteConfig_groupGoalSteps);
        }
    }
}

