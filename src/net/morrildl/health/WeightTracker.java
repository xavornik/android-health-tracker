package net.morrildl.health;

import android.app.Activity;
import android.os.Bundle;

public class WeightTracker extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_tracker);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	protected int computePoints(int kCal, int fatGrams, int fiberGrams) {
		if (kCal < 0 || fatGrams < 0 || fiberGrams < 0) {
			throw new IllegalArgumentException("Can't compute points for imaginary foods.");
		}
		return (int) Math.round(((double) kCal / 50)
				+ ((double) fatGrams / 12f)
				- (fiberGrams < 4 ? (double) fiberGrams : 4f) / 5f);
	}
}
