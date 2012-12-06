package org.umd.timetracker;

import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;

/**
 * Utility class used for describing different pieces of the app, e.g., 
 * columns used in the activity database. 
 * 
 * @author micinski
 *
 */
public class TimeTracker
{
	public static final String AUTHORITY = "org.umd.timetracker.provider.Activities";
	
	private TimeTracker() { }	
	
	/**
	 * Columns to be kept in the activities table.
	 */
	public static final class ActivityColumns implements BaseColumns {
		private ActivityColumns() { }
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/activities");
		
		/**
		 * By default we return results for the most recently started activity.
		 */
		public static final String DEFAULT_SORT_ORDER = "start_time DESC";
		
		public static final String ACTIVITY_NAME = "name";
		public static final String ACTIVITY_START_TIME = "start_time";
		public static final String ACTIVITY_END_TIME = "end_time";
		public static final String ACTIVITY_NOTE = "note"; 
	}
}
