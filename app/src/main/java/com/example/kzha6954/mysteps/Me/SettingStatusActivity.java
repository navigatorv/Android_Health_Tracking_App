package com.example.kzha6954.mysteps.Me;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.kzha6954.mysteps.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by kzha6954 on 3/07/2016.
 * note:
 */
public class SettingStatusActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etStatus;
    private String TAG = "setUsernameActivity";
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_me_status);

        etStatus = (EditText)findViewById(R.id.fragment_me_et_targetSteps);
        findViewById(R.id.fragment_me_btn_setSteps).setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_me_set_profile);
        toolbar.setTitle("Set Status");
        setSupportActionBar(toolbar);


        //set up btn
        ActionBar up_multiFrame = getSupportActionBar();
        up_multiFrame.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.fragment_me_btn_setSteps:
                writeGoalSteps();
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public String getEmail(){return FirebaseAuth.getInstance().getCurrentUser().getEmail();};
    public String getUid(){return FirebaseAuth.getInstance().getCurrentUser().getUid();};


    private void writeGoalSteps(){
        db.getInstance().getReference("user").child(getUid()).child("status").setValue(etStatus.getText().toString());
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
