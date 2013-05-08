package com.jlu.yangxu.newsspider.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 提供处理URL的相关函数
 * @author yangxu
 *
 */
public class URLUtil {
	public static String enCodeZh(String url) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		if (url != null && !url.equals("")) {
			for (int i = 0; i < url.length(); i++) {
				if (url.substring(i, i + 1).matches("[\u4e00-\u9fa5]+")//根据unicode范围 判断是不是中文
						|| url.substring(i, i + 1).equals(" ")) {
					result.append(URLEncoder.encode(url.substring(i, i + 1), "UTF-8"));
				} else {
					result.append(url.substring(i, i + 1));
				}
			}
		}
		
		return result.toString();
	}
	public static void main(String[] args) throws UnsupportedEncodingException{
		String url = "http://zhangshixi.iteye.com/blog/672697#bc2302586";
		System.out.println(enCodeZh(url));
	}
}
