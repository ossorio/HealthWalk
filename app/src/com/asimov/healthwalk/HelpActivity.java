package com.asimov.healthwalk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class HelpActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		SetupButtonGoHome();
    }
	
	private void SetupButtonGoHome() {
		Button buttonGoHome = (Button) findViewById(R.id.buttonBack1);
		buttonGoHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}