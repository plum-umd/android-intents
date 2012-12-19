package org.umd.timetracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class BackupActivity extends Activity {
    
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.backup_activity);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.activity_main, menu);
	return true;
    }
    
    public void onClick(View v) {
	
    }
}
