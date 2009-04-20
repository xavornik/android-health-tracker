package net.morrildl.health;

public class ChartEncoder {
	public static final String CHART_URI_BASE = "http://chart.apis.google.com/chart?";
	public static final String[] CHART_MAPPING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".split(".");
	
	public static final StringBuffer simpleEncode(int[] data) {
		int max = 0;
		for (int val : data) {
			if (val > max) max = val;
		}
		max = (int)Math.round(max * 1.1);

		StringBuffer sb = new StringBuffer("s:");
		for (int val : data) {
			if (val >= 0) { 
				sb.append(val);
			} else {
				sb.append("_");
			}
		}
		return sb;
	}
	
	public static String lineGraph(int width, int height, int[] data) {
		
		return "";
	}
}
