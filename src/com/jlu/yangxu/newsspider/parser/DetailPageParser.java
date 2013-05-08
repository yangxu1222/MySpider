/**
 *@author 杨旭，创建日期:2013-5-7
 *
*/
package com.jlu.yangxu.newsspider.parser;

import java.io.IOException;

import com.jlu.yangxu.newsspider.page.FileDownLoader;


public class DetailPageParser extends PageParser {
	public DetailPageParser() {
		super();
	}
	
	/**
	 * 针对detail页做特殊的页面解析操作。
	 * 增加提取内容的功能。
	 * @throws IOException 
	 */
	public void extractPage(String url, Integer depth,String dir) {
		System.out.println("parse : " + url + "  depth : " + depth);
		
		FileDownLoader fdl = getPageContent(url,dir);
	
		// extract content
		System.out.println("extract content : " + url);
		
		super.extractLinks(fdl, depth, fdl.getEncoding());
	}
	
	/**
	 * 继承自父类<code>PageParser</code>。
	 * 如果是detail页则不需要校验深度值<code>depth</code>。
	 */
	public boolean checkDepth(String url, int depth) {
		return true;
	}
}