package org.umd.timetracker;

import android.net.Uri;

import android.util.Log;

import android.widget.EditText;

import org.umd.timetracker.TimeTracker.ActivityColumns;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * View and modify an activity, an atomic item that represents
 * something you're doing at some point in time.
 * 
 * @author micinski
 */
public class ViewActivity extends Activity implements View.OnClickListener
{
    // Handler to update the UI and update the duration handler
    private Handler mDurationUpdateHandler = new Handler();
        
    private long mStartTime;
    private long mEndTime;
    private String mActivityName;
    private String mTags; // List of comma separated tags
    private int mId; // ID of the current activity, or -1 if it is the
		     // most recent activity
    
    private TextView mDurationView;
    private LinearLayout mTagsBox;
    private EditText mTagsEdit;
    private EditText mNameEdit;
    private TextView mActivityNameText;
    
    private static final String TAG = "org.umd.timetracker.ViewActivity";
    
    /**
     * Boolean extra telling this activity that --- upon starting ---
     * it should create a new activity and add it to the database,
     * allowing the user to update its parameters.
     */
    public static final String CREATE_NEW_ACTIVITY = "create_new";
    
    /**
     * String extra telling the activity that some activity with a
     * specified ID should be displayed.
     */
    public static final String ACTIVITY_ID = "activity_id";
    
    private static final String NEW_ACTIVITY = "New Activity!";
    
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	Intent intent = getIntent();
	setContentView(R.layout.current_activity);
	
	mTagsEdit = (EditText)findViewById(R.id.tags_input);
	mNameEdit = (EditText)findViewById(R.id.activity_input);
	mActivityNameText = (TextView)findViewById(R.id.current_activity_name);
	((Button)findViewById(R.id.switch_activity_button)).setOnClickListener(this);
	((Button)findViewById(R.id.stop_activity_button)).setOnClickListener(this);
	
	// Check to see if we should create a new entry in the
	// database.
	if (getIntent().getExtras().getBoolean(CREATE_NEW_ACTIVITY)) {
	    // We should create a new activity.  We first need to make
	    // sure that we stop tracking the current event.
	    
	    long currentTime = Calendar.getInstance().getTime().getTime();
	    ContentValues newActivity = new ContentValues();
	    mActivityName = "New Activity";
	    mStartTime = currentTime;
	    mEndTime = -1;
	    mTags = "";
	    
	    newActivity.put(ActivityColumns.ACTIVITY_NAME, mActivityName);
	    newActivity.put(ActivityColumns.ACTIVITY_START_TIME, mStartTime);
	    newActivity.put(ActivityColumns.ACTIVITY_END_TIME, mEndTime);
	    newActivity.put(ActivityColumns.ACTIVITY_TAGS, mTags);
	    
	    mId = Integer.parseInt(getContentResolver().
				   insert(ActivityColumns.CONTENT_URI,newActivity).
				   getLastPathSegment());
	} else if (getIntent().hasExtra(ACTIVITY_ID)) {
	    // We received an ID of the activity to be displayed.
	    updateViewFromId(getIntent().getExtras().getInt(ACTIVITY_ID));
	} else {
	    // Simply display the most recent activity
	} 
	
	updateViews();
    }
    
    /**
     * Update the information on all of the current views based on the
     * information in the different private fields.
     */
    private void updateViews() {
	mNameEdit.setText(mActivityName);
	mActivityNameText.setText(mActivityName);
	// Do tags
	mTagsEdit.setText(mTags);
    }
    
    /** 
     * Update the current view from an activity ID.
     * 
     * @param id The activity ID (in the activities table) of the 
     * activity to display.
     */
    private void updateViewFromId(int id) {
	mId = id;
	Uri.Builder uriBuilder = ActivityColumns.CONTENT_URI.buildUpon();
	uriBuilder.appendPath(Integer.toString(id));
	Cursor activity = this.getContentResolver().query(uriBuilder.build(),
							  null, // All columns
							  null,
							  null,
							  null); // Default sort order
	mActivityName = "";
	mTags = "";
	
	if (activity == null) {
	    Log.e(TAG, "Didn't find any information for URI" + Integer.toString(id));
	} else if (activity.getCount() < 1) {
	    Log.e(TAG, "Didn't find any information for URI" + Integer.toString(id));
	} else {
	    // Information received 
	    activity.moveToFirst();
	    mActivityName = activity.getString(activity.getColumnIndex(ActivityColumns.ACTIVITY_NAME));
	    if (mActivityName == null) { 
		Log.e(TAG, "null");
	    } 
	}
	activity.close();
    }
    
    /**
     * Update the duration inforamtion on the screen 
     */
    private void updateDuration() {
	
    }
    
    private Runnable mDurationUpdateTask = new Runnable() { 
	    public void run() {
		// Update the duration to 
		
		// Run again in 10 seconds.
		mDurationUpdateHandler.postDelayed(this, 1000 * 10);
	    }
	};
    
    /**
     * Start updating the duration.
     */
    public void onResume() {
	super.onResume();
	mDurationUpdateHandler.removeCallbacks(mDurationUpdateTask);
    }
    
    /**
     * Handle the switch or stop click.
     */
    public void onClick(View v) {
	switch(v.getId()) {
	case R.id.switch_activity_button:
	    // Update the current information
	    mActivityName = mNameEdit.getText().toString();
	    mTags = mTagsEdit.getText().toString();
	    updateEntryInDatabase();
	    updateViews();
	    break;
	case R.id.stop_activity_button:
	    // Update current information to stop tracking
	    mActivityName = mNameEdit.getText().toString();
	    mTags = mTagsEdit.getText().toString();
	    long currentTime = Calendar.getInstance().getTime().getTime();
	    mEndTime = currentTime;
	    updateEntryInDatabase();
	    updateViews();
	    break;
	}
    }
    
    /**
     * Update the database entry based on the actual contents of this
     * activity.
     */
    private void updateEntryInDatabase() {
        ContentValues values = new ContentValues();
	values.put(ActivityColumns.ACTIVITY_TAGS,mTags);
	values.put(ActivityColumns.ACTIVITY_NAME,mActivityName);
	
	int num = getContentResolver().update(ActivityColumns.CONTENT_URI,
				    values,
				    ActivityColumns._ID + "= ?",
				    new String[] {Integer.toString(mId)});
	Log.i(TAG, Integer.toString(num));
	return;
    }
    
    /**
     * Stop updates to the duration.
     */
    @Override
    public void onPause() {
	super.onPause();
	mDurationUpdateHandler.removeCallbacks(mDurationUpdateTask);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.view_activity, menu);
	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
        case R.id.items_activity:
            // app icon in action bar clicked; go home
            Intent intent = new Intent(this, ItemListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
	}
    }
}
