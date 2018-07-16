package com.nec.zeusas.data;

import java.util.ResourceBundle;

final class DataConfig {
	private static String TAG = "DATAVERSION";
	private static String NC = "NC";

	static ResourceBundle R = ResourceBundle.getBundle("dbms");
	static boolean NCVER = R.containsKey(TAG) ? NC.equals(R.getString(TAG)) : false;
}
