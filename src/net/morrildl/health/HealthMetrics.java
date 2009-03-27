package net.morrildl.health;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class HealthMetrics extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ((ImageButton)findViewById(R.id.heart_button)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(HealthMetrics.this, BloodPressureTracker.class);
				startActivity(intent);
			}        	
        });

        ((ImageButton)findViewById(R.id.food_button)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(HealthMetrics.this, FoodTracker.class);
				startActivity(intent);
			}        	
        });
    }
}