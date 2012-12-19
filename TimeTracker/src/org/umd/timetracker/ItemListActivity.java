 package org.umd.timetracker;

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
import android.view.View;

/**
 * View the set of activities that the user has performed, allowing
 * them to select and edit any activity in the database.
 * 
 * @author micinski
 */
public class ItemListActivity extends ListActivity 
  implements LoaderManager.LoaderCallbacks<Cursor> {
    
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
					   android.R.layout.simple_list_item_1, null,
					   new String[] { TimeTracker.ActivityColumns.ACTIVITY_NAME },
					   new int[] { android.R.id.text1 }, 0);
	setListAdapter(mAdapter);
	getLoaderManager().initLoader(0, null, this);
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
