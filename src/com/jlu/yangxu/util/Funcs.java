/**
 *@author 杨旭，创建日期:2013-5-7
 *
*/
package com.jlu.yangxu.util;

import java.io.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.jlu.yangxu.Crawler;

public class Funcs {
	public static Logger logger = getLogger();
	/**
	 * 将InputStream 转化为String
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String getContent(InputStream in) {
		String line;
		StringBuilder sb = new StringBuilder();
		BufferedReader reader ;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			while((line = reader.readLine()) != null){
				sb.append(line + "\n");
			}
			reader.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * 获取整个工程的logger
	 * @param args
	 * @throws IOException
	 */
	public static Logger getLogger(){
		Logger logger = Logger.getLogger(Crawler.class.getName());
		FileHandler fileHandler;
		try {
			fileHandler = new FileHandler("E:/crawler/Logger.log");
			fileHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(fileHandler);
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logger;
		
	}
	
	public static void main(String[] args) throws IOException {
		InputStream in = new FileInputStream(new File("f:\\seed.txt"));
		System.out.println(getContent(in));
		

	}

}
