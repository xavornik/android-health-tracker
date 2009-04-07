package net.morrildl.health;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class WeightTracker extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weight_tracker);
		
		final EditText weight = (EditText)findViewById(R.id.weight);
		((Button)findViewById(R.id.save_button)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (saveWeightRecord(weight.getText().toString())) {
					finish();
				} else {
					displayDialog(R.string.bp_error);
				}
			}		
		});

		((Button)findViewById(R.id.cancel_button)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}		
		});

	}

	protected boolean saveWeightRecord(String string) {
		return true;
	}

	protected void displayDialog(int bp_error) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.bp_error_title);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.bp_dismiss, null);
		builder.setMessage(bp_error);
		builder.create().show();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
