package com.example.kzha6954.mysteps.Me;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kzha6954.mysteps.Main.MainActivity;
import com.example.kzha6954.mysteps.Models.User;
import com.example.kzha6954.mysteps.Models.UserDaily;
import com.example.kzha6954.mysteps.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by zkd on 29-07-2016.
 */
public class SetUsernameActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etSetUsername;
    private String TAG = "setUsernameActivity";
    private FirebaseDatabase db;
    public static int i =1;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_me_set_username);

        if (i == 0) {
            findViewById(R.id.fragment_me_setName_btn).setEnabled(false);
        }
        etSetUsername = (EditText)findViewById(R.id.fragment_me_setName_tv);
        findViewById(R.id.fragment_me_setName_btn).setOnClickListener(this);
        read();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_me_set_profile);
        toolbar.setTitle("Set Username");
        setSupportActionBar(toolbar);

        //set up btn
        ActionBar up_multiFrame = getSupportActionBar();
        up_multiFrame.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                    etSetUsername.setText(user.getUsername());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        db.getInstance().getReference("user").child(getUid()).addValueEventListener(userListener);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.fragment_me_setName_btn:
                AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle("Save this username")
                        .setMessage("Sure to save this username")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                findViewById(R.id.fragment_me_setName_btn).setEnabled(false);
                                writeUsername();
                                SetUsernameActivity.i = 0;
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.create().show();
                break;
            default:
                break;
        }
    }

    public String getEmail(){return FirebaseAuth.getInstance().getCurrentUser().getEmail();};
    public String getUid(){return FirebaseAuth.getInstance().getCurrentUser().getUid();};

    /**
     * 读取数据，显示
     */
    private void read(){
//        String username = db.getInstance().getReference("user").child(getUid()).child("username").toString();;
//        etSetUsername.setText(username);
    }

    private void writeUsername(){
        db.getInstance().getReference("user").child(getUid()).child("username").setValue(etSetUsername.getText().toString());
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
