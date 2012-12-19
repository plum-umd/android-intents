package org.umd.timetracker;




import org.umd.timetracker.TimeTracker.ActivityColumns;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.Period;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * View and modify an activity, an atomic item that represents
 * something you're doing at some point in time.
 * 
 * @author micinski
 */
public class ViewActivity extends Activity 
    implements View.OnClickListener, 
	       ViewSwitcher.ViewFactory
{
    // Handler to update the UI and update the duration handler
    private Handler mDurationUpdateHandler = new Handler();
        
    private DateTime mStartTime;
    private DateTime mEndTime; // And end time of null indicates that
			       // this is the present activity.
    
    private String mActivityName;
    private String mTags; // List of comma separated tags
    private int mId = -1; // ID of the current activity, or -1 if it is the
                          // most recent activity
    
    private LinearLayout mTagsBox;
    private EditText mTagsEdit;
    private EditText mNameEdit;
    private EditText mCategoryEdit;
    private TextView mActivityNameText;
    private TextView mTagsDescText;
    private TextView mCategoryText;
    private TextSwitcher mActivityDurationText;
    
    private static final String TAG = "org.umd.timetracker.ViewActivity";
    
    private static final long UPDATE_INTERVAL = 10 * 1000;
    
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
	mCategoryEdit = (EditText)findViewById(R.id.category_input);
	mActivityNameText = (TextView)findViewById(R.id.current_activity_name);
	mTagsDescText = (TextView)findViewById(R.id.current_activity_tag_list);
	mCategoryText = (TextView)findViewById(R.id.current_category_name);
	mTagsBox = (LinearLayout)findViewById(R.id.tag_set);
	mActivityDurationText = (TextSwitcher)findViewById(R.id.current_activity_duration);
	((Button)findViewById(R.id.switch_activity_button)).setOnClickListener(this);
	((Button)findViewById(R.id.stop_activity_button)).setOnClickListener(this);
	// Stuff to make the duration view animate
	mActivityDurationText.setFactory(this);
        Animation in = AnimationUtils.loadAnimation(this,  android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        mActivityDurationText.setInAnimation(in);
        mActivityDurationText.setOutAnimation(out);
	
	// Check to see if this is a result of a configuration change
	if (true) {//savedInstanceState == null) {
	    // Check to see if we should create a new entry in the
	    // database.
	    if (intent.getExtras().getBoolean(CREATE_NEW_ACTIVITY)) {
		createNewActivity(getIntent());
	    } else if (getIntent().hasExtra(ACTIVITY_ID)) {
		// We received an ID of the activity to be displayed.
		updateViewFromId(getIntent().getExtras().getInt(ACTIVITY_ID));
	    } else {
		// Simply display the most recent activity
	    }
	} else {
	    TimeTracker._assert(mId > 0, 
				"mId is not initialized but bundle indicates "
				+ "that screen has been set up.");
	}
	return;
    }
    
    /**
     * Make the view for a new TextView, implementing the {@link
     * ViewSwitcher.ViewFactory} interface.
     */
    public View makeView() {
        TextView t = new TextView(this);
        t.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
        t.setTextSize(30);
	t.setTypeface(Typeface.DEFAULT_BOLD);
        t.setTextColor(Color.BLUE);
        return t;
    }
    
    private void createNewActivity(Intent intent) {
	// In the case that there is a currently active activity,
	// we should end it.
	Cursor curActivity = this.getContentResolver().query(ActivityColumns.CONTENT_URI,
							     new String[]{ActivityColumns._ID},
							     ActivityColumns.ACTIVITY_END_TIME + " IS NULL",
							     null,
							     null); // Default sort order
	    
	if (curActivity.getCount() < 1) {
	    // Nothing to be done..
	} else if (curActivity.getCount() == 1) {
	    // Close the current activity and set the ending time
	    // to the current time.
	    curActivity.moveToFirst();
	    ContentValues updateValues = new ContentValues();
	    updateValues.put(ActivityColumns.ACTIVITY_END_TIME, (new DateTime()).toString());
	    this.getContentResolver().update(ActivityColumns.CONTENT_URI,
					     updateValues,
					     ActivityColumns._ID + "= ?",
					     new String[]{Integer.toString(curActivity.getInt(0))});
	} else {
	    // Error! The database is an unsound state, and should
	    // be triggered appropriately.
	    TimeTracker._assert(false,"Database has two active activities at once");
	}
	    
	curActivity.close();
	    
	// We should create a new activity.  We first need to make
	// sure that we stop tracking the current event.
	DateTime currentTime = new DateTime();
	ContentValues newActivity = new ContentValues();
	mActivityName = "New Activity";
	mStartTime = currentTime;
	mEndTime = null;
	mTags = "";
	    
	// Don't put anything for end time, it's simply null,
	// indicating that the end of the activity has been
	// reached.
	newActivity.put(ActivityColumns.ACTIVITY_NAME, mActivityName);
	newActivity.put(ActivityColumns.ACTIVITY_START_TIME, mStartTime.toString());
	newActivity.put(ActivityColumns.ACTIVITY_TAGS, mTags);
	    
	// Insert values for the most recent activity.
	mId = Integer.parseInt(getContentResolver().
			       insert(ActivityColumns.CONTENT_URI,newActivity).
			       getLastPathSegment());
	return;
    }
    
    /**
     * Update the information on all of the current views based on the
     * information in the different private fields.
     */
    private void updateViews() {
	TimeTracker._assert(mActivityName != null, "null activity name");
	String[] act = mActivityName.split(TimeTracker.CATEGORY_SEPARATOR);
	mNameEdit.setText(act[0]);
	mActivityNameText.setText(act[0]);
	if (act.length > 1) {
	    // Simply use the first separator as the category
	    // separator.
	    mCategoryEdit.setText(act[1]);
	    mCategoryText.setText(act[1]);
	}
	updateTags();
	updateDuration();
    }
    
    /**
     * Update the tags view of the activity, using the designated tags
     * layout.
     */
    private void updateTags() {
	mTagsBox.removeAllViews();
	String[] tags = mTags.split(TimeTracker.TAG_SEPARATOR);
	
	if (tags.length == 0) {
	    mTagsDescText.setText("No tags");
	} else {
	    mTagsDescText.setText("");
	}
	
	for (int i = 0; i < tags.length; i++) {
	    TextView newTag = new TextView(this);
	    newTag.setBackgroundResource(R.layout.tag_background);
	    newTag.setText(tags[i]);
	    LinearLayout.LayoutParams margins = 
		new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 
					      LayoutParams.WRAP_CONTENT);
	    margins.setMargins(10,8,10,0);
	    newTag.setLayoutParams(margins);
	    newTag.setPadding(4,4,4,4);
	    newTag.setTypeface(Typeface.DEFAULT_BOLD);
	    mTagsBox.addView(newTag);
	}
	
	return;
    }
    
    /**
     * Check whether or not this is the user's current activity.
     * 
     * @return true iff this activity is the user's current activity.
     */
    private boolean isCurrentActivity() {
	return mEndTime == null;
    }
    
    /**
     * Get the duration of the currently lasting activity. 
     */
    private Period getCurrentDuration() {
	if (isCurrentActivity()) {
	    return (new Interval(mStartTime, new DateTime())).toPeriod();
	} else {
	    return  (new Interval(mStartTime, mEndTime)).toPeriod();
	}
    }
    
    /**
     * Update the duration inforamtion on the screen.
     */
    private void updateDuration() {
	mActivityDurationText.
	    setText(TimeTracker.convertDurationToString(this, getCurrentDuration()));
	return;
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
	    mActivityName = activity.getString(
			      activity.getColumnIndex(
				ActivityColumns.ACTIVITY_NAME));
	    mStartTime = 
		DateTime.parse(
		  activity.getString(activity.getColumnIndex(
				       ActivityColumns.ACTIVITY_START_TIME)));
	    mEndTime = 
		DateTime.parse(
                  activity.getString(activity.getColumnIndex(
				       ActivityColumns.ACTIVITY_END_TIME)));
	    mTags = 
		activity.getString(activity.getColumnIndex(ActivityColumns.ACTIVITY_TAGS));
		  
	    if (mActivityName == null) { 
		Log.e(TAG, "");
	    } 
	}
	activity.close();
    }
    
    // This uses a sort of arbitrary back off strategy
    private Runnable mDurationUpdateTask = new Runnable() { 
	    public void run() {
		updateDuration();
		if (getCurrentDuration().getMinutes() < 1) {
		    mDurationUpdateHandler.postDelayed(this, getCurrentDuration().getSeconds() * 500);
		} else {
		    mDurationUpdateHandler.postDelayed(this, 60 * 500);
		}
	    }
	};
    
    /**
     * Start updating the duration.
     */
    public void onResume() {
	super.onResume();
	// Start updating the duration of the activity if this is the
	// current activity.
	if (isCurrentActivity()) {
	    mDurationUpdateHandler.postDelayed(mDurationUpdateTask, 1000);
	}
    }
    
    /**
     * Handle the switch or stop click.
     *
     * @param v The View that was clicked.
     */
    public void onClick(View v) {
	switch(v.getId()) {
	case R.id.switch_activity_button:
	    // Update the current information
	    mActivityName = mNameEdit.getText().toString();
	    if (mActivityName.split(TimeTracker.CATEGORY_SEPARATOR).length > 1) {
		// The user specified their category in the string...
		// do nothing
	    } else {
		// Else form the name from appending the string with
		// the category
		mActivityName += 
		    TimeTracker.CATEGORY_SEPARATOR + 
		    mCategoryEdit.getText().toString();
	    }
	    mTags = mTagsEdit.getText().toString();
	    updateEntryInDatabase();
	    updateViews();
	    break;
	case R.id.stop_activity_button:
	    // Update current information to stop tracking
	    mActivityName = mNameEdit.getText().toString();
	    mTags = mTagsEdit.getText().toString();
	    mEndTime = new DateTime();
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
