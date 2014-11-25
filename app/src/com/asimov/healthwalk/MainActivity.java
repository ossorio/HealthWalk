package com.example.healthwalk;






import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		SetupButtonHelp();
		SetupButtonMuyGrave();
		SetupButtonGrave();
		SetupButtonNormal();
		SetupButtonLeve();
		
	}
	
	private void SetupButtonHelp() {
		Button buttonHelp = (Button) findViewById(R.id.buttonHelp);
		buttonHelp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				
				startActivity(new Intent(MainActivity.this, HelpActivity.class));
				//finish();
				
				
			}
		});
		
	}
	
	private void SetupButtonMuyGrave() {
		Button buttonHelp = (Button) findViewById(R.id.buttonMuyGrave);
		buttonHelp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				
				
				//finish();
				
				
			}
		});
		
	}
	
	private void SetupButtonGrave() {
		Button buttonHelp = (Button) findViewById(R.id.buttonGrave);
		buttonHelp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				
				
				//finish();
				
				
			}
		});
		
	}
	
	private void SetupButtonNormal() {
		Button buttonHelp = (Button) findViewById(R.id.buttonNormal);
		buttonHelp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				
				
				//finish();
				
				
			}
		});
		
	}
	
	private void SetupButtonLeve() {
		Button buttonHelp = (Button) findViewById(R.id.buttonLeve);
		buttonHelp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				
				
				//finish();
				
				
			}
		});
		
	}
				
				
				      
	
	
}
