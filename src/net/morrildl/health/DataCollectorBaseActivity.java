package net.morrildl.health;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;

/**
 * A base class for the various data collection Activities that centralizes some
 * convenience functions.
 */
public abstract class DataCollectorBaseActivity extends Activity {
	/** A handle to the database utility class singleton. */
	protected static DBUtil dbUtil = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbUtil = DBUtil.getInstance(this);
	}

	/**
	 * Convenience method that automates displaying a Dialog with the indicated
	 * message.
	 * 
	 * @param errorResourceId
	 *            the resource ID to display
	 * @param finishOnDismiss
	 *            if 'true', kills the host activity via a call to finish()
	 * @param b
	 */
	protected void displayErrorDialog(int errorResourceId,
			boolean finishOnDismiss) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.bp_error_title);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.bp_dismiss, null);
		builder.setMessage(errorResourceId);
		Dialog dialog = builder.create();
		if (finishOnDismiss) {
			dialog.setOnDismissListener(new OnDismissListener() {
				public void onDismiss(DialogInterface arg0) {
					finish();
				}
			});
		}
		dialog.show();
	}
}