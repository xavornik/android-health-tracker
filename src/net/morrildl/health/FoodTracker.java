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

/**
 * An Activity that collects nutritional (food intake) data.
 */
public class FoodTracker extends DataCollectorBaseActivity {
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

		final RadioButton pointsRB = (RadioButton) findViewById(R.id.points_radio);
		final EditText calories = (EditText) findViewById(R.id.calories);
		((Button) findViewById(R.id.save_button))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							boolean res = false;
							int caloriesInt = Integer.parseInt(calories
									.getText().toString());
							if (pointsRB.isSelected()) {
								res = dbUtil.addCaloriesRecord(caloriesInt);
							} else {
								res = dbUtil.addPointsRecord(caloriesInt);
							}
							if (res) {
								finish();
							} else {
								displayErrorDialog(R.string.food_error, false);
							}
						} catch (NumberFormatException ex) {
							displayErrorDialog(R.string.food_error, false);
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

	/**
	 * A convenience method that displays a calculator layout allowing the user
	 * to calculate diet points.
	 */
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
			((Button) computeDialog.findViewById(R.id.compute_button))
					.setOnClickListener(new View.OnClickListener() {
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

	/**
	 * Calculator method computing 'diet points' according to a popular formula.
	 * 
	 * @param kCal
	 *            nutritional calories of the food item
	 * @param fatGrams
	 *            grams of fat in the food item
	 * @param fiberGrams
	 *            grams of fiber in the food item
	 * @return kCal / 50 + fatGrams / 12 - fiberGrams / 4
	 */
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
