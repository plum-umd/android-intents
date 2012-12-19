package org.umd.timetracker;

import org.acra.*;
import org.acra.annotation.*;

import android.app.Application;

/**
 * We have an application class so that we can enable ACRA tracking.
 * formKey = "dDFtN3N0RGlpd0t3XzE4eUYtYTN0Y0E6MQ"
 */
@ReportsCrashes(formKey = "", // unused
		mailTo = "krismicinski+crashreports@gmail.com")
public class TimeTrackerApplication extends Application {
    @Override
    public void onCreate() {
	ACRA.init(this);
	super.onCreate();
    }
}
