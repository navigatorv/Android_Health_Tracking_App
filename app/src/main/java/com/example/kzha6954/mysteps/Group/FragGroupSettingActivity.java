package com.example.kzha6954.mysteps.Group;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.kzha6954.mysteps.Models.Group;
import com.example.kzha6954.mysteps.Models.UserDaily;
import com.example.kzha6954.mysteps.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by zkd on 31-07-2016.
 */
public class FragGroupSettingActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_GROUPID_KEY = "groupId_key";
    private EditText stMotto;
    private EditText stGroupNm;
    private String TAG = "FragGroupSettingActivity";
    private FirebaseDatabase db;
    String mGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_setting);

        stGroupNm = (EditText)findViewById(R.id.activity_group_et_name_setting);
        stMotto = (EditText)findViewById(R.id.activity_group_et_setting);
        findViewById(R.id.activity_group_btn_setting).setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_me_set_profile);
        toolbar.setTitle("Group setting");
        setSupportActionBar(toolbar);

        //get User's groupId from intent
        mGroupId = getIntent().getStringExtra(EXTRA_GROUPID_KEY);


        //set up btn
        ActionBar up_multiFrame = getSupportActionBar();
        up_multiFrame.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.activity_group_btn_setting:
                writeSetting();
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGroupId == null){
            stMotto.setText("");
            stGroupNm.setText("");
        }else {
            //read from userDaily table
            ValueEventListener groupDailyListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Group group = dataSnapshot.getValue(Group.class);
                    stMotto.setText(group.getMotto());
                    stGroupNm.setText(group.getGroupName());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };
            db.getInstance().getReference("group").child(mGroupId).addValueEventListener(groupDailyListener);
        }
    }

    private void writeSetting(){
        if (mGroupId != null) {
            db.getInstance().getReference("group").child(mGroupId).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Group p = mutableData.getValue(Group.class);
                    if (p == null) {
                        Log.i(TAG, "NULL");
                        return Transaction.success(mutableData);
                    }
                    Log.i(TAG, stMotto.getText().toString());
                    p.setMotto(stMotto.getText().toString());
                    p.setGroupName(stGroupNm.getText().toString());
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
            });
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
