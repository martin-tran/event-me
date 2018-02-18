package robertfiker.eventmi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by robfi on 2018-02-18.
 */

public class GoogleSignIn extends AppCompatActivity {

    private GoogleApiClient mGoogleApiClient;
    public static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton;
    private Button buttonCreate;
    private DatabaseReference mDatabase;
    private EditText title;
    private EditText proximity;
    private EditText discord;
    private EditText website;
    private EditText details;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        latitude = getIntent().getDoubleExtra("lat", 0);
        longitude = getIntent().getDoubleExtra("lon", 0);
        Log.d("EventMi", "user's passed:");
        Log.d("EventMi", Double.toString(longitude));
        Log.d("EventMi", Double.toString(latitude));
        title = (EditText) findViewById(R.id.text_title);
        proximity = (EditText) findViewById(R.id.text_proximity);
        discord = (EditText) findViewById(R.id.text_discord);
        website = (EditText) findViewById(R.id.text_website);
        details = (EditText) findViewById(R.id.text_details);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("events");
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        buttonCreate = (Button) findViewById(R.id.create_button);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.isEnabled())createEvent();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d("EventMi", "Google Connection Failed");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void createEvent(){
        Log.d("EventMi", "creating");
        //if(fieldsOk()){
        String dis = discord.getText().toString();
        String web = website.getText().toString();
        String det = details.getText().toString();
        if(discord.getText().toString().length()<2){
            dis = " ";
        }
        if(website.getText().toString().length()<2){
            web = " ";
        }
        if(details.getText().toString().length()<2){
            det = " ";
        }
        String tit =title.getText().toString();
        int prox = Integer.parseInt(proximity.getText().toString());

        EventInfo newEvent = new EventInfo(tit, prox, latitude, longitude, dis, web, det);
        if (mDatabase.push().setValue(newEvent).isSuccessful()) Log.d("EventMi", "PUSHED INTO");
        else Log.d("EventMi", "PUSHING FAILURE");
        //}
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            LinearLayout layout = (LinearLayout) findViewById(R.id.event_input);
            for (int i = 0; i < layout.getChildCount()-1; i++) {
                View child = layout.getChildAt(i);
                child.setEnabled(true);
            }
        }
    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Log.d("EventMi", "Signed Out");
            }
        });
    }

    public void onStop(){
        super.onStop();
    }
}
