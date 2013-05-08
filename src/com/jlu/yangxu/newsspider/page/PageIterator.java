/**
 *@author 杨旭，创建日期:2013-5-7
 *
 */
package com.jlu.yangxu.newsspider.page;

import java.util.regex.Pattern;

import com.jlu.yangxu.newsspider.parser.DetailPageParser;
import com.jlu.yangxu.newsspider.parser.ListPageParser;
import com.jlu.yangxu.newsspider.parser.PageParser;

public class PageIterator implements Runnable {
	private PageLinkCollector collector;

	private PageParser parser;

	private ListPageParser lparser;

	private DetailPageParser dparser;

	private String listUrlStart;

	private String detailUrlPattern;
	
	private String dir;

	public PageIterator(PageLinkCollector collector, PageParser parser,
			ListPageParser lparser, DetailPageParser dparser,
			String listUrlStart, String detailUrlPattern,String dir) {
		this.collector = collector;
		this.parser = parser;
		this.lparser = lparser;
		this.dparser = dparser;
		this.listUrlStart = listUrlStart;
		this.detailUrlPattern = detailUrlPattern;
		this.dir = dir;
	}

	@Override
	public void run() {
		String[] toDealLink = null;//[url,depth]
		while ((toDealLink = collector.getNextToDeal()) != null) {
			if (isDetailPage(toDealLink[0])) {
				dparser.extractPage(toDealLink[0],
						Integer.valueOf(toDealLink[1]),dir);
			} else if (isListPage(toDealLink[0])) {
				lparser.extractPage(toDealLink[0],
						Integer.valueOf(toDealLink[1]),dir);
			} else {
				parser.extractPage(toDealLink[0],
						Integer.valueOf(toDealLink[1]),dir);
			}

		}

	}

	public boolean isListPage(String url) {
		//if (url.startsWith(listUrlStart)) {
	//		return true;
		//}
		if (Pattern.matches(listUrlStart, url)){
			return true;
		}
		return false;
	}

	public boolean isDetailPage(String url) {
		if (Pattern.matches(detailUrlPattern, url)) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {

	}

}
