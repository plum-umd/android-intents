package org.umd.timetracker;

import org.umd.timetracker.TimeTracker.ActivityColumns;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Provides access to a database of time indexed data, the actual set of columns
 * is provided in {@link TimeTracker.ActivityColumns}.
 * 
 * Currently implemented using an SQLite database.
 */
public class TimeActivityProvider extends ContentProvider {

    private static final String TAG = "TimeActivityProvider";

    private static final String DATABASE_NAME = "activities.db";
    private static final int DATABASE_VERSION = 1;
    private static final String ACTIVITIES_TABLE_NAME = "activities";

    private static final UriMatcher sUriMatcher;

    private DatabaseHelper mOpenHelper;

    private static final int ACTIVITIES = 1;
    private static final int ACTIVITY_ID = 2;
    
	
    /**
     * Helper class to allow opening the database and updating across
     * install.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
	DatabaseHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/**
	 * Actually create the daabase
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
	    Log.i(TAG, "Creating database table now");
	    db.execSQL("CREATE TABLE " + ACTIVITIES_TABLE_NAME + " ("
		       + ActivityColumns._ID + " INTEGER PRIMARY KEY,"
		       + ActivityColumns.ACTIVITY_END_TIME
		       + " INTEGER," + ActivityColumns.ACTIVITY_NAME
		       + " VARCHAR(200),"
		       + ActivityColumns.ACTIVITY_TAGS + " TEXT,"
		       + ActivityColumns.ACTIVITY_NOTE + " TEXT,"
		       + ActivityColumns.ACTIVITY_START_TIME
		       + " INTEGER" + ");");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(TAG, "Upgrading database...");
	    db.execSQL("DROP TABLE IF EXISTS notes");
	    onCreate(db);
	}
    }
    
    /**
     * Delete some content from the database.
     */
    @Override
    public synchronized int delete(Uri arg0, String arg1, String[] arg2) {
	return 0;
    }

    @Override
    public String getType(Uri uri) {
	// TODO Auto-generated method stub
	return null;
    }
    
    /**
     * Insert a new activity in the database.
     *  
     * For now we only 
     * 
     * @param uri The URI into which the content should be inserted.  Currently 
     * we only support adding an activity into the database using the ACTIVITIES
     * type (it doesn't make any sense to add content to a activity with ID).
     * @param values The ContentValues key/value store for values which should 
     * be inserted in the database.
     */
    @Override
    public synchronized Uri insert(Uri uri, ContentValues values) {
	int uriType = sUriMatcher.match(uri);
	SQLiteDatabase db = mOpenHelper.getWritableDatabase();
	int rowsDeleted = 0;
	long id = 0;
	switch (uriType) {
	case ACTIVITIES:
	    id = db.insert(ACTIVITIES_TABLE_NAME, null, values);
	    break;
	default:
	    throw new IllegalArgumentException("Unknown URI: " + uri);
	}
	getContext().getContentResolver().notifyChange(uri, null);
	return Uri.parse(TimeTracker.ActivityColumns.ACTIVITIES_TABLE + "/" + id);
    }
    
    @Override
    public boolean onCreate() {
	mOpenHelper = new DatabaseHelper(getContext());
	return false;
    }
    
    private void checkColumns(String[] columns) {
	return;
    }
    
    /**
     * Query the content provider to extract a certain amount of
     * content.  Most of the time we will want to query activities and
     * then have them ordered by most recent events.  (E.g., for
     * displaying them in a view of activities.)
     * 
     * @param uri The URI type we want to query
     * @param projection The project of columns we want to view
     * @param selection The selection of items from the database
     * @param selectionArgs Strings filling in wildcards in the query string.
     * @param sortOrder The order in which to sort colums
     */
    @Override
    public synchronized Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	checkColumns(projection);
	queryBuilder.setTables(ACTIVITIES_TABLE_NAME);
	
	int uriType = sUriMatcher.match(uri);
	int activity;

	switch (uriType) {
	case ACTIVITY_ID:
	    queryBuilder.appendWhere(ActivityColumns._ID + "=" 
				     + uri.getLastPathSegment());
	    break;
	case ACTIVITIES:
	    // Get all of the activities
	    break;
	default:
	    throw new IllegalArgumentException("Unknown URI:" + uri);
	}
		
	SQLiteDatabase db = mOpenHelper.getWritableDatabase();
	Cursor cursor = queryBuilder.query(db, projection, selection,
					   selectionArgs, null, null,
					   (sortOrder == null) ?
					   ActivityColumns.DEFAULT_SORT_ORDER :
					   sortOrder);
	return cursor;
    }
    
    /**
     * Update information in the database.
     * 
     * For example, when we want to change and adjust times of
     * activities we use this.  Note that this method does not enforce
     * any kind of semantics to check that activity orderings are well
     * defined.
     */
    @Override
    public synchronized int update(Uri uri, ContentValues values, String selection,
		      String[] selectionArgs) {
	int uriType = sUriMatcher.match(uri);
	SQLiteDatabase db = mOpenHelper.getWritableDatabase();
	int updated = 0;
	switch (uriType) {
	case ACTIVITIES:
	    updated = db.update(ACTIVITIES_TABLE_NAME, values, selection, selectionArgs);
	    break;
	default:
	    throw new IllegalArgumentException("Unknown URI: " + uri);
	}
	getContext().getContentResolver().notifyChange(uri, null);
	return updated;
    }
    
    // Define sets of URI / macher pairs.
    static {
	sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	sUriMatcher.addURI(TimeTracker.AUTHORITY, "activities", ACTIVITIES);
	sUriMatcher.addURI(TimeTracker.AUTHORITY, "activities/#", ACTIVITY_ID);
    }
}
