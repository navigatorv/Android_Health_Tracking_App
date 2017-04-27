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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.kzha6954.mysteps.Models.Group;
import com.example.kzha6954.mysteps.Models.User;
import com.example.kzha6954.mysteps.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Semaphore;

/**
 * Created by kzha6954 on 3/07/2016.
 */
public class GroupMemberActivity extends AppCompatActivity {

    private String TAG = "GroupMemberActivity";
    public static final String EXTRA_GROUPID_KEY = "groupId_key";
    private List<Map<String, Object>> mData;
    private FirebaseDatabase db;
    String mGroupId;
    String mflag;
    private String memberId;
    Map<String,Object> members;

    private ListView lv;
    private TextView tv_username;
    private TextView tv_status;
    private ImageView portrait;

    private TextView myEmpty;
    int count =0;

    private memberListAdapter adp;
    String name_item;
    String imgPath_item;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member_list);

        adp = new memberListAdapter(this.getLayoutInflater());
        getGroupID();
        tv_username = (TextView) findViewById(R.id.item_user_name);
        tv_status = (TextView) findViewById(R.id.item_user_status);
        //portrait = (ImageView)findViewById(R.id.ItemImage);

        lv = (ListView) findViewById(R.id.memberList_lv);
        getData();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {

                Intent intent = new Intent(GroupMemberActivity.this, MemberDetailActivity.class);
                String uid = (String)mData.get(position).get("memberId");
                intent.putExtra("msg", uid);
                Log.i("qqq","When clicked" + uid);
                startActivity(intent);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Member List");
        setSupportActionBar(toolbar);


        //set up btn
        ActionBar up_multiFrame = getSupportActionBar();
        up_multiFrame.setDisplayHomeAsUpEnabled(true);



    }

    public String getUid(){return FirebaseAuth.getInstance().getCurrentUser().getUid();};

    public void getData(){
        Log.w(TAG, "getData");
        //read from firebase
        if (mGroupId == null){
            mflag = "nothing";
            Log.w(TAG, "GROUPID null");
        }else {
            //read from userDaily table
            db.getInstance().getReference("groupMember").child(mGroupId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.w(TAG, "iN THIS LISGTENER");
                            GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                            members = dataSnapshot.getValue(genericTypeIndicator);
                            for (String key : members.keySet()) {
                                Log.w(TAG, "Member"+ "key= "+ key + " and value= " + members.get(key));
                            }
                            readAllMembers();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        }
            });

        }
    }

    public void readAllMembers(){
        Log.w(TAG, "kevin       getMember");
        mData = new ArrayList<Map<String, Object>>();

//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("username", "G1");
//        map.put("status", "google 1");
//        mData.add(map);
//
//        Map<String, Object> m = new HashMap<String, Object>();
//        m = new HashMap<String, Object>();
//        m.put("username", "G2");
//        m.put("status", "google 2");
//        mData.add(m);
//        lv.setAdapter(adp);
        for (String key : members.keySet()) {
            memberId = key;
            final Map<String, Object> map = new HashMap<String, Object>();
            map.put("memberId", memberId);
            Log.i("qqq", "loop " + memberId);

            db.getInstance().getReference("user").child(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            Log.w(TAG, "All Member: "+ user.getUsername());
                            Log.w(TAG, "All Member: "+ user.getStatus());
                            if (user.username == null){
                                map.put("username","Default username");
                            }else{
                                map.put("username", user.username.toString());
                            }
                            if (user.status == null){
                                map.put("status","Default status");
                            }else{
                                map.put("status", user.status.toString());
                            }

                            mData.add(map);
                            lv.setAdapter(adp);
                            count++;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        }
                    });
        }


//        //read each group member's personal info and push them into map
//        for (String key : members.keySet()) {
//            Log.w(TAG, key);
//
//        }
    }


    public void getGroupID(){
        mGroupId = getIntent().getStringExtra(EXTRA_GROUPID_KEY);
        Log.w(TAG, "get groupID"+mGroupId);
    }




    public final class ViewHolder{
        public ImageView img;
        public TextView username;
        public TextView status;

    }

    class memberListAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public memberListAdapter(LayoutInflater inflater) {
            // TODO Auto-generated constructor stub
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();
                convertView = inflater.inflate(R.layout.fragment_member_list_single_item, null);
                holder.img = (ImageView)convertView.findViewById(R.id.ItemImage);
                holder.username = (TextView)convertView.findViewById(R.id.item_user_name);
                holder.status = (TextView)convertView.findViewById(R.id.item_user_status);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }

            for (String key : mData.get(position).keySet()) {
                Log.w(TAG, "Location" + position);
                Log.w(TAG, "Adapter: key= "+ key + " and value= " + mData.get(position).get(key));
            }

            //holder.img.setBackgroundResource((Integer)mData.get(position).get("img"));
            holder.username.setText((String)mData.get(position).get("username"));
            holder.status.setText((String)mData.get(position).get("status"));

            return convertView;
        }

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
