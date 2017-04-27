package com.example.kzha6954.mysteps.Main;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kzha6954.mysteps.GoogleFit.GoogleFitService;
import com.example.kzha6954.mysteps.Group.FragmentGroup;
import com.example.kzha6954.mysteps.Me.FragmentMe;
import com.example.kzha6954.mysteps.Messages.FragmentMessages;
import com.example.kzha6954.mysteps.Models.User;
import com.example.kzha6954.mysteps.Models.UserDaily;
import com.example.kzha6954.mysteps.Moments.FragmentMoments;
import com.example.kzha6954.mysteps.R;
import com.firebase.ui.database.BuildConfig;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener{

    static public int likeCountRestrictor;

    public final static String TAG = "MainActivity";
    private ConnectionResult mFitResultResolution;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private static final int REQUEST_OAUTH = 1431;
    private Button mConnectButton;
    private Button mGetStepsButton;
    private Button mMonthStepsButton;
    private Button mSkipButton;
    private TextView intro;

    private String INTRODUCTION ="MySteps uses Google Fit to track your progress. Connecting MySteps to Google Fit allows you to compete your steps walked with your teammates.";

    //firebase auth
    private GoogleApiClient mGoogleApiClient;
    // AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA-------Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    public static final String ANONYMOUS = "anonymous";

    //write actual step to firebase database
    FirebaseDatabase db;
    DatabaseReference dbRef;
    private String actual_steps;

    //Remote Config
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private long cacheExpiration = 10 ;
    private String RemoteConfig_groupGoalSteps;
    private String RemoteConfig_personalGoalSteps;

    private int DailyStepsStatusCode;
    private int MonthStepsStatusCode;

    PendingIntent pendingIntent;

    private String lastActive = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        DailyStepsStatusCode = 0;
        MonthStepsStatusCode = 0;
        likeCountRestrictor = 0;

        // AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA-------Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        //Set Up google Remote config
        intro = (TextView)findViewById(R.id.Introduction);
        intro.setText(INTRODUCTION);

        mConnectButton = (Button)findViewById(R.id.btnConnectToFit);
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleConnectButton();
            }
        });

        //mGetStepsButton = (Button)findViewById(R.id.btnGetSteps);
//        mGetStepsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                handleGetStepsButton();
//            }
//        });

//        mMonthStepsButton = (Button)findViewById(R.id.btnMonthSteps);
//        mMonthStepsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MonthStepsButton();
//            }
//        });

        mSkipButton = (Button)findViewById(R.id.btnSkip);
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfOtherExists();
            }
        });

        //Start disabled, enable later if we're not connected
        mConnectButton.setEnabled(false);
//        mGetStepsButton.setEnabled(false);
//        mMonthStepsButton.setEnabled(false);
        mSkipButton.setEnabled(false);

        LocalBroadcastManager.getInstance(this).registerReceiver(mFitStatusReceiver, new IntentFilter(GoogleFitService.FIT_NOTIFY_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(mFitDataReceiver, new IntentFilter(GoogleFitService.HISTORY_INTENT));

        requestFitConnection();

        //read Firebase Remote Config
        //set up  google REMOTE CONFIG VALUE
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        Log.w(TAG, "Get String");
        fetchAndInitiateRemoteConfigData();


        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()){
            db.getInstance().getReference("user").child(getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            User user = dataSnapshot.getValue(User.class);
                            if (user.lastActiveDate !=null){
                                lastActive = user.lastActiveDate;
                            }else if (user.lastActiveDate ==null){//this field not exists
                                lastActive = "empty";
                            }
                            //wifi is enabled
                            getRawData();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });


            Log.e(TAG, "Wifi Connected");
        }


        //mGetStepsButton.setEnabled(true);
        //mMonthStepsButton.setEnabled(true);
        //mSkipButton.setEnabled(true);
    }

    public void fetchAndInitiateRemoteConfigData(){
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.w(TAG, "Fetch Success");
                            // Once the config is successfully fetched it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Log.w(TAG, "Fetch Failed. Use default value");
                        }
                        RemoteConfig_groupGoalSteps = mFirebaseRemoteConfig.getString("groupGoalSteps");
                        RemoteConfig_personalGoalSteps = mFirebaseRemoteConfig.getString("personalGoalSteps");
                        Map<String,Object> resXml = new HashMap<String, Object>();
                        resXml.put("groupGoalSteps",RemoteConfig_groupGoalSteps);
                        resXml.put("groupGoalSteps",RemoteConfig_personalGoalSteps);
                        //fetch
                        mFirebaseRemoteConfig.setDefaults (resXml);
                        Log.w("test", "No "+ RemoteConfig_groupGoalSteps);
                        Log.w("test", "No "+ RemoteConfig_personalGoalSteps);
                    }
                });
    }


    private void getRawData(){
        //Start Service and wait for broadcast
        Intent service = new Intent(this, GoogleFitService.class);
        service.putExtra(GoogleFitService.SERVICE_REQUEST_TYPE, GoogleFitService. TYPE_GET_RAW_STEP_MONTH_DATA);
        service.putExtra("lastActiveDate", lastActive );
        startService(service);
    }



    private void checkIfOtherExists() {
        startActivity(new Intent(this,SetUpFrameActivity.class));
    }



    private void handleConnectButton() {
        try {
            authInProgress = true;
            mFitResultResolution.startResolutionForResult(MainActivity.this, REQUEST_OAUTH);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG,
                    "Activity Thread Google Fit Exception while starting resolution activity", e);
        }
    }

    private void handleGetStepsButton() {
        //Start Service and wait for broadcast
        Intent service = new Intent(this, GoogleFitService.class);
        service.putExtra(GoogleFitService.SERVICE_REQUEST_TYPE, GoogleFitService.TYPE_GET_STEP_TODAY_DATA);
        startService(service);
    }

    private void MonthStepsButton() {
        //Start Service and wait for broadcast
        Intent service = new Intent(this, GoogleFitService.class);
        service.putExtra(GoogleFitService.SERVICE_REQUEST_TYPE, GoogleFitService.TYPE_GET_STEP_MONTH_DATA);
        startService(service);
    }

    private void requestFitConnection() {
        Intent service = new Intent(this, GoogleFitService.class);
        service.putExtra(GoogleFitService.SERVICE_REQUEST_TYPE, GoogleFitService.TYPE_REQUEST_CONNECTION);
        startService(service);
    }

    private BroadcastReceiver mFitStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent.hasExtra(GoogleFitService.FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE) &&
                    intent.hasExtra(GoogleFitService.FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE)) {
                //Recreate the connection result
                int statusCode = intent.getIntExtra(GoogleFitService.FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE, 0);
                PendingIntent pendingIntent = intent.getParcelableExtra(GoogleFitService.FIT_EXTRA_NOTIFY_FAILED_INTENT);
                ConnectionResult result = new ConnectionResult(statusCode, pendingIntent);
                Log.d(TAG, "Fit connection failed - opening connect screen.");
                fitHandleFailedConnection(result);
            }
            if (intent.hasExtra(GoogleFitService.FIT_EXTRA_CONNECTION_MESSAGE)) {
                Log.d(TAG, "Fit connection successful - closing connect screen if it's open.");
                fitHandleConnection();
            }
        }
    };

    //This would typically go in your fragment.
    private BroadcastReceiver mFitDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent.hasExtra(GoogleFitService.HISTORY_EXTRA_STEPS_TODAY)) {
                int total= intent.getIntExtra(GoogleFitService.HISTORY_EXTRA_STEPS_TODAY, 0);
                actual_steps = String.valueOf(total);
                save();
                //Toast.makeText(MainActivity.this, "Total Steps: " + actual_steps, Toast.LENGTH_SHORT).show();
                DailyStepsStatusCode++;
                // All Data for initatiate the view have been fetched
                if (DailyStepsStatusCode > 0) {
//                    mConnectButton.setEnabled(false);
//                    mGetStepsButton.setEnabled(true);
//                    mMonthStepsButton.setEnabled(true);
//                    mSkipButton.setEnabled(true);
                }
            }
        }
    };

    public String getUid(){return FirebaseAuth.getInstance().getCurrentUser().getUid();};
    public String getDate(){
        SimpleDateFormat myFmt1=new SimpleDateFormat("dd-MM-yyyy");
        //DateFormat dateFormat = DateFormat.getDateInstance();
        String date = myFmt1.format(new    java.util.Date());
        Log.e("getDate", date);
        return date;
    };

    private void save(){
//        SharedPreferences preferences = this.getSharedPreferences("UserDynamic", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("ActualSteps", String.valueOf(actualSteps));
//        editor.commit();
        db = FirebaseDatabase.getInstance();
        final String keyForUserDaily =getUid()+"="+getDate();
        Log.w("Config", "No "+ getDate());
        //db.getReference("userDaily").child(keyForUserDaily).child("actualSteps").setValue(Integer.toString(p.actualSteps = Integer.toString(actual_steps);));


        db.getInstance().getReference("userDaily").child(keyForUserDaily).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                UserDaily p = mutableData.getValue(UserDaily.class);
                if (p == null) {
                    p = new UserDaily();
                    p.likeCount = 0;
                    p.date = getDate();
                    p.goalSteps = RemoteConfig_personalGoalSteps;
                    Log.w("Config", "No "+ RemoteConfig_personalGoalSteps);
                    p.actualSteps = actual_steps;
                    Log.w("Config", "In trasaction:establish self" + "transaction success");
                    mutableData.setValue(p);
                    return Transaction.success(mutableData);
                }

                p.actualSteps = actual_steps;
                p.goalSteps = RemoteConfig_personalGoalSteps;
                Log.w("Config", "personal goal steps " + p.goalSteps );
                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("Config", "postTransaction:onComplete:" + databaseError);
                mConnectButton.setEnabled(false);
//                mGetStepsButton.setEnabled(true);
//                mMonthStepsButton.setEnabled(true);
                if (databaseError == null){
                    mSkipButton.setEnabled(true);
                }else{
                    Toast.makeText(MainActivity.this,databaseError.toString(),Toast.LENGTH_LONG).show();
                }
            }
        });;

//        db.getReference("userDaily").child(keyForUserDaily).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        UserDaily u = dataSnapshot.getValue(UserDaily.class);
//                        if (!dataSnapshot.hasChild("likeCount")){
//                            db.getReference("userDaily").child(keyForUserDaily).child("likeCount").setValue(0);
//                        }
//                        db.getReference("userDaily").child(keyForUserDaily).child("date").setValue(getDate());
//                        db.getReference("userDaily").child(keyForUserDaily).child("goalSteps").setValue(RemoteConfig_personalGoalSteps);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        // Getting Post failed, log a message
//                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                    }
//                });
    }

    private void fitHandleConnection() {
        Toast.makeText(this, "Fit connected", Toast.LENGTH_SHORT).show();
        handleGetStepsButton();
        MonthStepsButton();
        //startActivity(new Intent(this,SetUpFrameActivity.class));
    }

    private void fitHandleFailedConnection(ConnectionResult result) {
        Log.i(TAG, "Activity Thread Google Fit Connection failed. Cause: " + result.toString());
        if (!result.hasResolution()) {
            // Show the localized error dialog
            GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, result.getErrorCode(),  0).show();
            return;
        }

        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an authorization dialog is displayed to the user.
        if (!authInProgress) {
            if (result.getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
                try {
                    Log.d(TAG, "Google Fit connection failed with OAuth failure.  Trying to ask for consent (again)");
                    result.startResolutionForResult(MainActivity.this, REQUEST_OAUTH);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Activity Thread Google Fit Exception while starting resolution activity", e);
                }
            } else {

                Log.i(TAG, "Activity Thread Google Fit Attempting to resolve failed connection");

                mFitResultResolution = result;
                mConnectButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fitSaveInstanceState(outState);
    }

    private void fitSaveInstanceState(Bundle outState) {
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fitActivityResult(requestCode, resultCode);
    }

    private void fitActivityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == Activity.RESULT_OK) {
                //Ask the service to reconnect.
                Log.d(TAG, "Fit auth completed.  Asking for reconnect.");
                requestFitConnection();

            } else {
                try {
                    authInProgress = true;
                    mFitResultResolution.startResolutionForResult(MainActivity.this, REQUEST_OAUTH);

                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG,
                            "Activity Thread Google Fit Exception while starting resolution activity", e);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mFitStatusReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mFitDataReceiver);

        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


}