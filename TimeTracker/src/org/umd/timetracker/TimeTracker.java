package org.umd.timetracker;

import org.joda.time.Period;
import org.joda.time.DateTime;

import android.content.Context;
import android.content.SharedPreferences;
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
	
	public static final String ACTIVITIES_TABLE = "activities";
	public static final String ACTIVITY_TAGS = "tags";
	public static final String ACTIVITY_NAME = "name";
	public static final String ACTIVITY_START_TIME = "start_time";
	public static final String ACTIVITY_END_TIME = "end_time";
	public static final String ACTIVITY_NOTE = "note"; 
    }
    
    // Static constants used throughout the project
    public static final String SHARED_PREFS_FILE = "prefs";
    
    public static final String TAG_SEPARATOR = ",";
    public static final String CATEGORY_SEPARATOR = "@";
    
    /**
     * Get the shared preferences file used to store the preferences
     * used in the app.
     *
     * @param ctxt The application context.
     */
    public static SharedPreferences getSharedPreferences(Context ctxt) {
	return ctxt.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
    }
    
    /**
     * Takes in a duration, as a number of milliseconds, and produces
     * a localized string representing the date and time according to
     * the user's settings.
     * 
     * @param A shared preferences file.
     * @param duration The duration of the event being displayed.
     */
    public static String convertDurationToString(SharedPreferences prefs, Period p) {
	if (p.getDays() > 1) {
	    return p.getDays() + " days, " + p.getHours() + " hours";
	} else if (p.getHours() > 1) {
	    return p.getHours() + " hours, " + p.getMinutes() + " minutes";
	} else if (p.getMinutes() > 1) {
	    return p.getMinutes() + " minutes"; //, " + p.getSeconds() + " seconds";
	} else if (p.getMinutes() == 1) {
	    return p.getMinutes() + " minute";
	} else {
	    return p.getSeconds() + " seconds";
	}
    }
    
    /**
     * Take a {@link DateTime} to a String that can be displayed in
     * the app, according to the user's preferences.
     * 
     * @param time The time to convert to a string.
     */
    public static String convertTimeToString(SharedPreferences prefs, DateTime time) {
	return time.toString();
    }
    
    public static String convertTimeToString(Context ctxt, DateTime time) {
	return convertTimeToString(getSharedPreferences(ctxt), time);
    }
    
    /**
     * Simply a boilerplate method for using {@link
     * convertDurationToString(SharedPreferences, long)}, but grab 
     * the default shared preferences file.
     *
     * @param ctxt The application context
     * @param duration The period which the event happend.
     */
    public static String convertDurationToString(Context ctxt, Period duration) {
	return convertDurationToString(getSharedPreferences(ctxt), duration);
    }
    
    /**
     * Throw an assertion error if some condition is not satisfied.
     * Works without enabling assertions on the device.
     */
    public static void _assert(boolean condition, String message) {
	if (!condition) {
	    throw new AssertionError(message);
	}
    }
}
