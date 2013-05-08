/**
 *@author ���񣬴�������:2013-5-7
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
	 * ���detailҳ�������ҳ�����������
	 * ������ȡ���ݵĹ��ܡ�
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
	 * �̳��Ը���<code>PageParser</code>��
	 * �����detailҳ����ҪУ�����ֵ<code>depth</code>��
	 */
	public boolean checkDepth(String url, int depth) {
		return true;
	}
}