package com.noelniles.alohafileserver;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class res {
	private static final String BUNDLE_NAME = "com.noelniles.alohafileserver.res"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private res() {
	}

	public static String str(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
