/*
 * Copyright 2009 Dan Morrill
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        
		if (!getSharedPreferences(Eula.PREFERENCES_EULA, Activity.MODE_PRIVATE)
				.getBoolean(Eula.PREFERENCE_EULA_ACCEPTED, false)) {
			Eula.showEula(this);
		}
        
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

        ((ImageButton)findViewById(R.id.weight_button)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(HealthMetrics.this, WeightTracker.class);
				startActivity(intent);
			}        	
        });
    }
}