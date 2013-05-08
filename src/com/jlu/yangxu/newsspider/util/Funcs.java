/**
 *@author 杨旭，创建日期:2013-5-7
 *
*/
package com.jlu.yangxu.newsspider.util;

import java.io.*;

public class Funcs {
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
	public static void main(String[] args) throws IOException {
		InputStream in = new FileInputStream(new File("f:\\seed.txt"));
		System.out.println(getContent(in));
		

	}

}
