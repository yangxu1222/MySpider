/**
 *@author 杨旭，创建日期:2013-5-5
 *
 */
package com.jlu.yangxu.newsspider.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
	private static Properties properties;
	static {
		InputStream in = null;
		try {
			in = ConfigUtil.class.getResourceAsStream("/com/jlu/yangxu/newsspider/config/crawler.properties");
			properties = new Properties();
			properties.load(in);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	public static void main(String[] args) {
		Properties p = ConfigUtil.properties;
		System.out.println(p);

	}

}
