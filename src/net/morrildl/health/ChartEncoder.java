package net.morrildl.health;

/**
 * A helper class for producing Google chart server URIs from data.
 * See http://code.google.com/apis/chart/
 */
public class ChartEncoder {
	public static final String CHART_URI_BASE = "http://chart.apis.google.com/chart?";
	public static final String[] CHART_MAPPING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".split(".");

	/**
	 * Encodes an array of integers into a chartserver simple-format string, building in 10% headroom.
	 * Returns a StringBuffer, since the caller is highly likely to be building up a URI out of StringBuffers
	 * anyway.
	 * @param data the array to be encoded
	 * @return a StringBuffer containing the chartserver simple format string, including "s:" prefix but not the "&chd="
	 */
	public static final StringBuffer simpleEncode(int[] data) {
		// find the max value
		int max = 0;
		for (int val : data) {
			if (val > max) max = val;
		}
		max = (int)Math.round(max * 1.1); // build in 10% headroom at the top of the chart

		// build up the actual data
		StringBuffer sb = new StringBuffer("s:");
		for (int val : data) {
			if (val >= 0) { 
				sb.append(val);
			} else {
				sb.append("_"); // _ means data at this slot is missing
			}
		}
		return sb;
	}

	/**
	 * Returns a URI to Google chartserver depicting the indicated data at the indicated size.
	 * @param width the width in pixels of the image to generate
	 * @param height the height in pixels of the image to generate
	 * @param data the data to be visualized
	 * @return a String containing the URI to the Google chartserver image
	 */
	public static String lineGraph(int width, int height, int[] data) {
		
		return "";
	}
}
