package org.umd.timetracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class BackupActivity extends Activity {
    
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.backup_activity);
    }
    
    private String mEmailAddress = "krismicinski@gmail.com";
    private String mSubject = "Backed up time activities";
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.activity_main, menu);
	return true;
    }
    
    public void onClick(View v) {
	MobileIntent i = new MobileIntent();
	Constraint sendAction = 
	    new PredicateConstraint("action",
				    new AtomConstraint("send"));
	Constraint messageType = 
	    new PredicateConstraint("mimetype",
				    new AtomConstraint("message/rfc822"));
	i.addConstraint(sendAction);
	i.addConstraint(messageType);
	i.putExtra(Intent.EXTRA_TEXT, csvExporter);
	i.putExtra(Intent.EXTRA_EMAIL, new String[]{mEmailAddress});
	i.putExtra(Intent.EXTRA_SUBJECT, mSubject);
	mIntentHelper.broadcastIntent(i);

	try {
	    startActivity(Intent.createChooser(i, "Send backup email"));
	} catch (android.content.ActivityNotFoundException ex) {
	    Toast.makeText(MyActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
	}
    }
    
    public void onReceiveMobileIntentResult(MobileIntentResult r) {
	if (!r.success()) {
	    Toast.makeText(MyActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
	}
    }
}
