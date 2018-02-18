package robertfiker.eventmi;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by robfi on 2018-02-17.
 */

//handles each event and its subgroups
public class MainActivityAdapter extends BaseExpandableListAdapter {
    private Activity activity;
    private DatabaseReference database;
    private ArrayList<DataSnapshot> dataSnapshots;
    private double latitude;
    private double longitude;

    private ChildEventListener listener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if(distanceOk(dataSnapshot)) {
                dataSnapshots.add(dataSnapshot);
                notifyDataSetChanged();
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public MainActivityAdapter(Activity activity, DatabaseReference ref, double lon, double lat){
        this.activity = activity;
        database = ref.child("events");
        database.addChildEventListener(listener);
        dataSnapshots = new ArrayList<>();
        longitude = lon;
        latitude = lat;
    }

    @Override
    public int getGroupCount() {
        return dataSnapshots.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public EventInfo getGroup(int groupPosition) {
        DataSnapshot ds = dataSnapshots.get(groupPosition);
        return ds.getValue(EventInfo.class);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.event_title_layout,null);
        }

        TextView eventName = (TextView)convertView.findViewById(R.id.text_event_title);
        EventInfo event = getGroup(groupPosition);
        eventName.setText(event.getTitle());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.event_details_layout,null);
        }
        TextView eventProximity = convertView.findViewById(R.id.text_proximity);
        TextView eventDetails = convertView.findViewById(R.id.text_details);
        EventInfo event = getGroup(groupPosition);
        String sourceString = "<b>"+"Distance: "+"</b>"+Integer.toString(event.getProximity())+"m";
        eventProximity.setText(Html.fromHtml(sourceString));
        sourceString = "<b>"+"Details: "+"</b>"+event.getDetails();
        eventDetails.setText(Html.fromHtml(sourceString));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


    public boolean distanceOk(DataSnapshot ds){
        EventInfo event = ds.getValue(EventInfo.class);
        Location eventLoc = new Location("");
        eventLoc.setLongitude(event.getLongitude());
        Log.d("EventMi", "event:");
        Log.d("EventMi", Double.toString(event.getLongitude()));
        eventLoc.setLatitude(event.getLatitude());
        Log.d("EventMi", Double.toString(event.getLatitude()));

        Location myLoc = new Location("");
        myLoc.setLongitude(longitude);
        myLoc.setLatitude(latitude);

        double distance = myLoc.distanceTo(eventLoc);
        if((int)distance<=event.getProximity())return true;
        else return false;
    }


    public void cleanup(){
        database.removeEventListener(listener);
    }
}
