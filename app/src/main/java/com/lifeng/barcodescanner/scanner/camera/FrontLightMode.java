package com.lifeng.barcodescanner.scanner.camera;

import android.content.SharedPreferences;

import com.lifeng.barcodescanner.scanner.config.Config;

/**
 * Enumerates settings of the prefernce controlling the front light.
 */
public enum FrontLightMode {

	/** Always on. */
	ON,
	/** On only when ambient light is low. */
	AUTO,
	/** Always off. */
	OFF;

	private static FrontLightMode parse(String modeString) {
		return modeString == null ? OFF : valueOf(modeString);
	}

	public static FrontLightMode readPref(SharedPreferences sharedPrefs) {
		return parse(sharedPrefs.getString(Config.KEY_FRONT_LIGHT_MODE, null));
	}

}
