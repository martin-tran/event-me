package robertfiker.eventmi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private ExpandableListView eventListView;
    private DatabaseReference database;
    private MainActivityAdapter adapter;
    private double mLatitude;
    private double mLongitude;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private final int REQUEST_CODE = 123;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance().getReference();
        eventListView = (ExpandableListView) findViewById(R.id.expandable_list);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        getLocation();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GoogleSignIn.class);
                intent.putExtra("lat", mLatitude);
                intent.putExtra("lon", mLongitude);
                Log.d("EventMi", Double.toString(mLongitude));
                Log.d("EventMi", Double.toString(mLatitude));
                startActivity(intent);
            }
        });
    }

    private void getLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLongitude = location.getLongitude();
                mLatitude = location.getLatitude();
                adapter = new MainActivityAdapter(MainActivity.this, database, mLongitude, mLatitude);
                eventListView.setAdapter(adapter);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, REQUEST_CODE);
            return;
        }
//        Location location = mLocationManager.getLastKnownLocation(mLocationManager.toString());
//        mLongitude = location.getLongitude();
//        Log.d("TestChat", "longitude: "+mLongitude);
//        mLatitude = location.getLatitude();
//        mAltitude = location.getAltitude();
//        mRoomsListAdapter = new RoomsListAdapter(RoomsListActivity.this, mDatabaseReference, mLongitude, mLatitude, mAltitude);
//        mListView.setAdapter(mRoomsListAdapter);
        mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, mLocationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation();
            }else{
                finish();
                System.exit(0);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onStart(){
        super.onStart();
    }

    public void onStop(){
        super.onStop();
        if(adapter!=null) adapter.cleanup();
    }
}
