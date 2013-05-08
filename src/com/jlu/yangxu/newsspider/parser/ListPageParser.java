/**
 *@author 杨旭，创建日期:2013-5-7
 *
*/
package com.jlu.yangxu.newsspider.parser;


public class ListPageParser extends PageParser {
	public ListPageParser() {
		super();
	}
	
	/**
	 * 继承自父类<code>PageParser</code>。
	 * 如果是detail页则不需要校验深度值<code>depth</code>。
	 */
	public boolean checkDepth(String url, int depth) {
		return true;
	}
}