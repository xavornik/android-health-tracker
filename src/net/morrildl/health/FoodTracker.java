package net.morrildl.health;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class FoodTracker extends Activity {
	private Dialog computeDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_tracker);
		((Button) findViewById(R.id.compute_points_button))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						showComputePointsDialog();
					}
				});

		((Button) findViewById(R.id.cancel_button))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						finish();
					}
				});

		final EditText calories = (EditText) findViewById(R.id.calories);
		((Button) findViewById(R.id.save_button))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (saveFoodRecord(calories.getText().toString(),
								calories.getText().toString())) {
							finish();
						} else {
							displayDialog(R.string.food_error);
						}
					}
				});

		final Button computePoints = (Button) findViewById(R.id.compute_points_button);
		((RadioButton) findViewById(R.id.points_radio))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						computePoints.setEnabled(isChecked);
					}
				});
		computePoints.setEnabled(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	protected void displayDialog(int bp_error) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.bp_error_title);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.bp_dismiss, null);
		builder.setMessage(bp_error);
		builder.create().show();
	}

	protected boolean saveFoodRecord(String string, String string2) {
		return true;
	}

	protected void showComputePointsDialog() {
		if (computeDialog == null) {
			computeDialog = new Dialog(this);
			computeDialog.setContentView(R.layout.compute_dialog);
			computeDialog.setCancelable(true);
			computeDialog.setTitle(R.string.compute_points_dialog_title);
			computeDialog.setOnDismissListener(new OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					try {
						int kCal = Integer.parseInt(((EditText) computeDialog
								.findViewById(R.id.comp_pts_kcal)).getText()
								.toString());
						int fatGrams = Integer
								.parseInt(((EditText) computeDialog
										.findViewById(R.id.comp_pts_fat))
										.getText().toString());
						int fiberGrams = Integer
								.parseInt(((EditText) computeDialog
										.findViewById(R.id.comp_pts_fiber))
										.getText().toString());
						Integer points = computePoints(kCal, fatGrams,
								fiberGrams);
						Editable x = ((EditText) findViewById(R.id.calories))
								.getText();
						x.replace(0, x.length(), points.toString());
					} catch (NumberFormatException ex) {
					}
				}
			});
			((Button)computeDialog.findViewById(R.id.compute_button)).setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					computeDialog.dismiss();
				}
			});
		}
		((EditText) computeDialog.findViewById(R.id.comp_pts_kcal)).getText()
				.clear();
		((EditText) computeDialog.findViewById(R.id.comp_pts_fat)).getText()
				.clear();
		((EditText) computeDialog.findViewById(R.id.comp_pts_fiber)).getText()
				.clear();
		computeDialog.show();
	}

	protected int computePoints(int kCal, int fatGrams, int fiberGrams) {
		if (kCal < 0 || fatGrams < 0 || fiberGrams < 0) {
			throw new NumberFormatException(
					"Can't compute points for imaginary foods.");
		}
		return (int) Math.round(((double) kCal / 50)
				+ ((double) fatGrams / 12f)
				- (fiberGrams < 4 ? (double) fiberGrams : 4f) / 5f);
	}
}
