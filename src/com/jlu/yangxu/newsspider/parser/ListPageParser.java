/**
 *@author ���񣬴�������:2013-5-7
 *
*/
package com.jlu.yangxu.newsspider.parser;


public class ListPageParser extends PageParser {
	public ListPageParser() {
		super();
	}
	
	/**
	 * �̳��Ը���<code>PageParser</code>��
	 * �����detailҳ����ҪУ�����ֵ<code>depth</code>��
	 */
	public boolean checkDepth(String url, int depth) {
		return true;
	}
}