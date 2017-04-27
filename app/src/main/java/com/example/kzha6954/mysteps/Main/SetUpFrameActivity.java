package com.example.kzha6954.mysteps.Main;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.kzha6954.mysteps.Group.FragmentGroup;
import com.example.kzha6954.mysteps.Me.FragmentMe;
import com.example.kzha6954.mysteps.Messages.FragmentMessages;
import com.example.kzha6954.mysteps.Models.User;
import com.example.kzha6954.mysteps.Moments.FragmentMoments;
import com.example.kzha6954.mysteps.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by zkd on 08-07-2016.
 */
public class SetUpFrameActivity extends AppCompatActivity implements View.OnClickListener {

    // Initial
    private FragmentGroup fragmentGroup;
    private FragmentMe fragmentMe;
    //private FragmentMessages fragmentMessages;
    private FragmentMoments fragmentMoments;
    //private HistorylineChartActivity fragmentHistory;
    //private HistoryBarChartFragment fragmentHistory;

    private FrameLayout MomentsFl, GroupFl, MessagesFl, MeFl;

    private ImageView MomentsIv, GroupIv, MessagesIv, MeIv;

    private DisplayMetrics dm;

    private GoogleApiClient mClient = null;

    //initial userDaily table
    private GoogleApiClient mGoogleApiClient;
    private FirebaseDatabase mDatabase;
    private DatabaseReference childReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //write the user info to the database
        writeNewUser(getEmail());
        initView();
        initListener();
        clickMomentsBtn();

    }

    public String getEmail(){return FirebaseAuth.getInstance().getCurrentUser().getEmail();};
    public String getUid(){return FirebaseAuth.getInstance().getCurrentUser().getUid();};

    private void writeNewUser(String email){
        mDatabase = FirebaseDatabase.getInstance();
        childReference = mDatabase.getReference("user");
        childReference.child(getUid()).child("email").setValue(email);
    };
    /**
     * Map parameters to xml layouts
     */
    private void initView() {
        MomentsFl = (FrameLayout) findViewById(R.id.layout_moments);
        GroupFl = (FrameLayout) findViewById(R.id.layout_group);
        //MessagesFl = (FrameLayout) findViewById(R.id.layout_messages);
        MeFl = (FrameLayout) findViewById(R.id.layout_me);

        MomentsIv = (ImageView) findViewById(R.id.image_moments);
        GroupIv = (ImageView) findViewById(R.id.image_group);
        //MessagesIv = (ImageView) findViewById(R.id.image_messages);
       MeIv = (ImageView) findViewById(R.id.image_me);
    }

    /**
     * set onclick lisetner
     */
    private void initListener() {

        MomentsFl.setOnClickListener(this);
        GroupFl.setOnClickListener(this);
//        MessagesFl.setOnClickListener(this);
        MeFl.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_moments:
                clickMomentsBtn();
                break;
            case R.id.layout_group:
                clickGroupBtn();
                break;
            //case R.id.layout_messages:
             //   clickMessagesBtn();
             //   break;
            case R.id.layout_me:
                clickMeBtn();
                break;
        }
    }

    /**
     * When any one of the bottom navigation has been selected
     */
    private void clickMomentsBtn() {

        fragmentMoments = new FragmentMoments();

        FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_content, fragmentMoments);
        fragmentTransaction.commit();

        //change the img when it is selected
        MomentsFl.setSelected(true);
        MomentsIv.setSelected(true);

        GroupFl.setSelected(false);
        GroupIv.setSelected(false);
//
//        MessagesFl.setSelected(false);
//        MessagesIv.setSelected(false);

        MeFl.setSelected(false);
        MeIv.setSelected(false);
    }

    /**
     *
     */
    private void clickGroupBtn() {

        fragmentGroup = new FragmentGroup();
        FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_content, fragmentGroup);
        fragmentTransaction.commit();

        MomentsFl.setSelected(false);
        MomentsIv.setSelected(false);

        GroupFl.setSelected(true);
        GroupIv.setSelected(true);

//        MessagesFl.setSelected(false);
//        MessagesIv.setSelected(false);

        MeFl.setSelected(false);
        MeIv.setSelected(false);
    }

    /**
     *
     */
//    private void clickMessagesBtn() {
//        fragmentMessages = new FragmentMessages();
//        //fragmentHistory = new HistorylineChartActivity();
//        //fragmentHistory = new HistoryBarChartFragment();
//        FragmentTransaction fragmentTransaction = this
//                .getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.frame_content, fragmentMessages);
//        fragmentTransaction.commit();
//
//        MomentsFl.setSelected(false);
//        MomentsIv.setSelected(false);
//
//        GroupFl.setSelected(false);
//        GroupIv.setSelected(false);
//
//        MessagesFl.setSelected(true);
//        MessagesIv.setSelected(true);
//
//        MeFl.setSelected(false);
//        MeIv.setSelected(false);
//    }

    /**
     *
     */
    private void clickMeBtn() {
        fragmentMe = new FragmentMe();
        FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_content, fragmentMe);
        fragmentTransaction.commit();

        MomentsFl.setSelected(false);
        MomentsIv.setSelected(false);

        GroupFl.setSelected(false);
        GroupIv.setSelected(false);

//        MessagesFl.setSelected(false);
//        MessagesIv.setSelected(false);

        MeFl.setSelected(true);
        MeIv.setSelected(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}