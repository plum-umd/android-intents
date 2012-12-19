 package org.umd.timetracker;

import android.util.Log;

import org.umd.timetracker.TimeTracker.ActivityColumns.*;
import org.umd.timetracker.TimeTracker.*;
import org.joda.time.DateTime;

import android.view.MenuItem;
import android.content.Intent;
import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;

/**
 * View the set of activities that the user has performed, allowing
 * them to select and edit any activity in the database.
 * 
 * @author micinski
 */
public class ItemListActivity extends ListActivity 
    implements LoaderManager.LoaderCallbacks<Cursor>,
	       SimpleCursorAdapter.ViewBinder
{
    // Adapter to hold list data.
    private SimpleCursorAdapter mAdapter;
    
    static final String[] ACTIVITIES_PROJECTION = new String[] {
	TimeTracker.ActivityColumns.ACTIVITY_NAME,
	TimeTracker.ActivityColumns.ACTIVITY_START_TIME,
	TimeTracker.ActivityColumns.ACTIVITY_END_TIME,
	TimeTracker.ActivityColumns._ID
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
	mAdapter = new SimpleCursorAdapter(this,
					   R.layout.activity_list_item,null
					   ,ACTIVITIES_PROJECTION
					   ,new int[] { R.id.list_activity_name,
							R.id.list_activity_category,
							R.id.list_activity_time,
							R.id.list_activity_time }
					   ,0);
	
	// Set the view binder to fill in the fields of the list
	mAdapter.setViewBinder(this);
	setListAdapter(mAdapter);
	getLoaderManager().initLoader(0, null, this);
    }
    
    /**
     * Draw the activity, category, and duration as appropriate for
     * the adapter on {@link SimpleCursorAdapter}.
     * 
     * @param view The view to fill in.
     * @param cursor The cursor with the appropriate row.
     * @param columnInde The column which should be filled in.
     */
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
	Log.e("ItemListActivity", "here!!!");
	switch (columnIndex) {
	case 0:
	    {
		// The activity name
		String activityName[] = 
		    cursor.getString(cursor.getColumnIndex(ActivityColumns.ACTIVITY_NAME)).
		    split(TimeTracker.CATEGORY_SEPARATOR);
		((TextView)view).setText(activityName[0]);
		break;
	    }
	case 1:
	    {
		String activityName[] = 
		    cursor.getString(cursor.getColumnIndex(ActivityColumns.ACTIVITY_NAME)).
		    split(TimeTracker.CATEGORY_SEPARATOR);
		if (activityName.length > 1) {
		    ((TextView)view).setText(activityName[1]);
		}
		break;
	    }
	case 2:
	    {
		DateTime startTimeDt = 
		    DateTime.parse(cursor.
				   getString(cursor.getColumnIndex(ActivityColumns.ACTIVITY_START_TIME)));
		String startTime = TimeTracker.convertTimeToString(this, startTimeDt);
		String endTime = 
		    cursor.getString(cursor.getColumnIndex(ActivityColumns.ACTIVITY_END_TIME));
		String timeValue = null;
		if (endTime == null) {
		    timeValue = startTime + " - now";
		} else {
		    timeValue = startTime + " - " 
			+ TimeTracker.convertTimeToString(this,DateTime.parse(endTime));
		}
		((TextView)view).setText(timeValue);
	    }
	default:
	    break;
	}
	return true;
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
	// app icon in action bar clicked; go home
	Intent intent = new Intent(this, ViewActivity.class);
	// Start a new activity for the user
	intent.putExtra(ViewActivity.ACTIVITY_ID, (int)l.getItemIdAtPosition(position));
	startActivity(intent);
	return;
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
	return new CursorLoader(this, TimeTracker.ActivityColumns.CONTENT_URI,
				ACTIVITIES_PROJECTION, null, null,
				null); // Choose default sorting order
    }
    
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
	mAdapter.swapCursor(data);
    }

    /**
     * Reset the loader to update new different kinds of categories of
     * text.
     */
    @Override
    public void onResume() {
	super.onResume();
	getLoaderManager().restartLoader(0, null, this);
    }
    
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
	mAdapter.swapCursor(null);
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it
	// is present.
	getMenuInflater().inflate(R.menu.items_activity, menu);
	return true;
    }
            
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
        case R.id.switch_activity:
	    {
		// app icon in action bar clicked; go home
		Intent intent = new Intent(this, ViewActivity.class);
		// Start a new activity for the user
		intent.putExtra(ViewActivity.CREATE_NEW_ACTIVITY, true);
		startActivity(intent);
		return true;
	    }
	case R.id.prefs:
	    {
		Intent intent = new Intent(this, PreferencesActivity.class);
		startActivity(intent);
		return true;
	    }
	case R.id.backup_activity:
	    {
		Intent intent = new Intent(this, BackupActivity.class);
		startActivity(intent);
		return true;
	    }
        default:
            return super.onOptionsItemSelected(item);
	}
    }
    
}
