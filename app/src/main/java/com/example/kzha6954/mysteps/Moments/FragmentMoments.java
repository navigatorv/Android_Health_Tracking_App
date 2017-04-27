package com.example.kzha6954.mysteps.Moments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kzha6954.mysteps.Main.MainActivity;
import com.example.kzha6954.mysteps.Models.Group;
import com.example.kzha6954.mysteps.Models.GroupDaily;
import com.example.kzha6954.mysteps.Models.User;
import com.example.kzha6954.mysteps.Models.UserDaily;
import com.example.kzha6954.mysteps.R;
import com.firebase.ui.database.BuildConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import android.text.format.Time;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class FragmentMoments extends Fragment {

    private String diverseText = "You have achieved ";

    static public String  mGroupId;

    private TextView username1;
    private TextView actual_steps1;
    private TextView like_count1;
    private TextView target_steps1;
    private TextView complete1;
    private Button btn1;
    private TextView progress;
    private TextView groupnm;
    private TextView groupGoal;
    private TextView groupAchieve;

    private TextView status1;

    private String TAG = "FragmentMoments";
    private FirebaseDatabase db;
    private DatabaseReference dbRef;

    int numberOfMembers;
    private int achieved_steps;
    private String gpGoalSteps;


    //lv
    private ListView lv;
    private List<Map<String, Object>> mDataSet;
    private Map<String,Object> members;
    private MomentAdapter adp;
    //userDailyID,userID, username,actualSteps,goalSteps,likeCount
    final int NO_OF_FIELDS_TO_UPDATE_IN_LV = 6;

    //Remote Config
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private long cacheExpiration = 10 ;
    static public String RemoteConfig_groupGoalSteps;
    static public String RemoteConfig_personalGoalSteps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moments, container, false);
        username1 = (TextView) view.findViewById(R.id.moments_tv_user_name1);
        actual_steps1 = (TextView) view.findViewById(R.id.moments_tv_user_steps_actual1);
        like_count1 = (TextView) view.findViewById(R.id.moments_tv_count);
        target_steps1 = (TextView) view.findViewById(R.id.moments_tv_user_steps_target1);
        complete1 = (TextView) view.findViewById(R.id.moments_tv_user_completion1);

        groupGoal = (TextView) view.findViewById(R.id.goal_display_tv_moments);
        groupAchieve = (TextView) view.findViewById(R.id.steps_status_tv_moments);

        progress =  (TextView) view.findViewById(R.id.progress_display_tv_moments);
        groupnm = (TextView) view.findViewById(R.id.groupname_tv_moments);

        //lv
        lv =(ListView) view.findViewById(R.id.fragment_monment_lv) ;
        adp = new MomentAdapter(getActivity().getLayoutInflater());

        mDataSet = new ArrayList<Map<String, Object>>();

        numberOfMembers=0;

        achieved_steps = 0;

//        btn1 = (Button) view.findViewById(R.id.button1);
//        btn1.setOnClickListener(this);

        Log.w(TAG, "Hi");
        //set up  google REMOTE CONFIG VALUE
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        Log.w(TAG, "Get String");
        fetchAndInitiateRemoteConfigData();

        readLikesfromUserDaily();
        Log.i("qqq",String.valueOf(MainActivity.likeCountRestrictor));

        readGroupInfo();

        return view;
    }

    public void fetchAndInitiateRemoteConfigData(){
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.w("Config", "Fetch success");

                            // Once the config is successfully fetched it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Log.w("Config", "Fetch failed");
                        }
                        RemoteConfig_groupGoalSteps = mFirebaseRemoteConfig.getString("groupGoalSteps");
                        RemoteConfig_personalGoalSteps = mFirebaseRemoteConfig.getString("personalGoalSteps");
                        Log.w("Config", "RemoteConfig_groupGoalSteps "+ RemoteConfig_groupGoalSteps);
                        Log.w("Config", "RemoteConfig_personalGoalSteps "+ RemoteConfig_personalGoalSteps);
                        Map<String,Object> resXml = new HashMap<String, Object>();
                        resXml.put("groupGoalSteps",RemoteConfig_groupGoalSteps);
                        resXml.put("groupGoalSteps",RemoteConfig_personalGoalSteps);
                        //fetch
                        mFirebaseRemoteConfig.setDefaults (resXml);
                        groupGoal.setText(RemoteConfig_groupGoalSteps);
                    }
                });
    }


    public void readLikesfromUserDaily(){
        String keyForUserDaily =getUid()+"="+getDate();
        db.getInstance().getReference("userDaily").child(keyForUserDaily)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Get user value
                                UserDaily ud = dataSnapshot.getValue(UserDaily.class);
                                Map<String, String> list = new HashMap<>();
                                list.putAll(ud.like);
                                //ArrayList<String> str = new ArrayList<String>();
                                String a = " ";
                                for (String keyName : list.keySet()) {
                                    //str.add(keyName);
                                    a += keyName;
                                    a = a+ " ";
                                }
                                Log.i("qqq","when do "+String.valueOf(MainActivity.likeCountRestrictor));
                                if (MainActivity.likeCountRestrictor == 0){
                                    int sz = list.size();
                                    if (sz == 1){
                                        Toast.makeText(getActivity().getApplicationContext(),"You have received a 'like' from"+a,Toast.LENGTH_SHORT).show();
                                    }else if(sz == 0){

                                    }else{
                                        Toast.makeText(getActivity().getApplicationContext(),"You have received "+sz +" 'likes' from"+a,Toast.LENGTH_SHORT).show();
                                    }
                                    MainActivity.likeCountRestrictor++;
                                }


                                //read user actual steps
                                String pr = diverseText + ud.actualSteps + " steps today";
                                progress.setText(pr);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            }
                        });

    }

    //Read GroupInfo
    public void readGroupInfo(){

        Log.w(TAG, "readGroup");
        db.getInstance().getReference("user").child(getUid()).child("groupId")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mGroupId = dataSnapshot.getValue(String.class);
                        if (mGroupId != null) {
                            Log.w(TAG, mGroupId);
                            //readGroupDailyInfo();
                            read();
                            getData();
                            //setGroupActualSteps();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });
    }



    //Read top part GroupInfo
    public void read(){
        Log.w(TAG, "out"+mGroupId);
        if (mGroupId == null){
            groupnm.setText("No Group yet");
        }else{
            //read from user table
            ValueEventListener groupListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Group group = dataSnapshot.getValue(Group.class);
                    Log.i(TAG, "changed: "+group.getGroupName());
                    if (group.groupName==null){
                        groupnm.setText("No Group Name yet");
                    }else{
                        groupnm.setText(group.getGroupName());
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };
            db.getInstance().getReference("group").child(mGroupId).addValueEventListener(groupListener);
        }
    }



    public void getData(){
        Log.w(TAG, "getData");
        //read from firebase
        if (mGroupId == null){
            Log.w(TAG, "GROUPID null");
        }else {
            //read two USERID belonging to this group, save it to member map
            db.getInstance().getReference("groupMember").child(mGroupId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.w(TAG, "iN THIS LISGTENER");
                            GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                            members = dataSnapshot.getValue(genericTypeIndicator);
                            for (String key : members.keySet()) {
                                Log.w(TAG, "Member"+ "key= "+ key + " and value= " + members.get(key));
                                numberOfMembers++;
                                Log.w(TAG, "number of members"+ numberOfMembers);
                            }
                            readListviewData();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        }
                    });

        }
    }

    public void readListviewData() {
        Log.w(TAG, "kevin       getMember");

        for (String key : members.keySet()) {
            final Map<String, Object> lvInflateData = new HashMap<String, Object>();
            String keyForUserDaily =key+"="+getDate();
            lvInflateData.put("userDailyID", keyForUserDaily);
            lvInflateData.put("userID", key);
            //read all users'username
            db.getInstance().getReference("user").child(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            //Map<String, Object> map = new HashMap<String, Object>();
                            Log.w(TAG, "User user name: " + user.getUsername());
                            if (user.username == null){
                                lvInflateData.put("username", "Default name");
                            }else{
                                lvInflateData.put("username", user.getUsername().toString());
                            }
                            Log.w(TAG, "User map: " + lvInflateData.get("username"));
                            if (lvInflateData.size() == NO_OF_FIELDS_TO_UPDATE_IN_LV) {
                                mDataSet.add(lvInflateData);
                            }
                            Log.w(TAG, "User mDataSet size: " + mDataSet.size());
                            if (mDataSet.size() == numberOfMembers) {
                                setGroupActualSteps();
                                lv.setAdapter(adp);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        }
                    });

            //check if the other member's userDaily exists


            //read achieve Steps, goal steps and like count from userDaily table
            //db.getInstance().getReference("userDaily").child(key).child("date").child(getDate())
            db.getInstance().getReference("userDaily").child(keyForUserDaily)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserDaily u = dataSnapshot.getValue(UserDaily.class);
                            //Map<String, Object> map = new HashMap<String, Object>();
                            //Log.w(TAG, "Get user name: " + user.getUsername());
                            if (dataSnapshot.hasChild("actualSteps")) {
                                achieved_steps += Integer.parseInt(u.getActualSteps());
                                Log.w("Config", "actual steps when added "+achieved_steps);
                                lvInflateData.put("actualSteps", u.getActualSteps().toString());
                            }else{
                                lvInflateData.put("actualSteps", "0");
                            }
                            Log.w("Config", "Set each list view-> remote personal goal Steps: "+ RemoteConfig_personalGoalSteps);
                            if  (RemoteConfig_personalGoalSteps == null){
                                lvInflateData.put("goalSteps", "10000");
                            }else{
                                //we read goalSteps from firebase remote config
                                lvInflateData.put("goalSteps", RemoteConfig_personalGoalSteps);
                            }

                            Log.w("Config", "data inflate = personal "+RemoteConfig_personalGoalSteps);
                            if (dataSnapshot.hasChild("likeCount")){
                                lvInflateData.put("likeCount", u.getLikeCount());
                            }else{
                                lvInflateData.put("likeCount", 0);
                                String keyForDailyUser = (String) lvInflateData.get("userDailyID");
                                //db.getInstance().getReference("userDaily").child(keyForDailyUser).child("likeCount").setValue(0);
                                //db.getInstance().getReference("userDaily").child(keyForDailyUser).child("date").setValue(getDate());
                                //Initial other user's daily table who havent online today
                                db.getInstance().getReference("userDaily").child(keyForDailyUser).runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        UserDaily p = mutableData.getValue(UserDaily.class);
                                        if (p == null) {
                                            p = new UserDaily();
                                            p.likeCount = 0;
                                            p.date = getDate();
                                            p.goalSteps = RemoteConfig_personalGoalSteps;
                                            Log.w(TAG,"null = " );
                                            Log.w("Config", "transaction success");
                                            mutableData.setValue(p);
                                            return Transaction.success(mutableData);
                                        }

                                        p.likeCount = 0;
                                        p.date = getDate();
                                        p.goalSteps = RemoteConfig_personalGoalSteps;

                                        // Set value and report transaction success
                                        mutableData.setValue(p);
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b,
                                                           DataSnapshot dataSnapshot) {
                                        // Transaction completed
                                        Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                                    }
                                });;

                            }


                            Log.w(TAG, "UserDaily: " + lvInflateData.size());
                            if (lvInflateData.size() == NO_OF_FIELDS_TO_UPDATE_IN_LV) {
                                mDataSet.add(lvInflateData);
                            }
                            Log.w(TAG, "Daily mDataSet size: " + mDataSet.size());
                            if (mDataSet.size() == numberOfMembers) {
                                setGroupActualSteps();
                                lv.setAdapter(adp);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        }
                    });
        }
    }

    //Read top part GroupInfo
    public void setGroupActualSteps(){
        Log.w("Config", "out"+mGroupId);
        if (mGroupId == null){
            groupAchieve.setText(0);
            Log.w("Config", "mGroupID NULL");
        }else{
            String keyForGroupDaily =mGroupId+"="+getDate();
            final String temp = String.valueOf(achieved_steps);
            Log.w("Config", "In else");
            db.getInstance().getReference("groupDaily").child(keyForGroupDaily).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Log.w("Config", "In transaction");
                    GroupDaily p = mutableData.getValue(GroupDaily.class);
                    if (p == null) {
                        p = new GroupDaily();
                        p.groupActualSteps = temp;
                        p.groupGoalSteps = RemoteConfig_groupGoalSteps;
                        Log.w(TAG,"null = " );
                        Log.w("Config", "transaction success");
                        mutableData.setValue(p);

                        return Transaction.success(mutableData);
                    }

                    p.groupActualSteps = temp;
                    p.groupGoalSteps = RemoteConfig_groupGoalSteps;
                    Log.w("Config", "actual steps" + temp);
//
                    // Set value and report transaction success
                    mutableData.setValue(p);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                    // Transaction completed
                    Log.d("Config", "postTransaction:onComplete:" + databaseError);
                    //spend 6hours finished this stupid update listview stuff,how could i forget callback func,
                    // this callback is just amazing
                }
            });;
            groupAchieve.setText(String.valueOf(achieved_steps));
            Log.w("Config", "out actual steps" + achieved_steps);
        }
    }

    public String getUid(){return FirebaseAuth.getInstance().getCurrentUser().getUid();};
    public String getDate(){
        SimpleDateFormat myFmt1=new SimpleDateFormat("dd-MM-yyyy");
        //DateFormat dateFormat = DateFormat.getDateInstance();
        String date = myFmt1.format(new    java.util.Date());
        Log.e("getDate", date);
        return date;
    };


    //    @Override
//    public void onClick(View view) {
//
//        SharedPreferences userDynamic = getActivity().getSharedPreferences("UserDynamic", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = userDynamic.edit();
//
//        String read = userDynamic.getString("LikeCount","99");
//        int count = Integer.parseInt(read)+1;
//        editor.putString("LikeCount",String.valueOf(count));
//        like_count1.setText(String.valueOf(count));
//        editor.commit();
//
//    }


    //adapter
    public final class ViewHolder{
        public TextView username;
        public TextView actualSteps;
        public TextView goalSteps;
        public ProgressBar ProgressBar1;
        public TextView complete1;
        public Button btn1;
        public TextView like_count1;

    }

    class MomentAdapter extends BaseAdapter {
        private int flag = 0;

        private LayoutInflater inflater;

        public MomentAdapter(LayoutInflater inflater) {
            // TODO Auto-generated constructor stub
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mDataSet.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();
                convertView = inflater.inflate(R.layout.fragment_moment_single_item, null);
                holder.username = (TextView)convertView.findViewById(R.id.moments_tv_user_name1);
                holder.actualSteps = (TextView)convertView.findViewById(R.id.moments_tv_user_steps_actual1);
                holder.goalSteps = (TextView)convertView.findViewById(R.id.moments_tv_user_steps_target1);
                holder.ProgressBar1 = (ProgressBar) convertView.findViewById(R.id.progressBar);
                holder.complete1 = (TextView) convertView.findViewById(R.id.moments_tv_user_completion1);

                holder.btn1 = (Button) convertView.findViewById(R.id.button1);
                holder.like_count1 = (TextView) convertView.findViewById(R.id.moments_tv_count);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }
//
//            for (String key : mDataSet.get(position).keySet()) {
//                Log.w(TAG, "Location" + position);
//                Log.w(TAG, "Adapter: key= "+ key + " and value= " + mDataSet.get(position).get(key));
//            }

            //holder.img.setBackgroundResource((Integer)mData.get(position).get("img"));
            holder.username.setText((String)mDataSet.get(position).get("username"));
            holder.actualSteps.setText((String)mDataSet.get(position).get("actualSteps"));
            holder.goalSteps.setText((String)mDataSet.get(position).get("goalSteps"));

            //set Progress bar
            int as = Integer.parseInt((String) mDataSet.get(position).get("actualSteps"));
            int gs = Integer.parseInt((String) mDataSet.get(position).get("goalSteps"));

            //when using progress bar,set Max BEFORE Progress,
            holder.ProgressBar1.setMax(gs);
            holder.ProgressBar1.setProgress(as);
            Log.w(TAG,"Progress bar as = " + as);
            Log.w(TAG,"Progress bar gs= " + gs);
            achieved_steps += as;

            holder.complete1.setText(calculateCompleteness(as,gs));
            Log.w(TAG,"like_count = " + Integer.toString((Integer) mDataSet.get(position).get("likeCount")));
            holder.like_count1.setText(Integer.toString((Integer) mDataSet.get(position).get("likeCount")));

            final String currentUser =getUid();

            holder.btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.w(TAG,"Uid = " + currentUser);
                    String userDailyId = (String)mDataSet.get(position).get("userDailyID");
                    Log.w(TAG,"userDailyId = " + userDailyId);
                    final String userId = (String)mDataSet.get(position).get("userID");
                    Log.w(TAG,"userId = " + userId);

                    if (currentUser.equals(userId)) {
                        Log.w("test","clicked ");
                        Toast.makeText(getActivity().getApplicationContext(), "Cannot 'Like' yourself" ,
                                Toast.LENGTH_SHORT).show();
                    }

                    //db.getInstance().getReference("userDaily").child(userId).child("date").child(getDate()).
                    db.getInstance().getReference("userDaily").child(userDailyId).runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            UserDaily p = mutableData.getValue(UserDaily.class);
                            if (p == null) {
                                Log.w(TAG,"null = " );
                                return Transaction.success(mutableData);
                            }

                            if (currentUser.equals(userId)){
                                Log.w(TAG,"EQUAL = " );
                                mutableData.setValue(p);
                                return Transaction.success(mutableData);
                            }else{
                                if (p.like.containsKey(getUid())) {
                                    // Unstar the post and remove self from stars
                                    p.likeCount = p.likeCount - 1;
                                    p.like.remove(getUid());
                                    int newLikeCount = (Integer) mDataSet.get(position).get("likeCount") - 1;
                                    mDataSet.get(position).put("likeCount",newLikeCount);
                                    Log.w(TAG,"LikeMinus = " + newLikeCount);
                                } else {
                                    // Star the post and add self to stars
                                    p.likeCount = p.likeCount + 1;
                                    p.like.put(getUid(), getLikeTime());
                                    int newLikeCount = (Integer) mDataSet.get(position).get("likeCount") + 1;
                                    mDataSet.get(position).put("likeCount",newLikeCount);
                                    Log.w(TAG,"LikePlus = " + newLikeCount);
                                }
                            }
                            // Set value and report transaction success
                            mutableData.setValue(p);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b,
                                               DataSnapshot dataSnapshot) {
                            // Transaction completed
                            Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                            //spend 6hours finished this stupid update listview stuff,how could i forget callback func,
                            // this callback is just amazing
                            notifyDataSetChanged();
                        }
                    });;
                }
            });
            return convertView;
        }

    }

    public String getLikeTime(){
        SimpleDateFormat myFmt1=new SimpleDateFormat("HH:mm dd-MM-yyyy");
        //DateFormat dateFormat = DateFormat.getDateInstance();
        String likeTime = myFmt1.format(new    java.util.Date());
        Log.e("getLikeTime", likeTime);
        return likeTime;
    }



    public String calculateCompleteness(int aSteps, int gSteps){
        double percentage;
        if(gSteps!=0){
            percentage = aSteps * 100.0 / gSteps;
        }else{
            percentage = 0;
        }

        String str = String.valueOf(percentage);
        int index = str.lastIndexOf(".");
        String num = str.substring(0, index);
        String result = num + "%";
        return result;
    }

}

